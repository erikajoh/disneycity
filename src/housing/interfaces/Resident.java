package housing.interfaces;

import java.util.List;
import java.util.Map;

import housing.ResidentAgent;
import housing.ResidentAgent.State;

/**
 * A sample Renter interface built to unit test a RenterAgent.
 *
 * @author Erika Johnson
 *
 */
public interface Resident {
		
	public void msgLeave();

	public void msgCookFood(String choice);

	public void msgHome(Map<String, Integer> items);

	public void msgToBed();
	
}