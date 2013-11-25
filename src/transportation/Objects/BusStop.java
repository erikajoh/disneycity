package transportation.Objects;

import java.util.*;

import astar.astar.Position;
import simcity.PersonAgent;

public class BusStop {
	public List<BusRider> busWaiters;
	String name;
	List<String> nearbyBuildings;
	Position associatedTile;
	
	public BusStop(String name) {
		busWaiters = new ArrayList<BusRider>();
		nearbyBuildings = new ArrayList<String>();
		this.name = name;
	}
	
	public List<BusRider> getBusWaiters() {
		return busWaiters;
	}
	
	public void clearRiders() {
		busWaiters = new ArrayList<BusRider>();
	}
	
	public void addRider(PersonAgent person, BusStop finalStop, String destination) {
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
	
	public void associateWalkTile(Position tile) {
		associatedTile = tile;
	}
	
	public Position getAssociatedTile() {
		return associatedTile;
	}
	
	public String getName() {
		return name;
	}
}
