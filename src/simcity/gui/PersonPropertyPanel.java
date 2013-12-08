package simcity.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import simcity.gui.trace.AlertLog;
import simcity.gui.trace.AlertTag;

public class PersonPropertyPanel extends JPanel implements ActionListener {
	
	private final static double START_MONEY = 20;
	
	SimCityGui gui;

	JPanel settings = new JPanel();
	
	JTextField nameField;
	JComboBox housingList;
	JSpinner moneySpinner;
	JComboBox foodPreferenceList;
	JComboBox personalityList;
	
	JButton addPersonButton = new JButton("Create person");
	
	public PersonPropertyPanel(SimCityGui gui) {
		this.gui = gui;
		updateGui();
	}

	public void updateGui(){
	    clear();
		settings.removeAll();
		settings.setLayout(new GridLayout(4, 1));
		
        Dimension panelDim = new Dimension(354,50);  
        
        nameField = new JTextField();
        housingList = new JComboBox(SimCityGui.simCityPanel.getAllHousing());
        
        SpinnerModel startMoneySpinner = new SpinnerNumberModel(START_MONEY, 0, 100, 1);
        moneySpinner = new JSpinner(startMoneySpinner);
        
        
        
        // display the GUI elements in the panel
        
        settings.add(nameField);
        settings.add(housingList);
        settings.add(moneySpinner);
        settings.add(nameField);
        settings.add(addPersonButton);
		add(settings);
	    setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
	}
	
	public void clear(){
		
		settings.revalidate();
		settings.repaint();
	}
	
	public void actionPerformed(ActionEvent e) {
		JComboBox cb = (JComboBox)e.getSource();
		String name = (String)cb.getSelectedItem();
		if(cb == housingList) {
		     
		}
		if(cb == personalityList) {
			
		}
	}
}
