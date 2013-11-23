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
		 
		 // TODO: No mocks in the end
		 Housing firstHousing = new Housing(gui, "Haunted Mansion");

		 PersonAgent firstHackedPerson = new PersonAgent("Narwhal Prime", firstHousing, "Owner", transportation);
		 people.add(firstHackedPerson);
		 firstHousing.setOwner(firstHackedPerson);

		 // Alternatively, you can call the next line as a hack (in place of the previous three lines)
//		 firstHousing.setOwner();
		 
		 PersonAgent secondHackedPerson = new PersonAgent("Narwhal Secondary", firstHousing, "Renter", transportation);
		 people.add(secondHackedPerson);
		 firstHousing.addRenter(secondHackedPerson);
		 
		 firstHackedPerson.startThread();
		 secondHackedPerson.startThread();

	     setLayout(new GridLayout());
	 }

}
