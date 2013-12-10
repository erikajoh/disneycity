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
	public double loanAmt;
	public int loanTime;
	public int accountChoice = 0;
	

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

		if((accounts.size() == 0 || decision < 0 || decision > 2) && decision !=2){
			originalAccount = null;
			bank.msgRequestAccount(this, requestAmount, true);
		}
		else {
			switch(decision){
			case 0:
			for(Account acc : accounts){
				if(acc.number == accountChoice){
					originalAccount = acc; break;
				}
			}
		    bank.msgRequestDeposit(this, originalAccount.getNumber(), requestAmount, true); break;
			case 1:
			for(Account acc : accounts){
				if(acc.number == accountChoice){
					originalAccount = acc; break;
				}
			}

			bank.msgRequestWithdrawal(this, originalAccount.getNumber(), requestAmount, true);
			requestAmount *= -1; break;
			
			case 2:
			bank.msgThief(this, requestAmount, true); break;
			}
		}
	}
	
	private void leave(){
		log.add(new LoggedEvent("PERSON LEFT BANK "+ newAccountNum + " " + change + " " + loanAmt + " " + loanTime));
		balance+=change;
		if(originalAccount == null && newAccountNum != -1){
			Account newAccount = new Account(newAccountNum, 0.00);
			newAccount.balance += requestAmount;
			newAccount.setLoanAmount(loanAmt);
			newAccount.setLoanTime(loanTime);
			accounts.add(newAccount);
		}
		else if(originalAccount != null){
			originalAccount.balance += requestAmount;
			if(originalAccount.loanAmount > 0 && loanAmt == 0){
				if(originalAccount.loanTime > 0){
				 originalAccount.balance -= originalAccount.loanAmount;
			    }
				else{
					 originalAccount.balance -= (originalAccount.loanAmount+((-originalAccount.loanTime+1)*25));
				}
			    
			
			}
			if(originalAccount.balance < 0){
				originalAccount.balance = 0;
			}
			originalAccount.setLoanAmount(loanAmt);
			originalAccount.setLoanTime(loanTime);
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
	
	public double getBalance(int accNum) {
		Account account = null;
		for(Account acc : accounts){
			if(acc.number == accNum){
				account = acc; break;
			}
		}
		return account.getBalance();
	}
	
	public double getLoanAmount(int accNum) {
		Account account = null;
		for(Account acc : accounts){
			if(acc.number == accNum){
				account = acc; break;
			}
		}
		return account.getLoanAmount();
	}
	
	public int getLoanTime(int accNum) {
		Account account = null;
		for(Account acc : accounts){
			if(acc.number == accNum){
				account = acc; break;
			}
		}
		return account.getLoanTime();
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

