package housing.gui;

import housing.RenterAgent;

import java.awt.*;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class RenterGui implements Gui{

	private RenterAgent agent = null;
	private boolean isPresent = false;
	private String text = "";

	HousingGui gui;

	private int xPos, yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand, GoToSeat, EnterHouse, LeaveHouse};
	private Command command=Command.noCommand;

	public static final int hWidth = 400;
	public static final int hHeight = 300;

	public RenterGui(RenterAgent r, HousingGui gui){ //HostAgent m) {
		agent = r;
		agent.setGui(this);
		xPos = (int)(hWidth*0.27);
		yPos = (int)(hHeight*0.92);
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
			//if (command==Command.GoToSeat) agent.msgAnimationFinishedGoToSeat();
			//else if (command==Command.GoToCashier) agent.msgAnimationFinishedGoToCashier();
			//else if (command==Command.LeaveRestaurant) {
				//agent.msgAnimationFinishedLeaveRestaurant();
				//setEnabled();
			//}
			command=Command.noCommand;
		}
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.CYAN);
		g.fillRect(xPos, yPos, hWidth/20, hHeight/15);
//		Image img = Toolkit.getDefaultToolkit().getImage("customer.jpg");
//		g.drawImage(img, xPos, yPos, yTable/12, yTable/12, null);
		g.setColor(Color.GRAY);
		if (text == null) text = "";
		g.drawString(text, xPos, yPos);
	}

	public boolean isPresent() {
		return isPresent;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}
	
	public void DoEnterHouse() {
		System.out.println("Entering house");
		setPresent(true);
		xDestination = (int)(hWidth*0.27);
		yDestination = (int)(hHeight*0.65);
		command = Command.EnterHouse;
	}
	
	public void DoGoToCouch() {
		xDestination = -hWidth/5;
		yDestination = -hHeight/6;
		command = Command.LeaveHouse;
	}
	
	public void DoGoToBed() {
		xDestination = -hWidth/5;
		yDestination = -hHeight/6;
		command = Command.LeaveHouse;
	}
	
	public void DoGoToBath() {
		xDestination = -hWidth/5;
		yDestination = -hHeight/6;
		command = Command.LeaveHouse;
	}
	
	public void DoGoToTable() {
		xDestination = -hWidth/5;
		yDestination = -hHeight/6;
		command = Command.LeaveHouse;
	}
	
	public void DoGoToKitchen() {
		xDestination = -hWidth/5;
		yDestination = -hHeight/6;
		command = Command.LeaveHouse;
	}

	public void DoLeaveHouse() {
		xDestination = (int)(hWidth*0.27);
		yDestination = (int)(hHeight*0.92);
		command = Command.LeaveHouse;
	}
	
	public void setText(String t) {
		text = t;
	}
}
