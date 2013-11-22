package bank.gui;

import bank.BankCustomerAgent;
import bank.interfaces.BankCustomer;
import bank.interfaces.Person;
import bank.gui.BankCustomerGui;

import javax.swing.*;

import agent.Agent;

import java.awt.*;
import java.awt.event.*;
/**
 * Main GUI class.
 * Contains the main frame and subsequent panels
 */
public class BankGui extends JFrame implements ActionListener {
    /* The GUI has two frames, the control frame (in variable gui) 
     * and the animation frame, (in variable animationFrame within gui)
     */
	JFrame animationFrame = new JFrame("Bank Animation");
	AnimationPanel animationPanel = new AnimationPanel();
	
    /* bankPanel holds 2 panels
     * 1) the staff listing, menu, and lists of current customers all constructed
     *    in bankPanel()
     * 2) the infoPanel about the clicked Customer (created just below)
     */    
    private BankPanel bankPanel = new BankPanel(this);
    
    /* infoPanel holds information about the clicked customer, if there is one*/
    private JPanel optionsPanel  = new JPanel();
    private JPanel infoPanel;
    private JLabel infoLabel; //part of infoPanel
    private JCheckBox inBank;//part of infoLabel
    private JPanel namePanel; //panel under infoPanel
    private JLabel nameLabel; //label in namePanel
    private JPanel addPersonPanel = new JPanel();
    private JLabel namePrompt = new JLabel("Please enter a name:");
    private JTextField nameField = new JTextField();
    private int BIGWINDOWX = 1240;
    private int BIGWINDOWY = 610;
    private int WINDOWX = 550;
    private int WINDOWY = 450;
  

    private Object currentPerson;/* Holds the agent that the info is about.
    								Seems like a hack */

    /**
     * Constructor for bankGui class.
     * Sets up all the gui components.
     */
    public BankGui() {
        Dimension animDim = new Dimension((int)(WINDOWX), (int) (WINDOWY));
        animationPanel.setPreferredSize(animDim);
        animationPanel.setMinimumSize(animDim);
        animationPanel.setMaximumSize(animDim);
        animationPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        animationPanel.setVisible(true);

    	setBounds(0, 0, BIGWINDOWX, BIGWINDOWY);
    	setVisible(true);
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 20));
        
        Dimension optionsDim = new Dimension((int)(WINDOWX), (int) (WINDOWY));
        optionsPanel.setPreferredSize(optionsDim);
        optionsPanel.setMinimumSize(optionsDim);
        optionsPanel.setMaximumSize(optionsDim);
        optionsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 20));
        
        Dimension restDim = new Dimension((int)(WINDOWX-20), (int) (WINDOWY * .50));
        bankPanel.setPreferredSize(restDim);
        bankPanel.setMinimumSize(restDim);
        bankPanel.setMaximumSize(restDim);
        optionsPanel.add(bankPanel);
        
        Dimension addDim = new Dimension((int)(WINDOWX*.95), (int) (WINDOWY*.06));
        addPersonPanel.setPreferredSize(addDim);
        addPersonPanel.setMinimumSize(addDim);
        addPersonPanel.setMaximumSize(addDim);
        addPersonPanel.setLayout(new BorderLayout(10, 20));
        addPersonPanel.add(namePrompt, BorderLayout.WEST);
        addPersonPanel.add(nameField, BorderLayout.CENTER);
        optionsPanel.add(addPersonPanel);
        
        // Now, setup the info panel
        Dimension infoDim = new Dimension(WINDOWX-20, (int) (WINDOWY*.18));
        infoPanel = new JPanel();
        infoPanel.setPreferredSize(infoDim);
        infoPanel.setMinimumSize(infoDim);
        infoPanel.setMaximumSize(infoDim);
        infoPanel.setBorder(BorderFactory.createTitledBorder("Information"));
        
        inBank = new JCheckBox();
        inBank.setVisible(false);
        inBank.addActionListener(this);

        infoPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 20));
        
        infoLabel = new JLabel(); 
        infoLabel.setText("<html><pre><i>Click Add to make customers</i></pre></html>");
        infoPanel.add(infoLabel);
        infoPanel.add(inBank);
        optionsPanel.add(infoPanel);
        
        Dimension nameDim = new Dimension(WINDOWX-20, (int) (WINDOWY*.09));
        namePanel = new JPanel();
        namePanel.setPreferredSize(nameDim);
        namePanel.setMinimumSize(nameDim);
        namePanel.setMaximumSize(nameDim);
        //namePanel.setBorder(BorderFactory.createTitledBorder("By"));
        
        namePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        nameLabel = new JLabel();
        nameLabel.setText("By Dylan Eirinberg");
        namePanel.add(nameLabel);
        optionsPanel.add(namePanel);
        
        add(optionsPanel);
        add(animationPanel);
        //add(animationFrame.getContentPane());
    }
    /**
     * updateInfoPanel() takes the given customer (or, for v3, Host) object and
     * changes the information panel to hold that person's info.
     *
     * @param person customer (or waiter) object
     */
    public void updateInfoPanel(Object person) {
        currentPerson = person;
        if (person instanceof BankCustomerAgent) {
            BankCustomerAgent customer = (BankCustomerAgent) person;
        	//System.out.println(customer.toString() + " " + customer.getGui().isInBank());
            inBank.setVisible(true);
            inBank.setText("Reenter bank?");
            inBank.setSelected(customer.getGui().isInBank());
            inBank.setEnabled(!customer.getGui().isInBank());
          //added code
            nameField.setText("");
          //end of added code
          // Hack. Should ask customerGui
            infoLabel.setText(
               "<html><pre>     Name: " + customer.getName() + " </pre></html>");
        }
  
        infoPanel.validate();
    }
    
    /**
     * Action listener method that reacts to the checkbox being clicked;
     * If it's the customer's checkbox, it will make him hungry
     * For v3, it will propose a break for the waiter.
     */
    public void actionPerformed(ActionEvent e) {
    	 if (e.getSource() == inBank) {
             if (currentPerson instanceof BankCustomerAgent) {
            	 System.out.println("REENTER");
                 BankCustomerAgent p = (BankCustomerAgent) currentPerson;
                 //p.getBank().msgEnteredBank(p);
                 p.getGui().setInBank(true);
                 inBank.setEnabled(false);
                 Person person = p.getPerson();
                 int num = (int)(Math.random() * 2);
                 person.msgArrive(num);
             } 
    	 }
    }
    
    /**
     * Main routine to get gui started
     */
    public static void main(String[] args) {
        BankGui gui = new BankGui();
        gui.setTitle("The Bank");
        gui.setVisible(true);
        gui.setResizable(false);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    public String getNameFieldText(){
    	return nameField.getText();
    }
    
    public void addBankCustomerGui(BankCustomerGui bg){
    	animationPanel.addGui(bg);
    }
    
    public int getAnimWindowX(){
    	return WINDOWX;
    }
    public int getAnimWindowY(){
    	return WINDOWY;
    }
}
