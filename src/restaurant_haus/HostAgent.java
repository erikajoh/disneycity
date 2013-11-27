package restaurant_haus;

import agent_haus.Agent;
import simcity.PersonAgent;

import java.awt.Point;
import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Restaurant Host Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the HostAgent. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class HostAgent extends Agent {
	static final int NTABLES = 3;//a global for the number of tables.
	static final int columns = 3;//number of columns in restruant gui
	//Notice that we implement waitingCustomers using ArrayList, but type it
	//with List semantics.
	public List<MyCustomer> waitingCustomers = Collections.synchronizedList(new ArrayList<MyCustomer>());
	public List<MyWaiter> waiters = Collections.synchronizedList(new ArrayList<MyWaiter>());
	public List<Table> tables;
	//note that tables is typed with Collection semantics.
	//Later we will see how it is implemented
	public PersonAgent person;
	private String name;

	private Menu m = new Menu();


	public HostAgent(String name) {
		super();

		this.name = name;
		// make some tables
		tables = Collections.synchronizedList(new ArrayList<Table>(NTABLES));
		synchronized(tables) {
			for (int ix = 1; ix <= NTABLES; ix++) {
				Table temp = new Table(ix);
				temp.calculatePosition(columns);

				tables.add(temp);//how you add to a collections
			}
		}
	}

	public String getMaitreDName() {
		return name;
	}

	public void setPerson(PersonAgent p) {
		person = p;
	}
	public String getName() {
		return name;
	}

	public List getWaitingCustomers() {
		return waitingCustomers;
	}

	public Collection getTables() {
		return tables;
	}
	// Messages

	public void msgGiveJob(WaiterAgent waiter) {
		MyWaiter tempWaiter = new MyWaiter(waiter);
		waiters.add(tempWaiter);
		waiter.setHome(100 + 25*(waiters.indexOf(tempWaiter)%7),150 + 25*(waiters.indexOf(tempWaiter)/7));
		waiter.giveMenu(m);
		print(waiter.getName() + " started working as a waiter.");
		stateChanged();
	}

	public void msgImHungry(CustomerAgent cust) {
		waitingCustomers.add(new MyCustomer(cust));
		stateChanged();
	}

	public void msgTableEmpty(CustomerAgent c) {
		synchronized(waitingCustomers) {
			for (MyCustomer mc : waitingCustomers) {
				if(mc.c == c) {
					mc.s = CustomerState.Leaving;
				}
			}
		}
		stateChanged();
	}

	public void msgIWantABreak(WaiterAgent w) {
		synchronized(waiters) {
			for (MyWaiter mw : waiters) {
				if(mw.w == w) {
					mw.s = WaiterState.WantBreak;
					print("You want a break already?");
				}
			}
		}
		stateChanged();
	}

	public void msgBreakOver(WaiterAgent w) {
		synchronized(waiters) {
			for (MyWaiter mw : waiters) {
				if(mw.w == w) {
					mw.s = WaiterState.Working;
				}
			}
		}
		stateChanged();
	}

	public void msgImLeaving(CustomerAgent c) {
		synchronized(waitingCustomers) {
			for(MyCustomer mc : waitingCustomers) {
				if(mc.c == c) {
					waitingCustomers.remove(mc);
					stateChanged();
					return;
				}
			}
		}
	}

	public void msgIWantASeat(CustomerAgent c) {
		synchronized(waitingCustomers) {
			for(MyCustomer mc : waitingCustomers) {
				if(mc.c == c) {
					mc.s = CustomerState.Waiting;
				}
			}
		}
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		if(isPaused) {
			try {
				pauseSem.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		/* Think of this next rule as:
            Does there exist a table and customer,
            so that table is unoccupied and customer is waiting.
            If so seat  him at the table.
		 */
		synchronized(waitingCustomers) {
			for(MyCustomer mc : waitingCustomers) {
				if(mc.s == CustomerState.Leaving) {
					for(Table table : tables) {
						if(table.getOccupant() == mc.c) {
							table.setUnoccupied();
							waitingCustomers.remove(mc);
							return true;
						}
					}
				}
			}
		}

		synchronized(waitingCustomers) {
			for(MyCustomer mc : waitingCustomers) {
				if(mc.s == CustomerState.Entered) {
					giveMenu(mc);
					return true;
				}
			}
		}

		synchronized(waiters) {
			for(MyWaiter mw : waiters) {
				if(mw.s == WaiterState.WantBreak) {
					//Count waiters and make sure the waiter can take a break
					int workingWaiters = 0;
					for(MyWaiter mw2 : waiters ) {
						if(mw2.s != WaiterState.Break) {
							workingWaiters++;
						}
					}
					if(workingWaiters > 1) {
						giveBreak(mw);
					}
					else {
						denyBreak(mw);
					}
				}
			}
		}

		synchronized(tables) {
			for (Table table : tables) {
				if (!table.isOccupied()) {
					if (!waitingCustomers.isEmpty()) {
						synchronized(waitingCustomers) {
							for(MyCustomer mc : waitingCustomers) {
								if(mc.getCustomerState() == CustomerState.Waiting) {
									if(!waiters.isEmpty()) {
										WaiterAgent temporaryWaiter = waiters.get(0).w;
										synchronized(waiters) {
											for(MyWaiter waiter : waiters) {
												if(waiter.w.getNumberOfCustomers() <= temporaryWaiter.getNumberOfCustomers() && waiter.s != WaiterState.Break) {
													temporaryWaiter = waiter.w;
												}
											}
										}
										assignWaiter(mc.getCustomer(), table, temporaryWaiter);//the action
										print("Waiter, " + temporaryWaiter.getName() + ", please seat customer, " + waitingCustomers.get(0).getCustomer().getName());
										mc.setState(CustomerState.Seated);
										return true;//return true to the abstract agent to reinvoke the scheduler.
									}
								}
							}
						}
					}
				}
			}
		}

		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions
	/*Deprecated v1 code
	private void seatCustomer(CustomerAgent customer, Table table) {
		customer.msgSitAtTable(table.xPos, table.yPos);
		DoSeatCustomer(customer, table);
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		table.setOccupant(customer);
		waitingCustomers.remove(customer);
		hostGui.DoLeaveCustomer();
	}


	// The animation DoXYZ() routines
	private void DoSeatCustomer(CustomerAgent customer, Table table) {
		//Notice how we print "customer" directly. It's toString method will do it.
		//Same with "table"
		print("Seating " + customer + " at " + table);
		hostGui.DoBringToTable(customer, table.xPos, table.yPos); 

	}
	 */

	private void giveBreak(MyWaiter mw) {
		mw.s = WaiterState.Break;
		print("Fine, have a break");
		mw.w.msgTakeBreak();
	}

	private void denyBreak(MyWaiter mw) {
		print("No break right now, ask later.");
		mw.s = WaiterState.Working;
		mw.w.msgNoBreak();
	}

	private void giveMenu(MyCustomer mc) {
		boolean emptyTable = false;
		synchronized(tables) {
			for(Table table : tables) {
				if(!table.isOccupied()) {
					emptyTable = true;
					break;
				}
			}
		}

		print("Welcome to the Restaurant!");
		if(!emptyTable) {
			print("Unfortunately there's no table availible at the current moment");
		}
		mc.c.msgHereIsMenu(m, emptyTable);

		mc.s = CustomerState.Greeted;
	}

	private void assignWaiter(CustomerAgent c, Table t, WaiterAgent w) {
		synchronized(waitingCustomers) {
			for(MyCustomer mc : waitingCustomers) {
				if(mc.getCustomer() == c) {
					mc.setState(CustomerState.Seated);
				}
			}
		}
		t.setOccupant(c);
		w.msgPleaseSeatCustomer(c, t.getTableNumber());
	}

	//utilities

	public Point getTableCoordinates(int tableNumber) {
		synchronized(tables) {
			for(Table table : tables) {
				if(table.tableNumber == tableNumber) {
					return (new Point(table.xPos, table.yPos));
				}
			}
		}
		return null;
	}

	public Menu getMenu() {
		return m;
	}

	/*
	public void setGui(WaiterGui gui) {
		hostGui = gui;
	}

	public WaiterGui getGui() {
		return hostGui;
	}
	 */

	private class MyCustomer {
		CustomerAgent c;
		CustomerState s;

		MyCustomer(CustomerAgent c) {
			this.c = c;
			s = CustomerState.Entered;
		}

		private CustomerAgent getCustomer() {
			return c;
		}

		private void setState(CustomerState s) {
			this.s = s;
		}

		private CustomerState getCustomerState() {
			return s;
		}
	}
	enum CustomerState {Waiting, Seated, Leaving, Greeted, Entered};

	private class MyWaiter {
		WaiterAgent w;
		WaiterState s;

		MyWaiter(WaiterAgent w) {
			this.w = w;
			s = WaiterState.Working;
		}
	}

	enum WaiterState {Working, WantBreak, Break};

	private class Table {
		CustomerAgent occupiedBy = null;
		int tableNumber;
		int xPos;
		int yPos;

		Table(int tableNumber) {
			this.tableNumber = tableNumber;

			calculatePosition(2);
		}

		Table(int tablenumber, int xPos, int yPos) {
			this.tableNumber = tableNumber;
			this.xPos = xPos;
			this.yPos = yPos;
		}

		void setPosition(int xPos, int yPos) {
			this.xPos = xPos;
			this.yPos = yPos;
		}

		void setOccupant(CustomerAgent cust) {
			occupiedBy = cust;
		}

		void setUnoccupied() {
			occupiedBy = null;
		}

		CustomerAgent getOccupant() {
			return occupiedBy;
		}

		boolean isOccupied() {
			return occupiedBy != null;
		}

		public String toString() {
			return "table " + tableNumber;
		}

		void calculatePosition(int columns) {//Determines the table's position based on its table number. Formats into rows and columns
			int xOffset = (this.tableNumber-1)%columns;
			int yOffset = (this.tableNumber-1)/columns;

			xPos = 53 + 83*xOffset;
			yPos = 228;
		}

		int getX() {
			return xPos;
		}

		int getY() {
			return yPos;
		}

		int getTableNumber() {
			return tableNumber;
		}
	}
}
