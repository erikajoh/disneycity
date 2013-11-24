package restaurant_bayou.gui;

import restaurant_bayou.CashierAgent;
import restaurant_bayou.CookAgent;
import restaurant_bayou.CustomerAgent;
import restaurant_bayou.HostAgent;
import restaurant_bayou.MarketAgent;
import restaurant_bayou.WaiterAgent;
import restaurant_bayou.interfaces.Market;
import simcity.Restaurant;
import simcity.PersonAgent;
import simcity.RestMenu;
import simcity.gui.SimCityGui;

import javax.swing.*;

import java.util.Hashtable;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class RestaurantBayou extends JPanel{

    //Host, cook, waiters and customers
    private HostAgent host; 
    private CashierAgent cashier;
    private CashierGui cashierGui; 
    private CookAgent cook;
    private CookGui cookGui;
    private List<CustomerAgent> customers = new ArrayList<CustomerAgent>();
    private List<WaiterAgent> waiters = new ArrayList<WaiterAgent>();
    private JPanel restLabel = new JPanel();
    private ListPanel customerPanel = new ListPanel(this, "customers");
    private ListPanel waiterPanel = new ListPanel(this, "waiters");
    private ListPanel marketPanel = new ListPanel(this, "markets");
    private JPanel group = new JPanel();
    private JLabel restSubLabel = new JLabel();
    private Hashtable<PersonAgent, CustomerAgent> returningCusts = new Hashtable<PersonAgent, CustomerAgent>();
    public RestMenu menu = new RestMenu();

    private SimCityGui gui; //reference to main gui

    public RestaurantBayou(SimCityGui gui) {
        this.gui = gui;

        setLayout(new GridLayout(1, 2, 20, 20));
        group.setLayout(new GridLayout(1, 2, 10, 10));

        initRestLabel();
        add(restLabel);
        
        add(customerPanel);
        add(waiterPanel);
        add(marketPanel);
        
//        add(group);
    }

    /**
     * Sets up the restaurant label that includes the menu,
     * and host and cook information
     */
    private void initRestLabel() {
        restLabel.setLayout(new BorderLayout());
//        String waiterText = "<tr><td>Waiters:</td>";
//        for (WaiterAgent waiter: host.waiters) {
//        	waiterText += "<tr>" + waiter.getName() + "</tr>";
//        }
//        waiterText += "</tr>";
        String menuText = "<h3>menu</h3><table><tr><td>salad</td><td>$7.50</td></tr><tr><td>pizza</td><td>$8.00</td></tr><tr><td>pasta</td><td>$8.50</td></tr><tr><td>steak</td><td>$9.00</td></tr></table><br>";
        restSubLabel.setText(
                "<html><h3>hello world!</h3>welcome to erika's v2.2 restaurant.<h3>host</h3><table><tr><td>" + host.getName() + "</table>" + menuText + "</html"); 
        restLabel.setBorder(BorderFactory.createRaisedBevelBorder());
        restLabel.add(restSubLabel, BorderLayout.CENTER);
        restLabel.add(new JLabel("        "), BorderLayout.EAST);
        restLabel.add(new JLabel("        "), BorderLayout.WEST);
    }

    /**
     * When a customer or waiter is clicked, this function calls
     * updatedInfoPanel() from the main gui so that person's information
     * will be shown
     *
     * @param type indicates whether the person is a customer or waiter
     * @param name name of person
     */
  /*  public void showInfo(String type, String name) {

        if (type.equals("customers")) {

            for (int i = 0; i < host.customers.size(); i++) {
                CustomerAgent temp = host.customers.get(i);
                if (temp.getName() == name)
                    gui.updateInfoPanel(temp);
            }
        }
        
        if (type.equals("waiters")) {

            for (int i = 0; i < host.waiters.size(); i++) {
                WaiterAgent temp = host.waiters.get(i);
                if (temp.getName() == name)
                    gui.updateInfoPanel(temp);
            }
        }
        
        if (type.equals("markets")) {
        	for (int i = 0; i < host.cook.markets.size(); i++) {
                Market temp = host.cook.markets.get(i).m;
                if (temp.getName() == name)
                    gui.updateInfoPanel(temp);
            }
        }
    }*/

    /**
     * Adds a customer or waiter to the appropriate list
     *
     * @param type indicates whether the person is a customer or waiter (later)
     * @param name name of person
     */
    public void addPerson(PersonAgent p, String type, String name, double money, String choice) {

    	if (type.equals("Customer")) {
    		//if (returningCusts.contains(p)) {
    		//	returningCusts.get(p).getGui().setHungry();
    		//}
    		//else {
    			CustomerAgent c = new CustomerAgent(name, money); // hack to make customer start with $100
    			CustomerGui g = new CustomerGui(c, gui, customers.size());
    			c.setGui(g);
    			customers.add(c);
    			//returningCusts.put(p, c);
    			g.setHungry();
    			//gui.animationPanel.addGui(g);// dw
    			if (host!=null) c.setHost(host);
    			if (host!= null) host.customers.add(c);
    			c.startThread();
    		//}
    	}
    	
    	else if (type.equals("Waiter")) {
    		WaiterAgent w = new WaiterAgent(name);
        	WaiterGui waiterGui = new WaiterGui(w, gui, waiters.size());
        	waiters.add(w);
        	w.setGui(waiterGui);
        	//gui.animationPanel.addGui(waiterGui);
        	if (host!=null) w.setHost(host);
        	if (cook!=null) w.setCook(cook);
        	w.startThread();
    	}
    	else if (type.equals("Host")) {
    		host = new HostAgent("le host");
    		if (cashierGui!=null) host.setGui(cashierGui);
    		if (cashier!=null) host.setCashier(cashier);
            if (cookGui!=null) host.setGui(cookGui);
            if (host!=null) {
            	for (WaiterAgent w : waiters) {
            		host.addWaiter(w);
            		w.setHost(host);
            	}
            }		
            host.startThread();
    	}
    	else if (type.equals("Cook")) {
    		cook = new CookAgent(name, menu);
    		cookGui = new CookGui();
    		cook.setGui(cookGui);
    		//gui.animationPanel.addGui(cookGui);
    		if (cashier!=null) cook.setCashier(cashier);
    		if (cashier!=null) cashier.setCook(cook);
    		if (host!=null) host.setGui(cookGui);
    		if (host!=null) cookGui.setHost(host);
    		for (WaiterAgent w : waiters) {
    			w.setCook(cook);
    		}
    		cook.startThread();
    		
    	}
    	else if (type.equals("Cashier")) {
    		cashier = new CashierAgent(name, menu, 100);
    		cashierGui = new CashierGui(host);
    		cashier.startThread();
    		//gui.animationPanel.addGui(cashierGui);
    		if (cook!=null) cashier.setCook(cook);
    	    if (cook!=null) cook.setCashier(cashier);
    		if (host!=null) host.setGui(cashierGui);
    		if (host!=null) host.setCashier(cashier);
    		cashier.startThread();
    		
    	}
    	
    }
    
    public void addButton(JButton b) {
		restLabel.add(b, BorderLayout.SOUTH);
	}
    
    public void resetState(Object person){
    	if (person instanceof CustomerAgent) customerPanel.resetCB(person);
    	if (person instanceof WaiterAgent) waiterPanel.resetCB(person);
    }
    
  /*  public void addTable(){
    	gui.animationPanel.addTable();
    }
    */
    
    public String addMarket(Boolean isEmpty){
    	MarketAgent m = host.addMarket();
    	m.startThread();
    	if (isEmpty) m.emptyInventory();
    	return m.getName();
    }
    
    public String addWaiter(Boolean onBreak){
    	WaiterAgent w = new WaiterAgent("w");
    	host.addWaiter(w);
    	WaiterGui waiterGui = new WaiterGui(w, gui, waiters.size());
    	w.setGui(waiterGui);
    	//gui.animationPanel.addGui(waiterGui);
    	w.startThread();
    	if (onBreak) w.askForBreak();
        return w.getName();
    }
    
    public int howManyWaiters(){
    	return host.howManyWaiters();
    }
	
}
