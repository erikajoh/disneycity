package simcity.test.mock;

import housing.ResidentAgent;

import java.util.Map;

import market.CustomerAgent;
import simcity.PersonAgent;
import simcity.Restaurant;
import simcity.interfaces.Housing_Douglass;
import simcity.interfaces.Market_Douglass;

public class MockMarket_Douglass extends Mock_Douglass implements Market_Douglass {

	public EventLog log;
	
	public String type;
	
	public MockMarket_Douglass(String name) {
		super(name);
		log = new EventLog();
	}
	
	@Override
	public String getName() { return name; }

	@Override
	public void msgLeaving(CustomerAgent c) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgHereIsPayment(Restaurant rest, double amt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void personAs(Restaurant r, String choice, int quantity, int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void personAs(PersonAgent p, String name, double money,
			String choice, int quantity, String location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void personAs(PersonAgent p, String name, double money,
			String choice, int quantity) {
		// TODO Auto-generated method stub
		
	}

	}
