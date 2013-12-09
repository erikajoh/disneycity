package restaurant_cafe;

import restaurant_cafe.WaiterAgent;
import restaurant_cafe.WaiterAgent.MyCustomer;
import restaurant_cafe.gui.RestaurantCafe;

public class WaiterAgentNorm extends WaiterAgent {
	
	public WaiterAgentNorm(String name, RestaurantCafe rest) {
		super(name, rest);
	}

	protected void dealWithOrder(MyCustomer c) {
		c.state = CustomerState.waitingForFood;
		waiterGui.DoGoToCook();
		try{ 
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		print("Telling cook to make " + c.choice + " for " + c.c.getName());
		cook.msgAddOrder(c.order);
		DoLeaveCustomer();
		stateChanged();
	}
}