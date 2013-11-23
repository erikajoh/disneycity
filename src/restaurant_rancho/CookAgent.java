package restaurant_rancho;

import agent_rancho.Agent;

import java.util.*;
import java.util.concurrent.Semaphore;

import restaurant_rancho.Order;
import restaurant_rancho.gui.CookGui;
import restaurant_rancho.interfaces.Person;

public class CookAgent extends Agent {
	
	public List<Order> orders;
	public enum orderState {pending, cooking, done};
	private String name;
	Hashtable<String, Integer> cookTimes;
	List<Food> foods;
	List<MyMarket> markets;
	List<MarketOrder> marketOrders;
	List<String> foodsUnavailable;
	List<WaiterAgent> waiters;
	CookGui gui;
	private Semaphore cooking = new Semaphore(0,true);
	int cookNum = 0;
	Person person;
	

	public CookAgent(String name) {
		super();
		this.name = name;
		markets = Collections.synchronizedList(new ArrayList<MyMarket>());
		marketOrders = Collections.synchronizedList(new ArrayList<MarketOrder>());
		orders = Collections.synchronizedList(new ArrayList<Order>());
		cookTimes = new Hashtable<String, Integer>();
		foods = Collections.synchronizedList(new ArrayList<Food>());
		waiters = Collections.synchronizedList(new ArrayList<WaiterAgent>());
		foodsUnavailable = Collections.synchronizedList(new ArrayList<String>());
		foods.add(new Food("Chicken", 7, 0, 7, 7000));
		foods.add(new Food("Steak", 7, 0, 7, 6000));
		foods.add(new Food("Salad", 7, 0, 7, 4000));
		foods.add(new Food("Pizza", 0, 0, 7, 7000));
		foods.add(new Food("Latte", 0, 0, 7, 2500));
		cookTimes.put("Chicken", 7000);
		cookTimes.put("Steak", 6000);
		cookTimes.put("Salad", 4000);
		cookTimes.put("Pizza", 7000);
		cookTimes.put("Latte", 2500);
		
		print("Initial inventory check");
		synchronized(foods) {
			for (Food f : foods) {
				if(f.amount <= f.low) {
					f.ordered = true;
					marketOrders.add(new MarketOrder(f.capacity-f.amount, f.choice));
				}
			}
		}
	}

	public String getName() {
		return name;
	}
	
	public void setPerson(Person p) {
		person = p;
	}

	public void setGui(CookGui cg) {
		gui = cg;
	}
	public List getOrders() {
		return orders;
	}
	
	public void addMarket(MarketAgent m) {
		markets.add(new MyMarket(m));
	}
	
	public void setInventory(int numAll, int numLatte) {
		if (numAll == numLatte) {
			synchronized(foods) {
				for (Food f : foods) {
					f.amount = numAll;
				}
			}
		}
		else 
			synchronized(foods) {
				for (Food f : foods) {
					if (f.choice == "Latte") {
						f.amount = numLatte;
					}
					else (f.amount) = numAll;
				}
			}
	}

	// Messages
	
	public void msgAtLoc() {
		cooking.release();
		stateChanged();
	}
	
	public void msgAddOrder(Order o) {
		orders.add(o);
		if (!waiters.contains(o.w)) waiters.add(o.w);
		stateChanged();	
	}
	
	public void msgHereIsFood(MarketAgent m, String choice, int amount) {
		MarketOrder mo = findMarketOrder(choice, m);
		Food f = findFood(choice);
		f.setOrdered(false);
		f.amount += amount;
		if (amount < mo.amount) {
			mo.market.removeFood(mo.choice);
			if (amount <= f.low) {
				marketOrders.add(new MarketOrder(f.capacity-f.amount, choice));
				f.setOrdered(true);
			}
		}
		if (amount != 0) {
			foodsUnavailable.remove(choice);
			synchronized(waiters) {
				for (WaiterAgent w : waiters) {
					w.msgUpdateMenu(foodsUnavailable);
				}
			}
			
		}
		marketOrders.remove(mo);
		stateChanged();
	}
	

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
	
		//rules 
	//	try{
			if (!orders.isEmpty() || !marketOrders.isEmpty()) {
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
							mo.os = moState.ordered;
							orderFromMarket(mo);
							return true;
						}
					}
				}
				return true;
			}
		//}
		//catch(Exception e) {
	    //	return false;
			
	 //	}
		return false;
	
	}

	// Actions

	private void cookIt(final Order o) {
		if (findFood(o.choice).amount == 0) {
			if (!foodsUnavailable.contains(o.choice)) foodsUnavailable.add(o.choice);
			o.w.msgOutOfFood(o);
			synchronized(waiters) {
				for (WaiterAgent w : waiters) w.msgUpdateMenu(foodsUnavailable);
			}
			print("We are out of " + o.choice);
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
			marketOrders.add(new MarketOrder(f.capacity-f.amount, f.choice));
			f.ordered =true;
		}
		
		stateChanged();
		
	}
	
	private void plateIt(Order o) {
		//print("Plated order, telling waiter order is ready ");
		DoGoToFood(o.cookingNum);
		gui.setText(o.choice.substring(0, 3), 1, o.cookingNum);
		DoGoToHome();
		(o.w).msgFoodIsReady(o);
		orders.remove(orders.indexOf(o));		
		stateChanged();
	}
	
	private void orderFromMarket(MarketOrder mo) {
		synchronized(markets) {
			for (MyMarket m : markets) {
				if (m.foodAvailable(mo.choice)) {
					mo.setMarket(m);
					print("Ordering " + mo.amount + " " + mo.choice + " from " + m.market.getName());
					m.market.msgNeedFood(this, mo.choice, mo.amount);
					return;
				}
			}
		}
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
	
	
	enum moState {pending, ordered};
	private class MarketOrder {
		int amount; 
		String choice;
		MyMarket market;
		boolean fulfilled;
		moState os;
		
		MarketOrder(int am, String c) {
			amount = am;
			choice = c;
			os = moState.pending;
			fulfilled = false;	
		}
		
		public void setMarket(MyMarket m) {
			market = m;
		}
		
	}
	
	private class MyMarket {
		List<String> foodsAvailable;
		MarketAgent market;
		
		MyMarket(MarketAgent m) {
			foodsAvailable = new ArrayList<String>();
			foodsAvailable.add("Pizza");
			foodsAvailable.add("Steak");
			foodsAvailable.add("Chicken");
			foodsAvailable.add("Salad");
			foodsAvailable.add("Latte");
			market = m;
		}
		
		public void removeFood(String food) {
			foodsAvailable.remove(food);
		}
		public boolean foodAvailable(String food) {
			synchronized(foodsAvailable) {
				for (String f : foodsAvailable) {
					if (f == food) {
						return true;
					}
				}
				return false;
			}
		}
	}
	
	private class Food {
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
	
	public MarketOrder findMarketOrder(String food, MarketAgent m) {
		synchronized(marketOrders) {
			for (MarketOrder mo : marketOrders) {
				if (mo.market.market == m && mo.choice == food) {
					return mo;
				}
			}
		}
		return null;
	}
	
	public MyMarket findMarket(MarketAgent m) {
		synchronized(markets) {
			for (MyMarket market : markets) {
				if (market.market == m)
					return market;
			}
		}
		return null;
	}

}
