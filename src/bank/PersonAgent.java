package restaurant;

import restaurant.gui.PersonGui;
import restaurant.gui.RestaurantGui;
import restaurant.gui.Account;
import restaurant.interfaces.Person;
import restaurant.interfaces.Teller;
import restaurant.interfaces.Bank;
import agent.Agent;

import java.util.*;



/**
 * Restaurant customer agent.
 */
public class PersonAgent extends Agent implements Person {
	private String name;
	private double balance = 25.00;
	private double change;
	
	private RestaurantGui restaurantGui;
	private PersonGui personGui;
	
	// agent correspondents
	private Bank bank = null;
	private Teller teller = null;

	public enum State
	{enteredBank, goToTeller, deciding, openingAccount, depositing, withdrawing, idle};
	
	State state = State.idle;
		
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
		state = State.enteredBank;
	}

	public String getCustomerName() {
		return name;
	}
	// Messages

	public void msgGoToTeller(Teller t){
		teller = t;
		state = State.goToTeller;
		stateChanged();
	}
	public void msgAccountOpened(Account account){
		balance += change;
		print("ACCOUNT OPENED "+balance);
		accounts.add(account);
		state = State.deciding;
		stateChanged();
	}
	public void msgMoneyDeposited(){
		balance += change;
		print("MONEY DEPOSITED "+ change);
		state = State.deciding;
		stateChanged();
	}
	public void msgMoneyWithdrawn(double amtWithdrawn){
		balance += change;
		print("MONEY WITHDRAWN "+ balance);
		state = State.deciding;
		change = amtWithdrawn;
		stateChanged();
	}
	public void msgLoanDecision(boolean status){
		print("LOAN "+status);
		
	}
	
	public void msgAnimationFinishedGoToTeller(){
		print("AT TELLER");
		state = State.deciding;
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		//	CustomerAgent is a finite state machine
		if(state == State.enteredBank){
			tellBankHere();
			return true;
		}
		else if(state == State.goToTeller){
			goToTeller();
			return true;
		}
		else if(state == State.deciding){
			decideAction();
			return true;
		}
		return false;
	}

	// Actions
	
	private void tellBankHere(){
		state = State.idle;
		bank.msgEnteredBank(this);
	}
	
	private void goToTeller(){
		state = State.idle;
		personGui.DoGoToTeller(teller.getGui().getBaseX(), teller.getGui().getBaseY());
	}
	private void decideAction(){
		if(accounts.size() == 0){
			openAccount(); return;
		}
			
		int num = (int) (Math.random() * 3);	
		switch(num){
		case 0: depositCash(); break;
		case 1: withdrawCash(); break;
		case 2: leaveBank(); break;
		}
	}
	
	private void openAccount(){
		teller.msgOpenAccount(this, balance*.5);
		change = -balance*.5;
		state = State.idle;
	}
	private void depositCash(){
		teller.msgDepositCash(accounts.get(0), 5.00);
		change = -5.00;
		state = State.idle;
	}
	private void withdrawCash(){
		teller.msgWithdrawCash(accounts.get(0), 5.00);
		change = 5.00;
		state = State.idle;
	}
	private void leaveBank(){
		teller.msgLeavingBank();
		state = State.idle;
		personGui.DoLeaveBank();
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
	
	public void setTeller(Teller t) {
		teller = t;
	}
	
	public void setGui(PersonGui g) {
		personGui = g;
	}

	public PersonGui getGui() {
		return personGui;
	}
}

