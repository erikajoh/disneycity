package restaurant_cafe.gui;

import restaurant_cafe.CashierAgent;
import restaurant_cafe.CookAgent;
import restaurant_cafe.CustomerAgent;
import restaurant_cafe.HostAgent;
import restaurant_cafe.MarketAgent;
import restaurant_cafe.WaiterAgent;
import restaurant_cafe.CookAgent.Order;
import bank.gui.Bank;
import simcity.RestMenu;
import simcity.Restaurant;
import simcity.gui.SimCityGui;
import market.Market;

import javax.swing.*;

import agent_cafe.Agent;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Vector;

import simcity.PersonAgent;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class RestaurantCafe extends JPanel implements Restaurant{

    //Host, cook, waiters and customers
	
	public String name;
    private HostAgent host;
    private HostGui hostGui;
    private CookAgent cook;
    private CashierAgent cashier;
    public Bank bank;
    public Market market;
    String type;
    boolean isOpen = true;
    
    private Hashtable<PersonAgent, CustomerAgent> returningCusts = new Hashtable<PersonAgent, CustomerAgent>();
    private Vector<CustomerAgent> customers = new Vector<CustomerAgent>();
    private Vector<WaiterAgent> waiters = new Vector<WaiterAgent>();
    
	public Collection<Food> foods = Collections.synchronizedList(new ArrayList<Food>());
    public RestMenu menu2 = new RestMenu();
    private Menu menu;
    private JPanel restLabel = new JPanel();
    private ListPanel customerPanel = new ListPanel(this, "Customers");
    private ListPanel waiterPanel = new ListPanel(this, "Waiters");
    private JButton  pauseButton = new JButton("Pause Agents");
    private JPanel pauseGroup = new JPanel();
    private JPanel group = new JPanel();

    private SimCityGui gui; //reference to main gui

    public RestaurantCafe(SimCityGui gui, String name) {
        this.gui = gui;
        this.name = name;
        type = "American";
                
        Food food = new Food("Apple-Granola Pancakes", 3000, 5, 4, 8, 10.49);
        foods.add(food);
        food = new Food("Sirloin Steak and Eggs", 4000, 5, 4, 8, 11.99);
        foods.add(food);
        food = new Food("Ham and Cheese Omelet", 4000, 2, 4, 8, 9.99);
        foods.add(food);
        food = new Food("Walt's Chili", 3500, 5, 4, 8, 6.99);
        foods.add(food);
        food = new Food("Main Street Cheeseburger", 4500, 5, 4, 8, 11.99);
        foods.add(food);
        
        menu2.addItem("Apple-Granola Pancakes", 10.49);
        menu2.addItem("Sirloin Steak and Eggs", 11.99);
        menu2.addItem("Ham and Cheese Omelet", 9.99);
        menu2.addItem("Walt's Chili", 6.99);
        menu2.addItem("Main Street Cheeseburger", 11.99);
        
        menu = new Menu(foods);
        
        setLayout(new GridLayout(1, 2, 20, 20));
        group.setLayout(new GridLayout(1, 2, 10, 10));

        group.add(customerPanel);
        group.add(waiterPanel);

     
    }
    


    /**
     * Sets up the restaurant label that includes the menu,
     * and host and cook information
     */

    /**
     * When a customer or waiter is clicked, this function calls
     * updatedInfoPanel() from the main gui so that person's information
     * will be shown
     *
     * @param type indicates whether the person is a customer or waiter
     * @param name name of person
     */
   /* public void showInfo(String type, String name) {

        if (type.equals("Customers")) {
            for (int i = 0; i < customers.size(); i++) {
                CustomerAgent temp = customers.get(i);
                if (temp.getName() == name)
                    gui.updateInfoPanel(temp);
            }
        }
        else  if (type.equals("Waiters")) {
        	 for (WaiterAgent waiter : waiters) {
                 if (waiter.getName() == name){
                     gui.updateInfoPanel(waiter);
                 }
             }	
        }
    }
    */

    /**
     * Adds a customer or waiter to the appropriate list
     *
     * @param type indicates whether the person is a customer or waiter (later)
     * @param name name of person
     */
    public void addPerson(PersonAgent p, String type, String name, double money) {

    	if (type.equals("Customer")) {
    		//if ((p!=null) && returningCusts.containsKey(p)) {
    		//	returningCusts.get(p).getGui().setHungry();	
    		//}
    		//else {
    		CustomerAgent c = new CustomerAgent(name, customers.size());	
    		if (p!=null) c.setPerson(p);
    		//returningCusts.put(p, c);
    		CustomerGui g = new CustomerGui(c, gui);
    		g.setHungry();
    		c.setBalance(money);
    		gui.cafeAniPanel.addGui(g);// dw
    		if (host!=null) c.setHost(host);
    		if (cashier!=null) c.setCashier(cashier);
    		c.setGui(g);
    		customers.add(c);
    		c.startThread();
    		//}
    	}
    	else if (type.equals("Waiter")) {
    		WaiterAgent w = new WaiterAgent(name, menu, waiters.size()+1);	
    		if (p!=null) w.setPerson(p);
    		WaiterGui g = new WaiterGui(w, gui);
    		gui.cafeAniPanel.addGui(g);
    		if (host!=null) w.setHost(host);
    		if (cashier!=null) w.setCashier(cashier);
    		if (host!=null) host.addWaiter(w);
    		if (cook!= null)w.setCook(cook);
    		waiters.add(w);
    		w.setGui(g);
    		w.startThread();
    	}
    	else if (type.equals("Host")) {
    		if (p!=null) host.setPerson(p);
    		host = new HostAgent(name);
    		hostGui= new HostGui(host);
    		host.setGui(hostGui);
    		for (WaiterAgent w : waiters) {
    			w.setHost(host);
    			host.addWaiter(w);
    		}
    		
    		for (CustomerAgent c : customers) {
    			c.setHost(host);
    		}
    		host.startThread();
    	}
    	else if (type.equals("Cook")) {
    		cook = new CookAgent(name, foods);
    		if (p!=null) cook.setPerson(p);
    		CookGui cookGui = new CookGui(cook, gui);
    		cook.setGui(cookGui);
    		gui.cafeAniPanel.addGui(cookGui);
    		for (WaiterAgent w : waiters) { 
    			w.setCook(cook);
    		}
    		 for(int i = 0; i<3; i++){
    	            MarketAgent market = new MarketAgent(i, menu, 5);
    	            market.setCashier(cashier);
    	            market.startThread();
    	            cook.addMarket(market);
    	     }
            cook.startThread();
    		
    	}
    	else if (type.equals("Cashier")) {
    		cashier = new CashierAgent(name, menu);
    		if (p!=null) cashier.setPerson(p);
    	    CashierGui cashierGui = new CashierGui(cashier, gui);
    	    for (WaiterAgent w : waiters) { 
    	    	w.setCashier(cashier);
    	    }
    	    for (CustomerAgent c : customers) {
    	    	c.setCashier(cashier);
    	    }
    		cashier.setGui(cashierGui);
    		gui.cafeAniPanel.addGui(cashierGui);
            cashier.startThread();
    		
    	}
    }
    
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == pauseButton){
        	if(pauseButton.getText().equals("Pause Agents")){
        		pauseButton.setText("Resume Agents");
        		Agent.pause = true;
        	}
        	else {
        		pauseButton.setText("Pause Agents");
        		Agent.pause = false;
        	}
        }
    }

	public RestMenu getMenu() {
		return menu2;
	}

	public String getRestaurantName() {
		return name;
	}

	public void setBank(Bank b) {
		bank = b;
		
	}

	public boolean isOpen() {
		return (cook!=null && waiters.size()>0 && cashier!=null && host!=null && isOpen);
	}

	@Override
	public void personAs(PersonAgent p, String type, String name, double money) {
		addPerson(p, type, name, money);
	}



	@Override
	public void setMarket(Market m) {
		market = m;
	}
	
	public String getType() {
		return type;
	}



	@Override
	public void msgHereIsOrder(String food, int quantity, int ID) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void msgEndOfShift() {
		isOpen = false;
		
	}

	@Override
	public void msgHereIsBill(Market m, double amt) {
		// TODO Auto-generated method stub
		
	}
}

