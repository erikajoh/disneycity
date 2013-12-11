package restaurant_pizza;

import agent_pizza.Agent;
import agent_pizza.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Semaphore;

import restaurant_pizza.gui.CookGui;
import restaurant_pizza.gui.RestaurantPizza;
import simcity.interfaces.Market_Douglass;
import simcity.interfaces.Person;
import simcity.RestMenu;

public class CookAgent extends Agent {

	// ***** DATA *****

	// Note: time is in seconds. Constant of 1000 gets multiplied when determining timeFinish
	private Map<String, Integer> recipeTimes = Collections.synchronizedMap(new HashMap<String, Integer>());
	private Map<String, Integer> inventory = Collections.synchronizedMap(new HashMap<String, Integer>());
	private List<Order> orders = Collections.synchronizedList(new LinkedList<Order>());
	Person person;
	
	private final static int startAmount = 20; // amount of each food cook starts with
	private final static int minimumStock = 2; // cook needs to order again when inventory of any food is below this value
	private final static int restockAmount = 2; // for testing: tells cook the max amount of the food he should bring back from market
	
	private String name;
	boolean shiftDone = false;
	public boolean isWorking = true;
	double wage;
	
	List<MarketOrder> marketOrders = new ArrayList<MarketOrder> ();
	Market_Douglass market;
	RestaurantPizza pizza;
	public enum AgentState {Working, WorkingAndPendingOrder};
	public AgentState state = AgentState.Working;
	public enum OrderState {NotReady, UnableToBeSupplied, BeingPrepared, Cooking, Ready, GoingToPlating};
	public RestMenu menu = new RestMenu();
	Timer timer = new Timer();
	Semaphore atDestination = new Semaphore(0, true);
	Semaphore pauseAction = new Semaphore(0, true);
	List<Food> foods;
	
	private enum moState {pending, ordered};
	int curID = 0;
	
	RestaurantPizza restaurant;
	
	private CookGui cookGui = null;

	public CookAgent(String name) {
		super();
		this.name = name;
		menu.addItem("Marsinara with Meatballs", 9.49 );
    	menu.addItem("Chicken Fusilli", 9.49);
    	menu.addItem("Pepperoni Pizza",  6.99);
    	menu.addItem("Celestial Caesar Chicken Salad", 8.49);
    	menu.addItem("Bread Sticks", 4.99);
    	for (int i = 0; i< menu.menuList.size(); i++) {
    		inventory.put(menu.menuList.get(i), startAmount);
    	}
    	foods = Collections.synchronizedList(new ArrayList<Food>());
    	foods.add(new Food("Marsinara with Meatballs", 0, 0, 6, 7000));
		foods.add(new Food("Chicken Fusilli", 0, 0, 6, 6000));
		foods.add(new Food("Pepperoni Pizza", 0, 0, 6, 4000));
		foods.add(new Food("Celestial Caesar Chicken Salad", 0, 0, 6, 7000));
		foods.add(new Food("Bread Sticks", 7, 0, 6, 2500));
    	recipeTimes.put("Marsinara with Meatballs", 30);
    	recipeTimes.put("Chicken Fusilli", 30);
    	recipeTimes.put("Pepperoni Pizza", 40);
    	recipeTimes.put("Celestial Caesar Chicken Salad", 20);
    	recipeTimes.put("Bread Sticks",  20);
    	//market = m;
    	//restaurant = rest;
	}
	
	public void setRestaurant(RestaurantPizza rest) {
		restaurant = rest;
	}
	
	public RestMenu getMenu() {
		return menu;
	}
	
	public void setPerson(Person p) {
		person = p;
	}
	
	 public void setAmount(String choice, int amount) {
	    // TODO This is empty, needed for WorkplacePropertiesPanel
	 }
	 
	 public void setMarket(Market_Douglass m) {
			print("setting market " + m.getName());
			market = m;
		}
	
	public void initializeMaps() throws Exception {
		URL fileURL = getClass().getResource("/res/MenuTextFile.txt");
		URI fileURI = fileURL.toURI();
		BufferedReader br = new BufferedReader(new FileReader(new File(fileURI)));
		int numItems = Integer.parseInt(br.readLine());
		StringTokenizer st;
		for(int i = 0; i < numItems; i++) {
			st = new StringTokenizer(br.readLine());
			String itemName = st.nextToken();
			st.nextToken();
			int itemCookTime = Integer.parseInt(st.nextToken());
			recipeTimes.put(itemName, itemCookTime);
			inventory.put(itemName, startAmount);
			//designatedMarkets.put(itemName, 0);
		}
		br.close();
	}

	public String getName() {
		return name;
	}
	
	public String getCookingOrders() {
		String output = "";
		synchronized(orders) {
			for(Order o : orders)
				if(o.state == OrderState.Cooking || o.state == OrderState.Ready) {
					output += "|" + o.order + "|";
				}
		}
		return output;
	}
	
