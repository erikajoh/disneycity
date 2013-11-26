package transportation.Agents;

import java.awt.*;
import java.util.*;
import java.util.List;

import transportation.Transportation;
import transportation.TransportationPanel;
import transportation.GUIs.BusGui;
import transportation.GUIs.CarGui;
import transportation.GUIs.WalkerGui;
import transportation.Objects.*;
import transportation.Objects.MovementTile.MovementType;
import agent.Agent;
import astar.astar.Position;
import simcity.*;

public class TransportationController extends Agent implements Transportation{

	TransportationPanel master;

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
		Position walkingTile;
		Position vehicleTile;
		BusStop closestBusStop;

		public Building(String name) {
			this.name  = name;
			walkingTile = null;
			vehicleTile = null;
			closestBusStop = null;
		}

		public Building(String name, Position walkingTile, Position vehicleTile, BusStop closestBusStop) {
			this.name  = name;
			this.walkingTile = walkingTile;
			this.vehicleTile = vehicleTile;
			this.closestBusStop = closestBusStop;
		}


		public void addEnteringTiles(Position walkingTile, Position vehicleTile) {
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
	BusAgent bus;

	public TransportationController(TransportationPanel panel) {
		master = panel;
		movingObjects = Collections.synchronizedList(new ArrayList<Mover>());

		//++++++++++++++++++++++BEGIN CREATION OF GRID++++++++++++++++++++++
		grid = new MovementTile[16][13];

		for(int i = 0; i < grid.length; i++) {
			for(int j = 0; j< grid[0].length; j++) {
				grid[i][j] = new MovementTile();
			}
		}
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

		grid[11][11].setMovement(true, false, false, false, MovementTile.MovementType.FLYING);

		grid[7][4].setMovement(false, true, true, false, MovementTile.MovementType.CROSSWALK);
		grid[11][6].setMovement(true, false, true, false, MovementTile.MovementType.CROSSWALK);
		grid[5][8].setMovement(false, false, false, true, MovementTile.MovementType.CROSSWALK);
		//+++++++++++++++++++++++END CREATION OF GRID+++++++++++++++++++++++

		//++++++++++++++++++++++BEGIN CREATION OF BUS STOPS++++++++++++++++++++++
		busStops = new ArrayList<BusStop>();

		BusStop tempBusStop = new BusStop("Bus Stop NW");//Top Left Bus Stop 0
		tempBusStop.addNearbyBuilding("Pirate Bank");
		tempBusStop.addNearbyBuilding("Rancho Del Zocalo");
		tempBusStop.addNearbyBuilding("Main St Apartments #1");
		tempBusStop.addNearbyBuilding("Main St Apartments #4");
		tempBusStop.associateWalkTile(new Position(7, 3));
		busStops.add(tempBusStop);

		tempBusStop = new BusStop("Bus Stop E");//Right Bus Stop 1
		tempBusStop.addNearbyBuilding("Main St Apartments #2");
		tempBusStop.addNearbyBuilding("Haunted Mansion");
		tempBusStop.addNearbyBuilding("The Blue Bayou");
		tempBusStop.addNearbyBuilding("Main St Apartments #6");
		tempBusStop.addNearbyBuilding("Carnation Cafe");
		tempBusStop.associateWalkTile(new Position(12, 6));
		busStops.add(tempBusStop);

		tempBusStop = new BusStop("Bus Stop SW");//Bottom Left Bus Stop 2
		tempBusStop.addNearbyBuilding("Main St Apartments #4");
		tempBusStop.addNearbyBuilding("Main St Apartments #5");
		tempBusStop.addNearbyBuilding("Village Haus");
		tempBusStop.addNearbyBuilding("Pizza Port");
		tempBusStop.addNearbyBuilding("Mickey's Market");
		tempBusStop.associateWalkTile(new Position(5, 9));
		busStops.add(tempBusStop);
		//+++++++++++++++++++++++END CREATION OF BUS STOPS+++++++++++++++++++++++


		//++++++++++++++++++++++BEGIN CREATION OF DIRECTORY++++++++++++++++++++++
		directory = new HashMap<String, Building>();

		Building tempBuilding = new Building("Main St Apartments #1", new Position(9, 2), new Position(9, 4), busStops.get(0));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Main St Apartments #2", new Position(12, 2), new Position(12, 4), busStops.get(1));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Main St Apartments #3", new Position(2, 3), new Position(4, 5), busStops.get(2));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Main St Apartments #4", new Position(2, 6), new Position(4, 6), busStops.get(0));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Main St Apartments #5", new Position(3, 10), new Position(4, 8), busStops.get(2));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Main St Apartments #6", new Position(13, 9), new Position(11, 8), busStops.get(1));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Haunted Mansion", new Position(13, 3), new Position(11, 4), busStops.get(1));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Mickey's Market", new Position(8, 10), new Position(8, 8), busStops.get(2));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Pirate Bank", new Position(7, 2), new Position(7, 4), busStops.get(0));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Rancho Del Zocalo", new Position(3, 2), new Position(4, 4), busStops.get(0));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Carnation Cafe", new Position(12, 10), new Position(11, 8), busStops.get(1));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("The Blue Bayou", new Position(13, 5), new Position(11, 5), busStops.get(1));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Pizza Port", new Position(5, 10), new Position(4, 8), busStops.get(2));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Village Haus", new Position(2, 9), new Position(4, 8), busStops.get(2));
		directory.put(tempBuilding.name, tempBuilding);

		tempBuilding = new Building("Bus Stop NW", new Position(7, 3), new Position(7, 4), busStops.get(0));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Bus Stop E", new Position(12, 6), new Position(11, 6), busStops.get(1));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Bus Stop SW", new Position(5, 9), new Position(5, 8), busStops.get(2));
		directory.put(tempBuilding.name, tempBuilding);
		//+++++++++++++++++++++++END CREATION OF DIRECTORY+++++++++++++++++++++++

		//Connecting bus stops to tiles
		grid[7][4].setBusStop(busStops.get(0));
		grid[11][6].setBusStop(busStops.get(1));
		grid[5][8].setBusStop(busStops.get(2));

		//Spawning Bus
		bus = new BusAgent(this);
		BusGui busGui = new BusGui(4, 4, bus);
		master.addGui(busGui);
		bus.setGui(busGui);
		bus.startThread();

		super.startThread();
	}

