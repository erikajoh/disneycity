package restaurant_pizza.gui;

import restaurant_pizza.CustomerAgent;
import simcity.gui.SimCityGui;

import java.awt.*;

public class CustomerGui implements Gui{

	private CustomerAgent agent = null;
	private boolean isPresent = false;
	private boolean isHungry = false;
	private String orderStatus = "";

	SimCityGui gui;

	private int xPos, yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand, GoToWaitingArea, GoToSeat, LeaveRestaurant, GoToCashier};
	private Command command=Command.noCommand;

    private static final int xCashier = 400, yCashier = 600;
    private static final int X_HOME_DESTINATION = -40;
    private static final int Y_HOME_DESTINATION = -40;
    
    public static final int mySize = 25;
    
    private int xWaitingArea = mySize;
    private int yWaitingArea = mySize;
    private final int waitingAreaOffset = 10;

	public CustomerGui(CustomerAgent c, SimCityGui gui){
		agent = c;
		xPos = X_HOME_DESTINATION;
		yPos = Y_HOME_DESTINATION;
		xDestination = X_HOME_DESTINATION;
		yDestination = Y_HOME_DESTINATION;
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
			if (command==Command.GoToSeat) { agent.msgAnimationFinishedGoToSeat(); }
			else if (command==Command.GoToCashier) { agent.msgAnimationFinishedGoToCashier(); }
			else if (command==Command.LeaveRestaurant) {
				agent.msgAnimationFinishedLeaveRestaurant();
				isHungry = false;
				//gui.setEnabled(agent);
			}
			command=Command.noCommand;
		}
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.GREEN);
		g.fillRect(xPos, yPos, mySize, mySize);
		g.setFont(new Font(null, Font.PLAIN, 18));
		g.drawString(orderStatus, xPos, yPos);
	}

	public boolean isPresent() {
		return isPresent;
	}
	public void setHungry() {
		isHungry = true;
		agent.msgGotHungry();
		setPresent(true);
	}
	public boolean isHungry() {
		return isHungry;
	}
	
	public void setOffsetWaitingArea(int ind) {
		System.out.println("ind = " + ind);
		xWaitingArea = mySize + (ind*waitingAreaOffset);
		yWaitingArea = mySize + (ind*waitingAreaOffset);
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}

	public void DoGoToWaitingArea() {
		xDestination = xWaitingArea;
        yDestination = yWaitingArea;
		command = Command.GoToWaitingArea;
	}
	
	public void DoGoToSeat(int tableNumber) {
		xDestination = xTable + (tableSpacing*(tableNumber));
        yDestination = yTable;
		command = Command.GoToSeat;
	}

	public void DoDisplayOrder(String aOrderStatus) {
		this.orderStatus = aOrderStatus;
	}
	
	public void DoGoToCashier() {
		xDestination = xCashier;
        yDestination = yCashier;
		command = Command.GoToCashier;
	}
	
	public void DoExitRestaurant() {
		xDestination = X_HOME_DESTINATION;
		yDestination = Y_HOME_DESTINATION;
		command = Command.LeaveRestaurant;
	}
}
