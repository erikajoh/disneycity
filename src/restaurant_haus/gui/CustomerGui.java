package restaurant_haus.gui;

import restaurant_haus.CustomerAgent;
import restaurant_haus.HostAgent;
import simcity.gui.SimCityGui;
import AnimationTools.AnimationModule;


import java.awt.*;

public class CustomerGui implements Gui{

	private CustomerAgent agent = null;
	private boolean isPresent = false;
	private boolean isHungry = false;

	//private HostAgent host;
	SimCityGui gui;
	private int xPos, yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand, GoToSeat, LeaveRestaurant};
	private Command command=Command.noCommand;
	AnimationModule animModule = new AnimationModule();
	
	private enum FoodState {Waiting, Received, NoFood};
	private FoodState foodState = FoodState.NoFood;
	private String food = "";

	private final int size = 20;
	
	public CustomerGui(CustomerAgent c, SimCityGui gui){ //HostAgent m) {
		agent = c;
		xPos = -40;
		yPos = -40;
		xDestination = -40;
		yDestination = -40;
		//maitreD = m;
		this.gui = gui;
	}

	public void updatePosition() {
		if (xPos < xDestination)
			xPos++;
		else if (xPos > xDestination)
			xPos--;

		if (yPos < yDestination)
			yPos++;
		else if (yPos > yDestination)
			yPos--;

		if (xPos == xDestination && yPos == yDestination) {
			if (command==Command.GoToSeat) agent.msgAnimationFinishedGoToSeat();
			else if (command==Command.LeaveRestaurant) {
				agent.msgAnimationFinishedLeaveRestaurant();
				//System.out.println("about to call gui.setCustomerEnabled(agent);");
				isHungry = false;
			//	gui.setCustomerEnabled(agent);
			}
			command=Command.noCommand;
			animModule.setStill();
		}
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.GREEN);
		//g.fillRect(xPos, yPos, size, size);
		 animModule.updateAnimation();//updates the frame and animation 
		 g.drawImage(animModule.getImage(), (int)xPos, (int)yPos, null);
		if(foodState == FoodState.Received) {
			g.setColor(Color.MAGENTA);
			g.fillRect(xPos + size, yPos, size, size);
			g.setColor(Color.BLACK);
			g.drawString(food, xPos + size, yPos + (int)(size * .5));
		}
		g.setColor(Color.BLACK);
		if(foodState == FoodState.Waiting) {
			g.drawString(food + "?", xPos + size, yPos + (int)(size * .5));
		}
	}

	public boolean isPresent() {
		return isPresent;
	}
	public void setHungry() {
		isHungry = true;
		agent.gotHungry();
		setPresent(true);
	}
	public void setNotHungry() {
		isHungry = false;
		setPresent(false);
	}
	public boolean isHungry() {
		return isHungry;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}

	public void DoGoToSeat(Point p) {//later you will map seatnumber to table coordinates.
		
		xDestination = (int)p.getX();
		yDestination = (int)p.getY();
		command = Command.GoToSeat;
		/*
		xDestination = xTable;
		yDestination = yTable;
		command = Command.GoToSeat;
		*/
	}
	
	public void GoToHost() {
		xDestination = 30;
		yDestination = 20;
	}
	
	public void DoExitRestaurant() {
		xDestination = -40;
		yDestination = -40;
		command = Command.LeaveRestaurant;
		foodState = FoodState.NoFood;
	}
	
	public void waitingFor(String choice) {
		this.food = choice.substring(0, 2);
		foodState = FoodState.Waiting;
	}
	
	public void receivedFood() {
		foodState = FoodState.Received;
	}
}
