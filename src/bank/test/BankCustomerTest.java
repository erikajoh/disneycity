package bank.test;

import java.util.ArrayList;
import java.util.Collection;

import simcity.gui.SimCityGui;
import bank.ManagerAgent;
import bank.TellerAgent;

//import bank.CashierAgent.cashierBillState;
//import bank.WaiterAgent.Bill;

import bank.gui.TellerGui;
import bank.interfaces.BankCustomer;
import bank.test.mock.MockBank;
import bank.test.mock.MockManager;
import bank.test.mock.MockTeller;
import bank.test.mock.MockPerson;
import junit.framework.*;

/**
 * 
 * This class is a JUnit test class to unit test the CashierAgent's basic interaction
 * with waiters, customers, and the host.
 * It is provided as an example to students in CS201 for their unit testing lab.
 *
 * @author Monroe Ekilah
 */
public class BankCustomerTest extends TestCase
{
	//these are instantiated for each test separately via the setUp() method.

	MockBank bank;
	MockManager manager;

	MockTeller teller0;
	MockTeller teller1;
	MockTeller teller2;
	MockTeller teller3;

	MockPerson customer0;
	MockPerson customer1;
	MockPerson customer2;
	
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();	
		bank = new MockBank(null);
		manager = new MockManager("Bank", bank, null);
        teller0 = new MockTeller("Teller0");	
        teller0.setManager(manager);
    	manager.addTeller(teller0);
    	
	}	
	/**
	 * This tests the cashier under very simple terms: paying one market
	 */
	public void testBankOpenAccountScenario(){
		for(MockMarket market : markets){
           market.setAllStockToAmt(4);
		}
		MockMarket market = null;
		Food food = null;
		for(MockMarket m : markets){
			market = m; break; //get first MockMarket
		}
		for(Food f : foods){
			food = f; break; //get first Food
		}
		
		market.msgHereIsOrder(cook, food, 4);
		
		//check preconditions
		assertEquals("Make sure cashier doesn't have any bills", cashier.bills.size(), 0);		
		assertTrue("Market's scheduler should have returned true (as it is biling the cashier)", market.pickAndExecuteAnAction());
		assertFalse("Market's scheduler should have returned false (as it has already billed the cashier and is idle)", market.pickAndExecuteAnAction());

		//test
		assertEquals("Make sure cashier has a bill", cashier.bills.size(), 1);		
		assertTrue("Cashier's scheduler should have returned true (as it is paying the markets' bill(s))", cashier.pickAndExecuteAnAction());
		assertFalse("Cashier's scheduler should have returned true (as it has already paid the markets' bill(s) and is idle)", cashier.pickAndExecuteAnAction());
		//market hasn't cleared the cashier yet
		assertEquals("Make sure cashier removes bill since it is paid", cashier.bills.size(), 1);		
		
		assertTrue("Market is now making sure the cashier paid enough", market.pickAndExecuteAnAction());
		assertFalse("Market doesn't have anything to do", market.pickAndExecuteAnAction());
		assertTrue("Cashier is removing the bill now", cashier.pickAndExecuteAnAction());
		assertFalse("Cashier doesn't have any bills left to remove", cashier.pickAndExecuteAnAction());
		
		//bill is officially cleared
		assertEquals("Make sure cashier removes bill since it is paid", cashier.bills.size(), 0);		
	}
	
}
