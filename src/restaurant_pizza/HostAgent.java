package restaurant_pizza;

import agent_pizza.Agent;

import java.util.*;

import restaurant_pizza.interfaces.Customer;
import restaurant_pizza.interfaces.Waiter;
import simcity.PersonAgent;
import simcity.interfaces.Person;

public class HostAgent extends Agent {
	
	// ***** DATA *****
	
	public List<MyCustomer> waitingCustomers =
			Collections.synchronizedList(new ArrayList<MyCustomer>());
	public List<MyWaiter> waiters =
			Collections.synchronizedList(new LinkedList<MyWaiter>());
	public List<Table> tables = 
			Collections.synchronizedList(new ArrayList<Table>(NTABLES));

	public static final int NTABLES = 3;
	public enum CustomerState {Waiting, WaitingAndNotified, InRestaurant};
	public enum WaiterState {Working, WorkingAndWantBreak, OnBreak};
	Person person;
	boolean shiftDone = false;
	private int waiterPointer = 0;
	private String name;
	
	public HostAgent(String name) {
		super();
		this.name = name;
		for (int ix = 0; ix < NTABLES; ix++) { // make some tables
			tables.add(new Table(ix)); //how you add to a collections
		}
	}
	
	public String getName() {
		return name;
	}

	public List<CustomerAgent> getWaitingCustomers() {
		List<CustomerAgent> output = new LinkedList<CustomerAgent>();
		synchronized (waitingCustomers) {
			for(MyCustomer mc : waitingCustomers)
				output.add(mc.cust);
			return output;
		}
	}

	public List<WaiterAgent> getWaiters() {
		List<WaiterAgent> output = new LinkedList<WaiterAgent>();
		synchronized (waiters) {
			for(MyWaiter mw : waiters)
				output.add(mw.waiter);
			return output;
		}
	}
	
	public Collection<Table> getTables() {
		return tables;
	}
	
	public void setPerson(Person p) {
		person = p;
	}
	
	// ***** MESSAGES *****
	
	public void msgAddWaiter(WaiterAgent w) {
		print("calling msgAddWaiter");
		MyWaiter tempMyWaiter = new MyWaiter(w);
		waiters.add(tempMyWaiter); // adding waiter to list
		stateChanged();
	}
	
	public void msgShiftDone() {
		shiftDone = true;
		if (waitingCustomers.size() == 0) {person.msgStopWork(10);}
	}
	
	public void msgIWantFood(CustomerAgent cust) {
		print("calling msgIWantFood");
		waitingCustomers.add(new MyCustomer(cust, -1));// adding customer to list
		stateChanged();
	}
	
	public void msgCannotWait(Customer impatientCust) {
		synchronized (waitingCustomers) {
			for(int i = 0; i < waitingCustomers.size(); i++) {
				if(waitingCustomers.get(i).cust.equals(impatientCust)) {
					waitingCustomers.remove(i);
					break;
				}
			}
		}
		stateChanged();
	}

	public void msgTableIsFree(Waiter w, int tableNum) {
		// find table with this tableNum and clear occupant in memory
		MyWaiter waiter = null;
		synchronized (waiters) {
			for(MyWaiter mw : waiters)
				if(mw.waiter.equals(w))
				{
					waiter = mw;
				}
		}
		assert waiter != null;
		waiter.numCustomers++;
		Table t = null;
		synchronized (tables) {
			for(Table tempT : tables)
				if(tempT.tableNum == tableNum)
					t = tempT;
			t.setOccupant(null);
		}
		stateChanged();
	}
	
	public void msgWantToGoOnBreak(Waiter incomingWaiter) {
		synchronized (waiters) {
			for(MyWaiter mw : waiters) {
				if(mw.waiter.equals(incomingWaiter)) { 
					mw.state = WaiterState.WorkingAndWantBreak;
					break;
				}
			}
		}
		stateChanged();
	}

	public void msgDoneWithBreak(WaiterAgent incomingWaiter) {
		print("msgDoneWithBreak from " + incomingWaiter.getName());
		synchronized (waiters) {
			for(MyWaiter mw : waiters)
				if(mw.waiter.equals(incomingWaiter)) {
					mw.state = WaiterState.Working;
					break;
				}
		}
		stateChanged();
	}
	
