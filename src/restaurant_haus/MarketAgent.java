package restaurant_haus;

import agent_haus.Agent;
import restaurant_haus.gui.WaiterGui;
import restaurant_haus.interfaces.Cashier;
import restaurant_haus.interfaces.Market;

import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Restaurant Host Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the HostAgent. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class MarketAgent extends Agent implements Market {

	private String name;
	private CookAgent cook;
	int deliveryTime;
	double money;

	private class Order {
		String choice;
		int quantity;
		OrderState s;
		CashierAgent cashier;

		Order(String choice, int quantity, CashierAgent cashier) {
			this.choice = choice;
			this.quantity = quantity;
			this.cashier = cashier;
			s = OrderState.Placed;
		}
	}

	private enum OrderState {Placed, Delivering, Filled};

	List<Order> orders = Collections.synchronizedList(new ArrayList<Order>());
	Timer timer = new Timer();

	private class Food {
		String choice;
		int inventory;
		double price;

		Food(String choice, int inventory) {
			this.choice = choice;
			this.inventory = inventory;
			if(choice.equalsIgnoreCase("Steak")) {
				price = 7.99f;
			}
			if(choice.equalsIgnoreCase("Chicken")) {
				price = 5.49f;
			}
			if(choice.equalsIgnoreCase("Salad")) {
				price = 3.99f;
			}
			if(choice.equalsIgnoreCase("Pizza")) {
				price = 4.49f;
			}
		}
	}

	Map<String, Food> marketInventory = new HashMap<String, Food>();
	List<String> orderConfirmation = Collections.synchronizedList(new ArrayList<String>());

	public MarketAgent(String name, int time, int steak, int chicken, int pizza, int salad) {
		super();
		this.name = name;
		money = 0.00f;

		deliveryTime = time;//hack for delivery time

		marketInventory.put("steak", new Food("steak", steak));
		marketInventory.put("chicken", new Food("chicken", chicken));
		marketInventory.put("pizza", new Food("pizza", pizza));
		marketInventory.put("salad", new Food("salad", salad));
		//hack for inventories
	}

	public String getName() {
		return name;
	}

	// Messages

	public void msgPlaceOrder (String choice, int quantity, CashierAgent cashier) {
		orders.add(new Order(choice, quantity, cashier));
		stateChanged();
		//Add new order to list of orders
	}

	public void msgOrderReady (Order o) {
		o.s = OrderState.Filled;
		stateChanged();
	}

	public void msgDoYouHave(String food) {
		orderConfirmation.add(food);
		stateChanged();
	}
	
	public void msgOrderPayment(double money, boolean paidFull) {
		this.money += money;
		if(paidFull)
			print("Thank you for your business!");
		else
			print("What? You can't pay!? $10 late fee!");
	}
	
	public void msgFinalPayment() {
		print("Thank you for fulfilling your debt.");
	}
	
	public void msgIncrementalPayment(double money) {
		print("Keep up the good work paying off the debt.");
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

		synchronized(orderConfirmation) {
			for (String order : orderConfirmation) {
				sendConfirmation(order);
				return true;
			}
		}

		synchronized(orders) {
			for(Order o : orders) {
				if(o.s == OrderState.Filled) {
					sendOrder(o);
					return true;
				}
			}
		}

		synchronized(orders) {
			for(Order o : orders) {
				if(o.s == OrderState.Placed) {
					startOrder(o);
					return true;
				}
			}
		}

		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	private class MarketTask extends TimerTask {
		Order o;
		MarketAgent market;

		public void run() {
			market.msgOrderReady(o);
		}

		MarketTask(Order o, MarketAgent market) {
			this.o = o;
			this.market = market;
		}
	}

	// Actions
	private void startOrder(Order o) {
		Food currentOrder = marketInventory.get(o.choice);
		
		/*
		if (currentOrder.inventory <= 0) {
			cook.msgOrderFromAnother(o.choice, this);
			print("Sorry I ran out.");
			orders.remove(o);
			return;
		}
		*/
		

		if (currentOrder.inventory >= o.quantity) {//If the market has more than enough food to fill order
			timer.schedule(new MarketTask(o, this), deliveryTime);

			print("Delivering order of " + String.valueOf(o.quantity) + " " + o.choice + ". It'll be there in " + String.valueOf(deliveryTime/1000) + " seconds.");
			o.s = OrderState.Delivering;

			currentOrder.inventory -= o.quantity;
			return;
		}

		else {
			o.quantity = currentOrder.inventory;
			timer.schedule(new MarketTask(o, this), deliveryTime);

			print("I only have " + String.valueOf(o.quantity) + " " + o.choice + ". It'll be there in " + String.valueOf(deliveryTime/1000) + "seconds.");
			cook.msgOrderFromAnother(o.choice, this);
			o.s = OrderState.Delivering;
			currentOrder.inventory = 0;
			return;
		}
		/*Start the timer by checking the map of food name to cooking time
		 * Set the state of the Order to cooking
		 */
	}

	private void sendOrder(Order o) {
		cook.msgOrderDelivered(o.choice, o.quantity);
		print("Here's your delivery of " + o.choice + ".");
		o.cashier.msgYourBillIs(this, marketInventory.get(o.choice).price * o.quantity);
		orders.remove(o);
		/*o.w.msgFoodReady(o.choice,o.table)
		 * Orders.remove(o);
		 */
	}

	private void sendConfirmation(String order) {
		if(marketInventory.get(order).inventory > 0) {
			print("Yes, we do.");
			cook.msgCanFill(order, true, this);
		}
		else {
			print("No, we don't.");
			cook.msgCanFill(order, false, this);
		}
		orderConfirmation.remove(order);
	}

	//Utilities
	public void setCook (CookAgent cook) {
		this.cook = cook;
	}

	public boolean checkStock(String food) {
		if(marketInventory.get(food).inventory > 0) {
			return true;
		}
		return false;
	}
}

