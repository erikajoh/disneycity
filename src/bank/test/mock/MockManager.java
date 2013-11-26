package bank.test.mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import simcity.gui.SimCityGui;
import bank.TellerAgent;
import bank.gui.Bank;
import bank.interfaces.BankCustomer;
import bank.interfaces.Manager;
import bank.interfaces.Teller;

public class MockManager extends Mock {
	
	  public EventLog log;
	  public class WaitingCustomer {
			 MockBankCustomer bankCustomer;
			 State state;
			 Action action;
			 private int accountNum;
			 private double requestAmt;
			 public WaitingCustomer(MockBankCustomer bc){
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

			class MyTeller {
				MockTeller teller;
				TellerState state;
				public MyTeller(MockTeller t){
					teller = t;
					state = TellerState.idle;
				}
			}
			enum TellerState{idle, busy};

			public List<MyTeller> tellers = Collections.synchronizedList(new ArrayList<MyTeller>());
			
			private MockBank bank;
			
			private SimCityGui simCityGui;

			private String name;


		public MockManager(String name, MockBank b, SimCityGui bg) {
			super(name);
			log = new EventLog();
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
		
		public void msgTellerFree(MockTeller teller, MockBankCustomer bankCustomer){
			log.add(new LoggedEvent("TELLER FREE"));
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
		}
		
		public void msgCustomerHere(MockBankCustomer bca){
			boolean found = false;
			for(WaitingCustomer wc : waitingCustomers){
				if(wc.bankCustomer == bca){
			       wc.state = State.entered;
			       found = true; break;
			    }
			}
			if(found == false){
			    waitingCustomers.add(new WaitingCustomer(bca));
			}
		}

		public void msgRequestAccount(MockBankCustomer bc, double amount){
			log.add(new LoggedEvent("New Bank Customer"));
			for(WaitingCustomer wc : waitingCustomers){
				if(wc.bankCustomer == bc){
					wc.setAccountNum(-1);
					wc.setRequestAmt(amount);
					wc.action = Action.newAccount;
					break;
				}
			}
		}
		
		public void msgRequestDeposit(MockBankCustomer bc, int accountNumber, double amount){
			WaitingCustomer waitingCustomer = null;
			for(WaitingCustomer wc : waitingCustomers){
				if(wc.bankCustomer == bc){
					waitingCustomer = wc;
					wc.setAccountNum(accountNumber);
					wc.setRequestAmt(amount);
					break;
				}
			}
			waitingCustomer.action = Action.deposit;
		}
		
		public void msgRequestWithdrawal(MockBankCustomer bc, int accountNumber, double amount){
			for(WaitingCustomer wc : waitingCustomers){
				if(wc.bankCustomer == bc){
					wc.setAccountNum(accountNumber);
					wc.setRequestAmt(amount);
					wc.action = Action.withdraw;
					break;
				}
			}
		}
		

		/**
		 * Scheduler.  Determine what action is called for, and do it.
		 */
		public boolean pickAndExecuteAnAction() {
		
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
					log.add(new LoggedEvent("LEAVING"));
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
			
			if(wc.action == Action.newAccount){
				log.add(new LoggedEvent("ASSIGNING AND NEW ACCOUNT" + wc.toString() + " " + wc.action));
				wc.bankCustomer.msgRequestNewAccount(wc.requestAmt);
			}
			else if(wc.action == Action.deposit){
				wc.bankCustomer.msgRequestDeposit(wc.requestAmt, wc.accountNum);
			}
			else if(wc.action == Action.withdraw){
				log.add(new LoggedEvent("RA: "+wc.requestAmt));
				wc.bankCustomer.msgRequestWithdraw(wc.requestAmt, wc.accountNum);
			}

			wc.state = State.busy;
			mt.teller.msgNewCustomer(wc.bankCustomer);
			mt.state = TellerState.busy;
		}
		
		private void tellerBusy(WaitingCustomer wc){
			wc.state = State.waiting;
		}
		
		private void updatePersonInfo(WaitingCustomer wc){
			MockBankCustomer bc = wc.bankCustomer;
			log.add(new LoggedEvent(bc.getAccountNum() + " $" + bc.getBalance()));
			wc.state = State.idle;
			bank.msgLeave(wc.bankCustomer, bc.getAccountNum(), bc.getChange(), bc.getLoanAmount(), bc.getLoanTime());
		}
		
		
		public void addTeller(MockTeller t){
			MyTeller mt = new MyTeller(t);
			tellers.add(mt);
		}
		
		public SimCityGui getGui(){
		  return simCityGui;
		}
		
}

