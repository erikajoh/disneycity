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
	private enum Command {noCommand, getItem, goHome};
	private Command command=Command.noCommand;
	
//	private Semaphore moving = new Semaphore(0, true);

	public static final int mWidth = 400;
	public static final int mHeight = 360;

	public WorkerGui(WorkerAgent w){
		agent = w;
		agent.setGui(this);
		xPos = (int)(mWidth*0.6) - agent.getNum()*mWidth/15;
		yPos = (int)(mHeight*0.1);
	}

	public void updatePosition() {
		if (command == Command.goHome) {
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
			if (command != Command.noCommand) agent.msgAnimationFinished();
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
    	xDestination = 200;
    	yDestination = 200;
    	command = Command.getItem;
    }
    
    public void DoGoToHome() {
    	xDestination = (int)(mWidth*0.6) - agent.getNum()*mWidth/15;
		yDestination = (int)(mHeight*0.08);
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
