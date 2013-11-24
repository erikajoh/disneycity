package transportation.Agents;

import java.awt.*;
import java.util.*;
import java.util.List;
import transportation.Objects.*;
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
	}

	List<Mover> movingObjects;

	class Building {
		String name;
		List <MovementTile> enteringTiles;

		public Building(String name) {
			this.name  = name;
			enteringTiles = new ArrayList<MovementTile>();
		}

		public void addTile(MovementTile enterTile) {
			enteringTiles.add(enterTile);
		}
	}

	Map<String, Building> directory;

	public TransportationController() {
		movingObjects = new ArrayList<Mover>();
		directory = new HashMap<String, Building>();
	}

	@Override
	protected boolean pickAndExecuteAnAction() {
		synchronized(movingObjects) {
			for(Mover mover : movingObjects) {
				if(mover.transportationState == TransportationState.REQUEST) {
					SpawnVehicle(mover);
					return true;
				}
			}
		}
		
		synchronized(movingObjects) {
			for(Mover mover : movingObjects) {
				if(mover.transportationState == TransportationState.DESTINATION) {
					
				}
			}
		}
		
		synchronized(movingObjects) {
			for(Mover mover : movingObjects) {
				if(mover.transportationState == TransportationState.WAITINGTOSPAWN) {
					
				}
			}
		}
		return false;
	}
}
