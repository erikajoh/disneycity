package restaurant_pizza;

public class StandOrder {
	WaiterAgent waiter;
	int tableNum;
	String order;
	public StandOrder(WaiterAgent aWaiter, int aTableNum, String aOrder) {
		waiter = aWaiter;
		tableNum = aTableNum;
		order = aOrder;
 	}
}
