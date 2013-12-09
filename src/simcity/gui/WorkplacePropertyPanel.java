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
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;


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
	
	String selectedPersonForMkt = "";
	int selectedPersonForMktIndex = 0;
	String[] peopleForMkt;
	
	String selectedMenuItem = "";
	int selectedMenuItemIndex = 0;
	String[] menu;
	
	String selectedRestWorker = "";
	int selectedRestWorkerIndex = 0;
	String[] restWorkers;
	
	String selectedPersonForRest = "";
	int selectedPersonForRestIndex = 0;
	String[] peopleForRest;
	
	enum WorkplaceType{Market, Restaurant, Bank};
	WorkplaceType type;
	
	JComboBox workplaceList;
	JComboBox inventoryList;
	JComboBox mktWorkersList;
	JComboBox peopleForMktList;
	JComboBox restWorkersList;
	JComboBox peopleForRestList;
	JComboBox menuItemsList;

	JButton setMktQuantity = new JButton("Set");
	JButton swapMktJobs = new JButton("Swap");
	JButton swapRestJobs = new JButton("Swap");
	JButton setFoodQtyAndBalance = new JButton("Set");
	JButton setTellerAmt = new JButton("Set");
	
	JPanel properties = new JPanel();
	JPanel inventory = new JPanel();
	JPanel editInventory = new JPanel();
	
	/*JPanel setInventory;
	JPanel setInventory1;
	JPanel setInventory2;
	JPanel setInventory3;
	JPanel setInventory4;*/
	JPanel swapMktWorkers = new JPanel();
	JPanel swapRestWorkers = new JPanel();

	JPanel tellers = new JPanel();
	JPanel menuItems = new JPanel();
	JPanel editMenuItems = new JPanel();
		
