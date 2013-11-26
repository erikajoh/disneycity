package transportation.Agents;

import java.util.List;
import java.util.concurrent.Semaphore;

import astar.astar.AStarNode;
import astar.astar.Position;
import simcity.PersonAgent;
import transportation.GUIs.WalkerGui;
import transportation.Objects.*;

public class WalkerAgent extends MobileAgent{

	PersonAgent walker;
	Position currentPosition;
	Position endPosition;
	TransportationController master;
	WalkerGui gui;
	boolean arrived;
	TransportationTraversal aStar;
	
	Semaphore animSem;
	BusStop busStop;
	String building;

	public WalkerAgent(PersonAgent walker, Position currentPosition, Position endPosition, TransportationController master, TransportationTraversal aStar) {
		this.walker = walker;
		this.currentPosition = currentPosition;
		this.endPosition = endPosition;
		this.master = master;
		arrived = false;
		this.aStar = aStar;
		
		animSem = new Semaphore(0, true);
		busStop = null;
		building = null;
	}
	
	public WalkerAgent(PersonAgent walker, Position currentPosition, Position endPosition, TransportationController master, TransportationTraversal aStar, BusStop busStop, String building) {
		this.walker = walker;
		this.currentPosition = currentPosition;
		this.endPosition = endPosition;
		this.master = master;
		arrived = false;
		this.aStar = aStar;
		
		animSem = new Semaphore(0, true);
		this.busStop= busStop;
		this.building = building;
	}
	
	public void msgHalfway() {//Releases semaphore at halfway point to prevent sprites from colliding majorly
		if(master.getGrid()[currentPosition.getX()][currentPosition.getY()].availablePermits() == 0)
			master.getGrid()[currentPosition.getX()][currentPosition.getY()].release();
		//System.out.println(String.valueOf(master.getGrid()[currentPosition.getX()][currentPosition.getY()].availablePermits()));
	}

	public void msgDestination() {
		animSem.release();
	}

	//Remember to release semaphores to tiles when despawning
	@Override
	protected boolean pickAndExecuteAnAction() {
		if(!arrived) {
			goToEndPosition();
		}
		if(arrived) {
			tauntAndLeave();
		}
		return false;
	}

	public void goToEndPosition() {
		AStarNode aStarNode = (AStarNode)aStar.generalSearch(currentPosition, endPosition);
		List<Position> path = aStarNode.getPath();
		Boolean firstStep   = true;
		Boolean gotPermit   = true;

		for (Position tmpPath: path) {
			//The first node in the path is the current node. So skip it.
			if (firstStep) {
				firstStep   = false;
				continue;
			}

			//Try and get lock for the next step.
			int attempts    = 1;
			gotPermit       = new Position(tmpPath.getX(), tmpPath.getY()).moveInto(aStar.getGrid());
			
			//Did not get lock. Lets make n attempts.
			while (!gotPermit && attempts < 3) {
				//System.out.println("[Gaut] " + guiWaiter.getName() + " got NO permit for " + tmpPath.toString() + " on attempt " + attempts);

				//Wait for 1sec and try again to get lock.
				try { Thread.sleep(1000); }
				catch (Exception e){}

				gotPermit   = new Position(tmpPath.getX(), tmpPath.getY()).moveInto(aStar.getGrid());
				attempts ++;
			}

			//Did not get lock after trying n attempts. So recalculating path.            
			if (!gotPermit) {
				//System.out.println("[Gaut] " + guiWaiter.getName() + " No Luck even after " + attempts + " attempts! Lets recalculate");
				goToEndPosition();
				break;
			}

			//Got the required lock. Lets move.
			//System.out.println("[Gaut] " + guiWaiter.getName() + " got permit for " + tmpPath.toString());
			//currentPosition.release(aStar.getGrid());
			gui.setDestination(tmpPath.getX(), tmpPath.getY());
			try {
				animSem.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			currentPosition = new Position(tmpPath.getX(), tmpPath.getY ());
		}
		
		arrived = true;
	}

	public void tauntAndLeave() {
		gui.doTauntAndLeave();
		try {
			animSem.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(busStop == null) {
			master.msgArrivedAtDestination(walker);
			System.out.println(String.valueOf(master.grid[currentPosition.getX()][currentPosition.getY()].availablePermits()));
		}
		else
			master.grid[currentPosition.getX()][currentPosition.getY()].getBusStop().addRider(walker, busStop, building);
		gui.setIgnore();
		stopThread();
	}

	public void setGui (WalkerGui gui) {
		this.gui = gui;
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return "walker";
	}

}
