package simcity;

import agent.Agent;
import java.util.concurrent.Semaphore;

import simcity.test.mock.EventLog;

// Priority of coding classes: Person, Housing, Transportation, Bank, Restaurant, Market 

public class PersonAgent extends Agent {
	
	// ************************* DATA ***********************************
	
	// Unit testing
	public EventLog log = new EventLog();
	
	// Inherent data - simple variables
	private String name;
	private int nourishmentLevel;
	private double moneyOnHand;
	private enum PersonType {Normal, Wealthy, Deadbeat, Crook};
	private PersonType myPersonality;
	
	// Location
	private enum Location {Home, Transit, Restaurant, Bank, Market};
	private Location currentLocation, targetLocation;
	
	// Food
	FoodPreference foodPreference;
	
	// Synchronization
	Semaphore readyForNextAction = new Semaphore(0, true);
	
	// ************************* SETUP ***********************************
	
	// Constructor for CustomerAgent class
	public PersonAgent(String aName) {
		super();
		name = aName;
		myPersonality = PersonType.Normal;
		currentLocation = Location.Home;
		targetLocation = Location.Home;
	}

	// get/set methods
	
	public String	getName()				{ return name; }
	public int		getNourishmentLevel()	{ return nourishmentLevel; }
	public double	getMoney()				{ return moneyOnHand; }

	public void		setNourishmentLevel(int level)	{ this.nourishmentLevel = level; }
	public void		setMoney(int money)				{ this.moneyOnHand = money; }
	
	public String toString() {
		return "Person " + getName();
	}
	
	// ************************* MESSAGES ***********************************

	// from Transportation
	public void msgReachedDestination() {
		
	}
	
	// from Bank
	public void withdrawalSuccessful(double amount) {
		
	}
	
	// from Restaurant
	public void msgDoneEating() {
		
	}
	
	// ************************* SCEHDULER ***********************************
	
	protected boolean pickAndExecuteAnAction() {
		/*
		if (state == AgentState.DoingNothing && event == AgentEvent.gotHungry) {
			state = AgentState.WaitingToBeSeated;
			GoToRestaurant();
			return true;
		}
		*/
		return false;
	}

	// ************************* ACTIONS ***********************************

	/*
	private void GoToRestaurant() {
		Do("Going to restaurant");
		customerGui.DoDisplayOrder("" + money);
		customerGui.DoGoToWaitingArea();
		host.msgIWantFood(this);
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
	*/
}
