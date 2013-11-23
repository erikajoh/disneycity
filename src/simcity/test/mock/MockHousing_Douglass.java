package simcity.test.mock;

import simcity.PersonAgent;
import simcity.interfaces.Housing;

public class MockHousing_Douglass extends Mock_Douglass implements Housing {

	public EventLog log;
	
	public String type;
	
	public MockHousing_Douglass(String name) {
		super(name);
		log = new EventLog();
	}
	
	@Override
	public String getName() { return name; }

	@Override
	public String getType() { return type; }

	@Override
	public void msgHereIsRent(PersonAgent personAgent, double amount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgIAmHome(PersonAgent personAgent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgIAmLeaving(PersonAgent personAgent) {
		// TODO Auto-generated method stub
		
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
}
