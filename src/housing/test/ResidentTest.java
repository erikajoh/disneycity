package housing.test;
import housing.ResidentAgent;
import junit.framework.*;

/**
 * 
 * This class is a JUnit test class to unit test the RenterAgent's basic interaction
 * with the OwnerAgent and Building.
 *
 * @author Erika Johnson
 */
public class ResidentTest extends TestCase
{
	//these are instantiated for each test separately via the setUp() method.
	ResidentAgent renter;
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();		
		renter = new ResidentAgent("renter", "house");
//		owner = new MockOwner("mockowner");
	}	
	
	public void testRenterOwnerScenario() //Normative scenario
	{
//		//setUp() runs first before this test!
//		owner.renter = renter;
//		
//		//Testing rent setup
//		owner.msgWantToRent(renter);
//		assertTrue("Owner should have logged \"New renter is here\" but didn't. His log reads instead: " + owner.log.getLastLoggedEvent().toString(), owner.log.containsString("New renter is here"));
//		
////		//Testing rent payment
//////		renter.msgTimeToPay(10.0);
////		assertTrue("Renter's scheduler should have returned true (checking to make sure the renter sends the payment to the owner).", renter.pickAndExecuteAnAction());	
////		assertTrue("Owner should have logged \"Payment received from renter\" but didn't. His log reads instead: " + owner.log.getLastLoggedEvent().toString(), owner.log.containsString("Payment received from renter"));
////		renter.msgPaymentAccepted();
////		assertTrue("Renter should have logged \"Payment accepted by owner\" but didn't. His log reads instead: " + renter.log.getLastLoggedEvent().toString(), renter.log.containsString("Payment accepted by owner"));
//
//		//Testing cooking
//		owner.msgReadyToCook(renter, "pizza");
//		assertTrue("Owner should have logged \"Renter wants to cook\" but didn't. His log reads instead: " + owner.log.getLastLoggedEvent().toString(), owner.log.containsString("Renter wants to cook"));
//		renter.msgFoodDone();
//		assertTrue("Renter should have logged \"Food is done\" but didn't. His log reads instead: " + renter.log.getLastLoggedEvent().toString(), renter.log.containsString("Food is done"));
//
//		//Testing maintenance
//		owner.msgWantMaintenance(renter);
//		assertTrue("Owner should have logged \"Renter wants maintenance\" but didn't. His log reads instead: " + owner.log.getLastLoggedEvent().toString(), owner.log.containsString("Renter wants maintenance"));
//		renter.msgFinishedMaintenance();
//		assertTrue("Renter should have logged \"Finished maintenance\" but didn't. His log reads instead: " + renter.log.getLastLoggedEvent().toString(), renter.log.containsString("Finished maintenance"));
//		
//		//Testing leaving
//		renter.msgLeave();
//		assertTrue("Renter should have logged \"Leaving\" but didn't. His log reads instead: " + renter.log.getLastLoggedEvent().toString(), renter.log.containsString("Leaving"));
//
//		//Post condition check
//		assertFalse("Renter's scheduler should have returned false (no actions left to do), but didn't.", renter.pickAndExecuteAnAction());	
	}
	
}