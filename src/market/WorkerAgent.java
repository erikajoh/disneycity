package market;

import agent.Agent;

import java.util.*;
import java.util.concurrent.Semaphore;

import simcity.PersonAgent;
import simcity.Restaurant;
import simcity.gui.trace.AlertLog;
import simcity.gui.trace.AlertTag;
import market.gui.WorkerGui;
import market.interfaces.Customer;

/**
 * Restaurant Waiter Agent
 */

public class WorkerAgent extends Agent {

	private String name;
	private ManagerAgent manager;
	private List<MyOrder> orders = new ArrayList<MyOrder>();
	private Semaphore moving = new Semaphore(0, true);
	private Semaphore working = new Semaphore(0, true);
	private enum State {idle, left;}
	private State state = State.idle;
	private int num;
	private double wage;
	private boolean shiftDone = false;
		
	class MyOrder {
		Customer c;
		String item;
		int quantity;
		boolean virtual;
		MyOrder(Customer cust, String i, int q, boolean v) { c = cust; item = i; quantity = q; virtual = v; }
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
	
	public void msgAnimationLeavingFinished() {
		//from animation
		moving.release();
		state = State.left;
	}
	
	public void msgGoGetItem(Customer cust, String c, int quantity, boolean virtual) { // from customer
		print("rcvd msgGoGetItem");
		orders.add(new MyOrder(cust, c, quantity, virtual));
		stateChanged();
	}
	
	public void msgShiftDone(double wage) {
		shiftDone = true;
		this.wage = wage;
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		for (int i=0; i<orders.size(); i++) {
			MyOrder o = orders.get(i);
			AlertLog.getInstance().logMessage(AlertTag.MARKET, name, "Getting "+o.item+" for "+o.c.toString());
			GetItemAndReturn(o);
			return true;
		}
		print("worker shift done? "+shiftDone);
		print("market no customers? "+market.noCustomers());
		if (shiftDone) {
			if (market.noCustomers()) {
				ShiftDone();
				shiftDone = false;
			}
			return true;
		}
		if (state == State.left) {
			StopWork();
			return true;
		}
		return false;
	}
	
	public void ShiftDone() {
		state = State.idle;
		workerGui.DoLeave();
		try {
			moving.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void StopWork() {
		state = State.idle;
		person.msgStopWork(wage);
		market.removeMe(this);
	}
	
	public void GetItemAndReturn(MyOrder o) {
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