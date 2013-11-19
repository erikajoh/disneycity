package bank;

import agent.Agent;
import bank.interfaces.Bank;
import bank.interfaces.Teller;
import bank.interfaces.Person;
import bank.gui.BankGui;

import java.util.*;

/**
 * bank Host Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the HostAgent. A Host is the manager of a bank who sees that all
//is proceeded as he wishes.
public class BankAgent extends Agent implements Bank {
	   class WaitingCustomer {
		 Person person;
		 State state;
		 Action action;
		 int accountNum;
		 double requestAmt;
		 public WaitingCustomer(Person p, int accNum, double ra, Action a){
			 action = a;
			 accountNum = accNum;
			 person = p;
			 requestAmt = ra;
			 state = State.waiting;
		 }
	   }

		List<WaitingCustomer> waitingCustomers = Collections.synchronizedList(new ArrayList<WaitingCustomer>());;
		enum State{entered, waiting, leaving, busy};
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
	
	public void msgTellerFree(Teller teller){
		for(MyTeller t : tellers){
			if(t.teller == teller){
				t.state = TellerState.idle;
			}
		}
		stateChanged();
	}
	
	public void msgRequestAccount(double amount, Person person){
		waitingCustomers.add(new WaitingCustomer(person, -1, amount, Action.newAccount));
		stateChanged();
	}
	
	public void msgRequestDeposit(int accountNumber, double amount, Person person, boolean forLoan){
	   if(forLoan == false){
		  waitingCustomers.add(new WaitingCustomer(person, accountNumber, amount, Action.deposit));
	   }
	   else {
		  waitingCustomers.add(new WaitingCustomer(person, accountNumber, amount, Action.loan));  
	   }
		stateChanged();
	}
	
	public void msgRequestWithdrawal(int accountNumber, double amount, Person person){
		  waitingCustomers.add(new WaitingCustomer(person, accountNumber, amount, Action.withdraw));
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
		
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions
	private void assignTeller(MyTeller mt, WaitingCustomer wc){
		BankCustomerAgent bca = new BankCustomerAgent(wc.person.getName(), wc.accountNum, this, bankGui);
		if(wc.action == Action.newAccount){
			bca.msgRequestNewAccount(wc.requestAmt);
		}
		else if(wc.action == Action.deposit){
			bca.msgRequestDeposit(wc.requestAmt);
		}
		else if(wc.action == Action.withdraw){
			bca.msgRequestWithdraw(wc.requestAmt);
		}
		else if(wc.action == Action.loan){
			bca.msgRequestLoan(wc.requestAmt);
		}
		//wc.person.msgGoToTeller(mt.teller);
		wc.state = State.busy;
		mt.teller.msgNewCustomer(bca);
		mt.state = TellerState.busy;
	}
	private void tellerBusy(WaitingCustomer wc){
		//wc.person.msgWait();
		wc.state = State.waiting;
	}
	
	public void addTeller(TellerAgent t){
		MyTeller mt = new MyTeller(t);
		tellers.add(mt); //?
		//tellers.add(new MyTeller(t));
		stateChanged();
	}

}

