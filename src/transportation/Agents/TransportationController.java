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
			case "car":
				//spawn car
				break;
			
			case  "walk":
				//spawn walker
				break;
			
			case "bus":
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
