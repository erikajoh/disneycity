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
	
	public void setUp() throws Exception {
		super.setUp();
		
		Map<String, Double> menu = new HashMap<String, Double>();
		menu.put("Orange chicken", 10.00);
		menu.put("Beef with brocolli", 8.00);
		menu.put("Hot and sour soup", 5.00);
		
		mockHousing = new MockHousing_Douglass("Mock House 1");
		mockBank = new MockBank_Douglass("Mock Bank 1");
		mockRestaurant = new MockRestaurant_Douglass("Mock Restaurant 1", "Chinese", menu);
		mockTransportation = new MockTransportation_Douglass("Mock Transportation");

		person = new PersonAgent("Narwhal Prime", mockHousing, 
				"OwnerResident", mockTransportation);
	}	
	
	// TEST #1
	// Person: leave house, walk to bank, withdraw money, walk to restaurant,
	// run restaurant scenario (successfully eat and pay), walk to home
	public void testNormative_HomeBankRestaurantHome() {
		
		// setup
		person.setMoney(20);
		person.setFoodPreference("Chinese", false);
		person.setNourishmentLevel(0);
		
		person.addHousing(mockHousing, "OwnerResident"); // TODO: There are three types; OwnerResident, Owner, Renter
		person.addRestaurant(mockRestaurant, "Customer");
		
		// step 1 pre-conditions
		assertEquals("Person: 20 dollars at start",
				20.00, person.getMoney());
		assertEquals("Person: 0 nourishment at start", 
				0, person.getNourishmentLevel());
		
		// step 1: person tells transportation that he wants to go to restaurant
		assertTrue("Call scheduler, query restaurants, not enough money, scheduler returns true",
				person.pickAndExecuteAnAction());
		
		// step 1 post-conditions and step 2 pre-conditions
		assertEquals("Person: 3 event logs.",
				3, person.log.size());
		assertTrue("Contains log: want to go to restaurant but not enough money",
				person.log.containsString("Want to eat at restaurant; not enough money"));
		assertTrue("Contains log: describes going from house to bank",
				person.log.containsString("Going from Mock House 1 to Mock Bank 1"));
		assertTrue("Contains log: describes arriving at bank",
				person.log.containsString("Received msgReachedDestination: destination = Mock Bank 1"));
		
		assertEquals("Transportation: 1 event log",
				1, mockTransportation.log.size());
		
		// step 2: transportation sends person in transit
		
		
		// step 2 post-conditions and step 3 pre-conditions
		
		
		// step 3: transportation delivers person to bank
		
		
		// step 3 post-conditions and step 4 pre-conditions
		 
		 
		// step 4: person requests withdrawal from bank
		 
		 
		// step 4 post-conditions and step 5 pre-conditions
		
		
		// step 5: transportation delivers person to bank
		
		
		// step 5 post-conditions and step 6 pre-conditions
		 
		 
		// step 6: person requests withdrawal from bank 
		 
		 
		// step 6 post-conditions and step 7 pre-conditions
		
		
		// step 7: transportation delivers person to bank
		
		
		// step 7 post-conditions

		
	}
}

