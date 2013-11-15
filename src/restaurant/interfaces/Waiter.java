package restaurant.interfaces;

import java.util.List;

import restaurant.interfaces.Customer;
import restaurant.Order;
import restaurant.WaiterAgent.MyCustomer;
import restaurant.WaiterAgent.customerEvent;
import restaurant.WaiterAgent.customerState;

public interface Waiter {
	public abstract void msgCreateCustomer(Customer c, int t, int l);
	

	public abstract void msgReadyToOrder(Customer c);

	public abstract void msgReadyForCheck(Customer c);
	
	public abstract void msgCheckReady(Customer cust, double amount);
	
	public abstract void msgHereIsMyOrder(Customer c, String choice);
	
	public abstract void msgUpdateMenu(List<String> foodsUnavailable);
	
	public abstract void msgOutOfFood(Order o);
	
	public abstract void msgFoodIsReady(Order o);
	
	public abstract void msgLeavingTable(Customer cust);

	public abstract void msgAtTable();

}
