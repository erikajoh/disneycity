package simcity.test.mock;

import simcity.PersonAgent;
import simcity.interfaces.Transportation;

public class MockTransportation extends Mock implements Transportation { //implements Bank {

	public EventLog log;
	
	public MockTransportation(String name) {
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
	
	public void msgPersonAs(String startLocation, String endLocation, PersonAgent person, String method) {
		
	}
	
	public void msgPayFare(PersonAgent p, double money) {
		
	}
}
