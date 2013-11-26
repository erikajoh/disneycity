package market.gui;

import market.CustomerAgent;

import java.awt.*;
import java.util.concurrent.Semaphore;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import AnimationTools.AnimationModule;

public class CustomerGui implements Gui{

	private CustomerAgent agent = null;
	private boolean isPresent = false;
	private String text = "";
	
	/* animation from Doug */
	AnimationModule animModule;
	private enum Direction { UP, DOWN, LEFT, RIGHT};
	boolean standing = false;
	private Direction currDir = Direction.UP;

	private int xPos, yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand, EnterMarket, MoveUp, LeaveMarket};
	private Command command=Command.noCommand;
	
	public static final int mWidth = 400;
	public static final int mHeight = 360;

	public CustomerGui(CustomerAgent c){
		animModule = new AnimationModule();
		agent = c;
		agent.setGui(this);
		xPos = (int)(mWidth*0.18);
		yPos = mHeight;
	}

	public void updatePosition() {
		
		// general animation states
		if (xPos < xDestination) {
			xPos++;
			currDir = Direction.RIGHT;
		}
		else if (xPos > xDestination) {
			xPos--;
			currDir = Direction.LEFT;
		}
		else if (yPos < yDestination) {
			yPos++;
			currDir = Direction.DOWN;
		}
		else if (yPos > yDestination) {
			yPos--;
			currDir = Direction.UP;
		}
		
		// special animation states
		standing = xPos == xDestination && yPos == yDestination;		
		
		// animation rules
		if(standing) {
			animModule.changeAnimation("Stand");
		}
		else {
			switch(currDir) {
				case UP:
					animModule.changeAnimation("WalkUp"); break;			
				case DOWN:
					animModule.changeAnimation("WalkDown"); break;
				case LEFT:
					animModule.changeAnimation("WalkLeft"); break;
				case RIGHT:
					animModule.changeAnimation("WalkRight"); break;
			}
		}
		
		if (xPos == xDestination && yPos == yDestination) {
			if (command == Command.MoveUp) agent.msgAnimationMoveUpFinished();
			else if (command != Command.noCommand) agent.msgAnimationFinished();
			command=Command.noCommand;
		}
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.CYAN);
		animModule.updateAnimation();//updates the frame and animation 
		g.drawImage(animModule.getImage(), xPos, yPos, null);
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
		yDestination = (int)(mHeight*0.35) + agent.getNum()*mHeight/10;
		command = Command.EnterMarket;
	}

	public void DoLeaveMarket() {
		xDestination = 0;
		yDestination = -mHeight/15;
		command = Command.LeaveMarket;
	}
	
	public void DoMoveUpInLine() {
		System.out.println("moving");
		xDestination = (int)(mWidth*0.18);
		yDestination = (int)(mHeight*0.35) + agent.getNum()*mHeight/10;
		command = Command.MoveUp;
	}
	
	public void setText(String t) {
		text = t;
	}
}
