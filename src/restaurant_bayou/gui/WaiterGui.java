 package restaurant_bayou.gui;


import restaurant_bayou.CustomerAgent;
import restaurant_bayou.WaiterAgent;
import simcity.gui.SimCityGui;

import java.awt.*;

public class WaiterGui implements Gui {

    private WaiterAgent agent = null;
    private String text = "";
    
    private SimCityGui gui;

    public static final int xTable = 100;
    public static final int yTable = 250;
    
    private int xHome, yHome, xPos, yPos, xDestination, yDestination;//default start position
    
    private int table = xTable;
    
    public boolean leaving = false;
    public boolean atTable = false;

    public WaiterGui(WaiterAgent agent, SimCityGui gui, int numWaiters) {
        this.agent = agent;
        this.gui = gui;
        xHome = xTable/20+(numWaiters*xTable/5);
        yHome = yTable/7;
        xPos = xHome;
        yPos = yHome;
        xDestination = xHome;
        yDestination = yHome;
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

        if (xPos == xDestination && yPos == yDestination){
        	if ((xDestination == xTable*table + xTable/10) && (yDestination == yTable - yTable/12)) {
        		agent.msgAtTable();
        		atTable = true;
        	} else if (leaving && xDestination == xTable*13/2-6*xTable/5 && yDestination == 11*yTable/12-3*xTable/2) {
        		leaving = false;
        		agent.msgAtTable();
        	} else if (leaving) {
        		leaving = false;
        		atTable = false;
        		agent.msgWaiterReady();
        	}
    	}
             
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.GREEN);
        g.fillRect(xPos, yPos, xTable/6, yTable/12);
//        Image img = Toolkit.getDefaultToolkit().getImage("host.jpg");
//		g.drawImage(img, xPos, yPos, yTable/12, yTable/12, null);
        g.setColor(Color.GRAY);
		if (text == null) text = "";
		g.drawString(text, xPos, yPos);
    }

    public boolean isPresent() {
        return true;
    }

    public void DoGoToTable(int seatnumber) {
    	table = seatnumber;
        xDestination = xTable*seatnumber + xTable/10;
        yDestination = yTable - yTable/12;
    }
    
    public void DoGoGetFood() {
    	xDestination = xTable*13/2-6*xTable/5;
    	yDestination = 11*yTable/12-3*xTable/2;
    	leaving = true;
    }

    public void DoLeaveCustomer() {
        xDestination = xHome;
        yDestination = yHome;
        leaving = true;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
    
    public void setText(String t) {
    	text = t;
    }
    
   // public void setEnabled() {
  //  	gui.setEnabled(agent);
   // }
}
