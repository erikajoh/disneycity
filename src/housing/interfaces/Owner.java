package housing.interfaces;

import java.util.List;

import housing.OwnerAgent;
import housing.RenterAgent;

/**
 * A sample Owner interface built to unit test a RenterAgent.
 *
 * @author Erika Johnson
 *
 */
public interface Owner {
	
	public void msgWantToRent(RenterAgent ra);
	
	public void msgHereIsPayment(RenterAgent ra, double amount);
	
	public void msgReadyToCook(RenterAgent ra, String food);
	
	public void msgWantMaintenance(RenterAgent ra);

}