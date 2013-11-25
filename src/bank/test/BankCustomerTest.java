package bank.test;



//import bank.CashierAgent.cashierBillState;
//import bank.WaiterAgent.Bill;


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
 * @author Dylan Eirinberg
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
	 * This tests the cashier under very simple terms: opening an account and having the correct amount of cash at the end
	 */
	public void testBankOpenAccountScenario(){
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
	}
	
	/**
	 * This tests the cashier under very simple terms: opening an account, depositing cash, and having the correct amount of cash at the end
	 */
	public void testBankOpenAndDepositScenario(){
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
		
		customer0.msgArrive(0, 5.00); //now depositing, see above
		assertTrue("Customer0 has arrived", customer0.pickAndExecuteAnAction());
		assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("New Bank Customer"));
	    mbc = bank.getMBC(customer0);
		assertTrue("Manager can assign customer to teller", manager.pickAndExecuteAnAction());
	
		assertTrue("MockBankCustomer will go to teller", mbc.pickAndExecuteAnAction());
		mbc.msgAnimationFinishedGoToTeller();
		assertNotSame(mbc.teller, null);
		assertTrue("Finished simulation to teller so bank customer should deposit cash", mbc.pickAndExecuteAnAction());
		assertTrue("Teller0 will finally open the account", teller0.pickAndExecuteAnAction());
		assertTrue("Spawned bank customer will leave bank", mbc.pickAndExecuteAnAction());
		assertTrue("Teller will tell manager that he is free", teller0.pickAndExecuteAnAction());
		assertTrue("Manager will pass this info to the bank", manager.pickAndExecuteAnAction());
		assertTrue("Person will generate new balance", customer0.pickAndExecuteAnAction());
		assertEquals(customer0.balance, 20.0);
		assertEquals(customer0.accounts.size(), 1);
}

/**
 * This tests the cashier under very simple terms: opening an account, withdrawing cash, and having the correct amount of cash at the end
 */

public void testBankOpenAndWithdrawScenario(){
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
	
	customer0.msgArrive(1, 5.00); //now depositing, see above
	assertTrue("Customer0 has arrived", customer0.pickAndExecuteAnAction());
	assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("New Bank Customer"));
    mbc = bank.getMBC(customer0);
	assertTrue("Manager can assign customer to teller", manager.pickAndExecuteAnAction());

	assertTrue("MockBankCustomer will go to teller", mbc.pickAndExecuteAnAction());
	mbc.msgAnimationFinishedGoToTeller();
	assertNotSame(mbc.teller, null);
	assertTrue("Finished simulation to teller so bank customer should withdraw cash", mbc.pickAndExecuteAnAction());
	assertTrue("Teller0 will finally open the account", teller0.pickAndExecuteAnAction());
	assertTrue("Spawned bank customer will leave bank", mbc.pickAndExecuteAnAction());
	assertTrue("Teller will tell manager that he is free", teller0.pickAndExecuteAnAction());
	assertTrue("Manager will pass this info to the bank", manager.pickAndExecuteAnAction());
	assertTrue("Person will generate new balance", customer0.pickAndExecuteAnAction());
	assertEquals(customer0.balance, 30.0);
	assertEquals(customer0.accounts.size(), 1);
  }


