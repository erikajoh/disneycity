package bank.test.mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import bank.gui.Account;
import bank.gui.TellerGui;
import bank.interfaces.BankCustomer;
import bank.interfaces.Manager;
import bank.interfaces.Teller;

public class MockTeller extends Mock implements Teller {
	
	  public EventLog log;
	  Manager manager;
	  List<Account> accounts = Collections.synchronizedList(new ArrayList<Account>());

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
			      for(Account acc : accounts){
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
		
		public MockTeller(String name) {
			super(name);

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
					log.add(new LoggedEvent("CUSTOMER IS "+bankCustomer.toString()));
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
			log.add(new LoggedEvent("OPEN ACCOUNT"));
			Account account = new Account(accounts.size());
			for(Customer cust : customers){
				if(cust.bankCustomer == bankCustomer){
					customer = cust;
					customer.state = State.openingAccount;
				}
			}
			accounts.add(account);
		}

		public void	msgDepositCash(int accountNum, double cash){
			log.add(new LoggedEvent("DEPOSIT CASH"));
			customer.requestAmt = cash;
			customer.state = State.depositingCash; 
		}
		
		public void	msgWithdrawCash(int accountNum, double cash){
			log.add(new LoggedEvent("WITHDRAW CASH"));
			customer.requestAmt = cash;
			customer.state = State.withdrawingCash; 
		}
		
		public void	msgLeavingBank(){
			log.add(new LoggedEvent("LEAVING"));
			customer.state = State.leaving;
		}
		
		/**
		 * Scheduler.  Determine what action is called for, and do it.
		 */
		protected boolean pickAndExecuteAnAction() {
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
			int accountNum = accounts.size();
			customer.account = new Account(accountNum);
			accounts.add(customer.account);
			customer.bankCustomer.msgAccountOpened(accountNum, customer.requestAmt);
			customer.state = State.deciding;
		}

		private void depositCash(){
			if(customer.account.loanAmount > 0){
				if(customer.account.loanAmount > customer.requestAmt){
				  customer.account.loanAmount -= customer.requestAmt;
				}
				else{
					customer.requestAmt -= customer.account.loanAmount;
					customer.account.loanAmount = 0.00;
					customer.account.loanTime = 0;
				}
			}
		    if(customer.requestAmt > 0){
			  double newBalance = customer.account.getBalance()+customer.requestAmt;
			  customer.account.setBalance(newBalance);
		    }
			customer.bankCustomer.msgMoneyDeposited(customer.requestAmt, customer.account.loanAmount, customer.account.loanTime);
			customer.state = State.deciding;
		}

		private void withdrawCash(){
			double newBalance = customer.account.getBalance()-customer.requestAmt;
			if(newBalance >= 0){
			  customer.account.setBalance(newBalance);
			  customer.account.loanAmount = 0.00;
			  customer.account.loanTime = 0;
			}
			else{
			  customer.account.setBalance(0);
			  customer.requestAmt = customer.account.getBalance();
			  customer.account.loanAmount = -customer.requestAmt;
			  customer.account.loanTime = 0;
			}
			customer.bankCustomer.msgMoneyWithdrawn(customer.requestAmt, customer.account.loanAmount, customer.account.loanTime);
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


