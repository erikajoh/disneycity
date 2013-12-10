package restaurant_haus;

import agent_haus.Agent;
import restaurant_haus.gui.Gui;
import restaurant_haus.gui.WaiterGui;
import restaurant_haus.interfaces.*;
import simcity.PersonAgent;
import simcity.interfaces.Person;

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
public class WaiterAgent extends Agent implements Waiter{
	//Notice that we implement waitingCustomers using ArrayList, but type it
	//with List semantics.
	public List<MyCustomer> customers = new ArrayList<MyCustomer>();
	//note that tables is typed with Collection semantics.
	//Later we will see how it is implemented

	private String name;
	protected Semaphore atDestination = new Semaphore(0,true);

	private enum BreakState {Working, WantBreak, Asked, OnBreak, TakeBreak, DeniedBreak};
	BreakState breakState = BreakState.Working;

	public WaiterGui waiterGui = null;
	public CookAgent cook;
	public Menu m;
	public HostAgent h = null;
	public CashierAgent cashier;
	public Person person;
	boolean shiftDone = false;
	

	Timer breakTimer = new Timer();

	public WaiterAgent(String name) {
		super();

		this.name = name;
		// make some tables
	}

	public void setHost(HostAgent h) {
		this.h = h;
	}
	
	public void setPerson(Person p) {
		person = p;
	}

	public void setCashier(CashierAgent cashier) {
		this.cashier = cashier;
	}

	public void giveMenu(Menu m) {
		this.m = m;
	}

	public void setCook(CookAgent cook) {
		this.cook = cook;
	}

	public String getMaitreDName() {
		return name;
	}

	public String getName() {
		return name;
	}

	public List getCustomers() {
		return customers;
	}

	public int getNumberOfCustomers() {
		return customers.size();
	}

	// Messages
	
	public void msgShiftDone() {
		shiftDone = true;
		if (customers.size() == 0) {
			print ("going home!");
			waiterGui.DoLeave(person);
			if (cook!=null) { 
				cook.msgShiftDone(); 
				if (cashier!=null) cashier.subtract(10); 
			}
			//if (host!=null) {  
			//	if (cashier!=null) cashier.subtract(10); 
			//}
			if (cashier!=null) { 
				cashier.msgShiftDone(); 
				cashier.subtract(20);
			}
		}
		else {
			print("my shift is done! but I still have customers");
		}
	}

	public void msgPleaseSeatCustomer(CustomerAgent c, int table) {
		customers.add(new MyCustomer(c, table));
		stateChanged();
	}

	public void msgReadyToOrder(CustomerAgent c) {
		try {
			for(MyCustomer mc : customers) {
				if(mc.c == c) {
					mc.s = CustomerState.ReadyToOrder;
				}
			}
		}
		catch (ConcurrentModificationException e) {
			e.printStackTrace();
		}
		stateChanged();
		/*
		For(MyCustomer mc : customers) {
			If(mc.getCustomer() = c) {
				Mc.setState(ReadyToOrder);
			}
		}*/
	}

	public void msgMyOrderIs(String choice, CustomerAgent c) {
		try {
			for(MyCustomer mc : customers) {
				if(mc.c == c) {
					mc.choice = choice;
					mc.s = CustomerState.Ordered;
				}
			}
		}
		catch (ConcurrentModificationException e) {
			e.printStackTrace();
		}
		stateChanged();
	}

	public void msgFoodReady(int table) {
		try {
			for(MyCustomer mc : customers) {
				if(mc.table == table) {
					mc.s = CustomerState.FoodReady;
				}
			}
		}
		catch (ConcurrentModificationException e) {
			e.printStackTrace();
		}
		stateChanged();
		/*
		For(MyCustomer mc : customers) {
			If(mc.getTable() = table) {
				Mc.setState(FoodReady);
			}
		}*/
	}

	public void msgDoneEating(CustomerAgent c) {
		try {
			for(MyCustomer mc : customers) {
				if(mc.c == c) {
					mc.s = CustomerState.Leaving;
				}
			}
		}
		catch (ConcurrentModificationException e) {
			e.printStackTrace();
		}
		stateChanged();
		/*
		For(MyCustomer mc : customers) {
			If(mc.getCustomer() = c) {
				Mc.setState(Leaving);
			}*/
	}

	public void msgOutOfFood(int table) {
		try {
			for(MyCustomer mc : customers) {
				if(mc.table == table) {
					mc.s = CustomerState.needReorder;
				}
			}
		}
		catch (ConcurrentModificationException e) {
			e.printStackTrace();
		}
		stateChanged();
	}

