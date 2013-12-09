package restaurant_bayou;

import agent_bayou.Agent;

import java.util.*;
import java.util.concurrent.Semaphore;

import simcity.PersonAgent;
import simcity.RestMenu;
import restaurant_bayou.HostAgent.Menu;
import restaurant_bayou.gui.CookGui;
import restaurant_bayou.gui.RestaurantBayou;
import restaurant_bayou.interfaces.Market;
import restaurant_bayou.CookAgent.Order;
import simcity.interfaces.Person;

public class CookAgent extends Agent {
	private String name;
	private List<Order> orders =  Collections.synchronizedList(new ArrayList<Order>());
	private Inventory i = new Inventory();
	private CashierAgent cashier;
	private Semaphore busy = new Semaphore(1,true);
	private Semaphore cooking = new Semaphore(1,true);
	private List<String> unavailableFood =  Collections.synchronizedList(new ArrayList<String>());
	private List<String> orderedFood =  Collections.synchronizedList(new ArrayList<String>());
	private List<MyMarket> cutoffMarkets =  Collections.synchronizedList(new ArrayList<MyMarket>()); 
	public List<MyMarket> markets =  Collections.synchronizedList(new ArrayList<MyMarket>());
	public Timer t = new Timer();
	private CookGui cookGui;
	private Person person;
	private RestMenu menu = new RestMenu();
	boolean shiftDone = false;
	RestaurantBayou restaurant;

	/**
	 * Constructor for CookAgent class
	 *
	 * @param name name of the cook
	 */
	public CookAgent(String name, RestaurantBayou r, RestMenu m){
		super();
		this.name = name;
		this.restaurant = r;
	    menu.addItem("Filet Mignon", 42.99);
	    menu.addItem("Pan-Seared Salmon", 33.99);
	    menu.addItem("Surf and Turf", 45.99);
	    menu.addItem("Seafood Jambalaya", 31.99);
		for (String item: menu.menuList) {
			i.add(item, 8);
		}
		//i.empty();
	}
	
	public void setPerson(Person p) {
		person = p;
	}

	public void setCashier(CashierAgent c) {
		this.cashier = c;
	}
	
	public void setAmount(String food, int am) {
		
	}
	public void msgHereIsOrder(WaiterAgent w, String choice, int table){
		orders.add(new Order(w, choice, table));
//		if (!waiters.contains(w)) waiters.add(w);
		stateChanged();
	}
	
	public void msgShiftDone() {
		shiftDone = true;
		if (orders.size() == 0) {
			//person.msgStopWork(10);
			cookGui.DoLeave(person);
		}
	}
	
	public void msgHereIsMoreFood(Market mkt, String f, int amt){
		if (amt > 0) {
			Do("Got "+amt+" "+f+" from "+mkt.getName());
			i.increase(f, amt);
		} else {
			Do(mkt.getName()+" is out of "+f);
			for (MyMarket m: markets) if (m.m == mkt) m.outOf(f);
		}
		orderedFood.remove(f);
		stateChanged();
	}
	
	public void msgMarketHasCutUsOff(Market m){
		cutoffMarkets.add(new MyMarket(m));
		stateChanged();
	}
	
	public void msgCookReady(){
		// from gui
		busy.release();
	}
	
