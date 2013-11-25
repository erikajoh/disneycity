package transportation.Agents;

import simcity.PersonAgent;
import transportation.Objects.*;

public class CarAgent extends MobileAgent{
	
	PersonAgent driver;
	MovementTile currentPosition;
	MovementTile endPosititon;
	TransportationController master;
	boolean arrived;
	//CarGUI gui;
	
	public CarAgent(PersonAgent driver, MovementTile currentPosition, MovementTile endPosition, TransportationController master) {
		this.driver = driver;
		this.currentPosition = currentPosition;
		this.endPosititon = endPosition;
		this.master = master;
		arrived = false;
	}
	//Remember to release semaphores to tiles when despawning
	@Override
	protected boolean pickAndExecuteAnAction() {
		
		return false;
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}

}
