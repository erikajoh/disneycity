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
	private List<MyOrder> orders = new ArrayList<MyOrder>();
	
	class MyOrder {
		CustomerAgent c;
		String item;
		int quantity;
	}
	
	public WorkerGui workerGui = null;
	private PersonAgent person;
	private CashierAgent cashier;
	private Market market;
		
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
	
	public void setMarket(Market market) {
		this.market = market;
	}
	
	public void setCashier(CashierAgent cashier) {
		this.cashier = cashier;
	}
	
	public void msgAnimationFinished() {
		//from animation
		stateChanged();
	}
	
	public void msgGoGetItem(CustomerAgent cust, String c) { // from customer
//		orders.put(cust, c);
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
//		for (CustomerAgent c: orders.keys()) {
			
//		}
		return false;
	}

	public void setGui(WorkerGui gui) {
		workerGui = gui;
	}

	public WorkerGui getGui() {
		return workerGui;
	}
	
}