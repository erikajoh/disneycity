package bank;

import agent.Agent;
import bank.gui.TellerGui;
import bank.gui.Account;
import bank.interfaces.Teller;
import bank.interfaces.Manager;
import bank.interfaces.BankCustomer;
import bank.test.mock.MockTeller.State;

import java.util.*;

/**
 * bank Host Agent
 */

public class TellerAgent extends Agent implements Teller {
	Manager manager;
	
	enum State {deciding, openingAccount, depositingCash, withdrawingCash, leaving, idle};

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
			  List<Account> accounts = manager.getAccounts();
		      for(Account acc :accounts){
			      if(acc.number == accountNumber){
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
		boolean found = false;
		for(Customer cust : customers){
			if(cust.bankCustomer == bankCustomer){
				customer = cust;
				customer.state = State.openingAccount;
				found = true; break;
			}
		}
		if(found == false){
			customer = new Customer(bankCustomer);
			customers.add(customer);
		}
		customer.requestAmt = cash;
		stateChanged();
	}

	public void	msgDepositCash(int accountNum, double cash){
		print("DEPOSIT CASH ");
		customer.requestAmt = cash;
		List<Account> accounts = manager.getAccounts();
		for(Account acc : accounts){
			if(acc.number == accountNum){
				customer.account = acc; break;
			}
		}
		stateChanged();
	}
	
	public void	msgWithdrawCash(int accountNum, double cash){
		print("DEPOSIT CASH ");
		customer.requestAmt = cash;
		List<Account> accounts = manager.getAccounts();
		for(Account acc : accounts){
			if(acc.number == accountNum){
				customer.account = acc; break;
			}
		}
		customer.state = State.withdrawingCash; 
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
		Account newAccount = new Account(manager.getAccounts().size(), customer.requestAmt);
		manager.addAccount(newAccount);
		customer.account = newAccount;
		customer.account.change = -customer.requestAmt;
		customer.bankCustomer.msgAccountOpened(newAccount.number, customer.account.change);
		customer.state = State.deciding;
	}

	private void depositCash(){
		if(customer.account.loanTime <= 0 && customer.account.loanAmount > 0){
			customer.account.loanAmount += ((-customer.account.loanTime+1)*25); //accounts for the extra day about to be decremented
		}
		
		if(customer.account.loanAmount > 0){
			if(customer.account.loanAmount > customer.requestAmt){
			  customer.account.loanAmount -= customer.requestAmt;
			  customer.account.loanTime--;
			}
			else{
				customer.account.balance = customer.requestAmt-customer.account.loanAmount;
				customer.account.loanAmount = 0.00;
				customer.account.loanTime = 0;
			}
		}
		else{ //if there is no loan, add requestAmt to balance
			customer.account.balance += customer.requestAmt;
		}
	  
	    customer.account.change = -customer.requestAmt;
		customer.bankCustomer.msgMoneyDeposited(customer.account.change, customer.account.loanAmount, customer.account.loanTime);
		customer.state = State.deciding;
	}


	private void withdrawCash(){
		double newBalance = customer.account.balance-customer.requestAmt;
		if(customer.account.loanTime <= 0 && customer.account.loanAmount > 0){
			customer.account.loanAmount += ((-customer.account.loanTime+1)*25); //accounts for the extra day about to be decremented
		}
		if(newBalance >= 0){
		  customer.account.balance = newBalance;
		  customer.account.change = customer.requestAmt;
		  customer.account.loanAmount = 0.00;
		  customer.account.loanTime = 0;
		}
		else if(customer.account.loanTime > 0 || (customer.account.loanTime == 0 && customer.account.loanAmount == 0)){ //can only take out loans if there is time left to pay them
		  customer.account.change = customer.requestAmt;
		  if(customer.account.loanTime == 0 && customer.account.loanAmount == 0){
			    customer.account.loanTime = 3; //if this is the first loan, loanTime should be 3
		  }
		  else{
		    customer.account.loanTime--; //if the customer is adding to his loan, he will have less time
		  }
		  customer.account.loanAmount -= newBalance;
		  customer.account.balance = 0.00;
		}
		else {
			customer.account.change = 0.00; //can't withdraw any money if can't pay off loans
			customer.account.loanTime--; //double jeopardy as loan time gets worse as well
		}
		customer.bankCustomer.msgMoneyWithdrawn(customer.account.change, customer.account.loanAmount, customer.account.loanTime);
		customer.state = State.deciding;
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


