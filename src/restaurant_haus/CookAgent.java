package restaurant_haus;

import agent_haus.Agent;
import restaurant_haus.gui.CookGui;
import restaurant_haus.gui.WaiterGui;
import simcity.PersonAgent;
import simcity.interfaces.Person;

import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Restaurant Host Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the HostAgent. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class CookAgent extends Agent {

	private String name;
	CashierAgent cashier;
	
	private Semaphore atDestination = new Semaphore(0, true);
	
	CookGui cookGui;
	public Person person;
	boolean shiftDone = false;

	
	Menu m;
	private class Order {
		WaiterAgent w;
		String choice;
		int table;
		State s;
		
		Order(WaiterAgent w, String choice, int table) {
			this.w = w;
			this.choice = choice;
			this.table = table;
			s = State.Pending;
		}
	}
	
	List<Order> orders = Collections.synchronizedList(new ArrayList<Order>());
	enum State {Pending, Cooking, Done, Plated, PickedUp};
	Timer timer = new Timer();
	
	private class Food {
		String choice;
		int time;
		int inventory;
		InventoryState s;
		MarketAgent nextMarket;
		
		Food(String choice, int time) {
			this.choice = choice;
			this.time = time;
			this.inventory = 7;//hack to test inventory
			if(this.choice.equals("Pastrami Cheeseburger")) {
				this.inventory = 100;
			}
			if(this.choice.equals("Chicken Sausage Pretzel Roll")) {
				this.inventory = 100;
			}
			if(this.choice.equals("BLT Flatbread")) {
				this.inventory = 100;
			}
			if(this.choice.equals("Apple & Cheddar Salad")) {
				this.inventory = 100;
			}
			s = InventoryState.Steady;
			nextMarket = null;
		}
		
		public void decreaseInventory() {
			inventory--;
		}
	}
	public void setPerson(Person p) {
		person = p;
	}
	
	public void setAmount(String choice, int amount) {
	   	
	}
	
	private enum InventoryState {Steady, Low, Ordered, Delivered, marketEmtpy, CanOrder, marketQueried, OrderAgain, OrderAnother};
	private final int SAFEFOODQUANTITY = 10;
	private final int DANGERFOODQUANTITY = 5;
	Map<String, Food> foodInventory = new HashMap<String, Food>();
	
	class MyMarket {
		MarketAgent market;
		List<String> stock;
		
		MyMarket(MarketAgent market) {
			this.market = market;
			stock = Collections.synchronizedList(new ArrayList<String>());
			
			for(Map.Entry<String, Food> food : foodInventory.entrySet()) {
				//if(market.checkStock(food.getKey())) {
					this.stock.add(food.getKey());
				//}
			}
		}
	}
	
	List<MyMarket> markets = Collections.synchronizedList(new ArrayList<MyMarket>());
	
	public CookAgent(String name) {
		super();
		this.name = name;
		
		foodInventory.put("Pastrami Cheeseburger", new Food("Pastrami Cheeseburger", 5000));
		foodInventory.put("Chicken Sausage Pretzel Roll", new Food("Chicken Sausage Pretzel Roll", 10000));
		foodInventory.put("BLT Flatbread", new Food("BLT Flatbread", 7000));
		foodInventory.put("Apple & Cheddar Salad", new Food("Apple & Cheddar Salad", 1000));
		
		for(Map.Entry<String, Food> foodItem : foodInventory.entrySet()) {
			if(inventoryLow(foodItem.getValue().inventory) && foodItem.getValue().s != InventoryState.Ordered && foodItem.getValue().s != InventoryState.marketEmtpy) {
				foodItem.getValue().s = InventoryState.Low;
			}
		}
		
	}

	public String getName() {
		return name;
	}
	
	public void setGui(CookGui gui) {
		this.cookGui = gui;
	}
	
	// Messages

	public void msgPlaceOrder (WaiterAgent w, String choice, int table) {
		orders.add(new Order(w, choice, table));
		stateChanged();
		//Add new order to list of orders
	}
	
	public void msgShiftDone() {
		shiftDone = true;
		if (orders.size() == 0) {person.msgStopWork(10);}
	}
	
	public void msgFoodDone (Order o) {
		o.s = State.Done;
		print("I finished cooking " + o.choice + " for table " + String.valueOf(o.table) + ".");
		stateChanged();
		//Set state of order to Done
	}
	
	public void msgPickUpFood (WaiterAgent w) {
		synchronized(orders) {
			for(Order order : orders) {
				if(order.w == w) {
					order.s = State.PickedUp;
					stateChanged();
					return;
				}
			}
		}
	}
	
	public void msgNoFood (String choice) {
		foodInventory.get(choice).s = InventoryState.marketEmtpy;
		stateChanged();
	}
	
	public void msgOrderDelivered (String choice, int quantity) {
		Food deliveredFood = foodInventory.get(choice);
		deliveredFood.inventory += quantity;
		deliveredFood.s = InventoryState.Delivered;
		stateChanged();
	}
	
	public void msgCanFill(String food, boolean canFill, MarketAgent market) {
		if(canFill) {
			foodInventory.get(food).s = InventoryState.CanOrder;
			foodInventory.get(food).nextMarket = market;
		}
		else {
			synchronized(markets) {
				for(MyMarket myMarket : markets) {
					if(myMarket.market == market) {
						myMarket.stock.remove(food);
					}
				}
			}
			foodInventory.get(food).s = InventoryState.Low;
		}
		stateChanged();
	}
	
	public void msgOrderFromAnother (String choice, MarketAgent market) {
		Food food = foodInventory.get(choice);
		food.s = InventoryState.OrderAnother;
		synchronized(markets) {
			for(MyMarket myMarket : markets) {
				if(myMarket.market == market) {
					myMarket.stock.remove(food);
				}
			}
		}
		stateChanged();
	}
	
	public void msgAtDestination() {
		atDestination.release();
		stateChanged();
	}
	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		if(isPaused) {
			try {
				pauseSem.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		for (Map.Entry<String, Food> foodItem : foodInventory.entrySet()) {
			if(foodItem.getValue().s == InventoryState.Delivered) {
				receiveOrder(foodItem.getValue());
				return true;
			}
		}
		
		for (Map.Entry<String, Food> foodItem : foodInventory.entrySet()) {
			if(foodItem.getValue().s == InventoryState.CanOrder) {
				orderFood(foodItem.getValue());
				return true;
			}
		}
		
		for (Map.Entry<String, Food> foodItem : foodInventory.entrySet()) {
			if(foodItem.getValue().s == InventoryState.Low || foodItem.getValue().s == InventoryState.OrderAnother) {
				findMarket(foodItem.getValue());
				return true;
			}
		}
		
		synchronized(orders) {
			for(Order o : orders) {
				if(o.s == State.Done) {
					plateIt(o);
					return true;
				}
			}
		}
		
		synchronized(orders) {
			for(Order o : orders) {
				if(o.s == State.Pending) {
					cookIt(o);
					return true;
				}
			}
		}
		
		synchronized(orders) {
			for(Order o : orders) {
				if(o.s == State.PickedUp) {
					removeIt(o);
					return true;
				}
			}
		}
		/*
		If there exists o in orders such that o.state = done
			then PlateIt(o);
		
		If there exists o in orders such that o.state = pending
			then CookIt(o);
		*/
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}
	
	private class CookingTask extends TimerTask {
		Order o;
		CookAgent cook;
		
		public void run() {
			cook.msgFoodDone(o);
		}
		
		CookingTask(Order o, CookAgent cook) {
			this.o = o;
			this.cook = cook;
		}
	}
	
	// Actions
	private void cookIt(Order o) {
		Food currentOrder = foodInventory.get(o.choice);
		if(currentOrder.inventory == 0) {
			if(m.isOnMenu(o.choice)) {
				m.removeItem(o.choice);
				print("We're outta bloody " + currentOrder.choice + ". I'm taking it off the menu.");
			}
			o.w.msgOutOfFood(o.table);
			orders.remove(o);
			return;
		}
		
		currentOrder.decreaseInventory();
		cookGui.GoToRefrigerator();
		try {
			atDestination.acquire();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		cookGui.SpawnFood(o.table, o.choice);
		
		cookGui.GoToStove();
		try {
			atDestination.acquire();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		cookGui.LeaveFood(o.table, false);
		
		timer.schedule(new CookingTask(o, this), (long)currentOrder.time);
		
		print("I started cooking " + o.choice + " for table " + String.valueOf(o.table) + ".");
		o.s = State.Cooking;
		
		if(inventoryLow(currentOrder.inventory) && currentOrder.s == InventoryState.Steady) {
			currentOrder.s = InventoryState.Low;
		}
		/*Start the timer by checking the map of food name to cooking time
		 * Set the state of the Order to cooking
		 */
	}
	
	private void plateIt(Order o) {
		cookGui.GoToStove();
		try {
			atDestination.acquire();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		cookGui.GrabFood(o.table);
		
		cookGui.GoToPlating();
		try {
			atDestination.acquire();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		cookGui.LeaveFood(o.table, true);
		
		o.w.msgFoodReady(o.table);
		o.s = State.Plated;
		/*o.w.msgFoodReady(o.choice,o.table)
		 * Orders.remove(o);
		 */
	}

	public void removeIt(Order o) {
		cookGui.deleteFood(o.table);
		orders.remove(o);
	}
	
	private void receiveOrder(Food food) {
		if(!m.isOnMenu(food.choice)) {
			m.addItem(food.choice);
			print("Alright! We got more " + food.choice + ". I'm putting it back on the menu.");
		}
		if (food.s != InventoryState.OrderAnother) {
			food.s = InventoryState.Steady;
		}
	}
	
	private void findMarket(Food food) {
		cookGui.GoToPhone();
		try {
			atDestination.acquire();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		synchronized(markets) {
			for(MyMarket market : markets) {
				if(market.stock.contains(food.choice)) {
					//print("Market " + market.market.getName() + " do you have " + food.choice + "?");
					market.market.msgDoYouHave(food.choice);
					food.s = InventoryState.marketQueried;
					return;
				}
			}
		}
		food.s = InventoryState.marketEmtpy;
	}
	
	private void orderFood(Food food) {
		cookGui.GoToPhone();
		try {
			atDestination.acquire();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		food.nextMarket.msgPlaceOrder(food.choice, SAFEFOODQUANTITY - food.inventory, cashier);
		print("I'd like to place an order for " + String.valueOf(SAFEFOODQUANTITY - food.inventory) + " " + food.choice + ", please.");
		food.s = InventoryState.Ordered;
	}
	
	//utilities
	
    public int getQuantity(String name){
    	if(foodInventory.get(name) != null){
    		return foodInventory.get(name).inventory;
    	}
    	return 0;
    }
	
	public void setMenu(Menu m) {
		this.m = m;
	}
	
	private boolean inventoryLow(int inventory) {
		if (inventory < DANGERFOODQUANTITY) {
			return true;
		}
		return false;
	}
	
	public void addMarket(MarketAgent market) {
		markets.add(new MyMarket(market));
	}

	public void setCashier(CashierAgent cashier) {
		this.cashier = cashier;
	}
}

