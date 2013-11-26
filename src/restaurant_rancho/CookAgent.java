package restaurant_rancho;

import agent_rancho.Agent;

import java.util.*;
import java.util.concurrent.Semaphore;

import restaurant_rancho.Order;
import restaurant_rancho.CustomerAgent.AgentEvent;
import restaurant_rancho.CustomerAgent.AgentState;
import restaurant_rancho.gui.CookGui;
import simcity.PersonAgent;
import restaurant_rancho.gui.RestaurantRancho;
import market.Market;
import restaurant_rancho.ProducerConsumerMonitor;

public class CookAgent extends Agent {
	
	public List<Order> orders;
	public enum orderState {pending, cooking, done};
	private String name;
	Hashtable<String, Integer> cookTimes;
	List<Food> foods;
	List<MarketOrder> marketOrders = new ArrayList<MarketOrder> ();
 	List<WaiterAgent> waiters;
	CookGui gui;
	Timer checkTimer = new Timer();
	private Semaphore cooking = new Semaphore(0,true);
	int cookNum = 0;
	PersonAgent person;
	RestaurantRancho restaurant;
	Market market;
	private enum moState {pending, ordered};
	public boolean inMarket;
	int curID;
	boolean shiftDone = false;
	

	public CookAgent(String name, RestaurantRancho rest, Market m) {
		super();
		this.name = name;
		market = m;
		orders = Collections.synchronizedList(new ArrayList<Order>());
		cookTimes = new Hashtable<String, Integer>();
		foods = Collections.synchronizedList(new ArrayList<Food>());
		waiters = Collections.synchronizedList(new ArrayList<WaiterAgent>());
		foods.add(new Food("Citrus Fire-Grilled Chicken", 0, 0, 6, 7000));
		foods.add(new Food("Red Chile Enchilada Platter", 0, 0, 6, 6000));
		foods.add(new Food("Soft Tacos Monterrey", 0, 0, 6, 4000));
		foods.add(new Food("Burrito Sonora", 7, 0, 6, 7000));
		foods.add(new Food("Chicken Tortilla Soup", 7, 0, 6, 2500));
		cookTimes.put("Citrus Fire-Grilled Chicken", 7000);
		cookTimes.put("Red Chile Enchilada Platter", 6000);
		cookTimes.put("Soft Tacos Monterrey", 5000);
		cookTimes.put("Burrito Sonora", 5000);
		cookTimes.put("Chicken Tortilla Soup", 3500);
		restaurant = rest;
		inMarket = false;
		curID = 0;
	
	}

	public String getName() {
		return name;
	}
	
	public void setMarket(Market m) {
		print("setting market " + m.getName());
		market = m;
	}
	
	public void setPerson(PersonAgent p) {
		person = p;
	}

	public void setGui(CookGui cg) {
		gui = cg;
	}
	public List getOrders() {
		return orders;
	}
	

	// Messages
	
	public void msgShiftDone() {
		shiftDone = true;
		if (orders.size()==0) {
			person.msgStopWork(10);
		}
	}
	
	public void msgAtLoc() {
		cooking.release();
		stateChanged();
	}
	
	public void msgAddOrder(Order o) {
		orders.add(o);
		if (!waiters.contains(o.w)) waiters.add(o.w);
		stateChanged();	
	}
	
	public void msgHereIsOrder(String choice, int amount, int id) {
		print("Received a delivery from the market!");
		for (MarketOrder mo : marketOrders){
			if (mo.id == id && mo.amount == amount) {
				Food f = findFood(mo.food);
				f.amount = amount;
				marketOrders.remove(mo);
			} else if (mo.food == choice && mo.amount != 0) {
				Food f = findFood(mo.food);
				f.amount = amount + mo.amount;
				mo.amount -= amount;
			}
		}
	}
	

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
	
			if (!orders.isEmpty()) {
				synchronized(orders) {
					for (Order order :orders ) {
						if (order.os == orderState.done) {
							plateIt(order);
							return true;
						}
					}
				}
				synchronized(orders) {
					for (Order order : orders) {
						if (order.os == orderState.pending) {
							cookIt(order);
							return true;
						}
					}
				}
				synchronized(marketOrders) {
					for (MarketOrder mo : marketOrders){ 
						if (mo.os == moState.pending) {
							print("ordering " + mo.food );
							mo.os = moState.ordered;
							market.personAs(restaurant, 100, "Mexican", mo.amount, mo.id);
							return true;
						}
					}
				}
				return true;
			}
			if (shiftDone) {person.msgStopWork(10);}
			Order newO = restaurant.orderStand.remove();
			if (newO!=null) {orders.add(newO); print("order stand not empty, got order for "+ newO.choice); return true;}
			else {waitTimer();}
		return false;
	
	}

	// Actions

	private void cookIt(final Order o) {
		if (findFood(o.choice).amount == 0) {
			restaurant.getMenu().remove(o.choice);
			o.w.msgOutOfFood(o);
			synchronized(waiters) {
				for (WaiterAgent w : waiters) w.msgUpdateMenu();
			}
			print("We are out of " + o.choice);
			marketOrders.add(new MarketOrder(o.choice, findFood(o.choice).capacity));
			orders.remove(o);
			return;
		}
		DoGoToFridge();
		gui.setCurText(o.choice.substring(0, 3));
		DoGoToFood(cookNum%3);
		gui.setCurText(" ");
		gui.setText(o.choice.substring(0, 3), 0, cookNum%3);
		DoGoToHome();
		o.cookingNum = cookNum%3;
		cookNum++;
		o.os = orderState.cooking;
		o.timer.schedule(new TimerTask() {
			public void run() {
				print(o.choice + " done cooking");
				o.os = orderState.done;
				gui.setText("", 0, o.cookingNum);
				stateChanged();
			}
		},
		cookTimes.get(o.choice));
		Food f = findFood(o.choice);
		f.amount--;
		if (f.amount <= f.low ) {
			marketOrders.add(new MarketOrder(f.choice, f.capacity-f.amount));
			f.ordered = true;
		}
		
		stateChanged();
		
	}


	private void plateIt(Order o) {
		DoGoToFood(o.cookingNum);
		gui.setText(o.choice.substring(0, 3), 1, o.cookingNum);
		DoGoToHome();
		(o.w).msgFoodIsReady(o);
		orders.remove(orders.indexOf(o));		
		stateChanged();
	}
	
	
	//gui actions
	
	private void DoGoToHome() {
		gui.DoWalkToHome();
		try {
			cooking.acquire();
		}catch (InterruptedException e) {
			
		}
		
	}
	
	private void DoGoToFridge() {
		gui.DoWalkToFridge();
		try {
			cooking.acquire();
		} catch (InterruptedException e) {
			
		}
	}
	
	private void DoGoToFood(int loc) {
		gui.DoWalkToFood(loc);
		try {
			cooking.acquire();
		} catch (InterruptedException e) {
			
		}
	}
	
	private void waitTimer() {
		checkTimer.schedule(new TimerTask() {
			public void run() {
				stateChanged();
			}
		},
		5000);
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
	
	/*public MarketOrder findMarketOrder(String food) {
		
	}
	
	*/
}



