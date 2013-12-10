package bank.test.mock;

import simcity.gui.SimCityGui;
import simcity.interfaces.Person;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import bank.BankCustomerAgent;
import bank.gui.BankCustomerGui;
import bank.interfaces.BankCustomer;

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
    
    public void msgThief(MockPerson person, double reqAmt, boolean present){
    	MockBankCustomer bca = createBankCustomer(person, present, true);
    	manager.msgThief(bca, reqAmt);
    }
    
    public void msgRequestAccount(MockPerson person, double balance, boolean present){
    	log.add(new LoggedEvent("bank received new account request"));
    	MockBankCustomer bca = createBankCustomer(person, present, false);
    	manager.msgRequestAccount(bca, balance);
    }
    
    public void msgRequestDeposit(MockPerson person, int accountNum, double reqAmt, boolean present){
    	MockBankCustomer bca = createBankCustomer(person, present, false);
    	manager.msgRequestDeposit(bca, accountNum, reqAmt);
    }
    
    public void msgRequestWithdrawal(MockPerson person, int accountNum, double reqAmt, boolean present){
    	MockBankCustomer bca = createBankCustomer(person, present, false);
    	manager.msgRequestWithdrawal(bca, accountNum, reqAmt);
    }
	
	public void msgLeave(MockBankCustomer bc, int accountNum, double change, double loanAmt, int loanTime){
		MockPerson person = (MockPerson)spawns.get(bc);
		person.msgLeave(accountNum, change, loanAmt, loanTime);
	}
    
    public MockBankCustomer createBankCustomer(MockPerson person, boolean present, boolean isThief){
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