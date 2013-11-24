package restaurant_rancho;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import agent_rancho.Agent;
import restaurant_rancho.Check;
import restaurant_rancho.interfaces.Cashier;
import restaurant_rancho.interfaces.Customer;
import restaurant_rancho.interfaces.Market;
import restaurant_rancho.interfaces.Waiter;
import simcity.PersonAgent;
import restaurant_rancho.test.mock.EventLog;
import restaurant_rancho.test.mock.LoggedEvent;
import simcity.RestMenu;


public class CashierAgent extends Agent implements Cashier{

	private String name;
	public EventLog log = new EventLog();
	public List<Check> checks;
	public List<MarketBill> bills;
	public double money;
	//public RestMenu menu= new RestMenu();
	public enum checkState {nothing, pending, readyForCust, waitingForCust, paid, complete, notComplete, completing};
	public CashierAgent(String name) {
		super();
		this.name = name;
		checks = Collections.synchronizedList(new ArrayList<Check>());
		bills = Collections.synchronizedList(new ArrayList<MarketBill>());
		money = 100;
	}
	PersonAgent person;
	Timer checkTimer = new Timer();

	public String getMaitreDName() {
		return name;
	}

	public String getName() {
		return name;
	}
	public void setPerson(PersonAgent p) {
		person = p;
	}


	// Messages
	
	
	public void msgComputeCheck(Waiter w, Customer c, String choice, RestMenu menu) {
		log.add(new LoggedEvent("Received Compute Check"));
		print( "Choice is " + choice);
		checks.add(new Check(c, w, choice, menu.menuItems.get(choice)));
		stateChanged();
	}
	
	public void msgHereIsMoney(Customer c, double amount) {
		log.add(new LoggedEvent("Received Cash. " + amount));
		Check check = findCheck(c);
		if (amount >= check.amount) {
			check.change = amount - check.amount;
			check.amount = 0;
			check.cs = checkState.paid;
			money += amount - check.change;
			stateChanged();
		}
		else {
			check.change = 0;
			check.amount = check.amount - amount;
			money += amount;
			check.cs = checkState.paid;
			stateChanged();
		}
		
	}
	
	public void msgHereIsMarketBill(Market m, double amount, int orderNum){
		log.add(new LoggedEvent("Received Market Bill."));
		bills.add(new MarketBill(m, amount, orderNum));
		stateChanged();
	}
	


	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {

	
		//rules 
		try{
		if (!bills.isEmpty()) {
			synchronized(bills) {
				for (MarketBill bill : bills) {
					payBill(bill);
					return true;
				}
			}
		}
		if (!checks.isEmpty()) {
			synchronized(checks) {
				for (Check ch : checks) {
					if (ch.cs == checkState.nothing) {
						makeCheck(ch);
						return true;
					}
				}
			}
			synchronized(checks) {
				for (Check ch : checks) {
					if (ch.cs == checkState.readyForCust) {
						ch.cs = checkState.waitingForCust;
						notifyWaiter(ch);
						return true;
					}
				}
			}
			synchronized(checks) {
				for (Check ch : checks) {
					if (ch.cs == checkState.paid) {
						giveChange(ch);
						return true;
					}
				}
			}
			return true;
		}
			
		}
		catch(ConcurrentModificationException e) {
		}
		

		return false;
	
	}

	// Actions
	private void makeCheck(final Check check) {
		check.cs = checkState.pending;
		checkTimer.schedule(new TimerTask() {
			public void run() {
				print("Check complete");
				check.cs= checkState.readyForCust;
				stateChanged();
			}
		},
		500);
	}
	
	private void payBill(MarketBill bill) {
		bill.market.msgHereIsPayment(bill.amount, bill.orderNum);
		money -= bill.amount;
		print ("Paid market, I have " + money + " dollars now");
		bills.remove(bill);
		stateChanged();
	}
	
	private void notifyWaiter(Check check) { 
		check.waiter.msgCheckReady(check.cust, check.amount);
		stateChanged();
	}
	
	private void giveChange(Check check) {
		if (check.amount != 0) {
			print("Check is incomplete, must pay " + check.amount + " next time");
			check.cs = checkState.notComplete;
			check.cust.msgCheckIncomplete(check.amount);
			stateChanged();
		}
		else {
			print("Giving customer " + check.change + " back");
			check.cust.msgHereIsChange(check.change);
			if (check.amount == 0) checks.remove(check);
			stateChanged();
		}
	}

	public static class MarketBill {
		Market market;
		double amount; 
		int orderNum;
		
		public MarketBill(Market m, double am, int num) {
			market = m;
			amount = am;
			orderNum = num;
		}
	}
	
	/*public class Check {
		Customer cust;
		Waiter waiter;
		String food;
		double amount;
		checkState cs;
		double change;
		
		Check (Customer c, Waiter w, String f, double am) {
			cust = c;
			waiter = w;
			food = f;
			amount = am;
			change = 0.0;
			cs = checkState.nothing;
		}

	}
	*/
	
	//utilities
	private Check findCheck(Customer cust) {
		synchronized(checks) {
			for (Check ch : checks) {
				if (ch.cust == cust) {
					return ch;
				}
			}
		}
		return null;
	}



}
