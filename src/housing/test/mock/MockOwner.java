package housing.test.mock;


import java.util.List;

import housing.OwnerAgent;
import housing.RenterAgent;
import housing.interfaces.Owner;

/**
 * A sample MockOwner built to unit test a RenterAgent.
 *
 * @author Erika Johnson
 *
 */
public class MockOwner extends Mock implements Owner {

	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public RenterAgent renter;

	public MockOwner(String name) {
		super(name);
	}

	@Override
	public void msgWantToRent(RenterAgent ra) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("New renter is here"));
	}

	@Override
	public void msgHereIsPayment(RenterAgent ra, double amount) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Payment received from renter: "));
	}

	@Override
	public void msgReadyToCook(RenterAgent ra, String food) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgWantMaintenance(RenterAgent ra) {
		// TODO Auto-generated method stub
		
	}

	//@Override

}
