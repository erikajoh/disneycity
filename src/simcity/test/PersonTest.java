package simcity.test;

import java.util.*;

import simcity.PersonAgent;
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

		person = new PersonAgent("Narwhal Prime", mockHousing, 
				"OwnerResident", mockTransportation);
	}	
	
	// TEST #1
	// Person: leave house, walk to bank, withdraw money, walk to restaurant,
	// run restaurant scenario (successfully eat and pay), walk to home, done
	public void testNormative_HomeBankRestaurantHome() {
		
		// setup
		person.setMoney(5);
		person.setFoodPreference("Chinese", false);
		person.setNourishmentLevel(0);
		person.addBank(mockBank, "BankCustomer");
		person.addHousing(mockHousing, "OwnerResident"); // TODO: There are three types; OwnerResident, Owner, Renter
		person.addRestaurant(mockRestaurant, "Customer");
		
		// step 1 pre-conditions
		assertEquals("Person: 5 dollars at start",
				5.00, person.getMoney());
		assertEquals("Person: 0 nourishment at start", 
				0, person.getNourishmentLevel());
		assertEquals("Restaurant: 3 food items", 
				3, mockRestaurant.menu.size());
		
		// step 1: person wants to go to restaurant, needs money first
			// step 1a: person tells transportation that he wants to go to restaurant
			// step 1b: transportation sends person in transit
			// step 1c: person arrives at bank
		assertTrue("Call scheduler, query restaurants, not enough money, scheduler returns true",
				person.pickAndExecuteAnAction());
		
		// step 1 post-conditions and step 2 pre-conditions
		assertEquals("Person: 3 event logs",
				3, person.log.size());
		assertTrue("Contains log: want to go to restaurant but not enough money",
				person.log.containsString("Want to eat at restaurant; not enough money"));
		assertTrue("Contains log: describes going from house to bank",
				person.log.containsString("Going from Mock House 1 to Mock Bank 1"));
		assertTrue("Contains log: describes arriving at bank",
				person.log.containsString("Received msgReachedDestination: destination = Mock Bank 1"));
		
		assertEquals("Transportation: 1 event log",
				1, mockTransportation.log.size());

		assertEquals("Person: currentLocation = Mock Bank 1",
				"Mock Bank 1", person.getCurrLocation());
		assertEquals("Person: currentLocationState = Bank",
				"Bank", person.getCurrLocationState());
		
		
		// TODO: Even more tests; make sure all relevant person and transportation variables are tested
		 
		// step 2: person is at bank, withdraws money from account
			// step 2a: person requests withdrawal from bank, blocks
			// TODO: step 2b: bank checks if withdrawal is valid
			// step 2b: after brief delay, bank messages that withdrawal approved
			// step 2c: person receives money, gets released
		assertTrue("Call scheduler, request withdrawal from bank, scheduler returns true",
				person.pickAndExecuteAnAction());
		 
		// step 2 post-conditions and step 3 pre-conditions
		assertEquals("Person: 5 event logs",
				5, person.log.size());
		assertEquals("Person: has 8 dollars", 
				8.00, person.getMoney());
		assertTrue("Contains log: Want to withdraw 3.0 from Mock Bank 1",
				person.log.containsString("Want to withdraw 3.0 from Mock Bank 1"));
		assertTrue("Contains log: Received msgWithdrawalSuccessful: amount = 3.0",
				person.log.containsString("Received msgWithdrawalSuccessful: amount = 3.0"));
		
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
		 
		assertEquals("Person: 7 event logs",
				7, person.log.size());
		assertTrue("Contains log: describes going from bank to restaurant",
				person.log.containsString("Going from Mock Bank 1 to Mock Restaurant 1"));
		assertTrue("Contains log: describes arriving at restaurant",
				person.log.containsString("Received msgReachedDestination: destination = Mock Restaurant 1"));
		
		assertEquals("Person: currentLocation = Mock Restaurant 1",
				"Mock Restaurant 1", person.getCurrLocation());
		assertEquals("Person: currentLocationState = Restaurant",
				"Restaurant", person.getCurrLocationState());
		 
		// step 4: person enters and eats at restaurant
		assertTrue("Call scheduler, enter restaurant, eat, scheduler returns true",
				person.pickAndExecuteAnAction());
		long startTime = System.currentTimeMillis();
		while(System.currentTimeMillis() - startTime < 2000);
		
		// step 4 post-conditions and step 5 pre-conditions
		assertEquals("Restaurant: 1 event log",
				1, mockRestaurant.log.size());
		assertTrue("Contains log: person eats at restaurant",
				person.log.containsString("Received msgReachedDestination: destination = Mock Restaurant 1"));
		
		assertEquals("Person: 1 nourishment level",
				1, person.getNourishmentLevel());
		
		// step 5: person is done eating, goes home 
		assertTrue("Call scheduler, going home, scheduler returns true",
				person.pickAndExecuteAnAction());
		
		// step 5 post-conditions
		assertEquals("Person: currentLocation = Mock House 1",
				"Mock House 1", person.getCurrLocation());
		assertEquals("Person: currentLocationState = Home",
				"Home", person.getCurrLocationState());
		
	}
	
	// TEST #2
	// Person: start at house, want to eat at home, not enough food,
	// not enough money, go to bank, go to market, walk to home, cook, eat, done
	public void testNormative_HomeBankMarketHomeCook() {
		// setup
		person.setMoney(5);
		person.setFoodPreference("Chinese", false);
		person.setNourishmentLevel(0);
		person.addBank(mockBank, "BankCustomer");
		person.addHousing(mockHousing, "OwnerResident"); // TODO: There are three types; OwnerResident, Owner, Renter
		person.addRestaurant(mockRestaurant, "Customer");
		
		
	}
	
	// TEST #3a
	// Person: start at house, has enough money on hand to buy car, walks to market,
	// buys car, drives home, done
	public void testNormative_HomeMarketCarHome() {
		
	}

	// TEST #3b
	// Person: start at house, has enough money on hand and in bank to buy car, walks to market,
	// buys car, drives home, done 
	public void testNormative_HomeBankMarketCarHome() {
		
	}
}

