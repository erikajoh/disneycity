package restaurant_cafe;

import agent_cafe.Agent;
import restaurant_cafe.HostAgent.MyCustomer;
import restaurant_cafe.gui.Food;
import restaurant_cafe.gui.Menu;
import restaurant_cafe.gui.MenuItem;
import restaurant_cafe.interfaces.Cashier;
import restaurant_cafe.interfaces.Cook;
import restaurant_cafe.interfaces.Market;
import restaurant_cafe.test.mock.MockCashier;

import java.awt.Point;
import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Restaurant Host Agent
 */

public class MarketAgent extends Agent implements Market {
	
	public class Order {
		Cook cook;
	    MyFood food;
	    OrderState state;
	    int amountRequested;
	    int amountToGive;
		
	    public Order(Cook cp, Food f, int amt){
	    	cook = cp;
			synchronized(foods){
			  for(MyFood fd : foods){
				  if(fd.food == f){
			    	  food = fd;
			    	  break;
				  }
			  }
			}
			state = OrderState.requested;
	    	amountRequested = amt;
	    }
	};
	
	public class MyFood {
		Food food;
		 int amount;
		
	    public MyFood(Food f, int amt){
	    	food = f;
	    	amount = amt;
	    	print("NEW MYFOOD: "+f.getName() + " " + amt);
	    }
	};
	
	enum OrderState{requested, complete, done};
	
	enum CashierPaid{yes, no, broke};
	
	CashierPaid cashierPaid = CashierPaid.no;
	
	private int number; //instead of name to distinguish the market
	
	public Collection<Order> orders = Collections.synchronizedList(new ArrayList<Order>());
	public Collection<MyFood> foods =  Collections.synchronizedList(new ArrayList<MyFood>());
	private CashierAgent cashier;
	
	private String name;
	Timer timer = new Timer();
	private Menu menu;
	
	public MarketAgent(int num, Menu m, int amt) {
		super();
		number = num;
		this.name = "Market"+num;

		menu = m;
    	print(amt+" "+menu.getItems().size());
		synchronized(menu.getItems()){
    	  for(MenuItem item : menu.getItems()){
    		  foods.add(new MyFood(item.getFood(), amt));
    	  }
		}
	}

	public String getMaitreDName() {
		return name;
	}

	public String getName() {
		return name;
	}
	
	// Messages
	public void msgPaidBill(double total, boolean fullyPaid){
		//can use this msg for extra credit
		if(fullyPaid == true){
			print("Thanks for paying for the food");
			cashierPaid = CashierPaid.yes;
		}
		else {
			print("Given your recent credit score, it's ok that you didn't fully pay us today, but we expect you to make good on this tomorrow. If not we're going to have to report you.");
			cashierPaid = CashierPaid.broke;
		}
	}
	public void msgHereIsOrder(Cook c, Food f, int amt){
		print(c.getName()+" ordered "+amt+" "+f.getName()+"s");
		orders.add(new Order(c, f, amt));
		stateChanged();
	}
	public void msgFulfilledOrder(Order o){
		o.state = OrderState.complete;
		print("FOOD IS DONE");
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
		synchronized(orders){
		  for(Order order : orders){
			  if(order.state == OrderState.requested){
				  getFood(order);
				  return true;
			  }
		  }
		}
		synchronized(orders){
		  for(Order order : orders){
			  if(order.state == OrderState.complete){
				  giveFoodToCook(order);
				  return true;
			  }
		  }
		}
		if(cashierPaid != CashierPaid.no){
			clearBill();
			return true;
		}
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions
	private void getFood(final Order o){
		if(o.food.amount == 0){
			print("NO MORE "+ o.food.food.getName() + " IN Market");
			o.cook.msgOutOfFood(o.food.food, number);
			orders.remove(o);
			return;
		}
		if(o.food.amount < o.amountRequested){
			o.amountToGive = o.food.amount;
			o.food.amount = 0;
		}
		else {
			o.amountToGive = o.amountRequested;
			o.food.amount -= o.amountRequested;
		}
		print("Getting "+o.amountToGive + " " + o.food.food.getName()+"s");
		o.state = OrderState.complete;
		timer.schedule(new TimerTask() {
			public void run() {
				msgFulfilledOrder(o);
			}
		}, ((int) (Math.random() * 4000)+4000)); 
	}
	private void giveFoodToCook(Order o){
		o.cook.msgFulfilledOrder(o.food.food, o.amountToGive);
		double total = 0.5*(o.food.food.getPrice()*o.amountToGive);
		total = Math.round(total * 100.0) / 100.0;
		cashier.msgBillFromMarket(this, total);
		orders.remove(o);
	}
	private void clearBill(){
		  if(cashierPaid == CashierPaid.yes){
		    cashier.msgClearBill(this, true);
		  }
		  else  {
		    cashier.msgClearBill(this, false);	
		  }
		
		cashierPaid = CashierPaid.no;
	}
	public void setStock(Food f, int amt){
		synchronized(foods){
		  for(MyFood food : foods){
			  if(f == food.food){
				  food.amount = amt;
			  }
		  }
		}
	}
	
	public void setAllStockToAmt(int amt){
		synchronized(foods){
		  for(MyFood food : foods){
			  food.amount = amt;
		  }
		}
		print("MARKET EMPTY");
	}
	
	public void setCashier(CashierAgent c){
		cashier = c;
	}
	public int getNumber(){
		return number;
	}
	public int getFoodAmount(Food f){
		int amount = 0;
		  synchronized(foods){
			for(MyFood food : foods){
				if(f == food.food){
					amount = f.getAmount(); break;
				}
			}
		  }
	   return amount;
	}
}

