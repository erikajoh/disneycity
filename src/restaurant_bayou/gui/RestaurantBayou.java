package restaurant_bayou.gui;

import restaurant_bayou.CashierAgent;
import restaurant_bayou.CookAgent;
import restaurant_bayou.CustomerAgent;
import restaurant_bayou.HostAgent;
import restaurant_bayou.MarketAgent;
import restaurant_bayou.WaiterAgent;
import restaurant_bayou.ProducerConsumerMonitor;
import restaurant_bayou.WaiterAgentNorm;
import restaurant_bayou.WaiterAgentPC;
import simcity.Restaurant;
import simcity.PersonAgent;
import simcity.RestMenu;
import simcity.gui.SimCityGui;
import simcity.gui.trace.AlertLog;
import simcity.gui.trace.AlertTag;
import bank.gui.Bank;
import market.Market;
import market.WorkerAgent;
import simcity.interfaces.Bank_Douglass;

import javax.swing.*;

import java.util.Hashtable;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;

import simcity.interfaces.Person;
import simcity.interfaces.Market_Douglass;

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
//    private ListPanel customerPanel = new ListPanel(this, "customers");
//    private ListPanel waiterPanel = new ListPanel(this, "waiters");
//    private ListPanel marketPanel = new ListPanel(this, "markets");
    private JPanel group = new JPanel();
    private JLabel restSubLabel = new JLabel();
    private Hashtable<Person, CustomerAgent> returningCusts = new Hashtable<Person, CustomerAgent>();
    public RestMenu menu = new RestMenu();
    boolean isOpen;
    private Bank_Douglass bank;
    private Market_Douglass market2;
    public ProducerConsumerMonitor orderStand = new ProducerConsumerMonitor();
    int numWorkers = 0;

    private SimCityGui gui; //reference to main gui

    public RestaurantBayou(SimCityGui gui, String name) {
        this.gui = gui;
        this.name = name;
        type = "Southern";
        setLayout(new GridLayout(1, 2, 20, 20));
        group.setLayout(new GridLayout(1, 2, 10, 10));
        
       
        menu.addItem("Filet Mignon", 42.99);
        menu.addItem("Pan-Seared Salmon", 33.99);
        menu.addItem("Surf and Turf", 45.99);
        menu.addItem("Seafood Jambalaya", 31.99);

        market = new MarketAgent("Market", menu);
    }
    
    public void setBank(Bank_Douglass b) {
    	bank = b;
    }
    
    public String getRestaurantName() {
    	return name;
    }
    
	public void startOfShift() {
		isOpen = true;
	}
    
    public boolean isOpen() {
    	return (cook!=null && waiters.size()>0 && cashier!=null && host!=null && isOpen);
    }
    
    public RestMenu getMenu() {
    	return menu;
    }
    
    public String[] getFoodNames(){
    	return menu.menuList.toArray(new String[0]);
    }
    
    public void removeWaiters() {
    	if (host!=null && host.isWorking==false) {
    		host = null;
    		numWorkers--;
    	}
    	if (cook!=null && cook.isWorking==false) {
    		cook = null;
    		numWorkers--;
    	}
    	if (cashier!=null && cashier.isWorking==false) {
    		cashier = null;
    		numWorkers--;
    	}
    	synchronized(waiters) {
    	for (WaiterAgent w : waiters ) {
    		if (w.isWorking==false) {
    			waiters.remove(w);
    			numWorkers--;
    		}
    	}
    	}

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
    
    public void setFoodAmount(String choice, int amount) {
    	if (cook!=null) {
    		cook.setAmount(choice, amount);
    	}
    }
    
    public void personAs(Person p, String type, String name, double money) {
    	addPerson(p, type, name, money);
    }

    /**
     * Adds a customer or waiter to the appropriate list
     *
     * @param type indicates whether the person is a customer or waiter (later)
     * @param name name of person
     */
    public void addPerson(Person p, String type, String name, double money) {
    	if (!isOpen && type.equals("Customer")) {
    		AlertLog.getInstance().logMessage(AlertTag.RESTAURANT, name, " told to go home because Rancho de Zocalo is now closed"); 
    		if (p != null) p.msgDoneEating(false, money);
    		return;
    	}
    	
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
    		WaiterAgentNorm w = new WaiterAgentNorm(name, this);
        	WaiterGui waiterGui = new WaiterGui(w, gui, waiters.size());
        	if (p!=null) w.setPerson(p);
        	waiters.add(w);
        	w.setGui(waiterGui);
        	gui.bayouAniPanel.addGui(waiterGui);
        	if (host!=null) w.setHost(host);
        	if (cook!=null) w.setCook(cook);
        	w.startThread();
    	}
    	else if (type.equals("WaiterPC")) {
    		WaiterAgentPC w = new WaiterAgentPC(name, this);
        	WaiterGui waiterGui = new WaiterGui(w, gui, waiters.size());
        	if (p!=null) w.setPerson(p);
        	waiters.add(w);
        	w.setGui(waiterGui);
        	gui.bayouAniPanel.addGui(waiterGui);
        	if (host!=null) w.setHost(host);
        	if (cook!=null) w.setCook(cook);
        	if (cashier!=null) w.setCashier(cashier);
        	w.startThread();
    	}
    	else if (type.equals("Host")) {
    		host = new HostAgent(name, this);
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
    		cook = new CookAgent(name, this, menu);
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
    
//    public void resetState(Object person){
//    	if (person instanceof CustomerAgent) customerPanel.resetCB(person);
//    	if (person instanceof WaiterAgent) waiterPanel.resetCB(person);
//    }
    
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
    	WaiterAgentNorm w = new WaiterAgentNorm("w", this);
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
	public void setMarket(Market_Douglass m) {
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
	public void endOfShift() {
		System.out.println("RESTAURANT BAYOU GOT END OF SHIFT");
		double wage;
		if (cashier!=null) {
			wage = cashier.balance - 500;
			cashier.subtract(wage);
		}
		else wage = 0;
		wage = wage/numWorkers;
		System.out.println("WAGE IS " + wage + " NUM WORKERS IS " + numWorkers);
		isOpen = false;
		if (host!=null) {
			host.msgShiftDone(wage);
			if (waiters.size() == 0) {
				if (cook!=null) {
					cook.msgShiftDone(wage);
				}
				if (cashier!=null) {
					cashier.msgShiftDone(wage);
				}
			}
		}
		else {
			if (cashier!=null) { cashier.msgShiftDone(wage);  }
			for (int i = 0; i < waiters.size(); i++) {
				WaiterAgent w = waiters.get(i);
				w.msgShiftDone(false, wage);
			}
			if (cook!=null) {
				cook.msgShiftDone(wage);
			}
		}
		
	}
	
	@Override
	public void msgHereIsBill(Market_Douglass m, double amt) {
		// TODO Auto-generated method stub
		
	}
	
}
