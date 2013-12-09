package transportation.Agents;

import java.awt.*;
import java.util.*;
import java.util.List;

import market.Market;
import transportation.Transportation;
import transportation.TransportationPanel;
import transportation.GUIs.BusGui;
import transportation.GUIs.CarGui;
import transportation.GUIs.TruckGui;
import transportation.GUIs.WalkerGui;
import transportation.Objects.*;
import transportation.Objects.MovementTile.MovementType;
import agent.Agent;
import astar.astar.Position;
import simcity.interfaces.Person;
import simcity.mock.LoggedEvent;
import simcity.*;
import simcity.gui.trace.AlertLog;
import simcity.gui.trace.AlertTag;

public class TransportationController extends Agent implements Transportation{

	TransportationPanel master;

	public enum TransportationState {
		REQUEST,
		MOVING,
		DESTINATION,
		WAITINGTOSPAWN
	};

	public class Mover {
		public TransportationState transportationState;
		public Person person;
		MobileAgent mobile;

		String startingLocation;
		String endingLocation;
		String method;
		String character;

		Mover(Person person, String startingLocation, String endingLocation, String method, String character) {
			this.person = person;
			this.startingLocation = startingLocation;
			this.endingLocation = endingLocation;
			this.method = method;
			this.character = character;
			transportationState = TransportationState.REQUEST;
		}
	}

	public List<Mover> movingObjects;

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



	public Map<String, Building> directory;

	MovementTile[][] grid;
	public List<BusStop> busStops;
	public List<BusAgent> buses;
	public TruckAgent truckMickey;
	public TruckAgent truckMinnie;
	private TrafficLight trafficLight;
	
	private class Crash {
		MobileAgent agent1;
		MobileAgent agent2;
		Position position;
		
		public Crash(MobileAgent agent1, MobileAgent agent2, Position position) {
			this.agent1 = agent1;
			this.agent2 = agent2;
			this.position = position;
		}
	}
	
	List<Crash> crashes;

