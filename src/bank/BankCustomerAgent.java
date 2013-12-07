package bank;

import bank.gui.BankCustomerGui;
import simcity.gui.SimCityGui;
import bank.gui.Account;
import bank.interfaces.BankCustomer;
import bank.interfaces.Person;
import bank.interfaces.Teller;
import bank.interfaces.Manager;
import agent.Agent;

import java.util.*;



/**
 * bank customer agent.
 */
public class BankCustomerAgent extends Agent implements BankCustomer {
	private String name;
	private double balance;
	private double change;
	
	private double loanAmount;
	private int loanTime;
	
	private SimCityGui simCityGui;
	private BankCustomerGui personGui;
	
	// agent correspondents
	private Manager manager = null;
	private Teller teller = null;
	
	public enum State
	{deciding, goingToTeller, openingAccount, depositing, withdrawing, robbing, leaving, failedRobbery, left, idle};
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
	public BankCustomerAgent(String name, Manager m, SimCityGui bg){
		super();
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
		print("REQ NEW ACCOUNT");
		requestAmt = ra;
		state = State.openingAccount;
		stateChanged();
	}
	public void	msgRequestDeposit(double ra, int accNum){
		print("REQ DEPOSIT");
		requestAmt = ra;
		accountNum = accNum;
		state = State.depositing;
		stateChanged();	
	}
	public void	msgRequestWithdraw(double ra, int accNum){
		print("REQ WITHDRAW");
		requestAmt = ra;
		accountNum = accNum;
		print("RA: "+requestAmt);
		state = State.withdrawing;
		stateChanged();
	}
	
	public void	msgThief(double ra){
		requestAmt = ra;
		print("ROBBING: "+requestAmt);
		state = State.robbing;
		stateChanged();
	}

	public void msgGoToTeller(Teller t){
		teller = t;
		animState = AnimState.go; 
		stateChanged();
	}
	public void msgAccountOpened(int an, double amountWithdrawn){
		balance += change;
		change = amountWithdrawn;
		print("ACCOUNT OPENED "+balance);
		accountNum = an;
		state = State.leaving;
		stateChanged();
	}
	public void msgMoneyDeposited(double amountAdded, double loanAmt, int lt){
		balance += amountAdded;
		change = amountAdded;
		print("MONEY DEPOSITED "+ balance);
		state = State.leaving;
		loanAmount = loanAmt;
		loanTime = lt;
		stateChanged();
	}
	public void msgMoneyWithdrawn(double amountWithdrawn, double loanAmt, int lt){
		balance += change;
		print("MONEY WITHDRAWN "+ balance);
		state = State.leaving;
		change = amountWithdrawn;
		loanAmount = loanAmt;
		loanTime = lt;
		stateChanged();
	}
	
	public void msgRobbedBank(double cash, boolean success){
		balance += cash;
		print("GOT CASH "+ cash);
		if(success == true){
			state = State.leaving;
		}
		else {
			state = State.failedRobbery;
		}
		
		stateChanged();
	}
	
	public void msgAnimationFinishedGoToTeller(){
    	System.out.println("MSG AT TELLER");
		print("AT TELLER " + state);
		animState = AnimState.idle;
		stateChanged();
	}
	
	public void msgAnimationFinishedLeavingBank(){
		animState = AnimState.idle;
		state = State.left;
		stateChanged();
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
		   else if(state == State.robbing){
			   robBank();
			   return true;
		    }
		   else if(state == State.leaving){
			   leaveBank();
			   return true;
		   }
		   else if(state == State.failedRobbery){
			   tryToLeaveBank();
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
		animState = AnimState.walking;
		personGui.DoGoToTeller(teller.getGui().getBaseX(), teller.getGui().getBaseY());
		state = State.openingAccount;
//	    simCityGui.updateInfoPanel(this);
	}
	
	private void openAccount(){
		if(teller == null){
			print("TELLER NULL");
		}
		teller.msgOpenAccount(this, requestAmt);
		state = State.idle;
		stateChanged();
	}
	
	private void depositCash(){
		teller.msgDepositCash(accountNum, requestAmt);
		state = State.idle;
		stateChanged();
	}
	private void withdrawCash(){
		teller.msgWithdrawCash(accountNum, requestAmt);
		state = State.idle;
		stateChanged();
	}
	private void robBank(){
		teller.msgRobBank(requestAmt);
		state = State.idle;
		stateChanged();
	}
	private void leaveBank(){
		animState = AnimState.walking;
		state = State.idle;
		personGui.DoLeaveBank();
	}
	private void tryToLeaveBank(){
		animState = AnimState.walking;
		state = State.idle;
		personGui.DoFailRobbery();
	}
	
	private void leftBank(){
		state = State.idle;
		teller.msgLeavingBank();
		stateChanged();
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
	
	public double getChange(){
		return change;
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

