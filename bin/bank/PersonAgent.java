package bank;

import bank.gui.PersonGui;
import bank.gui.BankGui;
import bank.gui.Account;
import bank.interfaces.Person;
import bank.interfaces.Teller;
import bank.interfaces.Bank;
import agent.Agent;

import java.util.*;



/**
 * bank customer agent.
 */
public class PersonAgent extends Agent implements Person {
	private String name;
	private double balance = 25.00;
	private double change;
	
	private BankGui bankGui;
	private PersonGui personGui;
	
	// agent correspondents
	private Bank bank = null;
	private Teller teller = null;

	public enum State
	{enteredBank, goToTeller, deciding, openingAccount, depositing, withdrawing, left, idle};
	
	State state = State.idle;
		
	List<Account> accounts = Collections.synchronizedList(new ArrayList<Account>());;
	

	/**
	 * Constructor for CustomerAgent class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the customergui so the customer can send it messages
	 */
	public PersonAgent(String name, BankGui bg){
		super();
		this.name = name;
		bankGui = bg;
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
		print("MONEY DEPOSITED "+ balance);
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
	
	public void msgAnimationFinishedLeavingBank(){
		state = State.left;
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
		else if(state == State.left){
			leftBank();
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
	    bankGui.updateInfoPanel(this);
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
	
	private void leftBank(){
		state = State.idle;
	    bankGui.updateInfoPanel(this);
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

