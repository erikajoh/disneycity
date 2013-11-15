package restaurant;

import agent.Agent;
import restaurant.gui.TellerGui;
import restaurant.gui.Account;
import restaurant.interfaces.Teller;
import restaurant.interfaces.Bank;
import restaurant.interfaces.Person;

import java.util.*;

/**
 * Restaurant Host Agent
 */

public class TellerAgent extends Agent implements Teller {
	int accounts;
	Bank bank;

	enum State {deciding, openingAccount, depositingCash, withdrawingCash, decidingOnLoan, givingLoan, leaving, idle};

	class Customer {
	  Person person;
	  List<Account> accounts;
	  Account pendingAccount;
	  int creditRating;
	  double requestAmt;
	  State state;
	  
	  public Customer(Person p){
		  person = p;
		  accounts = p.getAccounts();
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
	public void msgNewCustomer(Person person){
		print("NEW CUSTOMER");
		for(Customer cust : customers){
			if(cust.person == person){
				customer = cust;
				customer.state = State.idle;
			}
		}
		if(customer == null){
			customer = new Customer(person);
			customers.add(customer);
		}
	}

	public void	msgOpenAccount(Person person, double cash){ //open account w/ initial amt of cash
		print("OPEN ACCOUNT");
		Account account = new Account(accounts);
		for(Customer cust : customers){
			if(cust.person == person){
				customer = cust;
				customer.state = State.openingAccount;
			}
		}
		customer.accounts.add(account);
		accounts++;
		stateChanged();
	}

	public void	msgDepositCash(Account account, double cash){
		print("DEPOSIT CASH "+customer.accounts.size());
		for(Account acc : customer.accounts){
			if(account == acc){
				customer.requestAmt = cash;
				customer.pendingAccount = account;
				customer.state = State.depositingCash; break;
			}
		}
		stateChanged();
	}
	
	public void	msgWithdrawCash(Account account, double cash){
		print("WITHDRAW CASH "+customer.accounts.size());
		for(Account acc : customer.accounts){
			if(account == acc){
				print("FOUND OR NO???");
				customer.requestAmt = cash;
				customer.pendingAccount = account;
				customer.state = State.withdrawingCash; break;
			}
		}
		stateChanged();
	}
	
	public void	msgAskForLoan(Account account, double cash){
		print("ASK FOR LOAN");
		for(Account acc : customer.accounts){
			if(account == acc){
				customer.requestAmt = cash;
				customer.pendingAccount = account;
				customer.state = State.decidingOnLoan; break;
			}
		}
		stateChanged();
	}
	
	public void	msgLeavingBank(){
		print("LEAVING");
		customer.state = State.leaving;
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
		customer.person.msgAccountOpened(customer.pendingAccount);
		customer.state = State.deciding;
	}

	private void depositCash(){
		double newBalance = customer.pendingAccount.getBalance()+customer.requestAmt;
		customer.pendingAccount.setBalance(newBalance);
		customer.requestAmt = 0;
		customer.person.msgMoneyDeposited();
		customer.state = State.deciding;
	}

	private void withdrawCash(){
		print("HERE???");
		double newBalance = customer.pendingAccount.getBalance()-customer.requestAmt;
		if(newBalance >= 0){
		  customer.pendingAccount.setBalance(newBalance);
		}
		else{
		  customer.pendingAccount.setBalance(0);
		  customer.requestAmt = customer.pendingAccount.getBalance();
		}
		customer.person.msgMoneyWithdrawn(customer.requestAmt);
		customer.requestAmt = 0;
		customer.state = State.deciding;
	}

	private void decideOnLoan(){
		customer.state = State.deciding;
		if(customer.creditRating > 75){
		  customer.person.msgLoanDecision(true);
		  double newLoanBalance = customer.pendingAccount.getLoanBalance()+customer.requestAmt;
		  customer.pendingAccount.setLoanBalance(newLoanBalance);
		  //set loanTime
		}
		else {
		  customer.person.msgLoanDecision(false);
		}
		  customer.requestAmt = 0;

	}

	private void customerLeaving(){
		bank.msgTellerFree(this);
		customer.state = State.idle;
	}
	
	
	//utilities
	public void setGui(TellerGui gui) {
		tellerGui = gui;	
	}
		
	public void setBank(Bank b) {
		bank = b;
	}

	public TellerGui getGui() {
		return tellerGui;
	}
}


