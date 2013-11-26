package bank;

import agent.Agent;
import bank.interfaces.Manager;
import bank.interfaces.Teller;
import bank.interfaces.BankCustomer;
import bank.gui.Account;
import bank.gui.Bank;
import simcity.gui.SimCityGui;

import java.util.*;

/**
 * bank Host Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the HostAgent. A Host is the manager of a bank who sees that all
//is proceeded as he wishes.
public class ManagerAgent extends Agent implements Manager {
	   public class WaitingCustomer {
		 BankCustomer bankCustomer;
		 State state;
		 Action action;
		 private int accountNum;
		 private double requestAmt;
		 public WaitingCustomer(BankCustomer bc){
			 bankCustomer = bc;
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
		enum Action{newAccount, deposit, withdraw};
		
		List<Account> accounts = Collections.synchronizedList(new ArrayList<Account>());

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
		
		private Bank bank;
		
		private SimCityGui simCityGui;

		private String name;


	public ManagerAgent(String name, Bank b, SimCityGui bg) {
		super();
		bank = b;
		simCityGui = bg;
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
		synchronized(tellers){
		    for(MyTeller t : tellers){
			   if(t.teller == teller){
				   t.state = TellerState.idle; break;
			    }
		    }
		}
		synchronized(waitingCustomers){
		    for(WaitingCustomer wc : waitingCustomers){
			    if(wc.bankCustomer == bankCustomer){
				    wc.state = State.leaving; break;
			    }
		    }
		}
		stateChanged();
	}
	
	public void msgCustomerHere(BankCustomer bca){
		boolean found = false;
		synchronized(waitingCustomers){
		    for(WaitingCustomer wc : waitingCustomers){
			    if(wc.bankCustomer == bca){
		           wc.state = State.entered;
		           found = true; break;
		        }
		    }
		}
		if(found == false){
		    waitingCustomers.add(new WaitingCustomer(bca));
		}
	}

	public void msgRequestAccount(BankCustomer bc, double amount){
		synchronized(waitingCustomers){
			print("msgRequestAccount");
		   for(WaitingCustomer wc : waitingCustomers){
			   if(wc.bankCustomer == bc){
				   wc.setAccountNum(-1);
				   wc.setRequestAmt(amount);
				   wc.action = Action.newAccount;
				   break;
			   }
		   }
		}
		stateChanged();
	}
	
	public void msgRequestDeposit(BankCustomer bc, int accountNumber, double amount){
		WaitingCustomer waitingCustomer = null;
		synchronized(waitingCustomers){
		   for(WaitingCustomer wc : waitingCustomers){
			   if(wc.bankCustomer == bc){
				   waitingCustomer = wc;
				   wc.setAccountNum(accountNumber);
				   wc.setRequestAmt(amount);
				   break;
			   }
			}
		}
		waitingCustomer.action = Action.deposit;
		stateChanged();
	}
	
	public void msgRequestWithdrawal(BankCustomer bc, int accountNumber, double amount){
		synchronized(waitingCustomers){
		    for(WaitingCustomer wc : waitingCustomers){
			   if(wc.bankCustomer == bc){
				   wc.setAccountNum(accountNumber);
				   wc.setRequestAmt(amount);
				   wc.action = Action.withdraw;
				   break;
			   }
		    }
		}
		  stateChanged();
	}


	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		synchronized(waitingCustomers){
		   for(WaitingCustomer wc : waitingCustomers){
			   if(wc.state == State.waiting){
				   synchronized(tellers){
				      for(MyTeller mt : tellers){
					      if(mt.state == TellerState.idle){
					       	assignTeller(mt, wc);
						    return true;
					       }
				      }
				   }
				}
			}
		}
		synchronized(waitingCustomers){
		   for(WaitingCustomer wc : waitingCustomers){
			   if(wc.state == State.entered){
					synchronized(tellers){
				       for(MyTeller mt : tellers){
					      if(mt.state == TellerState.idle){
						       assignTeller(mt, wc);
						       return true;
					       }
					    }
				     }
			   }
				tellerBusy(wc);
				return true;
			}
		}
		synchronized(waitingCustomers){
		    for(WaitingCustomer wc : waitingCustomers){
			   if(wc.state == State.leaving){
				   updatePersonInfo(wc);
				   return true;
			   }
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
			wc.bankCustomer.msgRequestDeposit(wc.requestAmt, wc.accountNum);
		}
		else if(wc.action == Action.withdraw){
			print("RA: "+wc.requestAmt);
			wc.bankCustomer.msgRequestWithdraw(wc.requestAmt, wc.accountNum);
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
		bank.msgLeave(wc.bankCustomer, bc.getAccountNum(), bc.getChange(), bc.getLoanAmount(), bc.getLoanTime());
	}
	
	public void addAccount(Account acc){
		accounts.add(acc);
	}
	
	public List<Account> getAccounts(){
		return accounts;
	}
	
	
	public void addTeller(TellerAgent t){
		MyTeller mt = new MyTeller(t);
		tellers.add(mt); //?
		//tellers.add(new MyTeller(t));
		stateChanged();
	}
	
	public SimCityGui getGui(){
	  return simCityGui;
	}
	
}

