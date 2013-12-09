package restaurant_cafe;

import agent_cafe.Agent;
import restaurant_cafe.CustomerAgent.AgentEvent;
import restaurant_cafe.WaiterAgent.CustomerState;
import restaurant_cafe.WaiterAgent.MyCustomer;
import restaurant_cafe.gui.CashierGui;
import restaurant_cafe.gui.Check;
import restaurant_cafe.gui.Food;
import restaurant_cafe.gui.HostGui;
import restaurant_cafe.gui.Menu;
import restaurant_cafe.gui.MenuItem;
import restaurant_cafe.interfaces.Cashier;
import restaurant_cafe.interfaces.Customer;
import restaurant_cafe.interfaces.Market;
import restaurant_cafe.interfaces.Waiter;
import simcity.PersonAgent;
import simcity.interfaces.Person;

import java.awt.Point;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Restaurant Host Agent
 */

public class CashierAgent extends Agent implements Cashier {
	static final int NTABLES = 3; //a global for the number of tables.
	
	public class MyCustomer {
		Customer customer;
		String choice;
		Check check;
		double amountPaid;
		Waiter waiter;
		CustomerState state;
		public MyCustomer(Customer cust, Waiter w, String c){
			customer = cust;
			waiter = w;
			choice = c;
			state = CustomerState.producingCheck;
		}
	}
	enum CustomerState{idle, producingCheck, checkReady, giveCheck, paying, makeChange, giveChange, noMoney};
	Person person;
	public List<MyCustomer> customers = Collections.synchronizedList(new ArrayList<MyCustomer>());
	public Collection<Food> foods;
	boolean shiftDone = false;
	
	private double balance = 100.00;
	
	public class Bill {
		Market market;
		double total;
		boolean paid;
		
		public Bill(Market m, double t){
			market = m;
			total = t;
			paid = false;
		}
	}
	Bill clearBill = null;
	public List<Bill> bills = Collections.synchronizedList(new ArrayList<Bill>());
	
	Menu menu;

	public CashierGui cashierGui = null;

	//note that tables is typed with Collection semantics.
	//Later we will see how it is implemented
	private String name;
	Timer timer = new Timer();
	
