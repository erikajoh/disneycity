package housing;

import housing.interfaces.Person;

import java.util.HashMap;

public class Housing {

	HashMap<Person, OwnerAgent> owners = new HashMap<Person, OwnerAgent>();
	HashMap<Person, RenterAgent> renters = new HashMap<Person, RenterAgent>();
	HashMap<RenterAgent, OwnerAgent> ownerRenterPairs = new HashMap<RenterAgent, OwnerAgent>();
	
	//Messages
	
	public void msgIAmHome(Person rp) { // from person
		// r.msgHome();
	}
	
	public void msgPrepareToCookAtHome(Person rp, String choice) { // from person
		// r.msgCookFood(String choice);
	}
	
	public void msgHereIsRent(Person rp, double amt) { // from person
		// op.msgHereIsRent(double amt);
	}
		
	public void msgGoToBed(Person rp) { // from person
		// r.msgToBed();
	}
	
	public void msgIAmLeaving(Person rp) { // from person
		// r.msgLeave();
	}
	
	public void msgHereIsTime() { // from timer
		// r.msgDoMaintenance();
		// rp.msgRentIsDue();
	}
	
	public void msgFinishedMaintenance() { // from renter
		// rp.msgFinishedMaintenance();
	}
	
	public void msgFoodDone() { // from renter
		// rp.msgFoodDone();
	}
	
}
