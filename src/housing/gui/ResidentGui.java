package housing.gui;

import housing.ResidentAgent;

import java.awt.*;
import java.util.ArrayList;
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
		
	private HousingAnimationPanel panel;

	private int xPos, yPos, xDestination, yDestination, xDestNext, yDestNext, xDestFinal, yDestFinal;
	private int xTable, yTable, xBed, yBed, xKitchen, yKitchen, xEntrance, yEntrance;
	
	private enum Dir { x, y, all }; // to decide which direction to move first
	Dir dir = Dir.all;
	
	private class Wall {
		int xStart, xEnd, yStart, yEnd;
		Wall(double xs, double xe, double ys, double ye) {
			xStart = (int)(hWidth*xs);
			xEnd = (int)(hWidth*xe);
			yStart = (int)(hHeight*ys);
			yEnd = (int)(hHeight*ye);
//			panel.addLine(xStart, xEnd, yStart, yEnd); // for testing
		}
		boolean hitsHfromBelow(int xPos, int yPos) {
			if (yStart == yEnd && yPos == yStart+1 && xPos >= xStart && xPos <= xEnd) {
				//implication: should walk horizontally, i.e. x first
				return true;
			}
			return false;
		}
		boolean hitsHfromAbove(int xPos, int yPos) {
			if (yStart == yEnd && yPos == yStart-24 && xPos >= xStart && xPos <= xEnd) {
				//implication: should walk horizontally, i.e. x first
				return true;
			}
			return false;
		}
		boolean hitsVfromLeft(int xPos, int yPos) {
			if (xStart == xEnd && xPos == xStart-24 && yPos >= yStart && yPos <= yEnd) {
				//implication: should walk vertically, i.e. y first
				return true;
			}
			return false;
		}
		boolean hitsVfromRight(int xPos, int yPos) {
			if (xStart == xEnd && xPos == xStart+1 && yPos >= yStart && yPos <= yEnd) {
				//implication: should walk vertically, i.e. y first
				return true;
			}
			return false;
		}
	}
	
	ArrayList<Wall> walls = new ArrayList<Wall>();
	
	private enum Command {noCommand, EnterHouse, GoToBed, GoToTable, GoToKitchen, HelperMove, DoneMaintenance, LeaveHouse};
	private Command command=Command.noCommand;
	
	private Semaphore moving = new Semaphore(0, true);
	
	String type;

	public static final int hWidth = 400;
	public static final int hHeight = 360;

	public ResidentGui(ResidentAgent r, String t, int n, HousingAnimationPanel p){
		panel = p;
		xDestNext = -1;
		yDestNext = -1;
		animModule = new AnimationModule();
		agent = r;
		agent.setGui(this);
		type = t;
		roomNo = n;
		if(type == "house"){
			xPos = xEntrance = (int)(hWidth*0.23);
			yPos = yEntrance = (int)(hHeight*0.92);
			xTable = (int)(hWidth*0.23);
			yTable = (int)(hHeight*0.65);
			xBed = (int)(hWidth*0.7);
			yBed = (int)(hHeight*0.15);
			xKitchen = (int)(hWidth*0.8);
			yKitchen = (int)(hHeight*0.7);
			walls.add(new Wall(0.17,0.17,0.45,0.64));
			walls.add(new Wall(0.35,0.35,0.45,0.64));
			walls.add(new Wall(0.17,0.35,0.49,0.49));
			walls.add(new Wall(0.17,0.35,0.64,0.64));
			walls.add(new Wall(0.53,0.53,0,0.5));
			walls.add(new Wall(0.66,1,0.46,0.46));
			walls.add(new Wall(0.01,0.1,0.9,0.9));
			walls.add(new Wall(0.2,0.98,0.9,0.9));
		} else if(type == "apt"){			
			xPos = xEntrance = (int)(hWidth);
			yPos = yEntrance = (int)(hHeight*0.56);
			//horizontal apartment walls from left to right	        
			walls.add(new Wall(0,0.22,0.51,0.51));
			if (roomNo == 0) walls.add(new Wall(0.29,1,0.5,0.5));
			if (roomNo == 1) {
				walls.add(new Wall(0.29,0.54,0.5,0.5));
				walls.add(new Wall(0.6,1,0.5,0.5));
			}
			if (roomNo == 2) {
				walls.add(new Wall(0,0.84,0.5,0.5));
				walls.add(new Wall(0.93,1,0.5,0.5));
			}
			if (roomNo == 3) {
				walls.add(new Wall(0,0.55,0.63,0.63));
			}
			//horizontal apartment table sides
			walls.add(new Wall(0.69,0.75,0.59,0.59));
			walls.add(new Wall(0.69,0.75,0.69,0.69));
			//vertical apartment walls from left to right
			walls.add(new Wall(0.29,0.29,0,0.5));
			walls.add(new Wall(0.6,0.6,0,0.5));
			walls.add(new Wall(0.93,0.93,0,0.5));
			walls.add(new Wall(0.54,0.54,0.69,1));
			//vertical apartment table sides
			walls.add(new Wall(0.69,0.69,0.56,0.69));
			walls.add(new Wall(0.75,0.75,0.56,0.69));
			//entrance/exit wall
			walls.add(new Wall(0.92,0.92,0.64,1));

			if(roomNo == 0){
				xTable = (int)(hWidth*0.63);
				yTable = (int)(hHeight*0.59);
				xBed = (int)(hWidth*0.16);
				yBed = (int)(hHeight*0.15);
				xKitchen = (int)(hWidth*0.59);
				yKitchen = (int)(hHeight*0.7);
			}else if(roomNo == 1){
				xTable = (int)(hWidth*0.76);
				yTable = (int)(hHeight*0.59);
				xBed = (int)(hWidth*0.48);
				yBed = (int)(hHeight*0.15);
				xKitchen = (int)(hWidth*0.65);
				yKitchen = (int)(hHeight*0.7);
			}else if(roomNo == 2){
				xTable = (int)(hWidth*0.69);
				yTable = (int)(hHeight*0.51);
				xBed = (int)(hWidth*0.79);
				yBed = (int)(hHeight*0.15);
				xKitchen = (int)(hWidth*0.71);
				yKitchen = (int)(hHeight*0.7);
			}else if(roomNo == 3){
				xTable = (int)(hWidth*0.69);
				yTable = (int)(hHeight*0.67);
				xBed = (int)(hWidth*0.2);
				yBed = (int)(hHeight*0.71);
				xKitchen = (int)(hWidth*0.77);
				yKitchen = (int)(hHeight*0.7);
			}
		}
	}

	public void updatePosition() {
				
		// general animation states
		for (Wall w : walls) {
			
			if (w.hitsHfromAbove(xPos,  yPos) && currDir == Direction.DOWN) {
				System.out.println("hit a horizontal wall from above");
				System.out.println("xPos: "+xPos+" xDest: "+xDestination);
				xDestNext = xDestination;
				if (xDestination < xPos || xDestination < w.xStart || w.xEnd == hWidth) {
					System.out.println("changing xDest from " + xDestination + " to "+(w.xStart-24));
					xDestination = w.xStart-16;
				}
				else if (xDestination > xPos || xDestination > w.xEnd) {
					System.out.println("changing xDest from " + xDestination + " to "+(w.xEnd+1));
					xDestination = w.xEnd+1;
				}
				else {
					System.out.println("changing xDest from " + xDestination + " to "+(w.xEnd+1));
					xDestination = w.xEnd+1;
				}
				dir = Dir.x;
				break;
			}else if(w.hitsHfromBelow(xPos, yPos) && currDir == Direction.UP){
				System.out.println("hit a horizontal wall from below");
				System.out.println("xPos: "+xPos+" xDest: "+xDestination);
				xDestNext = xDestination;
				if (xDestination < xPos || xDestination < w.xStart) {
					System.out.println("changing xDest from " + xDestination + " to "+(w.xStart-24));
					xDestination = w.xStart-16;
				}
				else if (xDestination > xPos || xDestination > w.xEnd || w.xStart == 0) {
					System.out.println("changing xDest from " + xDestination + " to "+(w.xEnd+1));
					xDestination = w.xEnd+1;
				}
				else {
					System.out.println("changing xDest from " + xDestination + " to "+(w.xStart-24));
					xDestination = w.xStart-16;
				}
				dir = Dir.x;
				break;
			}else if(w.hitsVfromLeft(xPos, yPos) && currDir == Direction.RIGHT){
				System.out.println("hit a vertical wall from the left");
				System.out.println("yPos: "+yPos+" yDest: "+yDestination);
				yDestNext = yDestination;
				if (yDestination < yPos || yDestination < w.yStart || w.yEnd == hHeight) {
					System.out.println("changing yDest to "+(w.yStart-24));
					yDestination = w.yStart-16;
				}
				else if (yDestination > yPos || yDestination > w.yEnd) {
					System.out.println("changing yDest to "+(w.yEnd+1));
					yDestination = w.yEnd+1;
				}
				else {
					System.out.println("changing yDest to "+(w.yEnd+1));
					yDestination = w.yEnd+1;
				}
				dir = Dir.y;
				break;
			}else if(w.hitsVfromRight(xPos, yPos) && currDir == Direction.LEFT){
				System.out.println("hit a vertical wall from the right");
				System.out.println("yPos: "+yPos+" yDest: "+yDestination);
				yDestNext = yDestination;
				if (yDestination < yPos || yDestination < w.yStart) {
					System.out.println("changing yDest to "+(w.yStart-24));
					yDestination = w.yStart-16;
				}
				else if (yDestination > yPos || yDestination > w.yEnd || w.yStart == 0) {
					System.out.println("changing yDest to "+(w.yEnd+1));
					yDestination = w.yEnd+1;
				}
				else {
					System.out.println("changing yDest to "+(w.yStart-24));
					yDestination = w.yStart-16;
				}
				dir = Dir.y;
				break;
			}
			
		}
		
		if (dir == Dir.x) {
			if (xPos < xDestination) {
				xPos++;
				currDir = Direction.RIGHT;
			}
			else if (xPos > xDestination) {
				xPos--;
				currDir = Direction.LEFT;
			}
		} else if (dir == Dir.y) {
			if (yPos < yDestination) {
				yPos++;
				currDir = Direction.DOWN;
			}
			else if (yPos > yDestination) {
				yPos--;
				currDir = Direction.UP;
			}
		} else {
			if (xPos < xDestination) {
				xPos++;
				currDir = Direction.RIGHT;
			}
			else if (xPos > xDestination) {
				xPos--;
				currDir = Direction.LEFT;
			} else if (yPos < yDestination) {
				yPos++;
				currDir = Direction.DOWN;
			}
			else if (yPos > yDestination) {
				yPos--;
				currDir = Direction.UP;
			}
			
		}
				
		// special animation states
		standing = xPos == xDestination && yPos == yDestination;
		if(type == "house") sleeping = Math.abs(xPos - (int)(hWidth*0.7)) < 2 && Math.abs(yPos - (int)(hHeight*0.15)) < 2;
		else if(type == "apt") {
			if(roomNo == 0) sleeping = Math.abs(xPos - (int)(hWidth*0.16)) < 2 && Math.abs(yPos - (int)(hHeight*0.15)) < 2;
			else if(roomNo == 1) sleeping = Math.abs(xPos - (int)(hWidth*0.48)) < 2 && Math.abs(yPos - (int)(hHeight*0.15)) < 2;
			else if(roomNo == 2) sleeping = Math.abs(xPos - (int)(hWidth*0.79)) < 2 && Math.abs(yPos - (int)(hHeight*0.15)) < 2;
			else if(roomNo == 3) sleeping = Math.abs(xPos - (int)(hWidth*0.2)) < 2 && Math.abs(yPos - (int)(hHeight*0.71)) < 2;
		}
		
		// animation rules
		if(sleeping) {
			animModule.changeAnimation("Sleep");
		}
		else if(standing) {
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
		
		if (xPos == xDestFinal && yPos == yDestFinal) {
			
			xDestNext = -1;
			yDestNext = -1;
			
			if (command != Command.noCommand) moving.release();
			
			if (command == Command.LeaveHouse) {
				agent.msgAnimationLeavingFinished();
			}
			else if (command == Command.DoneMaintenance) agent.msgMaintenanceAnimationFinished();
			else if (command != Command.noCommand && command != Command.HelperMove) agent.msgAnimationFinished();
			
			command=Command.noCommand;
			
		}
		
		if (xPos == xDestination && yPos == yDestination) {
			if (xDestNext != -1) {
				System.out.println("changing xDestination back to: "+xDestNext);
				xDestination = xDestNext;
				xDestNext = -1;
				dir = Dir.y;
			}
			if (yDestNext != -1) {
				System.out.println("changing yDestination back to: "+yDestNext);
				yDestination = yDestNext;
				yDestNext = -1;
				dir = Dir.x;
			}
		} else if (xPos == xDestination) {
			dir = Dir.y;
		} else if (yPos == yDestination) {
			dir = Dir.x;
		}
		
		if (xPos == xDestination && xDestNext == -1 && xDestination != xDestFinal) {
			xDestination = xDestFinal;
		}
		if (yPos == yDestination && yDestNext == -1 && yDestination != yDestFinal) {
			yDestination = yDestFinal;
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
	
	public void DoEnterHouse() {
		xDestNext = -1;
		yDestNext = -1;
		System.out.println("Entering house");
		setPresent(true);
		xDestFinal = xDestination = xTable;
		yDestFinal = yDestination = yTable;
		command = Command.EnterHouse;
		try {
			moving.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	public void DoGoToBed() {
		xDestNext = -1;
		yDestNext = -1;
		System.out.println("Going to bed");
		xDestFinal = xDestination = xBed;
		yDestFinal = yDestination = yBed;
		command = Command.GoToBed;
		try {
			moving.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void DoGoToTable() {
		xDestNext = -1;
		yDestNext = -1;
		xDestFinal = xDestination = xTable;
		yDestFinal = yDestination = yTable;
		command = Command.GoToTable;
		try {
			moving.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void DoMaintenance() {
		xDestNext = -1;
		yDestNext = -1;
		DoGoToTable();
//		if (type == "house"){
//			for (int i=0; i<6; i++) {
//				MaintainArea(i);
//			}
//		} else if (type == "apt"){
//			for (int i=0; i<4; i++) {
//				MaintainApt(i);
//			}
//		}
	}
	
	private void MaintainApt(int i) {
		if (i == 0) {
			xDestination = (int)(hWidth*0.85);
			yDestination = (int)(hHeight*0.5);
			command = Command.HelperMove;
		} else if (i == 1) {
			xDestination = (int)(hWidth*0.58);
			yDestination = (int)(hHeight*0.5);
			command = Command.HelperMove;
		} else if (i == 2) {
			xDestination = (int)(hWidth*0.58);
			yDestination = (int)(hHeight*0.7);
			command = Command.HelperMove;
		} else if (i == 3) {
			xDestination = (int)(hWidth*0.85);
			yDestination = (int)(hHeight*0.5);
			command = Command.DoneMaintenance;
		}
		try {
			moving.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void MaintainArea(int i) {
		if (i == 0) {
			xDestination = (int)(hWidth*0.65);
			yDestination = (int)(hHeight*0.47);
			command = Command.HelperMove;
		} else if (i == 1) {
			xDestination = (int)(hWidth*0.05);
			yDestination = (int)(hHeight*0.47);
			command = Command.HelperMove;
		} else if (i == 2) {
			xDestination = (int)(hWidth*0.05);
			yDestination = (int)(hHeight*0.25);
			command = Command.HelperMove;
		} else if (i == 3) {
			xDestination = (int)(hWidth*0.4);
			yDestination = (int)(hHeight*0.25);
			command = Command.HelperMove;
		} else if (i == 4) {
			xDestination = (int)(hWidth*0.4);
			yDestination = (int)(hHeight*0.65);
			command = Command.HelperMove;
		} else {
			xDestination = (int)(hWidth*0.23);
			yDestination = (int)(hHeight*0.65);
			command = Command.DoneMaintenance;
		}		
		try {
			moving.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void DoGoToKitchen() {
		xDestNext = -1;
		yDestNext = -1;
		xDestFinal = xDestination = xKitchen;
		yDestFinal = yDestination = yKitchen;
		command = Command.GoToKitchen;
		try {
			moving.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void DoLeaveHouse() {
		xDestNext = -1;
		yDestNext = -1;
		xDestFinal = xDestination = xEntrance;
		yDestFinal = yDestination = yEntrance;
		System.out.println("leaving house to dest: "+xDestFinal+ " "+ yDestFinal);
		command = Command.LeaveHouse;
		try {
			moving.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    
	}
	
	public void setPanel(HousingAnimationPanel p) {
		panel = p;
	}
	
	public void setText(String t) {
		text = t;
	}
}
