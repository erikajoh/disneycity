package simcity.test;

import java.util.*;

import simcity.PersonAgent;
import simcity.Restaurant;
import simcity.test.mock.*;
import junit.framework.*;

/**
 * Unit test code for Person.
 */
public class PersonTest extends TestCase
{
	// instantiated in the setUp() method
	PersonAgent person;
	MockHousing_Douglass mockHousing;
	MockBank_Douglass mockBank;
	MockRestaurant_Douglass mockRestaurant;
	MockTransportation_Douglass mockTransportation;
	MockMarket_Douglass mockMarket;
	
	// custom test variables
	
	public void setUp() throws Exception {
		super.setUp();
		
		Map<String, Double> menu = new HashMap<String, Double>();
		menu.put("Beef with brocolli", 10.00);
		menu.put("Orange chicken", 13.00);
		menu.put("Hot and sour soup", 8.00);
		
		mockHousing = new MockHousing_Douglass("Mock House 1");
		mockBank = new MockBank_Douglass("Mock Bank 1");
		mockRestaurant = new MockRestaurant_Douglass("Mock Restaurant 1", "Chinese", menu);
		mockTransportation = new MockTransportation_Douglass("Mock Transportation");

		person = new PersonAgent("Narwhal Prime", mockHousing, 0, "Chinese", false, "OwnerResident", mockTransportation, 'W');
	}	
	
