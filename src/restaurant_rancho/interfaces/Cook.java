package restaurant_rancho.interfaces;

import restaurant_rancho.Order;

public interface Cook {
	

	public void msgAddOrder(Order o);
	
	public void msgHereIsOrder(String choice, int amount, int id);
}
