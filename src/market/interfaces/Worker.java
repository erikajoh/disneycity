package market.interfaces;

import market.CustomerAgent;

public interface Worker {
	public void msgAnimationDeliveredFinished();
	
	public void msgAnimationFinished();
	
	public void msgGoGetItem(CustomerAgent cust, String c, int quantity, boolean virtual);
}