public WorkplacePropertyPanel(SimCityGui gui) {
		
		this.gui = gui;
		updateGui();
	}

	public void updateGui(){
	    clear();
		properties.removeAll();
		
		properties.setLayout(new GridLayout(4, 1));
		JLabel label;
		
        Dimension panelDim = new Dimension(354,50);  

		JPanel workplace = new JPanel();
		
		workplaces = new String[]{ "1) Mickey's Market", "2) Carnation Cafe", "4) Blue Bayou", "8) Pirate Bank", "9) Rancho Del Zocalo", "12) Village Haus", "14) Pizza Port" };
		workplaceList = new JComboBox(workplaces);
		workplaceList.setFont(workplaceList.getFont().deriveFont(12.0f));

		    if(type == null){
			    setType(workplaceList.getSelectedItem().toString());
		    }
		    		
		workplaceList.setSelectedIndex(selectedWorkplaceIndex);
		workplaceList.addActionListener(this);
		workplace.setLayout(new FlowLayout());
		label = new JLabel("Choose a workplace:");
		label.setFont(label.getFont().deriveFont(12.0f));
		workplace.add(label);
		workplace.add(workplaceList);
		properties.add(workplace);
		
		if(type == WorkplaceType.Market){
		   inventory = new JPanel();
	       inventory.setMaximumSize(panelDim);
		   marketInventory = SimCityGui.mickeysMarket.getInventory();
		   inventoryList = new JComboBox(marketInventory);
		   inventoryList.setFont(inventoryList.getFont().deriveFont(12.0f));
		   inventoryList.setSelectedIndex(selectedMktItemIndex);
		   inventoryList.addActionListener(this);
		
		   label = new JLabel("Choose an inventory item:");
		   label.setFont(label.getFont().deriveFont(12.0f));
		   inventory.add(label);
		   inventory.add(inventoryList);
		   properties.add(inventory);
		
		   if(selectedMktItem.equals("")){
			   selectedMktItem = inventoryList.getSelectedItem().toString();
		   }
		
		   editInventory = new JPanel();
		   editInventory.setMaximumSize(panelDim);
		   editInventory.setLayout(new FlowLayout());
		   label = new JLabel("Current Quantity: "+SimCityGui.mickeysMarket.getItemQty(selectedMktItem)+"   ");
		   label.setFont(label.getFont().deriveFont(12.0f));
		   editInventory.add(label);
		   label = new JLabel("Set Quantity:");
		   label.setFont(label.getFont().deriveFont(12.0f));
		   editInventory.add(label);
		   SpinnerModel model = new SpinnerNumberModel(SimCityGui.mickeysMarket.getItemQty(selectedMktItem), 0, 100, 1);     
		   JSpinner spinner = new JSpinner(model);
		   spinner.setFont(spinner.getFont().deriveFont(12.0f));
		   editInventory.add(spinner);
		   setMktQuantity.setFont(setMktQuantity.getFont().deriveFont(12.0f));
		   editInventory.add(setMktQuantity);
		   properties.add(editInventory);
		   
		   swapMktWorkers = new JPanel();
		   swapMktWorkers.setLayout(new FlowLayout());
		   marketWorkers = SimCityGui.mickeysMarket.getWorkers();
		   mktWorkersList = new JComboBox(marketWorkers);
		   mktWorkersList.setFont(mktWorkersList.getFont().deriveFont(12.0f));
		   if(mktWorkersList.getItemCount() != 0){
		       mktWorkersList.setSelectedIndex(selectedMktWorkerIndex);
		   }
		   mktWorkersList.addActionListener(this);
		   swapMktWorkers.add(mktWorkersList);
		   label = new JLabel("and");
		   label.setFont(label.getFont().deriveFont(12.0f));
		   swapMktWorkers.add(label);
		   
		   peopleForMkt = SimCityGui.simCityPanel.getAllUnemployedPeople();
		   peopleForMktList = new JComboBox(peopleForMkt);
		   peopleForMktList.setFont(peopleForMktList.getFont().deriveFont(12.0f));
		   if(peopleForMktList.getItemCount() != 0){
		       peopleForMktList.setSelectedIndex(selectedPersonForMktIndex);
		   }
		   peopleForMktList.addActionListener(this);
		   swapMktWorkers.add(peopleForMktList);
		   swapMktJobs.setFont(swapMktJobs.getFont().deriveFont(12.0f));
		   swapMktWorkers.add(swapMktJobs);
		   properties.add(swapMktWorkers);
		}
		else if(type == WorkplaceType.Bank){
			tellers = new JPanel();
			tellers.setLayout(new FlowLayout());
			label = new JLabel("Set Teller Amount:");
		    label.setFont(label.getFont().deriveFont(12.0f));
			tellers.add(label);
			SpinnerModel model = new SpinnerNumberModel(4, 1, 4, 1);     
			JSpinner spinner = new JSpinner(model);
			spinner.setFont(spinner.getFont().deriveFont(12.0f));
			tellers.add(spinner);
			setTellerAmt.setFont(setTellerAmt.getFont().deriveFont(12.0f));
			tellers.add(setTellerAmt);
			properties.add(tellers);
		}
		else if(type == WorkplaceType.Restaurant){
			
			if (workplaceList.getSelectedItem().toString().contains("Bayou")){
				menu = SimCityGui.restBayou.getFoodNames();
				restWorkers = SimCityGui.restBayou.getWorkers();
			}
			else if (workplaceList.getSelectedItem().toString().contains("Cafe")){
				menu = SimCityGui.restCafe.getFoodNames();
				restWorkers = SimCityGui.restCafe.getWorkers();
			}
			else if (workplaceList.getSelectedItem().toString().contains("Haus")){
				menu = SimCityGui.restHaus.getFoodNames();
				restWorkers = SimCityGui.restHaus.getWorkers();
			}
			else if (workplaceList.getSelectedItem().toString().contains("Pizza")){
				menu = SimCityGui.restPizza.getFoodNames();
				restWorkers = SimCityGui.restPizza.getWorkers();
			}
			else if (workplaceList.getSelectedItem().toString().contains("Rancho")){
				menu = SimCityGui.restRancho.getFoodNames();
				restWorkers = SimCityGui.restRancho.getWorkers();
			}
			
			   menuItems = new JPanel();
		       menuItems.setMaximumSize(panelDim);
			   menuItemsList = new JComboBox(menu);
			   menuItemsList.setFont(menuItemsList.getFont().deriveFont(12.0f));
			   menuItemsList.setSelectedIndex(selectedMenuItemIndex);
			   menuItemsList.addActionListener(this);
			
			   label = new JLabel("Choose an food:");
			   label.setFont(label.getFont().deriveFont(12.0f));
			   menuItems.add(label);
			   menuItems.add(menuItemsList);
			   properties.add(menuItems);
			
			   if(selectedMenuItem.equals("")){
				   selectedMenuItem = menuItemsList.getSelectedItem().toString();
			   }
			
			   editMenuItems = new JPanel();
			   editMenuItems.setMaximumSize(panelDim);
			   editMenuItems.setLayout(new FlowLayout());
			   label = new JLabel("Food Quantity:");
			   label.setFont(label.getFont().deriveFont(12.0f));
			   editMenuItems.add(label);
			   SpinnerModel model = new SpinnerNumberModel(/*SimCityGui.mickeysMarket.getItemQty(selectedMktItem)*/50, 0, 100, 1);     
			   JSpinner spinner = new JSpinner(model);
			   spinner.setFont(spinner.getFont().deriveFont(12.0f));
			   editMenuItems.add(spinner);
			   properties.add(editMenuItems);
			   
				label = new JLabel("Balance:");
				label.setFont(label.getFont().deriveFont(12.0f));
				editMenuItems.add(label);
			    model = new SpinnerNumberModel(100.00, 0.00, 999.99, 5);     
				JSpinner spinner2 = new JSpinner(model);
				spinner2.setFont(spinner2.getFont().deriveFont(12.0f));
				JSpinner.NumberEditor editor = (JSpinner.NumberEditor)spinner2.getEditor();  
			    DecimalFormat format = editor.getFormat();  
			    format.setMinimumFractionDigits(2);  
			    editor.getTextField().setHorizontalAlignment(SwingConstants.CENTER);  
		        Dimension d = spinner.getPreferredSize();  
		        d.width = 80;  
		        spinner.setPreferredSize(d);
		        editMenuItems.add(spinner2);
				
				setFoodQtyAndBalance.setFont(setFoodQtyAndBalance.getFont().deriveFont(12.0f));
				editMenuItems.add(setFoodQtyAndBalance);
				
				   swapRestWorkers = new JPanel();
				   swapRestWorkers.setLayout(new FlowLayout());
				   restWorkersList = new JComboBox(restWorkers);
				   restWorkersList.setFont(restWorkersList.getFont().deriveFont(12.0f));
				   if(restWorkersList.getItemCount() != 0){
				       restWorkersList.setSelectedIndex(selectedRestWorkerIndex);
				   }
				   restWorkersList.addActionListener(this);
				   swapRestWorkers.add(restWorkersList);
				   label = new JLabel("and");
				   label.setFont(label.getFont().deriveFont(12.0f));
				   swapRestWorkers.add(label);
				   
				   peopleForRest = SimCityGui.simCityPanel.getAllUnemployedPeople();
				   peopleForRestList = new JComboBox(peopleForRest);
				   peopleForRestList.setFont(peopleForRestList.getFont().deriveFont(12.0f));
				   if(peopleForRestList.getItemCount() != 0){
				       peopleForRestList.setSelectedIndex(selectedPersonForRestIndex);
				   }
				   peopleForRestList.addActionListener(this);
				   swapRestWorkers.add(peopleForRestList);
				   swapRestJobs.setFont(swapRestJobs.getFont().deriveFont(12.0f));
				   swapRestWorkers.add(swapRestJobs);
				   properties.add(swapRestWorkers);
		
			/*
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
			
		}
		
		add(properties);
		
	    setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
	}

	public void clear(){
		properties.remove(inventory);
		properties.remove(editInventory);
		properties.remove(swapMktWorkers);
		properties.remove(tellers);
		properties.remove(menuItems);
		properties.remove(editMenuItems);
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
    else if(cb == peopleForMktList){
	     for(int i = 0; i<peopleForMkt.length; i++){
		     if(peopleForMkt[i].equals(name)){
			     selectedPersonForMkt = name;
			     selectedPersonForMktIndex = i;
			     updateGui();
			     break;
		     }
	     }
    }
    else if(cb == restWorkersList){
	     for(int i = 0; i<restWorkers.length; i++){
		     if(restWorkers[i].equals(name)){
			     selectedRestWorker = name;
			     selectedRestWorkerIndex = i;
			     updateGui();
			     break;
		     }
	     }
    }
   else if(cb == peopleForRestList){
	     for(int i = 0; i<peopleForRest.length; i++){
		     if(peopleForRest[i].equals(name)){
			     selectedPersonForRest = name;
			     selectedPersonForRestIndex = i;
			     updateGui();
			     break;
		     }
	     }
   }
    else if(cb == menuItemsList){
	     for(int i = 0; i<menu.length; i++){
		     if(menu[i].equals(name)){
		    	 setType(name);
			     selectedMenuItemIndex = i;
			     updateGui();
			     break;
		     }
	     }
    }
    
	}

}
