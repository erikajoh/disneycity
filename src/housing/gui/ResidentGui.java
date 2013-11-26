package housing.gui;

import housing.ResidentAgent;

import java.awt.*;
import java.util.concurrent.Semaphore;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import AnimationTools.AnimationModule;

public class ResidentGui implements Gui{

	/* animation from Doug */
	AnimationModule animModule;
	private enum Direction { UP, DOWN, LEFT, RIGHT};
	boolean standing = false;
	boolean sleeping = false;
	private Direction currDir = Direction.UP;
	
	private ResidentAgent agent = null;
	private boolean isPresent = false;
	private String text = "";
	private int roomNo;

	private int xPos, yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand, EnterHouse, GoToCouch, GoToBed, GoToRoom, AtBed, GoToBath, GoToTable, GoToKitchen, LeaveBedroom, DoMaintenance, DoneMaintenance, LeaveHouse};
	private Command command=Command.noCommand;
	
	private Semaphore moving = new Semaphore(0, true);
	private boolean inBedroom = false;
	
	String type;

	public static final int hWidth = 400;
	public static final int hHeight = 360;

	public ResidentGui(ResidentAgent r, String t, int n){
		animModule = new AnimationModule();
		agent = r;
		agent.setGui(this);
		type = t;
		roomNo = n;
		if(type == "house"){
			xPos = (int)(hWidth*0.23);
			yPos = (int)(hHeight*0.92);
		} else if(type == "apt"){
			xPos = (int)(hWidth);
			yPos = (int)(hHeight*0.57);
		}
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
		sleeping = Math.abs(xPos - (int)(hWidth*0.7)) < 2 && Math.abs(yPos - (int)(hHeight*0.15)) < 2;
		
		// animation rules
		if(sleeping) {
			animModule.changeAnimation("Sleep");
			animModule.changeFrame(1);
		}
		else if(standing) {
			animModule.changeAnimation("Stand");
			animModule.changeFrame(1);
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
			if (command == Command.DoMaintenance || command == Command.DoneMaintenance || command == Command.GoToBed || command == Command.AtBed || command == Command.GoToRoom || command == Command.LeaveBedroom)
				moving.release();
			if (command == Command.LeaveHouse) agent.msgAnimationLeavingFinished();
			if (command == Command.DoneMaintenance) agent.msgMaintenanceAnimationFinished();
			else if (command != Command.noCommand) agent.msgAnimationFinished();
			command=Command.noCommand;
		}
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.CYAN);
		// g.fillRect(xPos, yPos, hWidth/20, hHeight/15);
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
	
	public void DoEnterHouse() {
		System.out.println("Entering house");
		setPresent(true);
		if(type == "house"){
			xDestination = (int)(hWidth*0.23);
			yDestination = (int)(hHeight*0.65);
			command = Command.EnterHouse;
		}
		if(type == "apt"){
			xDestination = (int)(hWidth*0.8);
			yDestination = (int)(hHeight*0.57);
			command = Command.EnterHouse;
		}
	}
	
	public void DoLeaveBedroom() {
		xDestination = (int)(hWidth*0.53);
		yDestination = (int)(hHeight*0.65);
		command = Command.LeaveBedroom;
		inBedroom = false;
	}
	
	public void DoGoToCouch() {
		if (inBedroom) {
			DoLeaveBedroom();
			try {
				moving.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		xDestination = (int)(hWidth*0.8);
		yDestination = (int)(hHeight*0.15);
		command = Command.GoToCouch;
	}
		
	public void DoGoToBed() {
		if (type == "house") {
			for (int i=0; i<2; i++) {
				GoTowardBed(i);
				try {
					moving.acquire();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else if (type == "apt") {
			GoToAptRoom();
			try {
				moving.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			xDestination = (int)(hWidth*0.48);
			yDestination = (int)(hHeight*0.15);
			command = Command.GoToBed;
			try {
				moving.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		inBedroom = true;
	}
	
	private void GoTowardBed(int i) {
		if (i == 0) {
			xDestination = (int)(hWidth*0.53);
			yDestination = (int)(hHeight*0.15);
			command = Command.GoToBed;
		} else {
			xDestination = (int)(hWidth*0.7);
			yDestination = (int)(hHeight*0.15);
			command = Command.AtBed;
		}
	}
	
	public void DoGoToBath() {
		xDestination = -hWidth/5;
		yDestination = -hHeight/6;
		command = Command.GoToBath;
	}
	
	private void GoToAptRoom() {
		if(roomNo == 0){
			xDestination = (int)(hWidth*0.54);
			yDestination = (int)(hHeight*0.4);
			command = Command.GoToRoom;
		}else if(roomNo == 1){
			
		}else if(roomNo == 2){
			
		}else if(roomNo == 3){
			
		}
	}
	
	public void DoGoToTable() {
		if (inBedroom) {
			DoLeaveBedroom();
			try {
				moving.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (type == "house") {
			xDestination = (int)(hWidth*0.23);
			yDestination = (int)(hHeight*0.6);
		} else if (type == "apt") {
			xDestination = (int)(hWidth*0.74);
			yDestination = (int)(hHeight*0.59);
		}
		command = Command.GoToTable;
	}
	
	public void DoMaintenance() {
		if (inBedroom) {
			DoLeaveBedroom();
			try {
				moving.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (type == "house"){
			for (int i=0; i<6; i++) {
				MaintainArea(i);
				try {
					moving.acquire();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else if (type == "apt"){
			for (int i=0; i<4; i++) {
				MaintainApt(i);
				try {
					moving.acquire();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private void MaintainApt(int i) {
		if (i == 0) {
			xDestination = (int)(hWidth*0.85);
			yDestination = (int)(hHeight*0.5);
			command = Command.DoMaintenance;
		} else if (i == 1) {
			xDestination = (int)(hWidth*0.58);
			yDestination = (int)(hHeight*0.5);
			command = Command.DoMaintenance;
		} else if (i == 2) {
			xDestination = (int)(hWidth*0.58);
			yDestination = (int)(hHeight*0.7);
			command = Command.DoMaintenance;
		} else if (i == 3) {
			xDestination = (int)(hWidth*0.85);
			yDestination = (int)(hHeight*0.5);
			command = Command.DoneMaintenance;
		}
	}
	
	private void MaintainArea(int i) {
		if (i == 0) {
			xDestination = (int)(hWidth*0.65);
			yDestination = (int)(hHeight*0.47);
			command = Command.DoMaintenance;
		} else if (i == 1) {
			xDestination = (int)(hWidth*0.05);
			yDestination = (int)(hHeight*0.47);
			command = Command.DoMaintenance;
		} else if (i == 2) {
			xDestination = (int)(hWidth*0.05);
			yDestination = (int)(hHeight*0.05);
			command = Command.DoMaintenance;
		} else if (i == 3) {
			xDestination = (int)(hWidth*0.4);
			yDestination = (int)(hHeight*0.05);
			command = Command.DoMaintenance;
		} else if (i == 4) {
			xDestination = (int)(hWidth*0.4);
			yDestination = (int)(hHeight*0.65);
			command = Command.DoMaintenance;
		} else {
			xDestination = (int)(hWidth*0.23);
			yDestination = (int)(hHeight*0.65);
			command = Command.DoneMaintenance;
		}		
	}
	
	public void DoGoToKitchen() {
		if (inBedroom) {
			DoLeaveBedroom();
			try {
				moving.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (type == "house"){	
			xDestination = (int)(hWidth*0.8);
			yDestination = (int)(hHeight*0.7);
		} else if (type == "apt"){
			double xModifier = 0.06*roomNo;
			xDestination = (int)(hWidth*0.59 + xModifier);
			yDestination = (int)(hHeight*0.7);
		}
		command = Command.GoToKitchen;
	}

	public void DoLeaveHouse() {
		if (inBedroom) {
			DoLeaveBedroom();
			try {
				moving.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		xDestination = (int)(hWidth*0.23);
        yDestination = (int)(hHeight*0.92);
		command = Command.LeaveHouse;
	}
	
	public void setText(String t) {
		text = t;
	}
}
