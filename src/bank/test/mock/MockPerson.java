package bank.test.mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import simcity.gui.SimCityGui;
import bank.gui.Account;
import bank.gui.Bank;
import bank.gui.BankCustomerGui;
import bank.interfaces.BankCustomer;
import bank.interfaces.Manager;
import bank.interfaces.Person;
import bank.interfaces.Teller;

public class MockPerson extends Mock implements Person {
	
	public EventLog log;
	private String name;
	private double balance = 25.00;
	private Account originalAccount;
	private int newAccountNum;
	private double loanAmt;
	private int loanTime;

	private double newBalance;
		
	// agent correspondents
	private MockBank bank = null;

	private enum State{arrive, leave, idle};
	State state = State.idle;
	
	int decision = 0;
	
	List<Account> accounts = Collections.synchronizedList(new ArrayList<Account>());;
	

	/**
	 * Constructor for CustomerAgent class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the customergui so the customer can send it messages
	 */
	public MockPerson(String name){
		super(name);
		log = new EventLog();
		this.name = name;
	}
	
	// Messages

	public void msgArrive(int num){
		log.add(new LoggedEvent("PERSON ARRIVE"));
		decision = num;
		state = State.arrive;
	}
	
	//double loanAmt
	
	public void msgLeave(int accNum, double balance, double la, int lt){
		newAccountNum = accNum;
	    newBalance = balance;
	    loanAmt = la;
	    loanTime = lt;
		state = State.leave;
	}
	

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		//	CustomerAgent is a finite state machine
		if(state == State.arrive){
			tellBankHere();
			return true;
		}
		else if(state == State.leave){
			leave();
			return true;
		}
		return false;
	}

	// Actions
	
	private void tellBankHere(){
		state = State.idle;
		//bank.msgCustomerHere(this);
		
		
		if(accounts.size() == 0){
			bank.msgRequestAccount(this, balance*.5, true);
		}
		else if(decision == 0){
			originalAccount = accounts.get(0); //really a pick method for the index
		    bank.msgRequestDeposit(this, originalAccount.getNumber(), 5.00, true);
		}
		else if(decision == 1){
			 originalAccount = accounts.get(0); //really a pick method for the index
			 bank.msgRequestWithdrawal(this, originalAccount.getNumber(), 5.00, true);
		}
	
	}
	
	private void leave(){
		log.add(new LoggedEvent("PERSON LEFT BANK "+ newAccountNum + " " + newBalance + " " + loanAmt + " " + loanTime));
		if(originalAccount == null){
			Account newAccount = new Account(newAccountNum);
			newAccount.setBalance(newBalance);
			accounts.add(newAccount);
		}
		else {
			originalAccount.setBalance(newBalance);
		}
		state = State.idle;
	}
	
	// Accessors, etc.

	public String getName() {
		return name;
	}
	
	public void setBalance(double b) {
		balance = b;
	}
	
	public List<Account> getAccounts() {
		return accounts;
	}

	public String toString() {
		return "customer " + getName();
	}
	
	public void setBank(MockBank b) {
		bank = b;
	}
	
	public MockBank getBank() {
		return bank;
	}
}

