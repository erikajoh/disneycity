package transportation.Test;

import transportation.Agents.*;
import transportation.Test.mocks.mockPerson_Daron;
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

	//Generic spawning test and deletion test for a walker
	public void testReceivedWalkerRequest() {
		assertEquals("movingObjects size should be 0. Instead it is " + String.valueOf(controller.movingObjects.size()), 0, controller.movingObjects.size());
		assertEquals("busStops size should be 3. Instead it is " + String.valueOf(controller.busStops.size()), 3, controller.busStops.size());

		assertFalse("Scheduler should have nothing to do", controller.pickAndExecuteAnAction());

		mockPerson_Daron person = new mockPerson_Daron("person");
		controller.msgWantToGo("Haunted Mansion", "The Blue Bayou", person, "Walk", "Edgar");

		assertTrue("Controller should have logged a \"Received transportation request from\", but didn't. His log reads instead: " 
				+ controller.log.getLastLoggedEvent().toString(), controller.log.containsString("Received transportation request from"));

		assertEquals("movingObjects size should be 1. Instead it is " + String.valueOf(controller.movingObjects.size()), 1, controller.movingObjects.size());

		assertEquals("mover spawned should have a transportation state of REQUEST", TransportationController.TransportationState.REQUEST, controller.movingObjects.get(0).transportationState);

		assertTrue("Scheduler should be spawning a Walker", controller.pickAndExecuteAnAction());

		assertTrue("Controller should have logged a \"Spawning Walker\", but didn't. His log reads instead: " 
				+ controller.log.getLastLoggedEvent().toString(), controller.log.containsString("Spawning Walker"));

		assertEquals("mover should now have a transportation state of MOVING", TransportationController.TransportationState.MOVING, controller.movingObjects.get(0).transportationState);

		assertFalse("Scheduler should have nothing to do", controller.pickAndExecuteAnAction());

		controller.msgArrivedAtDestination(person);

		assertTrue("Controller should have logged a \"Person reached destination\", but didn't. His log reads instead: " 
				+ controller.log.getLastLoggedEvent().toString(), controller.log.containsString("Person reached destination"));

		assertEquals("mover should now have a transportation state of DESTINATION", TransportationController.TransportationState.DESTINATION, controller.movingObjects.get(0).transportationState);

		assertTrue("Scheduler should be trying to delete agent", controller.pickAndExecuteAnAction());

		assertTrue("Controller should have logged a \"Deleting mover\", but didn't. His log reads instead: " 
				+ controller.log.getLastLoggedEvent().toString(), controller.log.containsString("Deleting mover"));

		assertEquals("movingObjects size should be 0. Instead it is " + String.valueOf(controller.movingObjects.size()), 0, controller.movingObjects.size());

		assertFalse("Scheduler should have nothing to do", controller.pickAndExecuteAnAction());
	}

	//Generic spawning test and deletion test for a car
	public void testReceivedCarRequest() {
		assertEquals("movingObjects size should be 0. Instead it is " + String.valueOf(controller.movingObjects.size()), 0, controller.movingObjects.size());
		assertEquals("busStops size should be 3. Instead it is " + String.valueOf(controller.busStops.size()), 3, controller.busStops.size());

		assertFalse("Scheduler should have nothing to do", controller.pickAndExecuteAnAction());

		mockPerson_Daron person = new mockPerson_Daron("person");
		controller.msgWantToGo("Haunted Mansion", "The Blue Bayou", person, "Car", "Edgar");

		assertTrue("Controller should have logged a \"Received transportation request from\", but didn't. His log reads instead: " 
				+ controller.log.getLastLoggedEvent().toString(), controller.log.containsString("Received transportation request from"));

		assertEquals("movingObjects size should be 1. Instead it is " + String.valueOf(controller.movingObjects.size()), 1, controller.movingObjects.size());

		assertEquals("mover spawned should have a transportation state of REQUEST", TransportationController.TransportationState.REQUEST, controller.movingObjects.get(0).transportationState);

		assertTrue("Scheduler should be spawning a Walker", controller.pickAndExecuteAnAction());

		assertTrue("Controller should have logged a \"Spawning Car\", but didn't. His log reads instead: " 
				+ controller.log.getLastLoggedEvent().toString(), controller.log.containsString("Spawning Car"));

		assertEquals("mover should now have a transportation state of MOVING", TransportationController.TransportationState.MOVING, controller.movingObjects.get(0).transportationState);

		assertFalse("Scheduler should have nothing to do", controller.pickAndExecuteAnAction());

		controller.msgArrivedAtDestination(person);

		assertTrue("Controller should have logged a \"Person reached destination\", but didn't. His log reads instead: " 
				+ controller.log.getLastLoggedEvent().toString(), controller.log.containsString("Person reached destination"));

		assertEquals("mover should now have a transportation state of DESTINATION", TransportationController.TransportationState.DESTINATION, controller.movingObjects.get(0).transportationState);

		assertTrue("Scheduler should be trying to delete agent", controller.pickAndExecuteAnAction());

		assertTrue("Controller should have logged a \"Deleting mover\", but didn't. His log reads instead: " 
				+ controller.log.getLastLoggedEvent().toString(), controller.log.containsString("Deleting mover"));

		assertEquals("movingObjects size should be 0. Instead it is " + String.valueOf(controller.movingObjects.size()), 0, controller.movingObjects.size());

		assertFalse("Scheduler should have nothing to do", controller.pickAndExecuteAnAction());
	}

	//Generic spawning test and deletion test for a car
	public void testReceivedBusRequest() {
		assertEquals("movingObjects size should be 0. Instead it is " + String.valueOf(controller.movingObjects.size()), 0, controller.movingObjects.size());
		assertEquals("busStops size should be 3. Instead it is " + String.valueOf(controller.busStops.size()), 3, controller.busStops.size());

		assertFalse("Scheduler should have nothing to do", controller.pickAndExecuteAnAction());

		mockPerson_Daron person = new mockPerson_Daron("person");
		controller.msgWantToGo("Haunted Mansion", "The Blue Bayou", person, "Bus", "Edgar");

		assertTrue("Controller should have logged a \"Received transportation request from\", but didn't. His log reads instead: " 
				+ controller.log.getLastLoggedEvent().toString(), controller.log.containsString("Received transportation request from"));

		assertEquals("movingObjects size should be 1. Instead it is " + String.valueOf(controller.movingObjects.size()), 1, controller.movingObjects.size());

		assertEquals("mover spawned should have a transportation state of REQUEST", TransportationController.TransportationState.REQUEST, controller.movingObjects.get(0).transportationState);

		assertTrue("Scheduler should be spawning a BusWalker", controller.pickAndExecuteAnAction());

		assertTrue("Controller should have logged a \"Spawning Bus\", but didn't. His log reads instead: " 
				+ controller.log.getPenultimateLoggedEvent().toString(), controller.log.containsString("Spawning Bus"));

		assertTrue("Controller should have logged a \"Spawning Walker\", but didn't. His log reads instead: " 
				+ controller.log.getLastLoggedEvent().toString(), controller.log.containsString("Spawning Walker"));

		assertEquals("mover should now have a transportation state of MOVING", TransportationController.TransportationState.MOVING, controller.movingObjects.get(0).transportationState);

		assertFalse("Scheduler should have nothing to do", controller.pickAndExecuteAnAction());

		controller.msgArrivedAtDestination(person);

		assertTrue("Controller should have logged a \"Person reached destination\", but didn't. His log reads instead: " 
				+ controller.log.getLastLoggedEvent().toString(), controller.log.containsString("Person reached destination"));

		assertEquals("mover should now have a transportation state of DESTINATION", TransportationController.TransportationState.DESTINATION, controller.movingObjects.get(0).transportationState);

		assertTrue("Scheduler should be trying to delete agent", controller.pickAndExecuteAnAction());

		assertTrue("Controller should have logged a \"Deleting mover\", but didn't. His log reads instead: " 
				+ controller.log.getLastLoggedEvent().toString(), controller.log.containsString("Deleting mover"));

		assertEquals("movingObjects size should be 0. Instead it is " + String.valueOf(controller.movingObjects.size()), 0, controller.movingObjects.size());

		assertFalse("Scheduler should have nothing to do", controller.pickAndExecuteAnAction());
	}

	//Tests the controller attempting to spawn a walker and failing because the semaphore tile is taken. Then tries again after the semaphore is released and spawns the agent.
	public void testFailureToSpawn() {
		assertEquals("movingObjects size should be 0. Instead it is " + String.valueOf(controller.movingObjects.size()), 0, controller.movingObjects.size());
		assertEquals("busStops size should be 3. Instead it is " + String.valueOf(controller.busStops.size()), 3, controller.busStops.size());

		assertFalse("Scheduler should have nothing to do", controller.pickAndExecuteAnAction());

		mockPerson_Daron person = new mockPerson_Daron("person");
		controller.msgWantToGo("Haunted Mansion", "The Blue Bayou", person, "Walk", "Edgar");

		assertTrue("Controller should have logged a \"Received transportation request from\", but didn't. His log reads instead: " 
				+ controller.log.getLastLoggedEvent().toString(), controller.log.containsString("Received transportation request from"));

		assertEquals("movingObjects size should be 1. Instead it is " + String.valueOf(controller.movingObjects.size()), 1, controller.movingObjects.size());

		assertEquals("mover spawned should have a transportation state of REQUEST", TransportationController.TransportationState.REQUEST, controller.movingObjects.get(0).transportationState);

		//the location of the haunted mansion
		try {
			controller.getGrid()[13][3].acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		assertTrue("Scheduler should be trying to spawn a Walker", controller.pickAndExecuteAnAction());

		assertTrue("Controller should have logged a \"Failed to spawn Walker\", but didn't. His log reads instead: " 
				+ controller.log.getLastLoggedEvent().toString(), controller.log.containsString("Failed to spawn Walker"));

		assertEquals("mover should now have a transportation state of WAITINGTOSPAWN", TransportationController.TransportationState.WAITINGTOSPAWN, controller.movingObjects.get(0).transportationState);

		assertTrue("Scheduler should reset walker state to respawn next scheduler call", controller.pickAndExecuteAnAction());

		assertTrue("Controller should have logged a \"Resetting mover state\", but didn't. His log reads instead: " 
				+ controller.log.getLastLoggedEvent().toString(), controller.log.containsString("Resetting mover state"));

		assertEquals("mover should now have a transportation state of REQUEST", TransportationController.TransportationState.REQUEST, controller.movingObjects.get(0).transportationState);

		controller.getGrid()[13][3].release();

		assertTrue("Scheduler should be spawning a Walker", controller.pickAndExecuteAnAction());

		assertTrue("Controller should have logged a \"Spawning Walker\", but didn't. His log reads instead: " 
				+ controller.log.getLastLoggedEvent().toString(), controller.log.containsString("Spawning Walker"));

		assertEquals("mover should now have a transportation state of MOVING", TransportationController.TransportationState.MOVING, controller.movingObjects.get(0).transportationState);

		assertFalse("Scheduler should have nothing to do", controller.pickAndExecuteAnAction());
	}

	//Slightly more complicated test where multiple requests and deletes occur simultaneously
	public void realisticSpawningTest() {
		mockPerson_Daron person1 = new mockPerson_Daron("person1");
		mockPerson_Daron person2 = new mockPerson_Daron("person2");
		mockPerson_Daron person3 = new mockPerson_Daron("person3");
		assertEquals("movingObjects size should be 0. Instead it is " + String.valueOf(controller.movingObjects.size()), 0, controller.movingObjects.size());
		assertEquals("busStops size should be 3. Instead it is " + String.valueOf(controller.busStops.size()), 3, controller.busStops.size());

		assertFalse("Scheduler should have nothing to do", controller.pickAndExecuteAnAction());

		controller.msgWantToGo("Haunted Mansion", "The Blue Bayou", person1, "Walk", "Edgar");

		assertTrue("Controller should have logged a \"Received transportation request from\", but didn't. His log reads instead: " 
				+ controller.log.getLastLoggedEvent().toString(), controller.log.containsString("Received transportation request from"));

		assertEquals("movingObjects size should be 1. Instead it is " + String.valueOf(controller.movingObjects.size()), 1, controller.movingObjects.size());

		assertEquals("person1 mover spawned should have a transportation state of REQUEST", TransportationController.TransportationState.REQUEST, controller.movingObjects.get(0).transportationState);

		assertTrue("Scheduler should be spawning a Walker", controller.pickAndExecuteAnAction());

		assertTrue("Controller should have logged a \"Spawning Walker\", but didn't. His log reads instead: " 
				+ controller.log.getLastLoggedEvent().toString(), controller.log.containsString("Spawning Walker"));

		assertEquals("person1 should now have a transportation state of MOVING", TransportationController.TransportationState.MOVING, controller.movingObjects.get(0).transportationState);

		controller.msgWantToGo("Village Haus", "The Blue Bayou", person2, "Car", "Edgar");

		assertEquals("movingObjects size should be 2. Instead it is " + String.valueOf(controller.movingObjects.size()), 2, controller.movingObjects.size());
		assertEquals("person1 should now have a transportation state of MOVING", TransportationController.TransportationState.MOVING, controller.movingObjects.get(0).transportationState);
		assertEquals("person2 should now have a transportation state of REQUEST", TransportationController.TransportationState.REQUEST, controller.movingObjects.get(1).transportationState);

		assertTrue("Scheduler should be spawning a Car", controller.pickAndExecuteAnAction());

		assertTrue("Controller should have logged a \"Spawning Car\", but didn't. His log reads instead: " 
				+ controller.log.getLastLoggedEvent().toString(), controller.log.containsString("Spawning Car"));

		assertEquals("person1 should now have a transportation state of MOVING", TransportationController.TransportationState.MOVING, controller.movingObjects.get(0).transportationState);
		assertEquals("person2 should now have a transportation state of MOVING", TransportationController.TransportationState.MOVING, controller.movingObjects.get(1).transportationState);

		controller.msgWantToGo("The Blue Bayou", "The Blue Bayou", person3, "Walk", "Edgar");

		assertEquals("movingObjects size should be 3. Instead it is " + String.valueOf(controller.movingObjects.size()), 3, controller.movingObjects.size());
		assertEquals("person1 should now have a transportation state of MOVING", TransportationController.TransportationState.MOVING, controller.movingObjects.get(0).transportationState);
		assertEquals("person2 should now have a transportation state of MOVING", TransportationController.TransportationState.MOVING, controller.movingObjects.get(1).transportationState);
		assertEquals("person3 should now have a transportation state of REQUEST", TransportationController.TransportationState.REQUEST, controller.movingObjects.get(2).transportationState);

		controller.msgArrivedAtDestination(person2);

		assertTrue("Controller should have logged a \"Person reached destination\", but didn't. His log reads instead: " 
				+ controller.log.getLastLoggedEvent().toString(), controller.log.containsString("Person reached destination"));

		assertEquals("person1 should now have a transportation state of MOVING", TransportationController.TransportationState.MOVING, controller.movingObjects.get(0).transportationState);
		assertEquals("person2 should now have a transportation state of DESTINATION", TransportationController.TransportationState.DESTINATION, controller.movingObjects.get(1).transportationState);
		assertEquals("person3 should now have a transportation state of REQUEST", TransportationController.TransportationState.REQUEST, controller.movingObjects.get(2).transportationState);

		assertTrue("Scheduler should be spawning the Walker before deleting", controller.pickAndExecuteAnAction());

		assertTrue("Controller should have logged a \"Spawning Walker\", but didn't. His log reads instead: " 
				+ controller.log.getLastLoggedEvent().toString(), controller.log.containsString("Spawning Walker"));

		assertEquals("person1 should now have a transportation state of MOVING", TransportationController.TransportationState.MOVING, controller.movingObjects.get(0).transportationState);
		assertEquals("person2 should now have a transportation state of DESTINATION", TransportationController.TransportationState.DESTINATION, controller.movingObjects.get(1).transportationState);
		assertEquals("person3 should now have a transportation state of MOVING", TransportationController.TransportationState.MOVING, controller.movingObjects.get(2).transportationState);

		assertTrue("Scheduler should be trying to delete agent", controller.pickAndExecuteAnAction());

		assertTrue("Controller should have logged a \"Deleting mover\", but didn't. His log reads instead: " 
				+ controller.log.getLastLoggedEvent().toString(), controller.log.containsString("Deleting mover"));

		assertEquals("movingObjects size should be 2. Instead it is " + String.valueOf(controller.movingObjects.size()), 2, controller.movingObjects.size());

		assertTrue("person3 should now be in the second spot of movingObjects.", controller.movingObjects.get(1).person.getName().equals(person3.getName()));

		assertEquals("person1 should now have a transportation state of MOVING", TransportationController.TransportationState.MOVING, controller.movingObjects.get(0).transportationState);
		assertEquals("person3 should now have a transportation state of MOVING", TransportationController.TransportationState.MOVING, controller.movingObjects.get(1).transportationState);

		assertFalse("Scheduler should have nothing to do", controller.pickAndExecuteAnAction());

		controller.msgArrivedAtDestination(person1);

		assertTrue("Controller should have logged a \"Person reached destination\", but didn't. His log reads instead: " 
				+ controller.log.getLastLoggedEvent().toString(), controller.log.containsString("Person reached destination"));

		controller.msgArrivedAtDestination(person3);

		assertTrue("Controller should have logged a \"Person reached destination\", but didn't. His log reads instead: " 
				+ controller.log.getLastLoggedEvent().toString(), controller.log.containsString("Person reached destination"));
		
		assertEquals("person1 should now have a transportation state of DESTINATION", TransportationController.TransportationState.DESTINATION, controller.movingObjects.get(0).transportationState);
		assertEquals("person3 should now have a transportation state of DESTINATION", TransportationController.TransportationState.DESTINATION, controller.movingObjects.get(1).transportationState);
		
		assertTrue("Scheduler should be trying to delete agent", controller.pickAndExecuteAnAction());

		assertTrue("Controller should have logged a \"Deleting mover\", but didn't. His log reads instead: " 
				+ controller.log.getLastLoggedEvent().toString(), controller.log.containsString("Deleting mover"));

		assertEquals("movingObjects size should be 1. Instead it is " + String.valueOf(controller.movingObjects.size()), 1, controller.movingObjects.size());

		assertTrue("person3 should now be in the first spot of movingObjects.", controller.movingObjects.get(0).person.getName().equals(person3.getName()));
		assertEquals("person3 should now have a transportation state of DESTINATION", TransportationController.TransportationState.DESTINATION, controller.movingObjects.get(0).transportationState);
		
		assertTrue("Scheduler should be trying to delete agent", controller.pickAndExecuteAnAction());

		assertTrue("Controller should have logged a \"Deleting mover\", but didn't. His log reads instead: " 
				+ controller.log.getLastLoggedEvent().toString(), controller.log.containsString("Deleting mover"));

		assertEquals("movingObjects size should be 0. Instead it is " + String.valueOf(controller.movingObjects.size()), 0, controller.movingObjects.size());

		assertFalse("Scheduler should have nothing to do", controller.pickAndExecuteAnAction());
	}
}
