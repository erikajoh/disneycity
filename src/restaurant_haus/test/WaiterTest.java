package restaurant_haus.test;

import restaurant_haus.CookAgent;
import restaurant_haus.Order;
import restaurant_haus.OrderStand;
import restaurant_haus.PCWaiterAgent;
import restaurant_haus.WaiterAgent;
import restaurant_haus.gui.CookGui;
import restaurant_haus.gui.WaiterGui;
import restaurant_haus.test.mock.MockCookGui;
import restaurant_haus.test.mock.MockWaiterGui;
import junit.framework.TestCase;

public class WaiterTest extends TestCase{
	WaiterAgent normWaiter;
	PCWaiterAgent PCWaiter;
	PCWaiterAgent PCWaiter2;
	
	OrderStand orderStand;
	CookAgent cook;
	
	Order o1, o2, o3, o4;
	
	public void setUp() throws Exception{
		super.setUp();
		orderStand = new OrderStand();
		normWaiter = new WaiterAgent("normWaiter");
		normWaiter.setGui(new MockWaiterGui(normWaiter, null));
		PCWaiter = new PCWaiterAgent("PCWaiter", orderStand);
		PCWaiter.setGui(new MockWaiterGui(PCWaiter, null));
		PCWaiter2 = new PCWaiterAgent("PCWaiter2", orderStand);
		PCWaiter2.setGui(new MockWaiterGui(PCWaiter2, null));
		cook = new CookAgent("cook", null, orderStand);
		cook.setGui(new MockCookGui(cook, null));
		
		normWaiter.setCook(cook);
		PCWaiter.setCook(cook);
		PCWaiter2.setCook(cook);
		
		o1 = new Order(PCWaiter, "Pastrami Cheeseburger", 1);
		o2 = new Order(PCWaiter2, "Chicken Sausage Pretzel Roll", 2);
		o3 = new Order(normWaiter, "BLT Flatbread", 3);	
	}
	
	//Basic PC waiter test to make sure the cook the order after being notified
	public void testPCWaiterPutsOrder() {
		assertEquals("orderStand should be empty. Instead it has " + String.valueOf(orderStand.getSize()) + " entries.", 0, orderStand.getSize());
		assertEquals("Cook should not have any orders. Instead it has " + String.valueOf(cook.TESTgetOrderSize(this)) + " entries.", 0, cook.TESTgetOrderSize(this));
		assertFalse("Cook should have nothing to do.", cook.pickAndExecuteAnAction());
		assertFalse("PCWaiter should have nothing to do.", PCWaiter.pickAndExecuteAnAction());
		
		assertTrue("The test set the waiter to the about the order state", PCWaiter.TESTforceOrder(this, o1));
		
		assertEquals("orderStand should be empty. Instead it has " + String.valueOf(orderStand.getSize()) + " entries.", 0, orderStand.getSize());
		assertEquals("Cook should not have any orders. Instead it has " + String.valueOf(cook.TESTgetOrderSize(this)) + " entries.", 0, cook.TESTgetOrderSize(this));
		assertFalse("Cook should have nothing to do.", cook.pickAndExecuteAnAction());
		
		assertTrue("The PCWaiter finds something to add to the stand", PCWaiter.pickAndExecuteAnAction());
		
		assertTrue("PCWaiter should have logged a \"PC order added\", but didn't. Its log reads instead: " 
				+ PCWaiter.log.getLastLoggedEvent().toString(), PCWaiter.log.containsString("PC order added"));
		
		assertFalse("PCWaiter should have nothing to do.", PCWaiter.pickAndExecuteAnAction());
		assertEquals("orderStand should have 1 entry. Instead it has " + String.valueOf(orderStand.getSize()) + " entries.", 1, orderStand.getSize());
		
		assertTrue("Cook should check the stand.", cook.pickAndExecuteAnAction());
		assertTrue("Cook should have logged a \"Checked stand\", but didn't. Its log reads instead: " 
				+ cook.log.getLastLoggedEvent().toString(), cook.log.containsString("Checked stand"));
		
		assertEquals("orderStand should have 0 entries. Instead it has " + String.valueOf(orderStand.getSize()) + " entries.", 0, orderStand.getSize());
		assertEquals("Cook should have 1 orders. Instead it has " + String.valueOf(cook.TESTgetOrderSize(this)) + " orders.", 1, cook.TESTgetOrderSize(this));
	}
	
