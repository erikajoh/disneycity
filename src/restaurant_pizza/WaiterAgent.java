package restaurant_pizza;

import agent_pizza.Agent;
import restaurant_pizza.gui.RestaurantPizza;
import restaurant_pizza.gui.WaiterGui;
import restaurant_pizza.interfaces.Cashier;
import restaurant_pizza.interfaces.Customer;
import restaurant_pizza.interfaces.Waiter;
import simcity.PersonAgent;
import simcity.interfaces.Person;
import simcity.RestMenu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Semaphore;

public abstract class WaiterAgent extends Agent implements Waiter {
	
	// ***** DATA*****
	
	private LinkedList<MyCustomer> myCustomers = new LinkedList<MyCustomer>();
	private LinkedList<Check> checks = new LinkedList<Check>();
	
	protected String name;
	private String orderToPickUp = "";
	protected Semaphore atTable = new Semaphore(0, true);
	private Semaphore atNextCheckAction = new Semaphore(0, true);
	
	public enum AgentState {Working, WorkingAndWantBreak, FinishingUp, OnBreak};
	public enum AgentEvent {none, businessAsUsual, wantBreak, breakApproved, breakDenied};
	
	AgentState state = AgentState.Working;
	AgentEvent event = AgentEvent.businessAsUsual;
	
	public enum CustomerState {None, WaitingToBeSeated, FollowingWaiter, ReadyToOrder, WaiterHere,
		WaitingToOrder, GaveOrder, ReadyToOrderAgain, WaitingForFood, FoodIsComing, Eating,
		DoneWithMeal, WaitingForCheck, CheckIsComing, LeavingTable};

	public HostAgent host = null;
	public WaiterGui waiterGui = null;
	public CookAgent cook = null;
	public CashierAgent cashier = null;
	public RestMenu menu = new RestMenu();
	Person person;
	boolean shiftDone = false;
	
	public boolean isWorking = true;
	double wage = 0;

	public RestaurantPizza restaurant;

	boolean alert = false;
	boolean alertedShift = false;

	public WaiterAgent(String name) {
		super();
		initializeMenu();
		this.name = name;
	}
	
	public void initializeMenu() {
		menu.addItem("Marsinara with Meatballs", 9.49 );
    	menu.addItem("Chicken Fusilli", 9.49);
    	menu.addItem("Pepperoni Pizza",  6.99);
    	menu.addItem("Celestial Caesar Chicken Salad", 8.49);
    	menu.addItem("Bread Sticks", 4.99);
	}
	
	public void setRestaurant(RestaurantPizza rest) {
		restaurant = rest;
	}
	
	public void setHost(HostAgent aHost) {
		host = aHost;
	}
	
	public void setPerson(Person p) {
		person = p;
	}
	
	public void setCook(CookAgent aCook) {
		cook = aCook;
	}
	
	public void setCashier(CashierAgent c) {
		cashier = c;
	}

	public String getName() {
		return name;
	}

	// ***** MESSAGES *****
	
	public void msgSeatCustomer(CustomerAgent c, int tableNumber) {
		print("msgSeatCustomer() received from host to seat Customer " + c.getName());
		myCustomers.add(new MyCustomer(c, tableNumber, CustomerState.WaitingToBeSeated));
		stateChanged();
	}
	
	public void updateMenu() {
		menu = cook.getMenu();
	}
	public void msgIAmReadyToOrder(CustomerAgent c) {
		print("msgIAmReadyToOrder() received from Customer " + c.getName());
		MyCustomer mc = findCorrespondingMyCustomer(c);
		mc.state = CustomerState.ReadyToOrder;
		stateChanged();
	}
	
	public void msgGivingMyOrder(CustomerAgent c, String choice) {
		print("msgGivingMyOrder() received from Customer " + c.getName());
		MyCustomer mc = findCorrespondingMyCustomer(c);
		mc.state = CustomerState.GaveOrder;
		mc.setOrder(choice);
		stateChanged();
	}
	
	public void msgOutOfThisOrder(int tableNum, String order) {
		print("msgOutOfThisOrder() received from Cook");
		for(MyCustomer tempMC : myCustomers)
			if(tempMC.table == tableNum) {
				tempMC.state = CustomerState.ReadyToOrderAgain;
				tempMC.setOrder(null);
				break;
			}
		stateChanged();
	}
	
	public void msgOrderReady(int tableNum, String order) {
		print("msgOrderReady() received from Cook");
		MyCustomer mc = null;
		for(MyCustomer tempMC : myCustomers)
			if(tempMC.table == tableNum) {
				mc = tempMC;
				mc.state = CustomerState.FoodIsComing;
				break;
			}
		stateChanged();
	}
	
	public void msgNeedCheck(CustomerAgent c) {
		print("msgNeedCheck() received from Customer " + c.getName());
		MyCustomer mc = findCorrespondingMyCustomer(c); 
		mc.state = CustomerState.DoneWithMeal;
		stateChanged();
	}
	
