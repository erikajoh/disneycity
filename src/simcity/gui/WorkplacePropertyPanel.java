package simcity.gui;

import java.awt.Checkbox;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import simcity.PersonAgent;
import simcity.gui.trace.AlertLog;
import simcity.gui.trace.AlertTag;


public class WorkplacePropertyPanel extends JPanel implements ActionListener {
	SimCityGui gui;

	String selectedWorkplace = "";
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
	
	String selectedTeller = "";
	int selectedTellerIndex = 0;
	String[] tellerWorkers;
	
	String selectedPersonForTeller = "";
	int selectedPersonForTellerIndex = 0;
	String[] peopleForTellers;
	
	String selectedRestWorker = "";
	int selectedRestWorkerIndex = 0;
	String[] restWorkers;
	
	String selectedPersonForRest = "";
	int selectedPersonForRestIndex = 0;
	String[] peopleForRest;
	
	JSpinner mktSpinner;
	JSpinner foodQtySpinner;
	JSpinner restBalSpinner;
	
	public class ComboBox {
		JComboBox comboBox;
		String [] list;
		String selectedItem = "";
		int selectedIndex = 0;
		public ComboBox(JComboBox cb, String[] l, String selItem, int selIndex){
			comboBox = cb;
			list = l;
			selectedItem = selItem;
			selectedIndex = selIndex;
		}
	};
	
	private List<ComboBox> comboBoxes = new ArrayList<ComboBox>();
	
	enum WorkplaceType{Market, Restaurant, Bank};
	WorkplaceType type;
	
	JComboBox workplaceList;
	JComboBox inventoryList;
	JComboBox mktWorkersList;
	JComboBox peopleForMktList;
	JComboBox restWorkersList;
	JComboBox peopleForRestList;
	JComboBox tellersList;
	JComboBox peopleForTellersList;
	JComboBox menuItemsList;
	JCheckBox close;

	JButton setMktQuantity;
	JButton swapMktJobs;
	JButton swapRestJobs;
	JButton setFoodQtyAndBalance;
	JButton setTellerAmt;
	JButton swapTellersJobs;
	JButton changeRobberyState;
	
	ButtonGroup radioGroup;
	JRadioButton success;
	JRadioButton fail;
	JRadioButton random;
	
	JPanel properties = new JPanel();
	JPanel inventory = new JPanel();
	JPanel editInventory = new JPanel();
	
	JPanel swapMktWorkers = new JPanel();
	JPanel swapRestWorkers = new JPanel();

	JPanel swapTellerWorkers = new JPanel();
	JPanel setHandleThief = new JPanel();
	JPanel menuItems = new JPanel();
	JPanel editMenuItems = new JPanel();
		
public WorkplacePropertyPanel(SimCityGui gui) {
		this.gui = gui;
		comboBoxes.add(new ComboBox(workplaceList, workplaces, selectedWorkplace, selectedWorkplaceIndex));
		comboBoxes.add(new ComboBox(inventoryList, marketInventory, selectedMktItem, selectedMktItemIndex));
		comboBoxes.add(new ComboBox(mktWorkersList, marketWorkers, selectedMktWorker, selectedMktWorkerIndex));
		comboBoxes.add(new ComboBox(peopleForMktList, peopleForMkt, selectedPersonForMkt, selectedPersonForMktIndex));
		comboBoxes.add(new ComboBox(menuItemsList, menu, selectedMenuItem, selectedMenuItemIndex));
		comboBoxes.add(new ComboBox(restWorkersList, restWorkers, selectedRestWorker, selectedRestWorkerIndex));
		comboBoxes.add(new ComboBox(peopleForRestList, peopleForRest, selectedPersonForRest, selectedPersonForRestIndex));
		updateGui();
	}

