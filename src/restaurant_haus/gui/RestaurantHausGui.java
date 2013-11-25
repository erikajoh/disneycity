package restaurant_haus.gui;

import restaurant_haus.CustomerAgent;
import restaurant_haus.WaiterAgent;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.File;

/**
 * Main GUI class.
 * Contains the main frame and subsequent panels
 */
public class RestaurantHausGui extends JFrame implements ActionListener {
    /* The GUI has two frames, the control frame (in variable gui) 
     * and the animation frame, (in variable animationFrame within gui)
     */
	//JFrame animationFrame = new JFrame("Restaurant Animation");
	HausAnimationPanel animationPanel = new HausAnimationPanel();
	
    /* restPanel holds 2 panels
     * 1) the staff listing, menu, and lists of current customers all constructed
     *    in RestaurantPanel()
     * 2) the infoPanel about the clicked Customer (created just below)
     */    
   // private RestaurantHaus restPanel = new RestaurantHaus(this);
    
    /* infoPanel holds information about the clicked customer, if there is one*/
    private JPanel infoPanel;
    private JLabel infoLabel; //part of infoPanel
    private JCheckBox stateCB;//part of infoLabel
    
    private JPanel controlPanel = new JPanel();
    private BoxLayout controlLayout = new BoxLayout(controlPanel, BoxLayout.Y_AXIS);
    

    private Object currentPerson;/* Holds the agent that the info is about.
    								Seems like a hack */

    /**
     * Constructor for RestaurantGui class.
     * Sets up all the gui components.
     */
    public RestaurantHausGui() {
        final int WINDOWX = 272;
        final int WINDOWY = 350;
        
        final int INITXPOS = 50;
        final int INITYPOS = 50;
        
        //final int ANIMFRAMEHEIGHT = 350;
        final int ANIMEFRAMEWIDTH = 450;
        
        final double RESTDIMPERCENTAGE = 0.7;

        final int BORDERSIZE = 10;
        
        //animationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //animationFrame.setBounds(INITXPOS+WINDOWX, INITYPOS , ANIMEFRAMEWIDTH, WINDOWY);
        //animationFrame.setVisible(true);
    	//animationFrame.add(animationPanel); 
    	
    	setBounds(INITXPOS, INITYPOS, WINDOWX + ANIMEFRAMEWIDTH, WINDOWY);
    	
    	controlPanel.setLayout(controlLayout);
        setLayout(new BoxLayout((Container) getContentPane(), 
        		BoxLayout.X_AXIS));

        Dimension restDim = new Dimension(WINDOWX, (int) (WINDOWY * RESTDIMPERCENTAGE));
       // restPanel.setPreferredSize(restDim);
       // restPanel.setMinimumSize(restDim);
       // restPanel.setMaximumSize(restDim);
        //controlPanel.add(restPanel);
        
        // Now, setup the info panel
        Dimension infoDim = new Dimension(WINDOWX, (int) (WINDOWY * (1 - RESTDIMPERCENTAGE))- (3*BORDERSIZE));
        infoPanel = new JPanel();
        infoPanel.setPreferredSize(infoDim);
        infoPanel.setMinimumSize(infoDim);
        infoPanel.setMaximumSize(infoDim);
        infoPanel.setBorder(BorderFactory.createTitledBorder("Information"));

        stateCB = new JCheckBox();
        stateCB.setVisible(false);
        stateCB.addActionListener(this);

        infoPanel.setLayout(new GridLayout(2, 1, BORDERSIZE, BORDERSIZE));
        
        infoLabel = new JLabel(); 
        infoLabel.setText("<html><pre><i>Click Add to make customers or waiters</i></pre></html>");
        infoPanel.add(infoLabel);
        infoPanel.add(stateCB);
        controlPanel.add(infoPanel);
        
        add(controlPanel);
        add(animationPanel);
    }
    /**
     * updateInfoPanel() takes the given customer (or, for v3, Host) object and
     * changes the information panel to hold that person's info.
     *
     * @param person customer (or waiter) object
     */
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
        
        if (person instanceof WaiterAgent) {
            WaiterAgent waiter = (WaiterAgent) person;
            stateCB.setText("Break?");
          //Should checkmark be there? 
            stateCB.setSelected(waiter.wantsBreak());
          //Is customer hungry? Hack. Should ask customerGui
            stateCB.setEnabled(!waiter.wantsBreak());
          // Hack. Should ask customerGui
            infoLabel.setText(
               "<html><pre>     Name: " + waiter.getName() + " </pre></html>");
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
                if(!c.getGui().isHungry()) {
                	c.getGui().setHungry();
                }
                stateCB.setEnabled(false);
            }
            
            if (currentPerson instanceof WaiterAgent) {
                WaiterAgent waiter = (WaiterAgent) currentPerson;
                if(!waiter.wantsBreak()) {
                	waiter.msgWantBreak();
                }
                	stateCB.setEnabled(false);
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
    
    public void setWaiterEnabled(WaiterAgent waiter) {
    	if (currentPerson instanceof WaiterAgent) {
    		WaiterAgent wait = (WaiterAgent) currentPerson;
    		if(waiter.equals(wait)) {
    			stateCB.setEnabled(true);
                stateCB.setSelected(false);
    		}
    	}
    }
    
    /**
     * Main routine to get gui started
     */
    public static void main(String[] args) {
        RestaurantHausGui gui = new RestaurantHausGui();
        gui.setTitle("csci201 Restaurant");
        gui.setVisible(true);
        gui.setResizable(false);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
