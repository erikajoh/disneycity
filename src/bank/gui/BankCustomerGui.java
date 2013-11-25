package bank.gui;

import AnimationTools.AnimationModule;
import bank.BankCustomerAgent;

import java.awt.*;

import simcity.gui.SimCityGui;

public class BankCustomerGui implements Gui{

	private BankCustomerAgent agent = null;
	private boolean isPresent = false;

	SimCityGui gui;

	private int windowX, windowY;
	private int xPos, yPos;
	private int xDestination, yDestination;
	private String drawString = "";
	private String initial;
	private enum Command {noCommand, GoToTeller, LeaveBank}; //shortened to noCommand and walking?
	private enum Direction{left, right, up, down};
	private Direction direction = Direction.up;
	private Command command=Command.noCommand;
	private boolean isInBank = false;
	private AnimationModule animModule = new AnimationModule();


	public BankCustomerGui(BankCustomerAgent c, SimCityGui gui, boolean present, int wx, int wy){ //HostAgent m) {
		agent = c;
		windowX = wx;
		windowY = wy;
		isPresent = present;
		xPos = windowX/2-10;
		yPos = windowY-25;
		xDestination = xPos;
		yDestination = yPos;
		initial = c.toString().substring(9, 10);
		isPresent = true;
		this.gui = gui;
	}

	public void updatePosition() {
		if (xPos < xDestination){
			xPos++;
			direction = Direction.right;
		}
		else if (xPos > xDestination){
			xPos--;
			direction = Direction.left;
		}
		if (yPos < yDestination){
			yPos++;
			direction = Direction.down;
		}
		else if (yPos > yDestination){
			yPos--;
			direction = Direction.up;
		}
		

		if (xPos == xDestination && yPos == yDestination) {
			if (command==Command.GoToTeller) {
				agent.msgAnimationFinishedGoToTeller();
			}
			else if (command==Command.LeaveBank) {
				agent.msgAnimationFinishedLeavingBank();
				isInBank = false;
			}
			command=Command.noCommand;
		}
		
		switch(direction){
			case right:
			animModule.changeAnimation("WalkRight"); break;
			case left:
			animModule.changeAnimation("WalkLeft"); break;
			case up:
			animModule.changeAnimation("WalkUp"); break;
			case down:
			animModule.changeAnimation("WalkDown"); break;
		}
	}

	public void draw(Graphics2D g) {
		animModule.updateAnimation();//updates the frame and animation 
		g.drawImage(animModule.getImage(), xPos, yPos, null);
	}

	public boolean isPresent() {
		return isPresent;
	}
	
	public void setInBank(boolean inBank) {
		isInBank = inBank;
	}
	
	public boolean isInBank() {
		return isInBank;
	}
	
	public void setPresent(boolean p) {
		isPresent = p;
	}

	public void DoGoToTeller(int xd, int yd) {
		isInBank = true;
		xDestination = xd;
		yDestination = yd;
		command = Command.GoToTeller;
	}
	
	public void DoLeaveBank() {
		xDestination = windowX/2-10;
		yDestination = windowY+40;
		command = Command.LeaveBank;
	}
	
	public void setWindow(int wx, int wy){
		windowX = wx;
		windowY = wy;
	}
	
}
