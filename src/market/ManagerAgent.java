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
	
	static final int NTABLES = 4;
	static final int NWORKERS = 0;
	public List<CustomerAgent> customers =  Collections.synchronizedList(new ArrayList<CustomerAgent>());
	public List<WorkerAgent> workers =  Collections.synchronizedList(new ArrayList<WorkerAgent>());
	public CashierAgent cashier;
	private String name;
	
	private PersonAgent person;
	
	public ManagerAgent(String name) {
		super();

		this.name = name;
		
	}

	public String getName() {
		return name;
	}
	
	public void setPerson(PersonAgent person) {
		this.person = person;
	}

	public List getCustomers() {
		return customers;
	}
	
	public List getWorkers() {
		return workers;
	}
		
	public void msgIAmHere(CustomerAgent cust) { // from customer
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
	
	public void msgWantToOrder(String choice, int quantity) { // from customer
    	
    }
	
    public void msgOrderFulfilled(String choice, int quantity) { // from worker
    	
    }
	
	public void msgTransportationReady() { // from transportation
    	
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
		
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions
	
	public void addWorker(WorkerAgent w) {
		workers.add(w);
		stateChanged();
	}
	
	public void setCashier(CashierAgent ca) {
		cashier = ca;
	}
	
	public void addCustomer(CustomerAgent c) {
		customers.add(c);
		stateChanged();
	}
	
	public int howManyWorkers() {
		return workers.size();
	}
}

