package market;

import market.CashierAgent;
import market.CustomerAgent;
import market.ManagerAgent;
import market.WorkerAgent;

import market.gui.CustomerGui;
import market.gui.CashierGui;
import market.gui.WorkerGui;
import simcity.PersonAgent;
import simcity.Restaurant;
import simcity.gui.SimCityGui;
import simcity.gui.trace.AlertLog;
import simcity.gui.trace.AlertTag;
import simcity.interfaces.Market_Douglass;
import transportation.Transportation;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class Market implements Market_Douglass {

    /**
	 * 
	 */
	//Host, cook, waiters and customers
	String name;
    private ManagerAgent manager;
    private CashierAgent cashier;  
    private Transportation transport;
    private List<WorkerAgent> workers = new ArrayList<WorkerAgent>();
    private List<CustomerAgent> customers = new ArrayList<CustomerAgent>();
    private List<CustomerAgent> virtualCustomers = new ArrayList<CustomerAgent>();
//    private List<CustomerAgent> waitingCustomers = new ArrayList<CustomerAgent>(); // when market is between shifts
    private Hashtable<String, Integer> inventory = new Hashtable<String, Integer>();
    private Hashtable<String, Double> prices = new Hashtable<String, Double>();
    private Hashtable<String, Integer> locations = new Hashtable<String, Integer>();
    
    boolean isOpen = true;
    
    // Messages
    
	public void msgLeaving(CustomerAgent c) { // from CustomerAgent
		if (c.getPerson()!=null){
			if (c.virtual) transport.msgSendDelivery(c.getPerson(), this, c.getChoice(), c.quantity, c.getLocation());
			else c.getPerson().msgHereIsOrder(c.getChoice(), c.quantity);
			customers.remove(c);
			MoveLine();
		}
		else if (c.getRest()!=null) {
			transport.msgSendDelivery(c.getRest(), this, c.getChoice(), c.quantity, c.orderID);
			virtualCustomers.remove(c);
		}
	}
	
	public void msgHereIsPayment(Restaurant rest, double amt) { // from Restaurant
		for (int i=0; i<virtualCustomers.size(); i++) {
			CustomerAgent c = virtualCustomers.get(i);
			if (c.getRest() == rest) {
				c.msgHereIsMoney(amt);
				return;
			}
		}
	}
	
	private void MoveLine() {
		for (int i=0; i<customers.size(); i++) {
			CustomerAgent cust = customers.get(i);
			cust.msgLineMoved();
		}
	}
	
	public void startOfShift() {
		isOpen = true;
	}

    private SimCityGui gui;

    public Market(SimCityGui g, String n, Transportation t) {
    	this.name = n;
        this.gui = g;
        this.transport = t;
        inventory.put("Mexican", 50);
        prices.put("Mexican", 5.0);
        locations.put("Mexican", 1);
		inventory.put("Southern", 50);
		prices.put("Southern", 10.0);
		locations.put("Southern", 1);
		inventory.put("Italian", 50);
		prices.put("Italian", 5.0);
		locations.put("Italian", 2);
		inventory.put("German", 50);
		prices.put("German", 5.0);
		locations.put("German", 2);
		inventory.put("American", 50);
		prices.put("American", 5.0);
		locations.put("American", 2);
		inventory.put("Car", 20);
		prices.put("Car", 10.0);
		locations.put("Car", 3);
    }
    
    public SimCityGui getGui() {
    	return gui;
    }
    
    public boolean isOpen() {
    	return isOpen;
    }
    
    public String getName() { return name; }
    
    public void personAs(Restaurant r, String choice, int quantity, int id) {
    	System.out.println("The restaurant wants to order food!");
    	addPerson(r, r.getRestaurantName(), choice, quantity, id);
    }
    public void personAs(PersonAgent p, String name, double money, String choice, int quantity, String location){
    	addPerson(p, name, money, choice, quantity, location);
    }
    public void personAs(PersonAgent p, String name, double money, String choice, int quantity){
    	addPerson(p, name, money, choice, quantity);
    }
    
    public void addPerson(Restaurant r, String name, String choice, int quantity, int id) {
    	CustomerAgent cust = new CustomerAgent(name, choice, quantity, virtualCustomers.size(), id);	
		if (manager!=null) cust.setManager(manager);
		if (cashier!=null) cust.setCashier(cashier);
		cust.setRest(r);
		cust.setMarket(this);
		virtualCustomers.add(cust);
		cust.startThread();
    }
    
    public void addPerson(PersonAgent p, String name, double money, String choice, int quantity) {
		CustomerAgent c = new CustomerAgent(name, money, choice, quantity, customers.size());	
		CustomerGui g = new CustomerGui(c);
		gui.markAniPanel.addGui(g);
		if (manager!=null) c.setManager(manager);
		c.setGui(g);
		if (cashier!=null) c.setCashier(cashier);
		c.setPerson(p);
		c.setMarket(this);
		customers.add(c);
		c.startThread();
		g.updatePosition();
    }
    
    public void addPerson(PersonAgent p, String name, double money, String choice, int quantity, String location) {
		CustomerAgent c = new CustomerAgent(name, money, choice, quantity, virtualCustomers.size(), location);	
		if (manager!=null) c.setManager(manager);
		if (cashier!=null) c.setCashier(cashier);
		c.setPerson(p);
		c.setMarket(this);
		virtualCustomers.add(c);
		c.startThread();    
    }
   
    public void addPerson(PersonAgent p, String type, String name) {

    	if (type.equals("Worker")) {
    		AlertLog.getInstance().logDebug(AlertTag.MARKET, name, "added worker to market");
    		WorkerAgent w = new WorkerAgent(name, manager, workers.size());
    		WorkerGui g = new WorkerGui(w);
    		gui.markAniPanel.addGui(g);
    		if (cashier!=null) w.setCashier(cashier);
    		w.setPerson(p);
    		w.setMarket(this);
    		workers.add(w);
    		manager.addWorker(w);
    		w.startThread();
    		g.updatePosition();
    	}
    	else if (type.equals("Manager")) {
    		if (manager == null) {
    			manager = new ManagerAgent(name);
    			manager.setPerson(p);
    			manager.startThread();
    			for (int i=0; i<workers.size(); i++) {
    				WorkerAgent w = workers.get(i);
    				w.setManager(manager);
    			}
    		}
    	}
    	else if (type.equals("Cashier")) {   
    		if (cashier == null) {
    			cashier = new CashierAgent(name, 100);
    			CashierGui g = new CashierGui(cashier);
    			gui.markAniPanel.addGui(g);
    			cashier.setPerson(p);
    			cashier.setMarket(this);
    			cashier.startThread();
    			for (int i=0; i<workers.size(); i++) {
    				WorkerAgent w = workers.get(i);
    				w.setCashier(cashier);
    			}
    		}
    	}	
    	gui.updateGui();
    }
    
    public double getPrice(String f) {
    	return prices.get(f);
    }
    
    public int getLocation(String f) {
    	return locations.get(f);
    }
    
    public int getItem(String f, int amt) {
		if (inventory.get(f) >= amt) {
			inventory.put(f, inventory.get(f)-amt);
			return amt;
		} else if (inventory.get(f) >= 0) {
			int getAmt = inventory.get(f);
			inventory.put(f, 0);
			return getAmt;
		} else return 0;
	}
    
    public void setInventory(String name, int num){
    	inventory.put(name, num);
    }
    
    public String[] getInventory(){
    	Set<String> inventoryList = inventory.keySet(); 
    	return inventoryList.toArray(new String[0]);
    }
    
    public void endOfShift() {
		isOpen = false;
		double totalMoney = cashier.getMoney();
		int numEmployees = 2 + workers.size();
		double wage = totalMoney/numEmployees;
		System.out.println("MARKET GOT END OF SHIFT");
		for (int i = 0; i < workers.size(); i++) {
			WorkerAgent w = workers.get(i);
			w.msgShiftDone(wage);
			workers.remove(w);
		}
		if (manager!=null) {
			manager.msgShiftDone(wage);
			manager = null;
		}
		if (cashier!=null) {
			cashier.msgShiftDone(wage);
			cashier = null;
		}		
	}
    
    public String[] getWorkers(){
        List<String> marketWorkers = new ArrayList<String>();
        
    	if(cashier != null){
    		String cashierName = "Cashier: "+cashier.getName();
    		marketWorkers.add(cashierName);
    	}
    	for(WorkerAgent worker : workers){
    		String workerName = "Worker: "+worker.getName();
    		marketWorkers.add(workerName);
    	}
    	String[] mktWorkers = new String[marketWorkers.size()];
    	mktWorkers = marketWorkers.toArray(mktWorkers);
   
    	return mktWorkers;
    }
    
    public int getItemQty(String item) {
    	return inventory.get(item);
    }

}