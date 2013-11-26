package restaurant_rancho;

import java.util.ArrayList;
import restaurant_rancho.ProducerConsumerMonitor;

import restaurant_rancho.WaiterAgent.MyCustomer;
import restaurant_rancho.gui.RestaurantRancho;

public class WaiterAgentPC extends WaiterAgent {
	
	public WaiterAgentPC(String name, RestaurantRancho rest) {
		super(name, rest);
	}
	
	protected void dealWithOrder(MyCustomer c) {
		c.cs = customerState.waitingForFood;
		waiterGui.DoGoToCook();
		try{
			atTable.acquire();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		print("Adding order to revolving stand");
		restaurant.orderStand.insert(c.order);
		DoLeaveCustomer();
		stateChanged();
		
	}
	
	

}
