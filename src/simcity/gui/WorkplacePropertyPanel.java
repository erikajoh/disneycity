package simcity.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import simcity.gui.trace.AlertLog;
import simcity.gui.trace.AlertTag;
import simcity.RestMenu;


public class WorkplacePropertyPanel extends JPanel implements ActionListener {
	SimCityGui gui;

	int selectedWorkplaceIndex = 0;
	String[] workplaces;
	
	String selectedMktItem = "";
	int selectedMktItemIndex = 0;
	String[] marketInventory;
	
	String selectedMktWorker = "";
	int selectedMktWorkerIndex = 0;
	String[] marketWorkers;
	
	enum WorkplaceType{Market, Restaurant, Bank};
	WorkplaceType type;
	
	JComboBox workplaceList;
	JComboBox inventoryList;
	JComboBox mktWorkersList;
	
	JPanel properties = new JPanel();
	JPanel inventory = new JPanel();
	JPanel editInventory;
	JPanel setInventory;
	JPanel setInventory1;
	JPanel setInventory2;
	JPanel setInventory3;
	JPanel setInventory4;
	JPanel swapMktWorkers = new JPanel();

	JPanel tellers = new JPanel();
	JPanel restaurants = new JPanel();
	
	RestMenu menu;
	
public WorkplacePropertyPanel(SimCityGui gui) {
		
		this.gui = gui;
		updateGui();
	}

	public void updateGui(){
	    clear();
		properties.removeAll();
		properties.setLayout(new GridLayout(4, 1));

		JPanel workplace = new JPanel();
		
		workplaces = new String[]{ "1) Mickey's Market", "2) Carnation Cafe", "4) Blue Bayou", "8) Pirate Bank", "9) Rancho Del Zocalo", "12) Village Haus", "14) Pizza Port" };
		workplaceList = new JComboBox(workplaces);

		    if(type == null){
			    setType(workplaceList.getSelectedItem().toString());
		    }
		    
		AlertLog.getInstance().logMessage(AlertTag.CITY, "WPP", ""+type);
		
		workplaceList.setSelectedIndex(selectedWorkplaceIndex);
		workplaceList.addActionListener(this);
		workplace.setLayout(new FlowLayout());
		workplace.add(new JLabel("Choose a workplace:"));
		workplace.add(workplaceList);
		properties.add(workplace);
		
		if(type == WorkplaceType.Market){
		   inventory = new JPanel();
		   marketInventory = SimCityGui.mickeysMarket.getInventory();
		   inventoryList = new JComboBox(marketInventory);
		   inventoryList.setSelectedIndex(selectedMktItemIndex);
		   inventoryList.addActionListener(this);
		
		   inventory.add(new JLabel("Choose an inventory item:"));
		   inventory.add(inventoryList);
		   properties.add(inventory);
		
		   if(selectedMktItem.equals("")){
			   selectedMktItem = inventoryList.getSelectedItem().toString();
		   }
		
		   editInventory = new JPanel();
		   editInventory.setLayout(new FlowLayout());
		   editInventory.add(new JLabel("Current Quantity: "+SimCityGui.mickeysMarket.getItemQty(selectedMktItem)+"   "));
		   editInventory.add(new JLabel("Set Quantity:"));
		   SpinnerModel model = new SpinnerNumberModel(SimCityGui.mickeysMarket.getItemQty(selectedMktItem), 0, 100, 1);     
		   JSpinner spinner = new JSpinner(model);
		   editInventory.add(spinner);
		   properties.add(editInventory);
		   
		   swapMktWorkers = new JPanel();
		   swapMktWorkers.setLayout(new FlowLayout());
		   swapMktWorkers.add(new JLabel("Swap workers: "));
		   marketWorkers = SimCityGui.mickeysMarket.getWorkers();
		   mktWorkersList = new JComboBox(marketWorkers);
		   if(mktWorkersList.getItemCount() != 0){
		       mktWorkersList.setSelectedIndex(selectedMktWorkerIndex);
		   }
		   mktWorkersList.addActionListener(this);
		   swapMktWorkers.add(mktWorkersList);
		   properties.add(swapMktWorkers);
		   
		}
		else if(type == WorkplaceType.Bank){
			tellers = new JPanel();
			tellers.setLayout(new FlowLayout());
			tellers.add(new JLabel("Set Teller Amount:"));
			SpinnerModel model = new SpinnerNumberModel(4, 1, 4, 1);     
			JSpinner spinner = new JSpinner(model);
			tellers.add(spinner);
			properties.add(tellers);
		}
		else if(type == WorkplaceType.Restaurant){
			restaurants = new JPanel();
			restaurants.setLayout(new FlowLayout());
			restaurants.add(new JLabel("Set Balance:"));
			SpinnerModel model = new SpinnerNumberModel(100.00, 0.00, 999.99, 5);     
			JSpinner spinner = new JSpinner(model);
			JSpinner.NumberEditor editor = (JSpinner.NumberEditor)spinner.getEditor();  
		    DecimalFormat format = editor.getFormat();  
		    format.setMinimumFractionDigits(2);  
		    editor.getTextField().setHorizontalAlignment(SwingConstants.CENTER);  
	        Dimension d = spinner.getPreferredSize();  
	        d.width = 80;  
	        spinner.setPreferredSize(d);
			restaurants.add(spinner);
			
		/*	if (workplaceList.getSelectedItem().toString().contains("Bayou"))
				menu = SimCityGui.restBayou.getMenu();
			else if (workplaceList.getSelectedItem().toString().contains("Cafe"))
				menu = SimCityGui.restCafe.getMenu();
			else if (workplaceList.getSelectedItem().toString().contains("Haus"))
				menu = SimCityGui.restHaus.getMenu();
			else if (workplaceList.getSelectedItem().toString().contains("Pizza"))
				menu = SimCityGui.restPizza.getMenu();
			else if (workplaceList.getSelectedItem().toString().contains("Rancho"))
				menu = SimCityGui.restRancho.getMenu();
			
			setInventory = new JPanel();
			setInventory1 = new JPanel();
			setInventory2 = new JPanel();
			setInventory3 = new JPanel();
			if (menu.menuList.size() > 4) setInventory4 = new JPanel();
			
			setInventory.setLayout(new FlowLayout());
			setInventory1.setLayout(new FlowLayout());
			setInventory2.setLayout(new FlowLayout());
			setInventory3.setLayout(new FlowLayout());
			if (menu.menuList.size() > 4) setInventory4.setLayout(new FlowLayout());
			
			JLabel food0 = new JLabel(menu.menuList.get(0));
			setInventory.add(food0);
			JLabel food1 = new JLabel(menu.menuList.get(1));
			setInventory1.add(food1);
			JLabel food2 = new JLabel(menu.menuList.get(2));
			setInventory2.add(food2);
			JLabel food3 = new JLabel(menu.menuList.get(3));
			setInventory3.add(food3);
			if (menu.menuList.size() > 4) {
				JLabel food4 = new JLabel(menu.menuList.get(4));
				setInventory4.add(food4);
			}
			
			SpinnerModel foodModel = new SpinnerNumberModel(10.00, 0.00, 50.00, 1);
		    JSpinner spinner0 = new JSpinner(model);
			JSpinner spinner1 = new JSpinner(model);
			JSpinner spinner2 = new JSpinner(model);
		    JSpinner spinner3 = new JSpinner(model);
		    if (menu.menuList.size() > 4) {
		    	JSpinner spinner4 = new JSpinner(model);
			}
			JSpinner.NumberEditor editor1 = (JSpinner.NumberEditor)spinner.getEditor();  
		    DecimalFormat format1 = editor.getFormat();  
		    format.setMinimumFractionDigits(2);  
		    editor.getTextField().setHorizontalAlignment(SwingConstants.CENTER);  
	        Dimension d1 = spinner.getPreferredSize();  
	        d.width = 80;  
	        spinner.setPreferredSize(d);
	      
			*/
				
			
			properties.add(restaurants);
		}
		
		add(properties);
		
	    setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		
	}

