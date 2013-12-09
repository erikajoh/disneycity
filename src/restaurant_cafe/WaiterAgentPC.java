package restaurant_cafe;

import restaurant_cafe.WaiterAgent;
import restaurant_cafe.WaiterAgent.CustomerState;
import restaurant_cafe.WaiterAgent.MyCustomer;
import restaurant_cafe.gui.Menu;
import restaurant_cafe.gui.Order;
import restaurant_cafe.gui.RestaurantCafe;

public class WaiterAgentPC extends WaiterAgent {
	
	public WaiterAgentPC(String name, RestaurantCafe rest, Menu menu, int num) {
		super(name, rest, menu, num);
	}
	
	protected void giveOrderToCook(MyCustomer customer){
		restaurant.orderStand.insert(new Order(this, customer.choice, customer.table.tableNumber));
		customer.state = CustomerState.idle;
	}
}