	// ***** MESSAGES *****

	public void msgNewOrder(WaiterAgent w, int tableNum, String order) {
		print("msgNewOrder() from Waiter " + w.getName());
		long timeFinish = -1;
		if(findFood(order).amount > 0) {
			timeFinish = (System.currentTimeMillis() + (long)((menu.menuItems.get(order))*Constants.SECOND));
			int previousInventoryAmount = inventory.get(order);
			inventory.put(order, previousInventoryAmount - 1);
			print("Number of items of type " + order + " left: " + inventory.get(order));

			Order incomingOrder = new Order(w, tableNum, order, timeFinish);
			orders.add(incomingOrder);
			Food f = findFood(order);
			f.amount--;
			if (f.amount <= f.low ) {
				marketOrders.add(new MarketOrder(f.choice, f.capacity-f.amount));
				f.ordered = true;
			}
		}
		else {
			Order incomingOrder = new Order(w, tableNum, order, timeFinish);
			incomingOrder.state = OrderState.UnableToBeSupplied;
			menu.menuList.remove(order);
			orders.add(incomingOrder);
			Food f = findFood(order);
			marketOrders.add(new MarketOrder(f.choice, f.capacity-f.amount));
		}
		stateChanged();
	}
	
	public void msgShiftDone(double w) {
		shiftDone = true;
		isWorking = false;
		wage = w;
		cookGui.DoLeave(person, wage);
	}

	public void msgOrderDone(Order o) {
		o.state = OrderState.Ready;
		stateChanged();
	}
	
	public void msgHereIsOrder(String choice, int amount, int id) {
		print("Received a delivery of "+amount+" "+choice+"'s from the market!");
		for (int i=0; i<marketOrders.size(); i++){
			MarketOrder mo = marketOrders.get(i);
			if (mo.id == id && mo.amount == amount) {
				Food f = findFood(mo.food);
				f.amount = amount;
				print("removing a market order whee");
				marketOrders.remove(mo);
			} else if (mo.food == choice && mo.amount != 0) {
				Food f = findFood(mo.food);
				f.amount = amount + mo.amount;
				mo.amount -= amount;
			}
		}
	}
	
	/*public void msgHereIsResupply(MarketOrder mo) {
		if(mo.amount == 0) {
			int indObsoleteMarket = designatedMarkets.get(mo.foodType);
			designatedMarkets.put(mo.foodType, indObsoleteMarket+1);
		}
		if (!menu.menuList.contains(mo.foodType)) {
			menu.menuList.add(mo.foodType);
		}
		int oldInvAmt = inventory.get(mo.foodType);
		inventory.put(mo.foodType, oldInvAmt + mo.amount);
		state = AgentState.Working;
		stateChanged();
	}
	*/
	
	public void msgAtDestination() {
		atDestination.release();
		stateChanged();
	}
	
	// ***** SCHEDULER *****
	
	protected boolean pickAndExecuteAnAction() {

		synchronized (orders) {
			for(Order o : orders) {
				if(o.state == OrderState.UnableToBeSupplied) {
					TellWaiterWeAreOut(o);
					return true;
				}
			}
		}
		synchronized (orders) {
			for(Order o : orders) {
				if(o.state == OrderState.NotReady) {
					o.state = OrderState.BeingPrepared;
					PrepareOrder(o);
					return true;
				}
			}
		}
		synchronized (orders) {
			for(Order o : orders) {
				if(o.state == OrderState.Ready) {
					OrderIsReady(o);
					return true;
				}
			}
		}
		synchronized(marketOrders) {
			for (MarketOrder mo : marketOrders){ 
				if (mo.os == moState.pending) {
					print("Ordering "+mo.amount+" "+mo.food+"'s");
					mo.os = moState.ordered;
					print ("COWABUNGA ordering from market");
					goToMarket(restaurant, "Italian", mo.amount, mo.id);
					return true;
				}
			}
		}
		// Producer-consumer handling
		StandOrder orderFromStand = restaurant.revolvingStand.remove();
		if(orderFromStand != null) {
			
			WaiterAgent standWaiter = orderFromStand.waiter;
			int standTableNum = orderFromStand.tableNum;
			String standOrderType = orderFromStand.order;
			
			if(menu.menuItems.containsKey(orderFromStand.order) && inventory.get(orderFromStand.order) > 0) {
				long timeFinish = (System.currentTimeMillis() + (long)((menu.menuItems.get(standOrderType))*Constants.SECOND));
				Order newStandOrder = new Order(standWaiter, standTableNum, standOrderType, timeFinish);
				AddNewOrderFromStand(newStandOrder);
			}
			return true;
		}
		return false;
	}

	// ***** ACTIONS *****
	
