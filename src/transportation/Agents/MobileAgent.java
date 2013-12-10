package transportation.Agents;

import simcity.interfaces.Person;
import transportation.Objects.MovementTile;
import agent.*;

public abstract class MobileAgent extends Agent {
	protected abstract boolean pickAndExecuteAnAction();
	public abstract String getType();
	public void msgCrash() {}
	public Person getPerson(){return null;}
}
