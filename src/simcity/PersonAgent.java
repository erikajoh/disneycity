package simcity;

import agent.Agent;
import bank.gui.Bank;

import java.util.*;

import market.Market;
import housing.Housing;
import simcity.interfaces.Transportation_Douglass;
import simcity.test.mock.EventLog;
import simcity.test.mock.LoggedEvent;
import transportation.Transportation;

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
	
	private enum ActionString { becomeHungry, wakeUp, goToSleep, goToWork, payRent, receiveRent, needMaintenance };
	// TODO: Use goToWork
	
	private final static double MONEY_ON_HAND_LIMIT = 50.0;
	private final static int MARKET_PURCHASE_QUANTITY = 5;
	
	// Transportation
	private Transportation transportation;
	
	// Location
	private enum LocationState {Home, Transit, Restaurant, Bank, Market};
	private boolean insideHouse = false;
	private LocationState currentLocationState;
	private String currentLocation;
	
	// Variables with intention to update
	private double moneyWanted = 0.0;
	private double moneyToDeposit = 0.0;
	private double rentToPay = 0.0;
	private String targetLocation;
	private enum MarketState { None, WantToBuy, WantToWork };
	private MarketState marketState;
	private enum BankState { None, NeedTransaction };
	private BankState bankState;
	
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
	public enum PersonEvent {makingDecision, onHold, onHoldAtRestaurant, onHoldInMarket, onHoldInBank};
	public PersonEvent event = PersonEvent.makingDecision;
	// TODO Generic eventFired event instead of specific ones?
	
	// ************************* SETUP ***********************************
	
	// Constructor for CustomerAgent class
	public PersonAgent(String aName, Housing h, double startMoney, String foodPreference,
			String relationWithHousing, Transportation t) {
		super();
		name = aName;
		myPersonality = PersonType.Normal;
		isNourished = true;
		currentLocation = h.getName();
		moneyOnHand = startMoney;
		targetLocation = currentLocation;
		
		currentLocationState = LocationState.Home;
		preferredCommute = PreferredCommute.Bus;
		
		this.foodPreference = foodPreference;
		preferEatAtHome = false;
		
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
	
	public void	addBank(Bank b, String personType) {
		MyBank tempMyBank = new MyBank(b, b.getBankName(), personType);
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
		actionQueue.add(new Action(ActionString.goToSleep, 1, 0));
		stateChanged();
	}
	
	public void msgSetHungry() {
		print("I'm hungry now");
		if(isNourished) {
			actionQueue.add(new Action(ActionString.becomeHungry, 3, 1));
			stateChanged();
		}
	}

	public void msgGoToWork(int i) {
		print("Go to work: work period #" + i);
		actionQueue.add(new Action(ActionString.goToWork, 2, i));
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
	
	public void msgPayFare(double fare) {
		// TODO msgPayFare
		event = PersonEvent.makingDecision;
		stateChanged();
	}

	// from Bank
	public void msgLeftBank(Bank theBank, int accountNumber, double change, double loanAmount, int loanTime) {
		print("Leaving bank");
		if(myPersonalBankAccount == null) {
			myPersonalBankAccount = new MyBankAccount(accountNumber, "Personal", theBank, change, loanAmount, loanTime);
		}
		else {
			myPersonalBankAccount.amount -= change;
			myPersonalBankAccount.loanNeeded += loanAmount;
			myPersonalBankAccount.loanTime = loanTime;
		}
		moneyOnHand += change;
		event = PersonEvent.makingDecision;
		bankState = BankState.None;
		stateChanged();
	}
	
	// from Restaurant
	public void msgDoneEating(boolean success, double newMoneyOnHand) {
		if(success) {
			isNourished = true;
			moneyOnHand = newMoneyOnHand;
		}
		preferEatAtHome = !preferEatAtHome;
		event = PersonEvent.makingDecision;
		stateChanged();
	}
	
	// from Market
	public void msgHereIsOrder(String order, int quantity) {
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
			print("Calling PersonAgent's scheduler: currentStateLocation = " + currentLocationState);
		}
		
		// action queue for urgent actions
		if((event == PersonEvent.makingDecision || event == PersonEvent.onHold)
				&& actionQueue.size() > 0) {
			Action theAction = actionQueue.poll();
			switch(theAction.action) {
				case becomeHungry:
					isNourished = false; break;
				case wakeUp:
					bodyState = BodyState.Active; break;
				case goToSleep:
					bodyState = BodyState.Tired; break;
				case payRent:
					rentToPay += theAction.amount; break;
				case receiveRent:
					moneyOnHand += theAction.amount; break;
				case needMaintenance: 
					doMaintenance(); break;
				case goToWork:
					checkGoingToWork((int)theAction.amount); break; // TODO: handle go to work
			}
			event = PersonEvent.makingDecision;
			print("returning true from action queue; popped: " + theAction.action + "; size: " + actionQueue.size());
			return true;
		}
		
		// if no emergenices, proceed with normal decision rules
		if(event == PersonEvent.makingDecision && bodyState != BodyState.Asleep) {
			
			if(currentLocationState == LocationState.Home) { // at home
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
					print("returning true because !insideHouse");
					return true;
				}
				if(rentToPay > 0) {
					if(moneyOnHand >= rentToPay) {
						payRent(rentToPay);
					}
					else {
						getRentMoneyFromBank();
					}
					print("returning true because rentToPay > 0");
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
					print("returning true because !isNourished");
					return true;
				}
				if(currentLocation.equals(targetLocation)) {
					if(moneyOnHand > MONEY_ON_HAND_LIMIT) {
						haveMoneyToDeposit();
						print("returning true because haveMoneyToDeposit()");
						return true;
					}
				}
				else {
					leaveHouse();
					event = PersonEvent.onHold;
					print("returning true because isNourished");
					return true;
				}
				if(bodyState == BodyState.Tired) {
					goToSleep();
					bodyState = BodyState.Asleep;
					event = PersonEvent.onHold;
				}
			}
			if(currentLocationState == LocationState.Bank) { // at bank
				
				switch(bankState) {
					case NeedTransaction:
						if(myPersonalBankAccount == null) {
							requestNewAccount();
							event = PersonEvent.onHold;
						}
						else if(moneyWanted > 0) {
							requestWithdrawal();
							event = PersonEvent.onHold;
						}
						else if(moneyToDeposit > 0) {
							requestDeposit();
							event = PersonEvent.onHold;
						}
						break;
					case None:
						// Done at bank, time to transition
						if(!currentLocation.equals(targetLocation)) {
							goToTransportation();
							event = PersonEvent.onHold;
						}
						else {
							if(!isNourished && !preferEatAtHome) {
								hungryToRestaurant();
							}
							else if(preferEatAtHome) {
								goHome();
								event = PersonEvent.onHold;
							}
						}
						break;
				}
				return true;
			}
			if(currentLocationState == LocationState.Restaurant) { // at restaurant
				if(!isNourished) {
					enterRestaurant();
					event = PersonEvent.onHoldAtRestaurant;
				}
				else {
					goHome();
					event = PersonEvent.onHold;
				}
				print("Restaurant: setting on hold");
				return true;
			}
			if(currentLocationState == LocationState.Market) { // at market
				
				if(!isNourished && !preferEatAtHome) {
					hungryToRestaurant();
				}
				switch(marketState) {
					case None:
						goHome();
						event = PersonEvent.onHold;
						break;
					case WantToBuy:
						enterMarket();
						event = PersonEvent.onHoldInMarket;
						break;
					case WantToWork:
						// TODO go to work
						event = PersonEvent.onHold;
						break;
				}
				print("Market: event onHold set");
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

	private void checkGoingToWork(int workPeriod) {
		// TODO checkGoingToWork
	}

	//House actions
	private void enterHouse() {
		print("Entering house, adding items");
		Map<String, Integer> copyOfItems = new HashMap<String, Integer>();
		
		Set<String> keySet = itemsOnHand.keySet();
		String[] keyArray = keySet.toArray(new String[keySet.size()]);
		for(int i = 0; i < keyArray.length; i++) {
			Integer aQuantity = itemsOnHand.get(keyArray[i]);
			if(aQuantity > 0)
				copyOfItems.put(keyArray[i], aQuantity);
			itemsOnHand.put(keyArray[i], 0);
		}
		myHome.housing.msgIAmHome(this, copyOfItems);
	}
	
	private void prepareToCookAtHome() {
		print("I'm hungry and I want to cook at home");
		myHome.housing.msgPrepareToCookAtHome(this, foodPreference);
	}
	
	private void getRentMoneyFromBank() {
		print("I need rent money");
		log.add(new LoggedEvent("Need to pay rent; not enough money"));
		moneyWanted += rentToPay;
		bankState = BankState.NeedTransaction;
		MyBank targetBank = chooseBank();
		targetLocation = targetBank.name;
	}
	
	private void hungryToMarket() {
		print("I'm hungry and I want to buy food at market and cook at home");
		MyMarket targetMarket = chooseMarket();
		double price = targetMarket.theMarket.getPrice(foodPreference);
		if(moneyOnHand < price) {
			log.add(new LoggedEvent("Want to buy food at market; not enough money"));
			moneyWanted = price - moneyOnHand;
			bankState = BankState.NeedTransaction;
			MyBank targetBank = chooseBank();
			targetLocation = targetBank.name;
			return;
		}
		print("I have enough money to buy food from market");
		targetLocation = targetMarket.name;
	}
	
	private void haveMoneyToDeposit() {
		print("I have excess money to deposit");
		moneyToDeposit = moneyOnHand - MONEY_ON_HAND_LIMIT;
		bankState = BankState.NeedTransaction;
		MyBank targetBank = chooseBank();
		targetLocation = targetBank.name;
	}

	private void doMaintenance() {
		print("Performing maintenance");
		myHome.housing.msgDoMaintenance();
	}
	
	private void payRent(double amount) {
		print("Paying rent");
		moneyOnHand -= amount;
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
	
	//Restaurant actions
	private void hungryToRestaurant() {
		print("I'm hungry and I want to eat at restaurant");
		MyRestaurant targetRestaurant = chooseRestaurant();
		Map<String, Double> theMenu = targetRestaurant.menu;
		double lowestPrice = getLowestPrice(theMenu);
		if(moneyOnHand < lowestPrice) {
			log.add(new LoggedEvent("Want to eat at restaurant; not enough money"));
			moneyWanted = lowestPrice - moneyOnHand;
			bankState = BankState.NeedTransaction;
			MyBank targetBank = chooseBank();
			targetLocation = targetBank.name;
			return;
		}
		print("I have enough money to buy from restaurant");
		targetLocation = targetRestaurant.name;
	}
	
	private void enterRestaurant() {
		print("Entering restaurant");
		MyRestaurant myRest = (MyRestaurant)currentMyObject;
		myRest.restaurant.personAs(this, myRest.personType, name, moneyOnHand);
	}
	
	//Transportation actions
	private void goToTransportation() {
		print("Going from " + currentLocation + " to " + targetLocation);
		log.add(new LoggedEvent("Going from " + currentLocation + " to " + targetLocation));
		transportation.msgWantToGo(currentLocation, targetLocation, this, preferredCommute.name(), "Edgar");
	}
	
	private void goHome() {
		print("Going home");
		log.add(new LoggedEvent("Going home"));
		targetLocation = myHome.name;
		goToTransportation();
	}
	
	//Bank actions
	private void requestNewAccount() {
		print("Request new account");
		MyBank myBank = (MyBank)currentMyObject;
		log.add(new LoggedEvent("Creating account"));
		myBank.bank.msgRequestAccount(this, moneyToDeposit, true); // pointer to myself, money, present
	}
	
	private void requestWithdrawal() {
		print("Request withdrawal");
		MyBank myBank = (MyBank)currentMyObject;
		log.add(new LoggedEvent("Want to withdraw " + moneyWanted + " from " + myBank.name));
		myBank.bank.msgRequestWithdrawal(this, myPersonalBankAccount.accountNumber, moneyWanted, true);
	}
	
	private void requestDeposit() {
		print("Request deposit");
		MyBank myBank = (MyBank)currentMyObject;
		log.add(new LoggedEvent("Want to deposit " + moneyToDeposit + " from " + myBank.name));
		myBank.bank.msgRequestDeposit(this, myPersonalBankAccount.accountNumber, moneyToDeposit, true);
	}
	
	//Market actions
	private void enterMarket() {
		print("Entering market");
		MyMarket myMarket = (MyMarket)currentMyObject;
		myMarket.theMarket.personAs(this, "Customer", name, moneyOnHand, foodPreference, MARKET_PURCHASE_QUANTITY);
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
		// TODO: refine criteria for choosing restaurant
		MyObject[] myObjectsArray = getObjects();
		for(int i = 0; i < myObjectsArray.length; i++)
			if(myObjectsArray[i] instanceof MyRestaurant) {
				MyRestaurant tempRest = (MyRestaurant)myObjectsArray[i];
				//if(tempRest.restaurantType.equals(foodPreference))
					return tempRest;
			}
		return null;
	}
	
	private MyMarket chooseMarket() {
		MyObject[] myObjectsArray = getObjects();
		for(int i = 0; i < myObjectsArray.length; i++)
			if(myObjectsArray[i] instanceof MyMarket)
				return (MyMarket)myObjectsArray[i];
		return null;
	}
	
	private MyBank chooseBank() {
		MyObject[] myObjectsArray = getObjects();
		for(int i = 0; i < myObjectsArray.length; i++)
			if(myObjectsArray[i] instanceof MyBank)
				return (MyBank)myObjectsArray[i];
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
		String accountType;
		Bank bank;
		int accountNumber;
		double amount = 0, loanNeeded = 0;
		int loanTime;
		
		public MyBankAccount(int accountNumber, String accountType, Bank bank, double amount, double loanNeeded, int loanTime) {
			this.name = "Account " + accountNumber;
			this.accountType = accountType;
			this.bank = bank;
			this.accountNumber = accountNumber;
			this.amount = amount;
			this.loanNeeded = loanNeeded;
			this.loanTime = loanTime;
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
		
		Bank bank;
		String personType;
		public MyBank(Bank b, String name, String type) {
			bank = b;
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
