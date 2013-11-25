package restaurant_haus;

import restaurant_haus.gui.CustomerGui;
import simcity.PersonAgent;
import restaurant_haus.interfaces.Customer;
import agent_haus.Agent;

import java.awt.Point;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

/**
 * Restaurant customer agent.
 */
public class CustomerAgent extends Agent implements Customer {
	private String name;
	private int hungerLevel = 5;        // determines length of meal
	Timer timer = new Timer();
	private CustomerGui customerGui;
	public PersonAgent person;


	Point tablePos;
	//int xPos;
	//int yPos;

	WaiterAgent w;
	CashierAgent cashier;
	Menu m;
	String choice = "";
	String foodReceived;
	double money;
	double moneyOwed;

	Random RNG = new Random();

	// agent correspondents
	private HostAgent host;

	//    private boolean isHungry = false; //hack for gui
	public enum AgentState
	{DoingNothing, WaitingInRestaurant, BeingSeated, Ordering, Seated, Eating, DoneEating, Leaving, Reorder, WaitingForCheck};
	private AgentState state = AgentState.DoingNothing;//The start state

	public enum AgentEvent 
	{none, gotHungry, followWaiter, seated, askedOrder, gotFood, doneEating, doneLeaving, ReceivedCheck, gotMenu, restFull};
	AgentEvent event = AgentEvent.none;

	/**
	 * Constructor for CustomerAgent class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the customergui so the customer can send it messages
	 */
	public CustomerAgent(String name){
		super();
		this.name = name;
		
		//money = 20.00;//Hack to ensure customer has enough money
		moneyOwed = 0;
		
		//hacks to test scenarios
		if(name.equalsIgnoreCase("Hobo Joe")) {
			money = 0.37;
		}
		
		if(name.equalsIgnoreCase("College Student")) {
			money = 10.00;
		}
		
		if(name.equalsIgnoreCase("Vegan Steve")) {
			money = 8.00;
		}
	}

	/**
	 * hack to establish connection to Host agent.
	 */
	public void setHost(HostAgent host) {
		this.host = host;
	}
	
	public void setMoney(double cash) {
		money = cash;
	}

	public void setPerson(PersonAgent p) {
		person = p;
	}
	public String getCustomerName() {
		return name;
	}
	// Messages

	public void gotHungry() {//from animation
		print("I'm hungry");
		event = AgentEvent.gotHungry;
		choice = "";
		stateChanged();
	}

	public void msgSitAtTable(WaiterAgent w, Menu m, Point p) {
		print("Received msgSitAtTable");
		event = AgentEvent.followWaiter;
		this.w = w;
		this.m = m;

		tablePos = p;

		/* Deprecated Animation
		this.xPos = xPos;
		this.yPos = yPos;
		 */
		stateChanged();
	}

	public void msgWhatDoYouWant() {
		event = AgentEvent.askedOrder;
		stateChanged();
	}

	public void msgFoodReceived(String choice) {
		event = AgentEvent.gotFood;
		foodReceived = this.choice;//hack for now. can be used to implement wrong orders later
		stateChanged();
	}

	public void msgAnimationFinishedGoToSeat() {
		//from animation
		event = AgentEvent.seated;
		stateChanged();
	}
	public void msgAnimationFinishedLeaveRestaurant() {
		//from animation
		event = AgentEvent.doneLeaving;
		stateChanged();
	}

	public void msgPleaseReorder() {
		state = AgentState.Reorder;
		stateChanged();
	}

	public void msgHereIsCheck(double check, CashierAgent cashier) {
		this.cashier = cashier;
		event = AgentEvent.ReceivedCheck;
		moneyOwed = check;
		stateChanged();
	}
	
	public void msgHereIsMenu(Menu m, boolean freeTable) {
		this.m = m;
		event = AgentEvent.gotMenu;
		if(!freeTable) {
			event = AgentEvent.restFull;
		}
		stateChanged();
	}
	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		//	CustomerAgent is a finite state machine

