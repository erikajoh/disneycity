package simcity;

import agent.Agent;

import java.util.*;
import java.util.concurrent.Semaphore;

import simcity.interfaces.Transportation;
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
	private Transportation transportation;
	
	// Location
	private enum Location {Home, Transit, Restaurant, Bank, Market};
	private Location currentLocation, targetLocation;
	
	// Places
	private List<MyRestaurant> myRestaurants = new ArrayList<MyRestaurant>();
	
	// Food
	String foodPreference;
	boolean preferEatAtHome;
	
	// Synchronization
	Semaphore readyForNextAction = new Semaphore(0, true);
	
	// ************************* SETUP ***********************************
	
	// Constructor for CustomerAgent class
	public PersonAgent(String aName, Transportation t) {
		super();
		name = aName;
		myPersonality = PersonType.Normal;
		currentLocation = Location.Home;
		targetLocation = Location.Home;
		transportation = t;
	}

	// get/set methods
	
	public String	getName()				{ return name; }
	public int		getNourishmentLevel()	{ return nourishmentLevel; }
	public double	getMoney()				{ return moneyOnHand; }

	public void		setNourishmentLevel(int level)	{ nourishmentLevel = level; }
	public void		setMoney(int money)				{ moneyOnHand = money; }
	public void		setRestaurants(List<MyRestaurant> list)	{ myRestaurants = list; }
	public void		setFoodPreference(String type, boolean atHome)
		{ foodPreference = type; preferEatAtHome = atHome; }
	
	public String toString() {
		return "Person " + getName();
	}
	
	// ************************* MESSAGES ***********************************

	// from Transportation
	public void msgReachedDestination() {
		
	}
	
	// from Bank
	public void msgWithdrawalSuccessful(double amount) {
		
	}
	
	// from Restaurant
	public void msgDoneEating() {
		
	}
	
	// ************************* SCHEDULER ***********************************
	
	protected boolean pickAndExecuteAnAction() {
		/*
		if (state == AgentState.DoingNothing && event == AgentEvent.gotHungry) {
			state = AgentState.WaitingToBeSeated;
			GoToRestaurant();
			return true;
		}
		*/
		if(nourishmentLevel <= 0) {
			if(preferEatAtHome) {
				// TODO
			}
			else {
				MyRestaurant targetRestaurant = chooseRestaurant();
			}
		}
		return false;
	}

	// ************************* ACTIONS ***********************************

	private void goTo(/* TODO Add parameters */) {
		
	}
	
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
	
	// ************************* UTILITIES ***********************************
	
	private MyRestaurant chooseRestaurant() {
		return 
	}
	
	// ************************* WRAPPER CLASSES ***********************************
	
	private class MyHousing {
		
		String housingName, housingType, occupantType;
		Map<String, Integer> inventory = new HashMap<String, Integer>();
		
		public MyHousing(String housingName, String housingType, String occupantType) {
			this.housingName = housingName; 
			this.housingType = housingType;  
			this.occupantType = occupantType;
		}
	}
	
	private class MyBankAccount {
		// Bank theBank;
		String accountName, accountType;
		int accountNumber;
		double amount, loanNeeded;
		
		public MyBankAccount(String accountName, String accountType, int accountNumber) {
			this.accountName = accountName; 
			this.accountType = accountType;  
			this.accountNumber = accountNumber;
		}
	}
	
	private class MyRestaurant {
		// Restaurant theRestaurant;
		String restaurantName, restaurantType, personType;
		Map<String, Double> menu = new HashMap<String, Double>();
		
		public MyRestaurant(String restaurantName, String restaurantType, String personType, Map<String, Double> map) {
			// TODO initialize
		}
	}
}
