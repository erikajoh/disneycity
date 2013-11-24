package restaurant_rancho.gui;


import restaurant_rancho.CustomerAgent;
import restaurant_rancho.WaiterAgent;

import java.awt.*;

public class WaiterGui implements Gui {

    private WaiterAgent agent = null;

    private int xPos, yPos, xDestination, yDestination;
  //  private int xPos = -30, yPos = -30;//default waiter position
   // private int xDestination = -30, yDestination = -30;//default start position
    boolean actionInProgress= false;
    int waiterNum;

    public static final int xTable1 = (RanchoAnimationPanel.WINDOWX*2)/11;
	public static final int yTable1 = (RanchoAnimationPanel.WINDOWY*6)/10;
	public static final int xTable2 = (RanchoAnimationPanel.WINDOWX*5)/11;
	public static final int yTable2 = (RanchoAnimationPanel.WINDOWY*6)/10;;
	public static final int xTable3 = (RanchoAnimationPanel.WINDOWX*8)/11; 
	public static final int yTable3 = (RanchoAnimationPanel.WINDOWY*6)/10;
	
	String waiterText = "";

    public WaiterGui(WaiterAgent agent, int num) {
        this.agent = agent;
        waiterNum = num;
        xPos = -30; 
        yPos = -30;
        xDestination = 150 + (waiterNum%5)*50;
        yDestination = 300 - (waiterNum/5)*60;
        actionInProgress=true;
    }

    public void updatePosition() {
      if (actionInProgress) {
        if (xPos < xDestination)
            xPos++;
        else if (xPos > xDestination)
            xPos--;

        if (yPos < yDestination)
            yPos++;
        else if (yPos > yDestination)
            yPos--;

        if (xPos == xDestination && yPos == yDestination) {
        	agent.msgAtTable();
        	actionInProgress = false;
        	
        }
      }
    }

    public void draw(Graphics2D g) {
    	
    	g.setColor(Color.red);
    	g.setFont(new Font("helvetica", Font.BOLD, 16));
    	Image hostImage = Toolkit.getDefaultToolkit().getImage("res/host.png");
		g.drawImage(hostImage, xPos+0, yPos+0, 50, 50, null);
		g.drawString(waiterText, xPos+10, yPos+40);
		g.finalize();
    }

    public void setText(String wText) {
    	waiterText = wText;
    }
    
    public boolean isPresent() {
        return true;
    }
    public void DoWalkToCust(int loc) {
    	xDestination = 75 + ((loc%15)%5)*40;
		yDestination = 155 - ((loc%15)/5)*40;
    	actionInProgress = true;
    }
    public void DoGoToCook() {
    	xDestination = 280;
    	yDestination = 210;
    	actionInProgress=true;
    }
    public void DoPickUpFood(int plateLoc) {
    	if (plateLoc == 0) {
    		xDestination = 280;
    		yDestination = 170;
    	}
    	else if (plateLoc == 1) {
    		xDestination = 280;
    		yDestination = 210;
    		
    	}
    	else if (plateLoc == 2) {
    		xDestination = 280;
    		yDestination = 250;
    	}
    	
    	actionInProgress = true;
    }
    public void DoBringToTable(int tableNum) {
    	
        if (tableNum==1) {
        	xDestination = xTable1 + 20;
        	yDestination = yTable1 - 20; 
        }
        else if (tableNum==2) {
        	xDestination = xTable2 + 20;
        	yDestination = yTable2 - 20;
        }
        else if (tableNum==3) {
        	xDestination = xTable3 + 20;
        	yDestination = yTable3 - 20;
        }
        actionInProgress = true;
    
    }

    public void DoLeaveTable() {
    	actionInProgress = true;
        xDestination = 150 + (waiterNum%5)*50;
        yDestination = 300 - (waiterNum/5)*60;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
}
