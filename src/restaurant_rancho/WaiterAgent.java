package restaurant_rancho;

import agent_rancho.Agent;
import simcity.RestMenu;
import restaurant_rancho.gui.WaiterGui;
import restaurant_rancho.interfaces.Cashier;
import restaurant_rancho.interfaces.Customer;
import simcity.PersonAgent;
import simcity.interfaces.Person;
import restaurant_rancho.interfaces.Waiter;
import restaurant_rancho.gui.RestaurantRancho;

import java.util.*;
import java.util.concurrent.Semaphore;
	
	public abstract class WaiterAgent extends Agent implements Waiter{
		public List<MyCustomer> customers;
		protected String name;
		protected Semaphore atTable = new Semaphore(0,true);
		protected HostAgent host;
		protected CookAgent cook;
		public boolean isOnBreak = false;
		public RestMenu menu = new RestMenu();
		public WaiterGui waiterGui = null;
		public Cashier cashier;
		double cash;
		boolean shiftDone = false;
		RestaurantRancho restaurant;
		boolean alertedShift = false;
		boolean alert = false;
		boolean hasntLeft = true;
		
		double wage;
		
		public boolean isWorking = true;
		
		Person person;
		
		public WaiterAgent(String name, RestaurantRancho rest) {
			super();
			this.name = name;
			customers = new ArrayList<MyCustomer>();	
			restaurant = rest;
			menu = rest.getMenu();
		}
		
		public void setGui(WaiterGui wg) {
			this.waiterGui = wg;	
		}
		
		public void setHost(HostAgent host) {
			this.host = host;
		}
		public void setCook(CookAgent c) {
			cook = c;
		}
		public void setCashier(Cashier cashier) {
			this.cashier = cashier;
		}
		
		public String getMaitreDName() {
			return name;
		}
		
		public void setPerson(Person p) {
			person = p;
		}

		public String getName() {
			return name;
		}


		// Messages
		
		public void msgShiftDone(boolean alertOthers, double w) {
			shiftDone = true;
			alert = alertOthers;
			wage = w;
			stateChanged();
		}
	
		public void msgCreateCustomer(Customer c, int t, int l) {
			MyCustomer mc = new MyCustomer(c);
			mc.table = t;
			mc.cs = customerState.waiting;
			mc.loc = l;
			customers.add(mc);
			stateChanged();
		}
	 

		public void msgReadyToOrder(Customer c) {
			MyCustomer mc = findCustomer(c);
			mc.cs = customerState.readyToOrder;
			stateChanged();	
		}
		
	
		public void msgReadyForCheck(Customer c) {
			MyCustomer mc = findCustomer(c);
			mc.cs = customerState.readyForCheck;
			//Do("waiter received ready for check");
			stateChanged();
			
		}
		
		public void msgCheckReady(Customer cust, double amount){
			//print("waiter received check is ready");
			MyCustomer mc = findCustomer(cust);
			mc.amountOwed = amount;
			mc.event = customerEvent.CheckReady;
			stateChanged();
			
		}
		
	
		public void msgHereIsMyOrder(Customer c, String choice) {
			MyCustomer mc = findCustomer(c);
		    mc.choice = choice;
		    mc.order = new Order(this, choice, mc.table);
		    mc.cs = customerState.ordered;
		    stateChanged();  
		}
		
		public void msgUpdateMenu() {
			menu = restaurant.getMenu();
			for (int i = 0; i <menu.menuList.size(); i++) {
				System.out.println(menu.menuList.get(i));
			}
		}
		
		public void msgOutOfFood(Order o) {
			MyCustomer mc = findCustomer(o);
			mc.cs = customerState.readyToReOrder;
			stateChanged();
		}
		
		public void msgFoodIsReady(Order o) {
			MyCustomer mc = findCustomer(o);
			mc.event = customerEvent.FoodReady;
			stateChanged();	
		}
		
		
		public void msgLeavingTable(Customer cust) {
			MyCustomer mc = findCustomer(cust);
			mc.cs = customerState.leaving;
		    //host.msgTableIsFree(mc.table);
			stateChanged();
		}


		public void msgAtTable() {//from animation
			atTable.release();// = true;
			stateChanged();
		}

		/**
		 * Scheduler.  Determine what action is called for, and do it.
		 */
		protected boolean pickAndExecuteAnAction() {

		// rules 
			try{
			for (MyCustomer c : customers) {
				if (c.cs == customerState.leaving) {
					notifyHostFreeTable(c);
					return true;
				}
			}
			for (MyCustomer c : customers) {
				if (c.cs == customerState.waiting) {
					seatCustomer(c.c, c.table, c.loc);
					return true;
				}
			}
						
			for (MyCustomer c : customers) {
				if (c.cs == customerState.readyToOrder) {
					takeOrder(c.c);
					return true;
				}
			}
			for (MyCustomer c : customers) {
				if (c.cs == customerState.ordered) {
					dealWithOrder(c);
					return true;
				}
			}
			for (MyCustomer c : customers) {
				if (c.cs == customerState.readyToReOrder){
					reTakeOrder(c.c);
					return true;
				}
			}
			for (MyCustomer c : customers) {
				if (c.cs == customerState.readyForCheck){
					c.cs = customerState.askedForCheck;
					tellCashierMakeCheck(c);
					return true;
				}
			}
			for (MyCustomer c : customers) {
				if (c.cs == customerState.waitingForFood && c.event == customerEvent.FoodReady) {
					bringOrder(c.c);
					return true;
				}
			}
			for (MyCustomer c : customers) { 
				if (c.cs == customerState.askedForCheck && c.event == customerEvent.CheckReady) {
					giveChecktoCust(c, c.amountOwed);
					return true;
				}
			}
		
			}
			catch(ConcurrentModificationException e) {
				return false;
			}
			if (shiftDone && !alertedShift && customers.size() == 0) {leaveWork(); alertedShift = true;}
			return false;

		}

		// Actions
		
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
		protected void seatCustomer(Customer c, int table, int loc) {
			c.msgSitAtTable(this, menu);
			DoGoToCustomer(loc);
			DoSeatCustomer(c, table, loc);
			MyCustomer mc = findCustomer(c);
			mc.cs = customerState.seated;
			DoLeaveCustomer(); 
			stateChanged();
			
			
		}
		
		
		protected void takeOrder(Customer c) {
			MyCustomer mc = findCustomer(c);
			waiterGui.DoBringToTable(mc.table);
			try {
				atTable.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
			
			c.msgWhatIsYourOrder();
			print("Taking order of " + c);
			mc.cs = customerState.askedToOrder;
			if (customers.size()==1) DoLeaveCustomer();
			stateChanged();
		}
		
		protected void reTakeOrder(Customer c) {
			MyCustomer mc = findCustomer(c);
			waiterGui.DoBringToTable(mc.table);;
			try {
				atTable.acquire();
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			print("Taking reorder of " + c);
			c.msgWhatIsReorder(menu);
			mc.cs = customerState.askedToOrder; 
			if (customers.size()==1) DoLeaveCustomer();
			stateChanged();
		}
		
		protected abstract void dealWithOrder(MyCustomer c); 
		
		protected void bringOrder(Customer c) {
			print("Bringing order to " + c);
			MyCustomer mc = findCustomer(c);
			waiterGui.DoPickUpFood(mc.order.cookingNum);
			try {
				atTable.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			waiterGui.setText(mc.choice.substring(0, 3));
			cook.gui.setText("", 1, mc.order.cookingNum);
			waiterGui.DoBringToTable(mc.table);
			try {
				atTable.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
			c.msgHereIsYourFood();
			mc.cs = customerState.eating;
			waiterGui.setText("");
			DoLeaveCustomer();
			stateChanged();
		
		}
		
		protected void giveChecktoCust(MyCustomer c, double amount) {
			c.cs = customerState.paying;
			print("Giving Check to Customer");
			c.c.msgHereIsCheck(amount);
			stateChanged();
		}
		
		
		protected void tellCashierMakeCheck(MyCustomer c) {
			print("Telling cashier to compute check");
			cashier.msgComputeCheck(this, c.c, c.choice, menu);
		}
		
		protected void notifyHostFreeTable(MyCustomer c) {
			print("Notifying host of free table");
			host.msgTableIsFree(c.table);
			customers.remove(c);
		}

		protected void DoSeatCustomer(Customer customer, int table, int loc) {
			print("Seating " + customer + " at " + table);
			DoGoToCustomer(loc);
			waiterGui.DoBringToTable(table); 
			customer.getGui().DoGoToSeat(table);
			try {
				atTable.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
		}
		
		protected void DoGoToCustomer(int loc) {
			waiterGui.DoWalkToCust(loc);
			try {
				atTable.acquire();
			}
			catch (InterruptedException e) {
				
			}
		}
		
		protected void DoLeaveCustomer() {
			waiterGui.DoLeaveTable();
			try{
				atTable.acquire();
			} catch (InterruptedException e) {
			//	e.printStackTrace();
			}
		}
		
		public void release(){
			atTable.release();
		}
		
		//utilities
		protected MyCustomer findCustomer(Customer cust) {
				for (MyCustomer mc : customers) {
					if (mc.c == cust) {
						return mc;
					}
				}
				return null;
		}
		
		protected MyCustomer findCustomer(Order o) {
			for (MyCustomer mc: customers) {
				if (mc.order == o) {
					return mc;
				}
			}
			return null;
		}
		
		public int numCustomers() {
			return customers.size();
		}
		

		public enum customerEvent {Nothing, MustReorder, FoodReady, CheckReady};
		public enum customerState {doingNothing, waiting, seated, askedToOrder, 
			readyToOrder, readyToReOrder, ordered, waitingForFood, eating, readyForCheck, askedForCheck, paying, leaving};
        public class MyCustomer {
        	Customer c;
        	int table;
        	customerState cs;
        	String choice;	
        	customerEvent event;
        	Order order;
        	double amountOwed;
        	int loc;
        	MyCustomer(Customer c) {
        		this.c = c;
        		table = 0;
        		cs = customerState.doingNothing;
        		event = customerEvent.Nothing;
        		choice = "";
        		amountOwed = 0;
        		//order = null;
        	}
        	
        }
	

		
	}



