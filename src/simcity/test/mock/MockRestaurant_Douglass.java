package simcity.test.mock;

import java.util.*;

import simcity.PersonAgent;
import simcity.interfaces.*;

public class MockRestaurant_Douglass extends Mock_Douglass implements Restaurant {

	public EventLog log;
	
	public String type;
	public Map<String, Double> menu;
	public List<MyCustomer> waitingCustomers;
	
	public MockRestaurant_Douglass(String name, String type, Map<String, Double> menu) {
		super(name);
		this.type = type;
		log = new EventLog();
		this.menu = menu;
		waitingCustomers = new ArrayList<MyCustomer>();
	}

	@Override
	public String getName() { return name; }
	
	@Override
	public String getType() { return type; }

	@Override
	public Map<String, Double> getMenu() { return menu; }

	@Override
	public void msgPersonAs(PersonAgent personAgent, String personType,
			String name, double moneyOnHand, String foodPreference) {
		log.add(new LoggedEvent("Received msgPersonAs: "
				+ "name = " + name + "; "
				+ "money = " + moneyOnHand));
		if(personType.equals("Customer")) {
			MyCustomer newCustomer = new MyCustomer(name, moneyOnHand, foodPreference);
			waitingCustomers.add(newCustomer);
		}
		// TODO msgPersonAs in MockRestaurant
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
	class MyCustomer {
		String name;
		double moneyOnHand;
		String foodPreference;
		public MyCustomer(String aName, double money, String food) {
			name = aName;
			moneyOnHand = money;
			foodPreference = food;
		}
	}
}