	public void msgTakeBreak() {
		breakState = BreakState.TakeBreak;
		print("Oh boy, I got a break.");
		stateChanged();
	}

	public void msgNoBreak() {
		breakState = BreakState.DeniedBreak;
		print("No break? *grumble grumble grumble*");
		stateChanged();
	}

	public void msgCheckPlease(Customer c) {
		try {
			for(MyCustomer mc : customers) {
				if(mc.c == c) {
					mc.s = CustomerState.WaitingForCheck;
				}
			}
		}
		catch (ConcurrentModificationException e) {
			e.printStackTrace();
		}
		stateChanged();
	}

	public void msgHereIsCheck(Customer c, double price) {
		try {
			for(MyCustomer mc : customers) {
				if(mc.c == c) {
					mc.check = price;
					mc.s = CustomerState.DeliverCheck;
				}
			}
		}
		catch (ConcurrentModificationException e) {
			e.printStackTrace();
		}
		stateChanged();
	}

	public void msgWantBreak() {
		breakState = BreakState.WantBreak;
		print("Changing Break state");
		stateChanged();
	}

	public void msgAtDestination() {//from animation
		//print("msgAtTable() called");
		atDestination.release();// = true;
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
		try {
			for(MyCustomer mc : customers) {
				if(mc.s == CustomerState.Leaving) {
					EmptyTable(mc);
					return true;
				}
			}
		}
		catch (ConcurrentModificationException e) {
			e.printStackTrace();
			return false;
		}

		if(breakState == BreakState.DeniedBreak) {
			deniedBreak();
			return true;
		}
		if(breakState == BreakState.WantBreak) {
			askForBreak();
			return true;
		}

		if(customers.size() == 0 && breakState == BreakState.TakeBreak) {
			takeBreak();
			return true;
		}

		try {
			for(MyCustomer mc : customers) {
				if(mc.s == CustomerState.Ordered) {
					PlaceOrder(mc);
					return true;
				}
			}
		}
		catch (ConcurrentModificationException e) {
			e.printStackTrace();
			return false;
		}

		try {
			for(MyCustomer mc : customers) {
				if(mc.s == CustomerState.WaitingForCheck) {
					getCheck(mc);
					return true;
				}
			}
		}
		catch (ConcurrentModificationException e) {
			e.printStackTrace();
			return false;
		}

		try {
			for(MyCustomer mc : customers) {
				if(mc.s == CustomerState.DeliverCheck) {
					deliverCheck(mc);
					return true;
				}
			}
		}
		catch (ConcurrentModificationException e) {
			e.printStackTrace();
			return false;
		}

		try {
			for(MyCustomer mc : customers) {
				if(mc.s == CustomerState.needReorder) {
					RetakeOrder(mc);
					return true;
				}
			}
		}
		catch (ConcurrentModificationException e) {
			e.printStackTrace();
			return false;
		}

		try {
			for(MyCustomer mc : customers) {
				if(mc.s == CustomerState.FoodReady) {
					TakeFood(mc);
					return true;
				}
			}
		}
		catch (ConcurrentModificationException e) {
			e.printStackTrace();
			return false;
		}

		try {
			for(MyCustomer mc : customers) {
				if(mc.s == CustomerState.Waiting) {
					SeatCustomer(mc);
					return true;
				}
			}
		}
		catch (ConcurrentModificationException e) {
			e.printStackTrace();
			return false;
		}

		try {
			for(MyCustomer mc : customers) {
				if(mc.s == CustomerState.ReadyToOrder) {
					TakeOrder(mc);
					return true;
				}
			}
		}
		catch (ConcurrentModificationException e) {
			e.printStackTrace();
			return false;
		}
		
		GoHome();
		if (shiftDone) {msgShiftDone();}
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions

	public void EmptyTable(MyCustomer mc) {
		h.msgTableEmpty(mc.c);
		customers.remove(mc);
		if(customers.size() == 0) {
			print("I now have no customers.");
		}
	}

	public void TakeFood(MyCustomer mc) {
		waiterGui.GoToCook();
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		waiterGui.GoToTable(new Point(h.getTableCoordinates(mc.table).x, h.getTableCoordinates(mc.table).y + 40));
		cook.msgPickUpFood(this);
		waiterGui.CarryingFood(mc.choice);
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		print(mc.c.getName() + ", here is your order.");
		mc.c.msgFoodReceived("");
		mc.s = CustomerState.Eating;
		waiterGui.LeaveFood();
		//waiterGui.DoLeaveCustomer();
		//Hack for Reimu
		if(this.name.equals("Reimu")) {
			breakState = BreakState.WantBreak;
		}
		//GoToTable(mc.table);//animation
		//mc.setState(CustomerState.Eating);
	}

	public void SeatCustomer(MyCustomer mc) {
		//GoToTable(mc.table);//animation
		waiterGui.GoToCustomers();
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mc.getCustomer().msgSitAtTable(this, m, h.getTableCoordinates(mc.table));
		mc.s = CustomerState.Seated;
		waiterGui.GoToTable(h.getTableCoordinates(mc.table));
		print(mc.getCustomer().getName() + ", please follow me to your table");
		waiterGui.GoToTable(h.getTableCoordinates(mc.table));
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//waiterGui.DoLeaveCustomer();
	}

	public void TakeOrder(MyCustomer mc) {
		waiterGui.GoToTable(h.getTableCoordinates(mc.table));
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//waiterGui.DoLeaveCustomer();
		mc.getCustomer().msgWhatDoYouWant();
		print(mc.getCustomer().getName() + ", What do you want to eat?");
		mc.s = CustomerState.Ordered;
	};

	public void PlaceOrder(MyCustomer mc) {
		waiterGui.GoToOrder();
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cook.msgPlaceOrder(this, mc.choice, mc.table);
		print(cook.getName() + ", please cook " + mc.choice + " for table " + String.valueOf(mc.table) + ".");
		mc.s = CustomerState.Seated;
	}

	public void RetakeOrder(MyCustomer mc) {
		waiterGui.GoToTable(h.getTableCoordinates(mc.table));
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mc.getCustomer().msgPleaseReorder();
		//waiterGui.DoLeaveCustomer();
		print(mc.getCustomer().getName() + ", we're out of " + mc.choice + ". Would you like something else?");
		mc.s = CustomerState.Seated;
	}

	private void takeBreak() {
		waiterGui.GoToHome();
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		breakTimer.schedule(new TimerTask() {public void run() {breakOver();}}, 9000);
		print("I'm taking a break");
		breakState = BreakState.OnBreak;
	}

	private void breakOver() {
		breakState = BreakState.Working;
		waiterGui.msgBreakOver();
		h.msgBreakOver(this);
		print(h.getMaitreDName() + ", I'm working again.");
	}

	private void askForBreak() {
		print("Could I take a break now?");
		h.msgIWantABreak(this);
		breakState = BreakState.Asked;
	}

	private void getCheck(MyCustomer mc) {
		waiterGui.GoToCashier();
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		print("I need a check for " + mc.c.getName() + ". Their order was " + mc.choice + ".");
		mc.s = CustomerState.WaitingForCashier;
		cashier.msgNeedCheck(this, mc.choice, mc.c);
	}

	private void deliverCheck(MyCustomer mc) {
		waiterGui.GoToTable(h.getTableCoordinates(mc.table));
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//waiterGui.DoLeaveCustomer();
		print(mc.getCustomer().getName() + ", you owe " + String.valueOf(mc.check) + ". Go see the cashier.");
		mc.getCustomer().msgHereIsCheck(mc.check, cashier);
		mc.s = CustomerState.Paying;
	}

	private void deniedBreak() {
		waiterGui.msgBreakOver();
		breakState = BreakState.Working;
	}

	private void GoHome() {
		waiterGui.GoToHome();
	}
	/* Deprecated Actions
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
	 */

	// The animation DoXYZ() routines
	/*
	private void DoSeatCustomer(CustomerAgent customer, int table) {
		//Notice how we print "customer" directly. It's toString method will do it.
		//Same with "table"
		print("Seating " + customer + " at " + table);
		//Ask host for the table location
		//hostGui.DoBringToTable(customer, table.xPos, table.yPos); 
	}
	 */

	//utilities

	public boolean wantsBreak() {
		if(breakState != BreakState.Working) {
			return true;
		}
		return false;
	}

	public void setGui(WaiterGui gui) {
		waiterGui = gui;
	}

	public WaiterGui getGui() {
		return waiterGui;
	}

	public void setHome(int xHome, int yHome) {
		waiterGui.setHome(xHome, yHome);
	}

	class MyCustomer {
		CustomerAgent c;
		int table;
		String choice;
		CustomerState s;
		double check;

		MyCustomer(CustomerAgent c, int table) {
			this.c = c;
			this.table = table;
			s = CustomerState.Waiting;
		}

		public CustomerAgent getCustomer() {
			return c;
		}
	}

	enum CustomerState {Waiting, Seated, ReadyToOrder, Ordered, WaitingForFood,  FoodReady, Eating, Leaving, needReorder, WaitingForCheck, DeliverCheck, Paying, WaitingForCashier};

}