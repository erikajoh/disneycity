package bank.gui;

import bank.BankCustomerAgent;
import bank.ManagerAgent;
import simcity.PersonAgent;
import bank.TellerAgent;
import bank.interfaces.BankCustomer;
import simcity.interfaces.Person;

import javax.swing.*;

import simcity.gui.SimCityGui;
import simcity.gui.trace.AlertLog;
import simcity.gui.trace.AlertTag;
import simcity.interfaces.Bank_Douglass;

import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Panel in frame that contains all the bank information,
 * including host, cook, waiters, and customers.
 */
public class Bank extends JPanel implements ActionListener, Bank_Douglass {

    //Host, cook, waiters and customers

	String bankName;
	
    private Vector<Person> customers = new Vector<Person>();
    private Vector<TellerAgent> tellers = new Vector<TellerAgent>();
	private Map<BankCustomer, Person> spawns = new HashMap<BankCustomer, Person>();

	private int tellerAmt;
	private boolean open;
	
    private SimCityGui gui; //reference to main gui
    
    private ManagerAgent manager;
    
    public Bank(SimCityGui gui, String bankName, int ta) {
        this.gui = gui;
        this.bankName = bankName;
        manager = new ManagerAgent("Bank", this, gui);
        manager.startThread();
        tellerAmt = ta;
        
        /*
        for(int i = 0; i<tellerAmt; i++){
        	String name = "Teller"+i;
        	TellerAgent t = new TellerAgent(name);	
    		TellerGui g = new TellerGui(t, gui, tellers.size());
    		gui.bankAniPanel.addGui(g);
    		t.setManager(manager);
    		tellers.add(t);
    		t.setGui(g);
    		manager.addTeller(t);
    		t.startThread();
        }
        */
    }
    
    public void msgThief(Person person, double reqAmt, boolean present){
    	BankCustomer bca = createBankCustomer(person, present, true);
    	manager.msgThief(bca, reqAmt);
    }
    
    public void msgRequestAccount(Person person, double reqAmt, boolean present){
    	BankCustomer bca = createBankCustomer(person, present, false);
    	manager.msgRequestAccount(bca, reqAmt);
    }
    
    public void msgRequestDeposit(Person person, int accountNum, double reqAmt, boolean present){
    	BankCustomer bca = createBankCustomer(person, present, false);
    	manager.msgRequestDeposit(bca, accountNum, reqAmt);
    }
    
    public void msgRequestWithdrawal(Person person, int accountNum, double reqAmt, boolean present){
    	//hack to force trigger thief 
    	/*
    	msgThief(person, reqAmt, present);
    	return;*/
    	BankCustomer bca = createBankCustomer(person, present, false);
    	manager.msgRequestWithdrawal(bca, accountNum, reqAmt);
    }
	
	public void msgLeave(BankCustomer bc, int accountNum, double change, double loanAmt, int loanTime){
		Person person = (Person)spawns.get(bc);
		AlertLog.getInstance().logMessage(AlertTag.BANK, "Bank", "Person leaving");
		System.out.println("NEW INFO: "+accountNum + ", "+ change + ", "+ loanAmt + ", "+loanTime);
		person.msgLeftBank(this, accountNum, change, loanAmt, loanTime);
	}
	
	public void msgClose(){
		for(TellerAgent teller : tellers){
			AlertLog.getInstance().logMessage(AlertTag.BANK, "Bank", "Bank telling teller to leave");
			teller.msgClose();
		}
	}
	
	public void msgTellerLeftBank(TellerAgent teller){
		AlertLog.getInstance().logMessage(AlertTag.BANK, "Bank", "Teller left bank");
		tellers.remove(teller);
		if(tellers.size() == 0){
			AlertLog.getInstance().logMessage(AlertTag.BANK, "Bank", "Tellers should be clear");
			open = false;
			// tell people bank is closed
			ArrayList<PersonAgent> people = gui.getSimCityPanel().getPeople();
			for(int i = 0; i < people.size(); i++)
				people.get(i).msgSetBanksOpen(false);
			tellers = null;
		}
	}
    
    public BankCustomer createBankCustomer(Person person, boolean present, boolean isThief){
    	 customers.add(person);
   	  	 BankCustomerAgent bca = new BankCustomerAgent(person.getName(), manager, gui);
	     BankCustomerGui g = new BankCustomerGui(bca, gui, present, isThief, 400, 330);
	     gui.bankAniPanel.addGui(g);// dw
	     bca.setGui(g);
	     bca.startThread();
    	 manager.msgCustomerHere(bca);
    	 spawns.put(bca, person);
    	 return bca;
    }

    public String getBankName() {
    	return bankName;
    }

    /**
     * Adds a customer or waiter to the appropriate list
     *
     * @param type indicates whether the person is a customer or waiter (later)
     * @param name name of person
     */
    public void addPerson(String type, String name) {

    	if (type.equals("Customers")) {
    		/*Person p = new Person(name);
    		p.msgArrive(-1);
    		customers.add(p);
    		p.startThread();*/
    	}
    	else if (type.equals("Tellers")) {
    		TellerAgent t = new TellerAgent(name);	
    		TellerGui g = new TellerGui(t, gui, tellers.size());
    		gui.bankAniPanel.addGui(g);
    		t.setManager(manager);
    		tellers.add(t);
    		t.setGui(g);
    		manager.addTeller(t);
    		t.startThread();
    	}
    }
    
    public void addPerson(Person person){
			AlertLog.getInstance().logMessage(AlertTag.BANK, "Bank", "ADD TELLER");
    		TellerAgent t = new TellerAgent(person, person.getName());	
    		TellerGui g = new TellerGui(t, gui, tellers.size());
    		gui.bankAniPanel.addGui(g);
    		t.setManager(manager);
    		tellers.add(t);
    		t.setGui(g);
    		manager.addTeller(t);
    		t.startThread();
			// tell people bank is open
    		if(tellers.size()==1){
    			ArrayList<PersonAgent> people = gui.getSimCityPanel().getPeople();
    			for(int i = 0; i < people.size(); i++)
    				people.get(i).msgSetBanksOpen(true);
    		}
    		open = true;
    }
    
    
    public boolean isOpen(){
    	return open;
    }
    
    public void setTellerAmt(int amount){
    	tellerAmt = amount;
    }
    
    public void setManager(ManagerAgent m){
    	manager = m;
    }
    
    public void actionPerformed(ActionEvent e) {
    }
  
}

