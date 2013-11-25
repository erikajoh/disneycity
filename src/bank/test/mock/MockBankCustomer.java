package bank.test.mock;

import simcity.gui.SimCityGui;
import bank.gui.BankCustomerGui;
import bank.interfaces.BankCustomer;
import bank.interfaces.Manager;
import bank.interfaces.Teller;

public class MockBankCustomer extends Mock implements BankCustomer {
	
	public EventLog log;
	private String name;
	private double balance = 25.00;
	private double change;
	
	private double loanAmount;
	private int loanTime;
	
	private SimCityGui simCityGui;
	private BankCustomerGui personGui;
	
	// agent correspondents
	private Manager manager = null;
	private Teller teller = null;
	
	public enum State
	{deciding, openingAccount, depositing, withdrawing, leaving, left, idle};
	State state = State.idle;

	public enum AnimState{go, walking, idle};
	AnimState animState = AnimState.idle;
	
	int accountNum;
	double requestAmt;	

	/**
	 * Constructor for CustomerAgent class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the customergui so the customer can send it messages
	 */
	public MockBankCustomer(String name, Manager m, SimCityGui bg){
		super(name);
		log = new EventLog();
		this.name = name;
		manager = m;
		simCityGui = bg;
		state = State.idle;
	}

	public String getCustomerName() {
		return name;
	}
	// Messages
	
	public void	msgRequestNewAccount(double ra){
		requestAmt = ra;
		state = State.openingAccount;
	}
	public void	msgRequestDeposit(double ra){
		requestAmt = ra;
		state = State.depositing;
	}
	public void	msgRequestWithdraw(double ra){
		log.add(new LoggedEvent("REQ WITHDRAW"));
		requestAmt = ra;
		log.add(new LoggedEvent("RA: "+requestAmt));
		state = State.withdrawing;
	}

	public void msgGoToTeller(Teller t){
		teller = t;
		animState = AnimState.go; 
	}
	public void msgAccountOpened(int an, double amountWithdrawn){
		balance += change;
		change = amountWithdrawn;
		log.add(new LoggedEvent("ACCOUNT OPENED "+balance));
		accountNum = an;
		state = State.leaving;
	}
	public void msgMoneyDeposited(double amountAdded, double loanAmt, int lt){
		balance += amountAdded;
		change = amountAdded;
		log.add(new LoggedEvent("MONEY DEPOSITED "+ balance));
		state = State.leaving;
		loanAmount = loanAmt;
		loanTime = lt;
	}
	public void msgMoneyWithdrawn(double amountWithdrawn, double loanAmt, int lt){
		balance += change;
		log.add(new LoggedEvent("MONEY WITHDRAWN "+ balance));
		state = State.leaving;
		change = amountWithdrawn;
		loanAmount = loanAmt;
		loanTime = lt;
	}
	
	public void msgAnimationFinishedGoToTeller(){
		log.add(new LoggedEvent("AT TELLER " + state));
		animState = AnimState.idle;
	}
	
	public void msgAnimationFinishedLeavingBank(){
		log.add(new LoggedEvent("LEFT BANK"));
		animState = AnimState.idle;
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		//	CustomerAgent is a finite state machine
	    if(animState == AnimState.go){
			goToTeller();
			return true;
		}
	    else if(animState == AnimState.idle){
		
		   if(state == State.openingAccount){
			   openAccount();
			   return true;
		    }
		   else if(state == State.depositing){
			   depositCash();
			   return true;
		    }
		   else if(state == State.withdrawing){
			   withdrawCash();
			   return true;
		    }
		   else if(state == State.leaving){
			   leaveBank();
			   return true;
		   }
		  else if(state == State.left){
			   leftBank();
			   return true;
		   }
	    }
		return false;
	}

	// Actions
	
	private void goToTeller(){
		state = State.idle;
		animState = AnimState.walking;
	}
	
	private void openAccount(){
		if(teller == null){
			log.add(new LoggedEvent("TELLER NULL"));
		}
		teller.msgOpenAccount(this, balance*.5);
		change = -balance*.5;
		state = State.idle;
	}
	private void depositCash(){
		teller.msgDepositCash(accountNum, requestAmt);
		state = State.idle;
	}
	private void withdrawCash(){
		teller.msgWithdrawCash(accountNum, requestAmt);
		state = State.idle;
	}
	private void leaveBank(){
		teller.msgLeavingBank();
		animState = AnimState.walking;
		state = State.left;
		personGui.DoLeaveBank();
	}
	
	private void leftBank(){
		state = State.idle;
	}

	// Accessors, etc.

	public String getName() {
		return name;
	}
	
	public void setBalance(double b) {
		balance = b;
	}
	
	public double getBalance(){
		return balance;
	}

	public String toString() {
		return "customer " + getName();
	}
	
	
	public void setManager(Manager m) {
		manager = m;
	}
	
	public Manager getManager() {
		return manager;
	}
	
	public double getLoanAmount(){
		return loanAmount;
	}
	
	public int getLoanTime(){
		return loanTime;
	}
	
	public int getAccountNum(){
		return accountNum;
	}
	
	public void setAccountNum(int num) {
		accountNum = num;
	}
	
	public void setTeller(Teller t) {
		teller = t;
	}
	
	public void setGui(BankCustomerGui g) {
		personGui = g;
	}

	public BankCustomerGui getGui() {
		return personGui;
	}
}

