package bank.test.mock;

import bank.ManagerAgent;
import bank.interfaces.BankCustomer;
import simcity.gui.SimCityGui;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Panel in frame that contains all the bank information,
 * including host, cook, waiters, and customers.
 */
public class MockBank {

    //Host, cook, waiters and customers
	public EventLog log;
    private Vector<MockPerson> customers = new Vector<MockPerson>();
	private Map<MockBankCustomer, MockPerson> spawns = new HashMap<MockBankCustomer, MockPerson>();
	private Map<MockPerson, MockBankCustomer> rSpawns = new HashMap<MockPerson, MockBankCustomer>();


    private SimCityGui gui; //reference to main gui
    
    private MockManager manager;

    public MockBank(SimCityGui gui) {
    	log = new EventLog();
        this.gui = gui;
    }
    
    public void msgRequestAccount(MockPerson person, double balance, boolean present){
    	log.add(new LoggedEvent("bank received new account request"));
    	MockBankCustomer bca = createBankCustomer(person, present);
    	manager.msgRequestAccount(bca, balance);
    }
    
    public void msgRequestDeposit(MockPerson person, int accountNum, double reqAmt, boolean present){
    	MockBankCustomer bca = createBankCustomer(person, present);
    	manager.msgRequestDeposit(bca, accountNum, reqAmt);
    }
    
    public void msgRequestWithdrawal(MockPerson person, int accountNum, double reqAmt, boolean present){
    	MockBankCustomer bca = createBankCustomer(person, present);
    	manager.msgRequestWithdrawal(bca, accountNum, reqAmt);
    }
	
	public void msgLeave(MockBankCustomer bc, int accountNum, double change, double loanAmt, int loanTime){
		MockPerson person = (MockPerson)spawns.get(bc);
		person.msgLeave(accountNum, change, loanAmt, loanTime);
	}
    
    public MockBankCustomer createBankCustomer(MockPerson person, boolean present){
    	 customers.add(person);
   	  	 MockBankCustomer bca = new MockBankCustomer(person.getName(), manager, gui);
    	 manager.msgCustomerHere(bca);
    	 spawns.put(bca, person);
    	 rSpawns.put(person, bca);
    	 return bca;
    }
    
    public void setManager(MockManager m){
    	manager = m;
    }
    public MockBankCustomer getMBC(MockPerson mp){
		MockBankCustomer mbc = (MockBankCustomer)rSpawns.get(mp);
		return mbc;
    }
}