	//Make sure the cook grabs all orders from the stand
	public void testPCTwoOrdersCookAfter() {
		assertEquals("orderStand should be empty. Instead it has " + String.valueOf(orderStand.getSize()) + " entries.", 0, orderStand.getSize());
		assertEquals("Cook should not have any orders. Instead it has " + String.valueOf(cook.TESTgetOrderSize(this)) + " entries.", 0, cook.TESTgetOrderSize(this));
		assertFalse("Cook should have nothing to do.", cook.pickAndExecuteAnAction());
		assertFalse("PCWaiter should have nothing to do.", PCWaiter.pickAndExecuteAnAction());
		assertFalse("PCWaiter2 should have nothing to do.", PCWaiter2.pickAndExecuteAnAction());
		
		assertTrue("The test set the waiter to the about the order state", PCWaiter.TESTforceOrder(this, o1));
		
		assertEquals("orderStand should be empty. Instead it has " + String.valueOf(orderStand.getSize()) + " entries.", 0, orderStand.getSize());
		assertEquals("Cook should not have any orders. Instead it has " + String.valueOf(cook.TESTgetOrderSize(this)) + " entries.", 0, cook.TESTgetOrderSize(this));
		assertFalse("Cook should have nothing to do.", cook.pickAndExecuteAnAction());
		
		assertTrue("The PCWaiter finds something to add to the stand", PCWaiter.pickAndExecuteAnAction());
		
		assertTrue("PCWaiter should have logged a \"PC order added\", but didn't. Its log reads instead: " 
				+ PCWaiter.log.getLastLoggedEvent().toString(), PCWaiter.log.containsString("PC order added"));
		assertFalse("PCWaiter2 should have nothing to do.", PCWaiter2.pickAndExecuteAnAction());
		
		assertFalse("PCWaiter should have nothing to do.", PCWaiter.pickAndExecuteAnAction());
		assertEquals("orderStand should have 1 entry. Instead it has " + String.valueOf(orderStand.getSize()) + " entries.", 1, orderStand.getSize());
		
		assertTrue("The test set the waiter to the about the order state", PCWaiter2.TESTforceOrder(this, o2));
		assertTrue("The PCWaiter2 finds something to add to the stand", PCWaiter2.pickAndExecuteAnAction());
		assertTrue("PCWaiter2 should have logged a \"PC order added\", but didn't. Its log reads instead: " 
				+ PCWaiter2.log.getLastLoggedEvent().toString(), PCWaiter2.log.containsString("PC order added"));
		
		assertFalse("PCWaiter should have nothing to do.", PCWaiter.pickAndExecuteAnAction());
		assertFalse("PCWaiter2 should have nothing to do.", PCWaiter2.pickAndExecuteAnAction());
		
		assertEquals("orderStand should have 2 entries. Instead it has " + String.valueOf(orderStand.getSize()) + " entries.", 2, orderStand.getSize());
		
		assertTrue("Cook should check the stand.", cook.pickAndExecuteAnAction());
		assertTrue("Cook should have logged a \"Checked stand\", but didn't. Its log reads instead: " 
				+ cook.log.getLastLoggedEvent().toString(), cook.log.containsString("Checked stand"));
		
		assertEquals("orderStand should have 0 entries. Instead it has " + String.valueOf(orderStand.getSize()) + " entries.", 0, orderStand.getSize());
		assertEquals("Cook should have 2 orders. Instead it has " + String.valueOf(cook.TESTgetOrderSize(this)) + " orders.", 2, cook.TESTgetOrderSize(this));
		
		assertTrue("The cook's first order should be for a Pastrami Cheeseburger. Instead it is " + cook.TESTgetOrder(this, 0).getChoice(), cook.TESTgetOrder(this, 0).getChoice().equals("Pastrami Cheeseburger"));
		assertTrue("The cook's second order should be for a Chicken Sausage Pretzel Roll. Instead it is " + cook.TESTgetOrder(this, 0).getChoice(), cook.TESTgetOrder(this, 1).getChoice().equals("Chicken Sausage Pretzel Roll"));
	}
	
