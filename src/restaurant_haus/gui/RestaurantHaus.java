package restaurant_haus.gui;

import restaurant_haus.CashierAgent;
import restaurant_haus.CookAgent;
import restaurant_haus.CustomerAgent;
import restaurant_haus.HostAgent;
import restaurant_haus.WaiterAgent;
import bank.gui.Bank;
import simcity.PersonAgent;
import simcity.RestMenu;
import simcity.gui.SimCityGui;
import simcity.Restaurant;
import market.Market;
import simcity.interfaces.Bank_Douglass;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import simcity.interfaces.Market_Douglass;
import simcity.interfaces.Person;

import javax.swing.JTabbedPane;

import market.Market;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class RestaurantHaus extends JPanel implements Restaurant{

    //Host, cook, waiters and customers
    private HostAgent host;
    private CashierAgent cashier;
    CookAgent cook;
    private Hashtable<Person, CustomerAgent> returningCusts = new Hashtable<Person, CustomerAgent>();
    private Vector<Market_Douglass> markets = new Vector<Market_Douglass>();
    private Vector<CustomerAgent> customers = new Vector<CustomerAgent>();
    private Vector<WaiterAgent> waiters = new Vector<WaiterAgent>();
    String type;
    boolean isOpen = true;
    
    private boolean isPaused = false;
    
    private JPanel restLabel = new JPanel();
    private ListPanel customerPanel = new ListPanel(this, "Customers");
    private ListPanel waiterPanel = new ListPanel(this, "Waiters");
    private JPanel group = new JPanel();

    private SimCityGui gui; //reference to main gui
    
    private JPanel creditsPanel;
    private JLabel nameLabel;
    private JLabel pictureLabel;
    private ImageIcon personalPicture; 
    private RestMenu menu = new RestMenu();
    String name;
    Bank_Douglass bank;
    
    double money;
    
    private JTabbedPane restruantPane = new JTabbedPane();

    public RestaurantHaus(SimCityGui gui, String name) {
    	money = 500.00d;
        this.gui = gui;
        this.name = name;
        type = "German";
        
        menu.addItem("Pastrami Cheeseburger", 11.49);
        menu.addItem("Chicken Sausage Pretzel Roll", 8.99);
        menu.addItem("BLT Flatbread", 8.79);
        menu.addItem("Apple & Cheddar Salad", 7.99);
        setLayout(new GridLayout(1, 1));
        group.setLayout(new GridLayout(1, 3, 10, 10));

        group.add(customerPanel);

        initRestLabel();
        
        restruantPane.addTab("Menu", restLabel);
        restruantPane.addTab("Customers", group);
        restruantPane.addTab("Waiters", waiterPanel);
        restruantPane.addTab("Credits", creditsPanel);
        
        add(restruantPane);
        //add(restLabel);
        //add(group);
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
                "<html><h3><u>Tonight's Staff</u></h3><table><tr><td>host:</td><td>" + "name" + "</td></tr></table><h3><u> Menu</u></h3><table><tr><td>Steak</td><td>$15.99</td></tr><tr><td>Chicken</td><td>$10.99</td></tr><tr><td>Salad</td><td>$5.99</td></tr><tr><td>Pizza</td><td>$8.99</td></tr></table><br></html>");

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
  /*  public void showInfo(String type, String name) {

        if (type.equals("Customers")) {

            for (int i = 0; i < customers.size(); i++) {
                CustomerAgent temp = customers.get(i);
                if (temp.getName() == name)
                    gui.updateInfoPanel(temp);
            }
        }
        
        if (type.equals("Waiters")) {
        	for (WaiterAgent waiter : waiters) {
        		if(waiter.getName() == name) {
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
    		CustomerAgent c = new CustomerAgent(name);	
    		if (p!=null) c.setPerson(p);
    		//returningCusts.put(p, c);
    		CustomerGui g = new CustomerGui(c, gui);
    		gui.hausAniPanel.addGui(g);// dw
    		if (host!=null) c.setHost(host);
    		c.setGui(g);
    		c.setMoney(money);
    		customers.add(c);
    		c.startThread();
    		c.getGui().setHungry();
    		//}
    	}
    	if(type.equals("Waiter")) {
    		WaiterAgent w = new WaiterAgent(name);
    		if (p!=null) w.setPerson(p);
    		WaiterGui g = new WaiterGui(w, gui);    		
    		gui.hausAniPanel.addGui(g);// dw
    		if (host!=null) w.setHost(host);
    		if (cashier!= null) w.setCashier(cashier);
    		w.setGui(g);
    		if (host!=null) host.msgGiveJob(w);
    		if (cook!= null) w.setCook(cook);
    		waiters.add(w);
    		w.startThread();
    	}
    	else if (type.equals("Host")) {
    		host = new HostAgent(name);
    		if (p!=null) host.setPerson(p);
    		for (WaiterAgent w : waiters) {
    			w.setHost(host);
    			host.msgGiveJob(w);
    		}
    		if (cashier!=null) cashier.setMenu(host.getMenu());
    		if (cook!=null) cook.setMenu(host.getMenu());
    		host.startThread();
    	}
    	else if (type.equals("Cook")) { 
    		cook = new CookAgent(name, this);
    		if (p!=null) cook.setPerson(p);
    		CookGui cG = new CookGui(cook, gui);
    		gui.hausAniPanel.addGui(cG);
    	    cook.setGui(cG);
            if (host!=null) cook.setMenu(host.getMenu());
            if (cashier!=null) cook.setCashier(cashier);
            for (Market_Douglass m : markets) {
            	cook.addMarket(m);
            }
            for (WaiterAgent w : waiters) {
            	w.setCook(cook);
            }
    	    cook.startThread();
    	}
    	else if (type.equals("Cashier")) { 
    		cashier = new CashierAgent(name, this, money);
    		if (p!=null) cashier.setPerson(p);
    		if (host!=null) cashier.setMenu(host.getMenu());
    		for (WaiterAgent w : waiters) {
    			w.setCashier(cashier);
    		}
    		if (cook!=null) cook.setCashier(cashier);
    		cashier.startThread();
    	}
 
        
    	
    }
    
    public void pause() {
    	if(isPaused) {
    		isPaused = false;
    		cook.unpause();
    		host.unpause();
    		for(CustomerAgent c : customers) {
    			c.unpause();
    		}
    		for(WaiterAgent w : waiters) {
    			w.unpause();
    		}
    		//gui.animationPanel.unpauseAnim();
    	}
    	
    	else {
    		isPaused = true;
    		cook.pause();
    		host.pause();
    		for(CustomerAgent c : customers) {
    			c.pause();
    		}
    		for(WaiterAgent w : waiters) {
    			w.pause();
    		}
    		//gui.animationPanel.pauseAnim();
    	}
    }

	@Override
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

	@Override
	public String getRestaurantName() {
		return name;
	}

	@Override
	public void setBank(Bank_Douglass b) {
		bank = b;		
	}

	@Override
	public boolean isOpen() {
		return (cook!=null && waiters.size()>0 && cashier!=null && host!=null && isOpen);
	}

	@Override
	public void personAs(Person p, String type, String name, double money) {
		addPerson(p, type, name, money);
		
	}

	@Override
	public void setMarket(Market_Douglass m) {
		markets.add(m);
		cook.addMarket(m);
	}

	public String getType() {
		return type;
	}

	@Override
	public void msgHereIsOrder(String food, int quantity, int ID) {
		cook.msgOrderDelivered(food, quantity, ID);
		
	}

	@Override
	public void StartOfShift() {
		isOpen = true;
	}

	@Override
	public void EndOfShift() {
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
				w.msgShiftDone();
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
		cashier.msgYourBillIs(m, amt);
	}
}
