package simcity.test.mock;

import housing.ResidentAgent;

import java.util.Map;

import simcity.PersonAgent;
import simcity.interfaces.Housing_Douglass;

public class MockHousing_Douglass extends Mock_Douglass implements Housing_Douglass {

	public EventLog log;
	
	public String type;
	
	public MockHousing_Douglass(String name) {
		super(name);
		log = new EventLog();
	}
	
	@Override
	public String getName() { return name; }

	@Override
	public void msgHereIsRent(PersonAgent personAgent, double amount) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void msgIAmLeaving(PersonAgent personAgent) {
	
	}

	@Override
	public void msgGoToBed(PersonAgent personAgent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgPrepareToCookAtHome(PersonAgent personAgent,
			String foodPreference) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgIAmHome(PersonAgent rp, Map<String, Integer> items) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgDoMaintenance(PersonAgent rp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgRentDue() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgEntered(ResidentAgent ra) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgFinishedMaintenance(ResidentAgent ra) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgFoodDone(ResidentAgent ra, boolean success) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgLeft(ResidentAgent ra) {
		// TODO Auto-generated method stub
		
	}
}
