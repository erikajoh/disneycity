package simcity.gui;

import agent_rancho.Agent;
import restaurant_rancho.gui.RestaurantRancho;
import restaurant_rancho.gui.RestaurantRanchoGui;
import simcity.PersonAgent;
import simcity.interfaces.Housing_Douglass;
import simcity.interfaces.Transportation_Douglass;
//import simcity.test.mock.MockHousing_Douglass;
import simcity.test.mock.MockTransportation_Douglass;
import housing.Housing;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class SimCityPanel extends JPanel{
	
	SimCityGui gui = null;
	
	 RestaurantRanchoGui ranchoGui;
	 RestaurantRancho ranchoRest;
	 	 
	 ArrayList<PersonAgent> people = new ArrayList<PersonAgent>();
	 ArrayList<Housing> housings = new ArrayList<Housing>();
	 
	 Transportation_Douglass transportation = new MockTransportation_Douglass("Mock Transportation");
	 
	 private JPanel group = new JPanel();
	 
	 public SimCityPanel(SimCityGui gui) {
		 this.gui = gui;
//		 ranchoGui = new RestaurantRanchoGui("Rancho de Zocales");
//		 ranchoRest = ranchoGui.getRestaurant();
		 
		 // TODO: No mocks in the end
		 //Housing firstHousing = new Housing("Haunted Mansion", firstHousingGui);
		 Housing firstHousing = new Housing(gui, "Haunted Mansion");

		 PersonAgent firstHackedPerson = new PersonAgent("Narwhal Prime", firstHousing, "OwnerResident", transportation);
		 people.add(firstHackedPerson);
		 firstHousing.addRenter(firstHackedPerson);
		 
		 firstHackedPerson.startThread();
		 

	     setLayout(new GridLayout());
	 }

}
