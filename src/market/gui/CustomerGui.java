package market.gui;

import market.CustomerAgent;

import java.awt.*;
import java.util.concurrent.Semaphore;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class CustomerGui implements Gui{

	private CustomerAgent agent = null;
	private boolean isPresent = false;
	private String text = "";

	private int xPos, yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand, EnterMarket, LeaveMarket};
	private Command command=Command.noCommand;
	
	private Semaphore moving = new Semaphore(1, true);

	public static final int mWidth = 400;
	public static final int mHeight = 360;

	public CustomerGui(CustomerAgent c){
		agent = c;
		agent.setGui(this);
		xPos = (int)(mWidth*0.18);
		yPos = mHeight;
	}

	public void updatePosition() {
		if (xPos < xDestination)
			xPos++;
		else if (xPos > xDestination)
			xPos--;
		else if (yPos < yDestination)
			yPos++;
		else if (yPos > yDestination)
			yPos--;
		
		if (xPos == xDestination && yPos == yDestination) {
			if (command != Command.noCommand) agent.msgAnimationFinished();
			command=Command.noCommand;
		}
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.CYAN);
		g.fillRect(xPos, yPos, mWidth/20, mHeight/15);
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

	public void DoEnterMarket() {//later you will map seatnumber to table coordinates.
		System.out.println("enter");
		xDestination = (int)(mWidth*0.18);
		yDestination = (int)(mHeight*0.35);
		command = Command.EnterMarket;
	}

	public void DoLeaveMarket() {
		xDestination = 0;
		yDestination = -mHeight/15;
		command = Command.LeaveMarket;
	}
	
	public void setText(String t) {
		text = t;
	}
}
