package market;

import agent.Agent;

import java.util.*;
import java.util.concurrent.Semaphore;

import simcity.PersonAgent;
import simcity.Restaurant;
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
	int num;
	boolean shiftDone = false;
	boolean alert = false;
	boolean alertedShift = false;
		
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
	
	public void msgGoGetItem(Customer cust, String c, int quantity, boolean virtual) { // from customer
		print("rcvd msgGoGetItem");
		orders.add(new MyOrder(cust, c, quantity, virtual));
		stateChanged();
	}
	
//	public void msgShiftDone(boolean alertOthers) {
//		shiftDone = true;
//		alert = alertOthers;
//		if (orders.size() == 0 && alertOthers) {
//			alertedShift = true;
//			print ("going home!");
//			workerGui.DoLeave(); //need to fix
//			if (manager!=null) { 
//				if (cashier!=null) cashier.subtract(10.0); 
//			}
//			if (cashier!=null) { 
////				cashier.msgShiftDone(); 
//				cashier.subtract(20);
//			}
//		}
//		else if (orders.size() == 0){
//			print ("going home!");
//			workerGui.DoLeave();
//		}
//		else {
//			print("my shift is done! but I still have customers");
//		}
//	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		for (MyOrder o: orders) {
			GetItemAndReturn(o);
			return true;
		}
//		if (shiftDone && !alertedShift) {
//			msgShiftDone(alert);
//			alertedShift = true;
//		}
		return false;
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