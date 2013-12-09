package simcity.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
	JCheckBox preferAtHomeCheckBox;
	JComboBox transportationList;
	
	JButton addPersonButton = new JButton("Create person");
	
	public PersonPropertyPanel(SimCityGui gui) {
		this.gui = gui;
		updateGui();
	}

	// TODO: notes with creating person:
	// They will always be renters since owners are set in stone
	// 
	public void updateGui(){
	    clear();
		settings.removeAll();
		settings.setLayout(new GridLayout(10, 2));
        Dimension panelDim = new Dimension(354,50);  
        
		JLabel label = new JLabel("Name");
		settings.add(label);
		
        nameField = new JTextField();
        settings.add(nameField);
        
        label = new JLabel("Housing");
		settings.add(label);
        
        housingList = new JComboBox(SimCityGui.simCityPanel.getAllHousing());
        settings.add(housingList);
        
        label = new JLabel("Staring money");
		settings.add(label);
        
        SpinnerModel startMoneySpinner = new SpinnerNumberModel(START_MONEY, 0, 100, 1);
        moneySpinner = new JSpinner(startMoneySpinner);
        settings.add(moneySpinner);
        
        label = new JLabel("Food preference");
		settings.add(label);
        
        String[] foodPreferenceArray = {"Italian", "Mexican", "Southern", "American", "German"};
        foodPreferenceList = new JComboBox(foodPreferenceArray);
        settings.add(foodPreferenceList);
        
        label = new JLabel("Personality type");
		settings.add(label);
        
        String[] personalityArray = {"Normal", "Deadbeat", "Crook"};
        personalityList = new JComboBox(personalityArray);
        settings.add(personalityList);
        
        label = new JLabel("Start eating at home?");
		settings.add(label);
		
		preferAtHomeCheckBox = new JCheckBox();
		settings.add(preferAtHomeCheckBox);
        
		label = new JLabel("Transportation type");
		settings.add(label);
        
        String[] transportationArray = {"Walk", "Bus", "Car"};
        transportationList = new JComboBox(transportationArray);
        settings.add(transportationList);
		
        addPersonButton.addActionListener(this);
        settings.add(addPersonButton);
		add(settings);
	    setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
	}
	
	public void clear(){
		
		//settings.revalidate();
		//settings.repaint();
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == addPersonButton) {
			String personName = nameField.getText();
			/*gui.simCityPanel.addPerson(personName, (String)housingList.getSelectedItem(), 
					(double)moneySpinner.getValue(), (String)foodPreferenceList.getSelectedItem(),
					preferAtHomeCheckBox.isSelected(), ((String)transportationList.getSelectedItem()).charAt(0),
					(String)personalityList.getSelectedItem());*/
		}
	}
}
