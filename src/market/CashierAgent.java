package market;

import agent.Agent;
import housing.test.mock.LoggedEvent;
import housing.test.mock.EventLog;

import java.util.*;
import java.util.concurrent.Semaphore;

import market.gui.CashierGui;
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
	private Semaphore moving = new Semaphore(0, true);
	
	private PersonAgent person;
	private Customer customer;
	private Restaurant rest;
	private Market market;
	private CashierGui cashierGui;
	double wage;
	boolean shiftDone = false;
	
	enum State {idle, rcvdPayment, left};
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

	// Messages
	
	public void msgAnimationFinished() { // from gui
		moving.release();
		state = State.left;
	}
	
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
	
	public void msgShiftDone(double wage) {
		shiftDone = true;
		this.wage = wage;
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		if (state == State.rcvdPayment){
			for (int i=0; i<marketBills.size(); i++){
				Bill b = marketBills.get(i);
				if (b.cust == customer){
					ProcessPayment(b, amt);
					UpdateInventory(b);
					return true;
				}
			}
		}
//		print("no customers left: "+market.noCustomers());
//		print("shiftdone? "+shiftDone);
		if (shiftDone) {
			if (market.noCustomers()) {
				ShiftDone();
				shiftDone = false;
			}
			return true;
		}
		if (state == State.left) {
			StopWork();
			return true;
		}
		return false;
	}
	
	public void StopWork() {
		state = State.idle;
		person.msgStopWork(wage);
		market.removeMe(this);
	}
	
	public void ShiftDone() {
		state = State.idle;
		cashierGui.DoLeave();
		try {
			moving.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
	public void setGui(CashierGui gui) {
		cashierGui = gui;
		cashierGui.setPresent(true);
	}
	
	public double getMoney() {
		return r.getMoney();
	}
	
	public String getName() {
		return name;
	}

}
