package simcity;

import agent.Agent;

import java.util.*;
import java.util.concurrent.Semaphore;

import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;

import simcity.interfaces.Bank;
import simcity.interfaces.Housing;
import simcity.interfaces.Restaurant;
import simcity.interfaces.Transportation;
import simcity.test.mock.EventLog;
import simcity.test.mock.LoggedEvent;

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
	private enum PreferredCommute {Walk, Bus, Car};
	private PreferredCommute preferredCommute;
	
	private final static double MONEY_ON_HAND_LIMIT = 50;
	
	// Transportation
	private Transportation transportation;
	
	// Location
	private enum LocationState {Home, Transit, Restaurant, Bank, Market};
	private LocationState currentLocationState;
	private String currentLocation;
	
	// Variables with intention to update
	private double moneyWanted = 0.0;
	private String targetLocation;
	
	// Wrapper class lists
	private List<MyObject> myObjects = new ArrayList<MyObject>();
	
	private MyObject currentMyObject = null; // denotes actual location that person is at
	private MyHousing myHome = null;
	private MyBankAccount myPersonalBankAccount = null;
	
	// Food
	String foodPreference;
	boolean preferEatAtHome;
	
	// Synchronization
	Semaphore readyForAction = new Semaphore(0, true);
	
	// temporary variables for consideration
	/*
	 * BodyState {Sleeping, Awake}
	 * Time currentTime;
	 * 
	 */
	
	// ************************* SETUP ***********************************
	
	// Constructor for CustomerAgent class
	public PersonAgent(String aName, Housing h, String relationWithHousing, Transportation t) {
		super();
		name = aName;
		myPersonality = PersonType.Normal;
		currentLocation = h.getName();
		currentLocationState = LocationState.Home;
		preferredCommute = PreferredCommute.Walk;
		currentMyObject = addHousing(h, relationWithHousing);
		transportation = t;
	}

	// get/set methods
	
	public String	getName()				{ return name; }
	public int		getNourishmentLevel()	{ return nourishmentLevel; }
	public double	getMoney()				{ return moneyOnHand; }
	public String	getCurrLocation()	{ return currentLocation; }
	public String	getCurrLocationState()	{ return currentLocationState.name(); }

	public void		setNourishmentLevel(int level)	{ nourishmentLevel = level; }
	public void		setMoney(double money)			{ moneyOnHand = money; }
	

	public void	setFoodPreference(String type, boolean atHome) {
		foodPreference = type;
		preferEatAtHome = atHome;
	}
	
	public MyHousing addHousing(Housing h, String personType) {
		MyHousing tempMyHousing = new MyHousing(h.getName(), h.getType(), personType);
		if(personType == "Renter" || personType == "OwnerResident")
			myHome = tempMyHousing; 
		myObjects.add(tempMyHousing);
		return tempMyHousing;
	}
	
	public void	addBank(Bank b, String personType) {
		MyBank tempMyBank = new MyBank(b, b.getName(), personType);
		myObjects.add(tempMyBank);
	}
	
	public void	addRestaurant(Restaurant r, String personType) {
		MyRestaurant tempMyRestaurant = new MyRestaurant(r, r.getName(), r.getType(), personType, r.getMenu());
		myObjects.add(tempMyRestaurant);
	}
	
	// TODO: addMarket
	
	public String toString() {
		return "Person " + getName();
	}
	
	// ************************* MESSAGES ***********************************

	// from Transportation
	public void msgReachedDestination(String destination) {
		log.add(new LoggedEvent("Received msgReachedDestination: destination = " + destination));
		currentLocation = destination;
		mapLocationToEnum(currentLocation);
		updateCurrentMyObject(currentLocation);
		stateChanged();
	}

	// from Bank
	public void msgWithdrawalSuccessful(double amount) {
		log.add(new LoggedEvent("Received msgWithdrawalSuccessful: amount = " + amount));
		moneyOnHand += amount;
		moneyWanted -= amount;
		readyForAction.release();
		stateChanged();
	}
	
	// from Restaurant
	public void msgDoneEating() {
		// TODO: Hack; what should this value be?
		nourishmentLevel = 1;
		stateChanged();
	}
	
	public void msgReadyForNextAction() {
		readyForAction.release();
		stateChanged();
	}
	
	// ************************* SCHEDULER ***********************************
	
	public boolean pickAndExecuteAnAction() {
		
		if(currentLocationState == LocationState.Home) {
			if(nourishmentLevel <= 0) {
				if(preferEatAtHome) {
					// TODO if person prefers eating at home
				}
				else {
					hungryToRestaurant();
				}
			}
			return true;
		}
		
		if(currentLocationState == LocationState.Bank) {
			// TODO Person scheduler while in Bank
			// Stuff to do at bank
			if(moneyWanted > 0) {
				requestWithdrawal();
				return true;
			}
			// Done at bank, time to transition
			if(nourishmentLevel <= 0 && !preferEatAtHome) {
				hungryToRestaurant();
				return true;
			}
		}

		if(currentLocationState == LocationState.Restaurant) {
			if(nourishmentLevel <= 0) {
				enterRestaurant();
				return true;
			}
			else {
				goHome();
				return true;
			}
		}
		if(currentLocationState == LocationState.Market) {
			// TODO Person scheduler while in Market
		}
		
		return false;
	}

	// ************************* ACTIONS ***********************************

	private void hungryToRestaurant() {
		MyRestaurant targetRestaurant = chooseRestaurant();
		Map<String, Double> theMenu = targetRestaurant.menu;
		double lowestPrice = getLowestPrice(theMenu);
		if(moneyOnHand < lowestPrice) {
			log.add(new LoggedEvent("Want to eat at restaurant; not enough money"));
			moneyWanted = lowestPrice - moneyOnHand;
			// TODO: bank name hacked; MyBank and finding banks must be implemented
			targetLocation = "Mock Bank 1";
			goToTransportation();
			return;
		}
		targetLocation = targetRestaurant.name;
		goToTransportation();
	}
	
	private void goHome() {
		log.add(new LoggedEvent("Going home"));
		targetLocation = myHome.name;
		goToTransportation();
	}
	
	private void goToTransportation() {
		log.add(new LoggedEvent("Going from " + currentLocation + " to " + targetLocation));
		transportation.msgGoTo(currentLocation, targetLocation, this, preferredCommute.name());
	}
	
	private void requestWithdrawal() {
		MyBank myBank = (MyBank)currentMyObject;
		// TODO: Hacking in account number
		log.add(new LoggedEvent("Want to withdraw " + moneyWanted + " from " + myBank.name));
		myBank.theBank.msgRequestWithdrawal(moneyWanted, 1, this);
		try {
			readyForAction.acquire();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		/*
		 * bank.msgRequestAccount(Person p)
		 * msgDeposit(amount, accountNumber, p);
		 * msgWithdraw(amount, accountNumber, p);
		 */
	}

	private void enterRestaurant() {
		MyRestaurant myRest = (MyRestaurant)currentMyObject;
		myRest.restaurant.msgPersonAs(this, myRest.personType, name, moneyOnHand, foodPreference);
	}
	
	// ************************* UTILITIES ***********************************
	
	private void mapLocationToEnum(String location) {
		MyObject[] myObjectsArray = getObjects();
		for(int i = 0; i < myObjectsArray.length; i++) {
			MyObject tempObject = myObjectsArray[i];
			if(location.equals(tempObject.name)) {
				if(tempObject instanceof MyHousing)
					currentLocationState = LocationState.Home;
				else if(tempObject instanceof MyBank)
					currentLocationState = LocationState.Bank;
				else if(tempObject instanceof MyRestaurant)
					currentLocationState = LocationState.Restaurant;
				// TODO: Get market going
				if(tempObject instanceof MyMarket)
					currentLocationState = LocationState.Market;
				return;
			}
		}
	}
	
	private void updateCurrentMyObject(String location) {
		MyObject[] myObjectsArray = getObjects();
		for(int i = 0; i < myObjectsArray.length; i++) {
			MyObject tempObject = myObjectsArray[i];
			if(location.equals(tempObject.name)) {
				currentMyObject = tempObject;
				return;
			}
		}
	}

	private double getLowestPrice(Map<String, Double> theMenu) {
		Collection<Double> prices = theMenu.values();
		Double[] temp = new Double[1];
		Double[] pricesArray = prices.toArray(temp);
		Arrays.sort(pricesArray);
		return pricesArray[0].doubleValue();
	}
	
	private MyRestaurant chooseRestaurant() {
		// TODO: hack in choosing restaurants - choose first available
		MyObject[] myObjectsArray = getObjects();
		for(int i = 0; i < myObjectsArray.length; i++)
			if(myObjectsArray[i] instanceof MyRestaurant)
				return (MyRestaurant)myObjectsArray[i];
		return null;
	}
	
	private MyObject[] getObjects() {
		return (MyObject[])myObjects.toArray(new MyObject[myObjects.size()]);
	}
	
	// ************************* WRAPPER CLASSES ***********************************
	
	private class MyObject {
		String name;
	}
	
	private class MyHousing extends MyObject {
		
		String housingType, occupantType;
		Map<String, Integer> inventory = new HashMap<String, Integer>();
		
		public MyHousing(String housingName, String housingType, String occupantType) {
			this.name = housingName; 
			this.housingType = housingType;  
			this.occupantType = occupantType;
		}
	}
	
	private class MyBankAccount extends MyObject {
		MyBank theBank;
		String accountType, bankName;
		int accountNumber;
		double amount = 0, loanNeeded = 0;
		
		public MyBankAccount(String accountName, String accountType, String bankName, int accountNumber) {
			this.name = accountName; 
			this.accountType = accountType;
			this.bankName = bankName;
			this.accountNumber = accountNumber;
		}
	}
	
	private class MyRestaurant extends MyObject {
		Restaurant restaurant;
		String restaurantType, personType;
		Map<String, Double> menu = new HashMap<String, Double>();
		
		public MyRestaurant(Restaurant r, String restaurantName, String restaurantType, String personType, Map<String, Double> menu) {
			restaurant = r;
			this.name = restaurantName;
			this.restaurantType = restaurantType;
			this.personType = personType;
			this.menu = menu;
		}
	}
	
	private class MyBank extends MyObject {
		
		Bank theBank;
		String personType;
		public MyBank(Bank b, String name, String type) {
			theBank = b;
			this.name = name;
			personType = type;
		}
	}
	
	private class MyMarket extends MyObject {
		
		//Market theMarket
		String personType;
		public MyMarket(String name, String type) {
			this.name = name;
			personType = type;
		}
	}
}
