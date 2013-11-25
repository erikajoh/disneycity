package restaurant_cafe;

import agent_cafe.Agent;
import restaurant_cafe.CashierAgent.MyCustomer;
import restaurant_cafe.CookAgent.Table;
import restaurant_cafe.CustomerAgent.AgentEvent;
import restaurant_cafe.gui.CookGui;
import restaurant_cafe.gui.Food;
import restaurant_cafe.gui.HostGui;
import restaurant_cafe.interfaces.Cook;
import restaurant_cafe.interfaces.Customer;
import restaurant_cafe.interfaces.Market;
import restaurant_cafe.interfaces.Waiter;

import java.awt.Point;
import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Restaurant Host Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the CookAgent. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class CookAgent extends Agent implements Cook {
	static final int NTABLES = 3;//a global for the number of tables.
	
	public class Order {
		Waiter waiter;
		Food food;
		int exclude = 0; //which markets already ordered from for order
		int table;
	    OrderState s;
	    public Order(Waiter wp, String c, int t){
	    	waiter = wp;
			synchronized(foods){
	    	  for(Food f : foods){
	    		  if(f.getName().equals(c)){
	    			  food = f;
	    			  break;
	    		  }
	    	  }
			}
	    	table = t;
	    	s = OrderState.pending;
	    }
	};
	enum OrderState{pending, cooking, reorder, done};
	public Collection<Order> orders = Collections.synchronizedList(new ArrayList<Order>());
	public Collection<Market> markets = Collections.synchronizedList(new ArrayList<Market>());
	public Collection<Table> tables;
	public Collection<Food> foods;
	private CookGui cookGui;
	
	//note that tables is typed with Collection semantics.
	//Later we will see how it is implemented
	private String name;
	Timer timer = new Timer();
	
	public CookAgent(String name, Collection<Food> fds) {
		super();

		this.name = name;
		// make some tables
		tables = Collections.synchronizedList(new ArrayList<Table>(NTABLES));
		synchronized(tables){
		  for (int ix = 1; ix <= NTABLES; ix++) {
			  tables.add(new Table(ix));//how you add to a collections
		  }
		}
		foods = fds;
	}

	public String getMaitreDName() {
		return name;
	}

	public String getName() {
		return name;
	}

	public Collection<Table> getTables() {
		return tables;
	}
	
	// Messages
	public void msgHereIsOrder(Waiter w, String choice, Integer table){
		print("table "+table+" ordered "+choice);
		orders.add(new Order(w, choice, (int)table));
		stateChanged();
	}
	public void msgFulfilledOrder(Food food, int amount){
		print("GOT " + amount + " MORE "+food.getName()+ "s");
		food.setAmount(food.getAmount()+amount);
		food.setOrderAttempts(0);
	}
	public void msgOutOfFood(Food f, int ex){
	    synchronized(orders){
		  for(Order order : orders){
			  if(order.food == f){
				  if(order.s != OrderState.done){
					  order.s = OrderState.reorder;
					  order.exclude = ex;
				  }
			  }
		  }
	    }
		stateChanged();
	}
	public void msgFoodDone(Order o){
		o.s = OrderState.done;
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
			  if(order.s == OrderState.done){
				  plateIt(order);
				  return true;
			  }
		  }
		}
		synchronized(orders){
		  for(Order order : orders){
			  if(order.s == OrderState.pending){
				  cookIt(order);
				  return true;
			  }
		  }
		}
		synchronized(orders){
		  for(Order order : orders){
			  if(order.s == OrderState.reorder && order.food.getOrderAttempts()<=markets.size()){
				  makeOrder(order);
				  return true;
			  }
		  }
		}
		
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions
	private void cookIt(final Order o){
		print("COOK " + o.food.getName() + " " + o.food.getAmount());
		cookGui.DoGrilling(o.food.getName());
		if(o.food.getAmount() == 0){
			print("NO MORE "+ o.food.getName() + " TO COOK");
			o.waiter.msgOutOfFood(o.food);
			o.s = OrderState.reorder;
			return;
		}
		o.food.decreaseAmount();
		if(o.food.getAmount() <= o.food.getLowAmt()){
			o.exclude = -1;
			makeOrder(o);
		}
		o.s = OrderState.cooking;
		timer.schedule(new TimerTask() {
			public void run() {
				msgFoodDone(o);
				cookGui.DoneGrilling();
			}
		}, o.food.getCookingTime());
	}
	
	private void makeOrder(Order o){
		o.food.setOrderAttempts(o.food.getOrderAttempts()+1);
		int num = (int) (Math.random() * markets.size());
		while(o.exclude == num){
			num = (int) (Math.random() * markets.size());
		}
		print("INCREASE "+ o.food.getName() + " AMT "+num);
		Market market = null;
		synchronized(markets){
			int count = 0;
			for(Market m : markets){
				if(count == num){
					market = m; break;
				}
				count++;
			}
		}
		int orderAmt = o.food.getCapacity()-o.food.getAmount();
		market.msgHereIsOrder(this, o.food, orderAmt);
		if(market.getFoodAmount(o.food) < orderAmt && o.food.getOrderAttempts() == 1){
			o.exclude = num;
			makeOrder(o);
		}
	}
	
	private void plateIt(Order o){
		o.waiter.msgOrderDone(o.food.getName(), o.table);
		cookGui.DoPlating(o.food.getName());
		orders.remove(o);
	}
	
	public void addMarket(Market m){
		markets.add(m);
	}
	
	public void setGui(CookGui cg){
		cookGui = cg;
	}
	
	public CookGui getGui(){
		return cookGui;
	}
	
	public Collection<Market> getMarkets(){
		return markets;
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
