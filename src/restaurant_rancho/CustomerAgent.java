package restaurant_rancho;

import restaurant_rancho.RestMenu;
import restaurant_rancho.gui.CustomerGui;
import restaurant_rancho.gui.RestaurantRanchoGui;
import restaurant_rancho.interfaces.Cashier;
import restaurant_rancho.interfaces.Customer;
import restaurant_rancho.interfaces.Person;
import restaurant_rancho.interfaces.Waiter;
import agent_rancho.Agent;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;

import javax.swing.event.MenuListener;

/**
 * Restaurant customer agent.
 */
public class CustomerAgent extends Agent implements Customer{
	private String name;
	private int hungerLevel = 5;      
	Timer hungerTimer = new Timer();
	Timer menuTimer = new Timer();
	private CustomerGui customerGui;
	int waitNum;

	// agent correspondents
	private Waiter waiter;
	private HostAgent host;
	private Cashier cashier;
	public RestMenu menu;
	double cash;
	double iOwe= 0; 
	boolean returning;
	boolean atWaitingSpot;
	Person person;

	String choice = "";
	public enum AgentState
	{DoingNothing, WaitingInRestaurant, BeingSeated, Seated, PayingOldCheck, Choosing, WaitingforWaiter, CalledWaiter, WaitingforFood, Eating, ReadyForCheck, Paying, Leaving};
	public AgentState state = AgentState.DoingNothing;//The start state

