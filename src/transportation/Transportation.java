package transportation;

import simcity.PersonAgent;

public interface Transportation {
	public abstract void msgWantToGo(String startLocation, String endLocation, PersonAgent person, String mover, String character);
}
