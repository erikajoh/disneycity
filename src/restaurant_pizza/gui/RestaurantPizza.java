package restaurant_pizza.gui;

import restaurant_pizza.CashierAgent;
import restaurant_pizza.CookAgent;
import restaurant_pizza.CustomerAgent;
import restaurant_pizza.HostAgent;
import restaurant_pizza.MarketAgent;
import restaurant_pizza.WaiterAgent;
import restaurant_pizza.interfaces.Customer;
import restaurant_pizza.interfaces.Waiter;
import bank.gui.Bank;
import simcity.PersonAgent;
import agent_pizza.Agent;
import simcity.gui.SimCityGui;
import simcity.interfaces.Market_Douglass;
import simcity.interfaces.Person;
import simcity.interfaces.Bank_Douglass;

import javax.swing.*;

import market.Market;
import simcity.RestMenu;
import simcity.Restaurant;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class RestaurantPizza extends JPanel implements Restaurant {
	
	public String name;
	private JTabbedPane tabbedPane = new JTabbedPane();
    private JPanel restLabel = new JPanel();
    private ListPanel customerPanel = new ListPanel(this, "Customer");
    private ListPanel waiterPanel = new ListPanel(this, "Waiter");
    private JPanel group = new JPanel();
    public RestMenu menu = new RestMenu();
    Bank_Douglass bank;
    Market_Douglass market;
    boolean isOpen = false;
    
    private static final int GAP_SIZE = 10;
    private static int WAITER_X_START = 140;
    private static int WAITER_Y_START = 110;
    
    private int customerInd = 0;
	
    //Host, cook, waiters and customers
    private HostAgent host;
    private LinkedList<WaiterGui> waiterGuis = new LinkedList<WaiterGui>();
    private CookAgent cook;
    private CookGui cookGui;
    private CashierAgent cashier;
    String type;
    
    private Hashtable<Person, CustomerAgent> returningCusts = new Hashtable<Person, CustomerAgent>();
    private Vector<CustomerAgent> customers = new Vector<CustomerAgent>();
    private Vector<WaiterAgent> waiters = new Vector<WaiterAgent>();
    private Vector<MarketAgent> markets = new Vector<MarketAgent>();
    private SimCityGui gui; //reference to main gui
    
    public RestaurantPizza(SimCityGui gui, String name) {
    	
    	this.name = name;
    	type = "Italian";
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
    
    public String getRestaurantName() {
    	return name;
    }
    
    public RestMenu getMenu() {
    	return menu;
    }
    
    public String[] getFoodNames(){
    	return menu.menuList.toArray(new String[0]);
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
    
    public boolean isOpen() {
    	return (cook!=null && waiters.size()>0 && cashier!=null && host!=null && isOpen);
    }
    
    public void setFoodAmount(String choice, int amount) {
    	if (cook!=null) 
    		cook.setAmount(choice, amount);
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
    public void personAs(Person p, String type, String name, double money){
    	addPerson(p, type, name, money);
    }

    /**
     * Adds a customer or waiter to the appropriate list
     *
     * @param type indicates whether the person is a customer or waiter (later)
     * @param name name of person
     */
    public void addPerson(Person p, String type, String name, double money) {

    	if (type.equals("Customer")) {
    		//if ((p!=null) && returningCusts.containsKey(p)) {
    		//	returningCusts.get(p).getGui().setHungry();	
    		//}
    		//else {
    		CustomerAgent c = new CustomerAgent(name);	
    		c.setMoney(money);
    		//returningCusts.put(p, c);
    		if (p!=null) c.setPerson(p);
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
    		//}
    	}
    	else if (type.equals("Waiter")) {
    		WaiterAgent newWaiter = new WaiterAgent(name);	
    		WaiterGui newWaiterGui = new WaiterGui(newWaiter, WAITER_X_START, WAITER_Y_START);
    		WAITER_X_START += newWaiterGui.mySize;
    		if (p!=null) { newWaiter.setPerson(p);}
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
    		if (p!=null) host.setPerson(p);
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
    		if (p!=null) cook.setPerson(p);
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
    		if (p!=null) cashier.setPerson(p);
    		for (WaiterAgent w: waiters) {
    			w.setCashier(cashier);
    		}
    		cashier.startThread();
    	}
    }

	@Override
	public void setBank(Bank_Douglass b) {
		bank = b;
		
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
		System.out.println("RESTAURANT GOT END OF SHIFT");

		if (host!=null) {
			host.msgShiftDone();
			for (int i = 0; i < waiters.size(); i++) {
				if (cashier!=null) cashier.subtract(10);
			}
		}
		else {
			if (cashier!=null) { cashier.msgShiftDone(); cashier.subtract(10); }
			for (int i = 0; i < waiters.size(); i++) {
				WaiterAgent w = waiters.get(i);
				w.msgShiftDone(false);
				if (cashier!=null) cashier.subtract(10);
			}
			if (cook!=null) {
				cook.msgShiftDone();
				if (cashier!=null) cashier.subtract(10);
			}
		}
		
	}
	
	@Override
	public void msgHereIsBill(Market_Douglass m, double amt) {
		// TODO Auto-generated method stub
		
	}
	
}
