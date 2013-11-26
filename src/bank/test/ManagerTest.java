package bank.test;

import junit.framework.TestCase;
import bank.test.mock.MockBank;
import bank.test.mock.MockBankCustomer;
import bank.test.mock.MockManager;
import bank.test.mock.MockPerson;
import bank.test.mock.MockTeller;

/**
 * @author Dylan Eirinberg
 */


public class ManagerTest extends TestCase
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
	MockPerson customer3;
	MockPerson customer4;
	
	
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
    	manager.addTeller(teller1);
        teller2 = new MockTeller("Teller2");	
        teller2.setManager(manager);
    	manager.addTeller(teller2);
        teller3 = new MockTeller("Teller3");	
        teller3.setManager(manager);
    	manager.addTeller(teller3);
    	
	}	
	/**
	 * this is a simple test case to assign a teller to a person
	 */
	public void testBankSimpleAssign(){
		customer0 = new MockPerson("Person");
		customer0.setBalance(25.00); //creating a simple customer with 25.00 balance
		customer0.msgArrive(-1, 25.00); 
		customer0.setBank(bank);
		assertEquals(customer0.accounts.size(), 0);
		assertTrue("Customer0 has arrived", customer0.pickAndExecuteAnAction()); //tells manager it is here
		MockBankCustomer mbc = bank.getMBC(customer0);
		assertTrue("Manager can assign customer to teller", manager.pickAndExecuteAnAction());
		assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("ASSIGNING AND NEW ACCOUNT"));
		manager.getCustomerState(mbc); //spawns log message which is checked
		assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("CUSTOMER STATE: busy")); //customer should be busy
		manager.getTellerState(mbc.teller); //spawns log message which is checked
		assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("TELLER STATE: busy")); //teller should be busy
		assertTrue(mbc.log.getLastLoggedEvent().toString(), mbc.log.containsString("REQ NEW ACCOUNT")); //it is officially assigned
		assertSame(mbc.teller, teller0);
	}
	/**
	 * this is a simple test case to assign two tellers to two customers in progression
	 */
	public void testBankAssignProgressionTwoCustomers(){
		customer0 = new MockPerson("Person");
		customer0.setBalance(25.00); //creating a simple customer with 25.00 balance
		customer0.msgArrive(-1, 25.00); 
		customer0.setBank(bank);
	
		assertTrue("Customer0 has arrived", customer0.pickAndExecuteAnAction()); //tells manager it is here
		MockBankCustomer mbc = bank.getMBC(customer0);
		assertTrue("Manager can assign customer to teller", manager.pickAndExecuteAnAction());
		assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("ASSIGNING AND NEW ACCOUNT"));
		manager.getCustomerState(mbc); //spawns log message which is checked
		assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("CUSTOMER STATE: busy")); //customer should be busy
		manager.getTellerState(mbc.teller); //spawns log message which is checked
		assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("TELLER STATE: busy")); //teller should be busy
		assertTrue(mbc.log.getLastLoggedEvent().toString(), mbc.log.containsString("REQ NEW ACCOUNT")); //it is officially assigned
		assertSame(mbc.teller, teller0); //assigned to first teller

		assertTrue("MockBankCustomer will go to teller", mbc.pickAndExecuteAnAction());
		mbc.msgAnimationFinishedGoToTeller();
		assertNotSame(mbc.teller, null);
		assertTrue("Finished simulation to teller so bank customer should open account", mbc.pickAndExecuteAnAction());
		assertTrue("Teller0 will finally open the account", teller0.pickAndExecuteAnAction());
	  	assertTrue("Spawned bank customer will leave bank", mbc.pickAndExecuteAnAction());
	  	mbc.msgAnimationFinishedLeavingBank();
	  	assertTrue("Spawned bank customer has left bank", mbc.pickAndExecuteAnAction());
		assertTrue("Teller will tell manager that he is free", teller0.pickAndExecuteAnAction());
		assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("TELLER FREE")); //freed up
		assertTrue("Manager will pass this info to the bank", manager.pickAndExecuteAnAction());
		assertTrue(customer0.log.getLastLoggedEvent().toString(), customer0.log.containsString("TRANSACTION COMPLETE"));
		
		customer1 = new MockPerson("Person1");
		customer1.setBalance(25.00); //creating a new customer with 25.00 balance
		customer1.msgArrive(-1, 25.00); 
		customer1.setBank(bank);
	
		assertTrue("Customer1 has arrived", customer1.pickAndExecuteAnAction()); //tells manager it is here
	    mbc = bank.getMBC(customer1);
		assertTrue("Manager can assign customer to teller", manager.pickAndExecuteAnAction());
		assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("ASSIGNING AND NEW ACCOUNT"));
		manager.getCustomerState(mbc); //spawns log message which is checked
		assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("CUSTOMER STATE: busy")); //customer should be busy
		manager.getTellerState(mbc.teller); //spawns log message which is checked
		assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("TELLER STATE: busy")); //teller should be busy
		assertTrue(mbc.log.getLastLoggedEvent().toString(), mbc.log.containsString("REQ NEW ACCOUNT")); //it is officially assigned
		assertSame(mbc.teller, teller0); //assigned to first teller again, so it was successfully cleared
	}
	/**
	 * this is a simple test case to assign two tellers to two different tellers
	 */
	public void testBankAssignSameTimeTwoCustomers(){
		customer0 = new MockPerson("Person");
		customer0.setBalance(25.00); //creating a simple customer with 25.00 balance
		customer0.msgArrive(-1, 25.00); 
		customer0.setBank(bank);
	
		assertTrue("Customer0 has arrived", customer0.pickAndExecuteAnAction()); //tells manager it is here
		MockBankCustomer mbc = bank.getMBC(customer0);
		assertTrue("Manager can assign customer to teller", manager.pickAndExecuteAnAction());
		assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("ASSIGNING AND NEW ACCOUNT"));
		manager.getCustomerState(mbc); //spawns log message which is checked
		assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("CUSTOMER STATE: busy")); //customer should be busy
		manager.getTellerState(mbc.teller); //spawns log message which is checked
		assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("TELLER STATE: busy")); //teller should be busy
		assertTrue(mbc.log.getLastLoggedEvent().toString(), mbc.log.containsString("REQ NEW ACCOUNT")); //it is officially assigned
		assertSame(mbc.teller, teller0); //assigned to first teller
		
		customer1 = new MockPerson("Person1");
		customer1.setBalance(25.00); //creating a new customer with 25.00 balance
		customer1.msgArrive(-1, 25.00); 
		customer1.setBank(bank);
	
		assertTrue("Customer1 has arrived", customer1.pickAndExecuteAnAction()); //tells manager it is here
	    mbc = bank.getMBC(customer1);
		assertTrue("Manager can assign customer to teller", manager.pickAndExecuteAnAction());
		assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("ASSIGNING AND NEW ACCOUNT"));
		manager.getCustomerState(mbc); //spawns log message which is checked
		assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("CUSTOMER STATE: busy")); //customer should be busy
		manager.getTellerState(mbc.teller); //spawns log message which is checked
		assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("TELLER STATE: busy")); //teller should be busy
		assertTrue(mbc.log.getLastLoggedEvent().toString(), mbc.log.containsString("REQ NEW ACCOUNT")); //it is officially assigned
		assertSame(mbc.teller, teller1); //assigned to second teller again, since first one was busy
		
		mbc = bank.getMBC(customer0); //bank customer reset to first person
		assertTrue("MockBankCustomer will go to teller", mbc.pickAndExecuteAnAction());
		mbc.msgAnimationFinishedGoToTeller();
		assertNotSame(mbc.teller, null);
		assertTrue("Finished simulation to teller so bank customer should open account", mbc.pickAndExecuteAnAction());
		assertTrue("Teller0 will finally open the account", teller0.pickAndExecuteAnAction());
	  	assertTrue("Spawned bank customer will leave bank", mbc.pickAndExecuteAnAction());
	  	mbc.msgAnimationFinishedLeavingBank();
	  	assertTrue("Spawned bank customer has left bank", mbc.pickAndExecuteAnAction());
		assertTrue("Teller will tell manager that he is free", teller0.pickAndExecuteAnAction());
		assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("TELLER FREE")); //freed up
		assertTrue("Manager will pass this info to the bank", manager.pickAndExecuteAnAction());
		assertTrue(customer0.log.getLastLoggedEvent().toString(), customer0.log.containsString("TRANSACTION COMPLETE"));
	}
	/**
	 * this is a complex case to make all 4 tellers busy and have a fifth customer enter and wait
	 */
	public void testBankAllTellersBusy(){
		customer0 = new MockPerson("Person");
		customer0.setBalance(25.00); //creating a simple customer with 25.00 balance
		customer0.msgArrive(-1, 25.00); 
		customer0.setBank(bank);
	
		assertTrue("Customer0 has arrived", customer0.pickAndExecuteAnAction()); //tells manager it is here
		MockBankCustomer mbc = bank.getMBC(customer0);
		assertTrue("Manager can assign customer to teller", manager.pickAndExecuteAnAction());
		assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("ASSIGNING AND NEW ACCOUNT"));
		manager.getCustomerState(mbc); //spawns log message which is checked
		assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("CUSTOMER STATE: busy")); //customer should be busy
		manager.getTellerState(mbc.teller); //spawns log message which is checked
		assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("TELLER STATE: busy")); //teller should be busy
		assertTrue(mbc.log.getLastLoggedEvent().toString(), mbc.log.containsString("REQ NEW ACCOUNT")); //it is officially assigned
		assertSame(mbc.teller, teller0); //assigned to first teller
		
		customer1 = new MockPerson("Person1");
		customer1.setBalance(25.00); //creating a new customer with 25.00 balance
		customer1.msgArrive(-1, 25.00); 
		customer1.setBank(bank);
	
		assertTrue("Customer1 has arrived", customer1.pickAndExecuteAnAction()); //tells manager it is here
	    mbc = bank.getMBC(customer1);
		assertTrue("Manager can assign customer to teller", manager.pickAndExecuteAnAction());
		assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("ASSIGNING AND NEW ACCOUNT"));
		manager.getCustomerState(mbc); //spawns log message which is checked
		assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("CUSTOMER STATE: busy")); //customer should be busy
		manager.getTellerState(mbc.teller); //spawns log message which is checked
		assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("TELLER STATE: busy")); //teller should be busy
		assertTrue(mbc.log.getLastLoggedEvent().toString(), mbc.log.containsString("REQ NEW ACCOUNT")); //it is officially assigned
		assertSame(mbc.teller, teller1); //assigned to second teller again, since first one was busy
		
		customer2 = new MockPerson("Person2");
		customer2.setBalance(25.00); //creating a new customer with 25.00 balance
		customer2.msgArrive(-1, 25.00); 
		customer2.setBank(bank);
	
		assertTrue("customer2 has arrived", customer2.pickAndExecuteAnAction()); //tells manager it is here
	    mbc = bank.getMBC(customer2);
		assertTrue("Manager can assign customer to teller", manager.pickAndExecuteAnAction());
		assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("ASSIGNING AND NEW ACCOUNT"));
		manager.getCustomerState(mbc); //spawns log message which is checked
		assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("CUSTOMER STATE: busy")); //customer should be busy
		manager.getTellerState(mbc.teller); //spawns log message which is checked
		assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("TELLER STATE: busy")); //teller should be busy
		assertTrue(mbc.log.getLastLoggedEvent().toString(), mbc.log.containsString("REQ NEW ACCOUNT")); //it is officially assigned
		assertSame(mbc.teller, teller2); //assigned to second teller again, since first one was busy
		
		customer3 = new MockPerson("Person3");
		customer3.setBalance(25.00); //creating a new customer with 25.00 balance
		customer3.msgArrive(-1, 25.00); 
		customer3.setBank(bank);
	
		assertTrue("customer3 has arrived", customer3.pickAndExecuteAnAction()); //tells manager it is here
	    mbc = bank.getMBC(customer3);
		assertTrue("Manager can assign customer to teller", manager.pickAndExecuteAnAction());
		assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("ASSIGNING AND NEW ACCOUNT"));
		manager.getCustomerState(mbc); //spawns log message which is checked
		assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("CUSTOMER STATE: busy")); //customer should be busy
		manager.getTellerState(mbc.teller); //spawns log message which is checked
		assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("TELLER STATE: busy")); //teller should be busy
		assertTrue(mbc.log.getLastLoggedEvent().toString(), mbc.log.containsString("REQ NEW ACCOUNT")); //it is officially assigned
		assertSame(mbc.teller, teller3); //assigned to second teller again, since first one was busy
		
		customer4 = new MockPerson("Person4");
		customer4.setBalance(25.00); //creating a new customer with 25.00 balance
		customer4.msgArrive(-1, 25.00); 
		customer4.setBank(bank);
	
		assertTrue("customer4 has arrived", customer4.pickAndExecuteAnAction()); //tells manager it is here
	    mbc = bank.getMBC(customer4);
		assertFalse("Manager can assign customer to teller", manager.pickAndExecuteAnAction()); //can't assign teller as all are busy
		assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("ASSIGNING AND NEW ACCOUNT"));
		manager.getCustomerState(mbc); //spawns log message which is checked
		assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("CUSTOMER STATE: waiting")); //customer should be waiting as all tellers are busy
		
		
		mbc = bank.getMBC(customer2); //bank customer reset to first person
		assertTrue("MockBankCustomer will go to teller", mbc.pickAndExecuteAnAction());
		mbc.msgAnimationFinishedGoToTeller();
		assertNotSame(mbc.teller, null);
		assertTrue("Finished simulation to teller so bank customer should open account", mbc.pickAndExecuteAnAction());
		assertTrue("Teller0 will finally open the account", teller2.pickAndExecuteAnAction());
	  	assertTrue("Spawned bank customer will leave bank", mbc.pickAndExecuteAnAction());
	  	mbc.msgAnimationFinishedLeavingBank();
	  	assertTrue("Spawned bank customer has left bank", mbc.pickAndExecuteAnAction());
		assertTrue("Teller will tell manager that he is free", teller2.pickAndExecuteAnAction());
		assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("TELLER FREE")); //third teller is freed up
		assertTrue("Manager will pass this info to the bank", manager.pickAndExecuteAnAction());
		
		//manager can now assign the waiting customer (4) to the free teller (2)
		mbc = bank.getMBC(customer4);
		assertTrue("Manager can assign customer to teller", manager.pickAndExecuteAnAction());
		assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("ASSIGNING AND NEW ACCOUNT"));
		manager.getCustomerState(mbc); //spawns log message which is checked
		assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("CUSTOMER STATE: busy")); //customer should be busy
		manager.getTellerState(mbc.teller); //spawns log message which is checked
		assertTrue(manager.log.getLastLoggedEvent().toString(), manager.log.containsString("TELLER STATE: busy")); //teller should be busy
		assertTrue(mbc.log.getLastLoggedEvent().toString(), mbc.log.containsString("REQ NEW ACCOUNT")); //it is officially assigned
		assertSame(mbc.teller, teller2); //assigned to third teller since it was the first freed one	
	}
}