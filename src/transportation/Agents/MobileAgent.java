package transportation.Agents;

import transportation.Objects.MovementTile;
import agent.*;

public abstract class MobileAgent extends Agent {
	protected abstract boolean pickAndExecuteAnAction();
	public abstract String getType();
	public void msgCrash() {
		// TODO Auto-generated method stub
		
	}
}
