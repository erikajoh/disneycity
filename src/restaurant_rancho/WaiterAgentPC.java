package restaurant_rancho;

import java.util.ArrayList;

import restaurant_rancho.ProducerConsumerMonitor;
import restaurant_rancho.WaiterAgent.MyCustomer;
import restaurant_rancho.gui.RestaurantRancho;
import simcity.gui.trace.AlertLog;
import simcity.gui.trace.AlertTag;

public class WaiterAgentPC extends WaiterAgent {
	
	public WaiterAgentPC(String name, RestaurantRancho rest) {
		super(name, rest);
	}
	
	protected void dealWithOrder(MyCustomer c) {
		AlertLog.getInstance().logMessage(AlertTag.RESTAURANT, name, "Adding order for cook to revolving stand");
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
