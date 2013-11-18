package restaurant.gui;

import agent.Agent;
import restaurant.CustomerAgent;
import restaurant.HostAgent;
import restaurant.WaiterAgent;
import restaurant.CookAgent;
import restaurant.MarketAgent;
import restaurant.CashierAgent;

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
public class RestaurantPanel extends JPanel {

    //Host, cook, waiters and customers
    private HostAgent host = new HostAgent("Sarah");
    private CookAgent cook = new CookAgent("Jerry");
    private MarketAgent market = new MarketAgent("Whole Foods", 0, 0, 0, 7, 0);
    private MarketAgent market2 = new MarketAgent("Ralphs", 20, 20, 20, 20, 20);
    private MarketAgent market3 = new MarketAgent("Costco", 20, 20, 20, 20, 20);
    private CashierAgent cashier = new CashierAgent("Bob");
    
    private List<WaiterAgent> waiters = new ArrayList<WaiterAgent>();
    private List<CustomerAgent> customers = new ArrayList<CustomerAgent>();

    private JPanel restLabel = new JPanel();
    private ListPanel customerPanel = new ListPanel(this, "Customers");
    private ListPanel waiterPanel = new ListPanel (this, "Waiters");
    private JPanel group = new JPanel();


    private RestaurantGui gui; //reference to main gui
    private CookGui cookgui = new CookGui(cook, gui);

    public RestaurantPanel(RestaurantGui gui) {
        this.gui = gui;
        host.startThread();
        market.setCashier(cashier);
        market2.setCashier(cashier);
        market3.setCashier(cashier);
        market.startThread();
        market2.startThread();
        market3.startThread();
        cashier.startThread();
        cook.addMarket(market);
        cook.addMarket(market2);
        cook.addMarket(market3);
        cook.setGui(cookgui);
        gui.animationPanel.addGui(cookgui);
        cook.startThread();
        cookgui.updatePosition();
        setLayout(new GridLayout(1, 2, 20, 20));
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));

        group.add(customerPanel);
        group.add(waiterPanel);

        initRestLabel();
        add(restLabel);
        add(group);
    }
    
    public void PauseandUnpauseAgents() {
    	//System.out.println("in pause and unpause");
    	for (WaiterAgent w : waiters) {
    		w.pauseOrRestart();
    	}
    	for (CustomerAgent c : customers) {
    		c.pauseOrRestart();
    	}
    	cook.pauseOrRestart();
    	host.pauseOrRestart(); 	
    	market.pauseOrRestart();
    	market2.pauseOrRestart();
    	market3.pauseOrRestart();
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
        //restLabel.setLayout(new BoxLayout((Container)restLabel, BoxLayout.Y_AXIS));
        restLabel.setLayout(new BorderLayout());
        label.setText(
                "<html><h3><u>Tonight's Staff</u></h3><table><tr><td>host:</td><td>" + host.getName() + "</td></tr></table><h3><u> Menu</u></h3><table><tr><td>Steak</td><td>$14.50</td></tr><tr><td>Chicken</td><td>$12.50</td></tr><tr><td>Salad</td><td>$7.50</td></tr><tr><td>Pizza</td><td>$10.50</td></tr><tr><td>Latte</td><td>$3.25</td></tr></table><br></html>");
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
    public void showInfo(String type, String name) {

        if (type.equals("Customers")) {
            for (int i = 0; i < customers.size(); i++) {
                CustomerAgent temp = customers.get(i);
                if (temp.getName() == name)
                    gui.updateInfoPanel(temp);
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

    /**
     * Adds a customer or waiter to the appropriate list
     *
     * @param type indicates whether the person is a customer or waiter (later)
     * @param name name of person
     */
    public void addPerson(String type, String name, boolean isHungry) {

    	if (type.equals("Customers")) {
    		CustomerAgent c = new CustomerAgent(name);	
    		CustomerGui g = new CustomerGui(c, gui, customers.size());
    	//new	
    		if(isHungry==true) { 
    			g.setHungry();
    		}
    		// new
    		gui.animationPanel.addGui(g);// dw
    		c.setHost(host);
    		c.setGui(g);
    		c.setCashier(cashier);
    		Random rand = new Random();
    		c.setCash(rand.nextInt(20)+10);
    		//c.setCash(10);
    		customers.add(c);
    		c.startThread();
    		g.updatePosition();
    		
    	}
    	else if (type.equals("Waiters")) {
    		WaiterAgent w = new WaiterAgent(name);
    		WaiterGui g = new WaiterGui(w, waiters.size());
    		gui.animationPanel.addGui(g);
    		w.setHost(host);
    		w.setCook(cook);
    		w.setCashier(cashier);
    		host.addWaiter(w);
    		w.setGui(g);
    		waiters.add(w);
    		w.startThread();
    		g.updatePosition();
    		
    	}
    }

}
