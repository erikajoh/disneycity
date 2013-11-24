package transportation.Objects;

import java.util.*;

import transportation.Agents.Bus;
import simcity.PersonAgent;

public class BusStop {
	List<BusRider> busRiders;
	float fare;
	Bus currentBus;
	
	class BusRider {
		PersonAgent person;
		boolean payedFare;
		
		public BusRider(PersonAgent person) {
			this.person = person;
			payedFare = false;
		}
	}
	
	public BusStop() {
		busRiders = new ArrayList<BusRider>();
		fare = 1.50f;
		currentBus = null;
	}
	
	public void personWaitingAtStop(PersonAgent person) {
		busRiders.add(new BusRider(person));
	}
	
	public void busArrived(Bus bus) {
		currentBus = bus;
	}
	
	public void busLeaving() {
		currentBus = null;
	}
	
	public void messageRidersForFare() {
		if(currentBus == null) {
			System.out.println("NoBusFound. Find agent calling function.");
			return;
		}
		synchronized(busRiders) {
			for(BusRider busRider : busRiders) {
				if(!busRider.payedFare) {
					//busRider.person.msgPayFare(fare, currentBus);
				}
			}
		}
	}
	
	public void clearRiders() {
		busRiders = new ArrayList<BusRider>();
	}
}