	// TEST #1
	// Person: leave house, walk to bank, withdraw money, walk to restaurant,
	// run restaurant scenario (successfully eat and pay)
	public void testNormative_HomeBankRestaurant() {
		
		// setup
		person.setMoney(5);
		person.setFoodPreference("Chinese", false);
		person.setIsNourished(false);
		person.addBank(mockBank, "BankCustomer");
		person.addHousing(mockHousing, "OwnerResident"); // TODO: There are three types; OwnerResident, Owner, Renter
		person.addRestaurant(mockRestaurant, "Customer", 0);
		
		// step 1 pre-conditions
		assertEquals("Person: 5 dollars at start",
				5.00, person.getMoney());
		assertFalse("Person: not nourished at start", 
				person.getIsNourished());
		assertEquals("Restaurant: 3 food items", 
				3, mockRestaurant.getMenu().menuItems.size());
		
		// step 1: person wants to go to restaurant, needs money first
			// step 1a: person tells transportation that he wants to go to restaurant
			// step 1b: transportation sends person in transit
			// step 1c: person arrives at bank
		person.msgWakeUp();
		assertTrue("Call scheduler, wake up, scheduler returns true",
				person.pickAndExecuteAnAction());
		
		// step 1 post-conditions and step 2 pre-conditions
		assertEquals("Person: 1 event logs",
				1, person.log.size());
		assertTrue("Contains log: Must wake up",
				person.log.containsString("Must wake up"));
		assertTrue("Call scheduler, enter restaurant, scheduler returns true",
				person.pickAndExecuteAnAction());
		
		person.msgDoneEntering();
		assertEquals("Person: 3 event logs",
				3, person.log.size());
		assertTrue("Contains log: Returning true because !insideHouse",
				person.log.containsString("Returning true because !insideHouse"));
		assertTrue("Contains log: msgDoneEntering() called",
				person.log.containsString("msgDoneEntering() called"));
		assertTrue("Person: event = makingDecision", 
				person.event.toString().equals("makingDecision"));
		
		assertTrue("Call scheduler, deciding hunger, scheduler returns true",
				person.pickAndExecuteAnAction());
		assertTrue("Call scheduler, query restaurants, not enough money, scheduler returns true",
				person.pickAndExecuteAnAction());
		assertEquals("Person: 6 event logs",
				6, person.log.size());
		assertTrue("Contains log: want to go to restaurant but not enough money",
				person.log.containsString("returning true because !isNourished"));
		assertTrue("Contains log: want to go to restaurant but not enough money",
				person.log.containsString("Want to eat at restaurant; not enough money"));		
		
		person.msgDoneLeaving(); // leaving house to go get food
		assertEquals("Person: 7 event logs",
				7, person.log.size());
		assertTrue("Person: event = makingDecision", 
				person.event.toString().equals("makingDecision"));
		
		assertTrue("Call scheduler, set target destination to bank, scheduler returns true",
				person.pickAndExecuteAnAction());
		assertTrue("Contains log: describes going from house to bank",
				person.log.containsString("Going from Mock House 1 to Mock Bank 1"));
		
		assertFalse("Call scheduler, in transit, on hold, scheduler returns false",
				person.pickAndExecuteAnAction());
		
		mockTransportation.msgWantToGo(person.getCurrLocation(), "Mock Bank 1", person, "method", "Edgar");
		assertEquals("Received msgWantToGo: "
				+ "startLocation = " + "Mock House 1" + "; "
				+ "endLocation = " + "Mock Bank 1" + "; "
				+ "person = " + "Narwhal Prime" + "; "
				+ "method = method",
				"Received msgWantToGo: "
				+ "startLocation = " + person.getCurrLocation() + "; "
				+ "endLocation = " + "Mock Bank 1" + "; "
				+ "person = " + person.getName() + "; "
				+ "method = method");
		long startTime = System.currentTimeMillis();
		while(System.currentTimeMillis() - startTime < 2000);

		// step 2: person is at bank, withdraws money from account
			// step 2a: person requests withdrawal from bank, blocks
			// TODO: step 2b: bank checks if withdrawal is valid
			// step 2b: after brief delay, bank messages that withdrawal approved
			// step 2c: person receives money, gets released
		
		assertTrue("Call scheduler, arrive at bank successfully, scheduler returns true",
				person.pickAndExecuteAnAction());
		assertEquals("Person: 13 event logs",
				13, person.log.size());
		assertTrue("", person.log.containsString("Received msgReachedDestination: destination = Mock Bank 1"));
		assertTrue("Person: creates account",
				person.log.containsString("Creating account"));
		
		assertEquals("Transportation: 2 event log",
				2, mockTransportation.log.size());
		
		assertTrue("Call scheduler, nothing to do here, scheduler returns false",
				!person.pickAndExecuteAnAction());

		assertEquals("Person: currentLocation = Mock Bank 1",
				"Mock Bank 1", person.getCurrLocation());
		assertEquals("Person: currentLocationState = Bank",
				"Bank", person.getCurrLocationState());
		assertTrue("Person: event = onHoldInBank", 
				person.event.toString().equals("onHoldInBank"));
		
		mockBank.msgRequestWithdrawal(person, 1, 8.00 - person.getMoney(), true);
		
		// step 2 post-conditions and step 3 pre-conditions
		assertEquals("Person: 14 event logs",
				14, person.log.size());
		assertEquals("Person: has 8 dollars", 
				8.00, person.getMoney());
		assertTrue("Bank log: Received msgRequestWithdrawal(): amount = 3.0",
				mockBank.log.containsString("Received msgRequestWithdrawal(): amount"));
		
		assertEquals("Bank: 1 event log",
				1, mockBank.log.size());
		
		assertEquals("Person: currentLocation = Mock Bank 1",
				"Mock Bank 1", person.getCurrLocation());
		assertEquals("Person: currentLocationState = Bank",
				"Bank", person.getCurrLocationState());
		
		// step 3: person now goes to restaurant
		assertTrue("Call scheduler, go to restaurant, scheduler returns true",
				person.pickAndExecuteAnAction());
		 
		// step 3 post-conditions and step 4 pre-conditions
		
		assertTrue("Call scheduler, time to go home",
				person.pickAndExecuteAnAction());
		assertEquals("Person: 15 event logs",
				15, person.log.size());
		startTime = System.currentTimeMillis();
		while(System.currentTimeMillis() - startTime < 2000);
		
	}
	
	// TEST #2
	// Person: start with surplus money, go to bank, deposit
	public void testNormative_SurplusMoney() {
		// setup
		person.setMoney(500);
		person.setIsNourished(true);
		person.addBank(mockBank, "BankCustomer");
		person.addHousing(mockHousing, "OwnerResident");
		
		assertEquals("Person: 500 dollars at start",
				500.00, person.getMoney());
		assertTrue("Person: nourished at start", 
				person.getIsNourished());
		
		// step 1: wake up
		person.msgWakeUp();
		assertTrue("Call scheduler, wake up, scheduler returns true",
				person.pickAndExecuteAnAction());
		
		assertEquals("Person: 1 event logs",
				1, person.log.size());
		assertTrue("Contains log: Must wake up",
				person.log.containsString("Must wake up"));
		assertTrue("Call scheduler, enter restaurant, scheduler returns true",
				person.pickAndExecuteAnAction());
		
		// step 2: transportation
		mockTransportation.msgWantToGo("Mock House 1", "Mock Bank 1", person, "method", "Edgar");
	}
}
