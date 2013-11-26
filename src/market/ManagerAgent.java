package market;

import java.util.*;
import java.util.concurrent.Semaphore;

import simcity.PersonAgent;
import agent.Agent;

/**
 * Restaurant Host Agent
 */

//A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.

public class ManagerAgent extends Agent {
	
	private String name;
	
	private Market market;
	private PersonAgent person;
	public CashierAgent cashier;
	private List<WorkerAgent> myWorkers = new ArrayList<WorkerAgent>();
	private List<Order> myOrders = new ArrayList<Order>();
	
	class Order {
		CustomerAgent c;
		String choice;
		int quantity;
		boolean virtual;
		Order(CustomerAgent cust, String ch, int q, boolean v) { c = cust; choice = ch; quantity = q; virtual = v; }
	}
	
	public ManagerAgent(String name) {
		super();
		this.name = name;
	}
	
	public void setPerson(PersonAgent person) {
		this.person = person;
	}
	
	public void setMarket(Market market) {
		this.market = market;
	}
	
	public void setCashier(CashierAgent cashier) {
		this.cashier = cashier;
	}
	
	public void addWorker(WorkerAgent worker) {
		myWorkers.add(worker);
	}

	public String getName() {
		return name;
	}
	
	public void msgWantToOrder(CustomerAgent c, String choice, int quantity, boolean virtual) { // from customer
		myOrders.add(new Order(c, choice, quantity, virtual));
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
		for (Order o: myOrders) {
	    	myWorkers.get(0).msgGoGetItem(o.c, o.choice, o.quantity, o.virtual);
	    	myWorkers.add(myWorkers.get(0));
	    	myWorkers.remove(0);
	    	myOrders.remove(o);
	    	return true;
		}
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions
	
}

