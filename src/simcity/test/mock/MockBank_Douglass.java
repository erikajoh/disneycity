package simcity.test.mock;

import simcity.interfaces.Bank;

public class MockBank_Douglass extends Mock_Douglass implements Bank {

	public EventLog log;
	
	public MockBank_Douglass(String name) {
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
}
