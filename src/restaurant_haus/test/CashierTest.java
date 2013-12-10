package restaurant_haus.test;

import restaurant_haus.CashierAgent;
import restaurant_haus.Menu;
import restaurant_haus.CashierAgent.BillState;
import restaurant_haus.CashierAgent.Check;
import restaurant_haus.CashierAgent.CheckState;
import restaurant_haus.test.mock.MockCustomer;
import restaurant_haus.test.mock.MockMarket;
import restaurant_haus.test.mock.MockWaiter;
import junit.framework.*;

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
	CashierAgent cashier;
	MockWaiter waiter;
	MockCustomer customer;
	MockCustomer customer2;
	MockMarket market1;
	MockMarket market2;
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();		
		//cashier = new CashierAgent("cashier");		
		customer = new MockCustomer("mockcustomer");
		customer2 = new MockCustomer("mockcustomer2");
		waiter = new MockWaiter("mockwaiter");
		cashier.setMenu(new Menu());
		market1 = new MockMarket("market1");
		market2 = new MockMarket("market2");
	}	
	
	public void testOneMarketBillPayedFull() {
		assertTrue("Cashier's bills list should empty. It isn't", cashier.bills.isEmpty());
		assertEquals("Cashier should have $40.00. He has " + String.valueOf(cashier.money), 40.00, cashier.money);
		assertEquals("Market1's log should be empty. Instead it reads: " + market1.log.toString(), market1.log.size(), 0);
		
		assertFalse("Cashier should have nothing to do.", cashier.pickAndExecuteAnAction());
		cashier.msgYourBillIs(market1, 10.00f);
		
		assertEquals("Cashier's bill list should have 1 bill. It has " + cashier.bills.size(), 1, cashier.bills.size());
		assertEquals("The bill's state should be \"Outstanding\". It isn't", BillState.Outstanding, cashier.bills.get(0).state);
		
		assertTrue("Cashier should pay the bill in full", cashier.pickAndExecuteAnAction());
		
		assertTrue("MockMarket should have logged a \"Cashier Payed Full\", but didn't. His log reads instead: " 
				+ market1.log.getLastLoggedEvent().toString(), market1.log.containsString("Cashier Payed Full"));
		
		assertEquals("Bills list should now be size 0. It is " + String.valueOf(cashier.bills.size()), 0, cashier.bills.size());
		assertFalse("Cashier should now not have anything to do", cashier.pickAndExecuteAnAction());
		
		assertEquals("Cashier should have $30.00 left. He has " + String.valueOf(cashier.money), 30.00, cashier.money);
	}
	
	public void testTwoMarketBillsPayedFull() {
		assertTrue("Cashier's bills list should empty. It isn't", cashier.bills.isEmpty());
		assertEquals("Cashier should have $40.00. He has " + String.valueOf(cashier.money), 40.00, cashier.money);
		assertEquals("Market1's log should be empty. Instead it reads: " + market1.log.toString(), market1.log.size(), 0);
		assertEquals("Market2's log should be empty. Instead it reads: " + market2.log.toString(), market2.log.size(), 0);
		
		assertFalse("Cashier should have nothing to do.", cashier.pickAndExecuteAnAction());
		cashier.msgYourBillIs(market1, 10.00f);
		
		assertEquals("Cashier's bill list should have 1 bill. It has " + cashier.bills.size(), 1, cashier.bills.size());
		assertEquals("The bill's state should be \"Outstanding\". It isn't", BillState.Outstanding, cashier.bills.get(0).state);
		
		cashier.msgYourBillIs(market2, 4.87f);
		
		assertEquals("Cashier's bill list should have 2 bills. It has " + cashier.bills.size(), 2, cashier.bills.size());
		assertEquals("The bill's state should be \"Outstanding\". It isn't", BillState.Outstanding, cashier.bills.get(1).state);
		
		assertTrue("Cashier should pay bill 1 in full", cashier.pickAndExecuteAnAction());
		
		assertTrue("MockMarket should have logged a \"Cashier Payed Full\", but didn't. His log reads instead: " 
				+ market1.log.getLastLoggedEvent().toString(), market1.log.containsString("Cashier Payed Full"));
		
		assertEquals("Bills list should now be size 1. It is " + String.valueOf(cashier.bills.size()), 1, cashier.bills.size());
		
		assertEquals("Cashier should have $30.00 left. He has " + String.valueOf(cashier.money), 30.00, cashier.money);
		
		assertTrue("Cashier should pay bill 2 in full", cashier.pickAndExecuteAnAction());
		
		assertTrue("MockMarket should have logged a \"Cashier Payed Full\", but didn't. His log reads instead: " 
				+ market2.log.getLastLoggedEvent().toString(), market2.log.containsString("Cashier Payed Full"));
		
		assertEquals("Bills list should now be size 0. It is " + String.valueOf(cashier.bills.size()), 0, cashier.bills.size());
		
		assertFalse("Cashier should now not have anything to do", cashier.pickAndExecuteAnAction());
		
		assertEquals("Cashier should have $25.13 left. He has " + String.valueOf(cashier.money), 25.13, cashier.money);
	}
	
	public void testOneMarketBillPayedPart() {
		assertTrue("Cashier's bills list should empty. It isn't", cashier.bills.isEmpty());
		assertEquals("Cashier should have $40.00. He has " + String.valueOf(cashier.money), 40.00, cashier.money);
		assertEquals("Market1's log should be empty. Instead it reads: " + market1.log.toString(), market1.log.size(), 0);
		
		assertFalse("Cashier should have nothing to do.", cashier.pickAndExecuteAnAction());
		cashier.msgYourBillIs(market1, 50.00f);
		
		assertEquals("Cashier's bill list should have 1 bill. It has " + cashier.bills.size(), 1, cashier.bills.size());
		assertEquals("The bill's state should be \"Outstanding\". It isn't", BillState.Outstanding, cashier.bills.get(0).state);
		
		assertTrue("Cashier should pay the bill in part", cashier.pickAndExecuteAnAction());
		
		assertTrue("MockMarket should have logged a \"Cashier Payed Part\", but didn't. His log reads instead: " 
				+ market1.log.getLastLoggedEvent().toString(), market1.log.containsString("Cashier Payed Part"));
		
		assertEquals("Bills list should now be size 1. It is " + String.valueOf(cashier.bills.size()), 1, cashier.bills.size());
		assertEquals("The bill's state should be \"PayASAP\". It isn't", BillState.PayASAP, cashier.bills.get(0).state);
		assertFalse("Cashier should now not have anything to do", cashier.pickAndExecuteAnAction());
		
		assertEquals("Cashier should have $0.00 left. He has " + String.valueOf(cashier.money), 0.00, cashier.money);
		assertEquals("Bill should now be 10.00 more for a total of 20.00. It is " + String.valueOf(cashier.bills.get(0).bill), 20.00, cashier.bills.get(0).bill); 
		
		cashier.money = 5.00f;
		
		assertTrue("If cashier gets more money, he should now pay part of the bill.", cashier.pickAndExecuteAnAction());
		assertTrue("MockMarket should have logged a \"Cashier Made Payment On Debt\", but didn't. His log reads instead: " 
				+ market1.log.getLastLoggedEvent().toString(), market1.log.containsString("Cashier Made Payment"));
		assertEquals("Cashier should have $0.00 left. He has " + String.valueOf(cashier.money), 0.00, cashier.money);
		
		cashier.money = 20.00f;
		assertTrue("If cashier gets more money, he should now pay the rest of the bill.", cashier.pickAndExecuteAnAction());
		assertTrue("MockMarket should have logged a \"Cashier Payed Off Debt\", but didn't. His log reads instead: " 
				+ market1.log.getLastLoggedEvent().toString(), market1.log.containsString("Cashier Payed Off Debt"));
		assertEquals("Cashier should have $5.00 left. He has " + String.valueOf(cashier.money), 5.00, cashier.money);
	}
	
	public void testPayingMarketBillAfterCustomerPays() {
		assertTrue("Cashier's bills list should empty. It isn't", cashier.bills.isEmpty());
		assertEquals("Cashier should have $40.00. He has " + String.valueOf(cashier.money), 40.00, cashier.money);
		assertEquals("Market1's log should be empty. Instead it reads: " + market1.log.toString(), market1.log.size(), 0);
		
		assertFalse("Cashier should have nothing to do.", cashier.pickAndExecuteAnAction());
		cashier.msgYourBillIs(market1, 45.00f);
		
		assertEquals("Cashier's bill list should have 1 bill. It has " + cashier.bills.size(), 1, cashier.bills.size());
		assertEquals("The bill's state should be \"Outstanding\". It isn't", BillState.Outstanding, cashier.bills.get(0).state);
		
		assertTrue("Cashier should pay the bill in part", cashier.pickAndExecuteAnAction());
		
		assertTrue("MockMarket should have logged a \"Cashier Payed Part\", but didn't. His log reads instead: " 
				+ market1.log.getLastLoggedEvent().toString(), market1.log.containsString("Cashier Payed Part"));
		
		assertEquals("Bills list should now be size 1. It is " + String.valueOf(cashier.bills.size()), 1, cashier.bills.size());
		assertEquals("The bill's state should be \"PayASAP\". It isn't", BillState.PayASAP, cashier.bills.get(0).state);
		assertFalse("Cashier should now not have anything to do", cashier.pickAndExecuteAnAction());
		
		assertEquals("Cashier should have $0.00 left. He has " + String.valueOf(cashier.money), 0.00, cashier.money);
		
		//Customer Scenario now runs
		assertEquals("Cashier should have 0 checks in it. It doesn't.",cashier.checks.size(), 0);		
		assertEquals("CashierAgent should have an empty event log before the Cashier's HereIscheck is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());
		
		//step 1 of the test
		cashier.msgNeedCheck(waiter, "steak", customer);//send the message from a waiter

		//check postconditions for step 1 and preconditions for step 2
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		
		assertEquals("Cashier should have 1 check in it. It doesn't.", cashier.checks.size(), 1);
		
		assertEquals("The price of the check should be 15.99. It is: " + cashier.checks.get(0).price, cashier.checks.get(0).price, 15.99);
		
		assertEquals("MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		
		assertTrue("Cashier's scheduler should have returned true and gives the waiter a check.", cashier.pickAndExecuteAnAction());
		
		assertTrue("MockWaiter should have logged a \"Received check\", but didn't. His log reads instead: " 
					+ waiter.log.getLastLoggedEvent().toString(), waiter.log.containsString("Received check"));
		
		assertEquals("MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
					+ customer.log.toString(), 0, customer.log.size());
		
		//Waiter sends check to customer
		customer.msgHereIsCheck(waiter.check, cashier);
		
		assertTrue("MockCustomer should have logged a \"Received check\", but didn't. His log reads instead: "
					+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("received check"));
		
		//Customer pays for food
		
		assertEquals("Check in market should be set to CheckState.WithWaiter. It isn't", CheckState.WithWaiter, cashier.checks.get(0).s);
		assertTrue("Cashier's scheduler should have returned false.", !cashier.pickAndExecuteAnAction());
		
		cashier.msgHereIsPayment(customer, cashier.checks.get(0).price);
		
		assertEquals("Check should have payment equal to price. Instead it is: " + String.valueOf(cashier.checks.get(0).payment), cashier.checks.get(0).payment, cashier.checks.get(0).price);
		assertEquals("Check in market should be set to CheckState.Payed. It isn't", CheckState.Payed, cashier.checks.get(0).s);
		
		//Cashier should now pay off the bill
		assertEquals("Cashier should now have money equal to a steak 15.99. He has " + cashier.money, 15.99, cashier.money);
		assertTrue("Cashier now pays off the outstanding bill to the market", cashier.pickAndExecuteAnAction());
		
		assertEquals("Cashier should now have money equal to 0.99. He has " + cashier.money, 0.99, cashier.money);
		
		assertTrue("MockMarket should have logged a \"Cashier Payed Off Debt\", but didn't. His log reads instead: " 
				+ market1.log.getLastLoggedEvent().toString(), market1.log.containsString("Cashier Payed Off"));
		
		assertEquals("Cashier's bills size shouldbe 0. It is: " + cashier.bills.size(), 0, cashier.bills.size());
		
		assertTrue("Cashier's schedule should return true and delete the payed check", cashier.pickAndExecuteAnAction());
		
		assertEquals("Cashier's checks size shouldbe 0. It is: " + cashier.checks.size(), 0, cashier.checks.size());
		
		assertFalse("Cashier should have nothing left to do", cashier.pickAndExecuteAnAction());
	}
	
	/**
	 * This tests the cashier under very simple terms: one customer is ready to pay the exact check.
	 */
	public void testOneNormalCustomerScenario()
	{
		//setUp() runs first before this test!
		
		customer.cashier = cashier;//You can do almost anything in a unit test.			
		
		//check preconditions
		assertEquals("Cashier should have 0 checks in it. It doesn't.",cashier.checks.size(), 0);		
		assertEquals("CashierAgent should have an empty event log before the Cashier's HereIscheck is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());
		
		//step 1 of the test
		cashier.msgNeedCheck(waiter, "steak", customer);//send the message from a waiter

		//check postconditions for step 1 and preconditions for step 2
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		
		assertEquals("Cashier should have 1 check in it. It doesn't.", cashier.checks.size(), 1);
		
		assertEquals("The price of the check should be 15.99. It is: " + cashier.checks.get(0).price, cashier.checks.get(0).price, 15.99);
		
		assertEquals("MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		
		assertTrue("Cashier's scheduler should have returned true and gives the waiter a check.", cashier.pickAndExecuteAnAction());
		
		assertTrue("MockWaiter should have logged a \"Received check\", but didn't. His log reads instead: " 
					+ waiter.log.getLastLoggedEvent().toString(), waiter.log.containsString("Received check"));
		
		assertEquals("MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
					+ customer.log.toString(), 0, customer.log.size());
		
		//Waiter sends check to customer
		customer.msgHereIsCheck(waiter.check, cashier);
		
		assertTrue("MockCustomer should have logged a \"Received check\", but didn't. His log reads instead: "
					+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("received check"));
		
		//Customer pays for food
		
		assertEquals("Check in market should be set to CheckState.WithWaiter. It isn't", CheckState.WithWaiter, cashier.checks.get(0).s);
		assertTrue("Cashier's scheduler should have returned false.", !cashier.pickAndExecuteAnAction());
		
		cashier.msgHereIsPayment(customer, cashier.checks.get(0).price);
		
		assertEquals("Check should have payment equal to price. Instead it is: " + String.valueOf(cashier.checks.get(0).payment), cashier.checks.get(0).payment, cashier.checks.get(0).price);
		assertEquals("Check in market should be set to CheckState.Payed. It isn't", CheckState.Payed, cashier.checks.get(0).s);
		
		assertTrue("Cashier's schedule should return true and delete the payed check", cashier.pickAndExecuteAnAction());
		
		assertEquals("Cashier's checks size shouldbe 0. It is: " + cashier.checks.size(), 0, cashier.checks.size());
		
		assertFalse("Cashier should have nothing left to do", cashier.pickAndExecuteAnAction());
	}//end one normal customer scenario
	
	public void testTwoCustomersPaying() {
		customer.cashier = cashier;//You can do almost anything in a unit test.	
		customer2.cashier = cashier;
		
		//check preconditions
		assertEquals("Cashier should have 0 checks in it. It doesn't.",cashier.checks.size(), 0);		
		assertEquals("CashierAgent should have an empty event log before the Cashier's HereIscheck is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());
		
		//step 1 of the test
		cashier.msgNeedCheck(waiter, "steak", customer);//send the message from a waiter
		cashier.msgNeedCheck(waiter, "pizza", customer2);//send the message from a waiter
		

		//check postconditions for step 1 and preconditions for step 2
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		
		assertEquals("Cashier should have 2 checks in it. It doesn't.", cashier.checks.size(), 2);
		
		assertEquals("The price of the first check should be 15.99. It is: " + cashier.checks.get(0).price, cashier.checks.get(0).price, 15.99);
		assertEquals("The price of the other check should be 8.99. It is: " + cashier.checks.get(1).price, cashier.checks.get(1).price, 8.99);
		
		assertEquals("MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		
		assertTrue("Cashier's scheduler should have returned true and gives the waiter a check.", cashier.pickAndExecuteAnAction());
		
		assertTrue("MockWaiter should have logged a \"Received check\", but didn't. His log reads instead: " 
					+ waiter.log.getLastLoggedEvent().toString(), waiter.log.containsString("Received check"));
		
		assertEquals("MockWaiter's log should be 1 entry long. It is: " + String.valueOf(waiter.log.size()), waiter.log.size(), 1);
		
		assertTrue("Cashier's scheduler should have returned true and gives the waiter a check.", cashier.pickAndExecuteAnAction());
		
		assertTrue("MockWaiter should have logged a \"Received check\", but didn't. His log reads instead: " 
					+ waiter.log.getLastLoggedEvent().toString(), waiter.log.containsString("Received check"));
		
		assertEquals("MockWaiter's log should be 2 entries long. It is: " + String.valueOf(waiter.log.size()), waiter.log.size(), 2);
		
		assertEquals("MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
					+ customer.log.toString(), 0, customer.log.size());
		
		assertEquals("MockCustomer2 should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
				+ customer2.log.toString(), 0, customer2.log.size());
		
		//Waiter sends check to customer
		customer.msgHereIsCheck(waiter.check, cashier);
		
		assertTrue("MockCustomer should have logged a \"Received check\", but didn't. His log reads instead: "
					+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("received check"));
		
		//Customer pays for food
		
		assertEquals("Check in cashier should be set to CheckState.WithWaiter. It isn't", CheckState.WithWaiter, cashier.checks.get(0).s);
		assertTrue("Cashier's scheduler should have returned false.", !cashier.pickAndExecuteAnAction());
		
		cashier.msgHereIsPayment(customer, cashier.checks.get(0).price);
		
		assertEquals("Check should have payment equal to price. Instead it is: " + String.valueOf(cashier.checks.get(0).payment), cashier.checks.get(0).payment, cashier.checks.get(0).price);
		assertEquals("Check in market should be set to CheckState.Payed. It isn't", CheckState.Payed, cashier.checks.get(0).s);
		
		assertTrue("Cashier's schedule should return true and delete the payed check", cashier.pickAndExecuteAnAction());
		
		assertEquals("Cashier's checks size shouldbe 1. It is: " + cashier.checks.size(), 1, cashier.checks.size());
		
		assertFalse("Cashier should have nothing left to do", cashier.pickAndExecuteAnAction());
		
		//Second customer pays and doesn't have enough to pay
		customer2.msgHereIsCheck(waiter.check, cashier);
		
		assertTrue("MockCustomer should have logged a \"Received check\", but didn't. His log reads instead: "
					+ customer2.log.getLastLoggedEvent().toString(), customer2.log.containsString("received check"));
		
		assertTrue("The only check in cashier should be customer2's check", customer2.getName().equals(cashier.checks.get(0).c.getName()));
		
		assertEquals("The check's payment should be 0.00. It is " + String.valueOf(cashier.checks.get(0).payment), 0.00, cashier.checks.get(0).payment);
		
		assertEquals("Check in cashier should be set to CheckState.WithWaiter. It isn't", CheckState.WithWaiter, cashier.checks.get(0).s);
		assertTrue("Cashier's scheduler should have returned false.", !cashier.pickAndExecuteAnAction());
		
		cashier.msgHereIsPayment(customer2, cashier.checks.get(0).price - 2.00f);
		
		assertEquals("Check should have payment equal to 6.99. Instead it is: " + String.valueOf(cashier.checks.get(0).payment), 6.99, cashier.checks.get(0).payment);
		assertEquals("Check in market should be set to CheckState.Payed. It isn't", CheckState.Payed, cashier.checks.get(0).s);
		
		assertTrue("Cashier's schedule should return true and delete the payed check", cashier.pickAndExecuteAnAction());
		
		assertEquals("Cashier's checks size shouldbe 0. It is: " + cashier.checks.size(), 0, cashier.checks.size());
		
		assertFalse("Cashier should have nothing left to do", cashier.pickAndExecuteAnAction());
		//Customer tries to pay for food
	}
	
}
