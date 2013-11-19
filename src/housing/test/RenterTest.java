package housing.test;
import housing.RenterAgent;
import housing.interfaces.Owner;
import housing.test.mock.MockOwner;
import junit.framework.*;

/**
 * 
 * This class is a JUnit test class to unit test the RenterAgent's basic interaction
 * with the OwnerAgent and Building.
 *
 * @author Erika Johnson
 */
public class RenterTest extends TestCase
{
	//these are instantiated for each test separately via the setUp() method.
	RenterAgent renter;
	MockOwner owner;
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();		
		renter = new RenterAgent("renter");
		owner = new MockOwner("mockowner");
	}	
	
	public void testRenterOwnerScenario() //Normative scenario
	{
		
		//setUp() runs first before this test!
		owner.renter = renter;				
		
		//Testing rent setup
		owner.msgWantToRent(renter);
		assertTrue("Owner should have logged \"New renter is here\" but didn't. His log reads instead: " + owner.log.getLastLoggedEvent().toString(), owner.log.containsString("New renter is here"));
		
		//Testing rent payment
		renter.msgTimeToPay(owner, 10.0);
		while(renter.pickAndExecuteAnAction());
		assertFalse("Renter's scheduler should have returned true (checking to make sure the renter sends the payment to the owner).", renter.pickAndExecuteAnAction());	
		assertTrue("Owner should have logged \"Payment received from renter\" but didn't. His log reads instead: " + owner.log.getLastLoggedEvent().toString(), owner.log.containsString("Payment received from renter"));
		//assertTrue("Renter should have logged \"Payment accepted by owner\" but didn't. His log reads instead: " + renter.log.getLastLoggedEvent().toString(), renter.log.containsString("Payment accepted by owner"));
		
		//Testing maintenance
		
		//Testing cooking
				
		//cashier.msgHereIsBill(market, 7.5);//send the message from a market
		//check postconditions for step 1 and preconditions for step 2
//		assertEquals("MockMarket should have an empty event log before the Cashier's scheduler is called. Instead, the MockMarket's event log reads: "
//						+ market.log.toString(), 0, market.log.size());
//		assertEquals("Cashier should have 1 bill in it. It doesn't.", cashier.marketBills.size(), 1);
//		while(cashier.pickAndExecuteAnAction());
//		assertFalse("Cashier's scheduler should have returned true (checking to make sure the cashier sends the payment to the market).", cashier.pickAndExecuteAnAction());	
//		assertEquals(
//				"MockMarket should have have a log size of 1."
//						+ market.log.toString(), 1, market.log.size());
		//step 2 of the test
//		cashier.msgHereIsChange(8.5);
		//check postconditions for step 2 / preconditions for step 3
//		assertTrue("Cashier should have logged \"Received msgHereIsChange\" but didn't. His log reads instead: " 
//				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgHereIsChange"));
		//step 3			
//		assertFalse("Cashier's scheduler should have returned false (no actions left to do), but didn't.", 
//				cashier.pickAndExecuteAnAction());	
		
	}
}