package restaurant_pizza.gui;
/*
package restaurant.gui;

import restaurant.CustomerAgent;
import restaurant.HostAgent;

import java.awt.*;
import java.util.Collection;

public class HostGui implements Gui {

    private HostAgent agent = null;
    private boolean isGoingToEntrance = false;

	public static final int mySize = 20;

    private int xPos = -mySize, yPos = -mySize; //default waiter position
    private int xDestination = -mySize, yDestination = -mySize; //default start position
    public static final int xExitDestination = -mySize;
	public static final int yExitDestination = -mySize;
    
    public HostGui(HostAgent agent) {
        this.agent = agent;
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

        if (xPos == xDestination && yPos == yDestination)
        		// & (xDestination == xTable + mySize) & (yDestination == yTable - mySize))
        {
        	Collection<HostAgent.Table> tables = agent.getTables();
        	for(int tableNum = 0; tableNum < tables.size(); tableNum++)
        	{
        		if(xDestination == xTable + mySize + (tableSpacing*(tableNum)) && yDestination == yTable - mySize)
        			agent.msgAtTable();
        	}
        	if(isGoingToEntrance)
        	{
        		isGoingToEntrance = false;
        		agent.msgAtTable();
        	}
        }
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.MAGENTA);
        g.fillRect(xPos, yPos, mySize, mySize);
    }

    public boolean isPresent() {
        return true;
    }

    public void DoBringToTable(CustomerAgent customer, int tableNumber) {
        xDestination = xTable + mySize + (tableSpacing*(tableNumber));
        yDestination = yTable - mySize;
    }

    public void DoLeaveCustomer() {
    	isGoingToEntrance = true;
        xDestination = xExitDestination;
        yDestination = yExitDestination;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
}
*/