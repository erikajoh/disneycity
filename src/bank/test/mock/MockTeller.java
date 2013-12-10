package bank.test.mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bank.gui.Account;
import bank.gui.TellerGui;


public class MockTeller extends Mock {
	
	  public EventLog log;
	  MockManager manager;
	  public enum CustomerState {deciding, openingAccount, depositingCash, withdrawingCash, robbingBank, leaving, idle};
	  enum State{shouldEnter, shouldLeave, working};
	  State state = State.working;
	  private boolean success;

		class Customer {
		  MockBankCustomer bankCustomer;
		  Account account;
		  int creditRating;
		  double requestAmt;
		  boolean success;
		  
		  CustomerState state;
		  
		  public Customer(MockBankCustomer p){
			  bankCustomer = p;
			  int accountNumber = p.getAccountNum();
			  
			  if(accountNumber == -1){
				  account = null;
			  }
			  else{
				List<Account> accounts = manager.getAccounts();
			      for(Account acc : accounts){
				      if(acc.number == accountNumber){
				    	  account = acc; break;
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
		
		public MockTeller(String name) {
			super(name);
			log = new EventLog();
			this.name = name;
		}

		public String getMaitreDName() {
			return name;
		}

		public String getName() {
			return name;
		}

		
		// Messages
		public void msgNewCustomer(MockBankCustomer bankCustomer){
			for(Customer cust : customers){
				if(cust.bankCustomer == bankCustomer){
					log.add(new LoggedEvent("CUSTOMER IS "+bankCustomer.toString()));
					customer = cust;
					customer.state = CustomerState.idle;
				}
			}
			if(customer == null){
				customer = new Customer(bankCustomer);
				customers.add(customer);
			}
		}

		public void	msgOpenAccount(MockBankCustomer bankCustomer, double cash){ //open account w/ initial amt of cash
			log.add(new LoggedEvent("OPEN ACCOUNT"));

			boolean found = false;
			for(Customer cust : customers){
				if(cust.bankCustomer == bankCustomer){
					customer = cust;
					customer.state = CustomerState.openingAccount;
					found = true; break;
				}
			}
			if(found == false){
				customer = new Customer(bankCustomer);
				customers.add(customer);
			}
			customer.requestAmt = cash;
		}

		public void	msgDepositCash(int accountNum, double cash){
			log.add(new LoggedEvent("DEPOSIT CASH"));
			customer.requestAmt = cash;
			List<Account> accounts = manager.getAccounts();
			for(Account acc : accounts){
				if(acc.number == accountNum){
					customer.account = acc; break;
				}
			}
			customer.state = CustomerState.depositingCash; 
		}
		
		public void	msgWithdrawCash(int accountNum, double cash){
			log.add(new LoggedEvent("WITHDRAW CASH"));
			customer.requestAmt = cash;
			List<Account> accounts = manager.getAccounts();
			for(Account acc : accounts){
				if(acc.number == accountNum){
					customer.account = acc; break;
				}
			}
			customer.state = CustomerState.withdrawingCash; 
		}
		
		public void msgRobBank(double cash){
			customer.requestAmt = cash;
			customer.state = CustomerState.robbingBank; 
		}
		
		public void	msgLeavingBank(){
			log.add(new LoggedEvent("LEAVING"));
			customer.state = CustomerState.leaving;
		}
		
		public void msgOpen(){
			state = State.shouldEnter;
		}
		
		public void msgClose(){
			state = State.shouldLeave;
		}
		
		/**
		 * Scheduler.  Determine what action is called for, and do it.
		 */
		public boolean pickAndExecuteAnAction() {
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
			customer.success = success;
			customer.bankCustomer.msgRobbedBank(cash, customer.success);
			customer.state = CustomerState.deciding;
		}

		private void customerLeaving(){
			manager.msgTellerFree(this, customer.bankCustomer);
			customer = null;
		}
		
		
		//utilities
		public void setSuccess(boolean suc){
			success = suc;
		}
		public void setGui(TellerGui gui) {
			tellerGui = gui;	
		}
			
		public void setManager(MockManager m) {
			manager = m;
		}

		public TellerGui getGui() {
			return tellerGui;
		}
	}


