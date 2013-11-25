package restaurant_cafe;

import restaurant_cafe.gui.Check;
import restaurant_cafe.gui.CustomerGui;
import restaurant_cafe.gui.Menu;
import restaurant_cafe.gui.MenuItem;
import restaurant_cafe.gui.RestaurantCafeGui;
import restaurant_cafe.interfaces.Cashier;
import restaurant_cafe.interfaces.Customer;
import restaurant_cafe.interfaces.Waiter;
import agent_cafe.Agent;
import simcity.gui.SimCityGui;
import simcity.PersonAgent;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Restaurant customer agent.
 */
public class CustomerAgent extends Agent implements Customer {
	private String name;
	private int hungerLevel = 5;  
	private int tableNumber = 1; //which table the customer should go to
	private double balance;
	private int number;
	Timer timer = new Timer();
	private SimCityGui gui;
	private CustomerGui customerGui;
	Menu menu;
	String choice;
	Check check;
	PersonAgent person;

	
	// agent correspondents
	private HostAgent host;
	private Waiter waiter;
	private Cashier cashier;

	public enum AgentState
	{DoingNothing, WaitingInRestaurant, BeingSeated, MakingChoice, MadeChoice, WaitingForFood, Eating, DoneEating, GoingToCashier, GettingChange, Cleaning, DoneCleaning, Leaving};
	private AgentState state = AgentState.DoingNothing;

	public enum AgentEvent 
	{none, gotHungry, longWait, followHost, seated, askedToOrder, ordered, broughtFood, gotCheck, paying, goToClean, clean, paid, doneLeaving};
	AgentEvent event = AgentEvent.none;
	
	boolean ignorePrices = false;

