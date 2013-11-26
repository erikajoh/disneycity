package market.gui;

import market.WorkerAgent;

import java.awt.*;
import java.util.concurrent.Semaphore;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class WorkerGui implements Gui{

	private WorkerAgent agent = null;
	private boolean isPresent = false;
	private String text = "";

	private int xPos, yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand, getItem, bringItem, goHome};
	private Command command=Command.noCommand;
	
//	private Semaphore moving = new Semaphore(0, true);

	public static final int mWidth = 400;
	public static final int mHeight = 360;

	public WorkerGui(WorkerAgent w){
		agent = w;
		agent.setGui(this);
		xPos = (int)(mWidth*0.57) - agent.getNum()*mWidth/15;
		yPos = (int)(mHeight*0.15);
		xDestination = (int)(mWidth*0.57) - agent.getNum()*mWidth/15;
		yDestination = (int)(mHeight*0.15);
	}

	public void updatePosition() {
		if (command == Command.goHome || command == Command.bringItem) {
			if (yPos < yDestination)
				yPos++;
			else if (yPos > yDestination)
				yPos--;
			else if (xPos < xDestination)
				xPos++;
			else if (xPos > xDestination)
				xPos--;
		} else {
			if (xPos < xDestination)
				xPos++;
			else if (xPos > xDestination)
				xPos--;
			else if (yPos < yDestination)
				yPos++;
			else if (yPos > yDestination)
				yPos--;
		}
			
		if (xPos == xDestination && yPos == yDestination) {
			if (command == Command.bringItem) agent.msgAtFront();
			else if (command != Command.noCommand) agent.msgAnimationFinished();
			command=Command.noCommand;
		}
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.GREEN);
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
    
    public void DoGoGetItem(int shelf) {
    	double xModifier = 0.3 + (0.2*shelf);
    	xDestination = (int)(mWidth*xModifier);
    	yDestination = (int)(mHeight*0.6);
    	command = Command.getItem;
    }
    
    public void DoBringItemToFront() {
    	xDestination = (int)(mWidth*0.25);
    	yDestination = (int)(mHeight*0.2);
    	command = Command.bringItem;
    }
    
    public void DoGoToHome() {
    	xDestination = (int)(mWidth*0.57) - agent.getNum()*mWidth/15;
		yDestination = (int)(mHeight*0.15);
    	command = Command.goHome;
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
}
