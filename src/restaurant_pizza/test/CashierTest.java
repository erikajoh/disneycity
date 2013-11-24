package restaurant_pizza.test;

import restaurant_pizza.CashierAgent;
import restaurant_pizza.Check;
import restaurant_pizza.FoodBill;
import restaurant_pizza.CashierAgent.CheckState;
import restaurant_pizza.test.mock.*;
import junit.framework.*;
import simcity.RestMenu;

/**
 * Modified unit test code from CSCI 201 lab.
 */
public class CashierTest extends TestCase
{
	// instantiated in the setUp() method
	CashierAgent cashier;
	MockWaiter waiter;
	MockCustomer customer;
	MockMarket marketOne;
	MockMarket marketTwo;
	RestMenu menu = new RestMenu();

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
		cashier = new CashierAgent("cashier");		
		customer = new MockCustomer("mockcustomer");		
		waiter = new MockWaiter("mockwaiter");
		marketOne = new MockMarket("mockmarketOne");
		marketTwo = new MockMarket("mockmarketTwo");
	}	
	
	// TEST #1
	// Rubric: "One order, fulfilled by the market, bill paid in full."
	// "The simple normative scenario."
	public void testOneOrderOneMarketOneBillPaidInFull() {
		
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
	}
	
	// TEST #2
	// "One order, fulfilled by TWO markets, 2 bills paid in full."
	public void testOneOrderTwoMarketsTwoBillsPaidInFull() {

		// step 1 pre-conditions
		cashier.setTotalMoney(15.00);
		
		assertEquals("Cashier should have empty event log before msgCustomerNeedsCheck is called. Cashier's event log: "
				+ cashier.log.toString(), 0, cashier.log.size());
		assertEquals("Cashier should have 15 dollars to start", 15.00, cashier.getTotalMoney());
		
		// step 1: market gives cashier the bill
		FoodBill fb = new FoodBill("Salad", marketOne, 10);
		cashier.msgHereIsBill(fb);
		
		// step 1 post-conditions and step 2 pre-conditions
		//"msgHereIsBill from " + fb.market.getName() + " for order " + fb.order + " and amount " + fb.amountDue));
		assertTrue("Cashier should have logged \"msgHereIsBill from mockmarketOne for order Salad and amount 10.0\". Log instead reads: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("msgHereIsBill from mockmarketOne for order Salad and amount 10.0"));
		
		// step 2: cashier pays back the bill in full
		assertTrue("Cashier's scheduler should have returned true, but didn't", cashier.pickAndExecuteAnAction());
		
		// step 2 post-conditions and step 3 pre-conditions
		assertTrue("MockMarket should have logged \"msgHereIsBill from cashier: valid payment\". Log instead reads: " 
				+ marketOne.log.getLastLoggedEvent().toString(), marketOne.log.containsString("msgHereIsBill from cashier: valid payment"));
		assertEquals("Cashier should have five dollars", 5.0, cashier.getTotalMoney());
		
		// step 3: market #2 gives cashier the bill
		FoodBill fb2 = new FoodBill("Pizza", marketTwo, 5);
		cashier.msgHereIsBill(fb2);
		
		// step 3 post-conditions and step 4 pre-conditions
		assertTrue("Cashier should have logged \"msgHereIsBill from mockmarketTwo for order Pizza and amount 5.0\". Log instead reads: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("msgHereIsBill from mockmarketTwo for order Pizza and amount 5.0"));
		assertEquals("Cashier should have two log messages at the end.", 2, cashier.log.size());
		
		// step 4: cashier pays back second bill in full
		assertTrue("Cashier's scheduler should have returned true, but didn't", cashier.pickAndExecuteAnAction());
		
		// step 4 post-conditions
		assertTrue("MockMarket should have logged \"msgHereIsBill from cashier\". Log instead reads: " 
				+ marketOne.log.getLastLoggedEvent().toString(), marketOne.log.containsString("msgHereIsBill from cashier: valid payment"));
		assertEquals("Cashier should have no money at end", 0.0, cashier.getTotalMoney());
	}
	
	// TEST #3
	// Waiter requests check, then customer pays the check in full.
	// No market is involved.
	public void testOneCustomerPaidInFullScenario() {
		
		customer.cashier = cashier;			
		
		//preconditions for step 1
		assertEquals("Cashier should have 0 Checks in it. It doesn't.",cashier.checks.size(), 0);		
		assertEquals("CashierAgent should have empty event log before msgCustomerNeedsCheck is called. Cashier's event log: "
						+ cashier.log.toString(), 0, cashier.log.size());
		
		// step 1: waiter requests check and is being sent to customer
		cashier.msgCustomerNeedsCheck(waiter, "Steak", customer, menu);//message from waiter

		//postconditions for step 1, preconditions for step 2
		assertEquals("MockWaiter should have empty event log before Cashier's scheduler is called. MockWaiter's event log: "
						+ waiter.log.toString(), 0, waiter.log.size());
		assertEquals("Cashier should have 1 Check in it.", cashier.checks.size(), 1);
		assertTrue("Cashier's scheduler should have returned true (creates check).", cashier.pickAndExecuteAnAction());
		assertEquals("MockWaiter should have one event log after Cashier's scheduler is called for first time. MockWaiter's event log: "
						+ waiter.log.toString(), 1, waiter.log.size());
		
		assertTrue("Check should have state == SentToWaiter. Instead, state == " + cashier.checks.get(0).state,
				cashier.checks.get(0).state == CheckState.InTransit);
		
		//step 2: customer pays check
		Check aCheck = cashier.checks.get(0);
		cashier.msgPayingMyCheck(customer, aCheck, 20.00); //message from customer
		
		//check postconditions for step 2 / preconditions for step 3
		assertTrue("Check should have state == SentToWaiter. Instead, state == " + cashier.checks.get(0).state,
				cashier.checks.get(0).state == CheckState.PaidByCustomer);
		assertTrue("Cashier should have logged \"Calling msgPayingMyCheck\" but didn't. His log reads: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Calling msgPayingMyCheck"));
		assertTrue("Check should contain price 20.00. It contains this instead: " 
				+ cashier.checks.get(0).amountDue, cashier.checks.get(0).amountDue == 20.00);
		assertTrue("CashierCheck should contain a Check with the right customer in it. It doesn't.", 
					cashier.checks.get(0).customer == customer);
		
		//step 3
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		assertFalse("Cashier's scheduler should have returned false, but didn't.", cashier.pickAndExecuteAnAction());
		
		//check postconditions for step 3
		assertTrue("Check should have no more checks. Instead it has " + cashier.checks.size(),
				cashier.checks.size() == 0);
		assertTrue("CashierCheck should have logged \"handleValidPayment called\". It doesn't.", 
				cashier.log.containsString("handleValidPayment"));
	}
	
	// TEST #4
	// Waiter requests check, customer does not pay check in full, and is told to make up for debt next time.
	// No market is involved.
	public void testOneCustomerNotPaidInFullScenario() {

		customer.cashier = cashier;			
		
		//preconditions for step 1
		assertEquals("Cashier should have 0 Checks in it. It doesn't.",cashier.checks.size(), 0);		
		assertEquals("CashierAgent should have empty event log before msgCustomerNeedsCheck is called. Cashier's event log: "
						+ cashier.log.toString(), 0, cashier.log.size());
		
		// step 1: waiter requests check and is being sent to customer
		cashier.msgCustomerNeedsCheck(waiter, "Steak", customer, menu);//message from waiter

		//postconditions for step 1, preconditions for step 2
		assertEquals("MockWaiter should have empty event log before Cashier's scheduler is called. MockWaiter's event log: "
						+ waiter.log.toString(), 0, waiter.log.size());
		assertEquals("Cashier should have 1 Check in it.", cashier.checks.size(), 1);
		assertTrue("Cashier's scheduler should have returned true (creates check).", cashier.pickAndExecuteAnAction());
		assertEquals("MockWaiter should have one event log after Cashier's scheduler is called for first time. MockWaiter's event log: "
						+ waiter.log.toString(), 1, waiter.log.size());
		
		assertTrue("Check should have state == SentToWaiter. Instead, state == " + cashier.checks.get(0).state,
				cashier.checks.get(0).state == CheckState.InTransit);
		
		//step 2: customer pays check
		Check aCheck = cashier.checks.get(0);
		cashier.msgPayingMyCheck(customer, aCheck, 0.00); //message from customer
		
		//check postconditions for step 2 / preconditions for step 3
		assertTrue("Check should have state == SentToWaiter. Instead, state == " + cashier.checks.get(0).state,
				cashier.checks.get(0).state == CheckState.PaidByCustomer);
		assertTrue("Cashier should have logged \"Calling msgPayingMyCheck\" but didn't. His log reads: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Calling msgPayingMyCheck"));
		assertTrue("Check should contain price 20.00. It contains this instead: " 
				+ cashier.checks.get(0).amountDue, cashier.checks.get(0).amountDue == 20.00);
		assertTrue("CashierCheck should contain a Check with the right customer in it. It doesn't.", 
					cashier.checks.get(0).customer == customer);
		
		//step 3
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		assertFalse("Cashier's scheduler should have returned false, but didn't.", cashier.pickAndExecuteAnAction());
		
		//check postconditions for step 3
		assertTrue("Check should have no more checks. Instead it has " + cashier.checks.size(),
				cashier.checks.size() == 0);
		assertTrue("CashierCheck should have logged \"handleInvalidPayment called\". It doesn't.", 
				cashier.log.containsString("handleInvalidPayment"));
		assertTrue("CashierCheck should have logged \"Customer has this much debt to repay: 20.0\". It doesn't.", 
				cashier.log.containsString("Customer has this much debt to repay: 20.0"));
		assertEquals("Cashier should have 3 log messages.", cashier.log.size(), 3);
	}
	
	// TEST #5
	// Combination of waiter getting check, customer paying check, and market sending bill.
	// Customer can pay check in full.
	public void testAllAgentsAtOnceCustomerPaidInFull() {
		
		customer.cashier = cashier;
		cashier.setTotalMoney(15.00);
		
		//preconditions for step 1
		assertEquals("Cashier should have 0 Checks in it. It doesn't.",cashier.checks.size(), 0);		
		assertEquals("CashierAgent should have empty event log before msgCustomerNeedsCheck is called. Cashier's event log: "
						+ cashier.log.toString(), 0, cashier.log.size());
		
		// step 1: waiter requests check and is being sent to customer
		cashier.msgCustomerNeedsCheck(waiter, "Steak", customer, menu);//message from waiter

		//postconditions for step 1, preconditions for step 2a
		assertEquals("MockWaiter should have empty event log before Cashier's scheduler is called. MockWaiter's event log: "
						+ waiter.log.toString(), 0, waiter.log.size());
		assertEquals("Cashier should have 1 Check in it.", cashier.checks.size(), 1);
		assertTrue("Cashier's scheduler should have returned true (creates check).", cashier.pickAndExecuteAnAction());
		assertEquals("MockWaiter should have one event log after Cashier's scheduler is called for first time. MockWaiter's event log: "
						+ waiter.log.toString(), 1, waiter.log.size());
		
		assertTrue("Check should have state == SentToWaiter. Instead, state == " + cashier.checks.get(0).state,
				cashier.checks.get(0).state == CheckState.InTransit);
		
		//step 2a: customer pays check
		Check aCheck = cashier.checks.get(0);
		cashier.msgPayingMyCheck(customer, aCheck, 20.00); //message from customer
		
		//check postconditions for step 2a / preconditions for step 2b
		assertTrue("Check should have state == SentToWaiter. Instead, state == " + cashier.checks.get(0).state,
				cashier.checks.get(0).state == CheckState.PaidByCustomer);
		assertTrue("Cashier should have logged \"Calling msgPayingMyCheck\" but didn't. His log reads: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Calling msgPayingMyCheck"));
		assertTrue("Check should contain price 20.00. It contains this instead: " 
				+ cashier.checks.get(0).amountDue, cashier.checks.get(0).amountDue == 20.00);
		assertTrue("CashierCheck should contain a Check with the right customer in it. It doesn't.", 
					cashier.checks.get(0).customer == customer);
		
		// step 2b pre-conditions
		assertEquals("Cashier should have 1 event log: "
				+ cashier.log.toString(), 1, cashier.log.size());
		assertEquals("Cashier should have 15 dollars to start", 15.00, cashier.getTotalMoney());
		
		// step 2b: market gives cashier the bill
		FoodBill fb = new FoodBill("Salad", marketOne, 10);
		cashier.msgHereIsBill(fb);
		
		// step 2b post-conditions and step 2c pre-conditions
		//"msgHereIsBill from " + fb.market.getName() + " for order " + fb.order + " and amount " + fb.amountDue));
		assertTrue("Cashier should have logged \"msgHereIsBill from mockmarketOne for order Salad and amount 10.0\". Log instead reads: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("msgHereIsBill from mockmarketOne for order Salad and amount 10.0"));
		
		// step 2c: cashier pays back the bill in full
		assertTrue("Cashier's scheduler should have returned true, but didn't", cashier.pickAndExecuteAnAction());
		
		// step 2 post-conditions / step 3 pre-conditions
		assertTrue("MockMarket should have logged \"msgHereIsBill from cashier\". Log instead reads: " 
				+ marketOne.log.getLastLoggedEvent().toString(), marketOne.log.containsString("msgHereIsBill from cashier: valid payment"));
		assertEquals("Cashier should have 5 dollars after paying bill", 5.0, cashier.getTotalMoney());
		
		//step 3
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		assertFalse("Cashier's scheduler should have returned false, but didn't.", cashier.pickAndExecuteAnAction());
		
		//check postconditions for step 3
		assertTrue("Check should have no more checks. Instead it has " + cashier.checks.size(),
				cashier.checks.size() == 0);
		assertTrue("CashierCheck should have logged \"handleValidPayment called\". It doesn't.", 
				cashier.log.containsString("handleValidPayment"));
		assertEquals("Cashier should have 3 log messages.", cashier.log.size(), 3);
	}
	
	// TEST #6
	// Combination of waiter getting check, customer paying check, and market sending bill.
	// Customer cannot pay check in full.
	public void testAllAgentsAtOnceCustomerNotPaidInFull() {

		customer.cashier = cashier;
		cashier.setTotalMoney(15.00);
		
		//preconditions for step 1
		assertEquals("Cashier should have 0 Checks in it. It doesn't.",cashier.checks.size(), 0);		
		assertEquals("CashierAgent should have empty event log before msgCustomerNeedsCheck is called. Cashier's event log: "
						+ cashier.log.toString(), 0, cashier.log.size());
		
		// step 1: waiter requests check and is being sent to customer
		cashier.msgCustomerNeedsCheck(waiter, "Steak", customer, menu);//message from waiter

		//postconditions for step 1, preconditions for step 2a
		assertEquals("MockWaiter should have empty event log before Cashier's scheduler is called. MockWaiter's event log: "
						+ waiter.log.toString(), 0, waiter.log.size());
		assertEquals("Cashier should have 1 Check in it.", cashier.checks.size(), 1);
		assertTrue("Cashier's scheduler should have returned true (creates check).", cashier.pickAndExecuteAnAction());
		assertEquals("MockWaiter should have one event log after Cashier's scheduler is called for first time. MockWaiter's event log: "
						+ waiter.log.toString(), 1, waiter.log.size());
		
		assertTrue("Check should have state == SentToWaiter. Instead, state == " + cashier.checks.get(0).state,
				cashier.checks.get(0).state == CheckState.InTransit);
		
		//step 2a: customer pays check
		Check aCheck = cashier.checks.get(0);
		cashier.msgPayingMyCheck(customer, aCheck, 0.00); //message from customer
		
		//check postconditions for step 2a / preconditions for step 2b
		assertTrue("Check should have state == SentToWaiter. Instead, state == " + cashier.checks.get(0).state,
				cashier.checks.get(0).state == CheckState.PaidByCustomer);
		assertTrue("Cashier should have logged \"Calling msgPayingMyCheck\" but didn't. His log reads: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Calling msgPayingMyCheck"));
		assertTrue("Check should contain price 20.00. It contains this instead: " 
				+ cashier.checks.get(0).amountDue, cashier.checks.get(0).amountDue == 20.00);
		assertTrue("CashierCheck should contain a Check with the right customer in it. It doesn't.", 
					cashier.checks.get(0).customer == customer);
		
		// step 2b pre-conditions
		assertEquals("Cashier should have 1 event log: "
				+ cashier.log.toString(), 1, cashier.log.size());
		assertEquals("Cashier should have 15 dollars to start", 15.00, cashier.getTotalMoney());
		
		// step 2b: market gives cashier the bill
		FoodBill fb = new FoodBill("Salad", marketOne, 10);
		cashier.msgHereIsBill(fb);
		
		// step 2b post-conditions and step 2c pre-conditions
		//"msgHereIsBill from " + fb.market.getName() + " for order " + fb.order + " and amount " + fb.amountDue));
		assertTrue("Cashier should have logged \"msgHereIsBill from mockmarketOne for order Salad and amount 10.0\". Log instead reads: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("msgHereIsBill from mockmarketOne for order Salad and amount 10.0"));
		
		// step 2c: cashier pays back the bill in full
		assertTrue("Cashier's scheduler should have returned true, but didn't", cashier.pickAndExecuteAnAction());
		
		// step 2 post-conditions / step 3 pre-conditions
		assertTrue("MockMarket should have logged \"msgHereIsBill from cashier\". Log instead reads: " 
				+ marketOne.log.getLastLoggedEvent().toString(), marketOne.log.containsString("msgHereIsBill from cashier: valid payment"));
		assertEquals("Cashier should have 5 dollars after paying bill", 5.0, cashier.getTotalMoney());
		
		//step 3
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		assertFalse("Cashier's scheduler should have returned false, but didn't.", cashier.pickAndExecuteAnAction());
		
		//check postconditions for step 3
		assertTrue("Check should have no more checks. Instead it has " + cashier.checks.size(),
				cashier.checks.size() == 0);
		assertTrue("CashierCheck should have logged \"handleInvalidPayment called\". It doesn't.", 
				cashier.log.containsString("handleInvalidPayment"));
		assertTrue("CashierCheck should have logged \"Customer has this much debt to repay: 20.0\". It doesn't.", 
				cashier.log.containsString("Customer has this much debt to repay: 20.0"));
		assertEquals("Cashier should have 4 log messages.", cashier.log.size(), 4);
	}
}
