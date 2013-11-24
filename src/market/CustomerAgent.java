package market;

import agent.Agent;

import java.util.*;
import java.util.concurrent.Semaphore;

import simcity.PersonAgent;
import market.gui.CustomerGui;

/**
 * Restaurant customer agent.
 */
public class CustomerAgent extends Agent {
	private String name, choice;
	Timer timer = new Timer();
	private CustomerGui customerGui;
	private Wallet wallet;
	private Timer t = new Timer();

	private ManagerAgent manager;
	private CashierAgent cashier;
	private WorkerAgent worker;
	private PersonAgent person;
	
	private Semaphore moving = new Semaphore(1, true);
	
	public int table;

	public enum State {idle, entering, ordering, paying, leaving};
	private State state = State.idle;
	
	/**
	 * Constructor for CustomerAgent class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the customergui so the customer can send it messages
	 */
	public CustomerAgent(String name, double amt){
		super();
		this.name = name;
		wallet = new Wallet(amt);
		state = State.entering;
	}

	/**
	 * hack to establish connection to Host agent.
	 */
	
	public void setPerson(PersonAgent person) {
		this.person = person;
	}
	
	public void setManager(ManagerAgent manager) {
		this.manager = manager;
	}
	
	public void setCashier(CashierAgent cashier) {
		this.cashier = cashier;
	}
	
	public void msgWhatWouldYouLike() { // from manager
		state = State.ordering;
		stateChanged();
	}
	
	public void msgHereIsYourItem() { // from worker
		state = State.paying;
		stateChanged();
	}
	
	public void msgHereIsChange(double amt) {
		wallet.update(amt);
		state = State.leaving;
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
			EnterMarket();
			state = State.idle;
//			state = State.ordering; //hack
			return true;
		}
		else if (state == State.ordering){
			manager.msgWantToOrder("water", 1);
			state = State.idle;
//			state = State.paying; //hack
			return true;
		}
		else if (state == State.paying){
			state = State.idle;
//			state = State.leaving; //hack
			return true;
		}
		else if (state == State.leaving){
			LeaveMarket();
			state = State.idle;
			return true;
		}
		return false;
	}

	private void EnterMarket() {
		Do("Entering market");
		customerGui.setPresent(true);
		try {
			moving.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		customerGui.DoEnterMarket();
		manager.msgIAmHere(this);
	}
	
	private void PlaceOrder() {
		worker.msgHereIsMyChoice(this, choice);
	}

	private void LeaveMarket() {
		Do("Leaving market");
		try {
			moving.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		customerGui.DoLeaveMarket();
	}
	
	public String getName() {
		return name;
	}
	
	public String getChoice() {
		return choice;
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

