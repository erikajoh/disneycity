package restaurant_haus.test.mock;

import restaurant_haus.WaiterAgent;
import restaurant_haus.gui.WaiterGui;
import simcity.gui.SimCityGui;

public class MockWaiterGui extends WaiterGui {

	public MockWaiterGui(WaiterAgent agent, SimCityGui g) {
		super(agent, g);
	}
	
	@Override
	public void GoToCook() {
    	agent.msgAtDestination();
    }
	
	@Override
	public void GoToOrder() {
    	agent.msgAtDestination();
    }
	
	@Override
	public void GoToStand() {
		agent.msgAtDestination();
	}

}