		if(isPaused) {
			try {
				pauseSem.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (state == AgentState.DoingNothing & event == AgentEvent.gotHungry ) {
			goToRestaurant();
			return true;
		}
		
		if (state == AgentState.WaitingInRestaurant & event == AgentEvent.restFull) {
			doIStay();
		}
		
		if (state == AgentState.WaitingInRestaurant & event == AgentEvent.gotMenu) {
			checkMenu();
		}
		
		if (state == AgentState.WaitingInRestaurant & event == AgentEvent.followWaiter ) {
			SitDown();
		}

		if(state == AgentState.BeingSeated & event == AgentEvent.seated) {
			state = AgentState.Ordering;
			DecideOnOrder();
		}

		if(state == AgentState.Ordering & event == AgentEvent.askedOrder){
			OrderFood();
		}
		
		if(state == AgentState.Reorder) {
			ReOrder();
		}
		
		if(state == AgentState.Seated & event == AgentEvent.gotFood) {
			state = AgentState.Eating;
			eatFood();
		}

		if (state == AgentState.Eating & event == AgentEvent.doneEating) {
			state = AgentState.WaitingForCheck;
			AskForCheck();
		}
		
		if (state == AgentState.WaitingForCheck & event == AgentEvent.ReceivedCheck) {
			state = AgentState.Leaving;
			PayForFood();
		}
		
		if (state == AgentState.Leaving & event == AgentEvent.doneLeaving) {
			person.msgFoodDone(true);
			state = AgentState.DoingNothing;
		}
		return false;
	}

	// Actions

	private void goToRestaurant() {
		Do("Going to restaurant");
		state = AgentState.WaitingInRestaurant;
		customerGui.GoToHost();
		host.msgImHungry(this);//send our instance, so he can respond to us
	}

	private void SitDown() {
		Do("Being seated. Going to table");
		state = AgentState.BeingSeated;
		customerGui.DoGoToSeat(tablePos);
	}

	private void ReOrder() {
		if(checkMenu()) {
			Do("I've decided on a new order");
			choice = m.returnOrder(RNG.nextInt(m.getNumItems()));
			customerGui.waitingFor(choice);
			w.msgMyOrderIs(choice, this);
			state = AgentState.Seated;
		}
	}
	
	private void DecideOnOrder() {
		choice = m.returnOrder(RNG.nextInt(m.getNumItems()));
		Do("Waiter, I would like to order.");
		if(name.equalsIgnoreCase("Angus")) {
			choice = "steak";
			Do("MY NAME IS ANGUS AND I WANT STEAK!");
		}
		
		if(name.equalsIgnoreCase("Chico")) {
			choice = "chicken";
		}
		
		if(name.equalsIgnoreCase("Vegan Steve")) {
			choice = "salad";
		}
		w.msgReadyToOrder(this);
	}

	private void OrderFood() {
		w.msgMyOrderIs(choice, this);
		Do("I would like to have " + choice + ".");
		customerGui.waitingFor(choice);
		state = AgentState.Seated;
	}

	private void eatFood() {
		Do("Eating Food");
		customerGui.receivedFood();
		//This next complicated line creates and starts a timer thread.
		//We schedule a deadline of getHungerLevel()*1000 milliseconds.
		//When that time elapses, it will call back to the run routine
		//located in the anonymous class created right there inline:
		//TimerTask is an interface that we implement right there inline.
		//Since Java does not all us to pass functions, only objects.
		//So, we use Java syntactic mechanism to create an
		//anonymous inner class that has the public method run() in it.
		timer.schedule(new TimerTask() {
			Object cookie = 1;
			public void run() {
				print("Done eating, cookie=" + cookie);
				event = AgentEvent.doneEating;
				//isHungry = false;
				stateChanged();
			}
		},
		5000);//getHungerLevel() * 1000);//how long to wait before running task
	}

	private void leaveTable() {
		Do("Leaving.");
		w.msgDoneEating(this);
		customerGui.DoExitRestaurant();
	}

	private void AskForCheck() {
		print("I would like my check please.");
		w.msgCheckPlease(this);
	}
	
	private void PayForFood() {
		leaveTable();
		if(money >= moneyOwed) {
			money -= moneyOwed;
			print("I'm paying now. I have $" + String.valueOf(money) + " left.");
			cashier.msgHereIsPayment(this, moneyOwed);
		}
		else {
			print("I can't afford it. Here's what I have.");
			cashier.msgHereIsPayment(this, money);
			money = 0;
		}
	}
	
	private void doIStay() {
		if(RNG.nextBoolean()) {
			print("Sure, I can wait.");
			event = AgentEvent.gotMenu;
		}
		else {
			print("No thanks, I'll leave now.");
			host.msgImLeaving(this);
			state = AgentState.DoingNothing;
			customerGui.setNotHungry();
		}
	}
	
	private boolean checkMenu() {
		boolean canAfford = false;
		for(int n = 0; n < m.getNumItems(); n++) {
			if(m.checkPrice(m.returnOrder(n)) <= money) {
				canAfford = true;
			}
		}
		
		if(canAfford & state == AgentState.WaitingInRestaurant) {
			host.msgIWantASeat(this);
			return true;
		}
		
		else if(canAfford & state == AgentState.Reorder) {
			return true;
		}
		
		else {
			print("I can't afford anything here. I'm out.");
			if(state == AgentState.Reorder) {
				w.msgDoneEating(this);
				state = AgentState.Leaving;
				leaveTable();
				return false;
			}
			else {
				host.msgImLeaving(this);
				state = AgentState.DoingNothing;
				customerGui.setNotHungry();
			}
		}
		return true;
	}
	
	// Accessors, etc.

	public String getName() {
		return name;
	}

	public int getHungerLevel() {
		return hungerLevel;
	}

	public void setHungerLevel(int hungerLevel) {
		this.hungerLevel = hungerLevel;
		//could be a state change. Maybe you don't
		//need to eat until hunger lever is > 5?
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

