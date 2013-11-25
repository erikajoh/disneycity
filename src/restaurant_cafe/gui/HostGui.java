package restaurant_cafe.gui;


import restaurant_cafe.CustomerAgent;
import restaurant_cafe.HostAgent;

import java.awt.*;

public class HostGui implements Gui {

    private HostAgent agent = null;

    private int xPos = -20, yPos = -20;//default waiter position
    private int xDestination = -20, yDestination = -20;//default start position

    private int tableX = 200, tableY = 250; //default table position

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

        if (xPos == xDestination && yPos == yDestination
        		& (xDestination == tableX + 20) & (yDestination == tableY - 20)) {
           agent.msgAtTable();
        }
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.MAGENTA);
        g.fillRect(xPos, yPos, 20, 20);
    }

    public boolean isPresent() {
        return true;
    }

    public void DoBringToTable(CustomerAgent customer, int tableNumber) {
    	TableList tableList = new TableList();
    	Point tablePoint = tableList.getTablePoint(tableNumber);
    	tableX = tablePoint.x;
    	tableY = tablePoint.y;
        xDestination = tableX + 20;
        yDestination = tableY - 20;
    }

    public void DoLeaveCustomer() {
        xDestination = -20;
        yDestination = -20;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
    public int getTableXPos() {
        return tableX;
    }
    public int getTableYPos() {
        return tableY;
    }
}
