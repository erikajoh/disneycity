package transportation.Agents;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;

import javax.swing.Timer;

import market.Market;
import astar.astar.AStarNode;
import astar.astar.Position;
import simcity.Restaurant;
import simcity.gui.trace.AlertLog;
import simcity.gui.trace.AlertTag;
import simcity.interfaces.Person;
import transportation.GUIs.TruckGui;
import transportation.GUIs.WalkerGui;
import transportation.Objects.*;

public class TruckAgent extends MobileAgent{

	Position currentPosition;
	Position marketPosition;
	Position deliveryPosition;
	Position homePosition;
	TransportationController master;
	TruckGui gui = null;
	FlyingTraversal aStar;
	Semaphore animSem;

	enum Status {
		WAITING,
		DELIVERING,
		DELIVERED,
		RESTCLOSED
	}

	class deliveryOrder implements ActionListener{
		String food;
		int quantity;
		Restaurant restaurant;
		Person person;
		Market market;
		int ID;
		String location;
		Status status;
		TruckAgent truck;
		Timer timer;


		deliveryOrder(Restaurant restaurant, String food, int quantity, Market market, int ID, TruckAgent truck) {
			timer = new Timer(30000, this);
			this.restaurant = restaurant;
			this.food = food;
			this.quantity = quantity;
			person = null;
			this.market = market;
			this.ID = ID;
			status = Status.WAITING;
			this.truck = truck;
			stateChanged();
		}

		deliveryOrder(Person person, String food, int quantity, Market market, String location, TruckAgent truck) {
			timer = new Timer(30000, this);
			this.restaurant = null;
			this.food = food;
			this.quantity = quantity;
			this.person = person;
			this.market = market;
			this.location = location;
			status = Status.WAITING;
			this.truck = truck;
			stateChanged();
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			timer.stop();
			AlertLog.getInstance().logMessage(AlertTag.TRANSPORTATION, "Truck", "Retrying delivery for: " + restaurant.getRestaurantName());
			status = Status.DELIVERING;
		}
		
		public void startTimer() {
			timer.start();
		}
		//		public boolean returnType() {
		//			if(person != null && restaurant == null)
		//				return true;
		//			if(person == null && restaurant != null)
		//				return false;
		//			return false;
		//		}
	}

	List<deliveryOrder> orders;

	public TruckAgent(Position marketPosition, TransportationController master, FlyingTraversal aStar, int homeX, int homeY) {
		this.currentPosition = marketPosition;
		this.marketPosition = marketPosition;
		this.master = master;
		homePosition = new Position(homeX, homeY);

		animSem = new Semaphore(0, true);
		this.aStar = aStar;

		orders = Collections.synchronizedList(new ArrayList<deliveryOrder>());
	}

	public void msgDeliverOrder(Restaurant restaurant, Market market, String food, int quantity, int ID) {
		orders.add(new deliveryOrder(restaurant, food, quantity, market, ID, this));
		stateChanged();
	}

	public void msgDeliverOrder(Person person, Market market, String food, int quantity, String location) {
		orders.add(new deliveryOrder(person, food, quantity, market, location, this));
		stateChanged();
	}

	public void msgHalfway() {//Releases semaphore at halfway point to prevent sprites from colliding majorly
		if(master.getGrid()[currentPosition.getX()][currentPosition.getY()].availablePermits() == 0)
			master.getGrid()[currentPosition.getX()][currentPosition.getY()].release();
		//System.out.println(String.valueOf(master.getGrid()[currentPosition.getX()][currentPosition.getY()].availablePermits()));
	}

	public void msgDestination() {
		if(animSem.availablePermits() == 0) {
			animSem.release();
		}
	}

	//Remember to release semaphores to tiles when despawning
	@Override
	protected boolean pickAndExecuteAnAction() {
		synchronized(orders) {
			for(deliveryOrder order : orders) {
				if(order.status == Status.DELIVERING) {
					deliverOrder(order);
					return true;
				}
			}
		}

		synchronized(orders) {
			for(deliveryOrder order : orders) {
				if(order.status == Status.WAITING) {
					pickUpOrders();
					return true;
				}
			}
		}

		synchronized(orders) {
			for(deliveryOrder order : orders) {
				if(order.status == Status.DELIVERED) {
					goToPosition(marketPosition, null);
					deleteOrder(order);
					return true;
				}
			}
		}

		idle();
		return false;
	}

	public void goToPosition(Position goal, Position ignore) {
		AStarNode aStarNode = (AStarNode)aStar.generalSearch(currentPosition, goal, ignore);
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
				if(tmpPath == goal)
					goToPosition(goal, null);
				else
					goToPosition(goal, tmpPath);
				break;
			}

			//Got the required lock. Lets move.
			//			System.out.println("[Gaut] " + guiWaiter.getName() + " got permit for " + tmpPath.toString());
			//			currentPosition.release(aStar.getGrid());
			gui.setDestination(tmpPath.getX(), tmpPath.getY());
			try {
				animSem.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			currentPosition = new Position(tmpPath.getX(), tmpPath.getY ());
		}
	}

	private void deliverOrder(deliveryOrder order) {
		if(order.person != null) {//person order
			goToPosition(master.directory.get(order.location).walkingTile, null);
			order.person.msgHereIsOrder(order.food, order.quantity);
			order.status = Status.DELIVERED;
		}
		else if(order.restaurant != null) {//Restaurant order
			goToPosition(master.directory.get(order.restaurant.getRestaurantName()).walkingTile, null);
			if(order.restaurant.isOpen()) {
				order.restaurant.msgHereIsOrder(order.food, order.quantity, order.ID);
				order.status = Status.DELIVERED;
				AlertLog.getInstance().logMessage(AlertTag.TRANSPORTATION, "Truck", "Finished delivery for: " + order.restaurant.getRestaurantName());
			}
			else {
				order.status = Status.RESTCLOSED;
				order.startTimer();
				AlertLog.getInstance().logMessage(AlertTag.TRANSPORTATION, "Truck", "Failed delivery for: " + order.restaurant.getRestaurantName());
			}
		}
		gui.doDeliveryDance();
		try {
			animSem.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void pickUpOrders() {
		goToPosition(marketPosition, null);
		for(deliveryOrder order : orders) {
			order.status = Status.DELIVERING;
			stateChanged();
		}
	}

	private void deleteOrder(deliveryOrder order) {
		orders.remove(order);
	}

	private void idle() {
		goToPosition(new Position (homePosition.getX(), homePosition.getY()), null);
		gui.doIdle();
	}

	public void setGui (TruckGui gui) {
		this.gui = gui;
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return "Truck";
	}
}
