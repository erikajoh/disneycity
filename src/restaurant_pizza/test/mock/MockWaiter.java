package restaurant_pizza.test.mock;

import restaurant_pizza.Check;
import restaurant_pizza.CookAgent;
import restaurant_pizza.HostAgent;
import restaurant_pizza.interfaces.Cashier;
import restaurant_pizza.interfaces.Customer;
import restaurant_pizza.interfaces.Waiter;

public class MockWaiter extends Mock implements Waiter {

	public Cashier cashier;
	public EventLog log;
	
	public Check check;

	public MockWaiter(String name) {
		super(name);
		log = new EventLog();
	}

	@Override
	public void msgHereIsCheck(Check aCheck) {
		check = aCheck;
		log.add(new LoggedEvent("Received msgHereIsCheck from cashier. Delivering to customer."));
	}

	@Override
	public void msgShiftDone(boolean b) {
		
	}
}
