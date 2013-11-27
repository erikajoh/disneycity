package housing.interfaces;

import housing.ResidentAgent.State;
import housing.test.mock.LoggedEvent;

import java.util.Map;

public interface Resident {
	public void msgAnimationFinished();
	
	public void msgAnimationLeavingFinished();
	
	public void msgMaintenanceAnimationFinished();
	
	public void msgDoMaintenance();
	
	public void msgLeave();

	public void msgCookFood(String choice);
	
	public void msgDoneCooking(boolean success);

	public void msgHome(Map<String, Integer> items);
	
	public void msgToBed();

}
