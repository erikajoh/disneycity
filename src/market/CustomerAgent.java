package market;

import agent.Agent;

import java.util.*;
import java.util.concurrent.Semaphore;

import simcity.PersonAgent;
import simcity.Restaurant;
import market.gui.CustomerGui;
import market.interfaces.Customer;
import restaurant_rancho.CookAgent;
/**
 * Restaurant customer agent.
 */
public class CustomerAgent extends Agent implements Customer {
	private String name, choice, location;
	Timer timer = new Timer();
	private CustomerGui customerGui;
	private Wallet wallet;
	double amtDue;
	int quantity, numInLine, orderID;
	boolean virtual;

	private ManagerAgent manager;
	private CashierAgent cashier;
	private PersonAgent person;
	private Market market;
	private Restaurant rest;
	
	private Semaphore moving = new Semaphore(0, true);
	
	public int table;

	public enum State {idle, entering, moveUp, ordering, gotItemAndBill, paying, leaving};
	private State state = State.idle;
	
	/**
	 * Constructor for CustomerAgent class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the customergui so the customer can send it messages
	 */
	public CustomerAgent(String name, double amt, String choice, int quantity, int num){
		super();
		this.name = name;
		this.choice = choice;
		this.quantity = quantity;
		this.numInLine = num;
		this.virtual = false;
		wallet = new Wallet(amt);
		state = State.entering;
	}
	
	public CustomerAgent(String name, double amt, String choice, int quantity, int num, String location){
		super();
		this.name = name;
		this.choice = choice;
		this.quantity = quantity;
		this.numInLine = num;
		this.virtual = true;
		this.location = location;
		wallet = new Wallet(amt);
		state = State.entering;
	}
	
	public CustomerAgent(String name, String choice, int quantity, int num, int orderID){
		super();
		this.name = name;
		this.choice = choice;
		this.quantity = quantity;
		this.numInLine = num;
		this.orderID = orderID;
		this.virtual = true;
		wallet = new Wallet(0);
		state = State.entering;
	}

	/**
	 * hack to establish connection to Host agent.
	 */
	
	public int getNum() {
		return numInLine;
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setPerson(PersonAgent person) {
		this.person = person;
	}
	
	public void setRest(Restaurant rest) {
		this.rest = rest;
	}
	
	public Restaurant getRest() {
		return rest;
	}
	
	public void setManager(ManagerAgent manager) {
		this.manager = manager;
	}
	
	public void setCashier(CashierAgent cashier) {
		this.cashier = cashier;
	}
	
	public void setMarket(Market market) {
		this.market = market;
	}
	
	public void msgLineMoved() {
		numInLine--;
		state = State.moveUp;
		stateChanged();
	}
	
	public void msgHereIsItemAndBill(int num, double amt) { // from worker
		state = State.gotItemAndBill;
		quantity = num;
		amtDue = amt;
		stateChanged();
	}
	
	public void msgOutOfItem() { // from worker
		quantity = 0;
		state = State.leaving;
		stateChanged();
	}
	
	public void msgHereIsChange(double amt) { // from cashier
		wallet.update(amt);
		state = State.leaving;
		stateChanged();
	}
	
	public void msgHereIsMoney(double amt) { // from market (from restaurant)
		wallet.update(amt);
		state = State.paying;
		stateChanged();
	}
	
	public void msgAnimationMoveUpFinished() {
		//from animation
		print("my new num is "+numInLine);
		if (numInLine == 0) state = State.ordering;
		moving.release();
		stateChanged();
	}
	
	public void msgAnimationFinished() {
		//from animation
		moving.release();
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		if (state == State.entering){
			print("Entering market");
			EnterMarket();
			if (numInLine == 0) state = State.ordering;
			return true;
		}
		else if (state == State.moveUp){
			state = State.idle;
			MoveUp();
			return true;
		}
		else if (state == State.ordering){
			print("Ordering items");
			PlaceOrder();
			return true;
		}
		else if (state == State.gotItemAndBill){
			if (rest != null) {
				SendBill();
			} else {
				LeaveOrPay();
			}
		}
		else if (state == State.paying){
			print("Paying");
			PayMoney();
			return true;
		}
		else if (state == State.leaving){
			print("Leaving");
			LeaveMarket();
			SayGoodbye();
			return true;
		}
		return false;
	}
	
	private void PayMoney() {
		cashier.msgHereIsMoney(this, wallet.getAmt());
		state = State.idle;
	}
	
	private void LeaveOrPay() {
		if (wallet.getAmt() < amtDue) state = State.leaving;
		else state = State.paying;
	}
	
	private void SendBill() {
		rest.msgHereIsBill(market, amtDue);
		state = State.idle;
	}
	
	private void SayGoodbye() {
		market.msgLeaving(this);
	}

	private void EnterMarket() {
		if (person != null) {
			customerGui.setPresent(true);
			customerGui.DoEnterMarket();
			try {
				moving.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		state = State.idle;
	}
	
	private void PlaceOrder() {
		manager.msgWantToOrder(this, choice, quantity, virtual);
		state = State.idle;
	}
	
	private void MoveUp() {
		if (person != null) customerGui.DoMoveUpInLine();
		state = State.idle;
	}

	private void LeaveMarket() {
		if(person!=null){
			customerGui.DoLeaveMarket();
			try {
				moving.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		state = State.idle;
	}
	
	public String getName() {
		return name;
	}
	
	public String getChoice() {
		return choice;
	}
	
	public PersonAgent getPerson() {
		return person;
	}

	public String toString() {
		return "customer " + getName();
	}

	public void setGui(CustomerGui g) {
		customerGui = g;
	}

	public CustomerGui getGui() {
		return customerGui;
	}
	
	private class Wallet{
		double balance;
		Wallet(double amt){
			balance = amt;
		}
//		public void increase(double amt){
//			balance += amt;
//		}
//		public void decrease(double amt){
//			balance -= amt;
//		}
		public void update(double amt){
			balance = amt;
		}
		public double getAmt() {
			return balance;
		}
	}
}

