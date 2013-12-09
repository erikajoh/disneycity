package restaurant_bayou;

import restaurant_bayou.HostAgent.Table;
import restaurant_bayou.WaiterAgent;
import restaurant_bayou.gui.RestaurantBayou;

public class WaiterAgentNorm extends WaiterAgent {
	
		public WaiterAgentNorm(String name, RestaurantBayou rest) {
			super(name, rest);
		}

		protected void dealWithOrder(CustomerAgent c, Table t) {
			print("dealWithOrder called");
			waiterGui.DoGoGetFood();
			try{ 
				atTable.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			print("Telling cook to make " + c.choice + " for " + c.getName());
			cook.msgHereIsOrder(this, myChoices.get(c), t.num);
			waiterGui.DoLeaveCustomer();
		}
}
