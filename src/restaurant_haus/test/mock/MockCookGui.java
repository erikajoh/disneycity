package restaurant_haus.test.mock;

import restaurant_haus.CookAgent;
import restaurant_haus.gui.CookGui;
import simcity.gui.SimCityGui;

public class MockCookGui extends CookGui {

	public MockCookGui(CookAgent agent, SimCityGui g) {
		super(agent, g);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void goToStand() {
		agent.msgAtDestination();
	}
}
