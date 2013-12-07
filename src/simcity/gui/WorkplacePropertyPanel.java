package simcity.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import simcity.gui.trace.AlertLog;
import simcity.gui.trace.AlertTag;

public class WorkplacePropertyPanel extends JPanel implements ActionListener {
	SimCityGui gui;
	
public WorkplacePropertyPanel(SimCityGui gui) {
		
		this.gui = gui;
		/*
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = .8; 
		c.weighty = .6;
		c.gridx = 6;
		c.gridy = 0;
		c.gridwidth = 4;
		c.gridheight = 3;
		*/
		
		// workplace panel 
		JPanel selection = new JPanel();
		
		
		String[] workplaces = { "1) Mickey's Market", "2) Carnation Cafe", "4) Blue Bayou", "8) Pirate Bank", "9) Rancho Del Zocalo", "12) Village Haus", "14) Pizza Port" };
		// Create the combo box, select item at index 0.
		JComboBox workplaceList = new JComboBox(workplaces);
		workplaceList.setSelectedIndex(0);
		workplaceList.addActionListener(this);
		selection.setLayout(new BorderLayout());
		selection.add(new JLabel("Choose a workplace:"));
		
		String[] inventory = SimCityGui.mickeysMarket.getInventory();
		JComboBox inventoryList = new JComboBox(inventory);
		inventoryList.setSelectedIndex(0);
		inventoryList.addActionListener(this);

		selection.add(workplaceList);
		selection.add(inventoryList);
		add(selection);
				
	    setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
	}

	public void actionPerformed(ActionEvent e) {
	JComboBox cb = (JComboBox)e.getSource();
    String workplaceName = (String)cb.getSelectedItem();
	AlertLog.getInstance().logInfo(AlertTag.CITY, "Workplace properties list", "Changed to "+workplaceName);
}

}
