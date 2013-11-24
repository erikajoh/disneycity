package restaurant_pizza;

import restaurant_pizza.CashierAgent.CheckState;
import restaurant_pizza.interfaces.Customer;
import restaurant_pizza.interfaces.Waiter;

public class Check {
	public String order;
	public Waiter waiter;
	public Customer customer;
	public double amountDue;
	public CheckState state;
	public Check(String aOrder, Waiter aWaiter, Customer aCustomer, double anAmountDue) {
		order = aOrder;
		waiter = aWaiter;
		customer = aCustomer;
		amountDue = anAmountDue;
		state = CheckState.NewCheck;
	}
}
