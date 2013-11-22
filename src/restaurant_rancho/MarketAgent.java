package restaurant_rancho;

import agent_rancho.Agent;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Hashtable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import restaurant_rancho.CookAgent.orderState;
import restaurant_rancho.CustomerAgent.AgentEvent;
import restaurant_rancho.interfaces.Market;

public class MarketAgent extends Agent implements Market {
	
	private String name;
	List<MyFood> foods;
	List<FoodOrder> orders;
	Timer orderTimer = new Timer();
	CashierAgent cashier;
	double money;

	public MarketAgent(String name, int amount, int amount2, int amount3, int amount4, int amount5) {
		super();
		this.name = name;
		foods = new ArrayList<MyFood>();
		foods.add(new MyFood("Chicken", amount, 4));
		foods.add(new MyFood("Steak", amount2, 6));
		foods.add(new MyFood("Salad", amount3, 2));
		foods.add(new MyFood("Pizza", amount4, 3));
		foods.add(new MyFood("Latte", amount5, 2));
		orders = new ArrayList<FoodOrder>();
		
	}

	public String getMaitreDName() {
		return name;
	}

	public String getName() {
		return name;
	}
	
	public void setCashier(CashierAgent c) {
		cashier = c;
	}

	// Messages
	public void msgNeedFood(CookAgent c, String food, int am) {
		//print("got order for " + food);
		MyFood mf = findFood(food);
		if (mf.amount==0) {
			orders.add(new FoodOrder(c, mf, 0, false, orders.size()+1));
			stateChanged();
		}
		else if (mf.amount < am) {
			orders.add(new FoodOrder(c, mf, mf.amount, false, orders.size() +1));
			mf.amount = 0;
			stateChanged();
		}
		else {
			orders.add(new FoodOrder(c, mf, am, true, orders.size() +1));
			mf.amount-=am;
			stateChanged();
		}
	}
	
	public void msgHereIsPayment(double amount, int oNum) {
		money += amount;
		print("got paid " + amount+ " dollars, now have " + money + " dollars");
	}


	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
	
		//rules  
		try{
		if (!orders.isEmpty()) {
			for (FoodOrder order :orders ) {
				if (order.os == orderState.waiting) {
					order.os = orderState.pending;
					prepare(order);
					return true;
				}
				if (order.os == orderState.ready){
					fulfill(order);
					return true;
				}
			}
		}
		}
		catch(ConcurrentModificationException e) {
			return false;
		}

		return false;
	
	}

	// Actions
	
	public void prepare(final FoodOrder o) {
		orderTimer.schedule(new TimerTask() {
			public void run() {
				o.billAmount = o.amount * o.food.price;
				o.os = orderState.ready;
				stateChanged();
			}
		},
		8500);
		
	}
	
	
	public void fulfill(FoodOrder o) {
		if (o.canFulfill == true) {
			print("Here is " + o.amount + " " + o.food.choice + ", bill is " + o.billAmount);
		}
		else if (o.canFulfill == false && o.amount!=0) {
			print("Sorry, we cannot fully complete your order. Here is " + o.amount + " " + o.food.choice + ", bill is " + o.billAmount);
		}
		else {
			print("Sorry, we have no " + o.food.choice);
		}
		if (o.amount!=0) cashier.msgHereIsMarketBill(this, o.billAmount, o.orderNum);
		o.cook.msgHereIsFood(this, o.food.choice, o.amount);
		orders.remove(o);
		stateChanged();
		
	}
	
	private class MyFood {
		String choice;
		int amount;
		double price;

		
		MyFood (String ch, int am, double p) {
			choice = ch;
			amount = am;
			price = p;
		}
		
		public int getAmount() {
			return amount;
		}
	}
	
	public enum orderState {waiting, pending, paid, ready};
	private class FoodOrder {
		MyFood food; 
		CookAgent cook;
		int amount;
		boolean canFulfill;
		orderState os;
		double billAmount;
		int orderNum;
		
		FoodOrder (CookAgent c, MyFood f, int am, boolean cF, int num) {
			cook = c;
			food = f;
			amount = am;
			canFulfill= cF;
			os = orderState.waiting;
			billAmount = 0;
			orderNum = num;
		}
	}

	//utilities
	public MyFood findFood(String ch) {
		for (MyFood f : foods) {
			if (f.choice==ch) {
				return f;
			}
		}
		return null;
	}
	
	public FoodOrder findFoodOrder(int oNum) {
		for (FoodOrder fo : orders) {
			if (fo.orderNum==oNum) {
				return fo;
			}
		}
		return null;
	}

}
