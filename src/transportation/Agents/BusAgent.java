package transportation.Agents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Semaphore;

import astar.astar.Position;
import simcity.PersonAgent;
import transportation.GUIs.BusGui;
import transportation.Objects.BusRider;
import transportation.Objects.BusStop;
import transportation.Objects.MovementTile;

public class BusAgent extends MobileAgent{

	private final float fare = 0.00f;
	private float collectedFare = 0.00f;
	private Position currentPosition;
	private List<BusRider> busRiders;
	private Queue<Position> route;
	BusStop currentBusStop;
	BusGui gui;
	Semaphore animSem;

	TransportationController master;

	public BusAgent(TransportationController master) {
		collectedFare = 0;
		currentBusStop = null;
		busRiders = Collections.synchronizedList(new ArrayList<BusRider>());
		route = new LinkedList<Position>();
		this.master = master;
		currentPosition = new Position(4, 4);
		try {
			master.getGrid()[currentPosition.getX()][currentPosition.getY()].acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		animSem = new Semaphore(0, true);
		createRoute();
		
	}

	//+++++++++++++++++MESSAGES+++++++++++++++++
	public void msgPayFare(PersonAgent person, float fare) {
		for(BusRider busRider : busRiders) {
			if(busRider.getPerson() == person) {
				busRider.state = BusRider.RiderState.RIDING;
				collectedFare += fare;
				stateChanged();
			}
		}
	}

	public void msgReachedBusStop(BusStop busStop) {
		currentBusStop = busStop;
		stateChanged();
	}
	
	public void msgHalfway() {//Releases semaphore at halfway point to prevent sprites from colliding majorly
		if(master.getGrid()[currentPosition.getX()][currentPosition.getY()].availablePermits() == 0)
			master.getGrid()[currentPosition.getX()][currentPosition.getY()].release();
		//System.out.println(String.valueOf(master.getGrid()[currentPosition.getX()][currentPosition.getY()].availablePermits()));
	}

	public void msgDestination() {
		animSem.release();
	}
	
	//+++++++++++++++++SCHEDULER+++++++++++++++++
	@Override
	protected boolean pickAndExecuteAnAction() {
		if(currentBusStop != null) {
			dropOffRiders();
			pickUpRiders();
		}

		synchronized(busRiders) {
			for(BusRider busRider : busRiders) {
				if(busRider.state == BusRider.RiderState.HASTOPAY) {
					gui.setStill();
					return false;
				}
			}
		}

		moveToNextBusStop();
		return true;
	}

	//+++++++++++++++++ACTIONS+++++++++++++++++
	private void dropOffRiders() {
		for(BusRider busRider : busRiders) {
			if(busRider.getFinalStop() == currentBusStop){
				master.msgWantToGo(currentBusStop.getName(), busRider.getDestination(), busRider.getPerson(), "Walk", "Edgar");
				//send request to spawn walker to transportation
				busRider.state = BusRider.RiderState.GOTOFF;
			}
		}
	}

	private void pickUpRiders() {
		List<BusRider> tempList = currentBusStop.getBusWaiters();
		for(BusRider busRider : tempList) {
			System.out.println("gotRider");
			busRiders.add(busRider);
			busRider.getPerson().msgPayFare(fare);
			busRider.state = BusRider.RiderState.HASTOPAY;
			//msgPayFare(busRider.getPerson(), fare);
			//busRider.state = BusRider.RiderState.RIDING;
		}
		currentBusStop.clearRiders();
	}

	//Also removes BusRiders that aren't on the bus anymore
	private void moveToNextBusStop() {
		synchronized(busRiders) {
			for(int i = busRiders.size() - 1 ; i >= 0; i--) {
				if(busRiders.get(i).state == BusRider.RiderState.GOTOFF)
					busRiders.remove(i);
			}
		}

		MovementTile[][] grid = master.grid;
		while(true) {
			//currentBusStop = null;
			Position nextPosition = route.poll();
			route.add(nextPosition);//bus must be able to loop
			try {
				grid[nextPosition.getX()][nextPosition.getY()].acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			gui.setDestination(nextPosition.getX(), nextPosition.getY());
			try {
				animSem.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			currentPosition = nextPosition;
			currentBusStop = grid[nextPosition.getX()][nextPosition.getY()].getBusStop();
			if(grid[nextPosition.getX()][nextPosition.getY()].getBusStop() != null) {
				break;
			}
		}

		//gui code to go to next bus stop
		//pop next position from queue
		//add position to end of queue
		//move to tile
	}

	public void createRoute() {//Hack for one route
		//SPAWN BUS AT {4, 4}
		for(int i = 5; i <= 8; i++) {
			route.add(new Position(4, i));
		}
		for(int i = 5; i <= 11; i++) {
			route.add(new Position(i, 8));
		}
		for(int i = 7; i>=4; i--) {
			route.add(new Position(11, i));
		}
		for(int i = 10; i >= 4; i--) {
			route.add(new Position(i, 4));
		}
	}

	public void createRoute(Queue<Position> route) {
		this.route = route;
	}

	public void setGui(BusGui gui) {
		this.gui = gui;
	}
	
	@Override
	public String getType() {
		return "bus";
	}
}