	@Override
	public void msgHereIsCheck(Check aCheck) {
		checks.add(aCheck);
		Customer customer = (Customer)aCheck.customer;
		MyCustomer mc = findCorrespondingMyCustomer(customer);
		// mc.state = CustomerState.CheckIsComing;
		atNextCheckAction.release();
		stateChanged();
	}
	
	public void msgLeavingRestaurant(CustomerAgent c) {
		print("msgLeavingRestaurant() received from Customer " + c.getName());
		MyCustomer mc = findCorrespondingMyCustomer(c);
		mc.state = CustomerState.LeavingTable;
		stateChanged();
	}
	
	// Methods about going on break
	public void msgNeedBreak() { // entry point in WaiterAgent class for going on break
		print("msgNeedBreak() called");
		event = AgentEvent.wantBreak;
		stateChanged();
	}
	
	public void msgApproveBreak() {
		print("msgApproveBreak() called");
		event = AgentEvent.breakApproved;
		stateChanged();
	}

	public void msgDenyBreak() {
		print("msgDenyBreak() called");
		event = AgentEvent.breakDenied;
		stateChanged();
	}
	
	public void msgGetOffBreak() {
		print("msgGetOffBreak() called; state = " + state);
		event = AgentEvent.businessAsUsual;
		stateChanged();
	}
	
	public void msgShiftDone(boolean alertOthers, double w) {
		shiftDone = true;
		alert = alertOthers;
		wage = w;
		stateChanged();
	}
	
	public void msgAtDestination() {
		atTable.release();
		stateChanged();
	}
	
	// ***** SCHEDULER *****
	
	protected boolean pickAndExecuteAnAction() {
		try {
			if(state == AgentState.Working && event == AgentEvent.wantBreak) {
				state = AgentState.WorkingAndWantBreak;
				host.msgWantToGoOnBreak(this); //not a new action, directly done in scheduler
				return true;
			}
			if(state == AgentState.WorkingAndWantBreak && event == AgentEvent.breakDenied) {
				state = AgentState.Working;
				event = AgentEvent.businessAsUsual; //not a new action, directly done in scheduler
				return true;
			}
			if(state == AgentState.WorkingAndWantBreak && event == AgentEvent.breakApproved) {
				state = AgentState.FinishingUp;
				return true;
			}
			if(state == AgentState.OnBreak && event == AgentEvent.businessAsUsual) {
				state = AgentState.Working;
				host.msgDoneWithBreak(this); //not a new action, directly done in scheduler
				return true;
			}
			if((state == AgentState.FinishingUp && event == AgentEvent.breakApproved) ||
					(state == AgentState.Working && event == AgentEvent.businessAsUsual)) {
				if(state == AgentState.FinishingUp && myCustomers.isEmpty()) {
					state = AgentState.OnBreak;
					goToHomePositionIfNothingToDo();
					return true;
				}
				for(MyCustomer mc : myCustomers) {
					if(mc.state == CustomerState.WaitingToBeSeated) {
						mc.state = CustomerState.FollowingWaiter;
						seatCustomer(mc.c, mc.table);
						return true;
					}
				}
				for(MyCustomer mc : myCustomers) {
					if(mc.state == CustomerState.ReadyToOrder) {
						goToTableToTakeOrder(mc.c, mc.table);
						mc.state = CustomerState.WaiterHere;
						return true;
					}
				}
				for(MyCustomer mc : myCustomers) {
					if(mc.state == CustomerState.GaveOrder) {
						print("hi");
						goToCook(mc);
						mc.state = CustomerState.WaitingForFood;
						return true;
					}
				}
				for(MyCustomer mc : myCustomers) {
					if(mc.state == CustomerState.ReadyToOrderAgain) {
						print("yay2");
						goToTableToTakeOrderAgain(mc.c, mc.table);
						mc.state = CustomerState.WaiterHere;
						return true;
					}
				}
				for(MyCustomer mc : myCustomers) {
					if(mc.state == CustomerState.FoodIsComing) {
						goToTableToGiveFood(mc.c, mc.table);
						mc.state = CustomerState.Eating;
						return true;
					}
				}
				for(MyCustomer mc : myCustomers) {
					if(mc.state == CustomerState.DoneWithMeal) {
						// Here's where actions are combined
						
						goToCashierToGetCheck(mc);
						try {
							atNextCheckAction.acquire();
						}
						catch (InterruptedException e) {
							e.printStackTrace();
						}
						// release semaphore in message
						goToTableToGiveCheck(mc.c, mc.table);
						mc.state = CustomerState.LeavingTable;
						return true;
						/*
						
						goToCashierToGetCheck(mc);
						mc.state = CustomerState.WaitingForCheck;
						return true;
						*/
					}
				}
				for(MyCustomer mc : myCustomers) {
					if(mc.state == CustomerState.CheckIsComing) {
						goToTableToGiveCheck(mc.c, mc.table);
						mc.state = CustomerState.LeavingTable;
						return true;
					}
				}
				for(MyCustomer mc : myCustomers) {
					if(mc.state == CustomerState.LeavingTable) {
						leaveRestaurant(mc.c, mc.table);
						return true;
					}
				}
			}
			if (shiftDone && !alertedShift && myCustomers.size()==0) {msgShiftDone(alert); alertedShift = true;}
			return false;
		} catch (ConcurrentModificationException e) {
			return true;
		} catch (NullPointerException e) {
			print("NULL POINTER");
			e.printStackTrace();
			return false;
		}
	}

