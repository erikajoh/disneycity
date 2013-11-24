package restaurant_bayou.gui;

import restaurant_bayou.CustomerAgent;
import restaurant_bayou.MarketAgent;
import restaurant_bayou.WaiterAgent;

import javax.swing.*;

import agent_bayou.Agent;

import java.awt.*;
import java.awt.event.*;
/**
 * Main GUI class.
 * Contains the main frame and subsequent panels
 */
public class RestaurantBayouGui extends JFrame implements ActionListener {
    /* The GUI has two frames, the control frame (in variable gui) 
     * and the animation frame, (in variable animationFrame within gui)
     */
//	JFrame animationFrame = new JFrame("Restaurant Animation");
	BayouAnimationPanel animationPanel = new BayouAnimationPanel(350,450);
	
    /* restPanel holds 2 panels
     * 1) the staff listing, menu, and lists of current customers all constructed
     *    in RestaurantPanel()
     * 2) the infoPanel about the clicked Customer (created just below)
     */    
    private RestaurantBayou restPanel = new RestaurantBayou(this);
    
    private int lineLength = 0;
    
    /* infoPanel holds information about the clicked customer, if there is one*/
//    private JPanel infoPanel;
//    private JPanel aboutPanel;
//    private JLabel infoLabel; //part of infoPanel
//    private JLabel aboutLabel;
    private JCheckBox stateCB;//part of infoLabel
    private JButton pauseB = new JButton("pause");

    private Object currentPerson;/* Holds the agent that the info is about.
    								Seems like a hack */

    /**
     * Constructor for RestaurantGui class.
     * Sets up all the gui components.
     */
    public RestaurantBayouGui() {
        int WINDOWX = 725; //450
        int WINDOWY = 725; //725

//        animationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        animationFrame.setBounds(WINDOWX/5+WINDOWX, WINDOWY/8 , WINDOWX+WINDOWX/5, WINDOWY+WINDOWY/4);
//        animationFrame.setVisible(true);
//    	animationFrame.add(animationPanel); 
    	
    	setBounds(WINDOWX/10, WINDOWY/12, WINDOWX, WINDOWY);

        setLayout(new GridLayout(2,1));

        Dimension restDim = new Dimension(WINDOWX, (int) (WINDOWY * .6));
        restPanel.setPreferredSize(restDim);
        restPanel.setMinimumSize(restDim);
        restPanel.setMaximumSize(restDim);
        add(restPanel);
        
        // Now, setup the info panel
//        Dimension infoDim = new Dimension(WINDOWX, (int) (WINDOWY * .15));
//        infoPanel = new JPanel();
//        infoPanel.setPreferredSize(infoDim);
//        infoPanel.setMinimumSize(infoDim);
//        infoPanel.setMaximumSize(infoDim);
//        infoPanel.setBorder(BorderFactory.createTitledBorder("Information"));

        stateCB = new JCheckBox();
        stateCB.setVisible(false);
        stateCB.addActionListener(this);
        
        pauseB.addActionListener(this);
        restPanel.addButton(pauseB);

//        infoPanel.setLayout(new GridLayout(1, 2, 30, 0));
//        
//        infoLabel = new JLabel(); 
//        infoLabel.setText("<html><pre><i>Click Add to make customers</i></pre></html>");
//        infoPanel.add(infoLabel);
//        infoPanel.add(stateCB);
//        add(infoPanel);
        
        add(animationPanel);
                               
     // Now, setup the about panel
//        Dimension aboutDim = new Dimension(WINDOWX, (int) (WINDOWY * .15));
//        aboutPanel = new JPanel();
//        aboutPanel.setPreferredSize(aboutDim);
//        aboutPanel.setMinimumSize(aboutDim);
//        aboutPanel.setMaximumSize(aboutDim);
//        aboutPanel.setBorder(BorderFactory.createTitledBorder("About"));
//
//        aboutPanel.setLayout(new GridLayout(2, 1, 30, 0));
//        
//        aboutLabel = new JLabel(); 
//        aboutLabel.setText("<html><pre>Hello, my name is Erika.</pre></html>");
//        aboutPanel.add(aboutLabel);
//        aboutPanel.add(new JLabel(new ImageIcon("image.jpg")));
//        aboutPanel.add(pauseB);
//        add(aboutPanel);
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
            CustomerAgent c = (CustomerAgent) person;
            c.getGui().setHungry();
        }
        if (person instanceof WaiterAgent) {
            WaiterAgent w = (WaiterAgent) person;
            if (!w.isOnBreak()) w.askForBreak();
            else w.finishBreak();
        }
        if (person instanceof MarketAgent) {
            MarketAgent m = (MarketAgent) person;
            m.emptyInventory();
        }
//        stateCB.setVisible(true);
//        currentPerson = person;
//
//        if (person instanceof CustomerAgent) {
//            CustomerAgent customer = (CustomerAgent) person;
//            stateCB.setText("Hungry?");
//          //Should checkmark be there? 
//            stateCB.setSelected(customer.getGui().isHungry());
//          //Is customer hungry? Hack. Should ask customerGui
//            stateCB.setEnabled(!customer.getGui().isHungry());
//          // Hack. Should ask customerGui
//            infoLabel.setText(
//               "<html><pre>     Name: " + customer.getName() + " </pre></html>");
//        }
//        infoPanel.validate();
    }
    /**
     * Action listener method that reacts to the checkbox being clicked;
     * If it's the customer's checkbox, it will make him hungry
     * For v3, it will propose a break for the waiter.
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == pauseB) {
        	if (pauseB.getText() == "pause"){
//        		if (currentPerson instanceof CustomerAgent) {
//        			((Agent) currentPerson).stopThread();
//        		}
        		animationPanel.pauseOrResume();
        		pauseB.setText("resume");
        	}
        	else {
        		if (currentPerson instanceof CustomerAgent) {
        			((Agent) currentPerson).startThread();
        		}
        		animationPanel.pauseOrResume();
        		pauseB.setText("pause");
        	}
        }
    }
    /**
     * Message sent from a customer gui to enable that customer's
     * "I'm hungry" checkbox.
     *
     * @param c reference to the customer
     */
    public void setEnabled(Object person) {
    	restPanel.resetState(person);
//        if (currentPerson instanceof CustomerAgent) {
//            CustomerAgent cust = (CustomerAgent) currentPerson;
//            if (c.equals(cust)) {
//                stateCB.setEnabled(true);
//                stateCB.setSelected(false);
//            }
//        }
    }
    
    /**
     * Main routine to get gui started
     */
    public static void main(String[] args) {
        RestaurantBayouGui gui = new RestaurantBayouGui();
        gui.setTitle("le restaurant");
        gui.setVisible(true);
        gui.setResizable(false);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    public int howManyCustomers() {
    	return lineLength;
    }
    
    public void increaseLine() {
    	lineLength++;
    }
    
    public void decreaseLine() {
    	lineLength--;
    }
    public int howManyWaiters() {
    	return restPanel.howManyWaiters();
    }
}
