package restaurant_rancho.gui;

import restaurant_rancho.CustomerAgent;
import restaurant_rancho.WaiterAgent;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
/**
 * Main GUI class.
 * Contains the main frame and subsequent panels
 */


public class RestaurantRanchoGui extends JFrame implements ActionListener {
	
	public static final int WINDOWX = 1000;
	public static final int WINDOWY = 700;
   
	String name;
	AnimationPanel animationPanel = new AnimationPanel();
	private RestaurantRancho restPanel;
    private JPanel leftPanel = new JPanel(); 
    private JPanel rightPanel = new JPanel();
    private JPanel infoPanel;
    private JLabel infoLabel; //part of infoPanel
    private JPanel infoWaiterPanel;
    private JLabel infoWLabel;
    private JButton wBreak;
    private JLabel labelPic;
    private JCheckBox stateCB;//part of infoLabel
    private JButton pause;
    private boolean paused = false;
    private Object currentPerson;/* Holds the agent that the info is about.
    								Seems like a hack */

    /**
     * Constructor for RestaurantGui class.
     * Sets up all the gui components.
     */
    public RestaurantRanchoGui(String name) {
        
    	//restPanel= new RestaurantRancho(this, name);
        this.name = name;
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.add(animationPanel); 
        
 
        pause = new JButton("Pause");
        animationPanel.add(pause);
    	
    	setBounds(WINDOWX/20, WINDOWX/20, WINDOWX, WINDOWY);
    	leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
    	
    	setLayout(new BoxLayout((Container) getContentPane(), BoxLayout.X_AXIS));
        //setLayout(new GridLayout(3, 3));
    	Dimension leftPanDim = new Dimension(WINDOWX/2, WINDOWY);
    	leftPanel.setPreferredSize(leftPanDim);
        leftPanel.setMinimumSize(leftPanDim);
        leftPanel.setMaximumSize(leftPanDim);
        animationPanel.setPreferredSize(leftPanDim);
        animationPanel.setMinimumSize(leftPanDim);
        animationPanel.setMaximumSize(leftPanDim);

        leftPanel.add(restPanel);
   
        infoPanel = new JPanel();
       /* Dimension infoPanDim = new Dimension(WINDOWX/4, (int) WINDOWY/6);
        infoPanel.setPreferredSize(infoPanDim);
        infoPanel.setMinimumSize(infoPanDim);
        infoPanel.setMaximumSize(infoPanDim);
        */
        infoPanel.setBorder(BorderFactory.createTitledBorder("Customer Information"));
        
        stateCB = new JCheckBox();
        stateCB.setVisible(false);
        stateCB.addActionListener(this);
        
        infoWaiterPanel = new JPanel();
        infoWaiterPanel.setBorder(BorderFactory.createTitledBorder("Waiter Information"));
        infoWaiterPanel.setLayout(new GridLayout(1, 2, 30, 0));
     
        wBreak = new JButton("Go On Break");
        wBreak.addActionListener(this);
        
        infoWLabel = new JLabel(); 
        infoWLabel.setText("<html><pre><i>Click Add to make waiters</i></pre></html>");
        infoWaiterPanel.add(infoWLabel);
        infoWaiterPanel.add(wBreak);
        
        pause.addActionListener(this);
        
        infoPanel.setLayout(new GridLayout(1, 2, 30, 0));
        
        infoLabel = new JLabel(); 
        infoLabel.setText("<html><pre><i>Click Add to make customers</i></pre></html>");
        infoPanel.add(infoLabel);
        infoPanel.add(stateCB);
        
        leftPanel.add(infoPanel);
        leftPanel.add(infoWaiterPanel);
        add(leftPanel);
        add(rightPanel);
        
    }
    /**
     * updateInfoPanel() takes the given customer (or, for v3, Host) object and
     * changes the information panel to hold that person's info.
     *
     * @param person customer (or waiter) object
     */
    
    public RestaurantRancho getRestaurant() {
    	return restPanel;
    }
    
    public void updateInfoPanel(Object person) {
        stateCB.setVisible(true);
        currentPerson = person;

        if (person instanceof CustomerAgent) {
            CustomerAgent customer = (CustomerAgent) person;
            stateCB.setText("Hungry?");
          //Should checkmark be there? 
            stateCB.setSelected(customer.getGui().isHungry());
          //Is customer hungry? Hack. Should ask customerGui
            stateCB.setEnabled(!customer.getGui().isHungry());
          // Hack. Should ask customerGui
            infoLabel.setText(
               "<html><pre>     Name: " + customer.getName() + " </pre></html>");
        }
        infoPanel.validate();
    }
    
    public void updateWInfoPanel(Object person) {
    	currentPerson = person;
    	
    	if (person instanceof WaiterAgent) {
    		WaiterAgent waiter = (WaiterAgent) person;
    		if (waiter.isOnBreak == true) {
    			wBreak.setText("Go Off Break");
    		}
    		else wBreak.setText("Go On Break");
    		infoWLabel.setText(
    			"<html><pre>     Name: " + waiter.getName() + " </pre></html>");
    	}
    	
    	infoWaiterPanel.validate();
    }
    
   /* public update waiterPanel(Object person) {
    	
    }*/
    /**
     * Action listener method that reacts to the checkbox being clicked;
     * If it's the customer's checkbox, it will make him hungry
     * For v3, it will propose a break for the waiter.
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == stateCB) {
            if (currentPerson instanceof CustomerAgent) {
                CustomerAgent c = (CustomerAgent) currentPerson;
                c.getGui().setHungry();
                stateCB.setEnabled(false);
            }
        }
        
        else if (e.getSource() == pause) {
        	if (paused == false) {
        		paused = true;
        		pause.setText("Restart");
        		restPanel.PauseandUnpauseAgents();
        	}
        	else if (paused == true) {
        		paused = false;
        		pause.setText("Pause");
        		restPanel.PauseandUnpauseAgents();
        	}
        }
        
        else if (e.getSource() == wBreak) {
        	if (currentPerson instanceof WaiterAgent) {
        		WaiterAgent w = (WaiterAgent) currentPerson;
        		if (wBreak.getText() == "Go On Break") {
        			restPanel.waiterWantsBreak(w);
        		}
        		else {
        			restPanel.waiterWantsOffBreak(w);
        			wBreak.setText("Go On Break");
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
    public void setCustomerEnabled(CustomerAgent c) {
        if (currentPerson instanceof CustomerAgent) {
            CustomerAgent cust = (CustomerAgent) currentPerson;
            if (c.equals(cust)) {
                stateCB.setEnabled(true);
                stateCB.setSelected(false);
            }
        }
    }
    /**
     * Main routine to get gui started
     */
 //  public static void main(String[] args) {
      public void run() {
        /*RestaurantGui gui = new RestaurantGui("name");
        gui.setTitle("csci201 Restaurant");
        gui.setVisible(true);
        gui.setResizable(false);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        */
    	setTitle(name);
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
    }
}
