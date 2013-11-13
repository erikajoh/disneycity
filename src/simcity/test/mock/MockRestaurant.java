package simcity.test.mock;

public class MockRestaurant extends Mock { //implements Bank {

	public EventLog log;
	
	public MockRestaurant(String name) {
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
