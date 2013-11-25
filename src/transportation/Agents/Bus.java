package transportation.Agents;

import java.util.ArrayList;
import java.util.List;

import simcity.PersonAgent;
import transportation.Objects.BusRider;
import transportation.Objects.BusStop;

public class Bus extends MobileAgent{
	
	private final float fare = 1.50f;
	private float collectedFare;
	private List<BusRider> busRiders;
	BusStop currentBusStop;
	
	public Bus() {
		collectedFare = 0;
		currentBusStop = null;
		busRiders = new ArrayList<BusRider>();
	}
	
	//+++++++++++++++++MESSAGES+++++++++++++++++
	public void msgPayFare(PersonAgent person, float fare) {
		for(BusRider busRider : busRiders) {
			if(busRider.getPerson() == person) {
				busRider.state = BusRider.RiderState.RIDING;
				stateChanged();
			}
		}
	}
	
	public void msgReachedBusStop(BusStop busStop) {
		currentBusStop = busStop;
		stateChanged();
	}
	
	//+++++++++++++++++SCHEDULER+++++++++++++++++
	@Override
	protected boolean pickAndExecuteAnAction() {
		
		if(currentBusStop != null) {
			dropOffRiders();
			pickUpRiders();
		}
		
		synchronized(busRiders) {
			for(BusRider busRider : busRiders) {
				if(busRider.state == BusRider.RiderState.HASTOPAY)
					return false;
			}
		}
		
		moveToNextBusStop();
		return false;
	}

	//+++++++++++++++++ACTIONS+++++++++++++++++
	private void dropOffRiders() {
		for(BusRider busRider : busRiders) {
			if(busRider.getFinalStop() == currentBusStop){
				//send request to spawn walker to transportation
				busRider.state = BusRider.RiderState.GOTOFF;
			}
		}
	}
	
	private void pickUpRiders() {
		List<BusRider> tempList = currentBusStop.getBusWaiters();
		for(BusRider busRider : tempList) {
			busRiders.add(busRider);
			busRider.getPerson().msgPayFare(fare);
			busRider.state = BusRider.RiderState.HASTOPAY;
		}
	}
	
	//Also removes BusRiders that aren't on the bus anymore
	private void moveToNextBusStop() {
		synchronized(busRiders) {
			for(int i = busRiders.size() - 1 ; i >= 0; i--) {
				if(busRiders.get(i).state == BusRider.RiderState.GOTOFF)
					busRiders.remove(i);
			}
		}
		
		//gui code to go to next bus stop
	}
	
	@Override
	public String getType() {
		return "bus";
	}
}