	public TransportationController(TransportationPanel panel) {
		master = panel;
		movingObjects = Collections.synchronizedList(new ArrayList<Mover>());

		//++++++++++++++++++++++BEGIN CREATION OF GRID++++++++++++++++++++++
		grid = new MovementTile[34][30];

		for(int i = 0; i < grid.length; i++) {
			for(int j = 0; j< grid[0].length; j++) {
				grid[i][j] = new MovementTile(this, i, j);
			}
		}
		//Walkways
		for(int i = 2; i <= 31; i++) {
			grid[i][2].setMovement(true, true, true, true, MovementTile.MovementType.WALKWAY);
			grid[i][3].setMovement(true, true, true, true, MovementTile.MovementType.WALKWAY);
			grid[i][6].setMovement(true, true, true, true, MovementTile.MovementType.WALKWAY);
			grid[i][7].setMovement(true, true, true, true, MovementTile.MovementType.WALKWAY);
			grid[i][12].setMovement(true, true, true, true, MovementTile.MovementType.WALKWAY);
			grid[i][13].setMovement(true, true, true, true, MovementTile.MovementType.WALKWAY);
			grid[i][16].setMovement(true, true, true, true, MovementTile.MovementType.WALKWAY);
			grid[i][17].setMovement(true, true, true, true, MovementTile.MovementType.WALKWAY);
			grid[i][22].setMovement(true, true, true, true, MovementTile.MovementType.WALKWAY);
			grid[i][23].setMovement(true, true, true, true, MovementTile.MovementType.WALKWAY);
			grid[i][26].setMovement(true, true, true, true, MovementTile.MovementType.WALKWAY);
			grid[i][27].setMovement(true, true, true, true, MovementTile.MovementType.WALKWAY);
		}
		for(int i = 4; i <= 25; i++) {
			grid[2][i].setMovement(true, true, true, true, MovementTile.MovementType.WALKWAY);
			grid[3][i].setMovement(true, true, true, true, MovementTile.MovementType.WALKWAY);
			grid[6][i].setMovement(true, true, true, true, MovementTile.MovementType.WALKWAY);
			grid[7][i].setMovement(true, true, true, true, MovementTile.MovementType.WALKWAY);
			grid[14][i].setMovement(true, true, true, true, MovementTile.MovementType.WALKWAY);
			grid[15][i].setMovement(true, true, true, true, MovementTile.MovementType.WALKWAY);
			grid[18][i].setMovement(true, true, true, true, MovementTile.MovementType.WALKWAY);
			grid[19][i].setMovement(true, true, true, true, MovementTile.MovementType.WALKWAY);
			grid[26][i].setMovement(true, true, true, true, MovementTile.MovementType.WALKWAY);
			grid[27][i].setMovement(true, true, true, true, MovementTile.MovementType.WALKWAY);
			grid[30][i].setMovement(true, true, true, true, MovementTile.MovementType.WALKWAY);
			grid[31][i].setMovement(true, true, true, true, MovementTile.MovementType.WALKWAY);
		}

		//Roads
		for(int i = 4; i <= 29; i++) {
			grid[i][4].setMovement(false, true, true, false, MovementTile.MovementType.ROAD);
			grid[i][5].setMovement(true, false, false, true, MovementTile.MovementType.ROAD);

			grid[i][14].setMovement(false, true, true, false, MovementTile.MovementType.ROAD);
			grid[i][15].setMovement(true, false, false, true, MovementTile.MovementType.ROAD);

			grid[i][24].setMovement(false, true, true, false, MovementTile.MovementType.ROAD);
			grid[i][25].setMovement(true, false, false, true, MovementTile.MovementType.ROAD);
		}

		for(int i = 6; i <= 13; i++) {
			grid[4][i].setMovement(false, true, false, true, MovementTile.MovementType.ROAD);
			grid[5][i].setMovement(true, false, true, false, MovementTile.MovementType.ROAD);
			grid[4][i+10].setMovement(false, true, false, true, MovementTile.MovementType.ROAD);
			grid[5][i+10].setMovement(true, false, true, false, MovementTile.MovementType.ROAD);

			grid[16][i].setMovement(false, true, false, true, MovementTile.MovementType.ROAD);
			grid[17][i].setMovement(true, false, true, false, MovementTile.MovementType.ROAD);
			grid[16][i+10].setMovement(false, true, false, true, MovementTile.MovementType.ROAD);
			grid[17][i+10].setMovement(true, false, true, false, MovementTile.MovementType.ROAD);

			grid[28][i].setMovement(false, true, false, true, MovementTile.MovementType.ROAD);
			grid[29][i].setMovement(true, false, true, false, MovementTile.MovementType.ROAD);
			grid[28][i+10].setMovement(false, true, false, true, MovementTile.MovementType.ROAD);
			grid[29][i+10].setMovement(true, false, true, false, MovementTile.MovementType.ROAD);
		}

		//Intersections... oh joy!
		grid[4][4].setMovement(false, true, false, false, MovementTile.MovementType.ROAD);
		grid[5][4].setMovement(false, false, true, false, MovementTile.MovementType.ROAD);
		grid[4][5].setMovement(false, true, false, false, MovementTile.MovementType.ROAD);
		grid[5][5].setMovement(false, false, false, true, MovementTile.MovementType.ROAD);

		grid[16][4].setMovement(false, true, true, false, MovementTile.MovementType.ROAD);
		grid[17][4].setMovement(false, false, true, false, MovementTile.MovementType.ROAD);
		grid[16][5].setMovement(false, true, false, true, MovementTile.MovementType.ROAD);
		grid[17][5].setMovement(true, false, false, true, MovementTile.MovementType.ROAD);

		grid[28][4].setMovement(false, false, true, false, MovementTile.MovementType.ROAD);
		grid[29][4].setMovement(false, false, true, false, MovementTile.MovementType.ROAD);
		grid[28][5].setMovement(false, true, false, false, MovementTile.MovementType.ROAD);
		grid[29][5].setMovement(true, false, false, false, MovementTile.MovementType.ROAD);

		grid[4][14].setMovement(false, true, false, false, MovementTile.MovementType.ROAD);
		grid[5][14].setMovement(true, false, true, false, MovementTile.MovementType.ROAD);
		grid[4][15].setMovement(false, true, false, true, MovementTile.MovementType.ROAD);
		grid[5][15].setMovement(true, false, false, true, MovementTile.MovementType.ROAD);

		grid[28][14].setMovement(false, true, true, false, MovementTile.MovementType.ROAD);
		grid[29][14].setMovement(true, false, false, false, MovementTile.MovementType.ROAD);
		grid[28][15].setMovement(false, true, false, true, MovementTile.MovementType.ROAD);
		grid[29][15].setMovement(true, false, false, false, MovementTile.MovementType.ROAD);

		grid[4][24].setMovement(false, true, false, false, MovementTile.MovementType.ROAD);
		grid[5][24].setMovement(true, false, false, false, MovementTile.MovementType.ROAD);
		grid[4][25].setMovement(false, false, false, true, MovementTile.MovementType.ROAD);
		grid[5][25].setMovement(false, false, false, true, MovementTile.MovementType.ROAD);

		grid[16][24].setMovement(false, true, true, false, MovementTile.MovementType.ROAD);
		grid[17][24].setMovement(true, false, true, false, MovementTile.MovementType.ROAD);
		grid[16][25].setMovement(false, false, false, true, MovementTile.MovementType.ROAD);
		grid[17][25].setMovement(true, false, false, true, MovementTile.MovementType.ROAD);

		grid[28][24].setMovement(false, false, true, false, MovementTile.MovementType.ROAD);
		grid[29][24].setMovement(true, false, false, false, MovementTile.MovementType.ROAD);
		grid[28][25].setMovement(false, false, false, true, MovementTile.MovementType.ROAD);
		grid[29][25].setMovement(false, false, false, true, MovementTile.MovementType.ROAD);

		//Small Driveway Esque Roads
		//Houses first which are single tiles
		grid[9][1].setMovement(false, true, false, false, MovementTile.MovementType.ROAD);//Tiki Hut
		grid[9][4].setUp(true);

		grid[32][6].setMovement(false, false, true, false, MovementTile.MovementType.ROAD);//Haunted Mansion
		grid[29][6].setRight(true);

		grid[1][10].setMovement(false, false, false, true, MovementTile.MovementType.ROAD);//Rabbit Hole
		grid[4][10].setLeft(true);

		grid[32][19].setMovement(false, false, true, false, MovementTile.MovementType.ROAD);//Pirate's Suite
		grid[29][19].setRight(true);

		grid[11][21].setMovement(false, true, false, false, MovementTile.MovementType.ROAD);//Space Mountain
		grid[11][24].setUp(true);

		grid[1][24].setMovement(false, false, false, true, MovementTile.MovementType.ROAD);//Cinderella Castle
		grid[4][24].setLeft(true);

		//And then others which are loops to prevent traffic jams
		//North Market
		grid[12][0].setMovement(false, true, false, false, MovementTile.MovementType.ROAD);
		grid[13][0].setMovement(false, false, true, false, MovementTile.MovementType.ROAD);
		grid[14][0].setMovement(false, false, true, false, MovementTile.MovementType.ROAD);
		grid[12][1].setMovement(false, true, false, false, MovementTile.MovementType.ROAD);
		grid[13][1].setMovement(false, false, false, true, MovementTile.MovementType.ROAD);
		grid[14][1].setMovement(false, false, false, true, MovementTile.MovementType.ROAD);
		grid[13][4].setUp(true);

		//Rancho
		grid[20][0].setMovement(false, true, false, false, MovementTile.MovementType.ROAD);
		grid[21][0].setMovement(false, false, true, false, MovementTile.MovementType.ROAD);
		grid[20][1].setMovement(false, true, false, false, MovementTile.MovementType.ROAD);
		grid[21][1].setMovement(true, false, false, false, MovementTile.MovementType.ROAD);
		grid[21][4].setUp(true);

		//Apt #1
		grid[24][0].setMovement(false, true, false, false, MovementTile.MovementType.ROAD);
		grid[25][0].setMovement(false, false, true, false, MovementTile.MovementType.ROAD);
		grid[24][1].setMovement(false, true, false, false, MovementTile.MovementType.ROAD);
		grid[25][1].setMovement(true, false, false, false, MovementTile.MovementType.ROAD);
		grid[25][4].setUp(true);

		//Apt #2
		grid[0][6].setMovement(false, true, false, false, MovementTile.MovementType.ROAD);
		grid[1][6].setMovement(false, false, true, false, MovementTile.MovementType.ROAD);
		grid[0][7].setMovement(false, false, false, true, MovementTile.MovementType.ROAD);
		grid[1][7].setMovement(false, false, false, true, MovementTile.MovementType.ROAD);
		grid[4][6].setLeft(true);

		//Bank
		grid[24][9].setMovement(false, true, false, false, MovementTile.MovementType.ROAD);
		grid[25][9].setMovement(false, false, true, false, MovementTile.MovementType.ROAD);
		grid[24][10].setMovement(false, true, false, false, MovementTile.MovementType.ROAD);
		grid[25][10].setMovement(true, false, false, false, MovementTile.MovementType.ROAD);
		grid[24][11].setMovement(false, true, false, false, MovementTile.MovementType.ROAD);
		grid[25][11].setMovement(true, false, false, false, MovementTile.MovementType.ROAD);
		grid[25][14].setUp(true);

		//Haus
		grid[32][10].setMovement(false, false, true, false, MovementTile.MovementType.ROAD);
		grid[33][10].setMovement(false, false, true, false, MovementTile.MovementType.ROAD);
		grid[32][11].setMovement(false, false, false, true, MovementTile.MovementType.ROAD);
		grid[33][11].setMovement(true, false, false, false, MovementTile.MovementType.ROAD);
		grid[29][11].setRight(true);

		//Pizza and Apt #4
		grid[10][18].setMovement(false, true, false, false, MovementTile.MovementType.ROAD);
		grid[11][18].setMovement(false, false, false, true, MovementTile.MovementType.ROAD);
		grid[10][19].setMovement(true, false, false, false, MovementTile.MovementType.ROAD);
		grid[11][19].setMovement(true, false, false, false, MovementTile.MovementType.ROAD);
		grid[10][15].setDown(true);

		//Southern Market
		grid[22][18].setMovement(false, true, false, false, MovementTile.MovementType.ROAD);
		grid[23][18].setMovement(false, false, false, true, MovementTile.MovementType.ROAD);
		grid[22][19].setMovement(true, false, false, false, MovementTile.MovementType.ROAD);
		grid[23][19].setMovement(true, false, false, false, MovementTile.MovementType.ROAD);

		//Apt #3 and Apt #5
		grid[0][19].setMovement(false, true, false, false, MovementTile.MovementType.ROAD);
		grid[1][19].setMovement(false, false, true, false, MovementTile.MovementType.ROAD);
		grid[0][20].setMovement(false, false, false, true, MovementTile.MovementType.ROAD);
		grid[1][20].setMovement(false, false, false, true, MovementTile.MovementType.ROAD);
		grid[4][19].setLeft(true);

		//Cafe
		grid[24][20].setMovement(false, true, false, false, MovementTile.MovementType.ROAD);
		grid[25][21].setMovement(false, false, true, false, MovementTile.MovementType.ROAD);
		grid[24][20].setMovement(false, true, false, false, MovementTile.MovementType.ROAD);
		grid[25][21].setMovement(true, false, false, false, MovementTile.MovementType.ROAD);
		grid[25][24].setUp(true);

		//Apt #6 and Apt #7
		grid[32][24].setMovement(false, false, true, false, MovementTile.MovementType.ROAD);
		grid[33][24].setMovement(false, false, true, false, MovementTile.MovementType.ROAD);
		grid[32][25].setMovement(false, false, false, true, MovementTile.MovementType.ROAD);
		grid[33][25].setMovement(true, false, false, false, MovementTile.MovementType.ROAD);
		grid[29][25].setRight(true);

		//Apt #8
		grid[4][28].setMovement(false, true, false, false, MovementTile.MovementType.ROAD);
		grid[5][28].setMovement(true, false, false, false, MovementTile.MovementType.ROAD);
		grid[4][29].setMovement(false, false, false, true, MovementTile.MovementType.ROAD);
		grid[5][29].setMovement(true, false, false, false, MovementTile.MovementType.ROAD);
		grid[4][25].setDown(true);

		//Apt #9 and Bayou
		grid[10][28].setMovement(false, true, false, false, MovementTile.MovementType.ROAD);
		grid[11][28].setMovement(true, false, false, false, MovementTile.MovementType.ROAD);
		grid[10][29].setMovement(false, false, false, true, MovementTile.MovementType.ROAD);
		grid[11][29].setMovement(true, false, false, false, MovementTile.MovementType.ROAD);
		grid[10][25].setDown(true);

		//Apt #10
		grid[22][28].setMovement(false, true, false, false, MovementTile.MovementType.ROAD);
		grid[23][28].setMovement(true, false, false, false, MovementTile.MovementType.ROAD);
		grid[22][29].setMovement(false, false, false, true, MovementTile.MovementType.ROAD);
		grid[23][29].setMovement(true, false, false, false, MovementTile.MovementType.ROAD);
		grid[22][25].setDown(true);

		//Apt #11
		grid[28][28].setMovement(false, true, false, false, MovementTile.MovementType.ROAD);
		grid[29][28].setMovement(true, false, false, false, MovementTile.MovementType.ROAD);
		grid[28][29].setMovement(false, false, false, true, MovementTile.MovementType.ROAD);
		grid[29][29].setMovement(true, false, false, false, MovementTile.MovementType.ROAD);
		grid[28][25].setDown(true);

		//Time for some CROSSWALKS
		//Houses first again since they're multidirectional and only two
		grid[9][2].setMovement(true, true, false, false, MovementTile.MovementType.CROSSWALK);
		grid[9][3].setMovement(true, true, false, false, MovementTile.MovementType.CROSSWALK);

		grid[30][6].setMovement(false, false, true, true, MovementTile.MovementType.CROSSWALK);
		grid[31][6].setMovement(false, false, true, true, MovementTile.MovementType.CROSSWALK);

		grid[2][10].setMovement(false, false, true, true, MovementTile.MovementType.CROSSWALK);
		grid[3][10].setMovement(false, false, true, true, MovementTile.MovementType.CROSSWALK);

		grid[30][19].setMovement(false, false, true, true, MovementTile.MovementType.CROSSWALK);
		grid[31][19].setMovement(false, false, true, true, MovementTile.MovementType.CROSSWALK);

		grid[11][22].setMovement(true, true, false, false, MovementTile.MovementType.CROSSWALK);
		grid[11][23].setMovement(true, true, false, false, MovementTile.MovementType.CROSSWALK);

		grid[2][24].setMovement(false, false, true, true, MovementTile.MovementType.CROSSWALK);
		grid[3][24].setMovement(false, false, true, true, MovementTile.MovementType.CROSSWALK);

		//Square CrossWalks
		setCrossWalk(6, 4, true);
		setCrossWalk(14, 4, true);
		setCrossWalk(18, 4, true);
		setCrossWalk(26, 4, true);

		setCrossWalk(16, 6, false);

		setCrossWalk(4, 12, false);
		setCrossWalk(28, 12, false);

		setCrossWalk(6, 14, true);
		setCrossWalk(26, 14, true);

		setCrossWalk(4, 16, false);
		setCrossWalk(28, 16, false);

		setCrossWalk(16, 22, false);

		setCrossWalk(6, 24, true);
		setCrossWalk(14, 24, true);
		setCrossWalk(18, 24, true);
		setCrossWalk(26, 24, true);

		//Driveways that double as crosswalks
		setCrossWalk(12, 2, false);//Northern Market
		setCrossWalk(20, 2, false);//Rancho
		setCrossWalk(24, 2, false);//Apt #1

		setCrossWalk(2, 6, true);//Apt #2

		setCrossWalk(30, 10, true);//Haus

		setCrossWalk(24, 12, false);//Bank

		setCrossWalk(10, 18, false);//Pizza and Apt #4
		setCrossWalk(22, 18, false);//Southern Market

		setCrossWalk(19, 2, true);//Apt #3 and Apt #5

		setCrossWalk(24, 22, false);//Cafe

		setCrossWalk(30, 24, true);//Apt #6 and Apt #7

		setCrossWalk(4, 28, false);//Apt #8
		setCrossWalk(10, 28, false);//Apt #9 and Bayou
		setCrossWalk(22, 28, false);//Apt #10
		setCrossWalk(28, 28, false);//Apt #11

		//Traffic light
		trafficLight = new TrafficLight(new Position(16, 14), grid);
		master.addGui(trafficLight);

		//GoGo Pelipper trucks
		grid[18][1].setMovement(false, true, false, false, MovementTile.MovementType.FLYING);
		grid[20][21].setMovement(false, true, true, false, MovementTile.MovementType.FLYING);

		//grid[7][4].setMovement(false, true, true, false, MovementTile.MovementType.CROSSWALK);
		//grid[11][6].setMovement(true, false, true, false, MovementTile.MovementType.CROSSWALK);
		//grid[5][8].setMovement(false, false, false, true, MovementTile.MovementType.CROSSWALK);
		//+++++++++++++++++++++++END CREATION OF GRID+++++++++++++++++++++++

		//++++++++++++++++++++++BEGIN CREATION OF BUS STOPS++++++++++++++++++++++
		busStops = new ArrayList<BusStop>();

		BusStop tempBusStop = new BusStop("Bus Stop 0");//Top Left Bus Stop
		tempBusStop.associateWalkTile(new Position(11, 3));
		busStops.add(tempBusStop);
		grid[11][4].setBusStop(busStops.get(0));
		tempBusStop.addNearbyBuilding("Mickey's Market");
		tempBusStop.addNearbyBuilding("Tiki Hut");

		tempBusStop = new BusStop("Bus Stop 1");//Left Center Bus Stop
		tempBusStop.associateWalkTile(new Position(3, 11));
		busStops.add(tempBusStop);
		grid[4][11].setBusStop(busStops.get(1));
		tempBusStop.addNearbyBuilding("Main St Apartments #2");
		tempBusStop.addNearbyBuilding("Rabbit Hole");
		//		tempBusStop.addNearbyBuilding("Haunted Mansion");

		tempBusStop = new BusStop("Bus Stop 2");//Left Bottom Bus
		tempBusStop.associateWalkTile(new Position(3, 23));
		busStops.add(tempBusStop);
		grid[4][23].setBusStop(busStops.get(2));
		tempBusStop.addNearbyBuilding("Main St Apartments #3");
		tempBusStop.addNearbyBuilding("Main St Apartments #4");
		tempBusStop.addNearbyBuilding("Main St Apartments #5");
		tempBusStop.addNearbyBuilding("Main St Apartments #8");
		tempBusStop.addNearbyBuilding("Pizza Port");
		tempBusStop.addNearbyBuilding("Cinderella Castle");
		//		tempBusStop.addNearbyBuilding("Mickey's Market");

		tempBusStop = new BusStop("Bus Stop 3");//Bottom Left Bus
		tempBusStop.associateWalkTile(new Position(9, 26));
		busStops.add(tempBusStop);
		grid[9][25].setBusStop(busStops.get(3));
		tempBusStop.addNearbyBuilding("Main St Apartments #9");
		tempBusStop.addNearbyBuilding("The Blue Bayou");
		tempBusStop.addNearbyBuilding("Space Mountain");

		tempBusStop = new BusStop("Bus Stop 4");//Bottom Right Bus
		tempBusStop.associateWalkTile(new Position(24, 26));
		busStops.add(tempBusStop);
		grid[24][25].setBusStop(busStops.get(4));
		tempBusStop.addNearbyBuilding("Main St Apartments #10");
		tempBusStop.addNearbyBuilding("Main St Apartments #11");
		tempBusStop.addNearbyBuilding("Carnation Cafe");
		tempBusStop.addNearbyBuilding("Minnie's Market");


		tempBusStop = new BusStop("Bus Stop 5");//Center Right Bus
		tempBusStop.associateWalkTile(new Position(30, 18));
		busStops.add(tempBusStop);
		grid[29][18].setBusStop(busStops.get(5));
		tempBusStop.addNearbyBuilding("Main St Apartments #6");
		tempBusStop.addNearbyBuilding("Main St Apartments #7");
		tempBusStop.addNearbyBuilding("Pirate's Suite");

		tempBusStop = new BusStop("Bus Stop 6");//Right Top Bus
		tempBusStop.associateWalkTile(new Position(30, 8));
		busStops.add(tempBusStop);
		grid[29][8].setBusStop(busStops.get(6));
		tempBusStop.addNearbyBuilding("Pirate Bank");
		tempBusStop.addNearbyBuilding("Village Haus");
		tempBusStop.addNearbyBuilding("Haunted Mansion");

		tempBusStop = new BusStop("Bus Stop 7");//Top Right Bus
		tempBusStop.associateWalkTile(new Position(23, 3));
		busStops.add(tempBusStop);
		grid[23][4].setBusStop(busStops.get(7));
		tempBusStop.addNearbyBuilding("Main St Apartments #1");
		tempBusStop.addNearbyBuilding("Rancho Del Zocalo");
		//+++++++++++++++++++++++END CREATION OF BUS STOPS+++++++++++++++++++++++

		//++++++++++++++++++++++BEGIN CREATION OF DIRECTORY++++++++++++++++++++++
		directory = new HashMap<String, Building>();

		Building tempBuilding = new Building("Main St Apartments #1", new Position(27, 2), new Position(25, 0), busStops.get(7));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Main St Apartments #2", new Position(2, 4), new Position(0, 6), busStops.get(1));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Main St Apartments #3", new Position(2, 17), new Position(0, 19), busStops.get(2));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Main St Apartments #4", new Position(13, 17), new Position(11, 19), busStops.get(2));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Main St Apartments #5", new Position(2, 21), new Position(0, 20), busStops.get(2));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Main St Apartments #6", new Position(31, 22), new Position(33, 24), busStops.get(5));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Main St Apartments #7", new Position(31, 27), new Position(33, 25), busStops.get(5));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Main St Apartments #8", new Position(2, 27), new Position(4, 29), busStops.get(2));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Main St Apartments #9", new Position(8, 27), new Position(10, 29), busStops.get(3));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Main St Apartments #10", new Position(20, 27), new Position(22, 29), busStops.get(4));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Main St Apartments #11", new Position(26, 27), new Position(28, 29), busStops.get(4));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Haunted Mansion", new Position(31, 4), new Position(32, 6), busStops.get(6));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Tiki Hut", new Position(7, 2), new Position(9, 1), busStops.get(0));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Rabbit Hole", new Position(2, 12), new Position(1, 10), busStops.get(1));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Cinderella Castle", new Position(2, 26), new Position(1, 24), busStops.get(2));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Space Mountain", new Position(9, 22), new Position(11, 21), busStops.get(3));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Pirate's Suite", new Position(31, 17), new Position(32, 19), busStops.get(5));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Mickey's Market", new Position(16, 2), new Position(14, 1), busStops.get(0));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Minnie's Market", new Position(19, 19), new Position(21, 19), busStops.get(4));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Pirate Bank", new Position(21, 12), new Position(24, 11), busStops.get(6));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Rancho Del Zocalo", new Position(22, 2), new Position(21, 0), busStops.get(7));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Carnation Cafe", new Position(22, 22), new Position(24, 20), busStops.get(4));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("The Blue Bayou", new Position(13, 27), new Position(11, 29), busStops.get(4));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Pizza Port", new Position(8, 17), new Position(10, 19), busStops.get(3));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Village Haus", new Position(31, 9), new Position(33, 10), busStops.get(6));//hacked 33, 10
		directory.put(tempBuilding.name, tempBuilding);

		tempBuilding = new Building("Bus Stop 0", new Position(11, 3), new Position(11, 4), busStops.get(0));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Bus Stop 1", new Position(3, 11), new Position(4, 11), busStops.get(1));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Bus Stop 2", new Position(3, 23), new Position(4, 23), busStops.get(2));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Bus Stop 3", new Position(9, 26), new Position(9, 25), busStops.get(3));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Bus Stop 4", new Position(24, 26), new Position(24, 25), busStops.get(4));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Bus Stop 5", new Position(30, 18), new Position(29, 18), busStops.get(5));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Bus Stop 6", new Position(30, 8), new Position(29, 8), busStops.get(6));
		directory.put(tempBuilding.name, tempBuilding);
		tempBuilding = new Building("Bus Stop 7", new Position(23, 3), new Position(23, 4), busStops.get(7));
		directory.put(tempBuilding.name, tempBuilding);
		//+++++++++++++++++++++++END CREATION OF DIRECTORY+++++++++++++++++++++++
		//Spawning Buses
		buses = new ArrayList<BusAgent>();

		BusAgent tempBus = new BusAgent(this, new Position(4, 4));
		BusGui busGui = new BusGui(4, 4, tempBus);
		if(master != null)
			master.addGui(busGui);
		tempBus.setGui(busGui);
		tempBus.startThread();
		buses.add(tempBus);


		tempBus = new BusAgent(this, new Position(29, 25));
		busGui = new BusGui(29, 25, tempBus);
		if(master != null)
			master.addGui(busGui);
		tempBus.setGui(busGui);
		tempBus.startThread();
		buses.add(tempBus);


		//Spawning Delivery Truck
		truckMickey = new TruckAgent(new Position(17, 2), this, new FlyingTraversal(grid), 18, 1);
		TruckGui truckGui = new TruckGui(17, 2, truckMickey);
		if(master != null)
			master.addGui(truckGui);
		truckMickey.setGui(truckGui);
		truckMickey.startThread();
		
		truckMinnie = new TruckAgent(new Position(19, 20), this, new FlyingTraversal(grid), 20, 21);
		truckGui = new TruckGui(19, 20, truckMinnie);
		if(master != null)
			master.addGui(truckGui);
		truckMinnie.setGui(truckGui);
		truckMinnie.startThread();

		if(master != null)
			super.startThread();
	}

	private void setCrossWalk(int x, int y, boolean horizontal) {
		if(horizontal) {
			grid[x][y].setMovement(false, false, true, true, MovementType.CROSSWALK);
			grid[x+1][y].setMovement(false, false, true, true, MovementType.CROSSWALK);
			grid[x][y+1].setMovement(false, false, true, true, MovementType.CROSSWALK);
			grid[x+1][y+1].setMovement(false, false, true, true, MovementType.CROSSWALK);
		}

		else {
			grid[x][y].setMovement(true, true, false, false, MovementType.CROSSWALK);
			grid[x+1][y].setMovement(true, true, false, false, MovementType.CROSSWALK);
			grid[x][y+1].setMovement(true, true, false, false, MovementType.CROSSWALK);
			grid[x+1][y+1].setMovement(true, true, false, false, MovementType.CROSSWALK);
		}

	}

	//+++++++++++++++++MESSAGES+++++++++++++++++
	public void msgWantToGo(String startLocation, String endLocation, Person person, String mover, String character) {
		log.add(new LoggedEvent("Received transportation request from " + person.getName()));
		for(Mover m : movingObjects) {
			if(m.person == person && !m.method.equals("Bus"))
				return;
		}

		System.out.println("RECEIVED REQUEST TO TRANSPORT");

		movingObjects.add(new Mover(person, startLocation, endLocation, mover, character));
		stateChanged();
	}

	public void msgArrivedAtDestination(Person person){
		log.add(new LoggedEvent(person.getName() + ": Person reached destination"));
		for(Mover mover : movingObjects) {
			if(mover.person == person) {
				mover.transportationState = TransportationState.DESTINATION;
			}
		}
		stateChanged();
	}

	public void msgSendDelivery(Restaurant restaurant, Market market, String food, int quantity, int id) {
		if(market.getName().equals("Mickey's Market"))
			truckMickey.msgDeliverOrder(restaurant, market, food, quantity, id);
		else
			truckMinnie.msgDeliverOrder(restaurant, market, food, quantity, id);
	}

	public void msgSendDelivery(Person person, Market market, String food, int quantity, String location) {
		if(market.getName().equals("Mickey's Market"))
			truckMickey.msgDeliverOrder(person, market, food, quantity, location);
		else
			truckMinnie.msgDeliverOrder(person, market, food, quantity, location);
	}

	public void msgCrash(MobileAgent agent1, MobileAgent agent2, Position position) {
		
	}

	//+++++++++++++++++SCHEDULER+++++++++++++++++
	@Override
	public boolean pickAndExecuteAnAction() {
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
		//TransportationTraversal aStar = new TransportationTraversal(grid);
		if(mover.method.equals("Car")){
			log.add(new LoggedEvent("Spawning Car"));
			if(grid[directory.get(mover.startingLocation).vehicleTile.getX()][directory.get(mover.startingLocation).vehicleTile.getY()].tryAcquire()) {
				CarTraversal aStar = new CarTraversal(grid);
				mover.transportationState = TransportationState.MOVING;
				CarAgent driver = new CarAgent(mover.person, directory.get(mover.startingLocation).vehicleTile, directory.get(mover.endingLocation).vehicleTile, this, aStar);
				CarGui carGui = new CarGui(directory.get(mover.startingLocation).vehicleTile.getX(), directory.get(mover.startingLocation).vehicleTile.getY(), driver);
				if(master != null)
					master.addGui(carGui);
				driver.setGui(carGui);
				driver.startThread();
				AlertLog.getInstance().logMessage(AlertTag.TRANSPORTATION, "Transportation Controller", "Spawning Car for " + mover.person.getName() + ".");
			}
			else {
				mover.transportationState = TransportationState.WAITINGTOSPAWN;
			}
		}
		else if(mover.method.equals("Walk")){
			log.add(new LoggedEvent("Spawning Walker"));
			if(grid[directory.get(mover.startingLocation).walkingTile.getX()][directory.get(mover.startingLocation).walkingTile.getY()].tryAcquire()) {
				WalkerTraversal aStar = new WalkerTraversal(grid);
				mover.transportationState = TransportationState.MOVING;
				WalkerAgent walker = new WalkerAgent(mover.person, directory.get(mover.startingLocation).walkingTile, directory.get(mover.endingLocation).walkingTile, this, aStar);
				WalkerGui walkerGui = new WalkerGui(directory.get(mover.startingLocation).walkingTile.getX(), directory.get(mover.startingLocation).walkingTile.getY(), walker);
				if(master != null)
					master.addGui(walkerGui);
				walker.setGui(walkerGui);
				walker.startThread();
				AlertLog.getInstance().logMessage(AlertTag.TRANSPORTATION, "Transportation Controller", "Spawning Walker for " + mover.person.getName() + ".");
			}
			else {
				mover.transportationState = TransportationState.WAITINGTOSPAWN;
				log.add(new LoggedEvent("Failed to spawn Walker"));
			}
		}
		else if(mover.method.equals("Bus")){
			log.add(new LoggedEvent("Spawning Bus"));
			//find bus stop and spawn walker to go to bus stop
			mover.transportationState = TransportationState.MOVING;
			if(directory.get(mover.startingLocation).closestBusStop == directory.get(mover.endingLocation).closestBusStop) {
				mover.method = "Walk";
				spawnMover(mover);
				return;
			}
			if(easierToWalk(directory.get(mover.startingLocation).walkingTile, directory.get(mover.endingLocation).walkingTile, directory.get(mover.startingLocation).closestBusStop.getAssociatedTile(), directory.get(mover.endingLocation).closestBusStop.getAssociatedTile())) {
				mover.method = "Walk";
				spawnMover(mover);
				return;
			}
			if(grid[directory.get(mover.startingLocation).walkingTile.getX()][directory.get(mover.startingLocation).walkingTile.getY()].tryAcquire()) {
				WalkerTraversal aStar = new WalkerTraversal(grid);
				WalkerAgent busWalker = new WalkerAgent(mover.person, directory.get(mover.startingLocation).walkingTile, directory.get(mover.endingLocation).walkingTile, this, aStar, directory.get(mover.startingLocation).closestBusStop, directory.get(mover.endingLocation).closestBusStop, mover.endingLocation);
				WalkerGui busWalkerGui = new WalkerGui(directory.get(mover.startingLocation).walkingTile.getX(), directory.get(mover.startingLocation).walkingTile.getY(), busWalker);
				if(master != null)
					master.addGui(busWalkerGui);
				busWalker.setGui(busWalkerGui);
				busWalker.startThread();
				AlertLog.getInstance().logMessage(AlertTag.TRANSPORTATION, "Transportation Controller", "Spawning Bus Walker for " + mover.person.getName() + ".");
			}
			else {
				mover.transportationState = TransportationState.WAITINGTOSPAWN;
			}
		}
	}

	private boolean easierToWalk(Position start, Position end, Position startStop, Position endStop) {
		float walkingDistance = 0.00f;
		float busDistance = 0.00f;

		walkingDistance += abs((start.getY() - end.getY()));
		walkingDistance += abs((start.getX() - end.getX()));

		busDistance += abs((start.getX() - startStop.getX()));
		busDistance += abs((start.getY() - startStop.getY()));
		busDistance += abs((end.getX() - endStop.getX()));
		busDistance += abs((end.getY() - endStop.getY()));

		//iterate through a generic bus route and find how long the bus would require
		//Multiply it by 0.25 to encourage bus taking.
		Queue<Position> route = new LinkedList<Position>();
		createRoute(route);

		while(route.peek().getX() != startStop.getX() && route.peek().getY() != startStop.getY()) {
			route.add(route.poll());
		}

		while(route.peek().getX() != endStop.getX() && route.peek().getY() != endStop.getY()) {
			route.add(route.poll());
			busDistance += 0.25;
		}

		if(walkingDistance <= busDistance)
			return true;
		return false;
	}

	private int abs(int integer) {
		if(integer < 0) {
			return -integer;
		}
		return integer;
	}

	public void createRoute(Queue<Position> route) {//Hack for one route
		//SPAWN BUS AT {4, 4}
		for(int i = 5; i <= 25; i++) {
			route.add(new Position(4, i));
		}
		for(int i = 5; i <= 29; i++) {
			route.add(new Position(i, 25));
		}
		for(int i = 24; i>=4; i--) {
			route.add(new Position(29, i));
		}
		for(int i = 28; i >= 4; i--) {
			route.add(new Position(i, 4));
		}
	}

	private void despawnMover(Mover mover) {
		log.add(new LoggedEvent("Deleting mover"));
		if(!mover.method.equals("Bus")) {
			mover.person.msgReachedDestination(mover.endingLocation);
			AlertLog.getInstance().logMessage(AlertTag.TRANSPORTATION, "Transportation Controller", mover.person.getName() + " reached their destination.");
		}
		movingObjects.remove(mover);
	}

	private void retrySpawn(Mover mover) {
		log.add(new LoggedEvent("Resetting mover state"));
		mover.transportationState = TransportationState.REQUEST;
	}

	public MovementTile[][] getGrid() {
		return grid;
	}

	public void msgPayFare(Person person, float fare) {
		buses.get(0).msgPayFare(person, fare);
		buses.get(1).msgPayFare(person, fare);
	}
}
