package restaurant_rancho;

import agent_rancho.Agent;
import restaurant_rancho.Table;
import restaurant_rancho.gui.RestaurantRanchoGui;
import restaurant_rancho.gui.WaiterGui;
import restaurant_rancho.interfaces.Person;

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
	private List<MyCustomer> waitingCustomers;
	private List<MyWaiter> waiters;
	private Collection<Table> tables;
	private String name;
	private enum waiterState {working, onBreak};
	Person person;

	public HostAgent(String name) {
		super();

		this.name = name;
		waitingCustomers = Collections.synchronizedList(new ArrayList<MyCustomer>());
		waiters = Collections.synchronizedList(new ArrayList<MyWaiter>());
		tables = Collections.synchronizedList(new ArrayList<Table>(NTABLES));
		synchronized(tables) {
			for (int ix = 1; ix <= NTABLES; ix++) {
				tables.add(new Table(ix));//how you add to a collections
			}
		}
	
	}
	
	public void setPerson(Person p) {
		person = p;
	}
	
	public void addWaiter(WaiterAgent waiter) {
		MyWaiter w = new MyWaiter(waiter, waiterState.working);
		waiters.add(w);
		stateChanged();
	}

	public String getMaitreDName() {
		return name;
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
	public void msgIWantFood(CustomerAgent cust, int num) {
		waitingCustomers.add(new MyCustomer(cust, num));
		stateChanged();
		
	}
	
	public void msgWantBreak(WaiterAgent w) {
		if (waiters.size() !=1 && numWaitersWorking()>1 ){
			MyWaiter mw = findWaiter(w);
			mw.ws = waiterState.onBreak;
			print("Waiter " + w.getName() + " on break now");
			w.isOnBreak = true;
			stateChanged();
		}
		else 
			print("Can't go on break, " + w.getName() + ", there are no other waiters");
	}

	public void msgBackFromBreak(WaiterAgent w) {
		MyWaiter mw = findWaiter(w);
		mw.ws = waiterState.working;
		print("Waiter " + w.getName() + " off break now");
		w.isOnBreak = false;
		stateChanged();
	}
	
	public void msgLeaving(CustomerAgent c) {
		MyCustomer mc = findCustomer(c);
		waitingCustomers.remove(mc);
	}
	
	public void msgTableIsFree(int table ) {
		Table t = findTable(table);
		t.setUnoccupied();
		stateChanged();
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
		if (tablesAllFull() && !waitingCustomers.isEmpty()) {
			synchronized(waitingCustomers) {
				for (MyCustomer mc : waitingCustomers) {	
					if (mc.cs == customerState.waiting) {
						askToWait(mc);
						return true;
					}
				}
			}
		}
		if (!waitingCustomers.isEmpty()){
			synchronized(tables) {
				for (Table table : tables) {
					if (!table.isOccupied()) {
						if (!waitingCustomers.isEmpty() && !waiters.isEmpty()) {
							MyWaiter waiter = FindAWaiter();
							waiter.w.msgCreateCustomer(waitingCustomers.get(0).cust, table.tableNumber, waitingCustomers.get(0).loc);
							table.setOccupant(waitingCustomers.get(0).cust);
							Do("Gave " + waitingCustomers.get(0).cust + " to waiter " + waiter.w.getName());
							waitingCustomers.remove(0);
							return true;
						}
					}
				}
			}
			//return true;
			
		}
		return false;

	}

	// Actions
	private void askToWait(MyCustomer c) {
		print("asking " +c.cust.getName() + " to wait because restaurant is full");
		c.cs = customerState.askedToWait;
		c.cust.msgRestaurantFull();
	}
	
	private MyWaiter FindAWaiter() {
		if (waiters.size() == 1) {
			return (waiters.get(0));
		}
		MyWaiter chosenWaiter = null;
		int maxCusts = 100000;
		synchronized(waiters) {
			for (MyWaiter waiter: waiters) {
				if (waiter.w.numCustomers() < maxCusts) {
					if (waiter.ws == waiterState.working) maxCusts = waiter.w.numCustomers();
					chosenWaiter = waiter;
				}
			}
		}
		return chosenWaiter;
	}
		

	//utilities 
	
	private boolean tablesAllFull() {
		synchronized(tables) {
			for (Table t : tables) {
				if (t.isOccupied() == false) {
					return false;
				}
			}
		}
		return true;
	}
	
	private Table findTable(int tableNum) {
		synchronized(tables) {
			for (Table table : tables) {
				if (table.tableNumber == tableNum) {
					return table;
				}
			}
		}
		return null;
	}
	
	private MyWaiter findWaiter(WaiterAgent w) {
		synchronized(waiters) {
			for (MyWaiter waiter : waiters) {
				if (waiter.w == w) {
					return waiter;
				}
			}
		}
		return null;
	}
	
	private MyCustomer findCustomer(CustomerAgent c) {
		synchronized (waitingCustomers) {
			for (MyCustomer mc : waitingCustomers) {
				if (mc.cust == c) {
					return mc;
				}
			}
		}
		return null;
	}
	
	
	private int numWaitersWorking(){
		int count = 0;
		synchronized(waiters) {
			for (MyWaiter w: waiters) {
				if (w.ws == waiterState.working) {
				count ++;
				}
			}
		}
		return count;
	}

	private class MyWaiter {
		WaiterAgent w;
		waiterState ws;
		
		MyWaiter(WaiterAgent wait, waiterState state) {
			w= wait;
			ws = state;
		}
	}
	
	
	enum customerState{waiting, askedToWait}
	private class MyCustomer {
		CustomerAgent cust;
		customerState cs;
		int loc;
		
		MyCustomer(CustomerAgent c, int num) {
			cust = c;
			cs = customerState.waiting;
			loc = num;
		}
	}
	
}

