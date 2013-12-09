package restaurant_cafe;

import agent_cafe.Agent;
import restaurant_cafe.HostAgent.CustomerState;
import restaurant_cafe.HostAgent.MyCustomer;
import restaurant_cafe.gui.Check;
import restaurant_cafe.gui.CookGui;
import restaurant_cafe.gui.Food;
import restaurant_cafe.gui.Menu;
import restaurant_cafe.gui.MenuItem;
import restaurant_cafe.gui.WaiterGui;
import restaurant_cafe.interfaces.Cashier;
import restaurant_cafe.interfaces.Customer;
import restaurant_cafe.interfaces.Waiter;
import simcity.PersonAgent;
import simcity.interfaces.Person;

import java.awt.Point;
import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Restaurant Host Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the WaiterAgent. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class WaiterAgent extends Agent implements Waiter {
	static final int NTABLES = 3;//a global for the number of tables.
	//Notice that we implement waitingCustomers using ArrayList, but type it
	//with List semantics.
	class MyCustomer {
		Customer customer;
		int number;
		Table table;
		String choice;
		Check check;
		CustomerState state;
		public MyCustomer(Customer c, int tableNum, CustomerState s){
			customer = c;
			for(Table t : tables){
				if(t.tableNumber == tableNum){
					table = t;
				}
			}
			state = s;
		}
	}
	Person person;
	enum CustomerState{idle, waiting, readyToSit, readyToOrder, reorder, ordering, ordered, foodCooking, foodReady, foodBeingDelivered, produceCheck, gettingCheck, givingCheck, paying, eating, doneEating};
	public List<MyCustomer> customers = Collections.synchronizedList(new ArrayList<MyCustomer>());
	boolean shouldReturnToHome = false; //necessary boolean (independent of CustomerStates) that greatly increases waiter's efficiency. If the waiter doesn't have to go anywhere else it then leaves the customer.
	boolean animating = false; 
	enum BreakState{none, goOnBreak, goOffBreak};
	BreakState breakState = BreakState.none;
	boolean shiftDone = false;
	
	public Collection<Table> tables;
	//note that tables is typed with Collection semantics.
	//Later we will see how it is implemented
	
	Menu menu; //current menu. Out of stock foods taken off
	private String name;
	private Semaphore atTable = new Semaphore(0,true);
	
	public HostAgent host;
	public CookAgent cook;
	public Cashier cashier;
	public int number;

	public WaiterGui waiterGui;
	public CookGui cookGui;

	public WaiterAgent(String name, Menu m, int num) {
		super();
		this.name = name;
		number = num;
		tables = new ArrayList<Table>(NTABLES);
		for (int ix = 1; ix <= NTABLES; ix++) {
			tables.add(new Table(ix));//how you add to a collections
		}
		menu = m;
	}
	
	public void setPerson(Person p) {
		person = p;
	}

	public String getMaitreDName() {
		return name;
	}

	public String getName() {
		return name;
	}

	public List<MyCustomer> getCustomers() {
		return customers;
	}

	public Collection<Table> getTables() {
		return tables;
	}
	// Messages
	
	public void msgGoOnBreak(){
		breakState = BreakState.goOnBreak;
		stateChanged();
	}
	
	public void msgEndBreak(){
		breakState = BreakState.goOffBreak;
		stateChanged();
	}
	
	public void msgShiftDone() {
		shiftDone = true;
		if (customers.size() == 0) {
			print ("going home!");
			waiterGui.DoLeave(person);
			if (cook!=null) { 
				cook.msgShiftDone(); 
				if (cashier!=null) cashier.subtract(10); 
			}
			if (host!=null) { 
				if (cashier!=null) cashier.subtract(10); 
			}
			if (cashier!=null) { 
				cashier.msgShiftDone(); 
				cashier.subtract(20);
			}
		}
		else {
			print("my shift is done! but I still have customers");
		}
	}

	public void msgPleaseSeatCustomer(Customer cust, int tableNum) {
		//print("Adding cust "+cust.getName() + " to list");
		print("Cafe seat customer");
		customers.add(new MyCustomer(cust, tableNum, CustomerState.waiting));
		stateChanged();
	}
	
	public void msgLeaveCustomer(){
		shouldReturnToHome = true;
		stateChanged();
	}
	
	public void msgOutOfFood(Food f){
		MyCustomer mc = null;
		synchronized(customers){
		  for(MyCustomer customer : customers){
			  if(customer.state == CustomerState.idle && customer.choice.equals(f.getName())){
			      mc = customer;
				  print("Out of "+f.getName());
				  mc.state = CustomerState.reorder;
			  }
		  }
		}
		synchronized(menu.getItems()){
		  for(MenuItem item : menu.getItems()){
			  if(item.getName().equals(f.getName())){
				  item.setAvailability(false);
				  print("REMOVING "+item.getFood().getName());
				  menu.removeItem(item);
				  break;
			  }
		  }
		}
		stateChanged();
	}
	
	public void msgReadyToOrder(Customer cust){
		MyCustomer mc = null;
		synchronized(customers){
		  for(MyCustomer customer : customers){
			  if(customer.customer == cust){
				  print("FOUND");
				  mc = customer;
				  break;
			  }
		  }
		}
		mc.state = CustomerState.readyToOrder;
		stateChanged();
	}
	
	public void msgHereIsMyOrder(Customer cust, String choice){
		MyCustomer mc = null;
		synchronized(customers){
		  for(MyCustomer customer : customers){
			  if(customer.customer == cust){
				mc = customer;
				break;
			  }
		  }
		}
		mc.state = CustomerState.ordered;
		mc.choice = choice;
		print("GOT ORDER of "+choice);
		stateChanged();
	}
	
	public void msgOrderDone(String choice, int table){
		MyCustomer mc = null;
		synchronized(customers){
		  for(MyCustomer customer : customers){
			  if(customer.table.tableNumber == table){
				  mc = customer;
				  break;
			  }
		  }
		}
		mc.state = CustomerState.foodReady;
		stateChanged();
	}
	
	public void msgDoneEating(Customer cust){
		MyCustomer mc = null;
		synchronized(customers){
		  for(MyCustomer customer : customers){
			  if(customer.customer == cust){
				  mc = customer;
				  break;
			  }
		  }
		}
		mc.state = CustomerState.doneEating;
		for (Table table : tables) {
			if (table.tableNumber == mc.table.tableNumber) {
				print(cust + " leaving " + table);
				table.setUnoccupied();
				stateChanged();
			}
		}
	}
	
	
	public void msgHereIsCheck(Customer cust, Check check){
		MyCustomer customer = null;
		synchronized(customers){
		  for(MyCustomer mc : customers){
			  if(mc.customer == cust){
				  customer = mc;
				  break;
			  }
		  }
		}
		customer.check = check;
	}
	
	public void msgAtHost(){
		print("AT HOST");
		animating = false;
		stateChanged();
	}

	public void msgAtTable(int tableDestination) {//from animation
		atTable.release();// = true;
		animating = false;
		stateChanged();
	}
	
	public void msgAtCook(){
		print("AT COOK2");
		shouldReturnToHome = true;
		animating = false;
		stateChanged();
	}
	
	public void msgAtCashier(){
		print("AT CASHIER2");
		animating = false;
		stateChanged();
	}
	
	public void msgCustomerLeaving(Customer cust){
		synchronized(customers){
		  for(MyCustomer customer : customers){
			  if(customer.customer == cust){
				  customer.state = CustomerState.doneEating;	
				  break;
			  }
		  }
		}
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		/* Think of this next rule as:
            Does there exist a table and customer,
            so that table is unoccupied and customer is waiting.
            If so seat him at the table.
		 */
		if(breakState == BreakState.goOnBreak){
			breakState = BreakState.none;
			tryToBreak();
		}
		else if(breakState == BreakState.goOffBreak){
			breakState = BreakState.none;
			endBreak();
		}
		try {
		  if(animating == false){
		  for(MyCustomer customer : customers){
			  if(customer.state == CustomerState.ordered){
			      print("Bringing order to cook");
			      bringOrderToCook(customer);
				  return true;
			  }
		   }
		  for(MyCustomer customer : customers){
			   if(customer.state == CustomerState.ordering){
				  print("Get order");
				  getOrder(customer);
				  return true;
			   }
		   }
		
		   for(MyCustomer customer : customers){
			   if(customer.state == CustomerState.readyToSit){
				   print("Seat Customer");
				   seatCustomer(customer);
				   return true;
			   }
		   }
		   
		  for(MyCustomer customer : customers){
			    if(customer.state == CustomerState.eating){
				   print("GIVING Order");
				   giveFood(customer);
				   return true;
			    }
		  }
		  for(MyCustomer customer : customers){
				if(customer.state == CustomerState.reorder){
					tellCustomerToReorder(customer);
					return true;
			    }
		   }
		  for(MyCustomer customer : customers){
			    if(customer.state == CustomerState.foodCooking){
				     print("Giving order to cook");
				     giveOrderToCook(customer);
				     return true;
			    }
		   }
		  for(MyCustomer customer : customers){
			    if(customer.state == CustomerState.foodReady){
				    print("Getting food to cook");
				    getOrderFromCook(customer);
				    return true;
			    }
	    	}
		
		  for(MyCustomer customer : customers){
			    if(customer.state == CustomerState.foodBeingDelivered){
				    print("Taking order to customer");
				    takeFoodToTable(customer.choice, customer.table.tableNumber);
				    return true;
			    }
		  }
		  for(MyCustomer customer : customers){
			    if(customer.state == CustomerState.paying){
				    giveCheck(customer);
				    return true;
			    }
		   }
		  for(MyCustomer customer : customers){
			    if(customer.state == CustomerState.givingCheck){
				    print("Giving check to customer");
				    goToGiveCheck(customer);
				    return true;
			     }
		  }
		  for(MyCustomer customer : customers){
			    if(customer.state == CustomerState.produceCheck){
				    print("Go to cashier");
				    produceCheck(customer);
				    return true;
			    }
		  } 
		  for(MyCustomer customer : customers){
			    if(customer.state == CustomerState.gettingCheck){
				    print("Getting Check");
				    getCheck(customer);
				    return true;
			    }
		  }
		  for(MyCustomer customer : customers){
			   if(customer.state == CustomerState.doneEating){
				   	print("Table is empty");
					tellHostTableEmpty(customer);
					return true;
				}
			}
		   for(MyCustomer customer : customers){
			   if(customer.state == CustomerState.waiting){
				   print("Going to seat "+customer.customer.toString());
				   DoGoToHost(customer);
				   return true;
			   }
		   }
		  for(MyCustomer customer : customers){
			  if(customer.state == CustomerState.readyToOrder){
				  print("Go To Take Order");
				  goToTakeOrder(customer);
				  return true;
			  }
		  }
		  if(shouldReturnToHome == true){
			  returnToHome();
		  }
		  }
		}
		catch(ConcurrentModificationException e) {
			print("CAUGHT a concurrent modification exception in the waiter thread");
			//optional: e.printStackTrace();
			return false;
		}
		if (shiftDone) {msgShiftDone();}
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions
	
	private void tryToBreak(){
		host.msgIWantToBreak(this);
	}
	
	private void endBreak(){
		if(waiterGui.isOnBreak()){
			waiterGui.endBreak();
		}
		else {
			host.msgFinishedBreak(this);
		}
	}
	
	private void tellCustomerToReorder(MyCustomer c){
		c.state = CustomerState.idle;
		c.customer.msgReorder();
		DoGoToTable(c.table.tableNumber);
	}
	private void goToTakeOrder(MyCustomer c){
		print(c.customer.toString()+"'s state being changed from "+c.state+ " to idle");
		print("GOING TO GET ORDER FROM TABLE # " + c.table.tableNumber);
		c.state = CustomerState.ordering;
		DoGoToTable(c.table.tableNumber);
	}
	private void getOrder(MyCustomer customer){
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		customer.customer.msgAskOrder();
	}
	private void bringOrderToCook(MyCustomer customer){
		print("Bring order to cook");
		customer.state = CustomerState.foodCooking;
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		animating = true;
		shouldReturnToHome = false;
		waiterGui.DoGoToCook();
	}
	private void getOrderFromCook(MyCustomer customer){
		 animating = true;
		 customer.state = CustomerState.foodBeingDelivered;
		 shouldReturnToHome = false;
		 waiterGui.DoGoToCook();
	}
	private void takeFoodToTable(String c, int tableNum){
		cookGui.RemovePlate();
		MyCustomer mc = null;
		for(MyCustomer customer : customers){
			if(customer.table.tableNumber == tableNum){
				mc = customer;
				mc.state = CustomerState.eating;
				break;
			}
		}
		waiterGui.setOrderString(mc.choice);
		animating = true;
		shouldReturnToHome = false;
		waiterGui.DoGoToTable(tableNum);
	}
	
	private void giveFood(MyCustomer mc){
		waiterGui.setOrderString("");
		mc.customer.msgHereIsFood();
		mc.state = CustomerState.produceCheck;
		animating = true;
		print("ANIMATING 4");
		shouldReturnToHome = true;
	}
	
	private void produceCheck(MyCustomer customer){
		customer.state = CustomerState.gettingCheck;
		cashier.msgProduceCheck(customer.customer, this, customer.choice);
		animating = true;
		shouldReturnToHome = false;
		waiterGui.DoGoToCashier();
	}
	
	private void getCheck(MyCustomer customer){
		cashier.msgWaiterHere(customer.customer);
		customer.state = CustomerState.givingCheck;
	}
	
	private void goToGiveCheck(MyCustomer customer){
		animating = true;
		shouldReturnToHome = false;
		customer.state = CustomerState.paying;
		waiterGui.DoGoToTable(customer.table.tableNumber);
	}
	
	private void giveCheck(MyCustomer customer){
		customer.state = CustomerState.idle;
		customer.customer.msgHereIsCheck(customer.check);
		shouldReturnToHome = true;
	}
	
	private void returnToHome(){
		animating = true;
		shouldReturnToHome = false;
		waiterGui.DoLeaveCustomer(number);
	}

	private void tellHostTableEmpty(MyCustomer cust){
		host.msgTableAvailable(cust.customer);
		customers.remove(cust);
		cust.state = CustomerState.idle;
	}

	private void seatCustomer(MyCustomer customer) {
		DoSeatCustomer(customer.customer, customer.table);
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		customer.table.setOccupant(customer.customer);
		customer.state = CustomerState.idle;
	}

	// The animation DoXYZ() routines
	private void DoSeatCustomer(Customer customer, Table table) {
		//Notice how we print "customer" directly. It's toString method will do it.
		//Same with "table"
		print("Seating " + customer + " at " + table);
		animating = true;
		shouldReturnToHome = false;
		waiterGui.DoGoToTable(table.tableNumber); 
		Menu menuCopy = new Menu(menu.getFoods()); //used to prevent clobbering
		customer.msgFollowMe(this, table.tableNumber, menuCopy);
	}
	
	private void DoGoToHost(MyCustomer c){
		animating = true;
		shouldReturnToHome = false;
		int num = c.customer.getNumber();
		waiterGui.DoGoToHost(num); 
		c.state = CustomerState.readyToSit;
	}
	
	private void giveOrderToCook(MyCustomer customer){
		cook.msgHereIsOrder(this, customer.choice, customer.table.tableNumber);
		customer.state = CustomerState.idle;
	}
	
	private void DoGoToTable(int table){
		//gui stuff
		for(MyCustomer customer : customers){
			if(customer.table.tableNumber == table){
				print("GOING TO TABLE # "+table);
				animating = true;
				shouldReturnToHome = false;
				waiterGui.DoGoToTable(table);
				break;
			}
		}
	}

	//utilities
	public void setCook(CookAgent c){
		cook = c;
		cookGui = c.getGui();
	}
	public void setHost(HostAgent h){
		host = h;
	}
	public void setCashier(CashierAgent c){
		cashier = c;
	}
	
	public CookAgent getCook(){
		return cook;
	}
	
	public void setGui(WaiterGui gui) {
		waiterGui = gui;
	}

	public WaiterGui getGui() {
		return waiterGui;
	}
	
	private static class Table {
		Customer occupiedBy;
		int tableNumber;
		
		Table(int tableNumber) {
			this.tableNumber = tableNumber;
		}

		void setOccupant(Customer cust) {
			occupiedBy = cust;
		}

		void setUnoccupied() {
			occupiedBy = null;
		}

		Customer getOccupant() {
			return occupiedBy;
		}

		boolean isOccupied() {
			return occupiedBy != null;
		}

		public String toString() {
			return "table " + tableNumber;
		}
	}
}

