package housing;

import java.util.ArrayList;

import simcity.PersonAgent;
import simcity.gui.SimCityGui;

public class Housing {
	
	String name;
	PersonAgent ownerPerson;
	OwnerAgent owner;
	ArrayList<Renter> renters = new ArrayList<Renter>();
	SimCityGui gui;
	
	public Housing(SimCityGui g, String n) {
    	gui = g;
    	name = n;
    }
	
	public String getName() {
		return name;
	}
	
	public void setOwner() { //hack
		owner = new OwnerAgent("owner");
		gui.housAniPanel.setOwner(owner);
	}
	
	public void setOwner(PersonAgent op) {
		ownerPerson = op;
		owner = new OwnerAgent("owner");
		gui.housAniPanel.setOwner(owner);
	}
	
	public void addRenter() { //hack
		RenterAgent r = new RenterAgent("r"+renters.size()+1);
		r.setHousing(this);
		r.setOwner(owner);
		gui.housAniPanel.addRenter(r);
	}
	
	public void addRenter(PersonAgent rp) {
		RenterAgent r = new RenterAgent("r"+renters.size()+1);
		r.setHousing(this);
		r.setOwner(owner);
		renters.add(new Renter(r, rp));
		gui.housAniPanel.addRenter(r);
	}
	
	class Renter {
		RenterAgent agent;
		PersonAgent person;
		Renter(RenterAgent a, PersonAgent p) { agent = a; person = p; }
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
	
	public void msgHereIsTime() { // from timer
//		rp.msgDoMaintenance();
//		rp.msgRentIsDue();
	}
	
	public void msgEntered(RenterAgent ra) { // from renter
		for (Renter r: renters) {
			if (r.agent == ra) r.person.msgDoneEntering();
		} 
	}
	
	public void msgFinishedMaintenance(RenterAgent ra) { // from renter
		for (Renter r: renters) {
			if (r.agent == ra) r.person.msgFinishedMaintenance();
		}  
	}
	
	public void msgFoodDone(RenterAgent ra) { // from renter
		for (Renter r: renters) {
			if (r.agent == ra) r.person.msgFoodDone();
		}  
	}
	
	public void msgLeft(RenterAgent ra) { // from renter
		for (Renter r: renters) {
			if (r.agent == ra) r.person.msgDoneLeaving();
		} 
	}
	
}