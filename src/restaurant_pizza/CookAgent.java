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
import simcity.PersonAgent;
import simcity.interfaces.Person;
import simcity.RestMenu;

public class CookAgent extends Agent {

	// ***** DATA *****

	// Note: time is in seconds. Constant of 1000 gets multiplied when determining timeFinish
	private Map<String, Integer> recipeTimes = Collections.synchronizedMap(new HashMap<String, Integer>());
	private Map<String, Integer> inventory = Collections.synchronizedMap(new HashMap<String, Integer>());
	private Map<String, Integer> designatedMarkets = Collections.synchronizedMap(new HashMap<String, Integer>()); // food type, index of market in myMarkets
	public List<MyMarket> myMarkets = Collections.synchronizedList(new LinkedList<MyMarket>());
	private List<Order> orders = Collections.synchronizedList(new LinkedList<Order>());
	Person person;
	
	private final static int startAmount = 20; // amount of each food cook starts with
	private final static int minimumStock = 2; // cook needs to order again when inventory of any food is below this value
	private final static int restockAmount = 2; // for testing: tells cook the max amount of the food he should bring back from market
	
	private String name;
	boolean shiftDone = false;
	
	public enum AgentState {Working, WorkingAndPendingOrder};
	public AgentState state = AgentState.Working;
	public enum OrderState {NotReady, UnableToBeSupplied, BeingPrepared, Cooking, Ready, GoingToPlating};
	public RestMenu menu = new RestMenu();
	Timer timer = new Timer();
	Semaphore atDestination = new Semaphore(0, true);
	Semaphore pauseAction = new Semaphore(0, true);
	
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
    	recipeTimes.put("Marsinara with Meatballs", 30);
    	recipeTimes.put("Chicken Fusilli", 30);
    	recipeTimes.put("Pepperoni Pizza", 40);
    	recipeTimes.put("Celestial Caesar Chicken Salad", 20);
    	recipeTimes.put("Bread Sticks",  20);
	}
	
	public void setMarkets(List<MarketAgent> marketAgents) {
		for(MarketAgent ma : marketAgents)
			myMarkets.add(new MyMarket(ma));
	}
	
	public RestMenu getMenu() {
		return menu;
	}
	
	public void setPerson(Person p) {
		person = p;
	}
	
	 public void setAmount(String choice, int amount) {
	    	
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
			designatedMarkets.put(itemName, 0);
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
		print("msgNewOrder() called by Waiter " + w.getName());
		long timeFinish = -1;
		if(menu.menuItems.containsKey(order) && inventory.get(order) > 0) {
			timeFinish = (System.currentTimeMillis() + (long)((menu.menuItems.get(order))*Constants.SECOND));
			int previousInventoryAmont = inventory.get(order);
			inventory.put(order, previousInventoryAmont - 1);
			print("Number of items of type " + order + " left: " + inventory.get(order));

			Order incomingOrder = new Order(w, tableNum, order, timeFinish);
			orders.add(incomingOrder);
		}
		else {
			Order incomingOrder = new Order(w, tableNum, order, timeFinish);
			incomingOrder.state = OrderState.UnableToBeSupplied;
			menu.menuList.remove(order);
			orders.add(incomingOrder);
		}
		stateChanged();
	}
	
	public void msgShiftDone() {
		shiftDone = true;
		if (orders.size() == 0) {person.msgStopWork(10);}
	}

	public void msgOrderDone(Order o) {
		o.state = OrderState.Ready;
		stateChanged();
	}
	
	public void msgHereIsResupply(MarketOrder mo) {
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
	
	public void msgAtDestination() {
		atDestination.release();
		stateChanged();
	}
	
	// ***** SCHEDULER *****
	
	protected boolean pickAndExecuteAnAction() {
	
		if(state == AgentState.Working) {
			Set<String> foodsSet = inventory.keySet();
			synchronized (foodsSet) {
				for(String foodType : foodsSet) {
					int amount = inventory.get(foodType);
					if(amount < minimumStock) {
						Restock(new MarketOrder(foodType, restockAmount));
						state = AgentState.WorkingAndPendingOrder;
						return true;
					}
				}
			}
		}
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
		return false;
	}

	// ***** ACTIONS *****
	
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

	private void Restock(MarketOrder mo) {
		print("Restock() called");
		int indMarketToQuery = designatedMarkets.get(mo.foodType);
		if(indMarketToQuery < myMarkets.size()) {
			MyMarket theMarket = myMarkets.get(indMarketToQuery);
			theMarket.market.msgNeedFood(mo);
		}
	}
	
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
}