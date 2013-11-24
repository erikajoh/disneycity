package simcity;

import agent.Agent;

import java.util.*;
import java.util.concurrent.Semaphore;

import housing.Housing;
import housing.interfaces.*;
import restaurant_rancho.gui.RestaurantRancho;
import simcity.interfaces.Bank_Douglass;
import simcity.interfaces.Market_Douglass;
import simcity.interfaces.Restaurant_Douglass;
import simcity.interfaces.Transportation_Douglass;
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
	private Map<String, Integer> itemsOnHand;
	private enum PersonType {Normal, Wealthy, Deadbeat, Crook};
	private PersonType myPersonality;
	private enum PreferredCommute {Walk, Bus, Car};
	private PreferredCommute preferredCommute;
	private enum BodyState {Asleep, Active, Tired};
	private BodyState bodyState;
	
	private final static double MONEY_ON_HAND_LIMIT = 50.0;
	private final static int BASE_NOURISHMENT_LEVEL = 10;
	
	// Transportation
	private Transportation_Douglass transportation;
	
	// Location
	private enum LocationState {Home, Transit, Restaurant, Bank, Market};
	private boolean enteredHouse = false;
	private LocationState currentLocationState;
	private String currentLocation;
	
	// Variables with intention to update
	private double moneyWanted = 0.0;
	private double moneyToDeposit = 0.0;
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
	private PriorityQueue<Action> actionQueue = new PriorityQueue<Action>();
	public enum PersonEvent {makingDecision, onHold};
	public PersonEvent event = PersonEvent.makingDecision;
	// TODO Generic eventFired event instead of specific ones?
	
	// temporary variables for consideration
	/*
	 * BodyState {Sleeping, Awake}
	 * Time currentTime;
	 * 
	 */
	
	// ************************* SETUP ***********************************
	
	// Constructor for CustomerAgent class
	public PersonAgent(String aName, Housing h, String relationWithHousing, Transportation_Douglass t) {
		super();
		name = aName;
		myPersonality = PersonType.Normal;
		currentLocation = h.getName();
		targetLocation = currentLocation;
		
		currentLocationState = LocationState.Home;
		preferredCommute = PreferredCommute.Walk;
		currentMyObject = addHousing(h, relationWithHousing);
		transportation = t;
		bodyState = BodyState.Active;
		itemsOnHand = new HashMap<String, Integer>();
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
		MyHousing tempMyHousing = new MyHousing(h, h.getName(), personType);
		if(personType == "Renter" || personType == "OwnerResident")
			myHome = tempMyHousing; 
		myObjects.add(tempMyHousing);
		return tempMyHousing;
	}
	
	public void	addBank(Bank_Douglass b, String personType) {
		MyBank tempMyBank = new MyBank(b, b.getName(), personType);
		myObjects.add(tempMyBank);
	}
	
	public void	addRestaurant(RestaurantRancho r, String personType) {
		// TODO Hacked in restaurant type
		MyRestaurant tempMyRestaurant = new MyRestaurant(r, r.getName(), "Restaurant", personType, r.getMenu().menuItems);
		myObjects.add(tempMyRestaurant);
	}
	
	public void	addMarket(Market_Douglass m, String personType) {
		MyMarket tempMyMarket = new MyMarket(m, m.getName(), personType);
		myObjects.add(tempMyMarket);
	}
	
	public String toString() {
		return "Person " + getName();
	}
	
	// ************************* MESSAGES ***********************************

	// from main class
	// TODO: handle sleeping/waking in scheduler
	public void msgWakeUp() {
		bodyState = BodyState.Active;
		stateChanged();
	}
	
	public void msgGoToSleep() {
		bodyState = BodyState.Tired;
		stateChanged();
	}
	
	public void msgNourishmentDecrease(int amount) {
		nourishmentLevel -= amount;
		stateChanged();
	}
	
	public void msgSetBodyState() {
		stateChanged();
	}
	
	// from Housing
	public void msgDoneEntering() {
		event = PersonEvent.makingDecision;
		stateChanged();
	}
	
	public void msgRentIsDue(double amount) {
		actionQueue.add(new Action("payRent", 1));
	}
	
	public void msgHereIsRent(double amount) {
		actionQueue.add(new Action("receiveRent", 1));
		stateChanged();
	}
	
	public void msgFinishedMaintenance() {
		// TODO housing message
		event = PersonEvent.makingDecision;
		stateChanged();
	}
	
	public void msgFoodDone() {
		// TODO housing message
		event = PersonEvent.makingDecision;
		nourishmentLevel = BASE_NOURISHMENT_LEVEL;
		stateChanged();
	}
	
	public void msgDoneLeaving() {
		event = PersonEvent.makingDecision;
		stateChanged();
	}
	
	// from Transportation
	public void msgReachedDestination(String destination) {
		log.add(new LoggedEvent("Received msgReachedDestination: destination = " + destination));
		currentLocation = destination;
		mapLocationToEnum(currentLocation);
		updateCurrentMyObject(currentLocation);
		stateChanged();
	}

	// from Bank
	public void msgAccountOpened(int accountNumber) {
		stateChanged();
	}
	
	public void msgMoneyWithdrawn(double amount) {
		log.add(new LoggedEvent("Received msgMoneyWithdrawn: amount = " + amount));
		moneyOnHand += amount;
		moneyWanted -= amount;
		stateChanged();
	}
	
	public void msgMoneyDeposited() {
		log.add(new LoggedEvent("Received msgMoneyDeposited"));
		moneyOnHand -= moneyToDeposit;
		moneyToDeposit = 0;
		stateChanged();
	}
	
	public void msgLoanDecision(boolean status) {
		// TODO bank message
		stateChanged();
	}
	
	// from Restaurant
	public void msgDoneEating(boolean success) {
		if(success)
			nourishmentLevel = BASE_NOURISHMENT_LEVEL;
		stateChanged();
	}
	
	// from Market
	public void msgHereIsOrder(String order, int quantity) {
		
		stateChanged();
	}
	
	// ************************* SCHEDULER ***********************************
	
	public boolean pickAndExecuteAnAction() {
		print("Calling PersonAgent's scheduler");
		// based on state/emergencies
		
		if(actionQueue.size() > 0) {
			Action theAction = actionQueue.poll();
			// TODO: what to do with action...
		}
		
		// if no emergenices, proceed with normal decision rules
		if(event == PersonEvent.makingDecision) {
		
			// based on location
			if(currentLocationState == LocationState.Home) {
				if(!enteredHouse && currentLocation.equals(targetLocation)) {
					print("Entering house");
					enterHouse();
					enteredHouse = true;
					event = PersonEvent.onHold;
					return true;
				}
				if(nourishmentLevel <= 0) {
					print("Deciding to eat");
					if(preferEatAtHome) {
						prepareToCookAtHome();
					}
					else {
						hungryToRestaurant();
					}
					event = PersonEvent.onHold;
					return true;
				}
			}
			if(currentLocationState == LocationState.Bank) {
				// TODO Person scheduler while in Bank
				// Stuff to do at bank
				if(moneyWanted > 0) {
					requestWithdrawal();
					return true;
				}
				if(moneyToDeposit > 0) {
					requestDeposit();
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
		}		
		return false;
	}

	// ************************* ACTIONS ***********************************

	// House actions
	private void enterHouse() {
		myHome.housing.msgIAmHome(this);
	}
	
	private void prepareToCookAtHome() {
		// TODO home action
		myHome.housing.msgPrepareToCookAtHome(this, foodPreference);
	}
	
	private void payRent(double amount) {
		myHome.housing.msgHereIsRent(this, amount);
	}
	
	private void leaveHouse() {
		myHome.housing.msgIAmLeaving(this);
	}
	
	private void goToSleep() {
		myHome.housing.msgGoToBed(this);
	}
	
	// Restaurant actions
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
	
	private void enterRestaurant() {
		MyRestaurant myRest = (MyRestaurant)currentMyObject;
		myRest.restaurant.personAs(this, myRest.personType, name, moneyOnHand, foodPreference);
		// TODO: blocking
	}
	
	// Transportation actions
	private void goToTransportation() {
		log.add(new LoggedEvent("Going from " + currentLocation + " to " + targetLocation));
		transportation.msgWantToGo(currentLocation, targetLocation, this, preferredCommute.name());
		event = PersonEvent.onHold;
		// TODO blocking?
	}
	
	private void goHome() {
		log.add(new LoggedEvent("Going home"));
		targetLocation = myHome.name;
		goToTransportation();
	}
	
	//Bank actions
	private void createAccount() {
		MyBank myBank = (MyBank)currentMyObject;
		log.add(new LoggedEvent("Creating account"));
		// TODO: Hacking in account number
		myBank.theBank.msgRequestAccount(moneyToDeposit, this);
	}
	
	private void requestWithdrawal() {
		MyBank myBank = (MyBank)currentMyObject;
		log.add(new LoggedEvent("Want to withdraw " + moneyWanted + " from " + myBank.name));
		// TODO: Hacking in account number
		myBank.theBank.msgRequestWithdrawal(1, moneyWanted, this);
	}
	
	private void requestDeposit() {
		MyBank myBank = (MyBank)currentMyObject;
		log.add(new LoggedEvent("Want to deposit " + moneyToDeposit + " from " + myBank.name));
		// TODO: Hacking in account number and forLoan
		myBank.theBank.msgRequestDeposit(1, moneyWanted, this, false);
	}
	
	//Market actions
	
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
	
	// TODO interact with housing more
	private class MyHousing extends MyObject {
		Housing housing;
		String occupantType;
		Map<String, Integer> inventory = new HashMap<String, Integer>();
		
		public MyHousing(Housing h, String housingName, String occupantType) {
			housing = h;
			this.name = housingName;
			this.occupantType = occupantType;
		}
	}
	
	// TODO: Use and update stuff in this class
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
	// Customers, Waiters, Host, Cook, Cashier
	private class MyRestaurant extends MyObject {
		RestaurantRancho restaurant;
		String restaurantType, personType;
		Map<String, Double> menu = new HashMap<String, Double>();
		
		// TODO More things about different restaurant types
		public MyRestaurant(RestaurantRancho r, String restaurantName, String restaurantType, String personType, Hashtable<String, Double> menu) {
			restaurant = r;
			this.name = restaurantName;
			this.restaurantType = restaurantType;
			this.personType = personType;
			this.menu = menu;
		}
	}
	
	private class MyBank extends MyObject {
		
		Bank_Douglass theBank;
		String personType;
		public MyBank(Bank_Douglass b, String name, String type) {
			theBank = b;
			this.name = name;
			personType = type;
		}
	}
	
	private class MyMarket extends MyObject {
		
		Market_Douglass theMarket;
		String personType;
		public MyMarket(Market_Douglass m, String name, String type) {
			theMarket = m;
			this.name = name;
			personType = type;
		}
	}
	
	// TODO: determine actions and whatever priorities there are
	// TODO: determine if I even need this thing
	// 0 = emergency (highest priority)
	// 1 = urgent
	// 2 = necessary
	// 3 = not really needed at the moment
	private class Action implements Comparable {
		String action;
		int priority;
		public Action(String a, int p) {
			action = a;
			priority = p;
		}
		@Override
		public int compareTo(Object arg) {
			Action other = (Action)arg;
			return priority - other.priority;
		}
	}
	
	private void addAction(String anAction, int aPriority) {
		Action theAction = new Action(anAction, aPriority);
		actionQueue.add(theAction);
	}
}