	//+++++++++++++++++MESSAGES+++++++++++++++++
	public void msgWantToGo(String startLocation, String endLocation, PersonAgent person, String mover, String character) {
		
		for(Mover m : movingObjects) {
			if(m.person == person && !m.method.equals("Bus"))
				return;
		}
		
		System.out.println("RECEIVED REQUEST TO TRANSPORT");
		
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
		TransportationTraversal aStar = new TransportationTraversal(grid);
		switch(mover.method) {
		case "Car":
			mover.transportationState = TransportationState.MOVING;
			CarAgent driver = new CarAgent(mover.person, directory.get(mover.startingLocation).vehicleTile, directory.get(mover.endingLocation).vehicleTile, this, aStar);
			driver.startThread();
			CarGui carGui = new CarGui(directory.get(mover.startingLocation).vehicleTile.getX(), directory.get(mover.startingLocation).vehicleTile.getY(), driver);
			master.addGui(carGui);
			driver.setGui(carGui);
			break;

		case  "Walk":
			mover.transportationState = TransportationState.MOVING;
			WalkerAgent walker = new WalkerAgent(mover.person, directory.get(mover.startingLocation).walkingTile, directory.get(mover.endingLocation).walkingTile, this, aStar);
			walker.startThread();
			WalkerGui walkerGui = new WalkerGui(directory.get(mover.startingLocation).walkingTile.getX(), directory.get(mover.startingLocation).walkingTile.getY(), walker);
			master.addGui(walkerGui);
			walker.setGui(walkerGui);
			break;

		case "Bus":
			//find bus stop and spawn walker to go to bus stop
			mover.transportationState = TransportationState.MOVING;
			if(directory.get(mover.startingLocation).closestBusStop == directory.get(mover.endingLocation).closestBusStop) {
				mover.method = "Walk";
				spawnMover(mover);
				break;
			}
			WalkerAgent busWalker = new WalkerAgent(mover.person, directory.get(mover.startingLocation).walkingTile, directory.get(mover.endingLocation).walkingTile, this, aStar, directory.get(mover.startingLocation).closestBusStop, directory.get(mover.endingLocation).closestBusStop, mover.endingLocation);
			busWalker.startThread();
			WalkerGui busWalkerGui = new WalkerGui(directory.get(mover.startingLocation).walkingTile.getX(), directory.get(mover.startingLocation).walkingTile.getY(), busWalker);
			master.addGui(busWalkerGui);
			busWalker.setGui(busWalkerGui);
			break;
		}
	}

	private void despawnMover(Mover mover) {
		if(!mover.method.equals("Bus"))
			mover.person.msgReachedDestination(mover.endingLocation);
		movingObjects.remove(mover);
	}

	private void retrySpawn(Mover mover) {
		mover.transportationState = TransportationState.REQUEST;
	}

	public MovementTile[][] getGrid() {
		return grid;
	}

	public void msgPayFare(PersonAgent person, float fare) {
		bus.msgPayFare(person, fare);
	}
}
