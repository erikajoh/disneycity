package restaurant_pizza.gui;

import restaurant_pizza.WaiterAgent;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;

public class WaiterGui implements Gui {

    private WaiterAgent agent = null;
    private boolean readyToMove = false;
    private boolean onBreak = false;
	private String orderStatus = "";

    public enum Command {noCommand, seatCustomer, goToEntrance, goToTable, goToCook, goToCashier};
    public Command command = Command.noCommand;

	private Image bi;
	
    private int xPos = -mySize*2, yPos = -mySize*2; //default waiter position
    private int xDestination, yDestination; //default start position
    private static final int xCook = 250, yCook = 125;
    private static final int xCashier = 400, yCashier = 600;
    private static final int xEntrance = 0, yEntrance = 0;
	public static final int mySize = 20;
	
	public int xHomeDestination, yHomeDestination;
	
    public WaiterGui(WaiterAgent agent, int aXStart, int aYStart) {
        this.agent = agent;
        xPos = aXStart;
        yPos = aYStart;
        xDestination = aXStart;
        yDestination = aYStart;
        xHomeDestination = aXStart;
        yHomeDestination = aYStart;
        bi = Toolkit.getDefaultToolkit().getImage("res/waiter.png");
		
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

        if (xPos == xDestination && yPos == yDestination && readyToMove) {
        	readyToMove = false;
        	command = Command.noCommand;
        	agent.msgAtDestination();
        }
    }

    public void draw(Graphics2D g) {
        g.drawImage(bi, xPos, yPos, mySize*2, mySize*2, null);
        g.setColor(Color.RED );
        g.setFont(new Font(null, Font.PLAIN, 18));
        g.drawString(orderStatus, xPos, yPos);
    }

    public boolean isPresent() {
        return true;
    }
    
    public void setReadyToMove(boolean b) {
    	readyToMove = b;
    }

    public void setGoOnBreak() {
    	onBreak = true;
    	agent.msgNeedBreak();
    }
    
    public void setBackToWork() {
    	System.out.println("setBackToWork() called");
    	onBreak = false;
    	agent.msgGetOffBreak();
    }
    
    public boolean isOnBreak() {
    	return onBreak;
    }
    
    public void DoGoToTable(int tableNumber) {
        xDestination = xTable + mySize + (tableSpacing*(tableNumber));
        yDestination = yTable - mySize;
        command = Command.goToTable;
        readyToMove = true;
    }

    public void DoGoToHomePosition() {
    	xDestination = xHomeDestination;
        yDestination = yHomeDestination;
        command = Command.goToEntrance;
        readyToMove = true;
    }
    
    public void DoGoToEntrance() {
    	xDestination = xEntrance;
        yDestination = yEntrance;
        command = Command.goToEntrance;
        readyToMove = true;
    }
    
    public void DoGoToCook() {
    	xDestination = xCook;
        yDestination = yCook;
        command = Command.goToCook;
        readyToMove = true;
    }

	public void DoGoToCashier() {
		xDestination = xCashier;
        yDestination = yCashier;
        command = Command.goToCashier;
        readyToMove = true;
	}
    
    public void DoLeaveCustomer() {
        xDestination = xHomeDestination;
        yDestination = yHomeDestination;
        readyToMove = true;
    }
    
    public void DoDisplayOrder(String aOrderStatus) {
		this.orderStatus = aOrderStatus;
	}

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
}