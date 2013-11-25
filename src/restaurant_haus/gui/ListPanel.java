package restaurant_haus.gui;

import restaurant_haus.CustomerAgent;
import restaurant_haus.HostAgent;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Subpanel of restaurantPanel.
 * This holds the scroll panes for the customers and, later, for waiters
 */
public class ListPanel extends JPanel implements ActionListener {
	
	static final int NAMEFIELDWIDTH = 180;
	static final int BORDERSIZE = 10;
	//static final int BUTTONWIDTH = 150;
	
    public JScrollPane pane =
            new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private JPanel view = new JPanel();
    private List<JButton> list = new ArrayList<JButton>();
    private JButton addPersonB = new JButton("Add");
    private JButton pauseB = new JButton("Pause/Unpause");
    private JPanel buttonPanel = new JPanel();
    
    private JCheckBox initialHungry = new JCheckBox("Hungry?");
    
    private JLabel  nameLabel = new JLabel("Name: ");
    private JPanel namePanel = new JPanel();

    private RestaurantHaus restPanel;
    private String type;
    
    private JTextField customerName = new JTextField("");
    /**
     * Constructor for ListPanel.  Sets up all the gui
     *
     * @param rp   reference to the restaurant panel
     * @param type indicates if this is for customers or waiters
     */
    public ListPanel(RestaurantHaus rp, String type) {
        restPanel = rp;
        this.type = type;

        setLayout(new BoxLayout((Container) this, BoxLayout.Y_AXIS));
        add(new JLabel("<html><pre> <u>" + type + "</u><br></pre></html>"));
        
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        addPersonB.addActionListener(this);
        buttonPanel.add(addPersonB);
        
        pauseB.addActionListener(this);
        //pauseB.setPreferredSize(new Dimension(BUTTONWIDTH, pauseB.getHeight()));
        buttonPanel.add(pauseB);
        add(buttonPanel);
        
        if(type.equals("Customers")) {
        	add(initialHungry);
        }
        
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.X_AXIS));
        namePanel.add(nameLabel);
        customerName.setMaximumSize(new Dimension(NAMEFIELDWIDTH,getPreferredSize().height));
        namePanel.add(customerName);
        add(namePanel);
        

        view.setLayout(new BoxLayout((Container) view, BoxLayout.Y_AXIS));
        pane.setViewportView(view);
        add(pane);
    }

    public boolean getHungry() {
    	return initialHungry.isSelected();
    }
    
    /**
     * Method from the ActionListener interface.
     * Handles the event of the add button being pressed
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addPersonB) {
        	// Chapter 2.19 describes showInputDialog()
            //addPerson(JOptionPane.showInputDialog("Please enter a name:"));
        	if(customerName.getText().length() != 0) {
        		addPerson(customerName.getText());
        		customerName.setText("");
        	}
        }
        else if(e.getSource() == pauseB) {
        	restPanel.pause();
        	/*
        	if(pauseB.getText() == "Pause") {
        		pauseB.setText("Unpause");
        	}
        	else {
        		pauseB.setText("Pause");
        	}
        	*/
        }
        else {
        	// Isn't the second for loop more beautiful?
            /*for (int i = 0; i < list.size(); i++) {
                JButton temp = list.get(i);*/
       // 	for (JButton temp:list){
       //         if (e.getSource() == temp)
       //             restPanel.showInfo(type, temp.getText());
       //     }
        }
    }

    /**
     * If the add button is pressed, this function creates
     * a spot for it in the scroll pane, and tells the restaurant panel
     * to add a new person.
     *
     * @param name name of new person
     */
    public void addPerson(String name) {
        if (name != null) {
            JButton button = new JButton(name);
            button.setBackground(Color.white);

            Dimension paneSize = pane.getSize();
            Dimension buttonSize = new Dimension(paneSize.width - BORDERSIZE*2,
                    (int) (paneSize.height / 7));
            button.setPreferredSize(buttonSize);
            button.setMinimumSize(buttonSize);
            button.setMaximumSize(buttonSize);
            button.addActionListener(this);
            list.add(button);
            view.add(button);
         //   restPanel.addPerson(type, name);//puts customer on list
          //  restPanel.showInfo(type, name);//puts hungry button on panel
            validate();
        }
    }
}
