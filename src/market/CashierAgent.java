package market;

import agent.Agent;

import java.util.*;
import java.util.concurrent.Semaphore;

import simcity.PersonAgent;

public class CashierAgent extends Agent {
	private String name;
	private CashRegister r;
	private double amt;
	public List<Bill> marketBills = Collections.synchronizedList(new ArrayList<Bill>());
	
	private PersonAgent person;
	private CustomerAgent customer;
	private Market market;
	
	enum State {idle, rcvdPayment};
	State state = State.idle;
	
//	public EventLog log = new EventLog();

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

	// Messages
	
	public void msgHereIsBill(CustomerAgent c, double amount){ // from worker
		marketBills.add(new Bill(c, amount));
	}
	
	public void msgHereIsMoney(CustomerAgent c, double amount){ // from customer
//		log.add(new LoggedEvent("Received msgHereIsMoney"));
		customer = c;
		amt = amount;
		state = State.rcvdPayment;
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		if (state == State.rcvdPayment){
			for (Bill b: marketBills){
				if (b.cust == customer){
					ProcessPayment(b, amt);
					state = State.idle;
					return true;
				}
			}
		}
		return false;
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
		CustomerAgent cust;
		double amt;
		Bill(CustomerAgent c, double a){
			cust = c;
			amt = a;
		}
	}
	
	public void ProcessPayment(Bill b, double amtRcvd){
		if (amtRcvd >= b.amt){
			r.increase(b.amt);
			b.cust.msgHereIsChange(amtRcvd - b.amt);
		}
	}
	
	public String getName() {
		return name;
	}

}
