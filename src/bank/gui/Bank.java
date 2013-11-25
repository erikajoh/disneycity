package bank.gui;

import bank.BankCustomerAgent;
import bank.ManagerAgent;
import bank.PersonAgent;
import bank.TellerAgent;
import bank.interfaces.BankCustomer;
import bank.interfaces.Manager;
import bank.interfaces.Person;

import javax.swing.*;

import simcity.gui.SimCityGui;
import agent.Agent;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * Panel in frame that contains all the bank information,
 * including host, cook, waiters, and customers.
 */
public class Bank extends JPanel implements ActionListener {

    //Host, cook, waiters and customers

    private Vector<PersonAgent> customers = new Vector<PersonAgent>();
    private Vector<TellerAgent> tellers = new Vector<TellerAgent>();
	private Map<BankCustomer, PersonAgent> spawns = new HashMap<BankCustomer, PersonAgent>();


    private SimCityGui gui; //reference to main gui
    
    private ManagerAgent manager;

    public Bank(SimCityGui gui) {
        this.gui = gui;
        manager = new ManagerAgent("Bank", this, gui);
        manager.startThread();
        
        for(int i = 0; i<4; i++){
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
        
        String name = "Dylan";
		PersonAgent p = new PersonAgent(name);
		p.setBank(this);
		p.msgArrive(-1);
		customers.add(p);
		p.startThread();
    }
    
    public void msgRequestAccount(PersonAgent person, double balance, boolean present){
    	BankCustomer bca = createBankCustomer(person, present);
    	manager.msgRequestAccount(bca, balance);
    }
    
    public void msgRequestDeposit(PersonAgent person, int accountNum, double reqAmt, boolean present){
    	BankCustomer bca = createBankCustomer(person, present);
    	manager.msgRequestDeposit(bca, accountNum, reqAmt);
    }
    
    public void msgRequestWithdrawal(PersonAgent person, int accountNum, double reqAmt, boolean present){
    	BankCustomer bca = createBankCustomer(person, present);
    	manager.msgRequestWithdrawal(bca, accountNum, reqAmt);
    }
	
	public void msgLeave(BankCustomer bc, int accountNum, double balance, double loanAmt, int loanTime){
		PersonAgent person = (PersonAgent)spawns.get(bc);
		System.out.println(bc.toString());
		person.msgLeave(accountNum, balance, loanAmt, loanTime);
	}
    
    public BankCustomer createBankCustomer(PersonAgent person, boolean present){
    	 customers.add(person);
   	  	 BankCustomerAgent bca = new BankCustomerAgent(person.getName(), manager, gui);
	     BankCustomerGui g = new BankCustomerGui(bca, gui, present, 400, 330);
	     gui.bankAniPanel.addGui(g);// dw
	     bca.setGui(g);
	     bca.startThread();
    	 manager.msgCustomerHere(bca);
    	 spawns.put(bca, person);
    	 return bca;
    }


 


    /**
     * Adds a customer or waiter to the appropriate list
     *
     * @param type indicates whether the person is a customer or waiter (later)
     * @param name name of person
     */
    public void addPerson(String type, String name) {

    	if (type.equals("Customers")) {
    		PersonAgent p = new PersonAgent(name);
    		//p.setManager(manager);
    		p.msgArrive(-1);
    		//BankCustomerAgent c = new BankCustomerAgent(name, gui);	
    		//BankCustomerGui g = new BankCustomerGui(c, gui, gui.getAnimWindowX(), gui.getAnimWindowY());
    		//c.setBalance(25.00);
    		//c.setBank(bank);
    		//gui.animationPanel.addGui(g);// dw
    		//c.setGui(g);
    		customers.add(p);
    		p.startThread();
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
    
    public void actionPerformed(ActionEvent e) {
      /*  if(e.getSource() == pauseButton){
        	if(pauseButton.getText().equals("Pause Agents")){
        		pauseButton.setText("Resume Agents");
        		Agent.pause = true;
        	}
        	else {
        		pauseButton.setText("Pause Agents");
        		Agent.pause = false;
        	}
        }*/
    }
    
    /*public String getNameFieldText(){
    	return gui.getNameFieldText();
    }*/
  
}

