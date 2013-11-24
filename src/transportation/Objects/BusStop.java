package transportation.Objects;

import java.util.*;

import transportation.Agents.Bus;
import simcity.PersonAgent;

public class BusStop {
	List<BusWaiter> busWaiters;
	float fare;
	Bus currentBus;
	
	class BusWaiter {
		PersonAgent person;
		boolean payedFare;
		
		public BusWaiter(PersonAgent person) {
			this.person = person;
			payedFare = false;
		}
	}
	
	public BusStop() {
		busWaiters = new ArrayList<BusWaiter>();
		fare = 1.50f;
		currentBus = null;
	}
	
	public void personWaitingAtStop(PersonAgent person) {
		busWaiters.add(new BusWaiter(person));
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
		synchronized(busWaiters) {
			for(BusWaiter busWaiter : busWaiters) {
				if(!busWaiter.payedFare) {
					busWaiter.person.msgPayFare(fare, currentBus);
				}
			}
		}
	}
	
	public void clearRiders() {
		busWaiters = new ArrayList<BusWaiter>();
	}
}
