package transportation.Agents;

import java.awt.*;
import java.util.*;
import java.util.List;

import transportation.Objects.*;
import transportation.Objects.MovementTile.MovementType;
import agent.Agent;
import simcity.*;

public class TransportationController extends Agent {

	enum TransportationState {
		REQUEST,
		MOVING,
		DESTINATION,
		WAITINGTOSPAWN
	};

	class Mover {
		TransportationState transportationState;
		PersonAgent person;
		MobileAgent mobile;

		String startingLocation;
		String endingLocation;
		String method;
		String character;

		Mover(PersonAgent person, String startingLocation, String endingLocation, String method, String character) {
			this.person = person;
			this.startingLocation = startingLocation;
			this.endingLocation = endingLocation;
			this.method = method;
			this.character = character;
			transportationState = TransportationState.REQUEST;
		}
	}

	List<Mover> movingObjects;

	class Building {
		String name;
		MovementTile walkingTile;
		MovementTile vehicleTile;
		BusStop closestBusStop;

		public Building(String name) {
			this.name  = name;
			walkingTile = null;
			vehicleTile = null;
			closestBusStop = null;
		}

		public Building(String name, MovementTile walkingTile, MovementTile vehicleTile, BusStop closestBusStop) {
			this.name  = name;
			this.walkingTile = walkingTile;
			this.vehicleTile = vehicleTile;
			this.closestBusStop = closestBusStop;
		}


		public void addEnteringTiles(MovementTile walkingTile, MovementTile vehicleTile) {
			this.walkingTile = walkingTile;
			this.vehicleTile = vehicleTile;
		}

		public void setBusStop(BusStop busStop) {
			this.closestBusStop = busStop;
		}
	}

	Map<String, Building> directory;

	MovementTile[][] grid;
	List<BusStop> busStops;

