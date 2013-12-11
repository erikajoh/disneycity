package restaurant_haus;

import java.util.ArrayList;
import java.util.List;

public class OrderStand {
	List<Order> orders;
	
	synchronized public void addOrder(WaiterAgent w, String choice, int table) {
		orders.add(new Order(w, choice, table));
	}
	
	synchronized public List<Order> getOrders() {
		List<Order> tempList = orders;
		orders = new ArrayList<Order>();
		return tempList;
	}
	
	public OrderStand() {
		orders =  new ArrayList<Order>();
	}

	public int getSize() {
		return orders.size();
	}
}
