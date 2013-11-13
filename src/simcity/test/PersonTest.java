package simcity.test;

import simcity.PersonAgent;
import junit.framework.*;

/**
 * Unit test code for Person.
 */
public class PersonTest extends TestCase
{
	// instantiated in the setUp() method
	PersonAgent person;

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
		person = new PersonAgent("Narwhal Prime");
	}	
	
	// TEST #1
	// Person: leave house, walk to bank, withdraw money, walk to restaurant,
	// run restaurant scenario (successfully eat and pay), walk to home
	public void testNormative_HomeBankRestaurantHome() {
		
		
		/*
		// step 1 pre-conditions
		cashier.setTotalMoney(10.00);
		
		assertEquals("Cashier should have empty event log before msgCustomerNeedsCheck is called. Cashier's event log: "
				+ cashier.log.toString(), 0, cashier.log.size());
		assertEquals("Cashier should have 10 dollars to start", 10.00, cashier.getTotalMoney());
		
		// step 1: market gives cashier the bill
		FoodBill fb = new FoodBill("Salad", marketOne, 10);
		cashier.msgHereIsBill(fb);
		
		// step 1 post-conditions and step 2 pre-conditions
		//"msgHereIsBill from " + fb.market.getName() + " for order " + fb.order + " and amount " + fb.amountDue));
		assertTrue("Cashier should have logged \"msgHereIsBill from mockmarketOne for order Salad and amount 10.0\". Log instead reads: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("msgHereIsBill from mockmarketOne for order Salad and amount 10.0"));
		
		// step 2: cashier pays back the bill in full
		assertTrue("Cashier's scheduler should have returned true, but didn't", cashier.pickAndExecuteAnAction());
		assertFalse("Cashier's scheduler should have returned false, but didn't", cashier.pickAndExecuteAnAction());
		
		// step 2 post-conditions
		assertTrue("MockMarket should have logged \"msgHereIsBill from cashier\". Log instead reads: " 
				+ marketOne.log.getLastLoggedEvent().toString(), marketOne.log.containsString("msgHereIsBill from cashier: valid payment"));
		assertEquals("Cashier should have no money at end", 0.0, cashier.getTotalMoney());
		assertEquals("Cashier should have 1 event log at the end."
				+ cashier.log.toString(), 1, cashier.log.size());
		*/
	}
}
