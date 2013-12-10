package restaurant_haus;

import restaurant_haus.WaiterAgent.CustomerState;
import restaurant_haus.WaiterAgent.MyCustomer;

public class PCWaiterAgent extends WaiterAgent {
	OrderStand orderStand;
	public PCWaiterAgent(String name, OrderStand orderStand) {
		super(name);
		this.orderStand = orderStand;
	}
	@Override
	public void PlaceOrder(MyCustomer mc) {
		waiterGui.GoToStand();
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		orderStand.addOrder(this, mc.choice, mc.table);
		cook.msgCheckStand();
		print(cook.getName() + ", check the stand for orders.");
		mc.s = CustomerState.Seated;
	}
	// TODO Auto-generated constructor stub
}
