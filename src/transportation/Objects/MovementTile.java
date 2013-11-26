package transportation.Objects;

import java.util.concurrent.Semaphore;

public class MovementTile extends Semaphore{
	public enum MovementType {
		UNTYPED,
		WALKWAY,
		ROAD,
		CROSSWALK
	};
	
	MovementType type;
	public boolean up, down, left, right;
	BusStop busStop;
	
	public MovementTile() {
		super(1, true);
		
		up = false;
		down = false;
		left = false;
		right  = false;
		
		type = MovementType.UNTYPED;
		
		busStop = null;
	}
	
	public void setMovement(boolean up, boolean down, boolean left, boolean right, MovementType type) {
		this.up = up;
		this.down = down;
		this.left = left;
		this.right = right;
		this.type = type;
	}
	
	public void setBusStop(BusStop busStop) {
		this.busStop = busStop;
	}
	
	public BusStop getBusStop() {
		return busStop;
	}
}
