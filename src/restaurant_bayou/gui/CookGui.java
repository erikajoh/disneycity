package restaurant_bayou.gui;


import restaurant_bayou.CookAgent;
import restaurant_bayou.HostAgent;

import java.awt.*;

public class CookGui implements Gui {

    private CookAgent agent = null;
    private HostAgent host = null;
    
    public static final int xTable = 100;
    public static final int yTable = 250;
    
    private boolean getting, cooking, plating, goingHome;
    
    private int xHome, yHome, xPos, yPos, xDestination, yDestination;//default start position
    
    private int table = xTable;
    
    public boolean leaving = false;
    public boolean atTable = false;

    public CookGui(HostAgent agent) {
        this.host = agent;
        xHome = xTable*13/2;
        yHome = 11*yTable/12;
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
        	if (getting && xDestination == xHome-10 && yDestination == yHome+xTable){
        		getting = false;
        		agent.msgCookReady();
        	} else if (cooking && xDestination == xHome-20 && yDestination == yHome-20){
        		cooking = false;
        		agent.msgCookReady();
        	} else if (plating && xDestination == xHome-10 && yDestination == yHome-2*xTable){
        		plating = false;
        		agent.msgCookReady();
        	} else if (goingHome && xDestination == xHome && yDestination == yHome){
        		goingHome = false;
        		agent.msgCookReady();
        	}
    	}
             
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.MAGENTA);
        g.fillRect(xPos, yPos, xTable/4, yTable/12);
//        Image img = Toolkit.getDefaultToolkit().getImage("host.jpg");
//		g.drawImage(img, xPos, yPos, yTable/12, yTable/12, null);
        g.setColor(Color.GRAY);
//		if (text == null) text = "";
		g.drawString("cook", xPos, yPos);
    }

    public boolean isPresent() {
        return true;
    }

    public void DoGetIngredients() {
    	// go to fridge area
    	xDestination = xHome-10;
    	yDestination = yHome+xTable;
    	getting = true;
    }
    
    public void DoCookFood() {
    	// go to cooking area
        xDestination = xHome-20;
        yDestination = yHome-20;
        cooking = true;
    }
    
    public void DoPlateFood() {
    	xDestination = xHome-10;
    	yDestination = yHome-2*xTable;
    	plating = true;
    }
    
    public void DoGoHome() {
    	xDestination = xHome;
    	yDestination = yHome;
    	goingHome = true;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
    
    public void setAgent(CookAgent cook) {
    	agent = cook;
    }
   
}