	@SuppressWarnings("unchecked")
	public void updateGui(){
	    clear();
		properties.removeAll();
		
		properties.setLayout(new GridLayout(4, 1));
		JLabel label;
		
        Dimension panelDim = new Dimension(354,50);  

		JPanel workplace = new JPanel();
		
		workplaces = new String[]{ "1) Mickey's Market", "2) Minnie's Market", "2) Carnation Cafe", "4) Blue Bayou", "8) Pirate Bank", "25) Buccaneer Bank", "9) Rancho Del Zocalo", "12) Village Haus", "14) Pizza Port" };
		workplaceList = new JComboBox(workplaces);
		workplaceList.setFont(workplaceList.getFont().deriveFont(12.0f));

		    if(type == null){
			    setType(workplaceList.getSelectedItem().toString());
		    }
		    if(selectedWorkplace.equals("")){
				   selectedWorkplace = workplaceList.getSelectedItem().toString();
			 }
		    		
		workplaceList.setSelectedIndex(selectedWorkplaceIndex);
		workplaceList.addActionListener(this);
		workplace.setLayout(new FlowLayout());
		label = new JLabel("Choose a workplace:");
		label.setFont(label.getFont().deriveFont(12.0f));
		workplace.add(label);
		workplace.add(workplaceList);
		close = new JCheckBox("Close");
		close.addActionListener(this);
		workplace.add(close);
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
		   mktSpinner = new JSpinner(model);
		   mktSpinner.setFont(mktSpinner.getFont().deriveFont(12.0f));
		   editInventory.add(mktSpinner);
		   setMktQuantity = new JButton("Set");
		   setMktQuantity.setFont(setMktQuantity.getFont().deriveFont(12.0f));
		   setMktQuantity.addActionListener(this);
		   editInventory.add(setMktQuantity);
		   properties.add(editInventory);
		   
		   swapMktWorkers = new JPanel();
		   swapMktWorkers.setLayout(new FlowLayout());
		   marketWorkers = SimCityGui.mickeysMarket.getWorkers();
		   mktWorkersList = new JComboBox(marketWorkers);
		   mktWorkersList.setFont(mktWorkersList.getFont().deriveFont(12.0f));
		   
		   if(mktWorkersList.getItemCount() != 0){
		       mktWorkersList.setSelectedIndex(selectedMktWorkerIndex);
		       
		       if(selectedMktWorker.equals("")){
				   selectedMktWorker = mktWorkersList.getSelectedItem().toString();
			   }
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
		       
			   if(selectedPersonForMkt.equals("")){
				   selectedPersonForMkt = peopleForMktList.getSelectedItem().toString();
			   }
		   }
		   
		   peopleForMktList.addActionListener(this);
		   swapMktWorkers.add(peopleForMktList);
		   swapMktJobs = new JButton("Swap");
		   swapMktJobs.setFont(swapMktJobs.getFont().deriveFont(12.0f));
		   swapMktJobs.addActionListener(this);
		   swapMktWorkers.add(swapMktJobs);
		   properties.add(swapMktWorkers);
		}
		else if(type == WorkplaceType.Bank){
			swapTellerWorkers = new JPanel();
			swapTellerWorkers.setLayout(new FlowLayout());
			tellerWorkers = SimCityGui.pirateBank.getTellers();
			tellersList = new JComboBox(tellerWorkers);
			   tellersList.setFont(tellersList.getFont().deriveFont(12.0f));
			   
			   if(tellersList.getItemCount() != 0){
				   tellersList.setSelectedIndex(selectedTellerIndex);
			       
			       if(selectedTeller.equals("")){
					   selectedTeller = tellersList.getSelectedItem().toString();
				   }
			   }
			   
			   tellersList.addActionListener(this);
			   swapTellerWorkers.add(tellersList);
			   label = new JLabel("and");
			   label.setFont(label.getFont().deriveFont(12.0f));
			   swapTellerWorkers.add(label);
			   
			   peopleForTellers = SimCityGui.simCityPanel.getAllUnemployedPeople();
			   peopleForTellersList = new JComboBox(peopleForTellers);
			   peopleForTellersList.setFont(peopleForTellersList.getFont().deriveFont(12.0f));
			   
			   if(peopleForTellersList.getItemCount() != 0){
			       peopleForTellersList.setSelectedIndex(selectedPersonForTellerIndex);
			       
				   if(selectedPersonForTeller.equals("")){
					   selectedPersonForTeller = peopleForTellersList.getSelectedItem().toString();
				   }
			   }
			   
			   peopleForTellersList.addActionListener(this);
			   swapTellerWorkers.add(peopleForTellersList);
			   swapTellersJobs = new JButton("Swap");
			   swapTellersJobs.setFont(swapTellersJobs.getFont().deriveFont(12.0f));
			   swapTellersJobs.addActionListener(this);
			   swapTellerWorkers.add(swapTellersJobs);
			   properties.add(swapTellerWorkers);
			   
			   setHandleThief = new JPanel();
			   radioGroup= new ButtonGroup();
			   label = new JLabel("Robbery Success:");
			   setHandleThief.add(label);
			   success = new JRadioButton("True");
			   fail = new JRadioButton("False");
			   random = new JRadioButton("Random");
			   random.setSelected(true);
			   radioGroup.add(success);
			   radioGroup.add(fail);
			   radioGroup.add(random);
			   setHandleThief.add(success);
			   setHandleThief.add(fail);
			   setHandleThief.add(random);
			   changeRobberyState = new JButton("Change");
			   changeRobberyState.setFont(changeRobberyState.getFont().deriveFont(12.0f));
			   changeRobberyState.addActionListener(this);
			   setHandleThief.add(changeRobberyState);
			   properties.add(setHandleThief);
		}
		else if(type == WorkplaceType.Restaurant){
			int foodQty = 0;
			if (workplaceList.getSelectedItem().toString().contains("Bayou")){
				menu = SimCityGui.restBayou.getFoodNames();
				restWorkers = SimCityGui.restBayou.getWorkers();
				foodQty = SimCityGui.restBayou.getQuantity(selectedMenuItem);
			}
			else if (workplaceList.getSelectedItem().toString().contains("Cafe")){
				menu = SimCityGui.restCafe.getFoodNames();
				restWorkers = SimCityGui.restCafe.getWorkers();
				foodQty = SimCityGui.restCafe.getQuantity(selectedMenuItem);
			}
			else if (workplaceList.getSelectedItem().toString().contains("Haus")){
				menu = SimCityGui.restHaus.getFoodNames();
				restWorkers = SimCityGui.restHaus.getWorkers();
				foodQty = SimCityGui.restHaus.getQuantity(selectedMenuItem);
			}
			else if (workplaceList.getSelectedItem().toString().contains("Pizza")){
				menu = SimCityGui.restPizza.getFoodNames();
				restWorkers = SimCityGui.restPizza.getWorkers();
				foodQty = SimCityGui.restPizza.getQuantity(selectedMenuItem);
			}
			else if (workplaceList.getSelectedItem().toString().contains("Rancho")){
				menu = SimCityGui.restRancho.getFoodNames();
				restWorkers = SimCityGui.restRancho.getWorkers();
				foodQty = SimCityGui.restRancho.getQuantity(selectedMenuItem);
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
			   SpinnerModel model;
			   if(foodQty < 50){
				   model  = new SpinnerNumberModel(foodQty, 0, 50, 1);     
			   }
			   else {
				   model  = new SpinnerNumberModel(foodQty, 0, foodQty+10, 1);     
			   }
			   foodQtySpinner = new JSpinner(model);
			   foodQtySpinner.setFont(foodQtySpinner.getFont().deriveFont(12.0f));
			   editMenuItems.add(foodQtySpinner);
			   properties.add(editMenuItems);
			   
				label = new JLabel("Balance:");
				label.setFont(label.getFont().deriveFont(12.0f));
				editMenuItems.add(label);
			    model = new SpinnerNumberModel(100.00, 0.00, 999.99, 5);     
			    restBalSpinner = new JSpinner(model);
			    restBalSpinner.setFont(restBalSpinner.getFont().deriveFont(12.0f));
				JSpinner.NumberEditor editor = (JSpinner.NumberEditor)restBalSpinner.getEditor();  
			    DecimalFormat format = editor.getFormat();  
			    format.setMinimumFractionDigits(2);  
			    editor.getTextField().setHorizontalAlignment(SwingConstants.CENTER);  
		        Dimension d = restBalSpinner.getPreferredSize();  
		        d.width = 80;  
		        restBalSpinner.setPreferredSize(d);
		        editMenuItems.add(restBalSpinner);
				
		        setFoodQtyAndBalance = new JButton("Set");
				setFoodQtyAndBalance.setFont(setFoodQtyAndBalance.getFont().deriveFont(12.0f));
				setFoodQtyAndBalance.addActionListener(this);
				editMenuItems.add(setFoodQtyAndBalance);
				
				   swapRestWorkers = new JPanel();
				   swapRestWorkers.setLayout(new FlowLayout());
				   restWorkersList = new JComboBox(restWorkers);
				   restWorkersList.setFont(restWorkersList.getFont().deriveFont(12.0f));
				   
				   if(restWorkersList.getItemCount() != 0){
				       restWorkersList.setSelectedIndex(selectedRestWorkerIndex);
				       
					   if(selectedRestWorker.equals("")){
						   selectedRestWorker = restWorkersList.getSelectedItem().toString();
					   }
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
				       
					   if(selectedPersonForRest.equals("")){
						   selectedPersonForRest = peopleForRestList.getSelectedItem().toString();
					   }
				   }
				   
				   peopleForRestList.addActionListener(this);
				   swapRestWorkers.add(peopleForRestList);
				   swapRestJobs = new JButton("Swap");
				   swapRestJobs.setFont(swapRestJobs.getFont().deriveFont(12.0f));
				   swapRestJobs.addActionListener(this);
				   swapRestWorkers.add(swapRestJobs);
				   properties.add(swapRestWorkers);

		}
		
		add(properties);
		
	    setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
	}

