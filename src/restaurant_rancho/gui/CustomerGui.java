package restaurant_rancho.gui;

import restaurant_rancho.CustomerAgent;
import restaurant_rancho.HostAgent;
import simcity.gui.SimCityGui;

import java.awt.*;

public class CustomerGui implements Gui{

	private CustomerAgent agent = null;
	private boolean isPresent = false;
	private boolean isHungry = false;

	//private HostAgent host;
	SimCityGui gui;

	private int xPos, yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand, GoToWaitingSpot, GoToSeat, LeaveRestaurant};
	private Command command=Command.noCommand;
	
	public static final int xTable1 = (RanchoAnimationPanel.WINDOWX*2)/11;
	public static final int yTable1 = (RanchoAnimationPanel.WINDOWY*6)/10;
	public static final int xTable2 = (RanchoAnimationPanel.WINDOWX*5)/11;
	public static final int yTable2 = (RanchoAnimationPanel.WINDOWY*6)/10;;
	public static final int xTable3 = (RanchoAnimationPanel.WINDOWX*8)/11; 
	public static final int yTable3 = (RanchoAnimationPanel.WINDOWY*6)/10;
	
	String custText = "";
	public int waitNum;
	
	
	public CustomerGui(CustomerAgent c, SimCityGui gui, int num){ //HostAgent m) {
		agent = c;
		xPos = -50;
		yPos = -50;
		command = Command.GoToWaitingSpot;
		xDestination = 50 + ((num%15)%5)*40;
		yDestination = 130 - ((num%15)/5)*40;
		//maitreD = m;
		waitNum = num;
		this.gui = gui;
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

		if (xPos == xDestination && yPos == yDestination) {
			if (command==Command.GoToSeat) {agent.msgAnimationFinishedGoToSeat(); }
			else if (command==Command.LeaveRestaurant) {
				agent.msgAnimationFinishedLeaveRestaurant();
				isHungry = false;
			}
			else if (command == Command.GoToWaitingSpot) {
				agent.msgAnimationFinishedGotToWaitingSpot();
			}
			command=Command.noCommand;
		}
	}

	public void draw(Graphics2D g) {
		
		g.setColor(Color.blue);
    	g.setFont(new Font("helvetica", Font.BOLD, 16));
		Image custImage = Toolkit.getDefaultToolkit().getImage("res/customer.gif");
		g.drawImage(custImage, xPos, yPos, 30, 30, null);
		g.drawString(custText, xPos+30, yPos+20);
		g.finalize();
	}
	
	public void setText(String cText) {
		custText = cText;
	}

	public boolean isPresent() {
		return isPresent;
	}
	
	
	public void setHungry() {
		//if (agent.host.hostGui.getXPos() == -20 && agent.host.hostGui.getYPos() == -20) {
		isHungry = true;
		agent.gotHungry();
		setPresent(true);
		//}
		
	}
	public boolean isHungry() {
		return isHungry;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}
	
	public void DoGoToRestaurant(int loc) {
		xDestination = 50 + ((loc%15)%5)*40;
		yDestination = 130 - ((loc%15)/5)*40;
	}
	
	public void DoGoToSeat(int seatnumber) {//later you will map seatnumber to table coordinates.
		if (seatnumber==1){
		    xDestination = xTable1;
		    yDestination = yTable1;
		}
		else if (seatnumber==2){
			xDestination = xTable2;
			yDestination = yTable2;
		}
		else if (seatnumber==3) {
			xDestination = xTable3;
			yDestination = yTable3;
		}
		
		command = Command.GoToSeat;
	}

	public void DoExitRestaurant() {
		
		xDestination = -50;
		yDestination = -50;
		command = Command.LeaveRestaurant;
	}
}