	/**
	 * Constructor for CustomerAgent class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the customergui so the customer can send it messages
	 */
	public CustomerAgent(String name, int num){
		super();
		this.name = name;
		number = num;
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

	public String getCustomerName() {
		return name;
	}
	// Messages

	public void msgGotHungry() {
		print("I'm hungry");
		event = AgentEvent.gotHungry;
		stateChanged();
	}
	
	public void msgRestaurantFull() {
		print("Received msgRestaurantFull");
		event = AgentEvent.longWait;
		stateChanged();
	}

	public void msgFollowMe(Waiter w, int tn, Menu m) {
		print("Received msgSitAtTable");
		waiter = w;
		tableNumber = tn;
		event = AgentEvent.followHost;
		menu = m;
		stateChanged();
	}
	
	public void msgAskOrder(){
		print("Asked to Order "+ state);
		event = AgentEvent.askedToOrder;
		stateChanged();
	}
	
	public void msgReorder(){
		print("ASKED TO REORDER "+ menu.getItems().size());
		synchronized(menu.getItems()){
		  for(MenuItem item : menu.getItems()){
			  if(item.getFood().getName().equals(choice)){
				  menu.removeItem(item);
				  break;
			  }
		  }
		}
		state = AgentState.BeingSeated;
		event = AgentEvent.seated;
		customerGui.setDrawString("", false);
		stateChanged();
	}
	
	public void msgHereIsFood(){
		print("Thanks for the "+choice);
		event = AgentEvent.broughtFood;
		stateChanged();
	}
	
	public void msgHereIsCheck(Check c){
		print("Thanks for the check");
		check = c;
		event = AgentEvent.gotCheck;
		stateChanged();
	}
	
	public void msgHereIsChange(double change){
		change = Math.round(change*100.0)/100.0;
		print("Thanks for the change $" + change);
		balance += change;
		event = AgentEvent.paid;
		stateChanged();
	}
	
	public void msgCleanDishes(){
		event = AgentEvent.goToClean;
		stateChanged();
	}

	public void msgAnimationFinishedGoToSeat() {
		print("Seated");
		event = AgentEvent.seated;
		stateChanged();
	}
	
	public void msgAnimationFinishedGoToCashier() {
		print("At Cashier");
		event = AgentEvent.paying;
		stateChanged();
	}
	
	public void msgAnimationFinishedGoToCook() {
		print("At Cook");
		event = AgentEvent.clean;
		stateChanged();
	}
	
	public void msgAnimationFinishedLeaveRestaurant() {
		event = AgentEvent.doneLeaving;
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		//	CustomerAgent is a finite state machine
		if (state == AgentState.DoingNothing && event == AgentEvent.gotHungry){
			print("State is now waiting in restaurant");
			state = AgentState.WaitingInRestaurant;
			goToRestaurant();
			return true;
		}
		if (state == AgentState.WaitingInRestaurant && event == AgentEvent.followHost ){
			state = AgentState.BeingSeated;
			SitDown();
			return true;
		}
		if (state == AgentState.WaitingInRestaurant && event == AgentEvent.longWait){
			decideToLeave();
			return true;
		}
		if (state == AgentState.BeingSeated && event == AgentEvent.seated){
			state = AgentState.MakingChoice;
			print("Time to make a choice");
			makeChoice();
			return true;
		}
		if(state == AgentState.MadeChoice && event == AgentEvent.askedToOrder){
			state = AgentState.WaitingForFood;
			print("TELL WAITER CHOICE");
			tellWaiterChoice();
			return true;
		}
		if (state == AgentState.WaitingForFood && event == AgentEvent.broughtFood){
			state = AgentState.Eating;
			EatFood();
			return true;
		}
		if (state == AgentState.DoneEating && event == AgentEvent.gotCheck){
			state = AgentState.GoingToCashier;
			print("Go To Cashier");
			goToCashier();
			return true;
		}
		if (state == AgentState.GoingToCashier && event == AgentEvent.paying){
			state = AgentState.DoneEating;
			print("Getting Change");
			payCashier();
			return true;
		}
		if (state == AgentState.DoneEating && event == AgentEvent.goToClean){
			state = AgentState.Cleaning;
			goToCleanDishes();
			return true;
		}
		if (state == AgentState.Cleaning && event == AgentEvent.clean){
			state = AgentState.DoneEating;
			cleanDishes();
			return true;
		}
		if (state == AgentState.DoneEating && event == AgentEvent.paid){
			print("Leaving");
			state = AgentState.Leaving;
			leaveRestaurant();
			return true;
		}

		if (state == AgentState.Leaving && event == AgentEvent.doneLeaving){
			state = AgentState.DoingNothing;
			person.msgFoodDone(true);
			//no action
			return true;
		}
		return false;
	}

	// Actions

	private void goToRestaurant() {
		Do("Going to restaurant");
		host.msgIWantFood(this);
		customerGui.DoGoToRestaurant(number);
	}
	
	private void decideToLeave() {
		Do("Deciding to stay or not");
		state = AgentState.DoingNothing;
		int leave = (int) (Math.random() * 2);
		if(leave == 1){
			print("Leaving");
			host.msgCustomerLeaving(this);
			customerGui.DoExitRestaurant();
			return;
		}
		else {
			event = AgentEvent.gotHungry;
			state = AgentState.WaitingInRestaurant;
			print("Staying");
		}
	}

	private void SitDown() {
		Do("Being seated. Going to table");
		customerGui.DoGoToSeat(tableNumber);
	}
	
	private void makeChoice(){
		print("Thanks for the menu. Making a choice now " + menu.getItems().size());
		waiter.msgLeaveCustomer();
		int num = (int) (Math.random() * menu.getItems().size());
		if(menu.getItems().size()> 0){
			choice = menu.getItems().get(num).getName();
		  if(ignorePrices == false){
			while(menu.getItems().get(num).getPrice()>balance){
				print(menu.getItems().get(num).getName() + " too expensive");
				menu.getItems().remove(menu.getItems().get(num));
				if(menu.getItems().size() == 0){
					print("LEAVING SINCE NO FOOD IN PRICE RANGE");
					state = AgentState.DoingNothing;
					timer.schedule(new TimerTask() {
						public void run() {
							if(waiter == null){
								host.msgCustomerLeaving(CustomerAgent.this);
							}
							else {
								customerGui.setDrawString("", true);
								waiter.msgCustomerLeaving(CustomerAgent.this);
							}
							customerGui.DoExitRestaurant();
							stateChanged(); 
						}
					}, 3000);
					return;
				}
			num = (int) (Math.random() * menu.getItems().size());
			choice = menu.getItems().get(num).getName();
			}
		  }
		}
		synchronized(menu.getItems()){
		  for(MenuItem item : menu.getItems()){
			  if(item.getName().equals(getName())){
				  item.getFood().setAmount(0);
				  choice = item.getName();
			  }
		   }
		}
		
		timer.schedule(new TimerTask() {
			public void run() {
				print("TIMER DONE");
				state = AgentState.MadeChoice;
				waiter.msgReadyToOrder(CustomerAgent.this);
				stateChanged(); /**/
			}
		}, 3000);
	}
	
	private void tellWaiterChoice(){
		waiter.msgHereIsMyOrder(this, choice);
		customerGui.setDrawString(choice, false);
		event = AgentEvent.ordered;
	}

	private void EatFood() {
		Do("Eating Food");
		customerGui.setDrawString(choice, true);
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
				state = AgentState.DoneEating;
				stateChanged();
			}
		},
		5000);
		print("SAY WHAT");
	}
	
	public void goToCashier(){
		event = AgentEvent.none;
		customerGui.setDrawString("", true);
		customerGui.DoGoToCashier();
	}
	
	public void goToCleanDishes(){
		customerGui.DoGoToCook();
	}
	
	public void cleanDishes(){
		print("Cleaning");
		timer.schedule(new TimerTask() {
			public void run() {
				event = AgentEvent.paid;
				stateChanged();
			}
		}, 8000);
	}
	
	
	private void payCashier(){
		event = AgentEvent.none;
		double pay = check.getTotal();
		if(balance > (pay*1.2)){
			pay *= 1.2;
		}
		else if(balance < pay){
			print("I don't have enough money...");
			cashier.msgNoMoney(this);
			return;
		}
		pay = Math.round(pay*100.0)/100.0;
		cashier.msgHereIsPayment(this, pay);
	}

	private void leaveRestaurant() {
		Do("Leaving...");
		waiter.msgCustomerLeaving(this);
		customerGui.DoExitRestaurant();
	}


	// Accessors, etc.

	public String getName() {
		return name;
	}
	
	public int getHungerLevel() {
		return hungerLevel;
	}
	
	public void setBalance(double b) {
		balance = b;
	}
	
	public void ignorePrices() {
		ignorePrices = true;
	}

	public void setHungerLevel(int hungerLevel) {
		this.hungerLevel = hungerLevel;
	}

	public String toString() {
		return "customer " + getName();
	}

	public void setCashier(CashierAgent c) {
		cashier = c;
	}

	public int getNumber() {
		return number;
	}
	
	public void setGui(CustomerGui g) {
		customerGui = g;
	}

	public CustomerGui getGui() {
		return customerGui;
	}
}

