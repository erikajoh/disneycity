package restaurant_bayou.gui;

import restaurant_bayou.CashierAgent;
import restaurant_bayou.CookAgent;
import restaurant_bayou.CustomerAgent;
import restaurant_bayou.HostAgent;
import restaurant_bayou.MarketAgent;
import restaurant_bayou.WaiterAgent;
import simcity.Restaurant;
import simcity.PersonAgent;
import simcity.RestMenu;
import simcity.gui.SimCityGui;
import bank.gui.Bank;
import market.Market;

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
public class RestaurantBayou extends JPanel implements Restaurant{

    //Host, cook, waiters and customers
	String name;
	String type;
    private HostAgent host; 
    private CashierAgent cashier;
    private CashierGui cashierGui; 
    private CookAgent cook;
    private CookGui cookGui;
    private MarketAgent market;
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
    boolean isOpen;
    private Bank bank;
    private Market market2;

    private SimCityGui gui; //reference to main gui

    public RestaurantBayou(SimCityGui gui, String name) {
        this.gui = gui;
        this.name = name;
        type = "Southern";
        setLayout(new GridLayout(1, 2, 20, 20));
        group.setLayout(new GridLayout(1, 2, 10, 10));
        
       
        menu.addItem("Filet Mignon", 42.99);
        menu.addItem("Pan-Seared Salmon", 33.99);
        menu.addItem("Portobello Mushroom and Couscous Macque Choux", 29.99);
        menu.addItem("Seafood Jambalaya", 31.99);

        market = new MarketAgent("Market", menu);
       /* initRestLabel();
        add(restLabel);
        
        add(customerPanel);
        add(waiterPanel);
        add(marketPanel);
        */
        
//        add(group);
    }
    
    public void setBank(Bank b) {
    	bank = b;
    }
    
    public String getRestaurantName() {
    	return name;
    }
    
    public boolean isOpen() {
    	return (cook!=null && waiters.size()>0 && cashier!=null && host!=null && isOpen);
    }
    
    public RestMenu getMenu() {
    	return menu;
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
    
    public void personAs(PersonAgent p, String type, String name, double money) {
    	addPerson(p, type, name, money);
    }

    /**
     * Adds a customer or waiter to the appropriate list
     *
     * @param type indicates whether the person is a customer or waiter (later)
     * @param name name of person
     */
    public void addPerson(PersonAgent p, String type, String name, double money) {

    	if (type.equals("Customer")) {
    		//if (returningCusts.contains(p)) {
    		//	returningCusts.get(p).getGui().setHungry();
    		//}
    		//else {
    			CustomerAgent c = new CustomerAgent(name, money); 
    			CustomerGui g = new CustomerGui(c, gui, customers.size());
    			c.setGui(g);
    			if (p!=null) c.setPerson(p);
    			customers.add(c);
    			//returningCusts.put(p, c);
    			g.setHungry();
    			gui.bayouAniPanel.addGui(g);
    			if (host!=null) c.setHost(host);
    			if (host!= null) host.customers.add(c);
    			c.startThread();
    		//}
    	}
    	
    	else if (type.equals("Waiter")) {
    		WaiterAgent w = new WaiterAgent(name);
        	WaiterGui waiterGui = new WaiterGui(w, gui, waiters.size());
        	if (p!=null) w.setPerson(p);
        	waiters.add(w);
        	w.setGui(waiterGui);
        	gui.bayouAniPanel.addGui(waiterGui);
        	if (host!=null) w.setHost(host);
        	if (cook!=null) w.setCook(cook);
        	w.startThread();
    	}
    	else if (type.equals("Host")) {
    		host = new HostAgent(name);
    		if (p!=null) host.setPerson(p);
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
    		if (p!=null) cook.setPerson(p);
    		cook.setGui(cookGui);
    		gui.bayouAniPanel.addGui(cookGui);
    		if (cashier!=null) cook.setCashier(cashier);
    		if (cashier!=null) cashier.setCook(cook);
    		if (host!=null) host.setGui(cookGui);
    		if (host!=null) cookGui.setHost(host);
    		for (WaiterAgent w : waiters) {
    			w.setCook(cook);
    		}
    		if (market!=null) cook.addMarket(market);
    		cook.startThread();
    		
    	}
    	else if (type.equals("Cashier")) {
    		cashier = new CashierAgent(name, menu, 100);
    		cashierGui = new CashierGui(host);
    		if (p!=null) cashier.setPerson(p);
    		cashier.startThread();
    		gui.bayouAniPanel.addGui(cashierGui);
    		if (cook!=null) cashier.setCook(cook);
    	    if (cook!=null) cook.setCashier(cashier);
    		if (host!=null) host.setGui(cashierGui);
    		if (host!=null) host.setCashier(cashier);
    		cashier.startThread();	
    	}
    	
    	else if (type.equals("Market")) { 
    		market = (new MarketAgent(name, menu));
    		if (cook!=null) cook.addMarket(market);
    		market.startThread();
    		
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
    	market = new MarketAgent("hi", menu);
    	host.addMarket(market);
    	market.startThread();
    	if (isEmpty) market.emptyInventory();
    	return market.getName();
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

	@Override
	public void setMarket(Market m) {
		market2 = m;
		// T
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
	
}
