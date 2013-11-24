package restaurant_bayou.gui;


import restaurant_bayou.CustomerAgent;
import restaurant_bayou.HostAgent;

import java.awt.*;

public class CashierGui implements Gui {

    private HostAgent agent = null;

    public static final int xTable = 100;
    public static final int yTable = 250;
    
    private int xPos = -20, yPos = -20;//default cashier position
    private int xDestination = xTable*9/2, yDestination = yTable/12;//start cashier position
        
//    public boolean leaving = false;

    public CashierGui(HostAgent agent) {
        this.agent = agent;
    }

    public void updatePosition() {
   
    	xPos = xDestination;
    	yPos = yDestination;
    	
    }
    	
//    	if (xPos < xDestination)
//            xPos++;
//        else if (xPos > xDestination)
//            xPos--;
//
//        if (yPos < yDestination)
//            yPos++;
//        else if (yPos > yDestination)
//            yPos--;
//
//        if (xPos == xDestination && yPos == yDestination){
//        	if ((xDestination == xTable-100 + xTable/10) & (yDestination == yTable - yTable/12)) {
////        		agent.msgAtTable();
//        	} 
//        	//else if (leaving) {
//        		//leaving = false;
////        		agent.msgHostReady();
//        	}
//    	}
             

    public void draw(Graphics2D g) {
        g.setColor(Color.GRAY);
        g.fillRect(xPos, yPos, xTable/2, yTable/12);
//      Image img = Toolkit.getDefaultToolkit().getImage("host.jpg");
//		g.drawImage(img, xPos, yPos, yTable/12, yTable/12, null);
        g.drawString("cashier", xPos, yPos);
    }

    public boolean isPresent() {
        return true;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
}
