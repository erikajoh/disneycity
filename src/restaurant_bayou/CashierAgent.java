package restaurant_bayou;

import agent_bayou.Agent;

import java.util.*;
import java.util.concurrent.Semaphore;

import restaurant_bayou.CustomerAgent.AgentEvent;
import restaurant_bayou.HostAgent.Menu;
import restaurant_bayou.interfaces.Customer;
import restaurant_bayou.interfaces.Market;
import restaurant_bayou.interfaces.Waiter;
import restaurant_bayou.test.mock.EventLog;
import restaurant_bayou.test.mock.LoggedEvent;

public class CashierAgent extends Agent {
	private String name;
	public List<Check> checks =  Collections.synchronizedList(new ArrayList<Check>());
	private CashRegister r;
	private Menu myMenu;
	public List<Bill> marketBills =  Collections.synchronizedList(new ArrayList<Bill>());
	public List<Market> cutoffMarkets =  Collections.synchronizedList(new ArrayList<Market>());
	private CookAgent cook;
	
	public EventLog log = new EventLog();

	/**
	 * Constructor for CookAgent class
	 *
	 * @param name name of the cook
	 */
	public CashierAgent(String name, Menu menu, double amt){
		super();
		this.name = name;
		r = new CashRegister(amt);
		myMenu = menu;
	}

	public void msgGiveMeCheck(Waiter w, Customer c, String choice, int table){
		log.add(new LoggedEvent("Received msgGiveMeCheck"));
		double cost = myMenu.getCost(choice);
		checks.add(new Check(w, c, cost, table));
		stateChanged();
	}
	
	public void msgHereIsMoney(Customer c, double amount){
		log.add(new LoggedEvent("Received msgHereIsMoney"));
		for (Check ch: checks){
			if (ch.cust == c){
				ch.acceptPayment(amount);
			}
		}
	}
	
	public void msgHereIsBill(Market ma, double cost){
		marketBills.add(new Bill(ma, cost));
		stateChanged();
	}
	
	public void msgHereIsChange(double change){
		log.add(new LoggedEvent("Received msgHereIsChange"));
		r.update(change);
		stateChanged();
	}
	
	public void msgYouAreCutoff(Market m){
		log.add(new LoggedEvent("Received msgYouAreCutoff"));
		cutoffMarkets.add(m);
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		synchronized(marketBills){
			for (Bill b: marketBills) {
				b.m.msgHereIsPayment(this, r.getMoney());
				marketBills.remove(b);
				return true;
			}
		}
		synchronized(cutoffMarkets){
			for (Market m: cutoffMarkets){
				cook.msgMarketHasCutUsOff(m);
				cutoffMarkets.remove(m);
				return true;
			}
		}
		synchronized(checks){
			for (Check ch: checks) {
				if (ch.state == CheckState.Paid) {
					Do("Giving change to customer");
					ch.cust.msgHereIsChange(ch.change);
					checks.remove(ch);
					return true;
				}
			}
		}
		synchronized(checks){
			for (Check ch: checks) {
				if (ch.state == CheckState.Done) {
					Do("Sending check to waiter");
					ch.w.msgHereIsCheck(ch);
					ch.state = CheckState.Sent;
					return true;
				}
			}
		}
		synchronized(checks){
			for (Check ch: checks) {
				if (ch.state == CheckState.Waiting) {
					Do("Preparing check");
					ch.state = CheckState.Preparing;
					ch.prepCheck();
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
	
	public enum CheckState {Waiting, Preparing, Done, Sent, Paid;}
	public class Check {
		Waiter w;
		double cost;
		double change;
		int table;
		Customer cust;
		CheckState state;
//		Timer ti = new Timer();
		public Check(Waiter wa, Customer cu, double co, int tbl) {
			w = wa;
			cost = co;
			change = 0;
			cust = cu;
			table = tbl;
			state = CheckState.Waiting;
		}
		public double getCost() {
			return cost;
		}
		public Customer getCust() {
			return cust;
		}
		public double getChange() {
			return change;
		}
		public void prepCheck() {
			state = CheckState.Done;
			stateChanged();
//			t.schedule(new TimerTask() {
//				public void run() {
//					state = CheckState.Done;
//					stateChanged();
//				}
//			},
//			1000);
		}
		public void acceptPayment(double amt){
			if (amt < cost) {
				Do("Customer can't pay!");
				cust.msgDoDishesAsPunishment(1000);
			} else {
				change = amt - cost;
				r.increase(cost);
				state = CheckState.Paid;
			}
//			ti.schedule(new TimerTask() {
//			public void run() {
				stateChanged();
//			}
//		},
//		100);
		}
	}
	
	public class Bill {
		Market m;
		double amt;
		Bill(Market ma, double a){
			m = ma;
			amt = a;
		}
	}
	
	public String getName() {
		return name;
	}
	
	public void addMarket(MarketAgent m){
		m.startThread();
	}
	
	public void setCook(CookAgent c){
		cook = c;
	}

}
