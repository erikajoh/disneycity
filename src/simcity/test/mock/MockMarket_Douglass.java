package simcity.test.mock;

import housing.ResidentAgent;

import java.util.Map;

import simcity.PersonAgent;
import simcity.interfaces.Housing_Douglass;

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
	public void msgHereIsRent(PersonAgent personAgent, double amount) {
		// TODO Auto-generated method stub
		
	}

	}
