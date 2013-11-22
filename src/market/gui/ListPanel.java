package market.gui;

import market.CustomerAgent;
import market.ManagerAgent;
import market.WorkerAgent;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Subpanel of marketPanel.
 * This holds the scroll panes for the customers and, later, for waiters
 */

public class ListPanel extends JPanel implements ActionListener {

    public JScrollPane pane =
            new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private JPanel view = new JPanel();
    private JPanel inputLayout = new JPanel();
    private List<JCheckBox> list = new ArrayList<JCheckBox>();
    private JButton addPersonB = new JButton("add a customer");
    private JButton addWaiterB = new JButton("add a waiter");
    private JCheckBox inputCB = new JCheckBox("");
    private JTextField inputForm = new JTextField("", 4);

    private MarketPanel mktPanel;
    private String type;

    /**
     * Constructor for ListPanel.  Sets up all the gui
     *
     * @param rp   reference to the restaurant panel
     * @param type indicates if this is for customers or waiters
     */
    public ListPanel(MarketPanel mp, String type) {
        mktPanel = mp;
        this.type = type;

        setLayout(new BoxLayout((Container) this, BoxLayout.Y_AXIS));
        add(new JLabel("<html><pre><u>" + type + "</u><br></pre></html>"));

        inputLayout.setLayout(new FlowLayout());
        if (type.equals("customers")) inputLayout.add(inputForm);
        inputLayout.add(inputCB);
        add(inputLayout);
        
        if (type.equals("customers")){
        	addPersonB.addActionListener(this);
        	inputLayout.add(addPersonB);
        	inputCB.setText("hungry?");
        }
        
        if (type.equals("waiters")){
        	addWaiterB.addActionListener(this);
        	inputLayout.add(addWaiterB);
        	inputCB.setText("on break?");
        }

        view.setLayout(new BoxLayout((Container) view, BoxLayout.Y_AXIS));
        pane.setPreferredSize(new Dimension(100, 100));
        pane.setViewportView(view);
        add(pane);
    }

    /**
     * Method from the ActionListener interface.
     * Handles the event of the add button being pressed
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addPersonB) {
        	// Chapter 2.19 describes showInputDialog()
//            addPerson(JOptionPane.showInputDialog("Please enter a name:"));
        	addPerson(inputForm.getText(), inputCB.isSelected());
        	inputCB.setSelected(false);
        	inputForm.setText("");
        } else if (e.getSource() == addWaiterB) {
        	addWaiter(inputCB.isSelected());
        	inputCB.setSelected(false);
        }
        else {
        	// Isn't the second for loop more beautiful?
            /*for (int i = 0; i < list.size(); i++) {
                JButton temp = list.get(i);*/
        	for (JCheckBox temp:list){
                if (e.getSource() == temp) {
//                	if (temp.isSelected()) {
                	mktPanel.showInfo(type, temp.getText());
                	if (!type.equals("waiters")) temp.setEnabled(false);
//                	}
                }
            }
        }
    }

    /**
     * If the add button is pressed, this function creates
     * a spot for it in the scroll pane, and tells the restaurant panel
     * to add a new person.
     *
     * @param name name of new person
     */
    public void addPerson(String name, Boolean isHungry) {
        if (name != null) {

            Dimension paneSize = pane.getSize();
            
            JLabel hungryLabel = new JLabel("hungry?");

            JCheckBox stateCB = new JCheckBox();
            stateCB.setText(name);
            stateCB.setHorizontalTextPosition(SwingConstants.LEFT);
            stateCB.setVisible(true);
            stateCB.setSelected(isHungry);
            stateCB.setEnabled(!isHungry);
            stateCB.addActionListener(this);
            list.add(stateCB);
            
            JPanel personItem = new JPanel();
            personItem.setLayout(new FlowLayout());
            personItem.add(stateCB);
            personItem.add(hungryLabel);
            
            Dimension personItemSize = new Dimension((int) paneSize.width,
                    (int) (paneSize.height / 8));
            personItem.setPreferredSize(personItemSize);
            personItem.setMinimumSize(personItemSize);
            personItem.setMaximumSize(personItemSize);
            
            view.add(personItem);
            mktPanel.addPerson(type, name, isHungry);//puts customer on list
            //restPanel.showInfo(type, name);//puts hungry button on panel
            validate();
        }
    }
    
    public void addWaiter(Boolean onBreak){
//          JLabel label = new JLabel("Hungry?");

    	  String name = mktPanel.addWorker(onBreak);
          Dimension paneSize = pane.getSize();
          
          JLabel onBreakLabel = new JLabel("on break?");

          JCheckBox stateCB = new JCheckBox();
          stateCB.setText(name);
          stateCB.setHorizontalTextPosition(SwingConstants.LEFT);
          stateCB.setVisible(true);
          stateCB.setSelected(onBreak);
          stateCB.setEnabled(true);
          stateCB.addActionListener(this);
          list.add(stateCB);
          
          JPanel personItem = new JPanel();
          personItem.setLayout(new FlowLayout());
          personItem.add(stateCB);
          personItem.add(onBreakLabel);
          
          Dimension personItemSize = new Dimension((int) paneSize.width,
                  (int) (paneSize.height / 8));
          personItem.setPreferredSize(personItemSize);
          personItem.setMinimumSize(personItemSize);
          personItem.setMaximumSize(personItemSize);
          
          view.add(personItem);
          //restPanel.showInfo(type, name);//puts hungry button on panel
          validate();
    }
    
    public void resetCB(Object person){
    	if (person instanceof CustomerAgent) {
    		CustomerAgent cust = (CustomerAgent) person;
	    	for (JCheckBox temp:list){
	            if (cust.getName() == temp.getText()) {
	//                restPanel.showInfo(type, temp.getText());
	                temp.setEnabled(true);
	                temp.setSelected(false);
	            }
	        }
    	}
    	if (person instanceof WorkerAgent) {
    		WorkerAgent worker = (WorkerAgent) person;
    		for (JCheckBox temp:list){
	            if (worker.getName() == temp.getText()) {
	                temp.setSelected(false);
	            }
	        }
    	}
    }
    
}
