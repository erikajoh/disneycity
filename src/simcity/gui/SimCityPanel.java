package simcity.gui;

import restaurant_rancho.gui.RestaurantRancho;
import simcity.PersonAgent;
import simcity.interfaces.Transportation_Douglass;
import simcity.test.mock.MockTransportation_Douglass;
import housing.Housing;

import javax.swing.*;

import java.awt.*;
import java.util.ArrayList;

public class SimCityPanel extends JPanel{
	
	SimCityGui gui = null;
	RestaurantRancho restRancho;
	 	 
	 ArrayList<PersonAgent> people = new ArrayList<PersonAgent>();
	 ArrayList<Housing> housings = new ArrayList<Housing>();
	 
	 Transportation_Douglass transportation = new MockTransportation_Douglass("Mock Transportation");
	 
	 private JPanel group = new JPanel();
	 
	 public SimCityPanel(SimCityGui gui) {
		 this.gui = gui;
		 restRancho = gui.restRancho;
		 
		 Housing firstHousing = new Housing(gui, "Haunted Mansion");
		 String foodPreferenceMexican = "Mexican";
		 
		 // All PersonAgents are instantiated here. Upon instantiation, we must pass
		 // all pointers to all things (restaurants, markets, housings, banks) to the person as follows:
		 PersonAgent firstHackedPerson = new PersonAgent("Narwhal Prime", firstHousing, foodPreferenceMexican, "OwnerResident", transportation);
		 firstHousing.setOwner(firstHackedPerson);
		 firstHousing.addRenter(firstHackedPerson);
		 firstHackedPerson.addRestaurant(restRancho, "Customer");
		 people.add(firstHackedPerson);

		 // Alternatively, you can call the next line as a hack (in place of the previous three lines)
//		 firstHousing.setOwner();
		 
		 // These are two different people; in our first test it seemed that both renter and owner entered the house.
		 // The owner does not necessarily ever have to enter the house he owns.
		 /*
		 PersonAgent secondHackedPerson = new PersonAgent("Narwhal Secondary", firstHousing, "Renter", transportation);
		 people.add(secondHackedPerson);
		 firstHousing.addRenter(secondHackedPerson);
		 secondHackedPerson.addHousing(firstHousing, "Renter");
		 
		 secondHackedPerson.startThread();
		 */
		 
		 firstHackedPerson.startThread();

	     setLayout(new GridLayout());
	 }

}
