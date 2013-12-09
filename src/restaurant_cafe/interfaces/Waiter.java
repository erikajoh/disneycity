package restaurant_cafe.interfaces;

import restaurant_cafe.gui.Check;
import restaurant_cafe.gui.Food;
import restaurant_cafe.interfaces.Customer;

/**
 * A sample Waiter interface built to unit test a CashierAgent.
 *
 * @author Dylan Eirinberg
 *
 */
public interface Waiter {
	public void msgGoOnBreak();
	
	public void msgEndBreak();

	public void msgPleaseSeatCustomer(Customer cust, int tableNum);
	
	public void msgLeaveCustomer();
	
	public void msgOutOfFood(Food f);
	
	public void msgReadyToOrder(Customer cust);
	
	public void msgHereIsMyOrder(Customer cust, String choice);
	
	public void msgOrderDone(String choice, int table);
	
	public void msgDoneEating(Customer cust);
	
	public void msgHereIsCheck(Customer cust, Check check);
	
	public void msgCustomerLeaving(Customer cust);
	
	public Cook getCook();

}