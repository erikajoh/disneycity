

package restaurant_rancho;

import agent_rancho.Agent;
import restaurant_rancho.RestMenu;
import restaurant_rancho.gui.WaiterGui;
import restaurant_rancho.interfaces.Cashier;
import restaurant_rancho.interfaces.Customer;
import restaurant_rancho.interfaces.Person;
import restaurant_rancho.interfaces.Waiter;

import java.util.*;
import java.util.concurrent.Semaphore;

	/**
	 * Restaurant Host Agent
	 */
	
		public class WaiterAgent extends Agent implements Waiter{
		public List<MyCustomer> customers;
		private String name;
		private Semaphore atTable = new Semaphore(0,true);
		private HostAgent host;
		private CookAgent cook;
		public boolean isOnBreak = false;
		public RestMenu menu = new RestMenu();
		public WaiterGui waiterGui = null;
		public Cashier cashier;
		double cash;
		
		Person person;
		
		public WaiterAgent(String name) {
			super();
			this.name = name;
			customers = new ArrayList<MyCustomer>();	
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
		
		public void msgUpdateMenu(List<String> foodsUnavailable) {
			menu.replenish();
			for (String food : foodsUnavailable ) {
				menu.removeItem(food);
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
					talkToCook(c);
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
			return false;

		}

		// Actions

		private void seatCustomer(Customer c, int table, int loc) {
			c.msgSitAtTable(this, menu);
			DoGoToCustomer(loc);
			DoSeatCustomer(c, table, loc);
			MyCustomer mc = findCustomer(c);
			mc.cs = customerState.seated;
			DoLeaveCustomer(); 
			stateChanged();
			
			
		}
		
		
		private void takeOrder(Customer c) {
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
		
		private void reTakeOrder(Customer c) {
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
		
		private void talkToCook(MyCustomer c) {
			c.cs = customerState.waitingForFood;
			waiterGui.DoGoToCook();
			try{ 
				atTable.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			print("Telling cook to make " + c.choice + " for " + c.c.getName());
			cook.msgAddOrder(c.order);
			DoLeaveCustomer();
			stateChanged();
		}
		
		private void bringOrder(Customer c) {
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
		
		private void giveChecktoCust(MyCustomer c, double amount) {
			c.cs = customerState.paying;
			print("Giving Check to Customer");
			c.c.msgHereIsCheck(amount);
			stateChanged();
		}
		
		
		private void tellCashierMakeCheck(MyCustomer c) {
			print("Telling cashier to compute check");
			cashier.msgComputeCheck(this, c.c, c.choice);
		}
		
		private void notifyHostFreeTable(MyCustomer c) {
			print("Notifying host of free table");
			host.msgTableIsFree(c.table);
			customers.remove(c);
		}

		private void DoSeatCustomer(Customer customer, int table, int loc) {
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
		
		private void DoGoToCustomer(int loc) {
			waiterGui.DoWalkToCust(loc);
			try {
				atTable.acquire();
			}
			catch (InterruptedException e) {
				
			}
		}
		
		private void DoLeaveCustomer() {
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
		private MyCustomer findCustomer(Customer cust) {
				for (MyCustomer mc : customers) {
					if (mc.c == cust) {
						return mc;
					}
				}
				return null;
		}
		
		private MyCustomer findCustomer(Order o) {
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



