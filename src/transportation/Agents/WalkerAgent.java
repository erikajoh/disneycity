package transportation.Agents;

import simcity.PersonAgent;
import transportation.Objects.*;

public class WalkerAgent extends MobileAgent{
	
	PersonAgent walker;
	MovementTile currentPosition;
	MovementTile endPosititon;
	TransportationController master;
	boolean arrived;
	//CarGUI gui;
	
	public WalkerAgent(PersonAgent walker, MovementTile currentPosition, MovementTile endPosition, TransportationController master) {
		this.walker = walker;
		this.currentPosition = currentPosition;
		this.endPosititon = endPosition;
		this.master = master;
		arrived = false;
	}
	//Remember to release semaphores to tiles when despawning
	@Override
	protected boolean pickAndExecuteAnAction() {
		if(!arrived) {
			goToEndPosition();
		}
		if(arrived) {
			
		}
		return false;
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}

}
