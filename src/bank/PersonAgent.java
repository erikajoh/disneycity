package bank;

import bank.gui.Bank;
import bank.gui.BankCustomerGui;
import bank.gui.Account;
import bank.interfaces.BankCustomer;
import bank.interfaces.Person;
import bank.interfaces.Manager;
import agent.Agent;

import java.util.*;



/**
 * bank customer agent.
 */
public class PersonAgent extends Agent implements Person {
	private String name;
	private double balance = 25.00;
	private Account originalAccount;
	private int newAccountNum;
	private boolean forLoan;
	private int loanTime;

	private double newBalance;
		
	// agent correspondents
	private Bank bank = null;

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
	public PersonAgent(String name){
		super();
		this.name = name;
	}
	
	// Messages

	public void msgArrive(int num){
		print("PERSON ARRIVE");
		decision = num;
		state = State.arrive;
		stateChanged();
	}
	
	public void msgLeave(int accNum, double balance, boolean fl, int lt){
		newAccountNum = accNum;
	    newBalance = balance;
	    forLoan = fl;
	    loanTime = lt;
		state = State.leave;
		stateChanged();
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
			bank.msgRequestAccount(this, balance*.5);
		}
		else if(decision == 0){
			originalAccount = accounts.get(0); //really a pick method for the index
		    bank.msgRequestDeposit(this, originalAccount.getNumber(), 5.00);
		}
		else if(decision == 1){
			 originalAccount = accounts.get(0); //really a pick method for the index
			 bank.msgRequestWithdrawal(this, originalAccount.getNumber(), 5.00);
		}
	
	}
	
	private void leave(){
		print("PERSON LEFT BANK "+ newAccountNum + " " + newBalance + " " + forLoan + " " + loanTime);
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
	
	public void setBank(Bank b) {
		bank = b;
	}
	
	public Bank getBank() {
		return bank;
	}
}

