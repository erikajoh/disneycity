package bank;

import bank.gui.BankCustomerGui;
import bank.gui.BankGui;
import bank.gui.Account;
import bank.interfaces.BankCustomer;
import bank.interfaces.Person;
import bank.interfaces.Bank;
import agent.Agent;

import java.util.*;



/**
 * bank customer agent.
 */
public class PersonAgent extends Agent implements Person {
	private String name;
	private double balance = 25.00;
		
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
		decision = num;
		state = State.arrive;
		stateChanged();
	}
	
	public void msgLeave(){
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
		bank.msgCustomerHere(this);
		if(accounts.size() == 0){
			bank.msgRequestAccount(balance*.5, this);
		}
		else if(decision == 0){
		 bank.msgRequestDeposit(accounts.get(0).getNumber(), 5.00, this, false);
		}
		else if(decision == 1){
			 bank.msgRequestWithdrawal(accounts.get(0).getNumber(), 5.00, this);
		}
	
	}
	
	private void leave(){
		print("LEFT BANK");
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

