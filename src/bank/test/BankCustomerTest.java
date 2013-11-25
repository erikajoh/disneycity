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
import bank.test.mock.MockBankCustomer;
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
		bank.setManager(manager);
        teller0 = new MockTeller("Teller0");	
        teller0.setManager(manager);
    	manager.addTeller(teller0);
        teller1 = new MockTeller("Teller1");	
        teller1.setManager(manager);
    	manager.addTeller(teller0);
        teller2 = new MockTeller("Teller2");	
        teller2.setManager(manager);
    	manager.addTeller(teller0);
        teller3 = new MockTeller("Teller3");	
        teller3.setManager(manager);
    	manager.addTeller(teller0);
    	
	}	
	/**
	 * This tests the cashier under very simple terms: paying one market
	 */
	public void testBankOpenAccountScenario(){
		//check preconditions
		customer0 = new MockPerson("Person");
		customer0.setBalance(50.00);
		customer0.msgArrive(-1, 25.00); //hack for mock, 0 for deposit, 1 for withdraw, any other num for new acc or no num
		customer0.setBank(bank);
		assertEquals(customer0.accounts.size(), 0);
		assertTrue("Customer0 has arrived", customer0.pickAndExecuteAnAction());
		assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("New Bank Customer"));
		MockBankCustomer mbc = bank.getMBC(customer0);
		assertTrue("Manager can assign customer to teller", manager.pickAndExecuteAnAction());
		assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("ASSIGNING AND NEW ACCOUNT"));
		assertTrue(mbc.log.getLastLoggedEvent().toString(), mbc.log.containsString("REQ NEW ACCOUNT"));
		assertTrue("MockBankCustomer will go to teller", mbc.pickAndExecuteAnAction());
		mbc.msgAnimationFinishedGoToTeller();
		assertNotSame(mbc.teller, null);
		assertTrue("Finished simulation to teller so bank customer should open account", mbc.pickAndExecuteAnAction());
		assertTrue(mbc.log.getLastLoggedEvent().toString(), mbc.log.containsString("OPEN ACCOUNT"));
		assertTrue(teller0.log.getLastLoggedEvent().toString(), teller0.log.containsString("OPEN ACCOUNT"));
		assertTrue("Teller0 will finally open the account", teller0.pickAndExecuteAnAction());
		assertTrue(mbc.log.getLastLoggedEvent().toString(), mbc.log.containsString("ACCOUNT OPENED"));
		assertTrue("Spawned bank customer will leave bank", mbc.pickAndExecuteAnAction());
		assertTrue(teller0.log.getLastLoggedEvent().toString(), teller0.log.containsString("LEAVING"));
		assertTrue("Teller will tell manager that he is free", teller0.pickAndExecuteAnAction());
		assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("TELLER FREE"));
		assertTrue("Manager will pass this info to the bank", manager.pickAndExecuteAnAction());
		assertTrue(customer0.log.getLastLoggedEvent().toString(), customer0.log.containsString("TRANSACTION COMPLETE"));
		assertTrue("Person will generate new balance", customer0.pickAndExecuteAnAction());
		assertEquals(customer0.balance, 25.0);
		assertEquals(customer0.accounts.size(), 1);





		//assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("New Bank Customer"));

		/*
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
		assertEquals("Make sure cashier removes bill since it is paid", cashier.bills.size(), 0);	*/	
	}
	
}
