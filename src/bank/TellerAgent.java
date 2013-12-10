package bank;

import agent.Agent;
import bank.gui.Bank;
import bank.gui.TellerGui;
import bank.gui.Account;
import bank.interfaces.Teller;
import bank.interfaces.Manager;
import bank.interfaces.BankCustomer;

import java.util.*;

import simcity.PersonAgent;
import simcity.gui.trace.AlertLog;
import simcity.gui.trace.AlertTag;
import simcity.interfaces.Person;

/**
 * bank Host Agent
 */

public class TellerAgent extends Agent implements Teller {
	Manager manager;
	Person person;
	
	final static double amountPerDay = 25.00;
	private Random robberySuccess = new Random();
	
	private Bank bank;
	private boolean shouldLeave = false;
	
	enum CustomerState {deciding, openingAccount, depositingCash, withdrawingCash, robbingBank, leaving, idle};

	class Customer {
	  BankCustomer bankCustomer;
	  Account account;
	  double requestAmt;
	  boolean success;
	  
	  CustomerState state;

	  public Customer(BankCustomer p){
		  bankCustomer = p;
		  int accountNumber = p.getAccountNum();
		  
		  if(accountNumber == -1){
			  account = null;
		  }
		  else{
			  List<Account> accounts = manager.getAccounts();
			  synchronized(accounts){
		         for(Account acc :accounts){
			         if(acc.number == accountNumber){
			    	     account = acc; break;
			         }
		         }
			  }
		  }
		  		  
		  state = CustomerState.idle;
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
	
	public TellerAgent(Person p, String name) {
		super();
		person = p;
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
		synchronized(customers){
		   for(Customer cust : customers){
			   if(cust.bankCustomer == bankCustomer){
				   print("CUSTOMER IS "+bankCustomer.toString());
				   customer = cust;
				   customer.state = CustomerState.idle;
			   }
		   }
		}
		if(customer == null){
			customer = new Customer(bankCustomer);
			customers.add(customer);
		}
		stateChanged();
	}

	public void	msgOpenAccount(BankCustomer bankCustomer, double cash){ //open account w/ initial amt of cash
		print("OPEN ACCOUNT");
		boolean found = false;
		synchronized(customers){
		   for(Customer cust : customers){
			   if(cust.bankCustomer == bankCustomer){
				   customer = cust;
				   customer.state = CustomerState.openingAccount;
				   found = true; break;
			   }
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
		synchronized(accounts){
		   for(Account acc : accounts){
			   if(acc.number == accountNum){
				   customer.account = acc;
				   customer.state = CustomerState.depositingCash; break;
			   }
		   }
		}
		stateChanged();
	}
	
	public void	msgWithdrawCash(int accountNum, double cash){
		print("WITHDRAW CASH ");
		customer.requestAmt = cash;
		List<Account> accounts = manager.getAccounts();
		synchronized(accounts){
		   for(Account acc : accounts){
			   if(acc.number == accountNum){
				   customer.state = CustomerState.withdrawingCash;
				   customer.account = acc; break;
			   }
		   }
		}
		customer.state = CustomerState.withdrawingCash; 
		stateChanged();
	}
	
	public void msgRobBank(double cash){
		print("ROB BANK");
		customer.requestAmt = cash;
		customer.state = CustomerState.robbingBank; 
		stateChanged();
	}

	
	public void	msgLeavingBank(){
		print("LEAVING");
		customer.state = CustomerState.leaving;
		stateChanged();
	}
	
	public void	msgClose(Bank b){
		AlertLog.getInstance().logMessage(AlertTag.BANK, "Bank", "Teller should leave");
		bank = b;
		shouldLeave = true;
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
		    if(customer.state == CustomerState.openingAccount){
			   openAccount();
			   return true;
		    }
		   else if(customer.state == CustomerState.depositingCash){
			   depositCash();
			   return true;
		    }
		   else if(customer.state == CustomerState.withdrawingCash){
			   withdrawCash();
			   return true;
		    }
		   else if(customer.state == CustomerState.robbingBank){
			   robBank();
			   return true;
		    }
		  else if(customer.state == CustomerState.leaving){
			   customerLeaving();
			   return true;
		  }
		}
		
		if(shouldLeave == true){
			AlertLog.getInstance().logMessage(AlertTag.BANK, "Bank", "Teller should leave called from sched");
			leaveBank();
			return true;
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
		customer.state = CustomerState.deciding;
	}

	private void depositCash(){
		if(customer.account.loanTime <= 0 && customer.account.loanAmount > 0){
			customer.account.loanAmount += ((-customer.account.loanTime+1)*amountPerDay); //accounts for the extra day about to be decremented
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
		customer.state = CustomerState.deciding;
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
		customer.state = CustomerState.deciding;
	}
	
	private void robBank(){
		double cash = customer.requestAmt;
		customer.success = robberySuccess.nextBoolean();
		customer.bankCustomer.msgRobbedBank(cash, customer.success);
		customer.state = CustomerState.deciding;
	}
	
	
	private void customerLeaving(){
		customer.state = CustomerState.idle;
		manager.msgTellerFree(this, customer.bankCustomer);
		customer = null;
	}
	
	
	private void leaveBank(){
		AlertLog.getInstance().logMessage(AlertTag.BANK, "Bank", "Teller leave bank called");
		tellerGui.DoLeaveBank();
		shouldLeave = false;
		bank.msgTellerLeftBank(this);
	}
	
	//utilities
	
	public void setPerson(Person p) {
		person = p;
	}
	
	public Person getPerson() {
		return person;
	}
	
	public void setBank(Bank b){
		bank = b;
	}
	
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


