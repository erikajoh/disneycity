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

	public static final int xTable = 100;
	public static final int yTable = 250;

	public CustomerGui(CustomerAgent c, SimCityGui gui, int numCustomers){ //HostAgent m) {
		agent = c;
		xPos = xTable/20+(numCustomers*xTable/5);
		yPos = (yTable/50);
		xDestination = xTable/20+(numCustomers*xTable/5);
		yDestination = yTable/50;
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
//		Image img = Toolkit.getDefaultToolkit().getImage("customer.jpg");
//		g.drawImage(img, xPos, yPos, yTable/12, yTable/12, null);
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
		xDestination = xTable*seatnumber;
		yDestination = yTable;
		command = Command.GoToSeat;
	}
	
	public void DoGoToCashier() {
		xDestination = xTable*9/2;
		yDestination = yTable/5;
		command = Command.GoToCashier;
	}

	public void DoExitRestaurant() {
		xDestination = -xTable/5;
		yDestination = -yTable/6;
		command = Command.LeaveRestaurant;
	}
	
	public void setEnabled() {
		//gui.setEnabled(agent);
	}
	
	public void setText(String t) {
		text = t;
	}
}
