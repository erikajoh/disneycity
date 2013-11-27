package simcity.interfaces;

import housing.ResidentAgent;
import java.util.Map;

import simcity.PersonAgent;

public interface Housing_Douglass {
	public String getName();
	
	public void msgHereIsRent(PersonAgent personAgent, double amount);
	
	//public void msgIAmHome(PersonAgent personAgent);
	
	public void msgIAmLeaving(PersonAgent personAgent);
	
	public void msgGoToBed(PersonAgent personAgent);
	
	public void msgPrepareToCookAtHome(PersonAgent personAgent, String foodPreference);
	
	public void msgIAmHome(PersonAgent rp, Map<String, Integer> items);
	
	public void msgDoMaintenance();
	
	public void msgRentDue();
	
	public void msgEntered(ResidentAgent ra);
	
	public void msgFinishedMaintenance(ResidentAgent ra);
	
	public void msgFoodDone(ResidentAgent ra, boolean success);
	
	public void msgLeft(ResidentAgent ra);
	
}
