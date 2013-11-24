package housing;

import housing.gui.HousingAnimationPanel;

import java.util.ArrayList;

import simcity.PersonAgent;
import simcity.gui.SimCityGui;

public class Housing {
	
	String name, type;
	double rentAmt = 5;
	
	PersonAgent ownerPerson;
	ArrayList<Renter> renters = new ArrayList<Renter>();
	SimCityGui gui;
		
	public Housing(SimCityGui g, String n) {
    	gui = g;
    	name = n;
    	if (name == "Haunted Mansion") type = "house";
    	else if (name == "Main St Apartment") type = "apt";
    }
	
	public String getName() {
		return name;
	}
	
	public void setOwner(PersonAgent op) {
		ownerPerson = op;
	}
	
	public void addRenter() { //hack
		ResidentAgent r = new ResidentAgent("r"+renters.size()+1, type);
		r.setHousing(this);
		gui.housAniPanel.addRenter(r);
	}
	
	public void addRenter(PersonAgent rp) {
		ResidentAgent r = new ResidentAgent("r"+renters.size()+1, type);
		r.setHousing(this);
		renters.add(new Renter(r, rp));
		gui.housAniPanel.addRenter(r);
	}
	
	class Renter {
		ResidentAgent agent;
		PersonAgent person;
		Renter(ResidentAgent a, PersonAgent p) { agent = a; person = p; }
	}
	
	//Messages
	
	public void msgIAmHome(PersonAgent rp) { // from person
		for (Renter r: renters) {
			if (r.person == rp) r.agent.msgHome();
		}
	}
	
	public void msgPrepareToCookAtHome(PersonAgent rp, String choice) { // from person
		for (Renter r: renters) {
			if (r.person == rp) r.agent.msgCookFood(choice);
		}		
	}
	
	public void msgHereIsRent(PersonAgent rp, double amt) { // from person
		ownerPerson.msgHereIsRent(amt);
	}
		
	public void msgGoToBed(PersonAgent rp) { // from person
		for (Renter r: renters) {
			if (r.person == rp) r.agent.msgToBed();
		}	
	}
	
	public void msgIAmLeaving(PersonAgent rp) { // from person
		for (Renter r: renters) {
			if (r.person == rp) r.agent.msgLeave();
		}
	}
	
	public void msgDoMaintenance() { // from timer
		for (Renter r: renters) {
			r.agent.msgDoMaintenance();
		}
	}
	
	public void msgRentDue() { // from timer
		for (Renter r: renters) {
			r.person.msgRentIsDue(rentAmt);
		}
	}
	
	public void msgEntered(ResidentAgent ra) { // from renter
		for (Renter r: renters) {
			if (r.agent == ra) r.person.msgDoneEntering();
		} 
	}
	
	public void msgFinishedMaintenance(ResidentAgent ra) { // from renter
		for (Renter r: renters) {
			if (r.agent == ra) r.person.msgFinishedMaintenance();
		}  
	}
	
	public void msgFoodDone(ResidentAgent ra, boolean success) { // from renter
		for (Renter r: renters) {
			if (r.agent == ra) r.person.msgFoodDone(success);
		}  
	}
	
	public void msgLeft(ResidentAgent ra) { // from renter
		for (Renter r: renters) {
			if (r.agent == ra) r.person.msgDoneLeaving();
		} 
	}
	
}
