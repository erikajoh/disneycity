package restaurant_bayou;

import agent_bayou.Agent;

import java.util.*;
import java.util.concurrent.Semaphore;

import restaurant_bayou.CashierAgent.CheckState;
import restaurant_bayou.CustomerAgent.AgentEvent;
import restaurant_bayou.HostAgent.Menu;
import restaurant_bayou.interfaces.Market;
import simcity.RestMenu;

public class MarketAgent extends Agent implements Market {
	private String name;
	private List<Request> requests =  Collections.synchronizedList(new ArrayList<Request>());
	private Inventory i = new Inventory();
	private List<Payment> pendingPayments =  Collections.synchronizedList(new ArrayList<Payment>());
	private List<Payment> rcvdPayments =  Collections.synchronizedList(new ArrayList<Payment>());
	
	/**
	 * Constructor for CookAgent class
	 *
	 * @param name name of the cook
	 */
	public MarketAgent(String name, RestMenu menu){
		super();
		this.name = name;
		for (String item: menu.menuList) {
			print("heyo");
			i.add(item, 3);
			i.setCost(item, 10);
		}
	}

	public void msgNeedFood(CookAgent c, CashierAgent ca, String f, int amt){
		Do("market received msgNeedFood "+f);
		requests.add(new Request(c, ca, f, amt));
		stateChanged();
	}
	
	public void msgHereIsPayment(CashierAgent c, double amt){
		rcvdPayments.add(new Payment(c, amt));
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		synchronized(rcvdPayments){
			for (Payment p: rcvdPayments) {
				for (Payment p2: pendingPayments) {
					if (p.cashier == p2.cashier) {
						if (p.amt < p2.amt) {
							Do("Cashier can't pay!");
							p.cashier.msgYouAreCutoff(this);
						} else {
							p.cashier.msgHereIsChange(p.amt - p2.amt);
						}
						rcvdPayments.remove(p);
						pendingPayments.remove(p2);
						return true;
					}
				}
			}
		}
		synchronized(requests){
			for (Request r: requests) {
				if (r.state == RequestState.Done) {
					r.state = RequestState.Sent;
					r.cook.msgHereIsMoreFood(this, r.choice, r.amount);
					r.cashier.msgHereIsBill(this, (i.getCost(r.choice))*(r.amount));
					pendingPayments.add(new Payment(r.cashier, i.getCost(r.choice)*(r.amount)));
					requests.remove(r);
					return true;
				}
			}
		}
		synchronized(requests){
			for (Request r: requests) {
				if (r.state == RequestState.Waiting) {
					int rAmt = i.getAmt(r.choice);
					if (rAmt < r.amount && rAmt > 0) r.amount = rAmt;
					else if (rAmt == 0) {
						r.amount = 0;
						r.state = RequestState.Done;
						return true;
					}
					i.decrease(r.choice, r.amount);
					r.state = RequestState.FulfillingOrder;
					r.fulfillOrder();
					return true;
				}
			}
		}
		return false;
	}
	
	public class Inventory {
		Hashtable<String, Integer> food = new Hashtable<String, Integer>();
		Hashtable<String, Double> costs = new Hashtable<String, Double>();
		public double getCost(String f) {
			return costs.get(f);
		}
		public void setCost(String f, double c) {
			costs.put(f,  c);
		}
		public void decrease(String f, int amt) {
			food.put(f, food.get(f)-amt);
		}
		public void add(String f, int a) {
			food.put(f, a);
		}
		public int getAmt(String f) {
			return food.get(f);
		}
		public void empty() {
			Enumeration<String> foodKeys = food.keys();
			while(foodKeys.hasMoreElements()) {
			    String f = foodKeys.nextElement();
			    int amt = food.get(f);
			    if(amt != 0) {
			    	food.put(f, 0);
			    }
			}
		}
	}
	
	public enum RequestState {Waiting, FulfillingOrder, Done, Sent;}
	public class Request {
		CookAgent cook;
		CashierAgent cashier;
		String choice;
		int amount;
		RequestState state;
		Timer t = new Timer();
		public Request(CookAgent ca, CashierAgent caa, String c, int amt) {
			cook = ca;
			cashier = caa;
			choice = c;
			amount = amt;
			state = RequestState.Waiting;
		}
		public void fulfillOrder() {
			t.schedule(new TimerTask() {
				public void run() {
					state = RequestState.Done;
					stateChanged();
				}
			},
			100);
		}
	}
	
	public class Payment {
		CashierAgent cashier;
		double amt;
		Payment(CashierAgent c, double a){
			cashier = c;
			amt = a;
		}
	}
	
	public String getName() {
		return name;
	}

	public void emptyInventory() {
		Do("Emptying inventory");
		i.empty();
	}

}
