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
    boolean isOpen;
    
    // Messages
    
    public void msgEnterMarket() { // from customer to market
    	
    }
    
    public void msgHereIsPayment(double amt) { // from customer to cashier
    	
    }
    
    public void msgLeaveMarket() { // from customer to market
    	
    }
    
    public void msgOrderFulfilled(String choice, int quantity) { // from worker to manager
    	
    }
    
     /* msgHereYouGo(String order, double amt); // to customer from manager
     * msgRestaurantDelivery(PersonAgent c, String order, int quantity); // to transportation from manager
     * msgHomeDelivery(PersonAgent c, String order, int quantity); // to transportation from manager
     * msgFulfillOrder(String order, int quantity); // to worker from manager
     */

    private SimCityGui gui;

    public Market(SimCityGui g, String n) {
    	name = n;
        this.gui = g;
    }
    
    public SimCityGui getGui() {
    	return gui;
    }
    
    public boolean isOpen() {
    	return isOpen;
    }
    
    public String getName() { return name; }
    
   // public void personAs(String type, String name, PersonAgent p) {
    public void personAs(PersonAgent p, String type, String name, double money){
    	addPerson(p, type, name, money);
    }
    
    public void addPerson(PersonAgent p, String type, String name, double money) {

    	if (type.equals("Customer")) {
    		CustomerAgent c = new CustomerAgent(name, 100);	
    		CustomerGui g = new CustomerGui(c);
//    		gui.markAniPanel.addGui(g);
    		if (manager!=null) c.setManager(manager);
    		c.setGui(g);
    		if (cashier!=null) c.setCashier(cashier);
    		c.setPerson(p);
    		customers.add(c);
    		c.startThread();
    		g.updatePosition();
    		
    	}
    	else if (type.equals("Worker")) {
    		WorkerAgent w = new WorkerAgent(name, manager);
    		WorkerGui g = new WorkerGui(w);
//    		gui.markAniPanel.addGui(g);
//    		if (cashier!=null) w.setCashier(cashier);
    		if (manager!=null) manager.addWorker(w);
    		w.setGui(g);
    		w.setPerson(p);
    		workers.add(w);
    		w.startThread();
    		g.updatePosition();
    		
    	}
    	else if (type.equals("Manager")) {
    		if (manager == null) {
    			manager = new ManagerAgent(name);
    			manager.setPerson(p);
    			manager.startThread();
    			for (WorkerAgent w : workers) {
    				manager.addWorker(w);
    				w.setManager(manager);
    			}
    		}
    	}
    	else if (type.equals("Cashier")) {
    		if (cashier == null) {
    			cashier = new CashierAgent(name, 100);
    			cashier.setPerson(p);
    			cashier.startThread();
    		}
    	}	
    }

}