	public void clear(){
		properties.remove(inventory);
		properties.remove(editInventory);
		properties.remove(swapMktWorkers);
		properties.remove(swapTellerWorkers);
		properties.remove(setHandleThief);
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
	JComboBox cb = null;
	JButton b = null;
	JCheckBox closePlace = null;
	String name = "";
	if(e.getSource() instanceof JComboBox){
		 cb = (JComboBox)e.getSource();
		 name = (String)cb.getSelectedItem();
	}
	else if(e.getSource() instanceof JButton){
		 b = (JButton)e.getSource();
	}
	else if(e.getSource() instanceof JCheckBox){
		 closePlace = (JCheckBox)e.getSource();
	}

	if(b!= null){
	  if(b == setMktQuantity){
		AlertLog.getInstance().logInfo(AlertTag.CITY, "WPP", "Set "+ selectedMktItem +" quantity to "+(Integer)mktSpinner.getValue());
		SimCityGui.mickeysMarket.setInventory(selectedMktItem, (Integer)mktSpinner.getValue());
		updateGui();
	  }
	  else if(b == swapMktJobs){
		ArrayList<PersonAgent> people = SimCityGui.simCityPanel.getPeople();
		String[] parts = selectedMktWorker.split(": ");
		String role = parts[0].toLowerCase();
		String mktWorker = parts[1];
		String workplace = selectedWorkplace.substring(selectedWorkplace.indexOf(' ')+1, selectedWorkplace.length());
		for(PersonAgent person : people){
			if(person.getName().equals(mktWorker)){
				AlertLog.getInstance().logInfo(AlertTag.CITY, "WPP", mktWorker + " should switch role to unemployed");
				person.msgSwitchRole("unemployed", "");
			}
			else if(person.getName().equals(selectedPersonForMkt)){
				AlertLog.getInstance().logInfo(AlertTag.CITY, "WPP", selectedPersonForMkt + " should switch role to "+role+ " at "+workplace);
				person.msgSwitchRole(role, workplace);
			}
		}
	  }
	  else if(b == swapRestJobs){
		ArrayList<PersonAgent> people = SimCityGui.simCityPanel.getPeople();
		String[] parts = selectedRestWorker.split(": ");
		String role = parts[0].toLowerCase();
		String restWorker = parts[1];
		String workplace = selectedWorkplace.substring(selectedWorkplace.indexOf(' ')+1, selectedWorkplace.length());
		for(PersonAgent person : people){
			if(person.getName().equals(restWorker)){
				AlertLog.getInstance().logInfo(AlertTag.CITY, "WPP", restWorker + " should switch role to unemployed");
				person.msgSwitchRole("unemployed", "");
			}
			else if(person.getName().equals(selectedPersonForRest)){
				AlertLog.getInstance().logInfo(AlertTag.CITY, "WPP", selectedPersonForRest + " should switch role to "+role+ " at "+workplace);
				person.msgSwitchRole(role, workplace);
			}
		}
	  }
	  else if(b == swapTellersJobs){
		ArrayList<PersonAgent> people = SimCityGui.simCityPanel.getPeople();
		String role = "Teller";
		String workplace = selectedWorkplace.substring(selectedWorkplace.indexOf(' ')+1, selectedWorkplace.length());
		for(PersonAgent person : people){
			if(person.getName().equals(selectedTeller)){
				AlertLog.getInstance().logInfo(AlertTag.CITY, "WPP", selectedTeller + " should switch role to unemployed");
				person.msgSwitchRole("unemployed", "");
			}
			else if(person.getName().equals(selectedPersonForRest)){
				AlertLog.getInstance().logInfo(AlertTag.CITY, "WPP", selectedPersonForRest + " should switch role to "+role+ " at "+workplace);
				person.msgSwitchRole(role, workplace);
			}
		}
	  }
	  else if(b == changeRobberyState){
		if (selectedWorkplace.contains("Pirate")){
			if(success.isSelected()){
				SimCityGui.pirateBank.setRobberySuccess("true");
			}
			else if(fail.isSelected()){
				SimCityGui.pirateBank.setRobberySuccess("false");
			}
			else {
				SimCityGui.pirateBank.setRobberySuccess("random");
			}
		}
	  }
	  else if(b == setFoodQtyAndBalance){
		AlertLog.getInstance().logInfo(AlertTag.CITY, "WPP", "Set "+ selectedMenuItem +" quantity to "+(Integer)foodQtySpinner.getValue()+ " and the balance to "+(Double)restBalSpinner.getValue());
		if (selectedWorkplace.contains("Bayou")){
			SimCityGui.restBayou.setQuantityAndBalance(selectedMenuItem, (Integer)foodQtySpinner.getValue(), (Double)restBalSpinner.getValue());
		}
		else if (selectedWorkplace.contains("Cafe")){
			SimCityGui.restCafe.setQuantityAndBalance(selectedMenuItem, (Integer)foodQtySpinner.getValue(), (Double)restBalSpinner.getValue());
		}
		else if (selectedWorkplace.contains("Haus")){
			SimCityGui.restHaus.setQuantityAndBalance(selectedMenuItem, (Integer)foodQtySpinner.getValue(), (Double)restBalSpinner.getValue());
		}
		else if (selectedWorkplace.contains("Pizza")){
			SimCityGui.restPizza.setQuantityAndBalance(selectedMenuItem, (Integer)foodQtySpinner.getValue(), (Double)restBalSpinner.getValue());	
		}
		else if (selectedWorkplace.contains("Rancho")){
			SimCityGui.restRancho.setQuantityAndBalance(selectedMenuItem, (Integer)foodQtySpinner.getValue(), (Double)restBalSpinner.getValue());
		}
	  }
	}
    if(cb != null){
    if(cb == workplaceList){
	     for(int i = 0; i<workplaces.length; i++){
		     if(workplaces[i].equals(name)){
		    	 setType(name);
			     selectedWorkplace = name;
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
    else if(cb == tellersList){
	     for(int i = 0; i<tellerWorkers.length; i++){
		     if(tellerWorkers[i].equals(name)){
			     selectedTeller = name;
			     selectedTellerIndex = i;
			     updateGui();
			     break;
		     }
	     }
    }
   else if(cb == peopleForTellersList){
	     for(int i = 0; i<peopleForTellers.length; i++){
		     if(peopleForTellers[i].equals(name)){
			     selectedPersonForTeller = name;
			     selectedPersonForTellerIndex = i;
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
		    	 selectedMenuItem = name;
			     selectedMenuItemIndex = i;
			     updateGui();
			     break;
		     }
	     }
    }
    }
    if(closePlace!=null){
    	if(closePlace.isSelected()){
    		String workplace = selectedWorkplace.substring(selectedWorkplace.indexOf(' ')+1, selectedWorkplace.length());
    		if(workplace.contains("Mickey") && SimCityGui.mickeysMarket.isOpen()){
    			SimCityGui.mickeysMarket.endOfShift();
    		}
    		else if(workplace.contains("Cafe") && SimCityGui.restCafe.isOpen()){
    			SimCityGui.restCafe.endOfShift();
    		}
    		else if(workplace.contains("Rancho") && SimCityGui.restRancho.isOpen()){
    			SimCityGui.restRancho.endOfShift();
    		}
    		else if(workplace.contains("Pirate") && SimCityGui.pirateBank.isOpen()){
    			SimCityGui.pirateBank.msgClose();
    		}
      		else if(workplace.contains("Haus") && SimCityGui.restHaus.isOpen()){
    			SimCityGui.restHaus.endOfShift();
    		}
      		else if(workplace.contains("Pizza") && SimCityGui.restPizza.isOpen()){
    			SimCityGui.restPizza.endOfShift();
    		}
    	}
    }
    
	}

}
