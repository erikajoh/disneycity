package transportation.Agents;

import java.util.List;

import simcity.PersonAgent;
import transportation.Objects.BusRider;
import transportation.Objects.BusStop;

public class Bus extends MobileAgent{
	
	final float fare = 1.50f;
	float collectedFare;
	List<BusRider> busRiders;
	
	//+++++++++++++++++MESSAGES+++++++++++++++++
	public void msgPayFare(PersonAgent person, float fare) {
		for(BusRider busRider : busRiders) {
			if(busRider.getPerson() == person) {
				busRider.state = BusRider.RiderState.RIDING;
				stateChanged();
			}
		}
	}
	
	@Override
	protected boolean pickAndExecuteAnAction() {
		/*
		 * if at busstop
		 * Drop off riders
		 * get new riders
		 * 
		 */
		
		return false;
	}

	@Override
	public String getType() {
		return "bus";
	}
}
