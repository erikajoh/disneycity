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
	private int fallCount = 0;
	private int xDestination, yDestination;
	private enum Command {noCommand, GoToTeller, FailRobbery, LeaveBank}; //shortened to noCommand and walking?
	private enum Direction{up, down, left, right, fall};
	private Direction direction = Direction.up;
	private Command command=Command.noCommand;
	private boolean isInBank = false;
	private AnimationModule animModule;


	public BankCustomerGui(BankCustomerAgent c, SimCityGui gui, boolean present, boolean isThief, int wx, int wy){ //HostAgent m) {
		agent = c;
		windowX = wx;
		windowY = wy;
		
		if(isThief == false){
			 animModule = new AnimationModule();
		}
		else {
			 animModule = new AnimationModule("WarioThief");
		}
		
		isPresent = present;
		xPos = windowX/2-10;
		yPos = windowY-25;
		xDestination = xPos;
		yDestination = yPos;
		isPresent = true;
		this.gui = gui;
	}

	public void updatePosition() {
		if(command == Command.FailRobbery){
			if(fallCount < 5){
				fallCount++;
			}
			else{
				fallCount = 0;
				command = command.noCommand;
				xDestination = windowX/2-10;
				yDestination = windowY+40;
				agent.msgAnimationFinishedLeavingBank();
			}
		}
		else{
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
			case fall:
			animModule.changeAnimation("Fall"); break;
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
	
	public void DoFailRobbery() {
		xDestination = windowX/2-10;
		yDestination = windowY/2;
		command = Command.FailRobbery;
	}
	
	public void setWindow(int wx, int wy){
		windowX = wx;
		windowY = wy;
	}
	
}
