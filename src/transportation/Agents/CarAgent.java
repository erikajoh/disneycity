package transportation.Agents;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;

import agent.Agent;
import astar.astar.AStarNode;
import astar.astar.Position;
import simcity.interfaces.Person;
import transportation.GUIs.CarGui;
import transportation.GUIs.WalkerGui;
import transportation.Objects.*;
import transportation.Objects.MovementTile.MovementType;

public class CarAgent extends MobileAgent{

	Person driver;
	Position currentPosition;
	Position endPosition;
	TransportationController master;
	boolean arrived;
	CarGui gui = null;
	CarTraversal aStar;
	Semaphore animSem;

	Position crashPosition = null;
	Position nextPosition = null;

	int crashChance = 1;

	public CarAgent(Person person, Position currentPosition, Position endPosition, TransportationController master, CarTraversal aStar, boolean crashAll) {
		this.driver = person;
		this.currentPosition = currentPosition;
		this.endPosition = endPosition;
		this.master = master;
		arrived = false;

		animSem = new Semaphore(0, true);
		this.aStar = aStar;

		if(crashAll)
			crashChance = 100;
	}
	public void msgHalfway() {//Releases semaphore at halfway point to prevent sprites from colliding majorly
		master.getGrid()[currentPosition.getX()][currentPosition.getY()].removeOccupant(this);
		if(master.getGrid()[currentPosition.getX()][currentPosition.getY()].availablePermits() == 0) {
			master.getGrid()[currentPosition.getX()][currentPosition.getY()].release();
			//			master.getGrid()[currentPosition.getX()][currentPosition.getY()].removeOccupant(this);
		}
		//System.out.println(String.valueOf(master.getGrid()[currentPosition.getX()][currentPosition.getY()].availablePermits()));
	}

	public void crashRemoval(boolean reachedHalfway, boolean pedestrian){ 
		master.getGrid()[currentPosition.getX()][currentPosition.getY()].removeOccupant(this);
		master.getGrid()[nextPosition.getX()][nextPosition.getY()].removeOccupant(this);
		if(!pedestrian) {
			if(!(crashPosition.getX() == currentPosition.getX() && crashPosition.getY() == currentPosition.getY())) {//we're the cars causing the crash
				//no need to release previous position until later
				//we didn't acquire the semaphore either. Nothing to be done for now

			}
			if(!(crashPosition.getX() == nextPosition.getX() && crashPosition.getY() == nextPosition.getY())) {//we're the cars getting hit
				//Release the next position since we're not using it
				master.getGrid()[nextPosition.getX()][nextPosition.getY()].release();
			}
		}
		else {//Pedestrian Collision
			//release as if halfway since reachedHalfway is getting set to true
			master.getGrid()[currentPosition.getX()][currentPosition.getY()].release();
		}
	}

	public void crashDone() {
		master.getGrid()[currentPosition.getX()][currentPosition.getY()].release();
		gui.crashDone();
	}

	public void msgDestination() {
		animSem.release();
	}

	public void crash(Position position, boolean pedestrian) {//if it hits a pedestrian we need to not release the semaphore of the crash.
		crashPosition = position;
		gui.crash(pedestrian);
	}

	//Remember to release semaphores to tiles when despawning
	@Override
	protected boolean pickAndExecuteAnAction() {
		if(!arrived) {
			goToEndPosition(null);
		}
		if(arrived) {
			tauntAndLeave();
		}
		return false;
	}

	public void goToEndPosition(Position ignore) {
		AStarNode aStarNode = (AStarNode)aStar.generalSearch(currentPosition, endPosition, ignore);
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
			MovementTile.MovementType temp = master.getGrid()[tmpPath.getX()][tmpPath.getY()].getMovementType();
			MovementTile.MovementType currentTile = master.getGrid()[currentPosition.getX()][currentPosition.getY()].getMovementType();
			while(temp == MovementTile.MovementType.TRAFFICCROSSWALK || temp == MovementTile.MovementType.TRAFFICCROSSNONE) {
				if(currentTile == MovementTile.MovementType.TRAFFICCROSSWALK || currentTile == MovementTile.MovementType.TRAFFICCROSSNONE || currentTile == MovementTile.MovementType.TRAFFICCROSSINTERSECTION)
					break;
				try { Thread.sleep(1000); }
				catch (Exception e){}
				temp = master.getGrid()[tmpPath.getX()][tmpPath.getY()].getMovementType();
			}
			gotPermit = new Position(tmpPath.getX(), tmpPath.getY()).moveInto(aStar.getGrid());
			Random random = new Random();
			int randomInt = random.nextInt(100);
			if(!gotPermit && randomInt <= crashChance) {
				gotPermit = true;
				//				if(aStar.getGrid()[tmpPath.getX()][tmpPath.getY()].availablePermits() == 0)
				//					aStar.getGrid()[tmpPath.getX()][tmpPath.getY()].release();
			}
			//Did not get lock. Lets make n attempts.
			while (!gotPermit && attempts < 3) {
				//System.out.println("[Gaut] " + guiWaiter.getName() + " got NO permit for " + tmpPath.toString() + " on attempt " + attempts);

				//Wait for 1sec and try again to get lock.
				try { Thread.sleep(1000); }
				catch (Exception e){}
				while(temp == MovementTile.MovementType.TRAFFICCROSSWALK || temp == MovementTile.MovementType.TRAFFICCROSSNONE) {
					if(currentTile == MovementTile.MovementType.TRAFFICCROSSWALK || currentTile == MovementTile.MovementType.TRAFFICCROSSNONE || currentTile == MovementTile.MovementType.TRAFFICCROSSINTERSECTION)
						break;
					try { Thread.sleep(1000); }
					catch (Exception e){}
					temp = master.getGrid()[tmpPath.getX()][tmpPath.getY()].getMovementType();
				}
				gotPermit   = new Position(tmpPath.getX(), tmpPath.getY()).moveInto(aStar.getGrid());
				attempts ++;
			}

			randomInt = random.nextInt(100);
			//Did not get lock after trying n attempts. So recalculating path.            
			if (!gotPermit) {
				//System.out.println("[Gaut] " + guiWaiter.getName() + " No Luck even after " + attempts + " attempts! Lets recalculate");
				if(tmpPath.getX() == endPosition.getX() && tmpPath.getY() == endPosition.getY())
					goToEndPosition(null);
				else
					goToEndPosition(tmpPath);
				break;
			}
			//Got the required lock. Lets move.
			//System.out.println("[Gaut] " + guiWaiter.getName() + " got permit for " + tmpPath.toString());
			//currentPosition.release(aStar.getGrid());
			nextPosition = tmpPath;
			master.getGrid()[tmpPath.getX()][tmpPath.getY()].addOccupant(this);
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
		master.getGrid()[currentPosition.getX()][currentPosition.getY()].removeOccupant(this);
		if(master.grid[currentPosition.getX()][currentPosition.getY()].availablePermits() == 0) {
			master.grid[currentPosition.getX()][currentPosition.getY()].release();
			//			master.getGrid()[currentPosition.getX()][currentPosition.getY()].removeOccupant(this);
		}
		master.msgArrivedAtDestination(driver);
		gui.setIgnore();
		stopThread();
	}

	public void setGui (CarGui gui) {
		this.gui = gui;
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return "car";
	}

	@Override
	public Person getPerson() {
		return driver;
	}
}
