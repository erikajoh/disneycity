package restaurant_pizza.test.mock;

import restaurant_pizza.interfaces.Cashier;
import restaurant_pizza.interfaces.Customer;

public class MockCustomer extends Mock implements Customer {

	public Cashier cashier;
	public EventLog log;

	public MockCustomer(String name) {
		super(name);
		log = new EventLog();
	}

	@Override
	public void msgPaymentApproved() {
		log.add(new LoggedEvent("Received msgPaymentApproved from cashier"));
	}

	@Override
	public void msgPaymentInvalid() {
		log.add(new LoggedEvent("Received msgPaymentInvalid from cashier"));
	}

	@Override
	public void msgLeaveRestaurant() {
		log.add(new LoggedEvent("Received msgLeaveRestaurant from cashier"));
	}
}
