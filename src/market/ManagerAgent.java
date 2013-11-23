package market;

import java.util.*;
import java.util.concurrent.Semaphore;

import market.gui.CashierGui;
import agent.Agent;

/**
 * Restaurant Host Agent
 */

//A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.

public class ManagerAgent extends Agent {
	
	static final int NTABLES = 4;
	static final int NWORKERS = 0;
	public List<CustomerAgent> customers =  Collections.synchronizedList(new ArrayList<CustomerAgent>());
	public List<WorkerAgent> workers =  Collections.synchronizedList(new ArrayList<WorkerAgent>());
	public List<Shelf> shelves  =  Collections.synchronizedList(new ArrayList<Shelf>());
	public CashierGui cashierGui = null;
	public CashierAgent cashier;
	private String name;
	
	public ManagerAgent(String name) {
		super();

		this.name = name;

		shelves = new ArrayList<Shelf>(NTABLES);
		for (int ix = 1; ix <= NTABLES; ix++) {
			shelves.add(new Shelf(ix));
		}

		workers = new ArrayList<WorkerAgent>(NWORKERS);
		for (int ix = 1; ix <= NWORKERS; ix++) {
			workers.add(new WorkerAgent("le worker "+ix, this));
		}
		
		cashier = new CashierAgent("le cashier", 100);
		cashier.startThread();
		
	}

	public String getName() {
		return name;
	}

	public List getCustomers() {
		return customers;
	}

	public Collection getShelves() {
		return shelves;
	}
	
	public List getWorkers() {
		return workers;
	}
		
	public void msgIAmHere(CustomerAgent cust) {
		customers.add(cust);
		for (CustomerAgent c: customers) {
			if (c == cust) {
				stateChanged();
				return;
			}
		}
		customers.add(cust);
		stateChanged();
	}
	
	public void msgTableIsFree(Shelf t) {
		t.setUnoccupied();
		stateChanged();
	}
	
	public void msgIAmReady() {
		stateChanged();
	}
	
	public void msgWantToGoOnBreak(WorkerAgent w) {
		if (approveBreak(w)) {
			Do("Break approved!");
			w.msgOKToGoOnBreak(true);
		} else {
			Do("Break not approved.");
			w.msgOKToGoOnBreak(false);
		}
		stateChanged();
	}
	
	public void msgBackFromBreak(WorkerAgent w) {
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
		synchronized(customers){
			for (CustomerAgent c: customers) {
				Collections.sort(workers, new Comparator<WorkerAgent>() {
				    public int compare(WorkerAgent w1, WorkerAgent w2) {
				        return w1.getCustomers() - w2.getCustomers();
				    }});
				synchronized(workers){
					for (WorkerAgent w: workers) {
						if (w.isReady()) {
							synchronized(shelves){
								for (Shelf s: shelves) {
									if (!s.isOccupied()) {
										s.setOccupant(c);
										customers.remove(c);
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
	
	public WorkerAgent addWorker() {
		WorkerAgent w = new WorkerAgent("W"+(int)(workers.size()+1), this);
		workers.add(w);
		stateChanged();
		return w;
	}
	
	public boolean approveBreak(WorkerAgent w) {
		if (!w.isReady()) return false;
		for (WorkerAgent wa: workers) {
			if (wa != w && !wa.isOnBreak()) return true;
		}
		return false;
	}
	
	public boolean isFull() {
		for (Shelf t: shelves) {
			if (!t.isOccupied()) {
				return false;
			}
		}
		return true;
	}

	public class Shelf{
		CustomerAgent occupiedBy;
		int num;

		Shelf(int number) {
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
	
	public void setGui(CashierGui gui) {
		cashierGui = gui;
	}
	
	public int howManyWorkers() {
		return workers.size();
	}
}

