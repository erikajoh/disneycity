package transportation.Agents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;

import market.Market;
import astar.astar.AStarNode;
import astar.astar.Position;
import simcity.PersonAgent;
import simcity.Restaurant;
import transportation.GUIs.TruckGui;
import transportation.GUIs.WalkerGui;
import transportation.Objects.*;

public class TruckAgent extends MobileAgent{
	
	Position currentPosition;
	Position marketPosition;
	Position deliveryPosition;
	TransportationController master;
	TruckGui gui = null;
	FlyingTraversal aStar;
	Semaphore animSem;
	
	enum Status {
		WAITING,
		DELIVERING,
		DELIVERED
	}
	
	class deliveryOrder {
		String food;
		int quantity;
		Restaurant restaurant;
		PersonAgent person;
		Market market;
		int ID;
		String location;
		Status status;
		
		
		deliveryOrder(Restaurant restaurant, String food, int quantity, Market market, int ID) {
			this.restaurant = restaurant;
			this.food = food;
			this.quantity = quantity;
			person = null;
			this.market = market;
			this.ID = ID;
			status = Status.WAITING;
			stateChanged();
		}
		
		deliveryOrder(PersonAgent person, String food, int quantity, Market market, String location) {
			this.restaurant = null;
			this.food = food;
			this.quantity = quantity;
			this.person = person;
			this.market = market;
			this.location = location;
			status = Status.WAITING;
			stateChanged();
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
	
	public TruckAgent(Position marketPosition, TransportationController master, FlyingTraversal aStar) {
		this.currentPosition = marketPosition;
		this.marketPosition = marketPosition;
		this.master = master;
		
		animSem = new Semaphore(0, true);
		this.aStar = aStar;
		
		orders = Collections.synchronizedList(new ArrayList<deliveryOrder>());
	}
	
	public void msgDeliverOrder(Restaurant restaurant, Market market, String food, int quantity, int ID) {
		orders.add(new deliveryOrder(restaurant, food, quantity, market, ID));
		stateChanged();
	}
	
	public void msgDeliverOrder(PersonAgent person, Market market, String food, int quantity, String location) {
		orders.add(new deliveryOrder(person, food, quantity, market, location));
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
					pickUpOrders();
					deleteOrder(order);
					return true;
				}
			}
		}
		
		idle();
		return false;
	}

	public void goToPosition(Position goal) {
		AStarNode aStarNode = (AStarNode)aStar.generalSearch(currentPosition, goal);
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
				goToPosition(goal);
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
			goToPosition(master.directory.get(order.location).vehicleTile);
		}
		else if(order.restaurant != null) {//Restaurant order
			goToPosition(master.directory.get(order.restaurant.getRestaurantName()).vehicleTile);
		}
//		try {
//			animSem.acquire();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		if (order.person != null) order.person.msgHereIsOrder(order.food, order.quantity);
		else if (order.restaurant != null) order.restaurant.msgHereIsOrder(order.food, order.quantity, order.ID);
		order.status = Status.DELIVERED;
		gui.doDeliveryDance();
//		try {
//			animSem.acquire();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	private void pickUpOrders() {
		goToPosition(marketPosition);
//		try {
//			animSem.acquire();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		for(deliveryOrder order : orders) {
			order.status = Status.DELIVERING;
			stateChanged();
		}
	}
	
	private void deleteOrder(deliveryOrder order) {
		orders.remove(order);
	}
	
	private void idle() {
		goToPosition(new Position (11, 11));
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
