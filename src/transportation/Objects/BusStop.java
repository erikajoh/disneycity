package transportation.Objects;

import java.util.*;

import simcity.PersonAgent;

public class BusStop {
	public List<BusRider> busWaiters;
	List<String> nearbyBuildings;
	MovementTile associatedTile;
	
	public BusStop() {
		busWaiters = new ArrayList<BusRider>();
		nearbyBuildings = new ArrayList<String>();
	}
	
	public List<BusRider> getBusWaiters() {
		return busWaiters;
	}
	
	public void clearRiders() {
		busWaiters = new ArrayList<BusRider>();
	}
	
	public void addRider(PersonAgent person, BusStop finalStop, MovementTile destination) {
		busWaiters.add(new BusRider(person, finalStop, destination));
	}
	
	public void addNearbyBuilding(String building) {
		nearbyBuildings.add(building);
	}
	
	public boolean isDestinationNear(String building) {
		if(nearbyBuildings.contains(building))
			return true;
		return false;
	}
	
	public void associateWalkTile(MovementTile tile) {
		associatedTile = tile;
	}
}
