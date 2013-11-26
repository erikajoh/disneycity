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
	private Semaphore moving = new Semaphore(0, true);
	private Semaphore working = new Semaphore(0, true);
	int num;
		
	class MyOrder {
		CustomerAgent c;
		String item;
		int quantity;
		boolean virtual;
		MyOrder(CustomerAgent cust, String i, int q, boolean v) { c = cust; item = i; quantity = q; virtual = v; }
	}
	
	public WorkerGui workerGui = null;
	private PersonAgent person;
	private CashierAgent cashier;
	private Market market;
	
	public WorkerAgent(String name, ManagerAgent manager, int num) {
		super();

		this.name = name;
		this.manager = manager;
		this.num = num;

	}
	
	public String getName() {
		return name;
	}
	
	public int getNum() {
		return num;
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
	
	public void msgAnimationDeliveredFinished() {
		//from animation
		moving.release();
		working.release();
		stateChanged();
	}
	
	public void msgAnimationFinished() {
		//from animation
		moving.release();
		stateChanged();
	}
	
	public void msgGoGetItem(CustomerAgent cust, String c, int quantity, boolean virtual) { // from customer
		print("rcvd msgGoGetItem");
		orders.add(new MyOrder(cust, c, quantity, virtual));
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		for (MyOrder o: orders) {
			int numItems = GetItem(o.item, o.quantity, o.virtual);
			try {
				working.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (numItems == 0) o.c.msgOutOfItem();
			else {
				o.c.msgHereIsItemAndBill(numItems, market.getPrice(o.item)*numItems);
				cashier.msgHereIsBill(o.c, market.getPrice(o.item)*numItems);
			}
			orders.remove(o);
			workerGui.DoGoToHome();
			try {
				moving.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}
	
	public int GetItem(String item, int quantity, boolean virtual) {
		workerGui.DoGoGetItem(market.getLocation(item));
		try {
			moving.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (virtual) workerGui.DoBringItemToTruck();
		else workerGui.DoBringItemToFront();
		try {
			moving.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return market.getItem(item, quantity);
	}

	public void setGui(WorkerGui gui) {
		workerGui = gui;
		workerGui.setPresent(true);
	}

	public WorkerGui getGui() {
		return workerGui;
	}
	
}