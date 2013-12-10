package restaurant_cafe.gui;

import restaurant_cafe.CustomerAgent;
import restaurant_cafe.WaiterAgent;

import javax.swing.*;

import agent_cafe.Agent;

import java.awt.*;
import java.awt.event.*;
/**
 * Main GUI class.
 * Contains the main frame and subsequent panels
 */
public class RestaurantCafeGui extends JFrame implements ActionListener {
    /* The GUI has two frames, the control frame (in variable gui) 
     * and the animation frame, (in variable animationFrame within gui)
     */
	JFrame animationFrame = new JFrame("Restaurant Animation");
	CafeAnimationPanel animationPanel = new CafeAnimationPanel();
	
    /* restPanel holds 2 panels
     * 1) the staff listing, menu, and lists of current customers all constructed
     *    in RestaurantPanel()
     * 2) the infoPanel about the clicked Customer (created just below)
     */    
    //private RestaurantCafe restPanel = new RestaurantCafe(this);
    
    /* infoPanel holds information about the clicked customer, if there is one*/
    private JPanel optionsPanel  = new JPanel();
    private JPanel infoPanel;
    private JLabel infoLabel; //part of infoPanel
    private JCheckBox stateCB;//part of infoLabel
    private JPanel namePanel; //panel under infoPanel
    private JLabel nameLabel; //label in namePanel
    private JPanel addPersonPanel = new JPanel();
    private JLabel namePrompt = new JLabel("Please enter a name:");
    private JTextField nameField = new JTextField();
    private JCheckBox hungryBox = new JCheckBox("Hungry (only for customers)", true);
    private int BIGWINDOWX = 1240;
    private int BIGWINDOWY = 610;
    private int WINDOWX = 400;
    private int WINDOWY = 330;
  

    private Object currentPerson;/* Holds the agent that the info is about.
    								Seems like a hack */

    /**
     * Constructor for RestaurantGui class.
     * Sets up all the gui components.
     */
    public RestaurantCafeGui() {
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
     //   restPanel.setPreferredSize(restDim);
     //   restPanel.setMinimumSize(restDim);
     //   restPanel.setMaximumSize(restDim);
      //  optionsPanel.add(restPanel);
        
        Dimension addDim = new Dimension((int)(WINDOWX*.95), (int) (WINDOWY*.06));
        addPersonPanel.setPreferredSize(addDim);
        addPersonPanel.setMinimumSize(addDim);
        addPersonPanel.setMaximumSize(addDim);
        addPersonPanel.setLayout(new BorderLayout(10, 20));
        addPersonPanel.add(namePrompt, BorderLayout.WEST);
        addPersonPanel.add(nameField, BorderLayout.CENTER);
        addPersonPanel.add(hungryBox, BorderLayout.EAST);
        optionsPanel.add(addPersonPanel);
        
        // Now, setup the info panel
        Dimension infoDim = new Dimension(WINDOWX-20, (int) (WINDOWY*.18));
        infoPanel = new JPanel();
        infoPanel.setPreferredSize(infoDim);
        infoPanel.setMinimumSize(infoDim);
        infoPanel.setMaximumSize(infoDim);
        infoPanel.setBorder(BorderFactory.createTitledBorder("Information"));

        stateCB = new JCheckBox();
        stateCB.setVisible(false);
        stateCB.addActionListener(this);

        infoPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 20));
        
        infoLabel = new JLabel(); 
        infoLabel.setText("<html><pre><i>Click Add to make customers</i></pre></html>");
        infoPanel.add(infoLabel);
        infoPanel.add(stateCB);
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

        if (person instanceof CustomerAgent) {
            stateCB.setVisible(true);
            CustomerAgent customer = (CustomerAgent) person;
            stateCB.setText("Hungry?");
          //Should checkmark be there? 
            stateCB.setSelected(customer.getGui().isHungry());
          //Is customer hungry? Hack. Should ask customerGui
            stateCB.setEnabled(!customer.getGui().isHungry());
          //added code
            nameField.setText("");
            hungryBox.setSelected(false);
          //end of added code
          // Hack. Should ask customerGui
            infoLabel.setText(
               "<html><pre>     Name: " + customer.getName() + " </pre></html>");
        }
        else if(person instanceof WaiterAgent){
        	 stateCB.setVisible(true);
             WaiterAgent waiter = (WaiterAgent) person;
             stateCB.setText("Should Break?");
           //Should checkmark be there? 
              stateCB.setSelected(waiter.getGui().isOnBreak());
           //Is customer hungry? Hack. Should ask customerGui
              stateCB.setEnabled(true);
           //added code
             nameField.setText("");
             hungryBox.setSelected(false);
           //end of added code
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
                c.getGui().setHungry();
                stateCB.setEnabled(false);
            }
            else if (currentPerson instanceof WaiterAgent) {
                WaiterAgent w = (WaiterAgent) currentPerson;
                if(stateCB.isSelected()){
                	w.getGui().goOnBreak();
                }
                else {
                	w.getGui().endBreak();	
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
    
    public void setOffBreak(WaiterAgent w){
    	if (currentPerson instanceof WaiterAgent) {
    		WaiterAgent waiter = (WaiterAgent) currentPerson;
            if (w.equals(waiter)) {
                stateCB.setSelected(false);
            }
        }
    }
    /**
     * Main routine to get gui started
     */
    public static void main(String[] args) {
        RestaurantCafeGui gui = new RestaurantCafeGui();
        gui.setTitle("Los Pollos Hermanos");
        gui.setVisible(true);
        gui.setResizable(false);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    public String getNameFieldText(){
    	return nameField.getText();
    }
    
    public boolean getHungryState(){
    	return hungryBox.isSelected();
    }
    public int getAnimWindowX(){
    	return WINDOWX;
    }
    public int getAnimWindowY(){
    	return WINDOWY;
    }
}
