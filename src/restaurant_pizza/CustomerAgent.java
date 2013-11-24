package restaurant_pizza;

import restaurant_pizza.gui.CustomerGui;
import restaurant_pizza.interfaces.Cashier;
import restaurant_pizza.interfaces.Customer;
import restaurant_pizza.interfaces.Waiter;
import agent_pizza.Agent;
import agent_pizza.Constants;
import simcity.RestMenu;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

public class CustomerAgent extends Agent implements Customer {
	
	// ***** DATA *****
	
	private RestMenu menu;
	private String name;
	private String order = null;
	private int hungerLevel = 5; // determines length of meal
	private int orderTime = 2; // determines how long it takes for customer to choose
	private int tableNumber = -1;
	private int foodPointer = 0; // helps customer keep track of what has been ordered already
	
	// money scenario
	private double money;
	private int maxNumCents = 2500; // max amount of money in cents customer can have: $25
	private boolean flaky = false; // false: leave if can't afford anything; true: will order no matter what
	
	private double probImpatient = 0.5;
	
	Timer timer = new Timer();
	private CustomerGui customerGui;

	// agent correspondents
	private HostAgent host;
	private WaiterAgent waiter;
	private Cashier cashier;

	private Check myCheck = null;
	
	public enum AgentState {DoingNothing, WaitingToBeSeated, FollowingWaiter, ReadingMenu,
		ReadyToOrder, GivingOrder, WaitingForFood, Eating, ReadyToLeaveTable, GoingToCashier,
		CashierProcessing, Leaving};
		
	private AgentState state = AgentState.DoingNothing; //The start state

	public enum AgentEvent {none, gotHungry, followWaiter, seated, orderDecided, 
		waiterReadyToTakeOrder, waiterReadyToTakeOrderAgain, waiterBroughtFood, doneEating,
		waiterBroughtCheck, atCashier, leaving, cannotAffordAnything};
	private AgentEvent event = AgentEvent.none;

	/**
	 * Constructor for CustomerAgent class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the customergui so the customer can send it messages
	 */
	public CustomerAgent(String name) {
		super();
		this.name = name;
		
	}

	public void setHost(HostAgent aHost) {
		host = aHost;
	}
	public void setMoney(double money) {
		this.money = money;
	}
	public void setWaiter(Waiter aWaiter) {
		waiter = (WaiterAgent)aWaiter;
	}
	
	public void setCashier(Cashier c) {
		cashier = c;
	}

	public String getCustomerName() {
		return name;
	}
	
	// ***** MESSAGES *****

	public void msgGotHungry() {//from animation
		print("I'm hungry");
		event = AgentEvent.gotHungry;
		foodPointer = 0;
		stateChanged();
	}
	
	public void msgMustWait() {
		print("msgMustWait() called");
		if(Math.random() < probImpatient) {
			event = AgentEvent.leaving;
			stateChanged();
		}
	}

	public void msgSitAtTable(WaiterAgent w, int tableNumber, RestMenu m) {
		print("msgSitAtTable() received from Waiter " + w.getName());
		waiter = w;
		this.tableNumber = tableNumber;
		menu = m;
		event = AgentEvent.followWaiter;
		stateChanged();
	}

	public void msgCannotAffordAnything() {
		print("msgCannotAffordAnything() received");
		event = AgentEvent.cannotAffordAnything;
		stateChanged();
	}

	public void msgWhatWouldYouLike() {
		print("msgWhatWouldYouLike() received");
		event = AgentEvent.waiterReadyToTakeOrder;
		stateChanged();
	}

	public void msgWeAreOutOfThisOrder() {
		print("msgWeAreOutOfThisOrder() received");
		event = AgentEvent.waiterReadyToTakeOrderAgain;
		stateChanged();
	}

	public void msgFoodArrived() {
		print("msgFoodArrived() received");
		event = AgentEvent.waiterBroughtFood;
		stateChanged();
	}

	public void msgHereIsCheck(Check aCheck) {
		print("msgHereIsCheck() received");
		myCheck = aCheck;
		event = AgentEvent.waiterBroughtCheck;
		stateChanged();
	}
	
	@Override
	public void msgPaymentApproved() {
		print("msgPaymentApproved() received");
		// This is where payment gets deducted
		if(myCheck != null)
			money -= myCheck.amountDue;
		event = AgentEvent.leaving;
		stateChanged();
	}

	@Override
	public void msgPaymentInvalid() {
		print("msgPaymentInvalid() received");
		//TODO: what if payment invalid?

		event = AgentEvent.leaving;
		stateChanged();
	}

	public void msgLeaveRestaurant() {
		print("msgLeaveRestaurant() received");
		event = AgentEvent.leaving;
		stateChanged();
	}

	public void msgAnimationFinishedGoToSeat() {
		//from animation
		print("msgAnimationFinishedGoToSeat() received");
		event = AgentEvent.seated;
		stateChanged();
	}

	public void msgAnimationFinishedGoToCashier() {
		//from animation
		print("msgAnimationFinishedGoToCashier() received");
		event = AgentEvent.atCashier;
		stateChanged();
	}

