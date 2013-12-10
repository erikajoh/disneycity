package restaurant_haus.gui;


import restaurant_haus.CustomerAgent;
import restaurant_haus.WaiterAgent;
import simcity.gui.SimCityGui;
import simcity.interfaces.Person;

import java.awt.*;

public class WaiterGui implements Gui {
	
    private WaiterAgent agent = null;
    private SimCityGui gui;
    
    boolean atPosition = false;
    private Person person;

    private int xPos = -20, yPos = -20;//default waiter position
    private int xHome = 0, yHome = 0;
    private int xDestination = -20, yDestination = -20;//default start position

    public static final int xTable = 100;
    public static final int yTable = 100;
    
    private enum STATE {NOFOOD, FOOD, RETURNING};
    STATE state = STATE.NOFOOD;
    String carrying = "";
    
    public final int waiterSize = 20;
    private boolean leaving = false;

    public WaiterGui(WaiterAgent agent, SimCityGui g) {
    	super();
        this.agent = agent;
        gui = g;
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

        if (xPos == xDestination && yPos == yDestination && leaving == true) {
        	person.msgStopWork(10);
        	leaving = false;
        }
        if (xPos == xDestination && yPos == yDestination && !atPosition){
           agent.msgAtDestination();
           atPosition = true;
        }
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.CYAN);
        g.fillRect(xPos, yPos, waiterSize, waiterSize);
        
        if(state == STATE.FOOD) {
        	g.setColor(Color.MAGENTA);
        	g.fillRect(xPos, yPos + waiterSize, waiterSize, waiterSize);
        	g.setColor(Color.BLACK);
        	g.drawString(carrying, xPos, yPos + (int)(waiterSize * 1.5));
        }
    }

    public boolean isPresent() {
        return true;
    }
    
    public void DoLeave(Person p) {
    	xDestination = -50;
    	yDestination = -50; 
    	person = p;
    	leaving = true;
    }

    /*
    public void DoBringToTable(CustomerAgent customer, int xPos, int yPos) {
        xDestination = xPos + hostSize;
        yDestination = yPos - hostSize;
        TablePosX = xDestination;
        TablePosY = yDestination;
    }
    */
    
    public void setHome(int xHome, int yHome) {
    	this.xHome = xHome;
    	this.yHome = yHome;
    }
    
    public void msgBreakOver() {
    	//gui.setWaiterEnabled(agent);
    }
    
    public void GoToTable(Point tableLocation) {
    	state = STATE.NOFOOD;
    	xDestination = (int)tableLocation.getX() + waiterSize;
        yDestination = (int)tableLocation.getY() - waiterSize;
        atPosition = false;
    }
    
    public void GoToCustomers() {
    	xDestination = 50;
    	yDestination = 0;
    	atPosition = false;
    	
    }
    
    public void GoToCook() {
    	xDestination = 255;
        yDestination = 115;
        atPosition = false;
    }
    
    public void GoToOrder() {
    	xDestination = 255;
        yDestination = 125;
        atPosition = false;
    }
    
    public void GoToHome() {
    	xDestination = xHome;
    	yDestination = yHome;
    }
    
    public void GoToCashier() {
    	xDestination = xPos+1;
    	yDestination = yPos+1;
    	atPosition = false;
    }
    
    public void LeaveFood() {
    	state = STATE.NOFOOD;
    }
    
    public void CarryingFood(String food) {
    	carrying = food.substring(0,2);
    	state = STATE.FOOD;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }

	public void GoToStand() {
		xDestination = 255;
        yDestination = 70;
        atPosition = false;
	}
}
