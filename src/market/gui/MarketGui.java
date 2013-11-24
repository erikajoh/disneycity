package market.gui;

import market.CustomerAgent;
import market.WorkerAgent;

import javax.swing.*;

import agent.Agent;

import java.awt.*;
import java.awt.event.*;
/**
 * Main GUI class.
 * Contains the main frame and subsequent panels
 */
public class MarketGui extends JFrame implements ActionListener {
    /* The GUI has two frames, the control frame (in variable gui) 
     * and the animation frame, (in variable animationFrame within gui)
     */
//	JFrame animationFrame = new JFrame("Restaurant Animation");
	MarketAnimationPanel animationPanel = new MarketAnimationPanel();
	
    /* restPanel holds 2 panels
     * 1) the staff listing, menu, and lists of current customers all constructed
     *    in RestaurantPanel()
     * 2) the infoPanel about the clicked Customer (created just below)
     */        
    
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
    public MarketGui() {
        int WINDOWX = 400;
        int WINDOWY = 360;
    	
    	setBounds(WINDOWX/10, WINDOWY/12, WINDOWX, WINDOWY);
        setLayout(new GridLayout(2,1));
        add(animationPanel);
    }
    /**
     * updateInfoPanel() takes the given customer object and
     * changes the information panel to hold that person's info.
     *
     * @param person customer (or worker) object
     */
    public void updateInfoPanel(Object person) {
    	currentPerson = person;
        if (person instanceof CustomerAgent) {
            CustomerAgent c = (CustomerAgent) person;
        }
        if (person instanceof WorkerAgent) {
            WorkerAgent w = (WorkerAgent) person;
            if (!w.isOnBreak()) w.askForBreak();
            else w.finishBreak();
        }
    }

    /**
     * Main routine to get gui started
     */
    public static void main(String[] args) {
        MarketGui gui = new MarketGui();
        gui.setTitle("le market");
        gui.setVisible(true);
        gui.setResizable(false);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
   
}
