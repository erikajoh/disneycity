package market;

import agent.Agent;

import java.util.*;
import java.util.concurrent.Semaphore;

import simcity.PersonAgent;
import market.gui.CustomerGui;
import restaurant_rancho.CookAgent;
/**
 * Restaurant customer agent.
 */
public class CustomerAgent extends Agent {
	private String name, choice;
	Timer timer = new Timer();
	private CustomerGui customerGui;
	private Wallet wallet;
	double amtDue;
	int quantity, numInLine;

	private ManagerAgent manager;
	private CashierAgent cashier;
	private WorkerAgent worker;
	private PersonAgent person;
	private Market market;
	private CookAgent cook;
	
	private Semaphore moving = new Semaphore(0, true);
	
	public int table;

	public enum State {idle, entering, moveUp, ordering, paying, leaving};
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
		wallet = new Wallet(amt);
		state = State.entering;
	}

	/**
	 * hack to establish connection to Host agent.
	 */
	
	public int getNum() {
		return numInLine;
	}
	
	public void setPerson(PersonAgent person) {
		this.person = person;
	}
	
	public void setCook(CookAgent cook) {
		this.cook = cook;
	}
	
	public CookAgent getCook() {
		return cook;
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
	
//	public void msgWhatWouldYouLike() { // from manager
//		state = State.ordering;
//		stateChanged();
//	}
	
	public void msgLineMoved() {
		numInLine--;
		state = State.moveUp;
		stateChanged();
	}
	
	public void msgHereIsItemAndBill(int num, double amt) {
		quantity = num;
		amtDue = amt;
		if (wallet.getAmt() < amtDue) state = State.leaving;
		else state = State.paying;
		stateChanged();
	}
	
	public void msgOutOfItem() {
		quantity = 0;
		state = State.leaving;
		stateChanged();
	}
	
	public void msgHereIsChange(double amt) { // from cashier
		wallet.update(amt);
		state = State.leaving;
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
		else if (state == State.paying){
			print("Paying");
			cashier.msgHereIsMoney(this, wallet.getAmt());
			state = State.idle;
//			state = State.leaving; //hack
			return true;
		}
		else if (state == State.leaving){
			print("Leaving");
			LeaveMarket();
//			person.msgDoneAtMarket(quantity);
			market.msgLeaving(this);
			return true;
		}
		return false;
	}

	private void EnterMarket() {
		customerGui.setPresent(true);
		customerGui.DoEnterMarket();
		try {
			moving.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		state = State.idle;
	}
	
	private void PlaceOrder() {
		manager.msgWantToOrder(this, choice, 1);
		state = State.idle;
	}
	
	private void MoveUp() {
		customerGui.DoMoveUpInLine();
		state = State.idle;
	}

	private void LeaveMarket() {
		customerGui.DoLeaveMarket();
		try {
			moving.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

