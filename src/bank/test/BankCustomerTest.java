package bank.test;



//import bank.customerAgent.customerBillState;
//import bank.WaiterAgent.Bill;


import bank.test.mock.MockBank;
import bank.test.mock.MockBankCustomer;
import bank.test.mock.MockManager;
import bank.test.mock.MockTeller;
import bank.test.mock.MockPerson;
import junit.framework.*;

/**
 * 
 * This class is a JUnit test class to unit test the customerAgent's basic interaction
 * with waiters, customers, and the host.
 * It is provided as an example to students in CS201 for their unit testing lab.
 *
 * Assumptions:
 * 1) Person will never say they deposited more money than they actually did
 * 2) A teller will only service one person per account per time. The person will leave the bank after they
 * have either opened an account, deposited or withdrew cash.
 * 3) Loans automatically occur when a person withdraws more money in the account than they have. Each time
 * they revisit the bank for the SAME account, their time to pay back the loan decreases. Customers can take
 * out multiple loans, but their time to pay them back won't reset. If they fail to pay back the loan in the
 * number of visits they won't be able to take out another loan. Additionally they're loan will grow by 
 * increasing interest. After the first day of missing a loan they will owe an additional 25 dollars, then
 * an additional 50 for the second day and so on. There are steep fines for missing payment on loans.
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
	 * This tests the customer under very simple terms: opening an account and having the correct amount of cash at the end
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
	 * This tests the customer under very simple terms: opening an account, depositing cash, and having the correct amount of cash at the end
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
 * This tests the customer under very simple terms: opening an account, withdrawing cash, and having the correct amount of cash at the end
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
 * This tests the customer under very simple terms: opening an account, withdrawing cash until the person has a loan
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
  * This tests the customer under very simple terms: opening an account and paying back a loan
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
   * This tests the customer under more complex terms: making a loan and then adding to the loan later
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
   * This tests the customer under more complex terms: opening an account and failing to pay back a loan after 3 bank visits (loan time)
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
  	assertEquals(customer0.loanAmt, 0.00); //loan is finally cleared
  	assertEquals(customer0.loanTime, 0); //loan time is finally 0
  	assertEquals(customer0.accounts.size(), 1);
    }
   
	/**
	 * This tests the customer under very simple terms: opening two accounts
	 */
	public void testBankOpenTwoAccountsScenario(){
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
		
		customer0.msgArrive(-1, 25.00); //hack for mock, 0 for deposit, 1 for withdraw, any other num for new acc or no num
		customer0.setBank(bank);
		assertEquals(customer0.accounts.size(), 1);
		assertTrue("Customer0 has arrived", customer0.pickAndExecuteAnAction());
		assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("New Bank Customer"));
	    mbc = bank.getMBC(customer0);
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
		assertEquals(customer0.balance, 0.00);
		assertEquals(customer0.accounts.size(), 2);
		assertEquals(customer0.getBalance(0), 25.00);
		assertEquals(customer0.getBalance(1), 25.00);
}
	
	/**
	 * This tests the customer under very simple terms- opening two accounts and depositing his money in the appropriate accounts
	 */
	public void testBankOpenTwoAccountsAndDepositScenario(){
		customer0 = new MockPerson("Person");
		customer0.setBalance(100.00);
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
		assertEquals(customer0.balance, 75.0);
		assertEquals(customer0.accounts.size(), 1);
		
		customer0.msgArrive(-1, 25.00); //hack for mock, 0 for deposit, 1 for withdraw, any other num for new acc or no num
		customer0.setBank(bank);
		assertEquals(customer0.accounts.size(), 1);
		assertTrue("Customer0 has arrived", customer0.pickAndExecuteAnAction());
		assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("New Bank Customer"));
	    mbc = bank.getMBC(customer0);
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
		assertEquals(customer0.balance, 50.00);
		assertEquals(customer0.accounts.size(), 2);
		
		customer0.accountChoice = 1; //customer picks to use second account
		customer0.msgArrive(0, 40.00); //now depositing extra cash
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
	  	assertEquals(customer0.balance, 10.00); //on hand balance should be $10.00 left
	  	assertEquals(customer0.loanAmt, 0.00);
	  	assertEquals(customer0.loanTime, 0); 
	  	assertEquals(customer0.accounts.size(), 2);
	  	
		customer0.accountChoice = 0; //customer picks to use first account
		customer0.msgArrive(0, 10.00); //now depositing all extra cash
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
	  	assertEquals(customer0.balance, 0.00); //on hand balance should be $0.00 left
	  	assertEquals(customer0.loanAmt, 0.00);
	  	assertEquals(customer0.loanTime, 0); 
	  	assertEquals(customer0.accounts.size(), 2);
	  	
	  	assertEquals(customer0.getBalance(0), 35.00); //totals should be how they were deposited plus initial amounts
	  	assertEquals(customer0.getBalance(1), 65.00);
	  	
		customer0.accountChoice = 1; //customer picks to use second account
		customer0.msgArrive(1, 40.00); //now withdrawing cash (only works in this account, would be a loan in other account)
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
	  	assertEquals(customer0.balance, 40.00); //on hand balance should be $40.00 after withdrawal
	  	assertEquals(customer0.loanAmt, 0.00);
	  	assertEquals(customer0.loanTime, 0); 
	  	assertEquals(customer0.accounts.size(), 2);
	  	
	  	assertEquals(customer0.getBalance(0), 35.00); //total should be same as above
	  	assertEquals(customer0.getBalance(1), 25.00); //total should be 40.00 less than above
}
	
	/**
	 * This tests the customer under more complicated terms, opening two accounts and having a loan in one of the accounts
	 */ 
	public void testBankOpenTwoAccountsAndLoanScenario(){
		customer0 = new MockPerson("Person");
		customer0.setBalance(100.00);
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
		assertEquals(customer0.balance, 75.0);
		assertEquals(customer0.accounts.size(), 1);
		
		customer0.msgArrive(-1, 25.00); //hack for mock, 0 for deposit, 1 for withdraw, any other num for new acc or no num
		customer0.setBank(bank);
		assertEquals(customer0.accounts.size(), 1);
		assertTrue("Customer0 has arrived", customer0.pickAndExecuteAnAction());
		assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("New Bank Customer"));
	    mbc = bank.getMBC(customer0);
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
		assertEquals(customer0.balance, 50.00);
		assertEquals(customer0.accounts.size(), 2);
		
		customer0.accountChoice = 1; //customer picks to use second account
		customer0.msgArrive(0, 40.00); //now depositing extra cash
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
	  	assertEquals(customer0.balance, 10.00); //on hand balance should be $10.00 left
	  	assertEquals(customer0.loanAmt, 0.00);
	  	assertEquals(customer0.loanTime, 0); 
	  	assertEquals(customer0.accounts.size(), 2);
	  	
		customer0.accountChoice = 0; //customer picks to use first account
		customer0.msgArrive(0, 10.00); //now depositing all extra cash
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
	  	assertEquals(customer0.balance, 0.00); //on hand balance should be $0.00 left
	  	assertEquals(customer0.loanAmt, 0.00);
	  	assertEquals(customer0.loanTime, 0); 
	  	assertEquals(customer0.accounts.size(), 2);
	  	
	  	assertEquals(customer0.getBalance(0), 35.00); //totals should be how they were deposited plus initial amounts
	  	assertEquals(customer0.getBalance(1), 65.00);
	  		  	
		customer0.accountChoice = 0; //customer picks to use first account
		customer0.msgArrive(1, 40.00); //now withdrawing cash (since the balance is only 10.00, there will be a 15.00 loan)
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
	  	assertEquals(customer0.balance, 40.00); //on hand balance should be $40.00 after withdrawal
	  	
	  	assertEquals(customer0.getBalance(0), 0.00); //totals should be how they were deposited plus initial amounts
	  	assertEquals(customer0.getBalance(1), 65.00);
	  	
	  	assertEquals(customer0.getLoanAmount(0), 5.00); //see above
	  	assertEquals(customer0.getLoanTime(0), 3); //loan is now 3 days
	  	assertEquals(customer0.accounts.size(), 2);
} 
	
	/**
	 * This tests the customer under more complicated terms, opening two accounts and paying off a loan with another account's balance
	 */ 
	public void testBankPayLoanFromOtherAccountScenario(){
		customer0 = new MockPerson("Person");
		customer0.setBalance(100.00);
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
		assertEquals(customer0.balance, 75.0);
		assertEquals(customer0.accounts.size(), 1);
		
		customer0.msgArrive(-1, 25.00); //hack for mock, 0 for deposit, 1 for withdraw, any other num for new acc or no num
		customer0.setBank(bank);
		assertEquals(customer0.accounts.size(), 1);
		assertTrue("Customer0 has arrived", customer0.pickAndExecuteAnAction());
		assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("New Bank Customer"));
	    mbc = bank.getMBC(customer0);
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
		assertEquals(customer0.balance, 50.00);
		assertEquals(customer0.accounts.size(), 2);
		
		customer0.accountChoice = 1; //customer picks to use second account
		customer0.msgArrive(0, 40.00); //now depositing extra cash
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
	  	assertEquals(customer0.balance, 10.00); //on hand balance should be $10.00 left
	  	assertEquals(customer0.loanAmt, 0.00);
	  	assertEquals(customer0.loanTime, 0); 
	  	assertEquals(customer0.accounts.size(), 2);
	  	
		customer0.accountChoice = 0; //customer picks to use first account
		customer0.msgArrive(0, 10.00); //now depositing all extra cash
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
	  	assertEquals(customer0.balance, 0.00); //on hand balance should be $0.00 left
	  	assertEquals(customer0.loanAmt, 0.00);
	  	assertEquals(customer0.loanTime, 0); 
	  	assertEquals(customer0.accounts.size(), 2);
	  	
	  	assertEquals(customer0.getBalance(0), 35.00); //totals should be how they were deposited plus initial amounts
	  	assertEquals(customer0.getBalance(1), 65.00);
	  		  	
		customer0.accountChoice = 0; //customer picks to use first account
		customer0.msgArrive(1, 40.00); //now withdrawing cash (since the balance is only 10.00, there will be a 15.00 loan)
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
	  	assertEquals(customer0.balance, 40.00); //on hand balance should be $40.00 after withdrawal
	  		  	
	  	assertEquals(customer0.getBalance(0), 0.00); //totals should be how they were deposited plus initial amounts
	  	assertEquals(customer0.getBalance(1), 65.00);
	  	
	  	assertEquals(customer0.getLoanAmount(0), 5.00); //see above
	  	assertEquals(customer0.getLoanTime(0), 3); //loan is now 3 days
	  	assertEquals(customer0.accounts.size(), 2);
	  	
	  	customer0.setBalance(0); //customer lost its on hand cash
	  	
		customer0.accountChoice = 1; //customer picks to use second account
		customer0.msgArrive(1, 15.00); //now withdrawing cash from other account (since the balance is only 65.00, there will be a 50.00 left)
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
	  	assertEquals(customer0.balance, 15.00); //on hand balance should be $25.00 after withdrawal
	  		  	
	  	assertEquals(customer0.getBalance(0), 0.00); //totals should be how they were deposited plus initial amounts
	  	assertEquals(customer0.getBalance(1), 50.00);
	  	
	  	assertEquals(customer0.getLoanAmount(0), 5.00); //the loan amounts don't change, as the other account isn't being used
	  	assertEquals(customer0.getLoanTime(0), 3); //loan is still 3 days, account hasn't been touched so no interest
	  	assertEquals(customer0.accounts.size(), 2);
	  	
		customer0.accountChoice = 0; //customer picks to use first account
		customer0.msgArrive(0, 10.00); //now paying back loan on first account
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
	  	assertEquals(customer0.balance, 5.00); //on hand balance should be $5.00 after withdrawal, (15-10 = 5)
	    
	  	assertEquals(customer0.getBalance(0), 5.00); //total should be $5.00 and loan should be cleared (-5.00 + 10.00 = 5.00)
	  	assertEquals(customer0.getBalance(1), 50.00);
	  	
	  	assertEquals(customer0.getLoanAmount(0), 0.00); //the loan should be cleared
	  	assertEquals(customer0.getLoanTime(0), 0); //loan time should be 0 as well
	  	assertEquals(customer0.getLoanAmount(1), 0.00); //other account shouldn't have a loan either
	  	assertEquals(customer0.accounts.size(), 2);
} 
	
	/**
	 * This tests the customer under more complicated terms, having to pay off two separate loans in two accounts
	 */ 
	public void testBankTwoAccountsTwoLoans(){
		customer0 = new MockPerson("Person");
		customer0.setBalance(100.00);
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
		assertEquals(customer0.balance, 75.0);
		assertEquals(customer0.accounts.size(), 1);
		
		customer0.msgArrive(-1, 25.00); //hack for mock, 0 for deposit, 1 for withdraw, any other num for new acc or no num
		customer0.setBank(bank);
		assertEquals(customer0.accounts.size(), 1);
		assertTrue("Customer0 has arrived", customer0.pickAndExecuteAnAction());
		assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("New Bank Customer"));
	    mbc = bank.getMBC(customer0);
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
		assertEquals(customer0.balance, 50.00);
		assertEquals(customer0.accounts.size(), 2);
		
		customer0.accountChoice = 1; //customer picks to use second account
		customer0.msgArrive(0, 40.00); //now depositing extra cash
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
	  	assertEquals(customer0.balance, 10.00); //on hand balance should be $10.00 left
	  	assertEquals(customer0.loanAmt, 0.00);
	  	assertEquals(customer0.loanTime, 0); 
	  	assertEquals(customer0.accounts.size(), 2);
	  	
		customer0.accountChoice = 0; //customer picks to use first account
		customer0.msgArrive(0, 10.00); //now depositing all extra cash
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
	  	assertEquals(customer0.balance, 0.00); //on hand balance should be $0.00 left
	  	assertEquals(customer0.loanAmt, 0.00);
	  	assertEquals(customer0.loanTime, 0); 
	  	assertEquals(customer0.accounts.size(), 2);
	  	
	  	assertEquals(customer0.getBalance(0), 35.00); //totals should be how they were deposited plus initial amounts
	  	assertEquals(customer0.getBalance(1), 65.00);
	  		  	
		customer0.accountChoice = 0; //customer picks to use first account
		customer0.msgArrive(1, 50.00); //now withdrawing cash (since the balance is only 35.00, there will be an 15.00 loan)
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
	  	assertEquals(customer0.balance, 50.00); //on hand balance should be $50.00 after withdrawal
	  		  	
	  	assertEquals(customer0.getBalance(0), 0.00); //totals should be how they were deposited plus initial amounts
	  	assertEquals(customer0.getBalance(1), 65.00);
	  	
	  	assertEquals(customer0.getLoanAmount(0), 15.00); //see above
	  	assertEquals(customer0.getLoanTime(0), 3); //loan is now 3 days
	  	assertEquals(customer0.getLoanAmount(1), 0.00); //second account shouldn't have a loan yet
	  	assertEquals(customer0.getLoanTime(1), 0); //second account shouldn't have any loan time either
	  	assertEquals(customer0.accounts.size(), 2);
	  	
	  	customer0.setBalance(0.00); //customer0 spent all of its money
	  	
		customer0.accountChoice = 1; //customer picks to use first account
		customer0.msgArrive(1, 100.00); //now withdrawing cash (since the balance is only 65.00, there will be an 35.00 loan)
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
	  	assertEquals(customer0.balance, 100.00); //on hand balance should be $50.00 after withdrawal
	  		  	
	  	assertEquals(customer0.getBalance(0), 0.00); //totals should be how they were deposited plus initial amounts
	  	assertEquals(customer0.getBalance(1), 0.00);
	  	
	  	assertEquals(customer0.getLoanAmount(0), 15.00); //same as above, account wasn't accessed
	  	assertEquals(customer0.getLoanTime(0), 3); //loan is still 3 days
	  	assertEquals(customer0.getLoanAmount(1), 35.00); //second account now has a loan
	  	assertEquals(customer0.getLoanTime(1), 3); //second account now has a timer
	  	assertEquals(customer0.accounts.size(), 2);
	  	
	  	customer0.setBalance(0); //customer spends all of its money again
	  	
	  	for(int i = 0; i<4; i++){ //customer goes to the bank depositing nothing in account 0 until the loan expires (the amount deposited doesn't affect anything as long as it isn't greater than the loan)
		customer0.accountChoice = 0; 
		customer0.msgArrive(0, 0.00); 
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
	  	assertEquals(customer0.balance, 0.00); //on hand balance should still be 0
	  	assertEquals(customer0.getBalance(0), 0.00); //balance of accounts should still be 0
	  	assertEquals(customer0.getBalance(1), 0.00);
	  }
	  	
	  	for(int i = 0; i<4; i++){ //customer goes to the bank depositing nothing in account 1 until the loan expires (the amount deposited doesn't affect anything as long as it isn't greater than the loan)
		customer0.accountChoice = 1; 
		customer0.msgArrive(0, 0.00); 
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
	  	assertEquals(customer0.balance, 0.00); //on hand balance should be $50.00 after withdrawal
	  		  	
	  	assertEquals(customer0.getBalance(0), 0.00); //totals should be how they were deposited plus initial amounts
	  	assertEquals(customer0.getBalance(1), 0.00);
	  }
	  	
	  	assertEquals(customer0.getLoanAmount(0), 40.00); //same as above, account wasn't accessed
	  	assertEquals(customer0.getLoanTime(0), -1); //loan is still 3 days
	  	assertEquals(customer0.getLoanAmount(1), 60.00); //second account now has a loan
	  	assertEquals(customer0.getLoanTime(1), -1); //second account now has a timer
	  	assertEquals(customer0.accounts.size(), 2);
	  	
	  	customer0.setBalance(250.00); //customer wins the lottery
	  	customer0.accountChoice = 0; 
		customer0.msgArrive(0, 100.00); //pays back first loan, should 40.00+25*2=90.00 now
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
	  	assertEquals(customer0.balance, 150.00); //on hand balance should be $50.00 after withdrawal
	  	
	  	assertEquals(customer0.getLoanAmount(0), 0.00); //first loan is cleared
	  	assertEquals(customer0.getLoanTime(0), 0); //loan time is cleared as well
	  	System.out.println(customer0.getBalance(0));
	  	assertEquals(customer0.getBalance(0), 10.00); //balance should be 10.00, after 90/100 went towards the loan
	  	assertEquals(customer0.getBalance(1), 0.00);
	  	
	  	customer0.accountChoice = 1; 
		customer0.msgArrive(0, 125.00); //pays back second loan, should 60.00+25*2=110.00 now
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
	  	assertEquals(customer0.balance, 25.00); //on hand balance should be $50.00 after withdrawal
	  	
	  	assertEquals(customer0.getLoanAmount(1), 0.00); //second loan is cleared
	  	assertEquals(customer0.getLoanTime(1), 0); //loan time is cleared as well
	  	assertEquals(customer0.getBalance(0), 10.00);
	  	assertEquals(customer0.getBalance(1), 15.00); //balance should be 15.00 as 110.00/125.00 went towards the loan	  	
} 
	
	


}
