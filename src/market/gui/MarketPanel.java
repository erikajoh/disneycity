package market.gui;

import market.CustomerAgent;
import market.ManagerAgent;
import market.WorkerAgent;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class MarketPanel extends JPanel {

    //Host, cook, waiters and customers
    private ManagerAgent manager = new ManagerAgent("le manager");
    private CashierGui cashierGui = new CashierGui(manager);

    private JPanel restLabel = new JPanel();
    private ListPanel customerPanel = new ListPanel(this, "customers");
    private ListPanel workerPanel = new ListPanel(this, "workers");
    private ListPanel marketPanel = new ListPanel(this, "markets");
    private JPanel group = new JPanel();
    private JLabel restSubLabel = new JLabel();

    private MarketGui gui; //reference to main gui

    public MarketPanel(MarketGui gui) {
        this.gui = gui;
        manager.setGui(cashierGui);
        for (WorkerAgent w : manager.workers) {
        	WorkerGui workerGui = new WorkerGui(w, gui);
        	w.setGui(workerGui);
        	gui.animationPanel.addGui(workerGui);
        }

        gui.animationPanel.addGui(cashierGui);
        manager.startThread();
        for (WorkerAgent w : manager.workers) {
        	w.startThread();
        }

        setLayout(new GridLayout(1, 2, 20, 20));
        group.setLayout(new GridLayout(1, 2, 10, 10));

        initRestLabel();
        add(restLabel);
        
        add(customerPanel);
        add(workerPanel);
        
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
                "<html><h3>hello world!</h3>welcome to erika's market.<h3>host</h3><table><tr><td>" + manager.getName() + "</table>" + menuText + "</html"); 
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
    public void showInfo(String type, String name) {

        if (type.equals("customers")) {

            for (int i = 0; i < manager.customers.size(); i++) {
                CustomerAgent temp = manager.customers.get(i);
                if (temp.getName() == name)
                    gui.updateInfoPanel(temp);
            }
        }
        
        if (type.equals("workers")) {

            for (int i = 0; i < manager.workers.size(); i++) {
                WorkerAgent temp = manager.workers.get(i);
                if (temp.getName() == name)
                    gui.updateInfoPanel(temp);
            }
        }

    }

    /**
     * Adds a customer or waiter to the appropriate list
     *
     * @param type indicates whether the person is a customer or waiter (later)
     * @param name name of person
     */
    public void addPerson(String type, String name, Boolean isHungry) {

    	if (type.equals("customers")) {
    		CustomerAgent c = new CustomerAgent(name, 100); // hack to make customer start with $100
    		CustomerGui g = new CustomerGui(c, gui);

    		gui.animationPanel.addGui(g);// dw
    		c.setManager(manager);
    		c.setGui(g);
    		manager.customers.add(c);
    		c.startThread();
    	}
    	
    }
    
    public void addButton(JButton b) {
		restLabel.add(b, BorderLayout.SOUTH);
	}
    
    public void resetState(Object person){
    	if (person instanceof CustomerAgent) customerPanel.resetCB(person);
    	if (person instanceof WorkerAgent) workerPanel.resetCB(person);
    }
    
    public void addTable(){
    	gui.animationPanel.addTable();
    }
    
    public String addWorker(Boolean onBreak){
    	WorkerAgent w = manager.addWorker();
    	WorkerGui waiterGui = new WorkerGui(w, gui);
    	w.setGui(waiterGui);
    	gui.animationPanel.addGui(waiterGui);
    	w.startThread();
    	if (onBreak) w.askForBreak();
        return w.getName();
    }
    
    public int howManyWorkers(){
    	return manager.howManyWorkers();
    }
	
}
