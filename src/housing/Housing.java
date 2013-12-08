package housing;

import housing.gui.HousingAnimationPanel;
import housing.test.mock.EventLog;
import housing.test.mock.LoggedEvent;

import java.util.ArrayList;
import java.util.Map;

import simcity.PersonAgent;
import simcity.gui.SimCityGui;
import simcity.interfaces.Housing_Douglass;

public class Housing implements Housing_Douglass {
	
	String name, type;
	double rentAmt = 5;
	
	PersonAgent ownerPerson;
	ArrayList<Renter> renters = new ArrayList<Renter>();
	HousingAnimationPanel panel;
	
	public EventLog log = new EventLog();
	
	class Renter {
		ResidentAgent agent;
		PersonAgent person;
		int roomNo;
		Renter(ResidentAgent a, PersonAgent p, int n) { agent = a; person = p; roomNo = n; }
	}
		
	public Housing(HousingAnimationPanel p, String n) {
		panel = p;
		name = n;
    	if (name.contains("Haunted Mansion")) type = "house";
    	else if (name.contains("Main St Apartments")) type = "apt";
    	
    	if (name == "Haunted Mansion") panel.setBackground("res/hauntedmansion.png");
    	if (name == "Main St Apartments #1") panel.setBackground("res/mainstapts1.png");
    	if (name == "Main St Apartments #2") panel.setBackground("res/mainstapts2.png");
    	if (name == "Main St Apartments #3") panel.setBackground("res/mainstapts3.png");
    	if (name == "Main St Apartments #4") panel.setBackground("res/mainstapts4.png");
    	if (name == "Main St Apartments #5") panel.setBackground("res/mainstapts5.png");
    	if (name == "Main St Apartments #6") panel.setBackground("res/mainstapts6.png");
    }
	
	public String getName() {
		return name;
	}
	
	public boolean isApartment() {
		return type.equals("apt");
	}
	
	public int getNumResidents() {
		return renters.size();
	}
	
	public void setOwner(PersonAgent op) {
		ownerPerson = op;
	}
	
	public void addRenter() { //hack
		ResidentAgent r = new ResidentAgent("renter "+renters.size(), type, renters.size());
		r.setHousing(this);
		panel.addRenter(r, type, renters.size());
		renters.add(new Renter(r, null, renters.size()));
	}
	
	public void addRenter(PersonAgent rp) {
		ResidentAgent r = new ResidentAgent("renter "+renters.size(), type, renters.size());
		r.setHousing(this);
		panel.addRenter(r, type, renters.size());
		renters.add(new Renter(r, rp, renters.size()));
	}
	
	//Messages
	
	public void msgIAmHome(PersonAgent rp, Map<String, Integer> items) { // from person
		log.add(new LoggedEvent("Home"));
		for (Renter r: renters) {
//			if (r.person == rp) r.agent.msgHome(items);
		}
	}
	
	public void msgPrepareToCookAtHome(PersonAgent rp, String choice) { // from person
		log.add(new LoggedEvent("Cooking"));
		for (Renter r: renters) {
//			if (r.person == rp) r.agent.msgCookFood(choice);
		}		
	}
	
	public void msgHereIsRent(PersonAgent rp, double amt) { // from person
		log.add(new LoggedEvent("Here is rent"));
//		if (ownerPerson != null) ownerPerson.msgHereIsRent(amt);
	}
		
	public void msgGoToBed(PersonAgent rp) { // from person
		log.add(new LoggedEvent("Go to bed"));
		for (Renter r: renters) {
//			if (r.person == rp) r.agent.msgToBed();
		}	
	}
	
	public void msgIAmLeaving(PersonAgent rp) { // from person
		log.add(new LoggedEvent("Leaving"));
		for (Renter r: renters) {
//			if (r.person == rp) r.agent.msgLeave();
		}
	}
	
	public void msgDoMaintenance() { // from timer
		log.add(new LoggedEvent("Do maintenance"));
		for (Renter r: renters) {
//			r.agent.msgDoMaintenance();
		}
	}
	
	public void msgRentDue() { // from timer
		log.add(new LoggedEvent("Rent is due"));
		for (Renter r: renters) {
//			r.person.msgRentIsDue(rentAmt);
		}
	}
	
	public void msgEntered(ResidentAgent ra) { // from renter
		log.add(new LoggedEvent("Entered"));
		for (Renter r: renters) {
//			if (r.agent == ra) r.person.msgDoneEntering();
		} 
	}
	
	public void msgFinishedMaintenance(ResidentAgent ra) { // from renter
		log.add(new LoggedEvent("Finished maintenance"));
		for (Renter r: renters) {
//			if (r.agent == ra) r.person.msgFinishedMaintenance();
		}  
	}
	
	public void msgFoodDone(ResidentAgent ra, boolean success) { // from renter
		log.add(new LoggedEvent("Food done"));
		for (Renter r: renters) {
//			if (r.agent == ra) r.person.msgFoodDone(success);
		}  
	}
	
	public void msgLeft(ResidentAgent ra) { // from renter
		log.add(new LoggedEvent("Left"));
		for (Renter r: renters) {
//			if (r.agent == ra) r.person.msgDoneLeaving();
		} 
	}
	
}
