package simcity.gui;
import agent_rancho.Agent;
import restaurant_rancho.gui.RestaurantRancho;
import restaurant_rancho.gui.RestaurantRanchoGui;
import simcity.PersonAgent;
import simcity.interfaces.Housing;
import simcity.interfaces.Transportation;
import simcity.test.mock.MockHousing_Douglass;
import simcity.test.mock.MockTransportation_Douglass;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class SimCityPanel extends JPanel{
	
	 RestaurantRanchoGui ranchoGui;
	 RestaurantRancho ranchoRest;
	 
	 ArrayList<Housing> housings = new ArrayList<Housing>();
	 
	 ArrayList<PersonAgent> people = new ArrayList<PersonAgent>();
	 
	 Transportation transportation = new MockTransportation_Douglass("Mock Transportation");
	 
	 private JPanel group = new JPanel();
	 
	 public SimCityPanel() {
		 ranchoGui = new RestaurantRanchoGui("Rancho de Zocales");
		 ranchoRest = ranchoGui.getRestaurant();
		 
		 // TODO: No mocks in the end
		 MockHousing_Douglass firstHousing = new MockHousing_Douglass("Haunted Mansion"); 
		 housings.add(firstHousing);
		 
		 PersonAgent firstHackedPerson = new PersonAgent("Narwhal Prime", firstHousing, "Ow	nerResident", transportation);
		 people.add(firstHackedPerson);
		 
		 
	     
	     setLayout(new GridLayout());
	 }

}
