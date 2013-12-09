package restaurant_cafe;

import restaurant_cafe.WaiterAgent;
import restaurant_cafe.gui.Menu;
import restaurant_cafe.gui.RestaurantCafe;

public class WaiterAgentNorm extends WaiterAgent {
	
	public WaiterAgentNorm(String name, RestaurantCafe rest, Menu menu, int num) {
		super(name, rest, menu, num);
	}
	
	protected void giveOrderToCook(MyCustomer customer){
		cook.msgHereIsOrder(this, customer.choice, customer.table.tableNumber);
		customer.state = CustomerState.idle;
	}
}