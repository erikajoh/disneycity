package market;

import agent.Agent;

import java.util.*;
import java.util.concurrent.Semaphore;

import simcity.PersonAgent;
import market.gui.WorkerGui;

/**
 * Restaurant Waiter Agent
 */

public class WorkerAgent extends Agent {

	private String name;
	private ManagerAgent manager;
	private List<CustomerAgent> myCustomers = new ArrayList<CustomerAgent>();
	
	public WorkerGui workerGui = null;
	private PersonAgent person;
		
	public enum AgentEvent 
	{none, seatCustomer, leaveCustomer, takeOrder, deliverOrder};
	AgentEvent event = AgentEvent.none;

	public WorkerAgent(String name, ManagerAgent manager) {
		super();

		this.name = name;
		this.manager = manager;
	
	}
	
	public String getName() {
		return name;
	}
	
	public void setPerson(PersonAgent person) {
		this.person = person;
	}
	
	public void setManager(ManagerAgent manager) {
		this.manager = manager;
	}
	
	public void msgAnimationFinished() {
		//from animation
		stateChanged();
	}
	
	public void msgHereIsMyChoice(CustomerAgent cust, String c) { // from customer
		myCustomers.add(cust);
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		return false;
	}

	public void setGui(WorkerGui gui) {
		workerGui = gui;
	}

	public WorkerGui getGui() {
		return workerGui;
	}
	
	public int getCustomers() {
		return myCustomers.size();
	}
	
}