	public CashierAgent(String name, Menu m) {
		super();

		this.name = name;
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
	
	
	
	// Messages
	
	public void msgShiftDone() {
		shiftDone = true;
		if (!pickAndExecuteAnAction()) {if (person!=null) person.msgStopWork(10); print("cashier going home");}
	}
	
	public void msgBillFromMarket(Market market, double total){
		bills.add(new Bill(market, total));
		print("BILL $"+total+" FROM "+market.getName());
		stateChanged();
	}
	
	public void msgClearBill(Market market, boolean accepted){
		synchronized(bills){
		for(Bill bill : bills){
			if(bill.market == market){
				clearBill = bill; break;
			}
		}
		}
	   if(accepted == false){
			print("Ok I understand. I'll let my manager know.");
		}
		stateChanged();
	}
	
	public void msgProduceCheck(Customer c, Waiter w, String choice){
		MyCustomer mc = new MyCustomer(c, w, choice);
		customers.add(mc);
		stateChanged();
	}
	
	public void msgWaiterHere(Customer customer){
		MyCustomer mc = null;
		synchronized(customers){
			for(MyCustomer cust : customers){
				if(cust.customer == customer){
					mc = cust;
					break;
				}
			}
		}
		mc.state = CustomerState.giveCheck;
		stateChanged();
	}
	
	public void msgHereIsPayment(Customer cust, double cash){
		MyCustomer customer = null;
		synchronized(customers){
			for(MyCustomer mc : customers){
				if(mc.customer == cust){
					customer = mc;
					break;
				}
			}
		}
		print("Received $"+cash);
		customer.state = CustomerState.makeChange;
		customer.amountPaid = cash;
		stateChanged();
	}
	
	public void msgCheckReady(MyCustomer customer){
		print("FOOD IS DONE");
		customer.state = CustomerState.checkReady;
		stateChanged();
	}
	
	public void msgNoMoney(Customer c){
		MyCustomer customer = null;
		synchronized(customers){
			for(MyCustomer mc : customers){
				if(mc.customer == c){
					customer = mc;
					break;
				}
			}
		}
		customer.state = CustomerState.noMoney;
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
			  if(customer.state == CustomerState.producingCheck){
				  produceCheck(customer);
				  return true;
			  }
		  }
		}
		synchronized(customers){
		  for(MyCustomer customer : customers){
			  if(customer.state == CustomerState.checkReady){
				  checkReady(customer);
				  return true;
			   }
		  }
		}
		synchronized(customers){
		  for(MyCustomer customer : customers){
			  if(customer.state == CustomerState.makeChange){
				  makeChange(customer);
				  return true;
			  }
		  }
		}
		synchronized(customers){
		  for(MyCustomer customer : customers){
			  if(customer.state == CustomerState.giveCheck){
				  giveCheckToWaiter(customer);
				  return true;
			  }
		  }
		}
		synchronized(customers){
		  for(MyCustomer customer : customers){
			  if(customer.state == CustomerState.noMoney){
				  sendCustomerToCook(customer);
				  return true;
			  }
		  }
		}
		synchronized(bills){
			boolean billPaid = false; //necessary for cashier to be able to pay multiple bills at once and still return true
			  for(Bill bill : bills){
				  if(bill.paid == false) {
					payBill(bill);
				    billPaid = true;
			      }
			  }
			  if(billPaid == true){
				  return true;
			  }
		}
		if(clearBill != null){
			clearBill();
			return true;
		}
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions
	private void produceCheck(final MyCustomer customer){
		double price = 0;
		synchronized(menu.getItems()){
		  for(MenuItem item : menu.getItems()){
			  if(item.getName().equals(customer.choice)){
				  price = Math.round(item.getPrice() * 100.0) / 100.0;
				  break;
			  }
	     	}
		}
		
		customer.state = CustomerState.idle;
		customer.check = new Check(customer.choice, price);
		
		timer.schedule(new TimerTask() {
			public void run() {
				print("Creating check");				
				msgCheckReady(customer);
			}
		}, 1000);
	}
	
	private void makeChange(final MyCustomer customer){
		customer.state = CustomerState.idle;
		final double change = customer.amountPaid - customer.check.getTotal();
		timer.schedule(new TimerTask() {
			public void run() {
				print("Change created");				
				customer.customer.msgHereIsChange(change);
			}
		}, 1500);
	}
	
	private void checkReady(MyCustomer customer){
		customer.state = CustomerState.idle;
	}

	private void giveCheckToWaiter(MyCustomer customer){
		customer.waiter.msgHereIsCheck(customer.customer, customer.check);
		customer.state = CustomerState.idle;
	}
	private void payBill(Bill bill){
	  if(balance >= bill.total){
		  balance-= bill.total;  
		  bill.market.msgPaidBill(bill.total, true);
	  }
	  else {
		  print("I don't have enough money to pay the market");
		  bill.market.msgPaidBill(balance, false);
		  balance = 0.00;
	  }
	  bill.paid = true;
}
	
	private void clearBill(){
		bills.remove(clearBill);
		clearBill = null;
	}
	
	private void sendCustomerToCook(MyCustomer customer){
		print("If you don't want me to call the cops, go to the kitchen to clean dishes...");
		customer.customer.msgCleanDishes(); 
		customer.state = CustomerState.idle;
	}
	
	public void setBalance(double b){
		balance = b;
	}
	
	//utilities
		public void setGui(CashierGui gui) {
			cashierGui = gui;
		}

		public CashierGui getGui() {
			return cashierGui;
		}

		public void subtract(double amount) {
			balance -= amount;
		}
}


