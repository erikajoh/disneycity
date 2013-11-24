package restaurant_pizza.gui;

import restaurant_pizza.CookAgent;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;

public class CookGui implements Gui {

    private CookAgent agent = null;
    private boolean readyToMove = false;
	private String orderStatus = "";
	private String cookingOrders = "";

    public enum Command {noCommand, goToFridge, goToCookingArea, goToPlatingArea};
    public Command command = Command.noCommand;

	private Image bi;
	
    private int xPos = -mySize*2, yPos = -mySize*2; //default waiter position
    private int xDestination, yDestination; //default start position
    private static final int xPlatingArea = 250, yPlatingArea = 100;
    private static final int xFridge = 250, yFridge = 50;
    private static final int xCookingArea = 400, yCookingArea = 100;
    // TODO: Show the plates as text with a new area
    private static final int xCookingAreaText = 400, yCookingAreaText = 100;
	public static final int mySize = 20;
	
    public CookGui(CookAgent agent) {
        this.agent = agent;
        xPos = xPlatingArea;
        yPos = yPlatingArea;
        xDestination = xPlatingArea;
        yDestination = yPlatingArea;
        bi = Toolkit.getDefaultToolkit().getImage("res/customer.gif");

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
        g.setColor(Color.ORANGE);
        g.setFont(new Font(null, Font.PLAIN, 18));
        g.drawString(orderStatus, xPos, yPos);

        g.setFont(new Font(null, Font.PLAIN, 12));
        g.drawString(cookingOrders, xCookingAreaText, yCookingAreaText);
        //TODO Draw the plates on cooking area here
    }

    public boolean isPresent() {
        return true;
    }
    
    public void setReadyToMove(boolean b) {
    	readyToMove = b;
    }
    
    public void DoGoToPlatingArea() {
    	xDestination = xPlatingArea;
        yDestination = yPlatingArea;
        command = Command.goToPlatingArea;
        readyToMove = true;
    }
    
    public void DoGoToFridge() {
    	xDestination = xFridge;
        yDestination = yFridge;
        command = Command.goToFridge;
        readyToMove = true;
    }
    
    public void DoGoToCookingArea() {
    	xDestination = xCookingArea;
        yDestination = yCookingArea;
        command = Command.goToCookingArea;
        readyToMove = true;
    }
    
    public void DoDisplayOrder(String aOrderStatus) {
		this.orderStatus = aOrderStatus;
	}
    
    public void DoDisplayCookingOrders(String orders) {
    	System.out.println("CookGui will display: " + orders);
		this.cookingOrders = orders;
	}

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
}