	public void msgCookDone(){
		// from gui
		cooking.release();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		synchronized(cutoffMarkets){
			for (MyMarket m: cutoffMarkets) {
				synchronized(markets){
					for (MyMarket m1: markets) {
						if (m.m == m1.m) {
							markets.remove(m1);
							Do("Cut off by market "+m.m.getName());
							cutoffMarkets.remove(m);
							return true;
						}
					}	
				}
			}
		}
		synchronized(i.foodNames){
			for (String f: i.foodNames) {
				if (!orderedFood.contains(f) && markets.size() > 0 && i.food.get(f) < 2) {
					orderedFood.add(f);
					synchronized(markets){
						for (MyMarket m: markets) {
							if (m.has(f)){
								m.m.msgNeedFood(this, cashier, f, 2-i.food.get(f));
								return true;
							}
						}
					}
				}
			}
		}
		synchronized(orders){
			for (Order o: orders) {
				if (o.state == OrderState.Done) {
					o.state = OrderState.Sent;
					o.w.msgOrderIsDone(o.table);
					orders.remove(o);
					return true;
				}
			}
		}
		synchronized(orders){
			for (Order o: orders) {
				if (o.state == OrderState.Waiting) {
					if (unavailableFood.contains(o.choice)){
						o.w.msgOutOfFood(unavailableFood);
						orders.remove(o);
						return true;
					} else {
						try {
							cooking.acquire();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							busy.acquire();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						cookGui.DoGetIngredients();
						try {
							busy.acquire();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						cookGui.DoCookFood();
						Do("Cooking food");
						o.state = OrderState.Cooking;
						o.cookIt();
						i.decrease(o.choice);
						return true;
					}
				}
			}
		}
//		Order newO = restaurant.orderStand.remove();
//		if (newO!=null) {orders.add(newO); print("order stand not empty, got order for "+ newO.choice); return true;}
//		else {waitTimer();}
		return false;
	}
	
	public class Inventory {
		Hashtable<String, Integer> food = new Hashtable<String, Integer>();
		List<String> foodNames = Collections.synchronizedList(new ArrayList<String>());
		public void decrease(String f) {
			if (food.get(f) == 1) {
				unavailableFood.add(f);
			}
			if (food.get(f) >= 1) food.put(f, food.get(f)-1);
		}
		public void increase(String f, int amt){
			food.put(f, food.get(f)+amt);
			unavailableFood.remove(f);
		}
		public void add(String f, int a) {
			food.put(f, a);
			foodNames.add(f);
		}
		public void empty(){
			Enumeration<String> foodKeys = food.keys();
			while(foodKeys.hasMoreElements()) {
			    String f = foodKeys.nextElement();
			    int amt = food.get(f);
			    if(amt != 0) {
			    	food.put(f, 0);
			    	unavailableFood.add(f);
			    }
			}
		}
	}
	
	public enum OrderState {Waiting, Cooking, Done, Sent;}
	public class Order {
		WaiterAgent w;
		String choice;
		int table;
		OrderState state;
		public Order(WaiterAgent wa, String c, int tbl) {
			w = wa;
			choice = c;
			table = tbl;
			state = OrderState.Waiting;
		}
		public void cookIt() {
			t.schedule(new TimerTask() {
				public void run() {
					state = OrderState.Done;
					try {
						busy.acquire();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					cookGui.DoPlateFood();
					try {
						busy.acquire();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					cookGui.DoGoHome();
					cooking.release();
					stateChanged();
				}
			},
			5000);
		}
	}
	
	public class MyMarket {
		public Market m;
		List<String> outOfFoods = new ArrayList<String>();
		public MyMarket(Market ma) {
			m = ma;
		}
		public void outOf(String f) {
			outOfFoods.add(f);
			stateChanged();
		}
		public boolean has(String f) {
			return !outOfFoods.contains(f);
		}
	}
	
	public String getName() {
		return name;
	}
	
	public int getQuantity(String name){
		return i.food.get(name);
	}
	
	
	 public void setQuantity(String name, int num){
		 i.food.put(name, num);
	 }

	
	public void addMarket(Market m){
		markets.add(new MyMarket(m));
		stateChanged();
	}
	
	public int numMarkets(){
		return markets.size();
	}
	
	public void emptyInventory(){
		Do("Emptying inventory");
		i.empty();
		stateChanged();
	}
	
	public void setGui(CookGui c){
		cookGui = c;
		c.setAgent(this);
	}
	
	private void waitTimer() {
		t.schedule(new TimerTask() {
			public void run() {
				stateChanged();
			}
		},
		5000);
	}

}
