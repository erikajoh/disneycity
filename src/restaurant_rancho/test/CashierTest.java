package restaurant_rancho.test;

import junit.framework.*;
import restaurant_rancho.CashierAgent;
import restaurant_rancho.Check;
import restaurant_rancho.CashierAgent.MarketBill;
import restaurant_rancho.CashierAgent.checkState;
import restaurant_rancho.test.mock.MockCustomer;
import restaurant_rancho.test.mock.MockMarket;
import restaurant_rancho.test.mock.MockWaiter;
import restaurant_rancho.gui.RestaurantRancho;
import simcity.Restaurant;
import restaurant_rancho.test.mock.MockBank;
import restaurant_rancho.test.mock.MockMarket_Sim;
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
	MockWaiter waiter2;
	MockCustomer customer2;
	Restaurant restaurant;
	MockBank bank;
	MockMarket_Sim market;
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();		
		cashier = new CashierAgent("cashier", restaurant);		
		customer = new MockCustomer("mockcustomer");
		customer2 = new MockCustomer("mockcustomer2");
		waiter = new MockWaiter("mockwaiter");
		waiter2 = new MockWaiter("mockwaiter2");
		bank = new MockBank("mockbank");
		market = new MockMarket_Sim("mockmarket");
	}	
	/**
	 * This tests the cashier under very simple terms: one customer is ready to pay the exact bill.
	 */
	public void testOneNormalCustomerScenario()
	{
		//setUp() runs first before this test!
		
		customer.cashier = cashier;//You can do almost anything in a unit test.			
		
		//check preconditions
		assertEquals("Cashier should have 0 bills in it. It doesn't.",cashier.checks.size(), 0);	
		
		assertEquals("CashierAgent should have an empty event log before the Cashier's HereIsBill is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());
		
		//step 1 of the test
		Check check = new Check(customer, waiter, "Pizza", 12.0);
		
		cashier.checks.add(check); //add check to cashier's checks
		
		assertEquals("Cashier should have 1 bill in it. It doesn't.", cashier.checks.size(), 1);
		
		assertTrue("Check state should be nothing, but instead, it is " + cashier.checks.get(0).cs, cashier.checks.get(0).cs == checkState.nothing );
		
		
		assertTrue("Cashier's scheduler should return true when there is a check in checks, but it doesn't", cashier.pickAndExecuteAnAction());
		
		assertEquals(
				"MockCustomer should have an empty event log after the Cashier's scheduler is called for "
				+ "the first time. Instead, the MockCustomer's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		
		try {
			Thread.sleep(600);
		}  catch (InterruptedException e) {
			e.printStackTrace();
		}
			
		
		assertTrue("Check state should be ReadyForCustomer after scheduler is called. Instead, it is " + cashier.checks.get(0).cs, cashier.checks.get(0).cs == checkState.readyForCust);
		
		cashier.pickAndExecuteAnAction();
		
		assertTrue("MockWaiter should have logged \"Received Check Ready from cashier\". Instead, "
			+	"the MockWaiter's event log reads: "
						+ waiter.log.getLastLoggedEvent().toString(), waiter.log.containsString("Received Check Ready from cashier"));
		
		assertTrue("Cashier should contain a check of price = $12. It contains something else instead: $" 
				+ cashier.checks.get(0).amount, cashier.checks.get(0).amount == 12);
		
		assertTrue("Cashier should contain a check with the right customer in it. It doesn't.", 
				cashier.checks.get(0).cust == customer);
		
		assertTrue("Check should have state WaitingForCustomer", cashier.checks.get(0).cs == checkState.waitingForCust);
		
		//step 2 of the test
		cashier.msgHereIsMoney(customer, 12);
		
		assertTrue("Cashier should contain a check with state == paid. It doesn't.",
				cashier.checks.get(0).cs == checkState.paid);
		
		assertTrue("Cashier should have logged \"Received Cash\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received Cash"));
		
		
		assertTrue("Cashier should contain a check of price = $0. It contains something else instead: $" 
				+ cashier.checks.get(0).amount, cashier.checks.get(0).amount == 0);
	
		cashier.pickAndExecuteAnAction();
		
		assertTrue("MockCustomer should have logged an event for receiving \"HereIsYourChange\" with the correct change, but his last event logged reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received HereIsYourChange. Change = 0"));
		
		assertTrue("Cashier should have no checks in its list of checks, but it does", cashier.checks.size()==0); 
		
		assertFalse("Cashier's scheduler should return false(no actions left to do), but didn't", cashier.pickAndExecuteAnAction());
	
	}
	
	public void testTwoNonNormalCustomerScenario() {
		customer.cashier = cashier;
		
		Check check = new Check(customer, waiter, "Pizza", 12.0);
		
		cashier.checks.add(check); //add check to cashier's checks
		
		assertEquals("Cashier should have 1 bill in it. It doesn't.", cashier.checks.size(), 1);
		
		assertTrue("Cashier should contain a check of price = $12. It contains something else instead: $" 
				+ cashier.checks.get(0).amount, cashier.checks.get(0).amount == 12);
		
		assertTrue("Cashier should contain a check with the right customer in it. It doesn't.", 
				cashier.checks.get(0).cust == customer);
		
		cashier.pickAndExecuteAnAction();
		
		try {
			Thread.sleep(600);
		}  catch (InterruptedException e) {
			e.printStackTrace();
		}
			
		
		cashier.pickAndExecuteAnAction();
		
		
		assertTrue("Check should have state WaitingForCustomer, instead has state " + cashier.checks.get(0).cs, cashier.checks.get(0).cs == checkState.waitingForCust);
		
		//step 2 of the test
		cashier.msgHereIsMoney(customer, 6);
		
		assertTrue("Cashier should contain a check with state == paid. It doesn't, has state " + cashier.checks.get(0).cs,
				cashier.checks.get(0).cs == checkState.paid);
		
		assertTrue("Cashier should have logged \"Received Cash\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received Cash"));
		
		
		assertTrue("Cashier should contain a check of price = $6. It contains something else instead: $" 
				+ cashier.checks.get(0).amount, cashier.checks.get(0).amount == 6);
	
		cashier.pickAndExecuteAnAction();
		
		assertTrue("MockCustomer should have logged an event for receiving \"YouOweUs\", but his last event logged reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received YouOweUs"));
		
		assertTrue("Cashier should have one check in its list of checks, but it doesn't", cashier.checks.size()==1); 
		
		assertTrue("Cashier's scheduler should return true(there is still an unpaid check), but didn't", cashier.pickAndExecuteAnAction());
	}
	
	public void testThreeCustomerPaysNextTime() {
		customer.cashier=cashier;
		
		//setting preconditions
		Check check = new Check(customer, waiter, "Pizza", 6.0);
		check.cs = checkState.notComplete;
		cashier.checks.add(check);
		
		//checking preconditions
		
		assertTrue("Cashier should have one check", cashier.checks.size()==1);
		assertEquals("Customer log should be empty before he pays cashier ", 0, cashier.log.size());
		
		//customer comes back with $20, pays cashier upon sitting down at restaurant
		cashier.msgHereIsMoney(customer,20);
		
		assertTrue("Cashier should have logged event for \"Received Cash\" but didn't", cashier.log.containsString("Received Cash") );
		
		assertTrue("Check should have state paid, but state is " + cashier.checks.get(0).cs, cashier.checks.get(0).cs == checkState.paid);
		
		cashier.pickAndExecuteAnAction();
		
		assertTrue("Customer should have logged \"Received HereIsYourChange\" with right change amount, but instead logged " 
		+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received HereIsYourChange. Change = 14.0"));
		
		assertTrue("Cashier should have removed check after it was paid, but checks is not empty" , cashier.checks.size()==0);
		
		
	}
	
	public void testFourMultipleCustomersandWaiters() {
			
		//MockCustomer customer2 = new MockCustomer("mockcustomer");		
		//MockWaiter waiter2 = new MockWaiter("mockwaiter");
		
		customer.cashier = cashier;
		
		Check check1 = new Check(customer, waiter, "Steak", 14);
		Check check2 = new Check(customer2, waiter2, "Pizza", 12);
		
		cashier.checks.add(check1);
		cashier.checks.add(check2);
		
		assertEquals("Customer log should be empty before he pays cashier ", 0, cashier.log.size());
		assertEquals("Waiter log should be empty before cashier tells him check is ready ", 0, cashier.log.size());
		
		assertTrue("Cashier should have two checks in checks, but doesn't", cashier.checks.size()==2);
		
		cashier.pickAndExecuteAnAction();
		
		assertTrue("Check1 state should be pending, Check2 state should be nothing", cashier.checks.get(0).cs == checkState.pending && cashier.checks.get(1).cs == checkState.nothing);
		
		cashier.pickAndExecuteAnAction();
		
		assertTrue("Check1 state should be pending, Check2 state should be pending", cashier.checks.get(0).cs == checkState.pending && cashier.checks.get(1).cs == checkState.pending);
		
		try {
			Thread.sleep(600);
		}  catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		assertTrue("Check1 state should be readyForCust, Check2 state should be readyForCust", cashier.checks.get(0).cs == checkState.readyForCust && cashier.checks.get(1).cs == checkState.readyForCust);
		
		cashier.pickAndExecuteAnAction();
		
		assertTrue("Waiter1 should have logged message \"Received Check Ready from cashier\" for the right customer" , waiter.log.containsString("Received Check Ready"));
		
		cashier.pickAndExecuteAnAction();
		
		assertTrue("Waiter2 should have logged message \"Received Check Ready from cashier\" for the right customer" , waiter2.log.containsString("Received Check Ready"));
		
		assertTrue("Waiter1's event log should have only one item in its log, instead it has " + waiter.log.size(), waiter.log.size()==1);
		
		cashier.msgHereIsMoney(customer, 14);
		
		assertTrue("Cashier should have logged message \"Received Cash\" with right amount from customer, but it does not", cashier.log.containsString("Received Cash. 14.0"));
		
		assertTrue("Cashier should only have one logged message, as only one customer paid", cashier.log.size()==1);
		
		cashier.msgHereIsMoney(customer2,  12);
		
		assertTrue("Cashier should have logged message \"Received Cash\" with right amount from customer, but it does not", cashier.log.containsString("Received Cash. 12.0"));
		
		assertTrue("Cashier should have 2 logged messages, as both customers paid", cashier.log.size()==2);
		
		assertTrue("Both checks should be paid now", cashier.checks.get(0).cs == checkState.paid && cashier.checks.get(1).cs == checkState.paid);
		
		cashier.pickAndExecuteAnAction();
		cashier.pickAndExecuteAnAction();
		
		assertTrue("Customer1 should have \"Received HereIsYourChange\" with right change amount, but doesn't", customer.log.containsString("Received HereIsYourChange. Change = 0"));
		assertTrue("Customer2 should have \"Received HereIsYourChange\" with right change amount, but doesn't", customer2.log.containsString("Received HereIsYourChange. Change = 0"));
		
		assertTrue("Cashier should not have any checks in its list of checks after customers have paid, but it does.", cashier.checks.size()==0);
		
	}
	
	public void testFiveBankCreateAccount() {
		//MockMarket market = new MockMarket ("mockmarket");
		
		assertFalse("Cashier's scheduler should return false before MarketBill is added, but it doesn't", cashier.pickAndExecuteAnAction() );
		cashier.setBank(bank);
		Check check = new Check(customer, waiter, "Pizza", 450.0);
		check.cs = checkState.notComplete;
		cashier.checks.add(check);
		
		//checking preconditions
		
		assertTrue("Cashier should have one check", cashier.checks.size()==1);
		assertEquals("Customer log should be empty before he pays cashier ", 0, cashier.log.size());
		
		cashier.msgHereIsMoney(customer,450);
		
		assertTrue("Cashier's scheduler should return true after money goes above, but doesn't ", cashier.pickAndExecuteAnAction());
	  
		assertTrue("Bank's event log should have \"Received msg Request Account\" in it, but it doesn't", bank.log.containsString("Received msg Request Account"));
		
		//cannot test anything else because Bank is incomplete due to a team member's lack of effort in integration
	
		
	}
	
	public void testSixLowBalance() {
		assertFalse("Cashier's scheduler should return false before MarketBill is added, but it doesn't", cashier.pickAndExecuteAnAction() );
		cashier.setBank(bank);
		
		Check check = new Check(customer, waiter, "Pizza", 6.0);
		check.cs = checkState.notComplete;
		cashier.checks.add(check);
		
		assertTrue("Cashier should have one check", cashier.checks.size()==1);
		assertEquals("Customer log should be empty before he pays cashier ", 0, cashier.log.size());
	
		cashier.msgHereIsMoney(customer,450);
		
		assertTrue("Cashier's scheduler should return true after money goes above, but doesn't ", cashier.pickAndExecuteAnAction());
		
		assertFalse("Cashier's scheduler should return false, but doesn't ", cashier.pickAndExecuteAnAction());
		
		cashier.subtract(100);
		
		assertTrue("Cashier's scheduler should return true after money goes down below low threshold, but doesn't ", cashier.pickAndExecuteAnAction());
	  
		//cashier asks for bank account because it doesn't have one
		assertTrue("Bank's event log should have \"Received msg Request Account\" in it, but it doesn't", bank.log.containsString("Received msg Request Account"));
		
	}
	
	public void testSevenLowBalanceAndHasAccount() {
		assertFalse("Cashier's scheduler should return false before money is added or subtracted, but it doesn't", cashier.pickAndExecuteAnAction() );
		
		cashier.setBank(bank);
		
		cashier.setAcctNum(1010);
		
		assertTrue("Cashier should have no checks", cashier.checks.size()==0);
		
		assertFalse("Cashier's scheduler should return false before money is added or subtracted, but it doesn't", cashier.pickAndExecuteAnAction() );
		
		cashier.subtract(100); 
		
		assertTrue("Cashier's balance should be low now (below 20), but it isn't", cashier.money <20);
		
		assertTrue("Cashier's scheduler should return true after money goes low, but doesn't ", cashier.pickAndExecuteAnAction());
		
		assertTrue("Bank's event log should have \"Received msg Request Withdrawal\" in it, but it doesn't", bank.log.containsString("Received msg Request Withdrawal"));
		
		assertTrue("Cashier's balance should be low now (below 20), but it isn't", cashier.money <20);
		
		//cannot test any more because bank does not integrate with my restaurant due to team member's lack of effort and commitment to integrating
	}
	
	public void testEightMarket() {
		
		
		
	}
	
}
