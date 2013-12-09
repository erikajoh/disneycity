package restaurant_cafe.gui;

import restaurant_cafe.CashierAgent;
import restaurant_cafe.CookAgent;
import restaurant_cafe.CustomerAgent;
import restaurant_cafe.HostAgent;
import restaurant_cafe.MarketAgent;
import restaurant_cafe.WaiterAgent;
import restaurant_cafe.gui.Order;
import restaurant_cafe.WaiterAgentNorm;
import restaurant_cafe.WaiterAgentPC;
import restaurant_cafe.gui.WaiterGui;
import bank.gui.Bank;
import simcity.RestMenu;
import simcity.Restaurant;
import simcity.gui.SimCityGui;
import simcity.gui.trace.AlertLog;
import simcity.gui.trace.AlertTag;
import market.Market;
import simcity.interfaces.Bank_Douglass;

import javax.swing.*;

import agent_cafe.Agent;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import simcity.interfaces.Person;
import simcity.interfaces.Market_Douglass;
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
    public Bank_Douglass bank;
    public Market_Douglass market;
    String type;
    boolean isOpen = true;
    
    private Hashtable<Person, CustomerAgent> returningCusts = new Hashtable<Person, CustomerAgent>();
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
    
    public ProducerConsumerMonitor orderStand = new ProducerConsumerMonitor();

    private SimCityGui gui; //reference to main gui

    public RestaurantCafe(SimCityGui gui, String name) {
        this.gui = gui;
        this.name = name;
        type = "American";
               //initial food values are high so no mkt orders for now....
        Food food = new Food("Apple-Granola Pancakes", 3000, 15, 4, 8, 10.49);
        foods.add(food);
        food = new Food("Sirloin Steak and Eggs", 4000, 15, 4, 8, 11.99);
        foods.add(food);
        food = new Food("Ham and Cheese Omelet", 4000, 12, 4, 8, 9.99);
        foods.add(food);
        food = new Food("Walt's Chili", 3500, 5, 14, 8, 6.99);
        foods.add(food);
        food = new Food("Main Street Cheeseburger", 4500, 15, 4, 8, 11.99);
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
    
    public void setFoodAmount(String choice, int amount) {
    	if (cook!=null) 
    		cook.setAmount(choice, amount);
    }
    public void addPerson(Person p, String type, String name, double money) {

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
    		WaiterAgentNorm w = new WaiterAgentNorm(name, this, menu, waiters.size()+1);
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
    	else if (type.equals("WaiterPC")) {
    		WaiterAgentPC w = new WaiterAgentPC(name, this, menu, waiters.size()+1);
    		WaiterGui g = new WaiterGui(w, gui);
    		if (p!=null) w.setPerson(p);
    		gui.cafeAniPanel.addGui(g);
    		if (host!=null) w.setHost(host);
    		if (cook!= null) w.setCook(cook);
    		if (cashier!=null)w.setCashier(cashier);
    		if (host!=null) host.addWaiter(w);
    		w.setGui(g);
    		waiters.add(w);
    		w.startThread();
    		g.updatePosition();
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
    		cook = new CookAgent(name, this, foods);
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
    	    AlertLog.getInstance().logInfo(AlertTag.RESTAURANT, "COOK", "COOK's thread started ");
    		
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
	
    public String[] getFoodNames(){
    	return menu2.menuList.toArray(new String[0]);
    }
    
    public String[] getWorkers(){
   	 List<String> restWorkers = new ArrayList<String>();
        
    	if(cashier != null){
    		String cashierName = "Cashier: "+cashier.getName();
    		restWorkers.add(cashierName);
    	}
    	if(cook != null){
    		String cookName = "Cook: "+cook.getName();
    		restWorkers.add(cookName);
    	}
    	if(host != null){
    		String hostName = "Host: "+host.getName();
    		restWorkers.add(hostName);
    	}
    	for(WaiterAgent waiter : waiters){
    		String waiterName = "Waiter: "+waiter.getName();
    		restWorkers.add(waiterName);
    	}
    	String[] workers = new String[restWorkers.size()];
    	workers = restWorkers.toArray(workers);
   
    	return workers;	
   }
    
    public int getQuantity(String name){
    	if(cook != null){
    		return cook.getQuantity(name);
    	}
    	return 0;
    }
    
    public void setQuantityAndBalance(String name, int num, double balance){
    	if(cook != null){
    		 cook.setQuantity(name, num);
    	}
    }

	public String getRestaurantName() {
		return name;
	}

	public void setBank(Bank_Douglass b) {
		bank = b;
		
	}

	public boolean isOpen() {
		return (cook!=null && waiters.size()>0 && cashier!=null && host!=null && isOpen);
	}

	@Override
	public void personAs(Person p, String type, String name, double money) {
		addPerson(p, type, name, money);
	}



	@Override
	public void setMarket(Market_Douglass m) {
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
	public void msgHereIsBill(Market_Douglass m, double amt) {
		// TODO Auto-generated method stub
		
	}
}

