package simcity.test.mock;

import java.util.Map;

import simcity.interfaces.*;

public class MockRestaurant_Douglass extends Mock_Douglass implements Restaurant {

	public EventLog log;
	
	public String type;
	public Map<String, Double> menu;
	
	public MockRestaurant_Douglass(String name, String type, Map<String, Double> menu) {
		super(name);
		this.type = type;
		log = new EventLog();
		this.menu = menu;
	}

	@Override
	public String getName() { return name; }
	
	@Override
	public String getType() { return type; }

	@Override
	public Map<String, Double> getMenu() { return menu; }
	
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
	
}