/**
 * This tests the cashier under very simple terms: opening an account, withdrawing cash until the person has a loan
 */ 
 public void testBankOpenAndWithdrawUntilLoanScenario(){
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
	
	customer0.msgArrive(1, 15.00); //now withdrawing, see above
	assertTrue("Customer0 has arrived", customer0.pickAndExecuteAnAction());
	assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("New Bank Customer"));
    mbc = bank.getMBC(customer0);
	assertTrue("Manager can assign customer to teller", manager.pickAndExecuteAnAction());

	assertTrue("MockBankCustomer will go to teller", mbc.pickAndExecuteAnAction());
	mbc.msgAnimationFinishedGoToTeller();
	assertNotSame(mbc.teller, null);
	assertTrue("Finished simulation to teller so bank customer should deposit cash", mbc.pickAndExecuteAnAction());
	assertTrue("Teller0 will finally withdraw cash", teller0.pickAndExecuteAnAction());
	assertTrue("Spawned bank customer will leave bank", mbc.pickAndExecuteAnAction());
	assertTrue("Teller will tell manager that he is free", teller0.pickAndExecuteAnAction());
	assertTrue("Manager will pass this info to the bank", manager.pickAndExecuteAnAction());
	assertTrue("Person will generate new balance", customer0.pickAndExecuteAnAction());
	assertEquals(customer0.balance, 40.00);
	assertEquals(customer0.loanAmt, 0.00);
	assertEquals(customer0.accounts.size(), 1);
	
	customer0.msgArrive(1, 25.00); //now withdrawing more than bank balance (10.00-25.00 = -15.00), should create $50 balance, $15 loan
	assertTrue("Customer0 has arrived", customer0.pickAndExecuteAnAction());
	assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("New Bank Customer"));
    mbc = bank.getMBC(customer0);
	assertTrue("Manager can assign customer to teller", manager.pickAndExecuteAnAction());

	assertTrue("MockBankCustomer will go to teller", mbc.pickAndExecuteAnAction());
	mbc.msgAnimationFinishedGoToTeller();
	assertNotSame(mbc.teller, null);
	assertTrue("Finished simulation to teller so bank customer should deposit cash", mbc.pickAndExecuteAnAction());
	assertTrue("Teller0 will finally open the account", teller0.pickAndExecuteAnAction());
	assertTrue("Spawned bank customer will leave bank", mbc.pickAndExecuteAnAction());
	assertTrue("Teller will tell manager that he is free", teller0.pickAndExecuteAnAction());
	assertTrue("Manager will pass this info to the bank", manager.pickAndExecuteAnAction());
	assertTrue("Person will generate new balance", customer0.pickAndExecuteAnAction());
	assertEquals(customer0.balance, 65.00);
	assertEquals(customer0.loanAmt, 15.00);
	assertEquals(customer0.accounts.size(), 1);
  }
 /**
  * This tests the cashier under very simple terms: opening an account and paying back a loan
  */ 
  public void testBankPayBackLoanScenario(){
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
 	
 	customer0.msgArrive(1, 15.00); //now withdrawing, see above
 	assertTrue("Customer0 has arrived", customer0.pickAndExecuteAnAction());
 	assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("New Bank Customer"));
     mbc = bank.getMBC(customer0);
 	assertTrue("Manager can assign customer to teller", manager.pickAndExecuteAnAction());

 	assertTrue("MockBankCustomer will go to teller", mbc.pickAndExecuteAnAction());
 	mbc.msgAnimationFinishedGoToTeller();
 	assertNotSame(mbc.teller, null);
 	assertTrue("Finished simulation to teller so bank customer should deposit cash", mbc.pickAndExecuteAnAction());
 	assertTrue("Teller0 will finally withdraw cash", teller0.pickAndExecuteAnAction());
 	assertTrue("Spawned bank customer will leave bank", mbc.pickAndExecuteAnAction());
 	assertTrue("Teller will tell manager that he is free", teller0.pickAndExecuteAnAction());
 	assertTrue("Manager will pass this info to the bank", manager.pickAndExecuteAnAction());
 	assertTrue("Person will generate new balance", customer0.pickAndExecuteAnAction());
 	assertEquals(customer0.balance, 40.00);
 	assertEquals(customer0.loanAmt, 0.00);
 	assertEquals(customer0.accounts.size(), 1);
 	
 	customer0.msgArrive(1, 25.00); //now withdrawing more than bank balance (10.00-25.00 = -15.00), should create $50 balance, $15 loan
 	assertTrue("Customer0 has arrived", customer0.pickAndExecuteAnAction());
 	assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("New Bank Customer"));
     mbc = bank.getMBC(customer0);
 	assertTrue("Manager can assign customer to teller", manager.pickAndExecuteAnAction());

 	assertTrue("MockBankCustomer will go to teller", mbc.pickAndExecuteAnAction());
 	mbc.msgAnimationFinishedGoToTeller();
 	assertNotSame(mbc.teller, null);
 	assertTrue("Finished simulation to teller so bank customer should deposit cash", mbc.pickAndExecuteAnAction());
 	assertTrue("Teller0 will finally open the account", teller0.pickAndExecuteAnAction());
 	assertTrue("Spawned bank customer will leave bank", mbc.pickAndExecuteAnAction());
 	assertTrue("Teller will tell manager that he is free", teller0.pickAndExecuteAnAction());
 	assertTrue("Manager will pass this info to the bank", manager.pickAndExecuteAnAction());
 	assertTrue("Person will generate new balance", customer0.pickAndExecuteAnAction());
 	assertEquals(customer0.balance, 65.00);
 	assertEquals(customer0.loanAmt, 15.00);
 	assertEquals(customer0.loanTime, 3); //the customer should have 3 bank visits to start to pay off the loan
 	assertEquals(customer0.accounts.size(), 1);
 	
 	customer0.msgArrive(0, 10.00); //now paying back loan (loan balance should be 15.00-10.00 = 5.00), should create $55 balance, $5 loan
 	assertTrue("Customer0 has arrived", customer0.pickAndExecuteAnAction());
 	assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("New Bank Customer"));
     mbc = bank.getMBC(customer0);
 	assertTrue("Manager can assign customer to teller", manager.pickAndExecuteAnAction());

 	assertTrue("MockBankCustomer will go to teller", mbc.pickAndExecuteAnAction());
 	mbc.msgAnimationFinishedGoToTeller();
 	assertNotSame(mbc.teller, null);
 	assertTrue("Finished simulation to teller so bank customer should deposit cash", mbc.pickAndExecuteAnAction());
 	assertTrue("Teller0 will finally open the account", teller0.pickAndExecuteAnAction());
 	assertTrue("Spawned bank customer will leave bank", mbc.pickAndExecuteAnAction());
 	assertTrue("Teller will tell manager that he is free", teller0.pickAndExecuteAnAction());
 	assertTrue("Manager will pass this info to the bank", manager.pickAndExecuteAnAction());
 	assertTrue("Person will generate new balance", customer0.pickAndExecuteAnAction());
 	assertEquals(customer0.balance, 55.00);
 	assertEquals(customer0.loanAmt, 5.00);
 	assertEquals(customer0.loanTime, 2); //the customer should have 2 bank visits left since it didn't fully pay it off this time
 	assertEquals(customer0.accounts.size(), 1);
 	
	customer0.msgArrive(0, 27.00); //finished paying back loan (loan balance should be 0.00), should create $58 balance, $0 loan
 	assertTrue("Customer0 has arrived", customer0.pickAndExecuteAnAction());
 	assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("New Bank Customer"));
     mbc = bank.getMBC(customer0);
 	assertTrue("Manager can assign customer to teller", manager.pickAndExecuteAnAction());
 	
 	assertTrue("MockBankCustomer will go to teller", mbc.pickAndExecuteAnAction());
 	mbc.msgAnimationFinishedGoToTeller();
 	assertNotSame(mbc.teller, null);
 	assertTrue("Finished simulation to teller so bank customer should deposit cash", mbc.pickAndExecuteAnAction());
 	assertTrue("Teller0 will finally open the account", teller0.pickAndExecuteAnAction());
 	assertTrue("Spawned bank customer will leave bank", mbc.pickAndExecuteAnAction());
 	assertTrue("Teller will tell manager that he is free", teller0.pickAndExecuteAnAction());
 	assertTrue("Manager will pass this info to the bank", manager.pickAndExecuteAnAction());
 	assertTrue("Person will generate new balance", customer0.pickAndExecuteAnAction());
 	assertEquals(customer0.balance, 28.00);
 	assertEquals(customer0.loanAmt, 0.00);
 	assertEquals(customer0.loanTime, 0); //the customer should have 0 bank visits left since the loan is paid off and doesn't matter anymore
 	assertEquals(customer0.accounts.size(), 1);
 	
	customer0.msgArrive(0, 3.00); //finally with no loan the customer will deposit 3.00 bringing his total down to $25.00
 	assertTrue("Customer0 has arrived", customer0.pickAndExecuteAnAction());
 	assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("New Bank Customer"));
     mbc = bank.getMBC(customer0);
 	assertTrue("Manager can assign customer to teller", manager.pickAndExecuteAnAction());
 	
 	assertTrue("MockBankCustomer will go to teller", mbc.pickAndExecuteAnAction());
 	mbc.msgAnimationFinishedGoToTeller();
 	assertNotSame(mbc.teller, null);
 	assertTrue("Finished simulation to teller so bank customer should deposit cash", mbc.pickAndExecuteAnAction());
 	assertTrue("Teller0 will finally open the account", teller0.pickAndExecuteAnAction());
 	assertTrue("Spawned bank customer will leave bank", mbc.pickAndExecuteAnAction());
 	assertTrue("Teller will tell manager that he is free", teller0.pickAndExecuteAnAction());
 	assertTrue("Manager will pass this info to the bank", manager.pickAndExecuteAnAction());
 	assertTrue("Person will generate new balance", customer0.pickAndExecuteAnAction());
 	assertEquals(customer0.balance, 25.00);
 	assertEquals(customer0.loanAmt, 0.00);
 	assertEquals(customer0.accounts.size(), 1);
   }
  
  /**
   * This tests the cashier under more complex terms: making a loan and then adding to the loan later
   */ 
   public void testBankMultipleLoansScenario(){
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
  	
  	customer0.msgArrive(1, 15.00); //now withdrawing, see above
  	assertTrue("Customer0 has arrived", customer0.pickAndExecuteAnAction());
  	assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("New Bank Customer"));
      mbc = bank.getMBC(customer0);
  	assertTrue("Manager can assign customer to teller", manager.pickAndExecuteAnAction());

  	assertTrue("MockBankCustomer will go to teller", mbc.pickAndExecuteAnAction());
  	mbc.msgAnimationFinishedGoToTeller();
  	assertNotSame(mbc.teller, null);
  	assertTrue("Finished simulation to teller so bank customer should deposit cash", mbc.pickAndExecuteAnAction());
  	assertTrue("Teller0 will finally withdraw cash", teller0.pickAndExecuteAnAction());
  	assertTrue("Spawned bank customer will leave bank", mbc.pickAndExecuteAnAction());
  	assertTrue("Teller will tell manager that he is free", teller0.pickAndExecuteAnAction());
  	assertTrue("Manager will pass this info to the bank", manager.pickAndExecuteAnAction());
  	assertTrue("Person will generate new balance", customer0.pickAndExecuteAnAction());
  	assertEquals(customer0.balance, 40.00);
  	assertEquals(customer0.loanAmt, 0.00);
  	assertEquals(customer0.accounts.size(), 1);
  	
  	customer0.msgArrive(1, 110.00); //now withdrawing more than bank balance (10.00-110.00 = -100.00), should create $150 balance, $100 loan
  	assertTrue("Customer0 has arrived", customer0.pickAndExecuteAnAction());
  	assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("New Bank Customer"));
      mbc = bank.getMBC(customer0);
  	assertTrue("Manager can assign customer to teller", manager.pickAndExecuteAnAction());

  	assertTrue("MockBankCustomer will go to teller", mbc.pickAndExecuteAnAction());
  	mbc.msgAnimationFinishedGoToTeller();
  	assertNotSame(mbc.teller, null);
  	assertTrue("Finished simulation to teller so bank customer should deposit cash", mbc.pickAndExecuteAnAction());
  	assertTrue("Teller0 will finally open the account", teller0.pickAndExecuteAnAction());
  	assertTrue("Spawned bank customer will leave bank", mbc.pickAndExecuteAnAction());
  	assertTrue("Teller will tell manager that he is free", teller0.pickAndExecuteAnAction());
  	assertTrue("Manager will pass this info to the bank", manager.pickAndExecuteAnAction());
  	assertTrue("Person will generate new balance", customer0.pickAndExecuteAnAction());
  	assertEquals(customer0.balance, 150.00);
  	assertEquals(customer0.loanAmt, 100.00);
  	assertEquals(customer0.loanTime, 3); //the customer should have 3 bank visits to start to pay off the loan
  	assertEquals(customer0.accounts.size(), 1);
  	
  	customer0.msgArrive(1, 50.00); //now withdrawing more than bank balance (-100.00-50.00 = -150.00), should create $200 balance, $150 loan
  	assertTrue("Customer0 has arrived", customer0.pickAndExecuteAnAction());
  	assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("New Bank Customer"));
      mbc = bank.getMBC(customer0);
  	assertTrue("Manager can assign customer to teller", manager.pickAndExecuteAnAction());

  	assertTrue("MockBankCustomer will go to teller", mbc.pickAndExecuteAnAction());
  	mbc.msgAnimationFinishedGoToTeller();
  	assertNotSame(mbc.teller, null);
  	assertTrue("Finished simulation to teller so bank customer should deposit cash", mbc.pickAndExecuteAnAction());
  	assertTrue("Teller0 will finally open the account", teller0.pickAndExecuteAnAction());
  	assertTrue("Spawned bank customer will leave bank", mbc.pickAndExecuteAnAction());
  	assertTrue("Teller will tell manager that he is free", teller0.pickAndExecuteAnAction());
  	assertTrue("Manager will pass this info to the bank", manager.pickAndExecuteAnAction());
  	assertTrue("Person will generate new balance", customer0.pickAndExecuteAnAction());
  	assertEquals(customer0.balance, 200.00);
  	assertEquals(customer0.loanAmt, 150.00);
  	assertEquals(customer0.loanTime, 2); //the customer should have 3 bank visits to start to pay off the loan
  	assertEquals(customer0.accounts.size(), 1);
    }
  
  /**
   * This tests the cashier under more complex terms: opening an account and failing to pay back a loan after 3 bank visits (loan time)
   */ 
   public void testBankFailPayBackLoanScenario(){
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
  	
  	customer0.msgArrive(1, 15.00); //now withdrawing, see above
  	assertTrue("Customer0 has arrived", customer0.pickAndExecuteAnAction());
  	assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("New Bank Customer"));
      mbc = bank.getMBC(customer0);
  	assertTrue("Manager can assign customer to teller", manager.pickAndExecuteAnAction());

  	assertTrue("MockBankCustomer will go to teller", mbc.pickAndExecuteAnAction());
  	mbc.msgAnimationFinishedGoToTeller();
  	assertNotSame(mbc.teller, null);
  	assertTrue("Finished simulation to teller so bank customer should deposit cash", mbc.pickAndExecuteAnAction());
  	assertTrue("Teller0 will finally withdraw cash", teller0.pickAndExecuteAnAction());
  	assertTrue("Spawned bank customer will leave bank", mbc.pickAndExecuteAnAction());
  	assertTrue("Teller will tell manager that he is free", teller0.pickAndExecuteAnAction());
  	assertTrue("Manager will pass this info to the bank", manager.pickAndExecuteAnAction());
  	assertTrue("Person will generate new balance", customer0.pickAndExecuteAnAction());
  	assertEquals(customer0.balance, 40.00);
  	assertEquals(customer0.loanAmt, 0.00);
  	assertEquals(customer0.accounts.size(), 1);
  	
  	customer0.msgArrive(1, 260.00); //now withdrawing more than bank balance (10.00-260.00 = -250.00), should create $300 balance, $250 loan
  	assertTrue("Customer0 has arrived", customer0.pickAndExecuteAnAction());
  	assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("New Bank Customer"));
      mbc = bank.getMBC(customer0);
  	assertTrue("Manager can assign customer to teller", manager.pickAndExecuteAnAction());

  	assertTrue("MockBankCustomer will go to teller", mbc.pickAndExecuteAnAction());
  	mbc.msgAnimationFinishedGoToTeller();
  	assertNotSame(mbc.teller, null);
  	assertTrue("Finished simulation to teller so bank customer should deposit cash", mbc.pickAndExecuteAnAction());
  	assertTrue("Teller0 will finally open the account", teller0.pickAndExecuteAnAction());
  	assertTrue("Spawned bank customer will leave bank", mbc.pickAndExecuteAnAction());
  	assertTrue("Teller will tell manager that he is free", teller0.pickAndExecuteAnAction());
  	assertTrue("Manager will pass this info to the bank", manager.pickAndExecuteAnAction());
  	assertTrue("Person will generate new balance", customer0.pickAndExecuteAnAction());
  	assertEquals(customer0.balance, 300.00);
  	assertEquals(customer0.loanAmt, 250.00);
  	assertEquals(customer0.loanTime, 3); //the customer should have 3 bank visits to start to pay off the loan
  	assertEquals(customer0.accounts.size(), 1);
  	
  	customer0.setBalance(50.00); //the customer paid off someone else and is running out of money
  	
  	customer0.msgArrive(0, 50.00); //now depositing his extra cash, should create $0 balance, $200 loan
  	assertTrue("Customer0 has arrived", customer0.pickAndExecuteAnAction());
  	assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("New Bank Customer"));
      mbc = bank.getMBC(customer0);
  	assertTrue("Manager can assign customer to teller", manager.pickAndExecuteAnAction());

  	assertTrue("MockBankCustomer will go to teller", mbc.pickAndExecuteAnAction());
  	mbc.msgAnimationFinishedGoToTeller();
  	assertNotSame(mbc.teller, null);
  	assertTrue("Finished simulation to teller so bank customer should deposit cash", mbc.pickAndExecuteAnAction());
  	assertTrue("Teller0 will finally open the account", teller0.pickAndExecuteAnAction());
  	assertTrue("Spawned bank customer will leave bank", mbc.pickAndExecuteAnAction());
  	assertTrue("Teller will tell manager that he is free", teller0.pickAndExecuteAnAction());
  	assertTrue("Manager will pass this info to the bank", manager.pickAndExecuteAnAction());
  	assertTrue("Person will generate new balance", customer0.pickAndExecuteAnAction());
  	assertEquals(customer0.balance, 0.00);
  	assertEquals(customer0.loanAmt, 200.00);
  	assertEquals(customer0.loanTime, 2); //the customer should have 2 bank visits left to pay off loan
  	assertEquals(customer0.accounts.size(), 1);
  	
  	customer0.setBalance(50.00); //the customer got paid for working and decides to go back to the bank to deposit again

  	customer0.msgArrive(0, 50.00); //now depositing his extra cash, should create $0 balance, $150 loan
  	assertTrue("Customer0 has arrived", customer0.pickAndExecuteAnAction());
  	assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("New Bank Customer"));
      mbc = bank.getMBC(customer0);
  	assertTrue("Manager can assign customer to teller", manager.pickAndExecuteAnAction());

  	assertTrue("MockBankCustomer will go to teller", mbc.pickAndExecuteAnAction());
  	mbc.msgAnimationFinishedGoToTeller();
  	assertNotSame(mbc.teller, null);
  	assertTrue("Finished simulation to teller so bank customer should deposit cash", mbc.pickAndExecuteAnAction());
  	assertTrue("Teller0 will finally open the account", teller0.pickAndExecuteAnAction());
  	assertTrue("Spawned bank customer will leave bank", mbc.pickAndExecuteAnAction());
  	assertTrue("Teller will tell manager that he is free", teller0.pickAndExecuteAnAction());
  	assertTrue("Manager will pass this info to the bank", manager.pickAndExecuteAnAction());
  	assertTrue("Person will generate new balance", customer0.pickAndExecuteAnAction());
  	assertEquals(customer0.balance, 0.00);
  	assertEquals(customer0.loanAmt, 150.00);
  	assertEquals(customer0.loanTime, 1); //the customer should have 1 bank visit left to pay off loan
  	assertEquals(customer0.accounts.size(), 1);
  	
  	customer0.setBalance(50.00); //the customer got paid for working and decides to go back to the bank to deposit again
  	
  	customer0.msgArrive(0, 50.00); //now depositing his extra cash, should create $0 balance, $100 loan
  	assertTrue("Customer0 has arrived", customer0.pickAndExecuteAnAction());
  	assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("New Bank Customer"));
      mbc = bank.getMBC(customer0);
  	assertTrue("Manager can assign customer to teller", manager.pickAndExecuteAnAction());
  	
  	assertTrue("MockBankCustomer will go to teller", mbc.pickAndExecuteAnAction());
  	mbc.msgAnimationFinishedGoToTeller();
  	assertNotSame(mbc.teller, null);
  	assertTrue("Finished simulation to teller so bank customer should deposit cash", mbc.pickAndExecuteAnAction());
  	assertTrue("Teller0 will finally open the account", teller0.pickAndExecuteAnAction());
  	assertTrue("Spawned bank customer will leave bank", mbc.pickAndExecuteAnAction());
  	assertTrue("Teller will tell manager that he is free", teller0.pickAndExecuteAnAction());
  	assertTrue("Manager will pass this info to the bank", manager.pickAndExecuteAnAction());
  	assertTrue("Person will generate new balance", customer0.pickAndExecuteAnAction());
  	assertEquals(customer0.balance, 0.00);
  	assertEquals(customer0.loanAmt, 100.00);
  	assertEquals(customer0.loanTime, 0); //the customer should have no bank visits left to pay off loan
  	assertEquals(customer0.accounts.size(), 1);
  	
  	customer0.setBalance(100.00); //the customer got a big pay day and decides to finally pay off his loan
  	
  	customer0.msgArrive(0, 100.00); //unfortunately there is interest so he still has an exponential loan, should create $0 balance, $25 loan
  	assertTrue("Customer0 has arrived", customer0.pickAndExecuteAnAction());
  	assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("New Bank Customer"));
      mbc = bank.getMBC(customer0);
  	assertTrue("Manager can assign customer to teller", manager.pickAndExecuteAnAction());
  	
  	assertTrue("MockBankCustomer will go to teller", mbc.pickAndExecuteAnAction());
  	mbc.msgAnimationFinishedGoToTeller();
  	assertNotSame(mbc.teller, null);
  	assertTrue("Finished simulation to teller so bank customer should deposit cash", mbc.pickAndExecuteAnAction());
  	assertTrue("Teller0 will finally open the account", teller0.pickAndExecuteAnAction());
  	assertTrue("Spawned bank customer will leave bank", mbc.pickAndExecuteAnAction());
  	assertTrue("Teller will tell manager that he is free", teller0.pickAndExecuteAnAction());
  	assertTrue("Manager will pass this info to the bank", manager.pickAndExecuteAnAction());
  	assertTrue("Person will generate new balance", customer0.pickAndExecuteAnAction());
  	assertEquals(customer0.balance, 0.00);
  	System.out.println(customer0.loanAmt);
  	assertEquals(customer0.loanAmt, 25.00);
  	assertEquals(customer0.loanTime, -1); //1 day went by since the customer should have paid off his loan
  	assertEquals(customer0.accounts.size(), 1);
  	
  	customer0.msgArrive(1, 25.00);   //the customer is in trouble and needs another loan, requests $25.00
  	assertTrue("Customer0 has arrived", customer0.pickAndExecuteAnAction());
  	assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("New Bank Customer"));
      mbc = bank.getMBC(customer0);
  	assertTrue("Manager can assign customer to teller", manager.pickAndExecuteAnAction());
  	
  	assertTrue("MockBankCustomer will go to teller", mbc.pickAndExecuteAnAction());
  	mbc.msgAnimationFinishedGoToTeller();
  	assertNotSame(mbc.teller, null);
  	assertTrue("Finished simulation to teller so bank customer should deposit cash", mbc.pickAndExecuteAnAction());
  	assertTrue("Teller0 will finally open the account", teller0.pickAndExecuteAnAction());
  	assertTrue("Spawned bank customer will leave bank", mbc.pickAndExecuteAnAction());
  	assertTrue("Teller will tell manager that he is free", teller0.pickAndExecuteAnAction());
  	assertTrue("Manager will pass this info to the bank", manager.pickAndExecuteAnAction());
  	assertTrue("Person will generate new balance", customer0.pickAndExecuteAnAction());
  	assertEquals(customer0.balance, 0.00);
  	System.out.println(customer0.loanAmt);
  	assertEquals(customer0.loanAmt, 75.00); //unfortunately with interest his new loan has been denied and his old one jumped by 2*25=50 for missing another day of payment
  	assertEquals(customer0.loanTime, -2); //2 days have gone by by since the customer should have paid off his loan
  	assertEquals(customer0.accounts.size(), 1);
  	
  	customer0.setBalance(200.00); //the customer got a generous bonus and can finally pay off the loan
  	
  	customer0.msgArrive(0, 150.00); //unfortunately there is interest so he still has an exponential loan, should create $0 balance, $150 loan
  	assertTrue("Customer0 has arrived", customer0.pickAndExecuteAnAction());
  	assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("New Bank Customer"));
      mbc = bank.getMBC(customer0);
  	assertTrue("Manager can assign customer to teller", manager.pickAndExecuteAnAction());
  	
  	assertTrue("MockBankCustomer will go to teller", mbc.pickAndExecuteAnAction());
  	mbc.msgAnimationFinishedGoToTeller();
  	assertNotSame(mbc.teller, null);
  	assertTrue("Finished simulation to teller so bank customer should deposit cash", mbc.pickAndExecuteAnAction());
  	assertTrue("Teller0 will finally open the account", teller0.pickAndExecuteAnAction());
  	assertTrue("Spawned bank customer will leave bank", mbc.pickAndExecuteAnAction());
  	assertTrue("Teller will tell manager that he is free", teller0.pickAndExecuteAnAction());
  	assertTrue("Manager will pass this info to the bank", manager.pickAndExecuteAnAction());
  	assertTrue("Person will generate new balance", customer0.pickAndExecuteAnAction());
  	assertEquals(customer0.balance, 50.00);
  	System.out.println(customer0.loanAmt);
  	assertEquals(customer0.loanAmt, 0.00); //loan is finally cleared
  	assertEquals(customer0.loanTime, 0); //loan time is finally 0
  	assertEquals(customer0.accounts.size(), 1);
    }

}
