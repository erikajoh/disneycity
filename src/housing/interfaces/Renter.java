package housing.interfaces;

import java.util.List;

import housing.OwnerAgent;
import housing.RenterAgent;

/**
 * A sample Renter interface built to unit test a RenterAgent.
 *
 * @author Erika Johnson
 *
 */
public interface Renter {
	
//	public void msgYouAreApproved(Owner o);
	
	public void msgTimeToPay(double amt);
	
	public void msgPaymentAccepted();
	
	public void msgFinishedMaintenance();
	
	public void msgFoodDone();
	
}