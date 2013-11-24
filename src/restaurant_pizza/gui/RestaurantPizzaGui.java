package restaurant_pizza.gui;

import restaurant_pizza.CustomerAgent;
import restaurant_pizza.WaiterAgent;
import restaurant_pizza.interfaces.Customer;
import restaurant_pizza.interfaces.Waiter;

import javax.swing.*;

import agent_pizza.Agent;

import java.awt.*;
import java.awt.event.*;
/**
 * Main GUI class.
 * Contains the main frame and subsequent panels
 */
public class RestaurantPizzaGui extends JFrame implements ActionListener {
	
	PizzaAnimationPanel animationPanel = new PizzaAnimationPanel();
	JPanel nonAnimationPanel = new JPanel();
	
	/*
	 * The RestaurantPanel holds two panels:
	 * 1) AnimationPanel where visual representations of the agents move around and interact.
	 * 2) JPanel where the GUI controls and other elements reside.
	 */
    //private RestaurantPizza restPanel = new RestaurantPizza(this);
    
    /* infoPanel holds information about the clicked customer, if there is one*/
    private JPanel infoPanel;
    private JLabel infoLabel; //part of infoPanel
    private JCheckBox stateCB;//part of infoLabel
    
    private static final int WINDOWX = 1300;
    private static final int WINDOWY = 700;

    private Object currentPerson;

    /**
     * Constructor for RestaurantGui class.
     * Sets up all the gui components.
     */
    public RestaurantPizzaGui() {
        
    	setBounds(50, 50, WINDOWX, WINDOWY);
        setLayout(new GridLayout(1, 0));
    	
        Dimension animationDim = new Dimension((int)(WINDOWX * .2), (int) (WINDOWY * .75));
        animationPanel.setPreferredSize(animationDim);
        animationPanel.setMinimumSize(animationDim);
        animationPanel.setMaximumSize(animationDim);
        add(animationPanel);
        
        nonAnimationPanel.setLayout(new GridLayout(0, 1));
        add(nonAnimationPanel);
        
      //  nonAnimationPanel.add(restPanel);
        
        Dimension infoDim = new Dimension((int)(WINDOWX * .5), (int) (WINDOWY * .25));
        infoPanel = new JPanel();
        infoPanel.setPreferredSize(infoDim);
        infoPanel.setMinimumSize(infoDim);
        infoPanel.setMaximumSize(infoDim);
        infoPanel.setBorder(BorderFactory.createTitledBorder("Information"));
        infoPanel.setLayout(new GridLayout(1, 3, 10, 0));
        
        stateCB = new JCheckBox();
        stateCB.setVisible(false);
        stateCB.addActionListener(this);
        
        infoLabel = new JLabel(); 
        infoLabel.setText("<html><pre><i>Click Add to make customers</i></pre></html>");
        infoPanel.add(infoLabel);
        infoPanel.add(stateCB);
        nonAnimationPanel.add(infoPanel);
        
        JLabel aLabel = new JLabel();
        aLabel.setText("Douglass Chen, Fall 2013 CSCI 201 Restaurant GUI");
        nonAnimationPanel.add(aLabel);
    }
    /**
     * updateInfoPanel() takes the given customer (or, for v3, Host) object and
     * changes the information panel to hold that person's info.
     *
     * @param person customer (or waiter) object
     */
    public void updateInfoPanel(Object person) {
        currentPerson = person;

        if (person instanceof CustomerAgent) {
            stateCB.setVisible(true);
            CustomerAgent customer = (CustomerAgent)person;
            stateCB.setText("Hungry?");
            stateCB.setSelected(customer.getGui().isHungry());
            stateCB.setEnabled(!customer.getGui().isHungry());
            infoLabel.setText(
               "<html><pre>Name: " + customer.getName() + " </pre></html>");
        }
        if (person instanceof WaiterAgent) {
        	stateCB.setVisible(true);
        	WaiterAgent waiter = (WaiterAgent) person;
            stateCB.setText("Want break?");
            stateCB.setSelected(waiter.getGui().isOnBreak());
            stateCB.setEnabled(true);

            infoLabel.setText(
               "<html><pre>Name: " + waiter.getName() + " </pre></html>");
        }
        infoPanel.validate();
    }
    /**
     * Action listener method that reacts to the checkbox being clicked;
     * If it's the customer's checkbox, it will make him hungry
     * For v3, it will propose a break for the waiter.
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == stateCB) {
            if (currentPerson instanceof CustomerAgent) {
                CustomerAgent c = (CustomerAgent) currentPerson;
                c.getGui().setHungry(); // separation of concerns??
                stateCB.setEnabled(false);
            }
            else if (currentPerson instanceof WaiterAgent) {
                WaiterAgent w = (WaiterAgent) currentPerson;
                boolean checked = stateCB.isSelected();
                if(checked) {
                	w.getGui().setGoOnBreak();
                }
                else {
                	System.out.println("Executing backtowork");
                	w.getGui().setBackToWork();
                }
            }
        }
    }
    
    /**
     * Message sent from a customer gui to enable that customer's
     * "I'm hungry" checkbox.
     *
     * @param c reference to the customer
     */
    public void setEnabled(Agent c) {
        if (currentPerson instanceof CustomerAgent) {
            Customer cust = (Customer) currentPerson;
            if (c.equals(cust)) {
                stateCB.setEnabled(true);
                stateCB.setSelected(false);
            }
        }
    }
    
    /**
     * Main routine to get gui started
     */
    public static void main(String[] args) {
        RestaurantPizzaGui gui = new RestaurantPizzaGui();
        gui.setTitle("CSCI201 Restaurant - Douglass Chen");
        gui.setVisible(true);
        gui.setResizable(false);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
