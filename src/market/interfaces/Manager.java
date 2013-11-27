package market.interfaces;

import market.CustomerAgent;

public interface Manager {
	public void msgWantToOrder(Customer c, String choice, int quantity, boolean virtual);
}
