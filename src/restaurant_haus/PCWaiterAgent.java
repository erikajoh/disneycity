package restaurant_haus;

import restaurant_haus.WaiterAgent.CustomerState;
import restaurant_haus.WaiterAgent.MyCustomer;
import restaurant_haus.test.mock.LoggedEvent;

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
			e.printStackTrace();
		}
		orderStand.addOrder(this, mc.choice, mc.table);
		cook.msgCheckStand();
		print(cook.getName() + ", check the stand for orders.");
		mc.s = CustomerState.Seated;
		log.add(new LoggedEvent("PC order added to stand"));
	}
}
