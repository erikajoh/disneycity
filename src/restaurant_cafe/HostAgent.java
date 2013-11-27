package restaurant_cafe;

import agent_cafe.Agent;
import restaurant_cafe.HostAgent.MyCustomer;
import restaurant_cafe.HostAgent.Table;
import restaurant_cafe.gui.HostGui;
import restaurant_cafe.interfaces.Customer;
import restaurant_cafe.interfaces.Host;
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
//the HostAgent. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class HostAgent extends Agent implements Host {
	static final int NTABLES = 3;
	
	class MyCustomer {
		Customer customer;
		CustomerState state;
		public MyCustomer(Customer c, CustomerState s){
			customer = c;
			state = s;
		}
	}
	Person person;
	enum CustomerState{waiting, eating, done};
	public List<MyCustomer> customers = Collections.synchronizedList(new ArrayList<MyCustomer>());
	class MyWaiter {
		Waiter waiter;
		WaiterState state;
		int customers;
		public MyWaiter(Waiter w, WaiterState s){
			waiter = w;
			state = s;
			customers = 0;
		}
	}
	enum WaiterState{active, onBreak};
	public List<MyWaiter> waiters = Collections.synchronizedList(new ArrayList<MyWaiter>());
	public Collection<Table> tables;
	
	private String name;
	private Semaphore atTable = new Semaphore(0,true);
	

	public HostGui hostGui = null;

	public HostAgent(String name) {
		super();

		this.name = name;

		tables = Collections.synchronizedList(new ArrayList<Table>(NTABLES));
		for (int ix = 1; ix <= NTABLES; ix++) {
			tables.add(new Table(ix));
			}
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

	public void msgIWantFood(Customer cust) {
		customers.add(new MyCustomer(cust, CustomerState.waiting));
		print(cust.toString() + " wants to get a table "+waiters.size());
		stateChanged();
	}
	
	public void msgIWantToBreak(Waiter w){
		boolean activeWaiter = false;
		if(waiters.size() > 1){
			synchronized(waiters){
			  for(MyWaiter waiter : waiters){
				  if(activeWaiter == false && waiter.state == WaiterState.active){
					  activeWaiter = true;
				  }
				  else if(activeWaiter = true && waiter.waiter == w){
					  waiter.state = WaiterState.onBreak;
					  return;
				  }
			  }	
			}
		}
		w.msgEndBreak();
	}
	
	public void msgFinishedBreak(Waiter w){
		synchronized(waiters){
		  for(MyWaiter waiter : waiters){
			  if(waiter.waiter == w){
				  waiter.state = WaiterState.active;
			  }
		  }	
		}
	}
	
	public void msgTableAvailable(Customer cust) {
		synchronized(customers){
		  for(MyCustomer customer : customers){
			  if(customer.customer == cust){
				  customer.state = CustomerState.done;
			  }
		  }
		}
		synchronized(tables){
		  for (Table table : tables) {
			  if (table.getOccupant() == cust) {
				  print(cust + " leaving " + table);
				  table.setUnoccupied();
			  }
		  }
		}
		stateChanged();
	}
	
	public void msgCustomerLeaving(Customer cust) {
		synchronized(customers){
		  for(MyCustomer customer : customers){
			  if(customer.customer == cust){
				  customers.remove(customer);
				  break;
			  }
		  }
		}
		stateChanged();
	}

	public void msgAtTable() {
		atTable.release();
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
		synchronized(customers){
		for(MyCustomer customer : customers){
			if(customer.state == CustomerState.waiting){
			    for (Table table : tables) {
			    	if (!table.isOccupied() && waiters.size()>0) {
			    		MyWaiter waiter = getLeastBusyWaiter();
			    		//print("Assigning "+ waiter.waiter.getName() + " to " + customer.customer.toString());
						assignWaiter(customer, waiter, table);
						return true;//return true to the abstract agent to reinvoke the scheduler.
					}
			    }
			    if(waiters.size()>0){
			    	restaurantFull(customer);
			    	print(customer.customer + " not assigned table");
			    }
			}
		}
		}
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions
	private void assignWaiter(MyCustomer c, MyWaiter w, Table t){
		print(c.customer + " assigned");
		w.waiter.msgPleaseSeatCustomer(c.customer, t.tableNumber);
		w.customers++;
		c.state = CustomerState.eating;
		t.setOccupant(c.customer);
	}
	
	private void restaurantFull(MyCustomer c){
		print(c.customer + " assigned");
		c.customer.msgRestaurantFull();
	}
	
	public MyWaiter getLeastBusyWaiter(){
		MyWaiter waiter = null;
		synchronized(waiters){
		  for(MyWaiter w : waiters){
			  if(w.state == WaiterState.active){
				  waiter = w; break;
			  }
		  }
		}
		synchronized(waiters){
		  for(MyWaiter w : waiters){
			  if(w.state == WaiterState.active && w.customers < waiter.customers){
				  waiter = w;
			  }
		  }
		}
		return waiter;
	}
	
	public void addWaiter(WaiterAgent w){
		waiters.add(new MyWaiter(w, WaiterState.active));
		if(w.getName().equals("onBreak")){
			msgIWantToBreak(w);
		}
		print(w.getName() + " added to waiters");
		stateChanged();
	}

	//utilities
	public void setGui(HostGui gui) {
		hostGui = gui;
	}

	public HostGui getGui() {
		return hostGui;
	}
	
	public static class Table {
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