	//Make sure nothing gets clobbered by the cook
	public void testPCTwoOrdersCookChecksInBetween() {
		assertEquals("orderStand should be empty. Instead it has " + String.valueOf(orderStand.getSize()) + " entries.", 0, orderStand.getSize());
		assertEquals("Cook should not have any orders. Instead it has " + String.valueOf(cook.TESTgetOrderSize(this)) + " entries.", 0, cook.TESTgetOrderSize(this));
		assertFalse("Cook should have nothing to do.", cook.pickAndExecuteAnAction());
		assertFalse("PCWaiter should have nothing to do.", PCWaiter.pickAndExecuteAnAction());
		assertFalse("PCWaiter2 should have nothing to do.", PCWaiter2.pickAndExecuteAnAction());
		
		assertTrue("The test set the waiter to the about the order state", PCWaiter.TESTforceOrder(this, o1));
		
		assertEquals("orderStand should be empty. Instead it has " + String.valueOf(orderStand.getSize()) + " entries.", 0, orderStand.getSize());
		assertEquals("Cook should not have any orders. Instead it has " + String.valueOf(cook.TESTgetOrderSize(this)) + " entries.", 0, cook.TESTgetOrderSize(this));
		assertFalse("Cook should have nothing to do.", cook.pickAndExecuteAnAction());
		
		assertTrue("The PCWaiter finds something to add to the stand", PCWaiter.pickAndExecuteAnAction());
		
		assertTrue("PCWaiter should have logged a \"PC order added\", but didn't. Its log reads instead: " 
				+ PCWaiter.log.getLastLoggedEvent().toString(), PCWaiter.log.containsString("PC order added"));
		assertFalse("PCWaiter2 should have nothing to do.", PCWaiter2.pickAndExecuteAnAction());
		
		assertFalse("PCWaiter should have nothing to do.", PCWaiter.pickAndExecuteAnAction());
		assertEquals("orderStand should have 1 entry. Instead it has " + String.valueOf(orderStand.getSize()) + " entries.", 1, orderStand.getSize());
		
		assertTrue("Cook should check the stand.", cook.pickAndExecuteAnAction());
		assertTrue("Cook should have logged a \"Checked stand\", but didn't. Its log reads instead: " 
				+ cook.log.getLastLoggedEvent().toString(), cook.log.containsString("Checked stand"));
		
		assertEquals("orderStand should have 0 entries. Instead it has " + String.valueOf(orderStand.getSize()) + " entries.", 0, orderStand.getSize());
		assertEquals("Cook should have 1 order. Instead it has " + String.valueOf(cook.TESTgetOrderSize(this)) + " orders.", 1, cook.TESTgetOrderSize(this));
		assertTrue("The cook's first order should be for a Pastrami Cheeseburger. Instead it is " + cook.TESTgetOrder(this, 0).getChoice(), cook.TESTgetOrder(this, 0).getChoice().equals("Pastrami Cheeseburger"));
		
		assertTrue("The test set the waiter to the about the order state", PCWaiter2.TESTforceOrder(this, o2));
		assertTrue("The PCWaiter2 finds something to add to the stand", PCWaiter2.pickAndExecuteAnAction());
		assertTrue("PCWaiter2 should have logged a \"PC order added\", but didn't. Its log reads instead: " 
				+ PCWaiter2.log.getLastLoggedEvent().toString(), PCWaiter2.log.containsString("PC order added"));
		
		assertFalse("PCWaiter should have nothing to do.", PCWaiter.pickAndExecuteAnAction());
		assertFalse("PCWaiter2 should have nothing to do.", PCWaiter2.pickAndExecuteAnAction());
		
		assertEquals("orderStand should have 1 entry. Instead it has " + String.valueOf(orderStand.getSize()) + " entries.", 1, orderStand.getSize());
		assertEquals("Cook should have 1 order. Instead it has " + String.valueOf(cook.TESTgetOrderSize(this)) + " orders.", 1, cook.TESTgetOrderSize(this));
		
		assertTrue("Cook should check the stand.", cook.pickAndExecuteAnAction());
		assertTrue("Cook should have logged a \"Checked stand\", but didn't. Its log reads instead: " 
				+ cook.log.getLastLoggedEvent().toString(), cook.log.containsString("Checked stand"));
		
		assertEquals("orderStand should have 0 entries. Instead it has " + String.valueOf(orderStand.getSize()) + " entries.", 0, orderStand.getSize());
		assertEquals("Cook should have 2 orders. Instead it has " + String.valueOf(cook.TESTgetOrderSize(this)) + " orders.", 2, cook.TESTgetOrderSize(this));
		
		assertTrue("The cook's first order should be for a Pastrami Cheeseburger. Instead it is " + cook.TESTgetOrder(this, 0).getChoice(), cook.TESTgetOrder(this, 0).getChoice().equals("Pastrami Cheeseburger"));
		assertTrue("The cook's second order should be for a Chicken Sausage Pretzel Roll. Instead it is " + cook.TESTgetOrder(this, 0).getChoice(), cook.TESTgetOrder(this, 1).getChoice().equals("Chicken Sausage Pretzel Roll"));
	}
	
