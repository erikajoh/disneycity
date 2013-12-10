package restaurant_rancho.gui;

import agent_rancho.Agent;
import restaurant_rancho.CashierAgent;
import restaurant_rancho.CookAgent;
import restaurant_rancho.CustomerAgent;
import restaurant_rancho.HostAgent;
import restaurant_rancho.WaiterAgent;
import bank.gui.Bank;
import simcity.PersonAgent;
import simcity.gui.SimCityGui;
import simcity.gui.trace.AlertLog;
import simcity.gui.trace.AlertTag;
import simcity.RestMenu;
import simcity.Restaurant;
import restaurant_rancho.ProducerConsumerMonitor;
import restaurant_rancho.WaiterAgentPC;
import simcity.interfaces.Market_Douglass;

import javax.swing.*;

import simcity.interfaces.Person;
import restaurant_rancho.WaiterAgentNorm;
import market.Market;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Hashtable;

import simcity.interfaces.Bank_Douglass;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class RestaurantRancho extends JPanel implements Restaurant {


	private static final long serialVersionUID = 1L;
	//Host, cook, waiters and customers
	String name;
	String type;
	Bank_Douglass bank;
	Market_Douglass market;

	private Hashtable<Person, CustomerAgent> returningCusts = new Hashtable<Person, CustomerAgent>();
    private HostAgent host;
    private CookAgent cook;
    private CashierAgent cashier;    
    private List<WaiterAgent> waiters = Collections.synchronizedList( new ArrayList<WaiterAgent>());
    private List<CustomerAgent> customers = Collections.synchronizedList(new ArrayList<CustomerAgent>());
    private JPanel restLabel = new JPanel();
    private ListPanel customerPanel = new ListPanel(this, "Customers");
    private ListPanel waiterPanel = new ListPanel (this, "Waiters");
    private JPanel group = new JPanel();
    private RestMenu menu = new RestMenu();
    boolean isOpen = true;
    public ProducerConsumerMonitor orderStand = new ProducerConsumerMonitor();
    
    private MyWaitingCook waitingCook;
    private MyWaitingCashier waitingCashier;
    private MyWaitingHost waitingHost;
    private List<MyWaitingWaiter> waitingWaiters = new ArrayList<MyWaitingWaiter>();
    private List<MyWaitingWaiter> waitingPCWaiters = new ArrayList<MyWaitingWaiter>();
    private List<MyWaitingCustomer> waitingCustomers  = new ArrayList<MyWaitingCustomer>();
    
    private int numWorkers;
    
    public boolean hostShiftDone = false;
    public boolean cookShiftDone = false;
    public boolean waiterWorking = false;
    public boolean cashierShiftDone = false;
    
    public boolean shiftsSwitching = false;
    
    private SimCityGui gui;
    private CookGui cookgui;

    public RestaurantRancho(SimCityGui g, String n) {
    	name = n;
    	type = "Mexican";
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
    
    public void setBank(Bank_Douglass b) {
    	bank = b;
    	if (cashier!=null) setBank(b);
    }
    
    public boolean isOpen() {
    	return (!shiftsSwitching && host!=null && cook!=null && waiters.size()!=0 && cashier!=null && isOpen);
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
    
    public boolean isChangingShifts() {
    	if (host!=null && host.isWorking==false) {
    		removeHost();
    	}
    	if (cook!=null && cook.isWorking==false) {
    		removeCook();
    	}
    	if (cashier!=null && cashier.isWorking==false) {
    		removeCashier();
    	}
    	synchronized(waiters) {
    	for (WaiterAgent w : waiters ) {
    		if (w.isWorking==false) {
    			removeWaiter(w);
    		}
    	}
    	}
    	if (shiftsSwitching) {
    		if (host!=null && host.isWorking == true) 
    			return true;
    		else if (waiters.size()!=0) {
    			synchronized (waiters) {
    			for (WaiterAgent w : waiters) {
    				if (w.isWorking == true)
    					return true;
    			}
    			}
    		}
    		else if (cook!=null && cook.isWorking == true) {
    			return true; 
    		}
    		else if (cashier!=null && cashier.isWorking == true) {
    			return true;
    		}
    		else {
    			shiftsSwitching = false;
    			return false;
    		}
    	}
    	return false;
    	
    	
    }
    
    public String getRestaurantName() { return name; }
    public String getType() { return type; }
    
   // public void personAs(String type, String name, PersonAgent p) {
    public void personAs(Person p, String type, String name, double money){
    	addPerson(p, type, name, money);
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

        if (type.equals("Customer")) {
            for (int i = 0; i < customers.size(); i++) {
                CustomerAgent temp = customers.get(i);
                //if (temp.getName() == name)
                  //  gui.updateInfoPanel(temp);
            }
        }
        if (type.equals("Waiter")) {
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
    
    public void setFoodAmount(String choice, int amount) {
    	if (cook!=null) 
    		cook.setAmount(choice, amount);
    }
    
    public void addPerson(Person p, String type, String name, double money) {
    	if (!isOpen && type.equals("Customer")) {
    		AlertLog.getInstance().logMessage(AlertTag.RESTAURANT, name, " told to go home because Rancho de Zocalo is now closed"); 
    		p.msgDoneEating(false, money);
    	}
    	
    	// if changing shifts, don't want to add new workers with pointers to old workers as workers from shift get ready to leave work
    	if (isOpen) {

    		if (type.equals("Customer")) {
    			
    			if ((p!=null) && returningCusts.containsKey(p)) {
    				returningCusts.get(p).getGui().setHungry();	
    			}
    			else {
    			
    			CustomerAgent c = new CustomerAgent(name);	
    			CustomerGui g = new CustomerGui(c, gui, customers.size());
    			if (p!=null) c.setPerson(p);
    			//returningCusts.put(p, c);
    			g.setHungry();
    			gui.ranchoAniPanel.addGui(g);
    			if (host!=null) c.setHost(host);
    			c.setGui(g);
    			if (cashier!=null) c.setCashier(cashier);
    			c.setCash(money);
    			customers.add(c);
    			c.startThread();
    			g.updatePosition();
    			}
    		
    		}
    		else if (type.equals("Waiter")) {
    			waiterWorking = true;
    			numWorkers++;
    			WaiterAgentNorm w = new WaiterAgentNorm(name, this);
    			WaiterGui g = new WaiterGui(w, waiters.size());
    			w.isWorking = true;
    			if (p!=null) w.setPerson(p);
    			gui.ranchoAniPanel.addGui(g);
    			if (host!=null) w.setHost(host);
    			if (cook!= null) w.setCook(cook);
    			if (cashier!=null)w.setCashier(cashier);
    			if (host!=null) host.addWaiter(w);
    			w.setGui(g);
    			waiters.add(w);
    			w.startThread();
    			g.updatePosition();
    		
    		}
    		else if (type.equals("WaiterPC")) {
    			numWorkers++;
    			waiterWorking = true;
    			WaiterAgentPC w = new WaiterAgentPC(name, this);
    			WaiterGui g = new WaiterGui(w, waiters.size());
    			w.isWorking = true;
    			if (p!=null) w.setPerson(p);
    			gui.ranchoAniPanel.addGui(g);
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
    		//if (host == null) {
    			numWorkers++;
    			hostShiftDone = true;
    			host = new HostAgent(name);
    			host.isWorking = true;
      			if (p!=null) host.setPerson(p);
    			host.startThread();
    			initRestLabel();
    			for (WaiterAgent w : waiters) {
    				host.addWaiter(w);
    				w.setHost(host);
    			}
    		}
    //	}
    		else if (type.equals("Cook")) {
    			numWorkers++;
    			cookShiftDone = true;
    			cook = new CookAgent(name, this, market);
    			cookgui = new CookGui(cook);
    			cook.isWorking = true;
    			if (p!=null) cook.setPerson(p);
    			cook.setGui(cookgui);
    			//cookgui.updatePosition();
    			for (WaiterAgent w : waiters) {
    				w.setCook(cook);	
    			}
    			gui.ranchoAniPanel.addGui(cookgui);
    			cook.startThread();
    		}
    		else if (type.equals("Cashier")) {
    			numWorkers++;
    			cashierShiftDone = true;
    			cashier = new CashierAgent(name, this);
    			if (p!=null) cashier.setPerson(p);
    			if (bank!=null) cashier.setBank(bank);
    			if (market!=null) cashier.setMarket(market);
    			for (WaiterAgent w : waiters) {
    				w.setCashier(cashier);
    			}
    			cashier.startThread();
    	}
    	}
    	//if changing shifts, add patrons and workers to a list of waitingcustomers, waiters, etc until previous shift is cleared out, then a new agent for them will be spawned
    	else {
    		if (type.equals("Customer")) {
    			waitingCustomers.add(new MyWaitingCustomer(p, name, money));
    		}
    		if (type.equals("Host")) {
    			waitingHost = new MyWaitingHost(p, name);
    		}
    		if (type.equals("Waiter")) {
    			waitingWaiters.add(new MyWaitingWaiter(p, name));
    		}
    		if (type.equals("WaiterPC")) {
    			waitingPCWaiters.add(new MyWaitingWaiter(p, name));
    		}
    		if (type.equals("Cook")) {
    			waitingCook = new MyWaitingCook(p, name);
    		}
    		if (type.equals("Cashier")) {
    			waitingCashier = new MyWaitingCashier(p, name);
    		}
    	}		
    }
    
    public void removeCashier() {
    	cashier = null;
    	numWorkers--;
    }
    
    public void removeHost() {
    	host = null;
    	numWorkers--;
    }
    
    public void removeCook() {
    	cook = null;
    	numWorkers--;
    }
    
    public void removeWaiter(WaiterAgent w) {
    	waiters.remove(w);
    	numWorkers--;
    }
    
    public void removeMe(WaiterAgent wa, String type) {
    	if (type == "Waiter") {
    		waiters.remove(wa);
    		System.out.println("removing waiter " + wa.getName());
    	}
    }
	@Override
	public void setMarket(Market_Douglass m) {
		market = m;
		if (cashier!=null) {
			cashier.setMarket(m);
		}
		if (cook!=null) {
			cook.setMarket(m);
			
		}
		
	}

	@Override
	public void msgHereIsOrder(String food, int quantity, int ID) {
		cook.msgHereIsOrder(food, quantity, ID);
	}

	@Override
	public void StartOfShift() {
		isOpen = true;
	}
	
	@Override
	public void EndOfShift() {
		isOpen = false;
		System.out.println("RESTAURANT RANCHO GOT END OF SHIFT");
		double wage;
		if (cashier!=null) {
			wage = cashier.money - 500;
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
	public void msgHereIsBill(Market_Douglass m, double amount) {
		cashier.msgHereIsMarketBill(m, amount);
	}
	
	class MyWaitingCustomer {
		Person per;
		String name;
		double money;
		
		MyWaitingCustomer(Person p, String n, double m) {
			per = p;
			name = n;
			money = m;
		}
	}
	
	class MyWaitingHost {
		Person per;
		String name;
		MyWaitingHost (Person p, String n) {
			per = p;
			name = n;
		}
	}
	
	class MyWaitingCook {
		Person per;
		String name;
		MyWaitingCook (Person p, String n) {
			per = p;
			name = n;
		}
	}
	
	class MyWaitingCashier {
		Person per;
		String name;
		MyWaitingCashier (Person p, String n) {
			per = p;
			name = n;
		}
	}
	
	class MyWaitingWaiter {
		Person per;
		String name;
		MyWaitingWaiter (Person p, String n) {
			per = p;
			name = n;
		}
	}
}