	public void clear(){
		properties.remove(inventory);
		properties.remove(editInventory);
		properties.remove(swapMktWorkers);
		properties.remove(tellers);
		properties.remove(restaurants);
		properties.revalidate();
		properties.repaint();
	}
	
	public void setType(String name){
		if(name.contains("Market")){
			type = WorkplaceType.Market;
		}
		else if(name.contains("Bank")){
			type = WorkplaceType.Bank;
		}
		else {
			type = WorkplaceType.Restaurant;	
		}	
	}

	public void actionPerformed(ActionEvent e) {
	JComboBox cb = (JComboBox)e.getSource();
    String name = (String)cb.getSelectedItem();
    if(cb == workplaceList){
	     for(int i = 0; i<workplaces.length; i++){
		     if(workplaces[i].equals(name)){
		    	 setType(name);
			     selectedWorkplaceIndex = i;
			     updateGui();
			     break;
		     }
	     }
     }
    else if(cb == inventoryList){
	     for(int i = 0; i<marketInventory.length; i++){
		     if(marketInventory[i].equals(name)){
			     selectedMktItem = name;
			     selectedMktItemIndex = i;
			     updateGui();
			     break;
		     }
	     }
      }
    else if(cb == mktWorkersList){
	     for(int i = 0; i<marketWorkers.length; i++){
		     if(marketWorkers[i].equals(name)){
			     selectedMktWorker = name;
			     selectedMktWorkerIndex = i;
			     updateGui();
			     break;
		     }
	     }
     }
	}

}
