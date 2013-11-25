package market;

import market.CashierAgent;
import market.CustomerAgent;
import market.ManagerAgent;
import market.WorkerAgent;
import market.gui.CustomerGui;
import market.gui.WorkerGui;
import simcity.PersonAgent;
import simcity.gui.SimCityGui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class Market {

    /**
	 * 
	 */
	//Host, cook, waiters and customers
	String name;
    private ManagerAgent manager;
    private CashierAgent cashier;    
    private List<WorkerAgent> workers = new ArrayList<WorkerAgent>();
    private List<CustomerAgent> customers = new ArrayList<CustomerAgent>();
    private Hashtable<String, Integer> inventory = new Hashtable<String, Integer>();
    private Hashtable<String, Double> prices = new Hashtable<String, Double>();
    private Hashtable<String, Integer> locations = new Hashtable<String, Integer>();
    
    boolean isOpen;
    
    // Messages
    
	public void msgLeaving(CustomerAgent c) {
		c.getPerson().msgHereIsOrder(c.getChoice(), c.quantity);
		customers.remove(c);
	}
    
     /*
     * msgRestaurantDelivery(PersonAgent c, String order, int quantity); // to transportation from manager
     * msgHomeDelivery(PersonAgent c, String order, int quantity); // to transportation from manager
     * msgFulfillOrder(String order, int quantity); // to worker from manager
     * msgDoneAtMarket(boolean fulfilled);
     */

    private SimCityGui gui;

    public Market(SimCityGui g, String n) {
    	name = n;
        this.gui = g;
        inventory.put("Mexican", 5);
        prices.put("Mexican", 10.0);
        locations.put("Mexican", 1);
		inventory.put("Southern", 5);
		prices.put("Southern", 10.0);
		locations.put("Southern", 1);
		inventory.put("Italian", 5);
		prices.put("Italian", 10.0);
		locations.put("Italian", 2);
		inventory.put("German", 5);
		prices.put("German", 10.0);
		locations.put("German", 2);
		inventory.put("American", 5);
		prices.put("American", 10.0);
		locations.put("American", 2);
		inventory.put("Car", 5);
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
    
    public void personAs(PersonAgent p, String type, String name, double money, String choice, int quantity){
    	addPerson(p, type, name, money, choice, quantity);
    }
    
    public void personAs(PersonAgent p, String type, String name){
    	addPerson(p, type, name);
    }
    
    public void addPerson(PersonAgent p, String type, String name, double money, String choice, int quantity) {

    	if (type.equals("Customer")) {
    		CustomerAgent c = new CustomerAgent(name, money, choice, quantity);	
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
    	else if (type.equals("VirtualCustomer")) {
    		CustomerAgent c = new CustomerAgent(name, money, choice, quantity);	
    		if (manager!=null) c.setManager(manager);
    		if (cashier!=null) c.setCashier(cashier);
    		c.setPerson(p);
    		c.setMarket(this);
    		customers.add(c);
    		c.startThread();
    	}
    }
    
    public void addPerson(PersonAgent p, String type, String name) {

    	if (type.equals("Worker")) {
    		WorkerAgent w = new WorkerAgent(name, manager, workers.size());
    		WorkerGui g = new WorkerGui(w);
    		gui.markAniPanel.addGui(g);
    		if (cashier!=null) w.setCashier(cashier);
    		w.setGui(g);
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
    			for (WorkerAgent w : workers) {
    				w.setManager(manager);
    			}
    		}
    	}
    	else if (type.equals("Cashier")) {
    		if (cashier == null) {
    			cashier = new CashierAgent(name, 100);
    			cashier.setPerson(p);
    			cashier.setMarket(this);
    			cashier.startThread();
    		}
    	}	
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

}