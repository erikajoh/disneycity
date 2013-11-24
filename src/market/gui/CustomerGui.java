package market.gui;

import market.CustomerAgent;
import market.ManagerAgent;
import java.awt.*;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class CustomerGui implements Gui{

	private CustomerAgent agent = null;
	private boolean isPresent = false;
	private boolean isHungry = false;
	private String text = "";

	private int xPos, yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand, GoToSeat, GoToCashier, LeaveRestaurant};
	private Command command=Command.noCommand;

	public static final int xTable = 100;
	public static final int yTable = 250;

	public CustomerGui(CustomerAgent c){
		agent = c;
		xPos = xTable/20+(xTable/5);
		yPos = (yTable/50);
		xDestination = xTable/20+(xTable/5);
		yDestination = yTable/50;
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
			command=Command.noCommand;
		}
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.CYAN);
		g.fillRect(xPos, yPos, xTable/6, yTable/12);
//		Image img = Toolkit.getDefaultToolkit().getImage("customer.jpg");
//		g.drawImage(img, xPos, yPos, yTable/12, yTable/12, null);
		g.setColor(Color.GRAY);
		if (text == null) text = "";
		g.drawString(text, xPos, yPos);
	}

	public boolean isPresent() {
		return isPresent;
	}

	public boolean isHungry() {
		return isHungry;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}

	public void DoGoToSeat(int seatnumber) {//later you will map seatnumber to table coordinates.
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
	
	public void setText(String t) {
		text = t;
	}
}
