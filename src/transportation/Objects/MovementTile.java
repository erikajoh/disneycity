package transportation.Objects;

import java.util.concurrent.Semaphore;

import transportation.Objects.MovementTile.MovementType;

public class MovementTile extends Semaphore{
	public enum MovementType {
		UNTYPED,
		WALKWAY,
		ROAD,
		CROSSWALK,
		TRAFFICCROSSWALK,
		TRAFFICCROSSROAD,
		TRAFFICCROSSNONE,
		FLYING
	};

	MovementType type;
	public boolean up, down, left, right;
	BusStop busStop;
	TrafficLight light;

	public MovementTile() {
		super(1, true);

		up = false;
		down = false;
		left = false;
		right  = false;

		type = MovementType.UNTYPED;

		busStop = null;
		light = null;
	}

	public void setMovement(boolean up, boolean down, boolean left, boolean right, MovementType type) {
		this.up = up;
		this.down = down;
		this.left = left;
		this.right = right;
		this.type = type;
	}
	
	public void setUp(boolean up) {
		this.up = up;
	}
	
	public void setDown(boolean down) {
		this.down = down;
	}
	
	public void setRight(boolean Right) {
		this.right = right;
	}
	
	public void setLeft(boolean left) {
		this.left = left;
	}
	
	public void setMovementType(MovementType type) {
		this.type = type;
	}
	
	public void setTrafficLight(TrafficLight light) {
		this.light = light;
	}

	public void setBusStop(BusStop busStop) {
		this.busStop = busStop;
	}

	public BusStop getBusStop() {
		return busStop;
	}

	public MovementType getMovementType() {
		return type;
	}
}
