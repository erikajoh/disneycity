package transportation.Objects;

import java.util.concurrent.Semaphore;

public class MovementTile{
	public enum MovementType {
		WALKWAY,
		ROAD,
		CROSSWALK
	};
	
	MovementType type;
	
	Semaphore occupied;
	
	public void leaveTile() {
		occupied.release();
	}
	public void enterTile() {
		try {
			occupied.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
