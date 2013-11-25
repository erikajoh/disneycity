package simcity;

import agent.Agent;

import java.util.*;
import java.util.concurrent.Semaphore;

import market.Market;
import housing.Housing;
import housing.interfaces.*;
import restaurant_rancho.interfaces.Person;
import simcity.interfaces.Bank_Douglass;
import simcity.interfaces.Restaurant_Douglass;
import simcity.interfaces.Transportation_Douglass;
import simcity.test.mock.EventLog;
import simcity.test.mock.LoggedEvent;

// Priority of coding classes: Person, Housing, Transportation, Bank, Restaurant, Market 

public class PersonAgent extends Agent {
	
	// ************************* DATA ***********************************
	
	int printCount = 100;
	
	// Unit testing
	public EventLog log = new EventLog();
	
	// Inherent data - simple variables
	private String name;
	private boolean isNourished;
	private boolean needToWork;
	private double moneyOnHand = 20;
	private Map<String, Integer> itemsOnHand;
	private enum PersonType {Normal, Wealthy, Deadbeat, Crook};
	private PersonType myPersonality;
	private enum PreferredCommute {Walk, Bus, Car};
	private PreferredCommute preferredCommute;
	private enum BodyState {Asleep, Active, Tired};
	private BodyState bodyState;
	
	private enum ActionString { wakeUp, goToSleep, goToWork, payRent, receiveRent, needMaintenance };
	// TODO: Use goToWork
	
	private final static double MONEY_ON_HAND_LIMIT = 50.0;
	private final static int MARKET_PURCHASE_QUANTITY = 5;
	
	// Transportation
	private Transportation_Douglass transportation;
	
	// Location
	private enum LocationState {Home, Transit, Restaurant, Bank, Market};
	private boolean insideHouse = false;
	private LocationState currentLocationState;
	private String currentLocation;
	
	// Variables with intention to update
	private double moneyWanted = 0.0;
	private double moneyToDeposit = 0.0;
	private String targetLocation;
	private enum RestaurantState { None, JustArrived,  };
	private enum MarketState { None, WantToBuy, WantToWork };
	private MarketState marketState;
	
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
	
	// ************************* SETUP ***********************************
	
	// Constructor for CustomerAgent class
	public PersonAgent(String aName, Housing h, String foodPreference, String relationWithHousing, Transportation_Douglass t) {
		super();
		name = aName;
		myPersonality = PersonType.Normal;
		isNourished = true;
		currentLocation = h.getName();
		targetLocation = currentLocation;
		
		currentLocationState = LocationState.Home;
		preferredCommute = PreferredCommute.Walk;
		
		this.foodPreference = foodPreference;
		preferEatAtHome = true;
		
		currentMyObject = addHousing(h, relationWithHousing);
		transportation = t;
		bodyState = BodyState.Active;
		itemsOnHand = new HashMap<String, Integer>();
	}

	// get/set methods
	
	public String	getName()				{ return name; }
	public boolean	getIsNourished()		{ return isNourished; }
	public double	getMoney()				{ return moneyOnHand; }
	public String	getCurrLocation()		{ return currentLocation; }
	public String	getCurrLocationState()	{ return currentLocationState.name(); }
	public String	getBodyState()			{ return bodyState.toString(); }

	public void		setIsNourished(boolean full)	{ isNourished = full; }
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
	
	public void	addRestaurant(Restaurant r, String personType) {
		// TODO Hacked in restaurant type
		MyRestaurant tempMyRestaurant = new MyRestaurant(r, r.getRestaurantName(), "Restaurant", personType, r.getMenu().menuItems);
		myObjects.add(tempMyRestaurant);
	}
	
	public void	addMarket(Market m, String personType) {
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
		print("Must wake up");
		actionQueue.add(new Action(ActionString.wakeUp, 0, 0));
		event = PersonEvent.makingDecision;
		stateChanged();
	}
	
	public void msgGoToSleep() {
		print("Must go to sleep");
		actionQueue.add(new Action(ActionString.goToSleep, 2, 0));
		stateChanged();
	}
	
	public void msgSetHunger(boolean full) {
		isNourished = full;
		stateChanged();
	}
	
	// from Housing
	public void msgDoneEntering() {
		event = PersonEvent.makingDecision;
		stateChanged();
	}
	
	public void msgRentIsDue(double amount) {
		actionQueue.add(new Action(ActionString.payRent, 1, amount));
		stateChanged();
	}
	
	public void msgHereIsRent(double amount) {
		actionQueue.add(new Action(ActionString.receiveRent, 1, amount));
		stateChanged();
	}
	
	public void msgNeedMaintenance() {
		actionQueue.add(new Action(ActionString.needMaintenance, 1, 0));
		stateChanged();
	}
	
	public void msgFinishedMaintenance() {
		event = PersonEvent.makingDecision;
		stateChanged();
	}
	
	public void msgFoodDone(boolean doneEating) {
		// TODO housing message
		if(doneEating) {
			isNourished = true;
			preferEatAtHome = !preferEatAtHome;
		}
		else {
			marketState = MarketState.WantToBuy;
		}
		event = PersonEvent.makingDecision;
		stateChanged();
	}
	
