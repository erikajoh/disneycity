package bank;

import agent.Agent;
import bank.gui.TellerGui;
import bank.gui.Account;
import bank.interfaces.Teller;
import bank.interfaces.Manager;
import bank.interfaces.BankCustomer;

import java.util.*;

/**
 * bank Host Agent
 */

public class TellerAgent extends Agent implements Teller {
	Manager manager;
	List<Account> accounts = Collections.synchronizedList(new ArrayList<Account>());

	enum State {deciding, openingAccount, depositingCash, withdrawingCash, decidingOnLoan, givingLoan, leaving, idle};

	class Customer {
	  BankCustomer bankCustomer;
	  Account account;
	  int creditRating;
	  double requestAmt;
	  State state;
	  
	  public Customer(BankCustomer p){
		  bankCustomer = p;
		  int accountNumber = p.getAccountNum();
		  
		  if(accountNumber == -1){
			  account = null;
		  }
		  else{
		      for(Account acc : accounts){
			      if(acc.getNumber() == accountNumber){
			    	  account = acc; break;
			      }
		      }
		  }
		  		  
		  state = State.idle;
	  }
	}
	
	Customer customer;
	List<Customer> customers = Collections.synchronizedList(new ArrayList<Customer>());

	private String name;
	TellerGui tellerGui;
	
	public TellerAgent(String name) {
		super();

		this.name = name;
	}

	public String getMaitreDName() {
		return name;
	}

	public String getName() {
		return name;
	}

	
	// Messages
	public void msgNewCustomer(BankCustomer bankCustomer){
		for(Customer cust : customers){
			if(cust.bankCustomer == bankCustomer){
				print("CUSTOMER IS "+bankCustomer.toString());
				customer = cust;
				customer.state = State.idle;
			}
		}
		if(customer == null){
			customer = new Customer(bankCustomer);
			customers.add(customer);
		}
	}

	public void	msgOpenAccount(BankCustomer bankCustomer, double cash){ //open account w/ initial amt of cash
		print("OPEN ACCOUNT");
		Account account = new Account(accounts.size());
		for(Customer cust : customers){
			if(cust.bankCustomer == bankCustomer){
				customer = cust;
				customer.state = State.openingAccount;
			}
		}
		accounts.add(account);
		stateChanged();
	}

	public void	msgDepositCash(int accountNum, double cash){
		print("DEPOSIT CASH ");
		customer.requestAmt = cash;
		customer.state = State.depositingCash; 
		stateChanged();
	}
	
	public void	msgWithdrawCash(int accountNum, double cash){
		print("DEPOSIT CASH ");
		customer.requestAmt = cash;
		customer.state = State.depositingCash; 
		stateChanged();
	}
	

	public void msgAskForLoan(int accountNum, double cash) {
		// TODO Auto-generated method stub
		
	}
	
	public void	msgAskForLoan(Account account, double cash){
		print("ASK FOR LOAN");
		customer.requestAmt = cash;
		customer.state = State.decidingOnLoan;
		
		stateChanged();
	}
	
	public void	msgLeavingBank(){
		print("LEAVING");
		customer.state = State.leaving;
		stateChanged();
	}
	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		/* Think of this next rule as:
            Does there exist a table and customer,
            so that table is unoccupied and customer is waiting.
            If so seat him at the table.
		 */
		if(customer != null){
		    if(customer.state == State.openingAccount){
			   openAccount();
			   return true;
		    }
		   else if(customer.state == State.depositingCash){
			   depositCash();
			   return true;
		    }
		   else if(customer.state == State.withdrawingCash){
			   withdrawCash();
			   return true;
		    }
	      else if(customer.state == State.decidingOnLoan){
			   decideOnLoan();
			   return true;
		   }
		  else if(customer.state == State.leaving){
			   customerLeaving();
			   return true;
		  }
		}
		
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions
	private void openAccount(){
		int accountNum = accounts.size();
		customer.account = new Account(accountNum);
		accounts.add(customer.account);
		customer.bankCustomer.msgAccountOpened(accountNum);
		customer.state = State.deciding;
	}

	private void depositCash(){
		double newBalance = customer.account.getBalance()+customer.requestAmt;
		customer.account.setBalance(newBalance);
		customer.requestAmt = 0;
		customer.bankCustomer.msgMoneyDeposited();
		customer.state = State.deciding;
	}

	private void withdrawCash(){
		double newBalance = customer.account.getBalance()-customer.requestAmt;
		if(newBalance >= 0){
		  customer.account.setBalance(newBalance);
		}
		else{
		  customer.account.setBalance(0);
		  customer.requestAmt = customer.account.getBalance();
		}
		customer.bankCustomer.msgMoneyWithdrawn(customer.requestAmt);
		customer.requestAmt = 0;
		customer.state = State.deciding;
	}

	private void decideOnLoan(){
		customer.state = State.deciding;
		if(customer.creditRating > 75){
		  customer.bankCustomer.msgLoanDecision(true);
		  double newLoanBalance = customer.account.getLoanBalance()+customer.requestAmt;
		  customer.account.setLoanBalance(newLoanBalance);
		  //set loanTime
		}
		else {
		  customer.bankCustomer.msgLoanDecision(false);
		}
		  customer.requestAmt = 0;

	}

	private void customerLeaving(){
		manager.msgTellerFree(this, customer.bankCustomer);
		customer = null;
	}
	
	
	//utilities
	public void setGui(TellerGui gui) {
		tellerGui = gui;	
	}
		
	public void setManager(Manager m) {
		manager = m;
	}

	public TellerGui getGui() {
		return tellerGui;
	}
}