	// ***** SCHEDULER *****
	protected boolean pickAndExecuteAnAction() {
		if (waitingCustomers.size() == 0 && shiftDone == true) {person.msgStopWork(10);} 
		if(waiters.isEmpty())
			return false;
		
		// check if working waiter exists (not a working waiter wanting break)
		boolean workingWaiterExists = false;
		synchronized (waiters) {
			for(MyWaiter mw : waiters)
				if(mw.state == WaiterState.Working) {
					workingWaiterExists = true;
					break;
				}
		}
		// if there is no purely-working waiter, next guy who wants break doesn't get it.
		synchronized (waiters) {
			for(int i = 0; i < waiters.size(); i++) {
				MyWaiter mw = waiters.get(i);
	 			if(mw.state == WaiterState.WorkingAndWantBreak) {
					if(!workingWaiterExists) {
						mw.state = WaiterState.Working;
						denyBreak(mw);
						workingWaiterExists = true;
					}
					else {
						mw.state = WaiterState.OnBreak;
						approveBreak(mw);
						return true;
					}
				}
			}
		}
			
		boolean allFull = allTablesFull();
		// loop through all waiting customers and tables
		synchronized (waitingCustomers) {
			
			for(int mcIndex = 0; mcIndex < waitingCustomers.size(); mcIndex++) {
				MyCustomer c = waitingCustomers.get(mcIndex);
				if(c.state == CustomerState.Waiting && allFull) {
					c.state = CustomerState.WaitingAndNotified;
					c.cust.msgMustWait();
					return true;
				}
				
				synchronized (tables) {
				
					for(int tableIndex = 0; tableIndex < tables.size(); tableIndex++) {
						Table t = tables.get(tableIndex);
						if(!t.isOccupied()) {
							// We have a customer & un-occupied table; find waiter who will seat the customer
							MyWaiter chosenWaiter = null;
							
							// find the next waiter not on break
							while(waiters.get(waiterPointer).state == WaiterState.OnBreak)
								waiterPointer = (waiterPointer + 1) % (waiters.size());
							
							chosenWaiter = waiters.get(waiterPointer);
							if(chosenWaiter == null)
								return false;

							//this is done to balance out the waiter load
							waiterPointer = (waiterPointer + 1) % (waiters.size());
		
							//assign the waiter
							chosenWaiter.numCustomers++;
							WaiterAgent freeWaiter = chosenWaiter.waiter;
							assignWaiter(t, c, freeWaiter);
							c.state = CustomerState.InRestaurant;
							break;
						}
					}
				}
			}
		}
		return false;
	}

	// ***** ACTIONS *****
	public void assignWaiter(Table t, MyCustomer mc, WaiterAgent freeWaiter) {
		t.setOccupant(mc.cust);
		print("Telling waiter to seat customer " + mc.cust.getName());
		freeWaiter.msgSeatCustomer(mc.cust, t.tableNum);
		waitingCustomers.remove(mc);
	}
	
	public void approveBreak(MyWaiter mw) {
		print("Approving break for " + mw.waiter.getName());
		mw.waiter.msgApproveBreak();
	}
	
	public void denyBreak(MyWaiter mw) {
		print("Denying break for " + mw.waiter.getName());
		mw.waiter.msgDenyBreak();
	}
	
	//utilities

	private class MyWaiter {
		WaiterAgent waiter;
		int numCustomers = 0;
		private WaiterState state; //The start state
		
		public MyWaiter(WaiterAgent aWaiterAgent) {
			waiter = aWaiterAgent;
			state = WaiterState.Working;
		}
		
		public void setState(WaiterState state) {
			this.state = state;
		}
	}
	
	private boolean allTablesFull() {
		boolean allTablesFull = true;
		synchronized (tables) {
			for(Table t : tables) {
				if(!t.isOccupied()) {
					allTablesFull = false;
					break;
				}
			}
		}
		return allTablesFull;
	}
	
	private class MyCustomer {
		CustomerAgent cust;
		int table;
		CustomerState state;
		
		public MyCustomer(CustomerAgent aCustomerAgent, int aTable) {
			cust = aCustomerAgent;
			table = aTable;
			state = CustomerState.Waiting;
		}
	}
	
	public class Table {
		Customer occupiedBy;
		int tableNum;
		
		public Table(int aTableNum)
		{
			tableNum = aTableNum;
		}

		void setOccupant(Customer cust) {
			occupiedBy = cust;
		}

		void setUnoccupied() {
			occupiedBy = null;
		}

		Customer getOccupant() {
			return occupiedBy;
		}

		boolean isOccupied() {
			return occupiedBy != null;
		}
	}
}
