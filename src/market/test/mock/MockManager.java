package market.test.mock;

import housing.test.mock.LoggedEvent;
import market.CustomerAgent;
import market.interfaces.Customer;
import market.interfaces.Manager;

public class MockManager implements Manager{
	
	@Override
	public void msgWantToOrder(Customer c, String choice, int quantity,
			boolean virtual) {
		// TODO Auto-generated method stub
	}

}
