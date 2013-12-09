package restaurant_pizza.gui;

import restaurant_pizza.CookAgent;
import simcity.interfaces.Person;

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
    private static final int xPlatingArea = 280, yPlatingArea = 90;
    private static final int xFridge = 290, yFridge = 50;
    private static final int xCookingArea = 310, yCookingArea = 90;
    // TODO: Show the plates as text with a new area
    private static final int xCookingAreaText = 300, yCookingAreaText = 100;
	public static final int mySize = 20;
	private boolean leaving = false;
	Person person;
	
    public CookGui(CookAgent agent) {
        this.agent = agent;
        xPos = xPlatingArea;
        yPos = yPlatingArea;
        xDestination = xPlatingArea;
        yDestination = yPlatingArea;
        bi = Toolkit.getDefaultToolkit().getImage("res/cook.gif");
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
        if (xPos == xDestination && yPos == yDestination && leaving) {
        	person.msgStopWork(10);
        	leaving =false; 
        }
        if (xPos == xDestination && yPos == yDestination && readyToMove) {
        	readyToMove = false;
        	command = Command.noCommand;
        	agent.msgAtDestination();
        }
    }

    public void draw(Graphics2D g) {
        g.drawImage(bi, xPos, yPos, mySize*2, mySize*2, null);
        g.setColor(Color.ORANGE);
        g.setFont(new Font(null, Font.PLAIN, 12));
        if (orderStatus == "") {
        	g.drawString(orderStatus, xPos, yPos);
        }
        else {
        	g.drawString(orderStatus.substring(0,3), xPos-40, yPos);
        }
        g.setFont(new Font(null, Font.PLAIN, 12));
        if (cookingOrders.length() >4) g.drawString(cookingOrders.substring(1,4), xCookingAreaText + 50, yCookingAreaText);
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
    
    public void DoLeave(Person p) {
    	xDestination = -50;
    	yDestination = -50; 
    	person = p;
    	leaving = true;
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
