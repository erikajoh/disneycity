package simcity.interfaces;

import java.util.Map;

import simcity.PersonAgent;

public interface Housing_Douglass {
	public String getName();
	public String getType();
	public void msgHereIsRent(PersonAgent personAgent, double amount);
	public void msgIAmHome(PersonAgent personAgent);
	public void msgIAmLeaving(PersonAgent personAgent);
	public void msgGoToBed(PersonAgent personAgent);
	public void msgPrepareToCookAtHome(PersonAgent personAgent,
			String foodPreference);
}
