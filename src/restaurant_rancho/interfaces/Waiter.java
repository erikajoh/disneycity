package restaurant_rancho.interfaces;

import java.util.List;

import restaurant_rancho.Order;
import restaurant_rancho.WaiterAgent.MyCustomer;
import restaurant_rancho.WaiterAgent.customerEvent;
import restaurant_rancho.WaiterAgent.customerState;
import restaurant_rancho.interfaces.Customer;

public interface Waiter {
	public abstract void msgCreateCustomer(Customer c, int t, int l);
	

	public abstract void msgReadyToOrder(Customer c);

	public abstract void msgReadyForCheck(Customer c);
	
	public abstract void msgCheckReady(Customer cust, double amount);
	
	public abstract void msgHereIsMyOrder(Customer c, String choice);
	
	public abstract void msgUpdateMenu();
	
	public abstract void msgOutOfFood(Order o);
	
	public abstract void msgFoodIsReady(Order o);
	
	public abstract void msgLeavingTable(Customer cust);

	public abstract void msgAtTable();

}
