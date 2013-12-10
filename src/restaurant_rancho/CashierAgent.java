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
import restaurant_rancho.interfaces.Waiter;
import simcity.PersonAgent;
import simcity.interfaces.Person;
import simcity.Restaurant;
import restaurant_rancho.test.mock.EventLog;
import restaurant_rancho.test.mock.LoggedEvent;
import simcity.RestMenu;
import bank.gui.Bank;
import market.Market;
import simcity.interfaces.Market_Douglass;
import simcity.interfaces.Bank_Douglass;


public class CashierAgent extends Agent implements Cashier{

	private String name;
	public EventLog log = new EventLog();
	public List<Check> checks;
	public List<MarketBill> bills;
	public double money;
	private int accountNum;
	private boolean endOfDay;
	public enum bankState {nothing, waitingForBank};
	bankState bs;
	boolean shiftDone = false;
	double wage;
	Restaurant restaurant;
	//public RestMenu menu= new RestMenu();
	public enum checkState {nothing, pending, readyForCust, waitingForCust, paid, complete, notComplete, completing};
	public CashierAgent(String name, Restaurant restaurant) {
		super();
		this.name = name;
		this.restaurant = restaurant;
		checks = Collections.synchronizedList(new ArrayList<Check>());
		bills = Collections.synchronizedList(new ArrayList<MarketBill>());
		money = 500;
		accountNum = -1;
		endOfDay = false;
		bs = bankState.nothing;
	}
	Person person;
	Timer checkTimer = new Timer();
	private Bank_Douglass bank;
	private Market_Douglass market;
	
	public boolean isWorking = true;
	
	public void setMarket(Market_Douglass m) {
		market = m;
	}
	public void setBank(Bank_Douglass b){
		bank = b;
	}
	
	public void setAcctNum(int act) {
		accountNum = act;
	}
	public void setEndOfDay(Boolean b) {
		endOfDay = b;
	}

	public String getMaitreDName() {
		return name;
	}

	public String getName() {
		return name;
	}
	public void setPerson(Person p) {
		person = p;
	}

	// Messages

	public void msgShiftDone(double w) {
		print("got msg shift done");
		shiftDone = true;
		wage = w;
		stateChanged();
	
	}
	
	public void msgComputeCheck(Waiter w, Customer c, String choice, RestMenu menu) {
		log.add(new LoggedEvent("Received Compute Check"));
		print( "Choice is " + choice);
		checks.add(new Check(c, w, choice, menu.menuItems.get(choice)));
		stateChanged();
	}
	
	
	public void msgHereIsMarketBill(Market_Douglass m, double amount){
		log.add(new LoggedEvent("Received Market Bill."));
		bills.add(new MarketBill(m, amount));
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
	
	public void msgLeftBank(int aNum, double change, double loanAmount, int loan) {
		if(accountNum==-1) accountNum = aNum;
		money+=change;
		print("got account from bank");
		bs = bankState.nothing;
		stateChanged();
	}
	


	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {

	
		//rules 
		try{
		/*
		if (money>400) {
			if (accountNum==-1 && bs != bankState.waitingForBank) {
				print("requesting account from bank");
				bank.msgRequestAccount(person, money-100, false);
				bs = bankState.waitingForBank;
				return true;
			}
			else if (accountNum!=-1 && bs != bankState.waitingForBank){
				print(" requesting deposit");
				bank.msgRequestDeposit(person, accountNum, money-100, false);
				bs = bankState.waitingForBank;
				return true;
			}
		}
		if (money<20) {
			if (accountNum==-1) {
				bank.msgRequestAccount(person,  0, false);
				return true;
			}
			else {
				bank.msgRequestWithdrawal(person,  accountNum,  100,  false);
				return true;
			}
		}
		*/
		
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
		if (shiftDone) {leaveWork();}
		

		return false;
	
	}

	// Actions
	private void leaveWork() {
		person.msgStopWork(wage);
		isWorking = false; 
	}
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
		bill.market.msgHereIsPayment(restaurant, bill.amount);
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
		public Market_Douglass market;
		double amount; 
		
		public MarketBill(Market_Douglass m, double am) {
			market = m;
			amount = am;
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
	
	public void subtract(double amount) {
		money-=amount;
	}




}
