package restaurant_pizza.gui;

import restaurant_pizza.CashierAgent;
import restaurant_pizza.CookAgent;
import restaurant_pizza.CustomerAgent;
import restaurant_pizza.HostAgent;
import restaurant_pizza.MarketAgent;
import restaurant_pizza.WaiterAgent;
import restaurant_pizza.interfaces.Customer;
import restaurant_pizza.interfaces.Waiter;
import simcity.PersonAgent;
import agent_pizza.Agent;
import simcity.gui.SimCityGui;
import javax.swing.*;
import simcity.RestMenu;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URL;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class RestaurantPizza extends JPanel {

	private JTabbedPane tabbedPane = new JTabbedPane();
	private String name;
    private JPanel restLabel = new JPanel();
    private ListPanel customerPanel = new ListPanel(this, "Customer");
    private ListPanel waiterPanel = new ListPanel(this, "Waiter");
    private JPanel group = new JPanel();
    public RestMenu menu = new RestMenu();
    
    private static final int GAP_SIZE = 10;
    
    private static int WAITER_X_START = 250;
    private static int WAITER_Y_START = 250;
    
    private int customerInd = 0;
	
    //Host, cook, waiters and customers
    private HostAgent host;
    private LinkedList<WaiterGui> waiterGuis = new LinkedList<WaiterGui>();
    private CookAgent cook;
    private CookGui cookGui;
    private CashierAgent cashier;
    
    private Vector<CustomerAgent> customers = new Vector<CustomerAgent>();
    private Vector<WaiterAgent> waiters = new Vector<WaiterAgent>();
    private Vector<MarketAgent> markets = new Vector<MarketAgent>();
    private SimCityGui gui; //reference to main gui
    
    public RestaurantPizza(SimCityGui gui) {
    	
    	name = "Pizza Port";
        // hardcoding markets
    	menu.addItem("Marsinara with Meatballs", 9.49 );
    	menu.addItem("Chicken Fusilli", 9.49);
    	menu.addItem("Pepperoni Pizza",  6.99);
    	menu.addItem("Celestial Caesar Chicken Salad", 8.49);
    	menu.addItem("Bread Sticks", 4.99);
        MarketAgent ma1 = new MarketAgent("Planet A");
        MarketAgent ma2 = new MarketAgent("Planet B");
        MarketAgent ma3 = new MarketAgent("Planet C");
        markets.add(ma1);
        markets.add(ma2);
        markets.add(ma3);
        for(int i = 0; i < markets.size(); i++) {
        	markets.get(i).startThread();
        	//markets.get(i).setCook(cook);
        	//markets.get(i).setCashier(cashier);
        }
       // cook.setMarkets(markets);
    	
    	this.gui = gui;
       
        
        setLayout(new GridLayout(0, 2, GAP_SIZE, GAP_SIZE));
        group.setLayout(new GridLayout(0, 1, GAP_SIZE, GAP_SIZE));
        group.add(customerPanel);
        
       // initRestLabel();
        add(restLabel);
        
        tabbedPane.addTab("Waiter", waiterPanel);
        tabbedPane.addTab("Customer", customerPanel);
        add(tabbedPane);
    }

    /**
     * Sets up the restaurant label that includes the menu,
     * and host and cook information
     */
    private void initRestLabel() {
        try {
			JLabel label = new JLabel();
			restLabel.setLayout(new BorderLayout());
			
			String menuDisplayText = "<html><h3>Milliways, the Restaurant at the End of the Universe</h3>"
					+ "<table><tr><td>host:</td><td>"
					+ "name"
					+ "</td></tr></table><h3><u> Menu</u></h3><table>";
			
			URL fileURL = getClass().getResource("/res/MenuTextFile.txt");
			URI fileURI = fileURL.toURI();
			BufferedReader br = new BufferedReader(new FileReader(new File(fileURI)));
			int numItems = Integer.parseInt(br.readLine());
			StringTokenizer st;
			for(int i = 0; i < numItems; i++) {
				st = new StringTokenizer(br.readLine());
				String itemName = st.nextToken();
				double price = Double.parseDouble(st.nextToken());
				int itemCookTime = Integer.parseInt(st.nextToken());
				
				// rounding help from:
				// http://stackoverflow.com/a/15643364/555544
				BigDecimal bd = new BigDecimal(""+price);
				bd = bd.setScale(2, BigDecimal.ROUND_CEILING);
				
				menuDisplayText += "<tr><td>"+itemName+"</td><td>$"+bd.toString()+"</td></tr>";
			}
			menuDisplayText += "</table><br></html>";
			label.setText(menuDisplayText);
			br.close();
			
			restLabel.setBorder(BorderFactory.createRaisedBevelBorder());
			restLabel.add(label, BorderLayout.CENTER);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public LinkedList<Agent> getAllAgents() {
    	LinkedList<Agent> agentList = new LinkedList<Agent>();
    	agentList.add(host);
    	for(WaiterAgent waiter : waiters)
    		agentList.add(waiter);
    	agentList.add(cook);
    	for(CustomerAgent ca : customers) {
    		agentList.add(ca);
    	}
    	return agentList;
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
        if (type.equals("Customer")) {
            for (int i = 0; i < customers.size(); i++) {
                CustomerAgent temp = customers.get(i);
                if (temp.getName() == name)
                    gui.updateInfoPanel(temp);
            }
        }
        if (type.equals("Waiter")) {
            for (int i = 0; i < waiters.size(); i++) {
                WaiterAgent temp = waiters.get(i);
                if (temp.getName() == name)
                    gui.updateInfoPanel(temp);
            }
        }
    }
  */  
    public void personAs(PersonAgent p, String type, String name, double money){
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
    		CustomerAgent c = new CustomerAgent(name);	
    		c.setMoney(money);
    		CustomerGui g = new CustomerGui(c, gui);
    		g.setOffsetWaitingArea(customerInd);
    		customerInd++;
    		gui.pizzaAniPanel.addGui(g);
    		if (host!=null) c.setHost(host);
    		if (cashier!=null) c.setCashier(cashier);
    		c.setGui(g);
    		c.getGui().setHungry();
    		customers.add(c);
    		c.startThread();
    	}
    	else if (type.equals("Waiter")) {
    		WaiterAgent newWaiter = new WaiterAgent(name);	
    		WaiterGui newWaiterGui = new WaiterGui(newWaiter, WAITER_X_START, WAITER_Y_START);
    		WAITER_X_START += newWaiterGui.mySize;
    		
    		newWaiter.setGui(newWaiterGui);
    		gui.pizzaAniPanel.addGui(newWaiterGui);

    		if (host!=null) newWaiter.setHost(host);
    		if (cook!=null) newWaiter.setCook(cook);
    		if (cashier!=null) newWaiter.setCashier(cashier);
    		
    		waiterGuis.add(newWaiterGui);
    		waiters.add(newWaiter);
    		if (host!=null) host.msgAddWaiter(newWaiter);
    		newWaiter.startThread();
    	}
    	else if (type.equals("Host")) {
    		host = new HostAgent(name);
    		for (CustomerAgent c: customers) {
    			c.setHost(host);
    		}
    		for (WaiterAgent w: waiters) {
    			w.setHost(host);
    			host.msgAddWaiter(w);
    		}
    		host.startThread();
    	}
    	else if (type.equals("Cook")) {
    		cook = new CookAgent(name);
    		cookGui = new CookGui(cook);
    		cook.setGui(cookGui);
    		gui.pizzaAniPanel.addGui(cookGui);
    		for (WaiterAgent w : waiters) {
    			w.setCook(cook);
    		}
    		cook.setMarkets(markets);
    		cook.startThread();
    	}
    	else if (type.equals("Cashier")) {
    		cashier = new CashierAgent(name);
    		for (WaiterAgent w: waiters) {
    			w.setCashier(cashier);
    		}
    		cashier.startThread();
    	}
    }
}