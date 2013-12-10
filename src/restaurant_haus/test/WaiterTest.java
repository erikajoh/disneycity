package restaurant_haus.test;

import restaurant_haus.CookAgent;
import restaurant_haus.OrderStand;
import restaurant_haus.PCWaiterAgent;
import restaurant_haus.WaiterAgent;
import junit.framework.TestCase;

public class WaiterTest extends TestCase{
	WaiterAgent normWaiter;
	PCWaiterAgent PCWaiter;
	
	OrderStand orderStand;
	CookAgent cook;
	
	public void setUp() throws Exception{
		super.setUp();
		orderStand = new OrderStand();
		normWaiter = new WaiterAgent("normWaiter");
		PCWaiter = new PCWaiterAgent("PCWaiter", orderStand);
		cook = new CookAgent("cook", null, orderStand);
	}
}
