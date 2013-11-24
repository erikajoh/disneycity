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

	public String getName() {
		return name;
	}
	
	public void msgWantToOrder(CustomerAgent c, String choice, int quantity) { // from customer
    	
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
	
}

