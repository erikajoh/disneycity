package transportation.Test;

import transportation.Agents.*;
import junit.framework.*;

public class TransportationControllerTest extends TestCase {
	//these are instantiated for each test separately via the setUp() method.
	TransportationController controller;
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();
		controller = new TransportationController(null);
	}	

	public void testReceivedWalkerRequest() {
		assertEquals("movingObjects size should be 0. Instead it is " + String.valueOf(controller.movingObjects.size()), 0, controller.movingObjects.size());
		assertEquals("busStops size should be 3. Instead it is " + String.valueOf(controller.busStops.size()), 3, controller.busStops.size());
		
		assertFalse("Scheduler should have nothing to do", controller.pickAndExecuteAnAction());
		
		
	}
	
	/*
	 * assertTrue("MockMarket should have logged a \"Cashier Payed Part\", but didn't. His log reads instead: " 
				+ market1.log.getLastLoggedEvent().toString(), market1.log.containsString("Cashier Payed Part"));
	 */
	
}