	private void goToMarket(RestaurantPizza r, String type, int amount, int id) {
		market.personAs(r, type, amount, id);
	}
	
	private void AddNewOrderFromStand(Order newStandOrder) {
		print("Retrieving order from stand");
		msgNewOrder(newStandOrder.waiter, newStandOrder.tableNum, newStandOrder.order);
	}
	
	private void TellWaiterWeAreOut(Order o) {
		print("TellWaiterWeAreOut() called");
		WaiterAgent waiter = o.waiter;
		waiter.msgOutOfThisOrder(o.tableNum, o.order);
		orders.remove(o);
	}
	
	private void PrepareOrder(Order o) {
		print("PrepareOrder() called");
		DoGoToFridge();
		try {
			atDestination.acquire();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		cookGui.DoDisplayOrder(o.order + "(nd)");
		DoGoToCookingArea();
		try {
			atDestination.acquire();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		o.state = OrderState.Cooking;
		cookGui.DoDisplayOrder("");
		cookGui.DoDisplayCookingOrders(getCookingOrders());
		final Order theOrder = o;
		long currentTime = System.currentTimeMillis();
		//sets a timer that will notify when order is done cooking
		
		timer.schedule(new TimerTask() {
			public void run() {
				print("Finished order");
				msgOrderDone(theOrder);
				stateChanged();
			}
		},
		Math.max(o.finishTime - currentTime, 1)); //how long to wait before running task
		DoGoToPlatingArea();
		try {
			atDestination.acquire();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void OrderIsReady(Order o) {
		print("OrderIsReady() called: " + o.order + " ready for table #: " + o.tableNum);
		DoGoToCookingArea();
		try {
			atDestination.acquire();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		o.state = OrderState.GoingToPlating;
		cookGui.DoDisplayOrder(o.order + "(d)");
		cookGui.DoDisplayCookingOrders(getCookingOrders());
		DoGoToPlatingArea();
		try {
			atDestination.acquire();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		cookGui.DoDisplayOrder("");
		WaiterAgent w = o.waiter;
		w.msgOrderReady(o.tableNum, o.order);
		orders.remove(o);
	}

/*	private void Restock(MarketOrder mo) {
		print("Restock() called");
		int indMarketToQuery = designatedMarkets.get(mo.foodType);
		if(indMarketToQuery < myMarkets.size()) {
			MyMarket theMarket = myMarkets.get(indMarketToQuery);
			theMarket.market.msgNeedFood(mo);
		}
	}
	*/
	
	// animations
	
	private void DoGoToPlatingArea() {
		print("Going to plating area");
		cookGui.DoGoToPlatingArea();
	}
	
	private void DoGoToFridge() {
		print("Going to fridge");
		cookGui.DoGoToFridge();
	}
	
	private void DoGoToCookingArea() {
		print("Going to cooking area");
		cookGui.DoGoToCookingArea();
	}
	
	// utilities
	
	 public int getQuantity(String name){
		 for(Food food : foods){
			 if(food.choice.equals(name)){
				 return food.amount;
			 } 
		 }
	   	return 0;
	 } 
	 
	 public void setQuantity(String name, int num){
		 for(Food food : foods){
			 if(food.choice.equals(name)){
				  food.amount = num;
			 } 
		 }
	 }
	

	public void setGui(CookGui gui) {
		cookGui = gui;
	}

	private class MyMarket {
		MarketAgent market;
		
		public MyMarket(MarketAgent aMarket) {
			market = aMarket;
		}
	}
	
	private class Order {
		WaiterAgent waiter;
		int tableNum;
		String order;
		OrderState state;
		long finishTime; // time in system's milliseconds when order will be ready
		public Order(WaiterAgent aWaiter, int aTableNum, String aOrder, long aFinishTime) {
			waiter = aWaiter;
			tableNum = aTableNum;
			order = aOrder;
			state = OrderState.NotReady;
			finishTime = aFinishTime;
		}
	}
	
	
	 class Food {
		String choice;
		int cookingTime;
		int amount; 
		int capacity; 
		int low; 
		boolean ordered;
		
		Food (String ch, int am, int lo, int cap, int ct) {
			choice = ch; 
			amount = am;
			low = lo;
			capacity = cap;
			cookingTime = ct;
			ordered = false;
		}
		
		public void setOrdered(boolean ord) {
			ordered = ord;
		}
	 }

	
	class MarketOrder {
		String food;
		int amount;
		moState os;
		int id;
		MarketOrder(String f, int a) {
			amount = a;
			food = f;
			id = curID;
			curID++;
			os = moState.pending;
		}
		
		
	}
	//utilities

	
	    
	public Food findFood(String ch) {
		synchronized(foods) {
			for (Food f : foods) {
				if (f.choice==ch) {
					return f;
				}
			}
		}
		return null;
	}
}