	// ***** ACTIONS *****
	
	protected void leaveWork() {
		if (alert) {
			alertedShift = true;
			print ("going home!");
			isWorking = false;
			waiterGui.DoLeave(person, wage);
			if (cook!=null) { 
				cook.msgShiftDone(wage); 
			}
			if (host!=null) { 
			}
			if (cashier!=null) { 
				cashier.msgShiftDone(wage); 
			}
		}
		else {
			print ("going home!");
			isWorking = false;
			waiterGui.DoLeave(person, wage);
		}
	}

	private void goToTableToTakeOrder(CustomerAgent customer, int tableNumber) {
		print("Calling action goToTableToTakeOrder");
		DoGoToTable(tableNumber);
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		customer.msgWhatWouldYouLike();
	}
	
	private void goToTableToTakeOrderAgain(CustomerAgent customer, int tableNumber) {
		print("Calling action goToTableToTakeOrderAgain");
		DoGoToTable(tableNumber);
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		customer.msgWeAreOutOfThisOrder();
	}
	
	private void seatCustomer(CustomerAgent customer, int tableNumber) {
		print("Calling action seatCustomer");
		DoGoToEntrance();
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		updateMenu();
		customer.msgSitAtTable(this, tableNumber, menu);
		DoGoToTable(tableNumber);
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		DoGoToHomePosition();
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	protected abstract void goToCook(MyCustomer mc);
	
	private void goToTableToGiveFood(CustomerAgent customer, int tableNumber) {
		print("Calling action goToTableToGiveFood");
		
		DoGoToCook();
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		};
		
		MyCustomer mc = findCorrespondingMyCustomer(customer);
		orderToPickUp = mc.order;
		waiterGui.DoDisplayOrder(orderToPickUp);
		
		DoGoToTable(tableNumber);
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		orderToPickUp = "";
		waiterGui.DoDisplayOrder("");
		customer.msgFoodArrived();
		DoGoToHomePosition();
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void goToCashierToGetCheck(MyCustomer mc) {
		print("Calling action goToCashierToGetCheck");
		DoGoToCashier();
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		waiterGui.DoDisplayOrder("(check)");
		cashier.msgCustomerNeedsCheck(this, mc.order, mc.c, menu);
	}
	
	private void goToTableToGiveCheck(CustomerAgent customer, int tableNumber) {
		print("Calling action goToTableToGiveCheck()");
		
		Check theCheck = null;
		int checkInd;
		for(checkInd = 0; checkInd < checks.size(); checkInd++) {
			Check tempCheck = checks.get(checkInd);
			if(tempCheck.customer.equals(customer)) {
				theCheck = tempCheck;
				break;
			}
		}
		MyCustomer mc = findCorrespondingMyCustomer(customer);
		DoGoToTable(mc.table);
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		checks.remove(checkInd);
		waiterGui.DoDisplayOrder("");
		customer.msgHereIsCheck(theCheck);
		host.msgTableIsFree(this, tableNumber);
	}
	
	private void leaveRestaurant(Customer customer, int tableNumber) {
		MyCustomer mc = findCorrespondingMyCustomer(customer);
		myCustomers.remove(mc);
		host.msgTableIsFree(this, tableNumber);
		goToHomePositionIfNothingToDo();
	}
	
	private void goToHomePositionIfNothingToDo() {
		print("Calling action goToEntranceIfNothingToDo");
		DoGoToHomePosition();
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	// Animations go here
	
	private void DoGoToEntrance() {
		print("Going to entrance");
		waiterGui.DoGoToEntrance();	
	}
	
	private void DoGoToTable(int tableNum) {
		print("Going to table # " + tableNum);
		waiterGui.DoGoToTable(tableNum);
	}
	
	protected void DoGoToHomePosition() {
		print("Going to my home position");
		waiterGui.DoGoToHomePosition();
	}
	
	protected void DoGoToCook() {
		print("Going to cook");
		waiterGui.DoGoToCook();
	}
	
	private void DoGoToCashier() {
		print("Going to cashier");
		waiterGui.DoGoToCashier();
	}
	
	// Utilities

	public MyCustomer findCorrespondingMyCustomer(Customer c) {
		MyCustomer mc = null;
		for(MyCustomer tempMC : myCustomers)
			if(tempMC.c.equals(c)) {
				mc = tempMC;
				break;
			}
		return mc;
	}
	
	public void setGui(WaiterGui gui) {
		waiterGui = gui;
	}

	public WaiterGui getGui() {
		return waiterGui;
	}
	
	protected class MyCustomer {
		CustomerAgent c;
		int table;
		String order;
		CustomerState state;
		
		public MyCustomer(CustomerAgent aCustomerAgent, int aTable, CustomerState aState) {
			c = aCustomerAgent;
			table = aTable;
			order = null;
			state = aState;
		}
		
		public void setOrder(String aOrder) {
			order = aOrder;
		}
	}
}