package housing.gui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class HousingPanel extends JPanel {

    private JPanel restLabel = new JPanel();
    private ListPanel customerPanel = new ListPanel(this, "customers");
    private ListPanel waiterPanel = new ListPanel(this, "waiters");
    private ListPanel marketPanel = new ListPanel(this, "markets");
    private JPanel group = new JPanel();
    private JLabel restSubLabel = new JLabel();

    private HousingGui gui; //reference to main gui

    public HousingPanel(HousingGui gui) {
        this.gui = gui;
//        host.setGui(cashierGui);
//        host.setGui(cookGui);
//        for (WaiterAgent w : host.waiters) {
//        	WaiterGui waiterGui = new WaiterGui(w, gui);
//        	w.setGui(waiterGui);
//        	gui.animationPanel.addGui(waiterGui);
//        }
//
//        gui.animationPanel.addGui(cashierGui);
//        gui.animationPanel.addGui(cookGui);
//        host.startThread();
//        for (WaiterAgent w : host.waiters) {
//        	w.startThread();
//        }

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
                "<html><h3>hello world!</h3>welcome to erika's v2.2 restaurant.<h3>host</h3><table><tr><td>" + "</table>" + menuText + "</html"); 
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
//    public void showInfo(String type, String name) {
//
//        if (type.equals("customers")) {
//
//            for (int i = 0; i < host.customers.size(); i++) {
//                CustomerAgent temp = host.customers.get(i);
//                if (temp.getName() == name)
//                    gui.updateInfoPanel(temp);
//            }
//        }
//        
//        if (type.equals("waiters")) {
//
//            for (int i = 0; i < host.waiters.size(); i++) {
//                WaiterAgent temp = host.waiters.get(i);
//                if (temp.getName() == name)
//                    gui.updateInfoPanel(temp);
//            }
//        }
//        
//        if (type.equals("markets")) {
//        	for (int i = 0; i < host.cook.markets.size(); i++) {
//                Market temp = host.cook.markets.get(i).m;
//                if (temp.getName() == name)
//                    gui.updateInfoPanel(temp);
//            }
//        }
//    }

    /**
     * Adds a customer or waiter to the appropriate list
     *
     * @param type indicates whether the person is a customer or waiter (later)
     * @param name name of person
     */
//    public void addPerson(String type, String name, Boolean isHungry) {
//
//    	if (type.equals("customers")) {
//    		CustomerAgent c = new CustomerAgent(name, 100); // hack to make customer start with $100
//    		CustomerGui g = new CustomerGui(c, gui);
//    		if (isHungry) g.setHungry();
//
//    		gui.animationPanel.addGui(g);// dw
//    		c.setHost(host);
//    		c.setGui(g);
//    		host.customers.add(c);
//    		c.startThread();
//    	}
//    	
//    }
    
    public void addButton(JButton b) {
		restLabel.add(b, BorderLayout.SOUTH);
	}
    
//    public void resetState(Object person){
//    	if (person instanceof CustomerAgent) customerPanel.resetCB(person);
//    	if (person instanceof WaiterAgent) waiterPanel.resetCB(person);
//    }
    
//    public void addTable(){
//    	gui.animationPanel.addTable();
//    }
    
//    public String addMarket(Boolean isEmpty){
//    	MarketAgent m = host.addMarket();
//    	m.startThread();
//    	if (isEmpty) m.emptyInventory();
//    	return m.getName();
//    }
    
//    public String addWaiter(Boolean onBreak){
//    	WaiterAgent w = host.addWaiter();
//    	WaiterGui waiterGui = new WaiterGui(w, gui);
//    	w.setGui(waiterGui);
//    	gui.animationPanel.addGui(waiterGui);
//    	w.startThread();
//    	if (onBreak) w.askForBreak();
//        return w.getName();
//    }
    
//    public int howManyWaiters(){
//    	return host.howManyWaiters();
//    }
	
}
