package bank.gui;

import bank.BankCustomerAgent;
import bank.BankAgent;
import bank.PersonAgent;
import bank.TellerAgent;

import javax.swing.*;

import agent.Agent;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Vector;

/**
 * Panel in frame that contains all the bank information,
 * including host, cook, waiters, and customers.
 */
public class BankPanel extends JPanel implements ActionListener {

    //Host, cook, waiters and customers

    private Vector<BankCustomerAgent> customers = new Vector<BankCustomerAgent>();
    private Vector<TellerAgent> tellers = new Vector<TellerAgent>();
    

    private JPanel restLabel = new JPanel();
    private ListPanel customerPanel = new ListPanel(this, "Customers");
    private ListPanel tellerPanel = new ListPanel(this, "Tellers");
    private JButton  pauseButton = new JButton("Pause Agents");
    private JPanel pauseGroup = new JPanel();
    private JPanel group = new JPanel();

    private BankGui gui; //reference to main gui
    
    private BankAgent bank = new BankAgent("Bank", gui);

    public BankPanel(BankGui gui) {
        this.gui = gui;

        bank.startThread();
        
        setLayout(new GridLayout(1, 2, 20, 20));
        group.setLayout(new GridLayout(1, 2, 10, 10));

        group.add(customerPanel);
        group.add(tellerPanel);

        initRestLabel();
        pauseGroup.setLayout(new BorderLayout());
        pauseButton.addActionListener(this);
        pauseGroup.add(pauseButton, BorderLayout.NORTH);
        pauseGroup.add(restLabel, BorderLayout.CENTER);
        add(pauseGroup);
        add(group);
    }

    /**
     * Sets up the bank label that includes the menu,
     * and host and cook information
     */
    private void initRestLabel() {
        JLabel label = new JLabel();
        //restLabel.setLayout(new BoxLayout((Container)restLabel, BoxLayout.Y_AXIS));
        restLabel.setLayout(new BorderLayout());
        label.setText(
                "<html><h3><u>The Bank</u></h3></html>");
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
                BankCustomerAgent temp = customers.get(i);
                if (temp.getName() == name)
                    gui.updateInfoPanel(temp);
            }
        }
        else  if (type.equals("Tellers")) {
        	 for (TellerAgent teller : tellers) {
                 if (teller.getName() == name){
                     gui.updateInfoPanel(teller);
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
    public void addPerson(String type, String name) {

    	if (type.equals("Customers")) {
    		PersonAgent p = new PersonAgent(name);
    		BankCustomerAgent c = new BankCustomerAgent(name, gui);	
    		BankCustomerGui g = new BankCustomerGui(c, gui, gui.getAnimWindowX(), gui.getAnimWindowY());
    		c.setBalance(25.00);
    		c.setBank(bank);
    		gui.animationPanel.addGui(g);// dw
    		c.setGui(g);
    		customers.add(c);
    		c.startThread();
    	}
    	else if (type.equals("Tellers")) {
    		TellerAgent t = new TellerAgent(name);	
    		TellerGui g = new TellerGui(t, gui, tellers.size());
    		gui.animationPanel.addGui(g);
    		t.setBank(bank);
    		tellers.add(t);
    		t.setGui(g);
    		bank.addTeller(t);
    		t.startThread();
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
  
}

