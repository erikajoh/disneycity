package restaurant_rancho.gui;

import agent_rancho.Agent;
import restaurant_rancho.CashierAgent;
import restaurant_rancho.CookAgent;
import restaurant_rancho.CustomerAgent;
import restaurant_rancho.HostAgent;
import restaurant_rancho.MarketAgent;
import restaurant_rancho.WaiterAgent;
import restaurant_rancho.interfaces.Bank; 
import simcity.PersonAgent;
import simcity.gui.SimCityGui;
import restaurant_rancho.RestMenu;
import simcity.Restaurant;

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
public class RestaurantRancho extends JPanel implements Restaurant {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//Host, cook, waiters and customers
	String name;
	Bank bank;
    private HostAgent host;
    private CookAgent cook;
    private CashierAgent cashier;    
    private List<WaiterAgent> waiters = new ArrayList<WaiterAgent>();
    private List<CustomerAgent> customers = new ArrayList<CustomerAgent>();
    private List<MarketAgent> markets = new ArrayList<MarketAgent>();

    private JPanel restLabel = new JPanel();
    private ListPanel customerPanel = new ListPanel(this, "Customers");
    private ListPanel waiterPanel = new ListPanel (this, "Waiters");
    private JPanel group = new JPanel();
    private RestMenu menu = new RestMenu();
    boolean isOpen;


    private SimCityGui gui; //reference to main gui
    private CookGui cookgui;

