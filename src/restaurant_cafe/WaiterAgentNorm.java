package restaurant_cafe;

import restaurant_cafe.WaiterAgent;
import restaurant_cafe.gui.Menu;
import restaurant_cafe.gui.RestaurantCafe;
import simcity.gui.trace.AlertLog;
import simcity.gui.trace.AlertTag;

public class WaiterAgentNorm extends WaiterAgent {
	
	public WaiterAgentNorm(String name, RestaurantCafe rest, Menu menu, int num) {
		super(name, rest, menu, num);
	}
	
	protected void giveOrderToCook(MyCustomer customer){
	    AlertLog.getInstance().logInfo(AlertTag.RESTAURANT, "CAFE", "giveOrder called from WaiterAgentNorm");
		cook.msgHereIsOrder(this, customer.choice, customer.table.tableNumber);
		customer.state = CustomerState.idle;
	}
}