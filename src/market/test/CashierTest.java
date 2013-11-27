package market.test;

import simcity.gui.SimCityGui;
import market.test.mock.MockManager;
import market.CashierAgent;
import market.test.mock.MockCustomer;
import market.test.mock.MockWorker;
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
	MockWorker worker;
	MockCustomer customer;
	MockWorker worker2;
	MockCustomer customer2;
	MockManager manager;
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();		
		cashier = new CashierAgent("cashier", 500);		
		customer = new MockCustomer();
		customer2 = new MockCustomer();
		worker = new MockWorker();
		worker2 = new MockWorker();
		manager = new MockManager();
	}	
	/**
	 * This tests the cashier under very simple terms: one customer is ready to pay the exact bill.
	 */
	public void testOneNormalCustomerScenario() //one normal customer
	{
		//setUp() runs first before this test!
		
		manager.msgWantToOrder(customer, "American", 1, false);
		cashier.msgHereIsBill(customer, 100);
		assertFalse("Cashier's scheduler should return false when it doesn't have payments, but it doesn't", cashier.pickAndExecuteAnAction());
		cashier.msgHereIsMoney(customer, 100);
		assertTrue("Cashier's scheduler should return true when there is a payment, but it doesn't", cashier.pickAndExecuteAnAction());
		assertFalse("Cashier's scheduler should return false when it's done, but it doesn't", cashier.pickAndExecuteAnAction());
		
	}
	
	public void testTwoNormalCustomerScenario() //two normal customers
	{
		//setUp() runs first before this test!
		
		manager.msgWantToOrder(customer, "American", 1, false);
		manager.msgWantToOrder(customer2, "German", 1, false);
		cashier.msgHereIsBill(customer, 100);
		assertFalse("Cashier's scheduler should return false when it doesn't have payments, but it doesn't", cashier.pickAndExecuteAnAction());
		cashier.msgHereIsBill(customer2, 100);
		assertFalse("Cashier's scheduler should return false when it doesn't have payments, but it doesn't", cashier.pickAndExecuteAnAction());
		cashier.msgHereIsMoney(customer, 100);
		assertTrue("Cashier's scheduler should return true when there is a payment, but it doesn't", cashier.pickAndExecuteAnAction());
		cashier.msgHereIsMoney(customer2, 100);
		assertTrue("Cashier's scheduler should return true when there is a payment, but it doesn't", cashier.pickAndExecuteAnAction());
		assertFalse("Cashier's scheduler should return false when it's done, but it doesn't", cashier.pickAndExecuteAnAction());
		
	}
	
	public void testThreeVirtualCustomerScenario() //one virtual customer
	{
		//setUp() runs first before this test!
		
		manager.msgWantToOrder(customer, "American", 1, true);
		cashier.msgHereIsBill(customer, 200);
		assertFalse("Cashier's scheduler should return false when it doesn't have payments, but it doesn't", cashier.pickAndExecuteAnAction());
		cashier.msgHereIsMoney(customer, 200);
		assertTrue("Cashier's scheduler should return true when there is a payment, but it doesn't", cashier.pickAndExecuteAnAction());
		assertFalse("Cashier's scheduler should return false when it's done, but it doesn't", cashier.pickAndExecuteAnAction());
		
	}
	
	public void testFourVirtualCustomerScenario() //two virtual customers
	{
		//setUp() runs first before this test!
		
		manager.msgWantToOrder(customer, "American", 1, true);
		manager.msgWantToOrder(customer2, "German", 1, true);
		cashier.msgHereIsBill(customer, 200);
		assertFalse("Cashier's scheduler should return false when it doesn't have payments, but it doesn't", cashier.pickAndExecuteAnAction());
		cashier.msgHereIsBill(customer2, 200);
		assertFalse("Cashier's scheduler should return false when it doesn't have payments, but it doesn't", cashier.pickAndExecuteAnAction());
		cashier.msgHereIsMoney(customer, 200);
		assertTrue("Cashier's scheduler should return true when there is a payment, but it doesn't", cashier.pickAndExecuteAnAction());
		cashier.msgHereIsMoney(customer2, 200);
		assertTrue("Cashier's scheduler should return true when there is a payment, but it doesn't", cashier.pickAndExecuteAnAction());
		assertFalse("Cashier's scheduler should return false when it's done, but it doesn't", cashier.pickAndExecuteAnAction());
		
	}
	
}
