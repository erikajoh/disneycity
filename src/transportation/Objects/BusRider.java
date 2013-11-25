package transportation.Objects;

import simcity.PersonAgent;

public class BusRider {
	PersonAgent person;
	BusStop finalStop;
	MovementTile destination;
	public RiderState state;
	
	public enum RiderState {
		WAITING,
		HASTOPAY,
		RIDING,
		GOTOFF
	}
	
	public BusRider(PersonAgent person, BusStop finalStop, MovementTile destination) {
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
	
	public MovementTile getDestination() {
		return destination;
	}
}