	public void msgAnimationFinishedLeaveRestaurant() {
		//from animation
		print("msgAnimationFinishedLeaveRestaurant() received");
		event = AgentEvent.none; 	
		stateChanged();
	}
	
	// ***** SCHEDULER *****
	
	protected boolean pickAndExecuteAnAction() {
		if (state == AgentState.DoingNothing && event == AgentEvent.gotHungry) {
			state = AgentState.WaitingToBeSeated;
			GoToRestaurant();
			return true;
		}
		if(state == AgentState.WaitingToBeSeated && event == AgentEvent.leaving) {
			state = AgentState.DoingNothing;
			host.msgCannotWait(this);
			LeaveTable();
			return true;
		}
		if (state == AgentState.WaitingToBeSeated && event == AgentEvent.followWaiter) {
			state = AgentState.FollowingWaiter;
			GoingToTable();
			return true;
		}
		if (state == AgentState.FollowingWaiter && event == AgentEvent.seated) {
			state = AgentState.ReadingMenu;
			ReadingMenu();
			return true;
		}
		if(state == AgentState.ReadingMenu && event == AgentEvent.orderDecided) {
			state = AgentState.ReadyToOrder;
			CallingWaiter();
			return true;
		}
		if(state == AgentState.ReadyToOrder && event == AgentEvent.waiterReadyToTakeOrder) {
			state = AgentState.GivingOrder;
			GiveOrder();
			return true;
		}
		if(state == AgentState.WaitingForFood && event == AgentEvent.waiterReadyToTakeOrderAgain) {
			state = AgentState.ReadingMenu;
			ReadingMenu();
			return true;
		}
		if(state == AgentState.WaitingForFood && event == AgentEvent.waiterBroughtFood) {
			state = AgentState.Eating;
			EatFood();
			return true;
		}
		if (state == AgentState.Eating && event == AgentEvent.doneEating) {
			state = AgentState.ReadyToLeaveTable;
			CallWaiterForCheck();
			return true;
		}
		if (state == AgentState.ReadyToLeaveTable && event == AgentEvent.waiterBroughtCheck) {
			state = AgentState.GoingToCashier;
			GoToCashier();
			return true;
		}
		if (state == AgentState.GoingToCashier && event == AgentEvent.atCashier) {
			state = AgentState.CashierProcessing;
			double amountOnHand = (money < 0) ? 0 : myCheck.amountDue;
			cashier.msgPayingMyCheck(this, myCheck, amountOnHand);
			return true;
		}
		if (state == AgentState.CashierProcessing && event == AgentEvent.leaving) {
			state = AgentState.DoingNothing;
			LeaveTable();
			return true;
		}
		// Special rules
		if(event == AgentEvent.cannotAffordAnything) {
			state = AgentState.DoingNothing;
			waiter.msgLeavingRestaurant(this);
			LeaveTable();
		}
		return false;
	}

	// ***** ACTIONS *****

	private void GoToRestaurant() {
		Do("Going to restaurant");
		customerGui.DoDisplayOrder("" + money);
		customerGui.DoGoToWaitingArea();
		host.msgIWantFood(this);
	}

	private void GoingToTable() {
		Do("Being seated. Going to table");
		customerGui.DoGoToSeat(tableNumber);
	}
	
	private void ReadingMenu() {
		Do("Reading menu and deciding order...");
		
		timer.schedule(new TimerTask() {
			public void run() {
				print("Done deciding");
				event = AgentEvent.orderDecided;
				stateChanged();
			}
		},
		orderTime * Constants.SECOND); //how long to wait before running task
	}
	
	private void CallingWaiter() {
		Do("Calling waiter");
		waiter.msgIAmReadyToOrder(this);
	}
	
	private void GiveOrder() {
		state = AgentState.WaitingForFood;
		
		//TODO: Hack, find item with minimum value next time via iterating through the key
		//Hack - demonstrates inventory running out
		Random rand = new Random();
		print(" " + menu.menuList.size());
		int randomOrderIndex = rand.nextInt(menu.menuList.size());
		order = menu.menuList.get(randomOrderIndex);
		
		customerGui.DoDisplayOrder(order + '?');
		Do("I'M ORDERING " + order + "!!!");
		waiter.msgGivingMyOrder(this, order);
	}

	private void EatFood() {
		customerGui.DoDisplayOrder(order);
		Do("Eating Food");
		timer.schedule(new TimerTask() {
			public void run() {
				print("Finished eating");
				event = AgentEvent.doneEating;
				stateChanged();
			}
		},
		hungerLevel * Constants.SECOND); //how long to wait before running task
	}
	
	private void CallWaiterForCheck() {
		print("Ready to leave");
		waiter.msgNeedCheck(this);
	}

	private void GoToCashier() {
		Do("Calling action GoToCashier");
		customerGui.DoGoToCashier();
	}

	private void LeaveTable() {
		order = "";
		customerGui.DoDisplayOrder("");
		Do("Leaving.");
		customerGui.DoExitRestaurant();
	}

	// Accessors, etc.

	@Override
	public String getName() {
		return name;
	}
	
	public int getHungerLevel() {
		return hungerLevel;
	}

	public void setHungerLevel(int hungerLevel) {
		this.hungerLevel = hungerLevel;
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
}
