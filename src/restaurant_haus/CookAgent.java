package restaurant_haus;

import agent_haus.Agent;
import restaurant_cafe.gui.Food;
import restaurant_haus.gui.CookGui;
import restaurant_haus.gui.RestaurantHaus;
import restaurant_haus.gui.WaiterGui;
import simcity.PersonAgent;
import simcity.interfaces.Market_Douglass;
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
	RestaurantHaus rest;
	private Semaphore atDestination = new Semaphore(0, true);
	boolean needToCheckStand = false;
	private OrderStand orderStand;
	double wage;
	public boolean isWorking;
	
	int nextID = 0;
	
	CookGui cookGui;
	public Person person;
	boolean shiftDone = false;


	Menu m;

	List<Order> orders = Collections.synchronizedList(new ArrayList<Order>());
	
	Timer timer = new Timer();

	private class Food {
		String choice;
		int time;
		int inventory;
		InventoryState s;

		Food(String choice, int time) {
			this.choice = choice;
			this.time = time;
			this.inventory = 7;//hack to test inventory
			if(this.choice.equals("Pastrami Cheeseburger")) {
				this.inventory = 6;
			}
			if(this.choice.equals("Chicken Sausage Pretzel Roll")) {
				this.inventory = 6;
			}
			if(this.choice.equals("BLT Flatbread")) {
				this.inventory = 6;
			}
			if(this.choice.equals("Apple & Cheddar Salad")) {
				this.inventory = 6;
			}
			s = InventoryState.Steady;
		}

		public void decreaseInventory() {
			inventory--;
		}
	}
	
	
	class MarketOrder {
		String food;
		int quantity;
		int ID;
		MarketOrderState state;
		
		public MarketOrder(String food, int quantity, int ID) {
			this.food = food;
			this.quantity = quantity;
			this.ID = ID;
			state = MarketOrderState.ORDERED;
		}
	}
	
	enum MarketOrderState {
		ORDERED,
		DELIVERED,
		DONE
	}
	
	List<MarketOrder> marketOrders = Collections.synchronizedList(new ArrayList<MarketOrder>());
	
	public void setPerson(Person p) {
		person = p;
	}

	public void setAmount(String choice, int amount) {

	}

	private enum InventoryState {
		Steady,
		Low,
		Ordered,
		Delivered
	};
	private final int SAFEFOODQUANTITY = 10;
	private final int DANGERFOODQUANTITY = 5;
	Map<String, Food> foodInventory = new HashMap<String, Food>();

	List<Market_Douglass> markets = Collections.synchronizedList(new ArrayList<Market_Douglass>());

	public CookAgent(String name, RestaurantHaus rest, OrderStand orderStand) {
		super();
		this.orderStand = orderStand;
		this.name = name;
		this.rest = rest;
		foodInventory.put("Pastrami Cheeseburger", new Food("Pastrami Cheeseburger", 5000));
		foodInventory.put("Chicken Sausage Pretzel Roll", new Food("Chicken Sausage Pretzel Roll", 10000));
		foodInventory.put("BLT Flatbread", new Food("BLT Flatbread", 7000));
		foodInventory.put("Apple & Cheddar Salad", new Food("Apple & Cheddar Salad", 1000));

		for(Map.Entry<String, Food> foodItem : foodInventory.entrySet()) {
			if(inventoryLow(foodItem.getValue().inventory) && foodItem.getValue().s != InventoryState.Ordered) {
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

	public void msgShiftDone(double w) {
		shiftDone = true;
		isWorking = false;
		wage = w;
		cookGui.DoLeave(person, wage);
	}

	public void msgFoodDone (Order o) {
		o.s = Order.State.Done;
		print("I finished cooking " + o.choice + " for table " + String.valueOf(o.table) + ".");
		stateChanged();
		//Set state of order to Done
	}

	public void msgPickUpFood (WaiterAgent w) {
		synchronized(orders) {
			for(Order order : orders) {
				if(order.w == w) {
					order.s = Order.State.PickedUp;
					stateChanged();
					return;
				}
			}
		}
	}

	public void msgOrderDelivered (String choice, int quantity, int ID) {
		for(MarketOrder mo : marketOrders) {
			if(mo.ID == ID) {
				mo.state = MarketOrderState.DELIVERED;
			}
		}
		stateChanged();
	}
	
	public void msgCheckStand() {
		needToCheckStand = true;
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
		
		synchronized(marketOrders) {
			for(MarketOrder marketOrder : marketOrders) {
				if(marketOrder.state == MarketOrderState.DELIVERED) {
					receiveOrder(marketOrder);
					return true;
				}
			}
		}

		for (Map.Entry<String, Food> foodItem : foodInventory.entrySet()) {
			if(foodItem.getValue().s == InventoryState.Low) {
				orderFood(foodItem.getValue());
				return true;
			}
		}
		
		if(needToCheckStand) {
			checkStand();
			return true;
		}
		
		synchronized(orders) {
			for(Order o : orders) {
				if(o.s == Order.State.Done) {
					plateIt(o);
					return true;
				}
			}
		}

		synchronized(orders) {
			for(Order o : orders) {
				if(o.s == Order.State.Pending) {
					cookIt(o);
					return true;
				}
			}
		}

		synchronized(orders) {
			for(Order o : orders) {
				if(o.s == Order.State.PickedUp) {
					removeIt(o);
					return true;
				}
			}
		}

		return false;

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
		o.s = Order.State.Cooking;

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
		o.s = Order.State.Plated;
		/*o.w.msgFoodReady(o.choice,o.table)
		 * Orders.remove(o);
		 */
	}

	public void removeIt(Order o) {
		cookGui.deleteFood(o.table);
		orders.remove(o);
	}

	private void receiveOrder(MarketOrder marketOrder) {
		Food food = foodInventory.get(marketOrder.food);
		if(!m.isOnMenu(food.choice)) {
			m.addItem(food.choice);
			print("Alright! We got more " + food.choice + ". I'm putting it back on the menu.");
		}
		if(inventoryLow(food.inventory))
			food.s = InventoryState.Low;
		else
			food.s = InventoryState.Steady;
		marketOrder.state = MarketOrderState.DONE;
	}

	private void orderFood(Food food) {
		print("I'd like to place an order for " + String.valueOf(SAFEFOODQUANTITY - food.inventory) + " " + food.choice + ", please.");
		Random random = new Random();
		
		marketOrders.add(new MarketOrder(food.choice, SAFEFOODQUANTITY - food.inventory, nextID));
		markets.get(random.nextInt(markets.size())).personAs(rest, "German", SAFEFOODQUANTITY - food.inventory, nextID);
		nextID++;
		food.s = InventoryState.Ordered;
	}
	
	public void checkStand() {
		cookGui.goToStand();
		try {
			atDestination.acquire();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		List<Order> tempOrders = orderStand.getOrders();
		for(Order order : tempOrders)
			orders.add(order);
	}
	
	//utilities

	public int getQuantity(String name){
		if(foodInventory.get(name) != null){
			return foodInventory.get(name).inventory;
		}
		return 0;
	}

	public void setQuantity(String name, int num){
		if(foodInventory.get(name) != null){
			foodInventory.get(name).inventory = num;
		}
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

	public void addMarket(Market_Douglass market) {
		markets.add(market);
	}

	public void setCashier(CashierAgent cashier) {
		this.cashier = cashier;
	}
}