    //public RestaurantBase(RestaurantGui gui, String n) {
    public RestaurantRancho(SimCityGui g, String n) {
    	name = n;
        this.gui = g;
        menu.addItem("Citrus Fire-Grilled Chicken", 13.49);
        menu.addItem("Red Chile Enchilada Platter", 9.99);
        menu.addItem("Soft Tacos Monterrey", 10.99);
        menu.addItem("Burrito Sonora", 10.99);
        menu.addItem("Chicken Tortilla Soup", 5.99);
        setLayout(new GridLayout(1, 2, 20, 20));
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));

        group.add(customerPanel);
        group.add(waiterPanel);

        
        add(restLabel);
        add(group);
    }
    public SimCityGui getGui() {
    	return gui;
    }
    
    //public RestMenu getMenu
    
    public void setBank(Bank b) {
    	bank = b;
    }
    
    public boolean isOpen() {
    	return isOpen;
    }
    
    public RestMenu getMenu() {
    	return menu;
    }
    
    
   // public void personAs(String type, String name, PersonAgent p) {
    public void personAs(PersonAgent p, String type, String name, double money, String choice){
    	addPerson(p, type, name, money, choice);
    }
    public void PauseandUnpauseAgents() {
    	for (WaiterAgent w : waiters) {
    		w.pauseOrRestart();
    	}
    	for (CustomerAgent c : customers) {
    		c.pauseOrRestart();
    	}
    	cook.pauseOrRestart();
    	host.pauseOrRestart(); 	
    	for (MarketAgent m : markets) {
    		m.pauseOrRestart();
    	}
    	cashier.pauseOrRestart();
    }
    
    public void waiterWantsBreak(WaiterAgent w) {
    	host.msgWantBreak(w);
    }
    
    public void waiterWantsOffBreak(WaiterAgent w) {
    	host.msgBackFromBreak(w);
    }

    /**
     * Sets up the restaurant label that includes the menu,
     * and host and cook information
     */
    private void initRestLabel() {
        JLabel label = new JLabel();
        restLabel.setLayout(new BorderLayout());
        label.setText(
                "<html><h3><u>Tonight's Staff</u></h3><table><tr><td>host:</td><td>" + host.getName() +  "</td></tr></table><h3><u> Menu</u></h3><table><tr><td>Steak</td><td>$14.50</td></tr><tr><td>Chicken</td><td>$12.50</td></tr><tr><td>Salad</td><td>$7.50</td></tr><tr><td>Pizza</td><td>$10.50</td></tr><tr><td>Latte</td><td>$3.25</td></tr></table><br></html>");
        label.setFont(new Font("Helvetica", Font.PLAIN, 13));
        restLabel.setBorder(BorderFactory.createRaisedBevelBorder());
        restLabel.add(label, BorderLayout.CENTER);
        restLabel.add(new JLabel("               "), BorderLayout.EAST);
        restLabel.add(new JLabel("               "), BorderLayout.WEST);
    }

    /**
     * When a customer or waiter is clicked, this function calls
     * updatedInfoPanel() from the main gui so that person's information
     * will be shown
     *
     * @param type indicates whether the person is a customer or waiter
     * @param name name of person
 
   */
    /*
    public void showInfo(String type, String name) {

        if (type.equals("Customers")) {
            for (int i = 0; i < customers.size(); i++) {
                CustomerAgent temp = customers.get(i);
                //if (temp.getName() == name)
                  //  gui.updateInfoPanel(temp);
            }
        }
        if (type.equals("Waiters")) {
        	for (int i = 0; i < waiters.size(); i++) {
        		WaiterAgent temp = waiters.get(i);
        		if(temp.getName() == name) {
        			gui.updateWInfoPanel(temp);
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
    public void addPerson(PersonAgent p, String type, String name, double money, String choice) {

    	if (type.equals("Customers")) {
    		CustomerAgent c = new CustomerAgent(name);	
    		CustomerGui g = new CustomerGui(c, gui, customers.size());
    	//new	 
    		g.setHungry();
    		// new
    		gui.restAniPanel.addGui(g);
    		if (host!=null) c.setHost(host);
    		c.setGui(g);
    		if (cashier!=null) c.setCashier(cashier);
    		//Random rand = new Random();
    		//c.setCash(rand.nextInt(20)+10);
    		c.setCash(money);
    		c.setChoice(choice);
    		c.setPerson(p);
    		customers.add(c);
    		c.startThread();
    		g.updatePosition();
    		
    	}
    	else if (type.equals("Waiters")) {
    		WaiterAgent w = new WaiterAgent(name, this);
    		WaiterGui g = new WaiterGui(w, waiters.size());
    		gui.restAniPanel.addGui(g);
    		if (host!=null) w.setHost(host);
    		if (cook!= null) w.setCook(cook);
    		if (cashier!=null)w.setCashier(cashier);
    		if (host!=null) host.addWaiter(w);
    		w.setGui(g);
    		w.setPerson(p);
    		waiters.add(w);
    		w.startThread();
    		g.updatePosition();
    		
    	}
    	else if (type.equals("Host")) {
    		if (host == null) {
    			host = new HostAgent(name);
    			host.setPerson(p);
    			host.startThread();
    			initRestLabel();
    			for (WaiterAgent w : waiters) {
    				host.addWaiter(w);
    				w.setHost(host);
    			}
    		}
    	}
    	else if (type.equals("Cook")) {
    		if (cook == null) {
    			cook = new CookAgent(name, this);
    			cookgui = new CookGui(cook);
    			cook.setPerson(p);
    			cook.setGui(cookgui);
    			cookgui.updatePosition();
    			cook.startThread();
    			for (WaiterAgent w : waiters) {
    				w.setCook(cook);	
    			}
    			for (MarketAgent m : markets) {
    				cook.addMarket(m);
    			}
    			gui.restAniPanel.addGui(cookgui);
    		}
    	}
    	else if (type.equals("Cashier")) {
    		if (cashier == null) {
    			cashier = new CashierAgent(name);
    			cashier.setPerson(p);
    			cashier.startThread();
    			for (WaiterAgent w : waiters) {
    				w.setCashier(cashier);
    			}
    			for (MarketAgent m : markets) {
    				m.setCashier(cashier);
    			}
    		}
    	}
    	else if (type.equals("Market")) {
    		MarketAgent market = new MarketAgent(name, 10, 10, 10, 10, 10);
    		markets.add(market);
    		if (cashier!=null) market.setCashier(cashier);
    		if (cook!= null) cook.addMarket(market);
    		market.startThread();
    		
    	}
    			
    			
    		
    		
    }
    

}
