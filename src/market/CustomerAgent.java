package market;

import agent.Agent;

import java.util.*;

import market.gui.CustomerGui;

/**
 * Restaurant customer agent.
 */
public class CustomerAgent extends Agent {
	private String name;
	private int hungerLevel = 10;        // determines length of meal
	Timer timer = new Timer();
	private CustomerGui customerGui;
	private String choice;
	private Wallet wallet;
	private Timer t = new Timer();

	private ManagerAgent manager;
	private WorkerAgent worker;
	
	public int table;

	public enum AgentState
	{DoingNothing, Waiting, BeingSeated, Seated, Ordered, WaitingForOrder, WaitingForCheck, Paying, Leaving};
	private AgentState state = AgentState.DoingNothing;

	public enum AgentEvent 
	{none, gotHungry, followHost, seated, orderFood, orderTaken, itemArrived, rcvdCheck, arrivedAtCashier, donePaying, doneLeaving};
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
	public void setManager(ManagerAgent manager) {
		this.manager = manager;
	}
	
	public void gotHungry() {
		event = AgentEvent.gotHungry;
		stateChanged();
	}

	public void msgFollowMeToTable(ManagerAgent m) {
		manager = m;
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
	
	public void msgHereIsYourItem() {
		event = AgentEvent.itemArrived;
		stateChanged();
	}

	public void msgAnimationFinishedLeaveMarket() {
		//from animation
		event = AgentEvent.doneLeaving;
		stateChanged();
	}
	
	public void msgAnimationFinishedGoToCashier() {
		//from animation
		event = AgentEvent.arrivedAtCashier;
		stateChanged();
	}
	
	public void msgHereIsChange(double amt) {
		event = AgentEvent.donePaying;
		wallet.update(amt);
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		//	CustomerAgent is a finite state machine
		try {
			if (state == AgentState.DoingNothing && event == AgentEvent.gotHungry){
				if (manager.isFull() && !name.equalsIgnoreCase("wait")) {
					//customerGui.setEnabled();
					//customerGui.DoExitRestaurant();
					return true;
				}
				state = AgentState.Waiting;
				GoToRestaurant();
				return true;
			}
			if (state == AgentState.Waiting && event == AgentEvent.followHost){
				state = AgentState.BeingSeated;
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
			if (state == AgentState.WaitingForCheck && event == AgentEvent.rcvdCheck){
				state = AgentState.Paying;
				return true;
			}
			if (state == AgentState.Paying && event == AgentEvent.arrivedAtCashier){
				manager.cashier.msgHereIsMoney(this, wallet.getAmt());
				return true;
			}
			if (state == AgentState.Paying && event == AgentEvent.donePaying){
				state = AgentState.Leaving;
				LeaveRestaurant();
				return true;
			}
			if (state == AgentState.Leaving && event == AgentEvent.doneLeaving){
				state = AgentState.DoingNothing;
				worker.msgDoneLeaving(this);
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
		worker.msgHereIsMyChoice(this, choice);
		//customerGui.setText(choice+"?");
	}

	private void GoToRestaurant() {
		Do("Going to restaurant");
		manager.msgIAmHere(this);
	}

	private void LeaveRestaurant() {
		Do("Leaving.");
//		customerGui.setText("");
//		customerGui.DoExitRestaurant();
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

