package market.gui;

import market.CashierAgent;

import java.awt.*;
import java.util.concurrent.Semaphore;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class CashierGui implements Gui{

	private CashierAgent agent = null;
	private boolean isPresent = false;
	private String text = "";

	private int xPos, yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand, enter, leave};
	private Command command=Command.noCommand;
	
	public static final int mWidth = 400;
	public static final int mHeight = 360;
	
	public CashierGui(CashierAgent w){
		agent = w;
		agent.setGui(this);
		xPos = (int)(mWidth*0.2);
		yPos = -30;
		xDestination = (int)(mWidth*0.18);
		yDestination = (int)(mHeight*0.2);
		command = Command.enter;
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
			if (command != Command.leave) agent.msgAnimationFinished();
			command=Command.noCommand;
		}
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.GREEN);
		Image custImage = Toolkit.getDefaultToolkit().getImage("res/cashier.gif");
		g.drawImage(custImage, xPos, yPos, mWidth/15, mHeight/15, null);
//		g.fillRect(xPos, yPos, mWidth/20, mHeight/15);
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
    
    public void DoLeave() {
    	System.out.println("cashier gui leaving");
		yDestination = -30;
    	command = Command.leave;
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
