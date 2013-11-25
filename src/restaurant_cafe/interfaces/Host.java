package restaurant_cafe.interfaces;

import restaurant_cafe.interfaces.Customer;
import restaurant_cafe.interfaces.Waiter;

public interface Host {
	public void msgIWantFood(Customer cust);
	
	public void msgIWantToBreak(Waiter w);
	
	public void msgFinishedBreak(Waiter w);
	
	public void msgTableAvailable(Customer cust);
	
	public void msgCustomerLeaving(Customer cust);

}