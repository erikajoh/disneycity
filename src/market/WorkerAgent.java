package market;

import agent.Agent;

import java.util.*;
import java.util.concurrent.Semaphore;

import market.gui.WorkerGui;

/**
 * Restaurant Waiter Agent
 */

public class WorkerAgent extends Agent {

	private String name;
	private ManagerAgent manager;
	private List<CustomerAgent> myCustomers = new ArrayList<CustomerAgent>();
	private List<String> unavailableFood = new ArrayList<String>();
	
	private boolean readyForNextTask = true;
	private boolean wantBreak = false;
	private boolean onBreak = false;
	
	public WorkerGui workerGui = null;
		
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
	
	public void msgServeCustomer(CustomerAgent cust) {
		myCustomers.add(cust);
		stateChanged();
	}
	
	public void msgWorkerReady() {
		//from animation
		readyForNextTask = true;
		stateChanged();
	}
	
	public void msgHereIsMyChoice(CustomerAgent cust, String c) {
		myCustomers.add(cust);
		stateChanged();
	}
	
	public void msgOutOfFood(List<String> uf) {
		unavailableFood = uf;
		stateChanged();
	}
	
	public void msgOKToGoOnBreak(boolean ok) {
		if (ok) takeBreak();
//		else workerGui.setEnabled();
		wantBreak = false;
	}
	
	public void msgDoneLeaving(CustomerAgent c) {
		stateChanged();
	}
	
//	public void msgHereIsCheck(Check c) {
//		checks.put(c.table, c);
//		checksReady.add(c.table);
//		stateChanged();
//	}
	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		return false;
	}
	
	private void takeBreak() {
		onBreak = true;
		stateChanged();
//		Timer t = new Timer();
//		t.schedule(new TimerTask() {
//			public void run() {
//				onBreak = false;
//				stateChanged();
//			}
//		},
//		time);
	}

	public void setGui(WorkerGui gui) {
		workerGui = gui;
	}

	public WorkerGui getGui() {
		return workerGui;
	}
	
	public boolean isReady() {
		return (!onBreak && readyForNextTask);
	}
	
	public boolean isOnBreak() {
		return onBreak;
	}
	
	public int getCustomers() {
		return myCustomers.size();
	}
	
	public void askForBreak() {
		wantBreak = true;
		stateChanged();
	}
	
	public void finishBreak() {
		Do("Back from break.");
		onBreak = false;
		stateChanged();
	}
}