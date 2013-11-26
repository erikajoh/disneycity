package restaurant_rancho;

import restaurant_rancho.WaiterAgent.MyCustomer;
import restaurant_rancho.gui.RestaurantRancho;

public class WaiterAgentNorm extends WaiterAgent {
	
		public WaiterAgentNorm(String name, RestaurantRancho rest) {
			super(name, rest);
		}

		protected void dealWithOrder(MyCustomer c) {
			c.cs = customerState.waitingForFood;
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