	public void msgDoneLeaving() {
		insideHouse = false;
		event = PersonEvent.makingDecision;
		stateChanged();
	}
	
	// from Transportation
	public void msgReachedDestination(String destination) {
		print("Received msgReachedDestination: destination = " + destination);
		log.add(new LoggedEvent("Received msgReachedDestination: destination = " + destination));
		currentLocation = destination;
		mapLocationToEnum(currentLocation);
		updateCurrentMyObject(currentLocation);
		event = PersonEvent.makingDecision;
		stateChanged();
	}

	// from Bank
	// TODO: More parameters needed
	public void msgAccountOpened(int accountNumber) {
		log.add(new LoggedEvent("Account created: accountNumber = " + accountNumber));
		
		// TODO msgAccountOpened
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
			isNourished = true;
		preferEatAtHome = !preferEatAtHome;
		event = PersonEvent.makingDecision;
		stateChanged();
	}
	
	// from Market
	public void msgHereIsOrder(String order, int quantity) {
		// TODO Market state update - assume order is fulfilled
		print("Received msgHereIsOrder from Market");
		marketState = MarketState.None;
		event = PersonEvent.makingDecision;
		if(itemsOnHand.get(order) == null)
			itemsOnHand.put(order, quantity);
		else
			itemsOnHand.put(order, itemsOnHand.get(order) + quantity);
		stateChanged();
	}
	
	// ************************* SCHEDULER ***********************************
	
	public boolean pickAndExecuteAnAction() {
		if(printCount > 0) {
			printCount--;
			print("Calling PersonAgent's scheduler");
		}
		
		// action queue for urgent actions
		if(actionQueue.size() > 0) {
			Action theAction = actionQueue.poll();
			// TODO: what to do with action...
			switch(theAction.action) {
				case wakeUp:
					bodyState = BodyState.Active; break;
				case goToSleep:
					bodyState = BodyState.Tired; break;
				case payRent:
					payRent(theAction.amount); break;
				case receiveRent:
					moneyOnHand += theAction.amount; break;
				case needMaintenance: 
					doMaintenance(); break;
				case goToWork:
					break; // TODO: handle go to work
			}
			event = PersonEvent.makingDecision;
			return true;
		}
		
		// if no emergenices, proceed with normal decision rules
		if(event == PersonEvent.makingDecision && bodyState != BodyState.Asleep) {
			
			if(currentLocationState == LocationState.Home) {
				if(!insideHouse) { // if not inside house (i.e., at the doorstep), enter it
					print("Not inside my house");
					if(currentLocation.equals(targetLocation)) {
						enterHouse();
						insideHouse = true;
					}
					else {
						goToTransportation();
					}
					event = PersonEvent.onHold;
					return true;
				}
				if(!isNourished) { // if I'm hungry
					if(currentLocation.equals(targetLocation)) { // decide to stay at home
						print("Deciding to eat");
						if(preferEatAtHome) {
							if(marketState == MarketState.WantToBuy) { // not enough in fridge
								hungryToMarket();
							}
							else {
								prepareToCookAtHome();
								event = PersonEvent.onHold;
							}
						}
						else {
							hungryToRestaurant();
						}
					}
					else {
						leaveHouse();
						event = PersonEvent.onHold;
					}
					return true;
				}
				if(bodyState == BodyState.Tired) {
					goToSleep();
					bodyState = BodyState.Asleep;
					event = PersonEvent.onHold;
				}
			}
			if(currentLocationState == LocationState.Bank) {
				// TODO Person scheduler while in Bank
				// Stuff to do at bank
				if(moneyWanted > 0) {
					requestWithdrawal();
				}
				if(moneyToDeposit > 0) {
					requestDeposit();
				}
				// Done at bank, time to transition
				if(!isNourished && !preferEatAtHome) {
					hungryToRestaurant();
				}
				return true;
			}
			if(currentLocationState == LocationState.Restaurant) {
				if(!isNourished) {
					enterRestaurant();
				}
				else {
					goHome();
				}
				event = PersonEvent.onHold;
				return true;
			}
			if(currentLocationState == LocationState.Market) {
				
				if(!isNourished && !preferEatAtHome) {
					hungryToRestaurant();
				}
				switch(marketState) {
					case None:
						goHome(); break; // TODO hopefully this rule order works
					case WantToBuy:
						enterMarket(); break;
					case WantToWork:
						break;
				}
				print("Market: event onHold set");
				event = PersonEvent.onHold;
				return true;
			}
		}
		print("Nothing to do for now: isNourished = " + isNourished
				+ "; currentLocationState = " + currentLocationState.toString()
				+ "; bodyState = " + bodyState
				+ "; personEvent = " + event);
		return false;
	}

	// ************************* ACTIONS ***********************************

