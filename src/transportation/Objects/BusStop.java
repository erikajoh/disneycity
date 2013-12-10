package transportation.Objects;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.*;

import astar.astar.Position;
import simcity.PersonAgent;
import simcity.interfaces.Person;
import transportation.TransportationPanel;
import transportation.GUIs.Gui;

public class BusStop implements Gui{
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
	
	public void addRider(Person walker, BusStop finalStop, String destination) {
		busWaiters.add(new BusRider(walker, finalStop, destination));
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

	@Override
	public void updatePosition() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw(Graphics2D g, Point offset) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isPresent() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setPanel(TransportationPanel transportationPanel) {
		// TODO Auto-generated method stub
		
	}
}
