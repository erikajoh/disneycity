package transportation.Agents;

import simcity.interfaces.Person;
import transportation.Objects.MovementTile;
import agent.*;
import astar.astar.Position;

public abstract class MobileAgent extends Agent {
	protected abstract boolean pickAndExecuteAnAction();
	public abstract String getType();
	public Person getPerson(){return null;}
	public void crash(Position position) {
		// TODO Auto-generated method stub
		
	}
}