	// House actions
	private void enterHouse() {
		print("Entering house");
		myHome.housing.msgIAmHome(this);
		Set<String> keySet = itemsOnHand.keySet();
		String[] keyArray = keySet.toArray(new String[keySet.size()]);
		for(int i = 0; i < keyArray.length; i++) {
			if(itemsOnHand.get(keyArray[i]) > 0) {
				// TODO myHome.housing.
			}
		}
	}
	
	private void prepareToCookAtHome() {
		// TODO home action
		print("I'm hungry and I want to cook at home");
		myHome.housing.msgPrepareToCookAtHome(this, foodPreference);
	}
	
	private void hungryToMarket() {
		// TODO hungryToMarket
		print("I'm hungry and I want to buy food at market and cook at home");
		MyMarket targetMarket = chooseMarket();
		double price = targetMarket.theMarket.getPrice(foodPreference);
		if(moneyOnHand < price) {
			log.add(new LoggedEvent("Want to buy food at market; not enough money"));
			moneyWanted = price - moneyOnHand;
			// TODO: bank name hacked; MyBank and finding banks must be implemented
			targetLocation = "Mock Bank 1";
			return;
		}
		print("I have enough money to buy food from market");
		targetLocation = targetMarket.name;
	}

	private void doMaintenance() {
		print("Performing maintenance");
		myHome.housing.msgDoMaintenance();
	}
	
	private void payRent(double amount) {
		print("Paying rent");
		myHome.housing.msgHereIsRent(this, amount);
	}
	
	private void leaveHouse() {
		print("Leaving house");
		myHome.housing.msgIAmLeaving(this);
	}
	
	private void goToSleep() {
		print("Going to sleep");
		myHome.housing.msgGoToBed(this);
	}
	
	// Restaurant actions
	private void hungryToRestaurant() {
		print("I'm hungry and I want to eat at restaurant");
		MyRestaurant targetRestaurant = chooseRestaurant();
		Map<String, Double> theMenu = targetRestaurant.menu;
		// TODO do restaurant types as well
		double lowestPrice = getLowestPrice(theMenu);
		if(moneyOnHand < lowestPrice) {
			log.add(new LoggedEvent("Want to eat at restaurant; not enough money"));
			moneyWanted = lowestPrice - moneyOnHand;
			// TODO: bank name hacked; MyBank and finding banks must be implemented
			targetLocation = "Mock Bank 1";
			return;
		}
		print("I have enough money to buy from restaurant");
		targetLocation = targetRestaurant.name;
	}
	
	private void enterRestaurant() {
		print("Entering restaurant");
		MyRestaurant myRest = (MyRestaurant)currentMyObject;
		myRest.restaurant.personAs(this, myRest.personType, name, moneyOnHand);
		// TODO: blocking
	}
	
	// Transportation actions
	private void goToTransportation() {
		print("Going from " + currentLocation + " to " + targetLocation);
		log.add(new LoggedEvent("Going from " + currentLocation + " to " + targetLocation));
		transportation.msgWantToGo(currentLocation, targetLocation, this, preferredCommute.name());
		// TODO blocking?
	}
	
	private void goHome() {
		print("Going home");
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
	
	private void enterMarket() {
		print("Entering market");
		MyMarket myMarket = (MyMarket)currentMyObject;
		myMarket.theMarket.personAs(this, "Customer", name, moneyOnHand, foodPreference, MARKET_PURCHASE_QUANTITY);
		//TODO m.theMarket.personAs(this, "VirtualCustomer", this.name, moneyOnHand, foodPreference);
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
				else if(tempObject instanceof MyMarket)
					currentLocationState = LocationState.Market;
				else
					currentLocationState = LocationState.Transit;
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
		// TODO: refine criteria for choosing restaurant
		MyObject[] myObjectsArray = getObjects();
		for(int i = 0; i < myObjectsArray.length; i++)
			if(myObjectsArray[i] instanceof MyRestaurant)
				return (MyRestaurant)myObjectsArray[i];
		return null;
	}
	
	private MyMarket chooseMarket() {
		// TODO: hack in choosing market - choose first available
		MyObject[] myObjectsArray = getObjects();
		for(int i = 0; i < myObjectsArray.length; i++)
			if(myObjectsArray[i] instanceof MyMarket)
				return (MyMarket)myObjectsArray[i];
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
		Restaurant restaurant;
		String restaurantType, personType;
		Map<String, Double> menu = new HashMap<String, Double>();
		
		// TODO More things about different restaurant types
		public MyRestaurant(Restaurant r, String restaurantName, String restaurantType, String personType, Hashtable<String, Double> menu) {
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
		
		Market theMarket;
		String personType;
		public MyMarket(Market m, String name, String type) {
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
		ActionString action;
		int priority;
		double amount;
		public Action(ActionString a, int p, double d) {
			action = a;
			priority = p;
			amount = d;
		}
		@Override
		public int compareTo(Object arg) {
			Action other = (Action)arg;
			return priority - other.priority;
		}
	}
	
	private void addAction(ActionString anAction, int aPriority, double anAmount) {
		Action theAction = new Action(anAction, aPriority, anAmount);
		actionQueue.add(theAction);
	}
}
