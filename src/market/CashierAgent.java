package market;

import agent.Agent;
import housing.test.mock.LoggedEvent;
import housing.test.mock.EventLog;

import java.util.*;
import java.util.concurrent.Semaphore;

import market.interfaces.Customer;
import simcity.PersonAgent;
import simcity.Restaurant;
import simcity.gui.trace.AlertLog;
import simcity.gui.trace.AlertTag;

public class CashierAgent extends Agent {
	private String name;
	private CashRegister r;
	private double amt;
	public List<Bill> marketBills = Collections.synchronizedList(new ArrayList<Bill>());
	private boolean shiftDone = false;
	
	private PersonAgent person;
	private Customer customer;
	private Restaurant rest;
	private Market market;
	
	enum State {idle, rcvdPayment};
	State state = State.idle;
	
	public EventLog log = new EventLog();

	/**
	 * Constructor for CashierAgent class
	 *
	 * @param name name of the cashier
	 */
	public CashierAgent(String name, double amt){
		super();
		this.name = name;
		r = new CashRegister(amt);
	}
	
	public void setPerson(PersonAgent person) {
		this.person = person;
	}
	
	public void setMarket(Market market) {
		this.market = market;
	}
	
	public void setRestaurant(Restaurant rest) {
		this.rest = rest;
	}
	
	public void subtract(double amount) {
		r.balance -= amount;
	}

	// Messages
	
	public void msgHereIsBill(Customer c, double amount){ // from worker
		log.add(new LoggedEvent("Received msgHereIsBill"));
		print("rcvd bill");
		marketBills.add(new Bill(c, amount));
		stateChanged();
	}
	
	public void msgHereIsMoney(Customer c, double amount){ // from customer
		log.add(new LoggedEvent("Received msgHereIsMoney"));
		AlertLog.getInstance().logMessage(AlertTag.MARKET, name, "Received payment from customer");
		customer = c;
		amt = amount;
		state = State.rcvdPayment;
		stateChanged();
	}
	
	public void msgShiftDone() {
//		print("got msg shift done");
//		shiftDone = true;
//		if (marketBills.size()==0) {
//			person.msgStopWork(10);
//		}
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		if (state == State.rcvdPayment){
			for (Bill b: marketBills){
				if (b.cust == customer){
					ProcessPayment(b, amt);
					UpdateInventory(b);
					return true;
				}
			}
		}
		return false;
	}
	
	public void UpdateInventory(Bill b) {
		marketBills.remove(b);
		state = State.idle;
	}
	
	public class CashRegister {
		double balance;
		CashRegister(double amt){
			balance = amt;
		}
		public void increase(double amt){
			balance += amt;
		}
		public void update(double amt){
			balance = amt;
		}
		public double getMoney(){
			return balance;
		}
	}
	
	public class Bill {
		Customer cust;
		double amt;
		Bill(Customer c, double a){
			cust = c;
			amt = a;
		}
	}
	
	public void ProcessPayment(Bill b, double amtRcvd){
		print(""+amtRcvd);
		if (amtRcvd >= b.amt){
			r.increase(b.amt);
			b.cust.msgHereIsChange(amtRcvd - b.amt);
		}
	}
	
	public String getName() {
		return name;
	}

}
