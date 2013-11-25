package restaurant_haus.test.mock;

import restaurant_haus.interfaces.*;

public class MockMarket extends Mock implements Market{

	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public Cashier cashier;
	
	public double check;
	
	public MockMarket(String name) {
		super(name);

	}

	@Override
	public void msgOrderPayment(double money, boolean paidFull) {
		if(paidFull) {
			log.add(new LoggedEvent("Cashier Payed Full"));
		}
		
		else {
			log.add(new LoggedEvent("Cashier Payed Part"));
		}
	}

	@Override
	public void msgFinalPayment() {
		log.add(new LoggedEvent("Cashier Payed Off Debt"));
	}

	@Override
	public void msgIncrementalPayment(double money) {
		log.add(new LoggedEvent("Cashier Made Payment On Debt"));	
	}
}
