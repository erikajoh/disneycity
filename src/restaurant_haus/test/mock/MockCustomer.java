package restaurant_haus.test.mock;


import restaurant_haus.CashierAgent;
import restaurant_haus.interfaces.*;
import restaurant_haus.test.mock.EventLog;

/**
 * A sample MockCustomer built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public class MockCustomer extends Mock implements Customer {

	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public Cashier cashier;

	public MockCustomer(String name) {
		super(name);
	}

	@Override
	public void msgHereIsCheck(double check, CashierAgent cashier) {
		log.add(new LoggedEvent ("Customer received check."));
	}
}
