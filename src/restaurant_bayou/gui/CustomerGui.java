package restaurant_bayou.gui;

import restaurant_bayou.CustomerAgent;
import restaurant_bayou.HostAgent;
import simcity.gui.SimCityGui;
import AnimationTools.AnimationModule;

import java.awt.*;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import AnimationTools.AnimationModule;

public class CustomerGui implements Gui{

	private CustomerAgent agent = null;
	private boolean isPresent = false;
	private boolean isHungry = false;
	private String text = "";

	SimCityGui gui;
	AnimationModule animModule = new AnimationModule();

	private int xPos, yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand, GoToSeat, GoToCashier, LeaveRestaurant};
	private Command command=Command.noCommand;

	public static final int xTable1 = 60;
	public static final int yTable1 = 235;
	public static final int xTable2 = 160;
	public static final int yTable2 = 230;
	public static final int xTable3 = 260;
	public static final int yTable3 = yTable1; 
	public static final int xTable4 = 320;
	public static final int yTable4 = yTable1;

	public CustomerGui(CustomerAgent c, SimCityGui gui, int numCustomers){ //HostAgent m) {
		agent = c;
		xPos = xTable1/20+(numCustomers*xTable1/5);
		yPos = (yTable1/50);
		xDestination = xTable1/20+(numCustomers*xTable1/5);
		yDestination = yTable1/50;
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
			if (command==Command.GoToSeat) agent.msgAnimationFinishedGoToSeat();
			else if (command==Command.GoToCashier) agent.msgAnimationFinishedGoToCashier();
			else if (command==Command.LeaveRestaurant) {
				agent.msgAnimationFinishedLeaveRestaurant();
				isHungry = false;
				setEnabled();
			}
			animModule.setStill();
			command=Command.noCommand;
		}
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.CYAN);
		//g.fillRect(xPos, yPos, xTable/6, yTable/12);
		animModule.updateAnimation();//updates the frame and animation 
		g.drawImage(animModule.getImage(), (int)xPos, (int)yPos, null);
		g.setColor(Color.GRAY);
		if (text == null) text = "";
		g.drawString(text, xPos, yPos);
	}

	public boolean isPresent() {
		return isPresent;
	}
	public void setHungry() {
		//gui.increaseLine();
		isHungry = true;
		agent.gotHungry();
		setPresent(true);
	}
	public boolean isHungry() {
		return isHungry;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}

	public void DoGoToSeat(int seatnumber) {//later you will map seatnumber to table coordinates.
		//gui.decreaseLine();
		if (seatnumber == 1 ) {
			xDestination = xTable1;
			yDestination = yTable1;
		}
		else if (seatnumber == 2) {
			xDestination = xTable2; 
			yDestination = yTable2;
		}
		else if (seatnumber == 3) {
			xDestination = xTable3; 
			yDestination = yTable3;
		}
		else if (seatnumber == 4) {
			xDestination = xTable4;
			yDestination = yTable4;
		}
		command = Command.GoToSeat;
	}
	
	public void DoGoToCashier() {
		xDestination = 250;
		yDestination = 80;
		command = Command.GoToCashier;
	}

	public void DoExitRestaurant() {
		xDestination = 120;
		yDestination = -50;
		command = Command.LeaveRestaurant;
	}
	
	public void setEnabled() {
		//gui.setEnabled(agent);
	}
	
	public void setText(String t) {
		text = t;
	}
}
