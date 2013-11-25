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

    private Vector<MockPerson> customers = new Vector<MockPerson>();
	private Map<BankCustomer, MockPerson> spawns = new HashMap<BankCustomer, MockPerson>();


    private SimCityGui gui; //reference to main gui
    
    private MockManager manager;

    public MockBank(SimCityGui gui) {
        this.gui = gui;
    }
    
    public void msgRequestAccount(MockPerson person, double balance, boolean present){
    	BankCustomer bca = createBankCustomer(person, present);
    	manager.msgRequestAccount(bca, balance);
    }
    
    public void msgRequestDeposit(MockPerson person, int accountNum, double reqAmt, boolean present){
    	BankCustomer bca = createBankCustomer(person, present);
    	manager.msgRequestDeposit(bca, accountNum, reqAmt);
    }
    
    public void msgRequestWithdrawal(MockPerson person, int accountNum, double reqAmt, boolean present){
    	BankCustomer bca = createBankCustomer(person, present);
    	manager.msgRequestWithdrawal(bca, accountNum, reqAmt);
    }
	
	public void msgLeave(BankCustomer bc, int accountNum, double change, double loanAmt, int loanTime){
		MockPerson person = (MockPerson)spawns.get(bc);
		System.out.println(bc.toString());
		person.msgLeave(accountNum, change, loanAmt, loanTime);
	}
    
    public BankCustomer createBankCustomer(MockPerson person, boolean present){
    	 customers.add(person);
   	  	 MockBankCustomer bca = new MockBankCustomer(person.getName(), manager, gui);
    	 manager.msgCustomerHere(bca);
    	 spawns.put(bca, person);
    	 return bca;
    }
    
    public void setManager(MockManager m){
    	manager = m;
    }
}