	public TransportationController() {
		movingObjects = new ArrayList<Mover>();

		//++++++++++++++++++++++BEGIN CREATION OF GRID++++++++++++++++++++++
		grid = new MovementTile[16][13];
		//Walkways
		for(int i = 2; i <= 13; i++) {
			grid[i][2].setMovement(true, true, true, true, MovementTile.MovementType.WALKWAY);
			grid[i][3].setMovement(true, true, true, true, MovementTile.MovementType.WALKWAY);
			grid[i][9].setMovement(true, true, true, true, MovementTile.MovementType.WALKWAY);
			grid[i][10].setMovement(true, true, true, true, MovementTile.MovementType.WALKWAY);
		}
		for(int i = 4; i <= 8; i++) {
			grid[2][i].setMovement(true, true, true, true, MovementTile.MovementType.WALKWAY);
			grid[3][i].setMovement(true, true, true, true, MovementTile.MovementType.WALKWAY);
			grid[12][i].setMovement(true, true, true, true, MovementTile.MovementType.WALKWAY);
			grid[13][i].setMovement(true, true, true, true, MovementTile.MovementType.WALKWAY);
		}

		//Roads
		grid[5][7].setMovement(true, false, false, false, MovementTile.MovementType.ROAD);
		grid[11][5].setMovement(true, false, false, false, MovementTile.MovementType.ROAD);
		grid[11][7].setMovement(true, false, false, false, MovementTile.MovementType.ROAD);
		grid[11][8].setMovement(true, false, false, false, MovementTile.MovementType.ROAD);

		grid[4][4].setMovement(false, true, false, false, MovementTile.MovementType.ROAD);
		grid[4][7].setMovement(false, true, false, false, MovementTile.MovementType.ROAD);
		grid[4][8].setMovement(false, true, false, false, MovementTile.MovementType.ROAD);
		grid[10][5].setMovement(false, true, false, false, MovementTile.MovementType.ROAD);

		grid[5][4].setMovement(false, false, true, false, MovementTile.MovementType.ROAD);
		grid[10][4].setMovement(false, false, true, false, MovementTile.MovementType.ROAD);
		grid[11][4].setMovement(false, false, true, false, MovementTile.MovementType.ROAD);
		grid[10][7].setMovement(false, false, true, false, MovementTile.MovementType.ROAD);

		grid[5][5].setMovement(false, false, false, true, MovementTile.MovementType.ROAD);
		grid[4][8].setMovement(false, false, false, true, MovementTile.MovementType.ROAD);
		grid[5][8].setMovement(false, false, false, true, MovementTile.MovementType.ROAD);
		grid[10][8].setMovement(false, false, false, true, MovementTile.MovementType.ROAD);

		for(int i = 6; i <= 9; i++) {
			grid[i][4].setMovement(false, true, true, false, MovementTile.MovementType.ROAD);
			grid[i][7].setMovement(false, true, true, false, MovementTile.MovementType.ROAD);

			grid[i][5].setMovement(true, false, false, true, MovementTile.MovementType.ROAD);
			grid[i][8].setMovement(true, false, false, true, MovementTile.MovementType.ROAD);
		}

		grid[4][6].setMovement(false, true, false, true, MovementTile.MovementType.ROAD);
		grid[5][6].setMovement(true, false, true, false, MovementTile.MovementType.ROAD);
		grid[10][6].setMovement(false, true, false, true, MovementTile.MovementType.ROAD);
		grid[11][6].setMovement(true, false, true, false, MovementTile.MovementType.ROAD);
		//+++++++++++++++++++++++END CREATION OF GRID+++++++++++++++++++++++

		//++++++++++++++++++++++BEGIN CREATION OF BUS STOPS++++++++++++++++++++++
		busStops = new ArrayList<BusStop>();

		BusStop tempBusStop = new BusStop();//Top Left Bus Stop 0
		tempBusStop.addNearbyBuilding("Bank");
		tempBusStop.addNearbyBuilding("Rancho");
		tempBusStop.addNearbyBuilding("Apt1");
		tempBusStop.addNearbyBuilding("Apt4");
		tempBusStop.associateWalkTile(grid[7][3]);
		busStops.add(tempBusStop);

		tempBusStop = new BusStop();//Right Bus Stop 1
		tempBusStop.addNearbyBuilding("Apt2");
		tempBusStop.addNearbyBuilding("Mansion");
		tempBusStop.addNearbyBuilding("Bayou");
		tempBusStop.addNearbyBuilding("Apt6");
		tempBusStop.addNearbyBuilding("Cafe");
		tempBusStop.associateWalkTile(grid[12][6]);
		busStops.add(tempBusStop);

		tempBusStop = new BusStop();//Bottom Left Bus Stop 2
		tempBusStop.addNearbyBuilding("Apt4");
		tempBusStop.addNearbyBuilding("Apt5");
		tempBusStop.addNearbyBuilding("Haus");
		tempBusStop.addNearbyBuilding("Pizza");
		tempBusStop.addNearbyBuilding("Market");
		tempBusStop.associateWalkTile(grid[5][8]);
		busStops.add(tempBusStop);
		//+++++++++++++++++++++++END CREATION OF BUS STOPS+++++++++++++++++++++++


		//++++++++++++++++++++++BEGIN CREATION OF DIRECTORY++++++++++++++++++++++
		directory = new HashMap<String, Building>();

		Building tempBuilding = new Building("Apt1", grid[9][2], grid[9][4], busStops.get(0));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Apt2", grid[12][2], grid[12][4], busStops.get(1));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Apt3", grid[2][3], grid[4][5], busStops.get(2));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Apt4", grid[2][6], grid[4][6], busStops.get(0));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Apt5", grid[3][10], grid[4][8], busStops.get(2));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Apt6", grid[13][9], grid[11][8], busStops.get(1));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Mansion", grid[13][3], grid[11][4], busStops.get(1));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Market", grid[8][10], grid[8][8], busStops.get(2));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Bank", grid[7][2], grid[7][4], busStops.get(0));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Rancho", grid[2][2], grid[4][5], busStops.get(0));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Cafe", grid[12][10], grid[11][8], busStops.get(1));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Bayou", grid[13][5], grid[11][5], busStops.get(1));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Pizza", grid[5][10], grid[4][8], busStops.get(2));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Haus", grid[2][9], grid[4][8], busStops.get(2));
		directory.put(tempBuilding.name, tempBuilding);
		//+++++++++++++++++++++++END CREATION OF DIRECTORY+++++++++++++++++++++++

		super.startThread();
	}

	//+++++++++++++++++MESSAGES+++++++++++++++++
	public void msgWantToGo(String startLocation, String endLocation, PersonAgent person, String mover, String character) {
		movingObjects.add(new Mover(person, startLocation, endLocation, mover, character));
		stateChanged();
	}

	public void msgArrivedAtDestination(PersonAgent person){
		for(Mover mover : movingObjects) {
			if(mover.person == person) {
				mover.transportationState = TransportationState.DESTINATION;
			}
		}
		stateChanged();
	}

	//+++++++++++++++++SCHEDULER+++++++++++++++++
	@Override
	protected boolean pickAndExecuteAnAction() {
		synchronized(movingObjects) {
			for(Mover mover : movingObjects) {
				if(mover.transportationState == TransportationState.REQUEST) {
					spawnMover(mover);
					return true;
				}
			}
		}

		synchronized(movingObjects) {
			for(Mover mover : movingObjects) {
				if(mover.transportationState == TransportationState.DESTINATION) {
					despawnMover(mover);//POTENTIAL ERROR
					return true;
				}
			}
		}

		synchronized(movingObjects) {
			boolean retry = false;
			for(Mover mover : movingObjects) {
				if(mover.transportationState == TransportationState.WAITINGTOSPAWN) {
					retry = true;
					retrySpawn(mover);
				}
			}
			if(retry)
				return true;
		}
		return false;
	}

	private void spawnMover(Mover mover) {
		//Try to spawn mover
		switch(mover.method) {
		case "Car":
			//spawn car
			break;

		case  "Walk":
			//spawn walker
			break;

		case "Bus":
			//find bus stop and spawn walker to go to bus stop
			break;
		}
	}

	private void despawnMover(Mover mover) {
		mover.person.msgReachedDestination(mover.endingLocation);
		movingObjects.remove(mover);
	}

	private void retrySpawn(Mover mover) {
		mover.transportationState = TransportationState.REQUEST;
	}
}
