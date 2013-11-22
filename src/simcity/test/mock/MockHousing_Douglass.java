package simcity.test.mock;

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
}
