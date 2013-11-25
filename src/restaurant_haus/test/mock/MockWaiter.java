package restaurant_haus.test.mock;

import restaurant_haus.interfaces.*;

/**
 * A sample MockCustomer built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public class MockWaiter extends Mock implements Waiter {

	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public Cashier cashier;
	public Customer customer;
	public Customer customer2;
	
	public double check;
	
	public MockWaiter(String name) {
		super(name);

	}

	@Override
	public void msgCheckPlease(Customer c) {
	}
	
	@Override
	public void msgHereIsCheck(Customer c, double price) {
		log.add(new LoggedEvent("Received check for mockcustomer"));
		check = price;
	}
}
