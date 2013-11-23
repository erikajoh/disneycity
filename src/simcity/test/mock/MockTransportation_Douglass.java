package simcity.test.mock;

import simcity.PersonAgent;
import simcity.interfaces.Transportation_Douglass;

public class MockTransportation_Douglass extends Mock_Douglass implements Transportation_Douglass { //implements Bank {

	public EventLog log;
	
	public MockTransportation_Douglass(String name) {
		super(name);
		log = new EventLog();
	}

	/*
	@Override
	public void msgHereIsBill(double amount, boolean lastBillFulfilled) {
		if(lastBillFulfilled) {
			log.add(new LoggedEvent("msgHereIsBill from cashier: valid payment"));
		}
		else {
			log.add(new LoggedEvent("msgHereIsBill from cashier: invalid payment"));
		}
	}
	*/
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void msgWantToGo(String startLocation, String endLocation, PersonAgent person, String method) {
		log.add(new LoggedEvent("Received msgGoTo: "
				+ "startLocation = " + startLocation + "; "
				+ "endLocation = " + endLocation + "; "
				+ "person = " + person.getName() + "; "
				+ "method = " + method));
		person.msgReachedDestination(endLocation);
	}
	
	@Override
	public void msgPayFare(PersonAgent p, double money) {
		
	}
}
