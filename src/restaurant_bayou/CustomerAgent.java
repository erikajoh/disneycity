package restaurant_bayou;

import restaurant_bayou.CashierAgent.Check;
import restaurant_bayou.CookAgent.OrderState;
import restaurant_bayou.HostAgent.Menu;
import restaurant_bayou.HostAgent.Table;
import restaurant_bayou.gui.CustomerGui;
import restaurant_bayou.gui.RestaurantBayouGui;
import restaurant_bayou.interfaces.Customer;
import agent_bayou.Agent;
import simcity.PersonAgent;
import simcity.RestMenu;

import java.util.*;

/**
 * Restaurant customer agent.
 */
public class CustomerAgent extends Agent implements Customer {
	private String name;
	private int hungerLevel = 10;        // determines length of meal
	Timer timer = new Timer();
	private CustomerGui customerGui;
	private RestMenu menu;
	private List<String> unavailableFood =  new ArrayList<String>();
	private String choice;
	private Check myCheck;
	private Wallet wallet;
	private int dishDoingTime = 0;
	private Timer t = new Timer();
	private PersonAgent person;

	private HostAgent host;
	private WaiterAgent waiter;
	
	public int table;

	public enum AgentState
	{DoingNothing, Waiting, BeingSeated, Seated, Ordered, WaitingForOrder, Eating, WaitingForCheck, Paying, Leaving};
	private AgentState state = AgentState.DoingNothing;

	public enum AgentEvent 
	{none, gotHungry, followHost, seated, orderFood, orderTaken, foodArrived, doneEating, rcvdCheck, arrivedAtCashier, donePaying, doDishes, doneLeaving};
	AgentEvent event = AgentEvent.none;
	
	/**
	 * Constructor for CustomerAgent class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the customergui so the customer can send it messages
	 */
	public CustomerAgent(String name, double amt){
		super();
		this.name = name;
		if (name.equalsIgnoreCase("broke") || name.equalsIgnoreCase("bad")) wallet = new Wallet(0); //hack to test customer's ability to pay
		else if (name.equalsIgnoreCase("cheap")) wallet = new Wallet(7.5);
		else wallet = new Wallet(amt);
	}

	/**
	 * hack to establish connection to Host agent.
	 */
	public void setHost(HostAgent host) {
		this.host = host;
	}
	
	public void setPerson(PersonAgent p) {
		person = p;
	}
	
	public void gotHungry() {
		event = AgentEvent.gotHungry;
		stateChanged();
	}

	public void msgFollowMeToTable(WaiterAgent w, RestMenu m, List<String> uf) {
		waiter = w;
		menu = m;
		unavailableFood = uf;
		event = AgentEvent.followHost;
		stateChanged();
	}
	
	public void msgAnimationFinishedGoToSeat() {
		//from animation
		event = AgentEvent.seated;
		stateChanged();
	}
	
	public void msgWhatWouldYouLike() {
		event = AgentEvent.orderFood;
		stateChanged();
	}
	
	public void msgOrderHasBeenReceived() {
		event = AgentEvent.orderTaken;
		stateChanged();
	}
	
	public void msgHereIsYourFood() {
		event = AgentEvent.foodArrived;
		stateChanged();
	}

	public void msgAnimationFinishedLeaveRestaurant() {
		//from animation
		event = AgentEvent.doneLeaving;
		stateChanged();
	}
	
	public void msgAnimationFinishedGoToCashier() {
		//from animation
		event = AgentEvent.arrivedAtCashier;
		stateChanged();
	}
	
	public void msgPleaseReorder(List<String> uf) {
		state = AgentState.Seated;
		event = AgentEvent.orderFood;
		unavailableFood = uf;
		stateChanged();
	}
	
	public void msgHereIsYourCheck(Check c) {
		event = AgentEvent.rcvdCheck;
		myCheck = c;
		stateChanged();
	}
	
	public void msgHereIsChange(double amt) {
		event = AgentEvent.donePaying;
		wallet.update(amt);
		stateChanged();
	}
	
