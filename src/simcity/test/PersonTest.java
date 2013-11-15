package simcity.test;

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
	MockBank mockBank;
	MockRestaurant mockRestaurant;
	MockTransportation mockTransportation;

	//Make sure all logs are empty, or at correct numbers
	//LOOP
	//Send message to cashier
	//Message received correctly
	//Call scheduler
	//Check correct post conditions
		//Right Recipients
		//Right number of messages
		//Right message contents
	
	public void setUp() throws Exception{
		super.setUp();
		mockBank = new MockBank("Mock Bank 1");
		mockRestaurant = new MockRestaurant("Mock Restaurant 1");
		mockTransportation = new MockTransportation("Mock Transportation");

		person = new PersonAgent("Narwhal Prime", mockTransportation);
	}	
	
	// TEST #1
	// Person: leave house, walk to bank, withdraw money, walk to restaurant,
	// run restaurant scenario (successfully eat and pay), walk to home
	public void testNormative_HomeBankRestaurantHome() {
		
		// setup and step 1 pre-conditions
		person.setMoney(20);
		person.setFoodPreference("Steak", false);
		person.setNourishmentLevel(0);
		
		// step 1: person tells transportation that he wants to go to restaurant
		
		
		// step 1 post-conditions and step 2 pre-conditions
		
		
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

