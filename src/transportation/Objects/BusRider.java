package transportation.Objects;

import astar.astar.Position;
import simcity.PersonAgent;

public class BusRider {
	PersonAgent person;
	BusStop finalStop;
	String destination;
	public RiderState state;
	
	public enum RiderState {
		WAITING,
		HASTOPAY,
		RIDING,
		GOTOFF
	}
	
	public BusRider(PersonAgent person, BusStop finalStop, String destination) {
		this.person = person;
		this.finalStop = finalStop;
		this.destination = destination;
		state = RiderState.WAITING;
	}
	
	public PersonAgent getPerson() {
		return person;
	}
	
	public BusStop getFinalStop() {
		return finalStop;
	}
	
	public String getDestination() {
		return destination;
	}
}
