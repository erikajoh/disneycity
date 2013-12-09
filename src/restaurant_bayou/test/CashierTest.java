package restaurant_bayou.test;

import restaurant_bayou.CashierAgent;
import restaurant_bayou.HostAgent;
import restaurant_bayou.HostAgent.Menu;
import restaurant_bayou.gui.RestaurantBayou;
import restaurant_bayou.test.mock.MockCustomer;
import restaurant_bayou.test.mock.MockMarket;
import restaurant_bayou.test.mock.MockWaiter;
import junit.framework.*;
import simcity.RestMenu;

/**
 * 
 * This class is a JUnit test class to unit test the CashierAgent's basic interaction
 * with waiters, customers, and the host.
 * It is provided as an example to students in CS201 for their unit testing lab.
 *
 * @author Monroe Ekilah
 */
public class CashierTest extends TestCase
{
	//these are instantiated for each test separately via the setUp() method.
	RestaurantBayou restaurant;
	CashierAgent cashier;
	MockWaiter waiter;
	MockCustomer customer;
	MockMarket market;
	HostAgent host = new HostAgent("host", restaurant);
	RestMenu m = host.menu;
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();		
		cashier = new CashierAgent("cashier", m, 100);		
		customer = new MockCustomer("mockcustomer");		
		waiter = new MockWaiter("mockwaiter");
		market = new MockMarket("mockmarket");
	}	
	
	public void testOneMarketScenario() // cashier pays for order fulfilled by one market
	{
		//setUp() runs first before this test!
		customer.cashier = cashier;//You can do almost anything in a unit test.				
		//step 1 of the test
		cashier.msgHereIsBill(market, 7.5);//send the message from a market
		//check postconditions for step 1 and preconditions for step 2
		assertEquals("MockMarket should have an empty event log before the Cashier's scheduler is called. Instead, the MockMarket's event log reads: "
						+ market.log.toString(), 0, market.log.size());
		assertEquals("Cashier should have 1 bill in it. It doesn't.", cashier.marketBills.size(), 1);
		while(cashier.pickAndExecuteAnAction());
		assertFalse("Cashier's scheduler should have returned true (checking to make sure the cashier sends the payment to the market).", cashier.pickAndExecuteAnAction());	
		assertEquals(
				"MockMarket should have have a log size of 1."
						+ market.log.toString(), 1, market.log.size());
		//step 2 of the test
		cashier.msgHereIsChange(8.5);
		//check postconditions for step 2 / preconditions for step 3
		assertTrue("Cashier should have logged \"Received msgHereIsChange\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgHereIsChange"));
		//step 3			
		assertFalse("Cashier's scheduler should have returned false (no actions left to do), but didn't.", 
				cashier.pickAndExecuteAnAction());	
	}
	
	public void testTwoMarketScenario() // cashier pays for order fulfilled by two markets
	{
		//setUp() runs first before this test!
		customer.cashier = cashier;//You can do almost anything in a unit test.				
		//step 1 of the test
		cashier.msgHereIsBill(market, 7.5);//send the message from a market
		//check postconditions for step 1 and preconditions for step 2
		assertEquals("MockMarket should have an empty event log before the Cashier's scheduler is called. Instead, the MockMarket's event log reads: "
						+ market.log.toString(), 0, market.log.size());
		assertEquals("Cashier should have 1 bill in it. It doesn't.", cashier.marketBills.size(), 1);
		while(cashier.pickAndExecuteAnAction());
		assertFalse("Cashier's scheduler should have returned true (checking to make sure the cashier sends the payment to the market).", cashier.pickAndExecuteAnAction());
		assertEquals(
				"MockMarket should have have a log size of 1."
						+ market.log.toString(), 1, market.log.size());
		//step 2 of the test
		cashier.msgHereIsChange(8.5);
		//check postconditions for step 2 / preconditions for step 3
		assertTrue("Cashier should have logged \"Received msgHereIsChange\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgHereIsChange"));
		//step 3	
		assertFalse("Cashier's scheduler should have returned false (no actions left to do), but didn't.", 
				cashier.pickAndExecuteAnAction());
	}
	
	public void testThreeMarketScenario() // cashier get cutoff by market because unable to pay
	{
		//setUp() runs first before this test!
		customer.cashier = cashier;//You can do almost anything in a unit test.						
		//step 1 of the test
		cashier.msgHereIsBill(market, 7.5);//send the message from a market
		//check postconditions for step 1 and preconditions for step 2
		assertEquals("MockMarket should have an empty event log before the Cashier's scheduler is called. Instead, the MockMarket's event log reads: "
						+ market.log.toString(), 0, market.log.size());		
		assertEquals("Cashier should have 1 bill in it. It doesn't.", cashier.marketBills.size(), 1);		
		while(cashier.pickAndExecuteAnAction());
		assertFalse("Cashier's scheduler should have returned true (checking to make sure the cashier sends the payment to the market).", cashier.pickAndExecuteAnAction());				
		assertEquals(
				"MockMarket should have have a log size of 1."
						+ market.log.toString(), 1, market.log.size());		
		//step 2 of the test
		cashier.msgYouAreCutoff(market);		
		//check postconditions for step 2 / preconditions for step 3		
		assertTrue("Cashier should have logged \"Received msgYouAreCutoff\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgYouAreCutoff"));
		//step 3
		assertEquals("Cashier should have 1 cutoff market in it. It doesn't.", cashier.cutoffMarkets.size(), 1);		
	}
	
	public void testOneNormalCustomerScenario() // customer pays exactly the right amount of money
	{
		//setUp() runs first before this test!		
		customer.cashier = cashier;//You can do almost anything in a unit test.					
		//check preconditions
		assertEquals("Cashier should have 0 bills in it. It doesn't.",cashier.checks.size(), 0);		
		assertEquals("CashierAgent should have an empty event log before the Cashier's HereIsBill is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());		
		//step 1 of the test
		cashier.msgGiveMeCheck(waiter, customer, "pasta", 2);//send the message from a waiter
		//check postconditions for step 1 and preconditions for step 2
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());		
		assertEquals("Cashier should have 1 bill in it. It doesn't.", cashier.checks.size(), 1);		
		while(cashier.pickAndExecuteAnAction());
		assertFalse("Cashier's scheduler should have returned true (checking to make sure the cashier sends the check back to the waiter).", cashier.pickAndExecuteAnAction());		
		assertEquals(
				"MockWaiter should have have a log size of 1."
						+ waiter.log.toString(), 1, waiter.log.size());
		//step 2 of the test
		cashier.msgHereIsMoney(customer, 8.50);		
		//check postconditions for step 2 / preconditions for step 3		
		assertTrue("Cashier should have logged \"Received msgHereIsMoney\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgHereIsMoney"));
		assertTrue("CashierBill should contain a bill of price = $8.50. It contains something else instead: $" 
				+ cashier.checks.get(0).getCost(), cashier.checks.get(0).getCost() == 8.50);		
		assertTrue("CashierBill should contain a bill with the right customer in it. It doesn't.", 
					cashier.checks.get(0).getCust() == customer);
		//step 3
		assertTrue("Cashier should have logged \"Received msgHereIsMoney\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgHereIsMoney"));				
		assertTrue("CashierBill should contain changeDue == 0.0. It contains something else instead: $" 
				+ cashier.checks.get(0).getChange(), cashier.checks.get(0).getChange() == 0);
		//step 4
		assertTrue("Cashier's scheduler should have returned true (needs to react to customer's ReadyToPay), but didn't.", 
					cashier.pickAndExecuteAnAction());		
		//check postconditions for step 4
		assertTrue("MockCustomer should have logged \"Received msgHereIsChange\" with the correct change, but his last event logged reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received HereIsYourChange from cashier. Change = 0.0"));		
		assertFalse("Cashier's scheduler should have returned false (no actions left to do), but didn't.", 
				cashier.pickAndExecuteAnAction());	
	}
	
	public void testTwoCustomerScenario() // customer eats food that he can't pay for
	{
		//setUp() runs first before this test!		
		customer.cashier = cashier;//You can do almost anything in a unit test.					
		//check preconditions
		assertEquals("Cashier should have 0 bills in it. It doesn't.",cashier.checks.size(), 0);		
		assertEquals("CashierAgent should have an empty event log before the Cashier's HereIsBill is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());		
		//step 1 of the test
		cashier.msgGiveMeCheck(waiter, customer, "pasta", 2);//send the message from a waiter
		//check postconditions for step 1 and preconditions for step 2
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());		
		assertEquals("Cashier should have 1 bill in it. It doesn't.", cashier.checks.size(), 1);		
		while(cashier.pickAndExecuteAnAction());
		assertFalse("Cashier's scheduler should have returned true (checking to make sure the cashier sends the check back to the waiter).", cashier.pickAndExecuteAnAction());		
		assertEquals(
				"MockWaiter should have have a log size of 1."
						+ waiter.log.toString(), 1, waiter.log.size());		
		//step 2 of the test
		cashier.msgHereIsMoney(customer, 0);		
		//check postconditions for step 2 / preconditions for step 3		
		assertTrue("Cashier should have logged \"Received msgHereIsMoney\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgHereIsMoney"));
		assertTrue("CashierBill should contain a bill of price = $8.50. It contains something else instead: $" 
				+ cashier.checks.get(0).getCost(), cashier.checks.get(0).getCost() == 8.50);		
		assertTrue("CashierBill should contain a bill with the right customer in it. It doesn't.", 
					cashier.checks.get(0).getCust() == customer);		
		//step 3
		assertTrue("Cashier should have logged \"Received msgHereIsMoney\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgHereIsMoney"));
		assertTrue("CashierBill should contain changeDue == 0.0. It contains something else instead: $" 
				+ cashier.checks.get(0).getChange(), cashier.checks.get(0).getChange() == 0);
		//step 4
		assertTrue("MockCustomer should have logged \"Received msgDoDishesAsPunishment\", but his last event logged reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received msgDoDishesAsPunishment"));		
		assertFalse("Cashier's scheduler should have returned false (no actions left to do), but didn't.", 
				cashier.pickAndExecuteAnAction());	
	}
	
	public void testThreeCustomerScenario() // customer pays more money than the food costs
	{
		//setUp() runs first before this test!
		customer.cashier = cashier;//You can do almost anything in a unit test.			
		//check preconditions
		assertEquals("Cashier should have 0 bills in it. It doesn't.",cashier.checks.size(), 0);		
		assertEquals("CashierAgent should have an empty event log before the Cashier's HereIsBill is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());
		//step 1 of the test
		cashier.msgGiveMeCheck(waiter, customer, "pasta", 2);//send the message from a waiter
		//check postconditions for step 1 and preconditions for step 2
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		assertEquals("Cashier should have 1 bill in it. It doesn't.", cashier.checks.size(), 1);
		while(cashier.pickAndExecuteAnAction());
		assertFalse("Cashier's scheduler should have returned true (checking to make sure the cashier sends the check back to the waiter).", cashier.pickAndExecuteAnAction());
		assertEquals(
				"MockWaiter should have have a log size of 1."
						+ waiter.log.toString(), 1, waiter.log.size());
		//step 2 of the test
		cashier.msgHereIsMoney(customer, 9.00);
		//check postconditions for step 2 / preconditions for step 3
		assertTrue("Cashier should have logged \"Received msgHereIsMoney\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgHereIsMoney"));
		assertTrue("CashierBill should contain a bill of price = $8.50. It contains something else instead: $" 
				+ cashier.checks.get(0).getCost(), cashier.checks.get(0).getCost() == 8.50);
		assertTrue("CashierBill should contain a bill with the right customer in it. It doesn't.", 
					cashier.checks.get(0).getCust() == customer);
		//step 3
		assertTrue("Cashier should have logged \"Received msgHereIsMoney\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgHereIsMoney"));
		assertTrue("CashierBill should contain changeDue == 0.5. It contains something else instead: $" 
				+ cashier.checks.get(0).getChange(), cashier.checks.get(0).getChange() == 0.5);
		//step 4
		assertTrue("Cashier's scheduler should have returned true (needs to react to customer's ReadyToPay), but didn't.", 
					cashier.pickAndExecuteAnAction());	
		//check postconditions for step 4
		assertTrue("MockCustomer should have logged \"Received msgHereIsChange\" with the correct change, but his last event logged reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received HereIsYourChange from cashier. Change = 0.5"));
		assertFalse("Cashier's scheduler should have returned false (no actions left to do), but didn't.", 
				cashier.pickAndExecuteAnAction());
	}
}