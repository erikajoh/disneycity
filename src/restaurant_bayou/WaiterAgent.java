package restaurant_bayou;

import agent_bayou.Agent;
import restaurant_bayou.CashierAgent.Check;
import restaurant_bayou.CookAgent.Order;
import restaurant_bayou.CookAgent.OrderState;
import restaurant_bayou.CustomerAgent.AgentEvent;
import restaurant_bayou.CustomerAgent.AgentState;
import restaurant_bayou.HostAgent.Menu;
import restaurant_bayou.HostAgent.Table;
import restaurant_bayou.gui.WaiterGui;
import restaurant_bayou.interfaces.Waiter;
import simcity.PersonAgent;
import simcity.RestMenu;

import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Restaurant Waiter Agent
 */

public class WaiterAgent extends Agent implements Waiter {

	private String name;
	private HostAgent host;
	private CookAgent cook;
	private Semaphore atTable = new Semaphore(0, true);
	private List<CustomerAgent> myCustomers = new ArrayList<CustomerAgent>();
	private List<Integer> myTables = new ArrayList<Integer>();
	private RestMenu myMenu;
	private Hashtable<CustomerAgent, String> myChoices = new Hashtable<CustomerAgent, String>();
	private List<String> unavailableFood = new ArrayList<String>();
	private Hashtable<Integer, Check> checks = new Hashtable<Integer, Check>();
	private List<Integer> checksReady = new ArrayList<Integer>();
	private PersonAgent person;
	
	
	private boolean readyForNextTask = true;
	private boolean wantBreak = false;
	private boolean onBreak = false;
	private boolean needToReorder = false;
	
	public WaiterGui waiterGui = null;
		
	public enum AgentEvent 
	{none, seatCustomer, leaveCustomer, takeOrder, deliverOrder};
	AgentEvent event = AgentEvent.none;

	public WaiterAgent(String name) {
		super();

		this.name = name;
	
	}
	
	public String getName() {
		return name;
	}
		
	public void msgOrderIsDone(int t) {
		myTables.add(t);
		stateChanged();
	}
	
	public void setPerson(PersonAgent p) {
		person = p;
	}
	
	public void setHost(HostAgent h) {
		host = h;
	}
	
	public void setCook(CookAgent c) {
		cook = c;
	}

	public void msgLeavingTable(CustomerAgent cust) {
		for (Table table : host.tables) {
			if (table.getOccupant() == cust) {
				print(cust + " leaving " + table.num);
				table.setUnoccupied();
				stateChanged();
			}
		}
	}

	public void msgAtTable() {
		//from animation
		atTable.release();
		stateChanged();
	}
	
	public void msgSeatCustomer(CustomerAgent cust, RestMenu m) {
		myCustomers.add(cust);
		myMenu = m;
		stateChanged();
	}
	
	public void msgImReadyToOrder(CustomerAgent cust) {
		myCustomers.add(cust);
		stateChanged();
	}
	
	public void msgWaiterReady() {
		//from animation
		readyForNextTask = true;
		host.msgIAmReady();
		stateChanged();
	}
	
	public void msgHereIsMyChoice(CustomerAgent cust, String c) {
		myCustomers.add(cust);
		myChoices.put(cust, c);
		stateChanged();
	}
	
	public void msgDoneEating(CustomerAgent c) {
		stateChanged();
	}
	
	public void msgDoneLeaving(CustomerAgent c) {
		stateChanged();
	}
	
	public void msgOutOfFood(List<String> uf) {
		needToReorder = true;
		unavailableFood = uf;
		stateChanged();
	}
	
	public void msgOKToGoOnBreak(boolean ok) {
		if (ok) takeBreak();
		//else waiterGui.setEnabled();
		wantBreak = false;
	}
	
	public void msgHereIsCheck(Check c) {
		checks.put(c.table, c);
		checksReady.add(c.table);
		stateChanged();
	}
	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		try {
			if (!waiterGui.leaving) {
				for (CustomerAgent c : myCustomers) {
					if (c.isWaitingForCheck()) {
						if (checks.containsKey(c.table)) {
							waiterGui.setText("check");
							waiterGui.DoGoToTable(c.table);
							try {
								atTable.acquire();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							c.msgHereIsYourCheck(checks.get(c.getName()));
							waiterGui.setText("");
							waiterGui.DoLeaveCustomer();
							checks.remove(c.getName());
							checksReady.remove(c.getName());
							return true;
						} else if (!checksReady.contains(c.table)) {
							host.cashier.msgGiveMeCheck(this, c, c.getChoice(), c.table);
							checksReady.add(c.table);
							return true;
						}
					}
					if (c.isWaiting()) {
	//					Do(c+" is waiting");
						for (Table t: host.tables) {
							if (t.getOccupant() == c) {
								waiterGui.setText(name);
								DoSeatCustomer(c, t, myMenu);
								try {
									atTable.acquire();
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								waiterGui.DoLeaveCustomer();
								myCustomers.remove(c);
								return true;
							}
						}
					}
					if (c.isSeated()) {
						waiterGui.setText(name);
						waiterGui.DoGoToTable(c.table);
						try {
							atTable.acquire();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						c.msgWhatWouldYouLike();
						waiterGui.DoLeaveCustomer();
						myCustomers.remove(c);
						return true;
					}
					if (c.hasOrdered() && readyForNextTask) {
						for (Table t: host.tables) {
							if (t.getOccupant() == c) {
								c.msgOrderHasBeenReceived();
								cook.msgHereIsOrder(this, myChoices.get(c), t.num);
								readyForNextTask = false;
								return true;
							}
						}
					}
					if (c.isWaitingForOrder() && needToReorder) {
						for (Table t: host.tables) {
							if (t.getOccupant() == c) {
								waiterGui.setText("uh oh!");
								waiterGui.DoGoToTable(t.num);
								try {
									atTable.acquire();
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								myChoices.remove(c);
								c.msgPleaseReorder(unavailableFood);
								waiterGui.setText("");
								waiterGui.DoLeaveCustomer();
								needToReorder = false;
								return true;
							}
						}
					}
					if (c.isWaitingForOrder() && myTables.size() > 0) {
							if (myTables.contains((Integer) c.table)) {
								waiterGui.DoGoGetFood();
								try {
									atTable.acquire();
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								waiterGui.setText(myChoices.get(c));
								waiterGui.DoGoToTable(c.table);
								try {
									atTable.acquire();
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								c.msgHereIsYourFood();
								waiterGui.setText("");
								waiterGui.DoLeaveCustomer();
								myChoices.remove(c);
								myTables.remove((Integer) c.table);
								return true;
							}
					}
				}
			}
			if (wantBreak) {
				Do("I wanna go on break");
				host.msgWantToGoOnBreak(this);
				return true;
			}
			return false;
		} catch (ConcurrentModificationException e) {
			return false;
		}
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

	private void DoSeatCustomer(CustomerAgent customer, Table table, RestMenu m) {
		print("Seating " + customer + " at " + table.num);
		waiterGui.DoGoToTable(table.num);
		customer.msgFollowMeToTable(this, m, unavailableFood);
	}

	public void setGui(WaiterGui gui) {
		waiterGui = gui;
	}

	public WaiterGui getGui() {
		return waiterGui;
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

