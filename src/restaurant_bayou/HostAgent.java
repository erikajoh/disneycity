package restaurant_bayou;

import agent_bayou.Agent;
import restaurant_bayou.gui.CashierGui;
import restaurant_bayou.gui.CookGui;
import restaurant_bayou.gui.WaiterGui;

import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Restaurant Host Agent
 */

//A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.

public class HostAgent extends Agent {
	
	static final int NTABLES = 4;
	static final int NWAITERS = 0;
	public List<CustomerAgent> customers =  Collections.synchronizedList(new ArrayList<CustomerAgent>());
	public List<CustomerAgent> waitingCustomers =  Collections.synchronizedList(new ArrayList<CustomerAgent>());
	public List<CustomerAgent> unseatedCustomers =  Collections.synchronizedList(new ArrayList<CustomerAgent>());
	public List<WaiterAgent> waiters =  Collections.synchronizedList(new ArrayList<WaiterAgent>());
	public Menu menu = new Menu();
	public Collection<Table> tables;
	public CashierGui cashierGui = null;
	public CookGui cookGui = null;
	public CookAgent cook;
	public CashierAgent cashier;
	private String name;
	
	public HostAgent(String name) {
		super();

		this.name = name;

		tables = new ArrayList<Table>(NTABLES);
		for (int ix = 1; ix <= NTABLES; ix++) {
			tables.add(new Table(ix));
		}

		waiters = new ArrayList<WaiterAgent>(NWAITERS);
		for (int ix = 1; ix <= NWAITERS; ix++) {
			waiters.add(new WaiterAgent("le waiter "+ix, this));
		}
		
		menu.add("salad", 7.50);
		menu.add("pizza", 8.00);
		menu.add("pasta", 8.50);
		menu.add("steak", 9.00);
		
		cashier = new CashierAgent("le cashier", menu, 100);
		cashier.startThread();
		
		cook = new CookAgent(cashier, "le cook", menu);
		cook.startThread();
	}

	public String getName() {
		return name;
	}

	public List getCustomers() {
		return customers;
	}

	public Collection getTables() {
		return tables;
	}
	
	public List getWaiters() {
		return waiters;
	}
		
	public void msgIAmHere(CustomerAgent cust) {
		waitingCustomers.add(cust);
		for (CustomerAgent c: customers) {
			if (c == cust) {
				stateChanged();
				return;
			}
		}
		customers.add(cust);
		stateChanged();
	}
	
	public void msgTableIsFree(Table t) {
		t.setUnoccupied();
		stateChanged();
	}
	
	public void msgIAmReady() {
		stateChanged();
	}
	
	public void msgWantToGoOnBreak(WaiterAgent w) {
		if (approveBreak(w)) {
			Do("Break approved!");
			w.msgOKToGoOnBreak(true);
		} else {
			Do("Break not approved.");
			w.msgOKToGoOnBreak(false);
		}
		stateChanged();
	}
	
	public void msgBackFromBreak(WaiterAgent w) {
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		/* Think of this next rule as:
            Does there exist a table, customer and waiter,
            such that table is unoccupied, customer is waiting, and waiter is ready.
            If so, tell waiter to seat customer at table.
		 */
		synchronized(waitingCustomers){
			for (CustomerAgent c: waitingCustomers) {
				Collections.sort(waiters, new Comparator<WaiterAgent>() {
				    public int compare(WaiterAgent w1, WaiterAgent w2) {
				        return w1.getCustomers() - w2.getCustomers();
				    }});
				synchronized(waiters){
					for (WaiterAgent w: waiters) {
						if (w.isReady()) {
							synchronized(tables){
								for (Table t: tables) {
									if (!t.isOccupied()) {
										t.setOccupant(c);
										w.msgSeatCustomer(c, menu);
										waitingCustomers.remove(c);
										return true;
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
	
	public WaiterAgent addWaiter() {
		WaiterAgent w = new WaiterAgent("W"+(int)(waiters.size()+1), this);
		waiters.add(w);
		stateChanged();
		return w;
	}
	
	public MarketAgent addMarket() {
		MarketAgent mkt = new MarketAgent("M"+(int)(cook.numMarkets()+1), menu);
		cook.addMarket(mkt);
		return mkt;
	}
	
	public boolean approveBreak(WaiterAgent w) {
		if (!w.isReady()) return false;
		for (WaiterAgent wa: waiters) {
			if (wa != w && !wa.isOnBreak()) return true;
		}
		return false;
	}
		
	public void setGui(CashierGui gui) {
		cashierGui = gui;
	}
	
	public void setGui(CookGui gui) {
		cookGui = gui;
		cook.setGui(gui);
	}

	public CashierGui getGui() {
		return cashierGui;
	}
	
	public boolean isFull() {
		for (Table t: tables) {
			if (!t.isOccupied()) {
				return false;
			}
		}
		return true;
	}

	public class Table {
		CustomerAgent occupiedBy;
		int num;

		Table(int number) {
			this.num = number;
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
		
	}
	
	public class Menu {
		Hashtable<String, Double> menuItems = new Hashtable<String, Double>();
		List<String> menuNames = new ArrayList<String>();
		public void add(String name, double cost) {
			menuItems.put(name, cost);
			menuNames.add(name);
		}
		public double getCost(String name) {
			return menuItems.get(name);
		}
	}
	
	public int howManyWaiters() {
		return waiters.size();
	}
}

