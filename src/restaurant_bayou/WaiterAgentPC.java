package restaurant_bayou;

import java.util.ArrayList;

import restaurant_bayou.HostAgent.Table;
import restaurant_bayou.ProducerConsumerMonitor;
import restaurant_bayou.ProducerConsumerMonitor.Order;
import restaurant_bayou.WaiterAgent;
import restaurant_bayou.gui.RestaurantBayou;

public class WaiterAgentPC extends WaiterAgent {
	
	public WaiterAgentPC(String name, RestaurantBayou rest) {
		super(name, rest);
	}
	
	protected void dealWithOrder(CustomerAgent c, Table t) {
		waiterGui.DoGoGetFood();
		try{
			atTable.acquire();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		print("Adding order to revolving stand");
		restaurant.orderStand.insert(this, myChoices.get(c), t.num);
		waiterGui.DoLeaveCustomer();
	}
	
	

}
