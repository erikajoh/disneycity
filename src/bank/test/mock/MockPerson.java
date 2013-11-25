package bank.test.mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bank.gui.Account;
import bank.interfaces.Person;

public class MockPerson extends Mock implements Person {
	
	public EventLog log;
	private String name;
	public double balance = 25.00;
	private Account originalAccount;
	public int newAccountNum;
	private double loanAmt;
	private int loanTime;

	private double change;
		
	// agent correspondents
	private MockBank bank = null;

	private enum State{arrive, leave, idle};
	State state = State.idle;
	
	int decision = 0;
	double requestAmount = 0;
	
	public List<Account> accounts = Collections.synchronizedList(new ArrayList<Account>());;
	

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

	public void msgArrive(int num, double reqAmt){
		log.add(new LoggedEvent("PERSON ARRIVE"));
		decision = num;
		requestAmount = reqAmt;
		state = State.arrive;
	}
	
	//double loanAmt
	
	public void msgLeave(int accNum, double ch, double la, int lt){
		log.add(new LoggedEvent("TRANSACTION COMPLETE"));
		newAccountNum = accNum;
	    change = ch;
	    loanAmt = la;
	    loanTime = lt;
		state = State.leave;
	}
	

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
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
		log.add(new LoggedEvent("TELL BANK HERE"));
		state = State.idle;		
		
		if(accounts.size() == 0){
			bank.msgRequestAccount(this, requestAmount, true);
		}
		else if(decision == 0){
			originalAccount = accounts.get(0); //really a pick method for the index
		    bank.msgRequestDeposit(this, originalAccount.getNumber(), requestAmount, true);
		}
		else if(decision == 1){
			 originalAccount = accounts.get(0); //really a pick method for the index
			 bank.msgRequestWithdrawal(this, originalAccount.getNumber(), requestAmount, true);
		}
	
	}
	
	private void leave(){
		log.add(new LoggedEvent("PERSON LEFT BANK "+ newAccountNum + " " + change + " " + loanAmt + " " + loanTime));
		System.out.println("CHANGE: "+ change);
		balance+=change;
		System.out.println(balance);
		if(originalAccount == null){
			Account newAccount = new Account(newAccountNum, balance);
			newAccount.setBalance(balance);
			accounts.add(newAccount);
		}
		else {
			originalAccount.setBalance(balance);
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