	//Test working with normal waiters
	public void testPCOrderThenNormOrderThenCookCheckStand() {
		assertEquals("orderStand should be empty. Instead it has " + String.valueOf(orderStand.getSize()) + " entries.", 0, orderStand.getSize());
		assertEquals("Cook should not have any orders. Instead it has " + String.valueOf(cook.TESTgetOrderSize(this)) + " entries.", 0, cook.TESTgetOrderSize(this));
		assertFalse("Cook should have nothing to do.", cook.pickAndExecuteAnAction());
		assertFalse("PCWaiter should have nothing to do.", PCWaiter.pickAndExecuteAnAction());
		assertFalse("normWaiter should have nothing to do.", normWaiter.pickAndExecuteAnAction());
		
		assertTrue("The test set the waiter to the about the order state", PCWaiter.TESTforceOrder(this, o1));
		
		assertEquals("orderStand should be empty. Instead it has " + String.valueOf(orderStand.getSize()) + " entries.", 0, orderStand.getSize());
		assertEquals("Cook should not have any orders. Instead it has " + String.valueOf(cook.TESTgetOrderSize(this)) + " entries.", 0, cook.TESTgetOrderSize(this));
		assertFalse("Cook should have nothing to do.", cook.pickAndExecuteAnAction());
		assertTrue("The PCWaiter finds something to add to the stand", PCWaiter.pickAndExecuteAnAction());
		
		assertTrue("PCWaiter should have logged a \"PC order added\", but didn't. Its log reads instead: " 
				+ PCWaiter.log.getLastLoggedEvent().toString(), PCWaiter.log.containsString("PC order added"));
		assertFalse("normWaiter should have nothing to do.", normWaiter.pickAndExecuteAnAction());
		assertFalse("PCWaiter should have nothing to do.", PCWaiter.pickAndExecuteAnAction());
		
		assertEquals("Cook should not have any orders. Instead it has " + String.valueOf(cook.TESTgetOrderSize(this)) + " entries.", 0, cook.TESTgetOrderSize(this));
		
		assertTrue("The test set the waiter to the about to order state", normWaiter.TESTforceOrder(this, o3));
		
		assertTrue("normWaiter should place an order.", normWaiter.pickAndExecuteAnAction());
		assertFalse("PCWaiter should have nothing to do.", PCWaiter.pickAndExecuteAnAction());
		
		assertEquals("Cook should have 1 order. Instead it has " + String.valueOf(cook.TESTgetOrderSize(this)) + " entries.", 1, cook.TESTgetOrderSize(this));
		assertTrue("The cook's first order should be for a BLT Flatbread. Instead it is " + cook.TESTgetOrder(this, 0).getChoice(), cook.TESTgetOrder(this, 0).getChoice().equals("BLT Flatbread"));
		
		assertFalse("PCWaiter should have nothing to do.", PCWaiter.pickAndExecuteAnAction());
		assertFalse("normWaiter should have nothing to do.", normWaiter.pickAndExecuteAnAction());
		
		assertTrue("Cook should check the stand.", cook.pickAndExecuteAnAction());
		assertTrue("Cook should have logged a \"Checked stand\", but didn't. Its log reads instead: " 
				+ cook.log.getLastLoggedEvent().toString(), cook.log.containsString("Checked stand"));
		
		assertEquals("Cook should have 2 orders. Instead it has " + String.valueOf(cook.TESTgetOrderSize(this)) + " entries.", 2, cook.TESTgetOrderSize(this));
		assertTrue("The cook's first order should be for a BLT Flatbread. Instead it is " + cook.TESTgetOrder(this, 0).getChoice(), cook.TESTgetOrder(this, 0).getChoice().equals("BLT Flatbread"));
		assertTrue("The cook's second order should be for a Pastrami Cheeseburger. Instead it is " + cook.TESTgetOrder(this, 1).getChoice(), cook.TESTgetOrder(this, 1).getChoice().equals("Pastrami Cheeseburger"));
	}
}