	public void msgDoDishesAsPunishment(int time) {
		event = AgentEvent.doDishes;
		dishDoingTime = time;
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		//	CustomerAgent is a finite state machine
		try {
			if (state == AgentState.DoingNothing && event == AgentEvent.gotHungry){
				if (host.isFull() && !name.equalsIgnoreCase("wait")) {
					customerGui.setEnabled();
					customerGui.DoExitRestaurant();
					return true;
				}
				state = AgentState.Waiting;
				GoToRestaurant();
				return true;
			}
			if (state == AgentState.Waiting && event == AgentEvent.followHost){
				state = AgentState.BeingSeated;
				SitDown();
				return true;
			}
			if (state == AgentState.BeingSeated && event == AgentEvent.seated){
				state = AgentState.Seated;
				return true;
			}
			if (state == AgentState.Seated && event == AgentEvent.orderFood){
				state = AgentState.Ordered;
				OrderFood();
				return true;
			}
			if (state == AgentState.Ordered && event == AgentEvent.orderTaken){
				state = AgentState.WaitingForOrder;
				return true;
			}
			if (state == AgentState.WaitingForOrder && event == AgentEvent.foodArrived){
				state = AgentState.Eating;
				EatFood();
				return true;
			}
	
			if (state == AgentState.Eating && event == AgentEvent.doneEating){
				state = AgentState.WaitingForCheck;
				customerGui.setText("");
				waiter.msgDoneEating(this);
				return true;
			}
			if (state == AgentState.WaitingForCheck && event == AgentEvent.rcvdCheck){
				state = AgentState.Paying;
				LeaveTableToGoPay();
				return true;
			}
			if (state == AgentState.Paying && event == AgentEvent.arrivedAtCashier){
				host.cashier.msgHereIsMoney(this, wallet.getAmt());
				return true;
			}
			if (state == AgentState.Paying && event == AgentEvent.doDishes){
				DoDishes();
				return true;
			}
			if (state == AgentState.Paying && event == AgentEvent.donePaying){
				state = AgentState.Leaving;
				LeaveRestaurant();
				return true;
			}
			if (state == AgentState.Leaving && event == AgentEvent.doneLeaving){
				state = AgentState.DoingNothing;
				waiter.msgDoneLeaving(this);
				return true;
			}
			return false;
		} catch (ConcurrentModificationException e) {
			return false;
		}
	}
	
	private void OrderFood() {
		choice = name.toLowerCase();
		int count = 0;
		while (!menu.menuList.contains(choice) || unavailableFood.contains(choice) || (menu.menuItems.get(choice) > wallet.getAmt() && !name.equalsIgnoreCase("bad"))) {
			if (count == menu.menuList.size()) {
				state = AgentState.Leaving;
				LeaveRestaurant();
				return;
			}
		//	Random rand = new Random(System.currentTimeMillis());
		//	int det = Math.abs(rand.nextInt()%(menu.menuList.size()));
			choice = menu.menuList.get(count++);
			Do(""+choice);
		}
		waiter.msgHereIsMyChoice(this, choice);
		customerGui.setText(choice+"?");
	}

	private void GoToRestaurant() {
		Do("Going to restaurant");
		host.msgIAmHere(this);
	}

	private void SitDown() {
		Do("Being seated. Going to table");
		for (Table t: host.tables) {
			if (t.getOccupant() == this){
				this.table = t.num;
				customerGui.DoGoToSeat(t.num);
				break;
			}
		}
		waiter.msgImReadyToOrder(this);
	}

	private void EatFood() {
		customerGui.setText(choice);
		Do("Eating Food");
		timer.schedule(new TimerTask() {
			public void run() {
				print("Done eating");
				event = AgentEvent.doneEating;
				stateChanged();
			}
		},
		getHungerLevel() * 1000);
	}
	
	private void LeaveTableToGoPay() {
		Do("Going to pay");
		waiter.msgLeavingTable(this);
		customerGui.setText("check");
		customerGui.DoGoToCashier();
	}

	private void LeaveRestaurant() {
		Do("Leaving.");
		customerGui.setText("");
		customerGui.DoExitRestaurant();
	}
	
	private void DoDishes() {
		Do("Finished doing dishes as punishment");
		event = AgentEvent.donePaying;
		stateChanged();
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isWaiting() {
		return (state == AgentState.Waiting);
	}
	
	public boolean isSeated() {
		return (state == AgentState.Seated);
	}
	
	public boolean hasOrdered() {
		return (state == AgentState.Ordered);
	}
	
	public boolean isWaitingForOrder() {
		return (state == AgentState.WaitingForOrder);
	}
	
	public boolean isWaitingForCheck() {
		return (state == AgentState.WaitingForCheck);
	}
	
	public int getHungerLevel() {
		return hungerLevel;
	}

	public void setHungerLevel(int hungerLevel) {
		this.hungerLevel = hungerLevel;
	}
	
	public String getChoice() {
		return choice;
	}

	public String toString() {
		return "customer " + getName();
	}

	public void setGui(CustomerGui g) {
		customerGui = g;
	}

	public CustomerGui getGui() {
		return customerGui;
	}
	
	private class Wallet{
		double balance;
		Wallet(double amt){
			balance = amt;
		}
//		public void increase(double amt){
//			balance += amt;
//		}
//		public void decrease(double amt){
//			balance -= amt;
//		}
		public void update(double amt){
			balance = amt;
		}
		public double getAmt() {
			return balance;
		}
	}
}

