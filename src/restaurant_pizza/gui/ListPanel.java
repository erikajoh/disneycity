package restaurant_pizza.gui;

import javax.swing.*;

import agent_pizza.Agent;

import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

/**
 * Subpanel of restaurantPanel.
 * This holds the scroll panes for the customers and, later, for waiters
 */
public class ListPanel extends JPanel implements ActionListener {
	
    public JScrollPane scrollPane =
            new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private JPanel view = new JPanel();
    private List<JButton> list = new ArrayList<JButton>();
    
    private JTextField addPersonText = new JTextField();
    private JButton addCustomerButton = new JButton("Add customer");
    private JCheckBox hungryCheckbox = new JCheckBox("Hungry?"); 
    private JButton pauseButton = new JButton("Pause");
    private boolean isPaused = false;
    
    private JButton addWaiterButton = new JButton("Add waiter");
    
    private static final int NUM_BUTTONS_ROWS = 5;
    private static final int NUM_BUTTONS_COLS = 2;
    private static final int BUTTON_WIDTH_ADJUST = 42;
    private static final int MAX_TEXTFIELD_HEIGHT = 100;
    private static final int BUTTON_GAP_SIZE = 2;

    private RestaurantPizza restPanel;
    private String type;
    private int customerCounter = 0;
    private int waiterCounter = 0;

    /**
     * Constructor for ListPanel.  Sets up all the gui
     *
     * @param rp   reference to the restaurant panel
     * @param type indicates if this is for customers or waiters
     */
    public ListPanel(RestaurantPizza rp, String type) {
        restPanel = rp;
        this.type = type;

        setLayout(new BoxLayout((Container) this, BoxLayout.Y_AXIS));
        add(new JLabel("<html><pre> <u>" + type + "</u><br></pre></html>"));

        addPersonText.setMaximumSize(new Dimension(super.getMaximumSize().width, MAX_TEXTFIELD_HEIGHT));
        add(addPersonText);
        if(type.equals("Customer"))
        {
        	add(hungryCheckbox);
        	addCustomerButton.addActionListener(this);
            add(addCustomerButton);
        }
        if(type.equals("Waiter"))
        {
            pauseButton.addActionListener(this);
            add(pauseButton);
        	addWaiterButton.addActionListener(this);
            add(addWaiterButton);
        }

        view.setLayout(new GridLayout(0, NUM_BUTTONS_COLS, BUTTON_GAP_SIZE, BUTTON_GAP_SIZE));
        scrollPane.setViewportView(view);
        add(scrollPane);
    }

    /**
     * Method from the ActionListener interface.
     * Handles the event of the add button being pressed
     */
  public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addCustomerButton) {
        	addPerson(customerCounter + "-" + addPersonText.getText(), hungryCheckbox.isSelected());
        	customerCounter++;
        }
  }
        /*
        if (e.getSource() == addWaiterButton) {
        	addPerson(waiterCounter + "-" + addPersonText.getText(), hungryCheckbox.isSelected());
        	waiterCounter++;
        }
        else if(e.getSource() == pauseButton)
        {
        	if(!isPaused) {
        		pauseButton.setText("Unpause");
        		LinkedList<Agent> allAgents = restPanel.getAllAgents();
        		for(Agent anAgent : allAgents)
        		{
        			try {
						anAgent.pauseAction();
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
        		}
        	}
        	else {
        		pauseButton.setText("Pause");
        		LinkedList<Agent> allAgents = restPanel.getAllAgents();
        		for(Agent anAgent : allAgents)
        		{
        			anAgent.resumeAction();
        		}
        	}
        	isPaused = !isPaused;
        }
        else {
        	for (JButton temp:list){
                if (e.getSource() == temp)
                    restPanel.showInfo(type, temp.getText());
            }
        }
    }
    */

    /**
     * If the add button is pressed, this function creates
     * a spot for it in the scroll pane, and tells the restaurant panel
     * to add a new person.
     *
     * @param name name of new person
     */
    public void addPerson(String name, boolean isHungry) {
        if (name != null) {
            JButton button = new JButton(name);
            button.setBackground(Color.white);

            Dimension paneSize = scrollPane.getSize();
            Dimension buttonSize = new Dimension((paneSize.width / NUM_BUTTONS_COLS) - BUTTON_WIDTH_ADJUST,
                    paneSize.height / NUM_BUTTONS_ROWS);
            button.setPreferredSize(buttonSize);
            button.setMinimumSize(buttonSize);
            button.setMaximumSize(buttonSize);
            button.addActionListener(this);
            list.add(button);
            view.add(button);
            if(isHungry)
            	restPanel.addPerson(null, "Hungry" + type, name, 50);//puts hungry customer on list
            else
            	restPanel.addPerson(null, type, name, 50);//puts customer on list
    //        restPanel.showInfo(type, name);//puts hungry button on panel
            validate();
        }
    }
}
