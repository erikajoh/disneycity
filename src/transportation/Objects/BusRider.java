package transportation.Objects;

import astar.astar.Position;
import simcity.interfaces.Person;

public class BusRider {
	Person person;
	BusStop finalStop;
	String destination;
	public RiderState state;
	
	public enum RiderState {
		WAITING,
		HASTOPAY,
		RIDING,
		GOTOFF
	}
	
	public BusRider(Person walker, BusStop finalStop, String destination) {
		this.person = walker;
		this.finalStop = finalStop;
		this.destination = destination;
		state = RiderState.WAITING;
	}
	
	public Person getPerson() {
		return person;
	}
	
	public BusStop getFinalStop() {
		return finalStop;
	}
	
	public String getDestination() {
		return destination;
	}
}
