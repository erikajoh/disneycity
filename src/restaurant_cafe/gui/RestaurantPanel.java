package restaurant_cafe.gui;

import restaurant_cafe.CashierAgent;
import restaurant_cafe.CookAgent;
import restaurant_cafe.CustomerAgent;
import restaurant_cafe.HostAgent;
import restaurant_cafe.MarketAgent;
import restaurant_cafe.WaiterAgent;
import restaurant_cafe.CookAgent.Order;
import restaurant_cafe.interfaces.Market;

import javax.swing.*;

import agent_cafe.Agent;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Vector;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class RestaurantPanel extends JPanel implements ActionListener {

    //Host, cook, waiters and customers
    private HostAgent host = new HostAgent("Sarah");
    private HostGui hostGui = new HostGui(host);

    
    private CookAgent cook;
    private CashierAgent cashier;

    private Vector<CustomerAgent> customers = new Vector<CustomerAgent>();
    private Vector<WaiterAgent> waiters = new Vector<WaiterAgent>();
    
	public Collection<Food> foods = Collections.synchronizedList(new ArrayList<Food>());
    private Menu menu;

    private JPanel restLabel = new JPanel();
    private ListPanel customerPanel = new ListPanel(this, "Customers");
    private ListPanel waiterPanel = new ListPanel(this, "Waiters");
    private JButton  pauseButton = new JButton("Pause Agents");
    private JPanel pauseGroup = new JPanel();
    private JPanel group = new JPanel();

    private RestaurantGui gui; //reference to main gui

    public RestaurantPanel(RestaurantGui gui) {
        this.gui = gui;
        host.setGui(hostGui);

        host.startThread();
                
        Food food = new Food("Steak", 3000, 5, 4, 8, 15.99);
        foods.add(food);
        food = new Food("Chicken", 2500, 5, 4, 8, 10.99);
        foods.add(food);
        food = new Food("Salad", 2000, 2, 4, 8, 5.99);
        foods.add(food);
        food = new Food("Pizza", 3500, 5, 4, 8, 8.99);
        foods.add(food);
        
        menu = new Menu(foods);
        
        cashier = new CashierAgent("Lindsey", menu);
        CashierGui cashierGui = new CashierGui(cashier, gui);
		cashier.setGui(cashierGui);
		gui.animationPanel.addGui(cashierGui);
        cashier.startThread();
       
        cook = new CookAgent("Ralph", foods);
        for(int i = 0; i<3; i++){
            MarketAgent market = new MarketAgent(i, menu, 5);
            market.setCashier(cashier);
            market.startThread();
        	cook.addMarket(market);
        }
    	CookGui cookGui = new CookGui(cook, gui);
		cook.setGui(cookGui);
		gui.animationPanel.addGui(cookGui);
        cook.startThread();
        
        setLayout(new GridLayout(1, 2, 20, 20));
        group.setLayout(new GridLayout(1, 2, 10, 10));

        group.add(customerPanel);
        group.add(waiterPanel);

        initRestLabel();
        pauseGroup.setLayout(new BorderLayout());
        pauseButton.addActionListener(this);
        pauseGroup.add(pauseButton, BorderLayout.NORTH);
        pauseGroup.add(restLabel, BorderLayout.CENTER);
        add(pauseGroup);
        add(group);
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
                "<html><h3><u>Tonight's Staff</u></h3><table><tr><td>host:</td><td>" + host.getName() + "</td></tr></table><h3><u> Menu</u></h3><table><tr><td>Steak</td><td>$15.99</td></tr><tr><td>Chicken</td><td>$10.99</td></tr><tr><td>Salad</td><td>$5.99</td></tr><tr><td>Pizza</td><td>$8.99</td></tr></table><br></html>");
        label.setFont(new Font("Sans-Serif", Font.PLAIN, 12));
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
        else  if (type.equals("Waiters")) {
        	 for (WaiterAgent waiter : waiters) {
                 if (waiter.getName() == name){
                     gui.updateInfoPanel(waiter);
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
    public void addPerson(String type, String name, boolean hungryState) {

    	if (type.equals("Customers")) {
    		CustomerAgent c = new CustomerAgent(name, customers.size());	
    		CustomerGui g = new CustomerGui(c, gui);
    		
    		if(hungryState == true){
    			g.setHungry();
    		}
    		
    		if(name.equals("Cheap")){
    			c.setBalance(6.00);
    		}
    		else if(name.equals("Markets")){
    			c.setBalance(6.00);
    			Collection<Market> markets = cook.getMarkets();
    			Food food = null;
    			synchronized(foods){ 
    			  for(Food f : foods){
    				  if(f.getName().equals("Salad")){
    					  f.setAmount(2);
    					  food = f;
    				  }
    			  }
    			}
    			synchronized(markets){
    			  for(Market market : markets){
    				  market.setStock(food, 2);
    			  }
    			}
    		}
       		else if(name.equals("BrokeCashier")){
    			cashier.setBalance(5.00);
    		}
    		else if(name.equals("Broke")){
    			c.setBalance(0.00);
    		}
    		else if(name.equals("Unlucky")){
    			c.setBalance(6.00);
    			Collection<Market> markets = cook.getMarkets();
    			synchronized(foods){ 
    			  for(Food food : foods){
    				  if(food.getName().equals("Salad")){
    					  food.setAmount(0);
    				  }
    			  }
    			  for(Market market : markets){
    				  market.setAllStockToAmt(0);
    			  }
    		    }
    		 }
    		else if(name.equals("Flaky")){
    			c.setBalance(0.00);
    			c.ignorePrices();
    		}
    		
    		gui.animationPanel.addGui(g);// dw
    		c.setHost(host);
    		c.setCashier(cashier);
    		c.setGui(g);
    		customers.add(c);
    		c.startThread();
    	}
    	else if (type.equals("Waiters")) {
    		WaiterAgent w = new WaiterAgent(name, menu, waiters.size()+1);	
    		WaiterGui g = new WaiterGui(w, gui);
    		gui.animationPanel.addGui(g);
    		w.setHost(host);
    		w.setCashier(cashier);
    		
    		host.addWaiter(w);
    		w.setCook(cook);
    		waiters.add(w);
    		w.setGui(g);
    		w.startThread();
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
    
    public String getNameFieldText(){
    	return gui.getNameFieldText();
    }
    public boolean getHungryState(){
    	return gui.getHungryState();
    }
}

