package bank;

import agent.Agent;
import bank.interfaces.Bank;
import bank.interfaces.Teller;
import bank.interfaces.Person;
import bank.interfaces.BankCustomer;
import bank.gui.BankGui;
import bank.gui.BankCustomerGui;

import java.util.*;

/**
 * bank Host Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the HostAgent. A Host is the manager of a bank who sees that all
//is proceeded as he wishes.
public class BankAgent extends Agent implements Bank {
	   public class WaitingCustomer {
		 BankCustomer bankCustomer;
		 Person person;
		 State state;
		 Action action;
		 private int accountNum;
		 private double requestAmt;
		 public WaitingCustomer(BankCustomer bc, Person p){
			 bankCustomer = bc;
			 person = p;
			 state = State.waiting;
		 }
		 
		 public void setAccountNum(int accNum){
			 accountNum = accNum;
		 }
		 public int getAccountNum(){
			 return accountNum;
		 }
		 public void setRequestAmt(double ra){
			 requestAmt = ra;
		 }
		 public double getRequestAmt(){
			 return requestAmt;
		 }		 
	   }

		List<WaitingCustomer> waitingCustomers = Collections.synchronizedList(new ArrayList<WaitingCustomer>());;
		enum State{entered, waiting, leaving, busy, idle};
		enum Action{newAccount, deposit, withdraw, loan};

		class MyTeller {
			Teller teller;
			TellerState state;
			public MyTeller(Teller t){
				teller = t;
				state = TellerState.idle;
			}
		}
		enum TellerState{idle, busy};

		public List<MyTeller> tellers = Collections.synchronizedList(new ArrayList<MyTeller>());
		
		private BankGui bankGui;

		private String name;


	public BankAgent(String name, BankGui bg) {
		super();
		bankGui = bg;
		this.name = name;
	}

	public String getMaitreDName() {
		return name;
	}

	public String getName() {
		return name;
	}

	
	// Messages
	
	public void msgTellerFree(Teller teller, BankCustomer bankCustomer){
		for(MyTeller t : tellers){
			if(t.teller == teller){
				t.state = TellerState.idle; break;
			}
		}
		for(WaitingCustomer wc : waitingCustomers){
			if(wc.bankCustomer == bankCustomer){
				wc.state = State.leaving; break;
			}
		}
		stateChanged();
	}
	
	public void msgCustomerHere(Person person){
		boolean found = false;
		for(WaitingCustomer wc : waitingCustomers){
			if(wc.person == person){
		       wc.state = State.entered;
		       found = true; break;
		    }
		}
		if(found == false){
			 BankCustomerAgent bca = new BankCustomerAgent(person.getName(), this, bankGui);
		      BankCustomerGui g = new BankCustomerGui(bca, bankGui, bankGui.getAnimWindowX(), bankGui.getAnimWindowY());
		      bankGui.addBankCustomerGui(g);// dw
		      bca.setGui(g);
		      bca.startThread();
		      waitingCustomers.add(new WaitingCustomer(bca, person));
		}
	}

	public void msgRequestAccount(double amount, Person person){
		//print(person.getName());
		for(WaitingCustomer wc : waitingCustomers){
			if(wc.person == person){
				wc.setAccountNum(-1);
				wc.setRequestAmt(amount);
				wc.action = Action.newAccount;
				break;
			}
		}
		stateChanged();
	}
	
	public void msgRequestDeposit(int accountNumber, double amount, Person person, boolean forLoan){
		WaitingCustomer waitingCustomer = null;
		for(WaitingCustomer wc : waitingCustomers){
			if(wc.person == person){
				waitingCustomer = wc;
				wc.setAccountNum(accountNumber);
				wc.setRequestAmt(amount);
				break;
			}
		}
	   if(forLoan == false){
			waitingCustomer.action = Action.deposit;
	   }
	   else {
			waitingCustomer.action = Action.loan;
	   }
		stateChanged();
	}
	
	public void msgRequestWithdrawal(int accountNumber, double amount, Person person){
		for(WaitingCustomer wc : waitingCustomers){
			if(wc.person == person){
				wc.setAccountNum(accountNumber);
				wc.setRequestAmt(amount);
				wc.action = Action.withdraw;
				break;
			}
		}
		  stateChanged();
	}
	
	public void msgRequestLoan(int accountNumber, double amount, Person person){
		
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
		//print("HERE");
		for(WaitingCustomer wc : waitingCustomers){
			if(wc.state == State.waiting){
				for(MyTeller mt : tellers){
					if(mt.state == TellerState.idle){
						assignTeller(mt, wc);
						return true;
					}
				}
			}
		}
		for(WaitingCustomer wc : waitingCustomers){
			if(wc.state == State.entered){
				for(MyTeller mt : tellers){
					if(mt.state == TellerState.idle){
						assignTeller(mt, wc);
						return true;
					}
				}
				tellerBusy(wc);
				return true;
			}
		}
		
		for(WaitingCustomer wc : waitingCustomers){
			if(wc.state == State.leaving){
				updatePersonInfo(wc);
				return true;
			}
		}
		
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions
	private void assignTeller(MyTeller mt, WaitingCustomer wc){	
		wc.bankCustomer.msgGoToTeller(mt.teller);
		print(wc.toString() + " " + wc.action);
		if(wc.action == Action.newAccount){
			wc.bankCustomer.msgRequestNewAccount(wc.requestAmt);
		}
		else if(wc.action == Action.deposit){
			wc.bankCustomer.msgRequestDeposit(wc.requestAmt);
		}
		else if(wc.action == Action.withdraw){
			print("RA: "+wc.requestAmt);
			wc.bankCustomer.msgRequestWithdraw(wc.requestAmt);
		}
		else if(wc.action == Action.loan){
			wc.bankCustomer.msgRequestLoan(wc.requestAmt);
		}
		//wc.person.msgGoToTeller(mt.teller);
		wc.state = State.busy;
		mt.teller.msgNewCustomer(wc.bankCustomer);
		mt.state = TellerState.busy;
	}
	
	private void tellerBusy(WaitingCustomer wc){
		//wc.person.msgWait();
		wc.state = State.waiting;
	}
	
	private void updatePersonInfo(WaitingCustomer wc){
		BankCustomer bc = wc.bankCustomer;
		print(bc.getAccountNum() + " $" + bc.getBalance());
		wc.state = State.idle;
		wc.person.msgLeave(bc.getAccountNum(), bc.getBalance());
	}
	
	public Person getPerson(BankCustomer bc){
		Person person = null;
		for(WaitingCustomer wc : waitingCustomers){
			if(wc.bankCustomer == bc){
				person = wc.person; break;
			}
		}
		return person;
	}
	
	public void addTeller(TellerAgent t){
		MyTeller mt = new MyTeller(t);
		tellers.add(mt); //?
		//tellers.add(new MyTeller(t));
		stateChanged();
	}
	
	public BankGui getGui(){
	  return bankGui;
	}
	
}