	public enum AgentEvent 
	{none, gotHungry, restaurantFull, decidedToWait, followWaiter, seated, readyToOrder, ordering, ordered, receivedFood, doneEating, receivedCheck, paid, doneLeaving};
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
		waiter = null;
		returning = false;
	}

	/**
	 * hack to establish connection to Host agent.
	 */
	
	public void setPerson(Person p) {
		person = p;
	}
	
	public void setChoice(String choice) {
		this.choice = choice;
	}
	public void setHost(HostAgent host) {
		this.host = host;
	}
	public void setWaiter(Waiter waiter) {
		this.waiter = waiter;
	}

	public String getCustomerName() {
		return name;
	}
	public void setCashier(Cashier cashier) {
		this.cashier = cashier;
	}
	public void setCash(double amount) {
		cash = amount;
	}
	// Messages

	public void gotHungry() {//from animation
		print("I'm hungry");
		if (returning) customerGui.DoGoToRestaurant(customerGui.waitNum);
		event = AgentEvent.gotHungry;
		stateChanged();
	}

	public void msgSitAtTable(Waiter w, RestMenu m) {
		setWaiter(w);
		menu = m;
		event = AgentEvent.followWaiter;
		stateChanged();
	}
	
	public void msgAnimationFinishedGotToWaitingSpot() {
		atWaitingSpot = true;
		stateChanged();
	}
	

	public void msgAnimationFinishedGoToSeat() {
		//from animation
		event = AgentEvent.seated;
		stateChanged();
	}
	
	public void msgRestaurantFull() {
		print("got message restaurant is full");
		event = AgentEvent.restaurantFull;
		stateChanged();
	}
	
	public void msgWhatIsYourOrder(){
		print("Ordering now");
		event = AgentEvent.ordering;
		stateChanged();
	}

	
	public void msgHereIsCheck(double amount) {
		event = AgentEvent.receivedCheck;
		iOwe = amount;
		stateChanged();
	}
	public void msgHereIsChange(double amount) {
		customerGui.setText("$" + Double.toString(amount));
		iOwe = 0;
		cash = amount;
		if (state == AgentState.Paying) event = AgentEvent.paid;
		if (state == AgentState.PayingOldCheck) state = AgentState.BeingSeated;
		print("Got change. I have " + cash + " dollars.");
		stateChanged();
	}
	
	public void msgCheckIncomplete(double owed) {
		customerGui.setText("!");
		cash = 0;
		iOwe = owed;
		event = AgentEvent.paid;
		print("Didn't get change. I owe " + owed);
		customerGui.setText("$0");
		stateChanged();
	}
	public void msgWhatIsReorder(RestMenu menu) { 
		print("Reordering now");
		event = AgentEvent.ordering;
		state = AgentState.WaitingforWaiter;
		this.menu = menu;
		stateChanged();
	}
	
	public void msgHereIsYourFood() {
		customerGui.setText(choice.substring(0,3));
		event = AgentEvent.receivedFood;
		stateChanged();
		
	}


	public void msgAnimationFinishedLeaveRestaurant() {
		//from animation
		event = AgentEvent.doneLeaving;
		returning = true;
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		//	CustomerAgent is a finite state machine

		if (state == AgentState.DoingNothing && event == AgentEvent.gotHungry ){
			state = AgentState.WaitingInRestaurant;
			if (iOwe!=0) customerGui.setText("$" + Double.toString(cash));
			goToRestaurant();
			return true;
		}
		if (state == AgentState.WaitingInRestaurant && event == AgentEvent.restaurantFull && atWaitingSpot) {
			decideStayOrLeave();
			return true;
		}

		if (state == AgentState.WaitingInRestaurant && event == AgentEvent.followWaiter){
			state = AgentState.BeingSeated;
			return true;
		}
		
		//rules :
		if (state == AgentState.BeingSeated && event == AgentEvent.seated && iOwe!=0) {
			state = AgentState.PayingOldCheck;
			payCashier();
			return true;
		}
		if (state == AgentState.BeingSeated && event == AgentEvent.seated && iOwe==0) {
			state = AgentState.Choosing;
			LookAtMenu();
			return true;
		}
		if (state == AgentState.Choosing && event == AgentEvent.readyToOrder) {
			state = AgentState.WaitingforWaiter;
			CallWaiter();
			return true;		
		}
		
		if (state == AgentState.WaitingforWaiter && event == AgentEvent.ordering) {
			state = AgentState.WaitingforFood;
			GiveOrder();
			return true;
		}
		if (state == AgentState.WaitingforFood && event == AgentEvent.receivedFood) {
			state = AgentState.Eating;
			EatFood();
			return true;
		}

		if (state == AgentState.Eating && event == AgentEvent.doneEating){
			state = AgentState.ReadyForCheck;
			callWaiterforCheck();
			return true;
		}
		if (state == AgentState.ReadyForCheck && event == AgentEvent.receivedCheck) {
			state = AgentState.Paying;
			payCashier();
			return true;
		}
		if (state == AgentState.Paying && event == AgentEvent.paid) {
			state = AgentState.Leaving;
			leaveTable();
			return true;
		}
		if (state == AgentState.Leaving && event == AgentEvent.doneLeaving){
			state = AgentState.DoingNothing;
			customerGui.setText("");
			return true;
		}
		
		return false;
	}

	// Actions

	private void goToRestaurant() {
		Do("Going to restaurant");
		host.msgIWantFood(this, customerGui.waitNum);//send our instance, so he can respond to us
	
	}


	
	private void LookAtMenu() {
		menuTimer.schedule(new TimerTask() {
			public void run() {
				if (canPay()>0) {
					print("Ready to order now");
					event = AgentEvent.readyToOrder;
					stateChanged();
				}
				else {
					customerGui.setText("!");
					print("Food too expensive, leaving");
					event = AgentEvent.paid;
					state = AgentState.Paying;
					stateChanged();
				}
			}
		},
		2500);
		
	}

	private void decideStayOrLeave() {
	
		Random rand = new Random();
		int chance = rand.nextInt(10);
		if ((chance >=2 && !name.equals("Leave")) || name.equals("Wait")) {
			print("decided to wait");
			event = AgentEvent.decidedToWait;
			stateChanged();
			
		}
		else {
			print("decided to leave");
			host.msgLeaving(this);
			customerGui.DoExitRestaurant();
			state = AgentState.Paying;
			event = AgentEvent.paid;
			stateChanged();
		}
	}
	

	
	private void CallWaiter() {
		 waiter.msgReadyToOrder(this);	
		
	}
	private void GiveOrder() {
		Random rand = new Random();
		if (name.equals("Zero")) cash = 0;
		if (name.equals("Four")) cash = 4;
		if (name.equals("SteakTen")) cash = 10;
		if (menu.menuList.size() == 0 || canPay() ==0) {
			if (canPay() == 0) {
				print("Everything on menu is too expensive");
			}
			state = AgentState.Paying;
			event = AgentEvent.paid;
			stateChanged();
			return;
		}
		int choiceIndex = (rand.nextInt(menu.menuList.size()));
		if (canPay() == 1 && menu.menuList.contains("Latte")) choice = "Latte";
		else if (name.equals("Latte") && menu.menuList.contains("Latte")) choice = "Latte" ;
		else if (name.equals("SteakTen")) choice = "Steak";
		else choice = menu.getItemAt(choiceIndex);
		waiter.msgHereIsMyOrder(this, choice);
		customerGui.setText(choice.substring(0,3) + "?");
		print("Ordered " + choice);
		event = AgentEvent.ordered;
		stateChanged();
		
	}

	private void EatFood() {
		Do("Eating " + choice);
		hungerTimer.schedule(new TimerTask() {
			public void run() {
				print("Done eating");
				event = AgentEvent.doneEating;
				customerGui.setText("$" + cash);
				stateChanged();
			}
		},
		5000);
	}
	
	private void callWaiterforCheck() {
		print("Ready for check. I have " + cash + " dollars");
		waiter.msgReadyForCheck(this);
	}
	
	private void payCashier(){
		print("Paying cashier");
		cashier.msgHereIsMoney(this, cash);
	}

	private void leaveTable() {
		Do("Leaving.");
		cash+=20;
		if (waiter!=null) waiter.msgLeavingTable(this);
		customerGui.DoExitRestaurant();
	}
	// Utilities 
	
	private int canPay() {
		int count = 0;
		for (String food : menu.menuList) {
			if (cash>menu.menuItems.get(food)) {
				count ++;
			}
		}
		return count;
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

