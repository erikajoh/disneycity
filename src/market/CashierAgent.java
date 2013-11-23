package market;

import agent.Agent;

import java.util.*;
import java.util.concurrent.Semaphore;

public class CashierAgent extends Agent {
	private String name;
	private CashRegister r;
	public List<Bill> marketBills =  Collections.synchronizedList(new ArrayList<Bill>());
	
	//public EventLog log = new EventLog();

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

	// Messages
	public void msgHereIsMoney(CustomerAgent c, double amount){
		//log.add(new LoggedEvent("Received msgHereIsMoney"));
	}
	

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		synchronized(marketBills){
			for (Bill b: marketBills) {
				marketBills.remove(b);
				return true;
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
		double amt;
		Bill(double a){
			amt = a;
		}
	}
	
	public String getName() {
		return name;
	}

}
