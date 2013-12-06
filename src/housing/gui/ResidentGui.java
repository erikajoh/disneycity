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
	
	private enum Dir { x, y, all, none }; // to decide which direction to move first
	Dir dir = Dir.all;
	
	private class Wall {
		int xStart, xEnd, yStart, yEnd;
		Wall(int xs, int xe, int ys, int ye) { xStart = xs; xEnd = xe; yStart = ys; yEnd = ye; }
		boolean hitsHorizontally(int xPos, int yPos) {
			if (yPos == yStart && yPos == yEnd && xPos >= xStart && xPos <= xEnd) {
				//implication: should walk horizontally, i.e. x first
				return true;
			}
			return false;
		}
		boolean hitsVertically(int xPos, int yPos) {
			if (xPos == xStart && xPos == xEnd && yPos >= yStart && yPos <= yEnd) {
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

	public ResidentGui(ResidentAgent r, String t, int n){
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
//			walls.add(0);
		} else if(type == "apt"){			
			xPos = xEntrance = (int)(hWidth);
			yPos = yEntrance = (int)(hHeight*0.56);
			
			//horizontal apartment walls from left to right
			if(roomNo == 0) walls.add( new Wall( 0, (int)(hWidth*0.24), (int)(hHeight*0.48), (int)(hHeight*0.48) ) );
			else if(roomNo == 1) walls.add( new Wall( (int)(hWidth*0.30), (int)(hWidth*0.55), (int)(hHeight*0.48), (int)(hHeight*0.48) ) );
			else if(roomNo == 2) walls.add( new Wall( (int)(hWidth*0.62), hWidth, (int)(hHeight*0.48), (int)(hHeight*0.48) ) );
			else if(roomNo == 3) {
				walls.add( new Wall( 0, (int)(hWidth*0.58), (int)(hHeight*0.53), (int)(hHeight*0.53) ) );
				walls.add( new Wall( (int)(hWidth*0.4), (int)(hWidth*0.69), (int)(hHeight*0.66), (int)(hHeight*0.66) ) );
			}
			//horizontal apartment table sides
			walls.add( new Wall( (int)(hWidth*0.65), (int)(hWidth*0.73), (int)(hHeight*0.53), (int)(hHeight*0.53) ) );
			walls.add( new Wall( (int)(hWidth*0.65), (int)(hWidth*0.73), (int)(hHeight*0.65), (int)(hHeight*0.65) ) );
			
			//vertical apartment walls from left to right
			if(roomNo == 0) walls.add( new Wall( (int)(hWidth*0.25), (int)(hWidth*0.25), (int)(hHeight*0.13), (int)(hHeight*0.52) ) );
			else if(roomNo == 1) walls.add( new Wall( (int)(hWidth*0.56), (int)(hWidth*0.56), (int)(hHeight*0.13), (int)(hHeight*0.52) ) );
//			else if(roomNo == 2) {
//				walls.add( new Wall( (int)(hWidth*0.24), (int)(hWidth*0.24), (int)(hHeight*0.13), (int)(hHeight*0.7) ) );
//				walls.add( new Wall( (int)(hWidth*0.24), (int)(hWidth*0.24), (int)(hHeight*0.13), (int)(hHeight*0.7) ) );
//			}
//			else if(roomNo == 3) walls.add( new Wall( (int)(hWidth*0.58), (int)(hWidth*0.58), (int)(hHeight*0.13), (int)(hHeight*0.7) ) );
			
			//vertical apartment table sides
			walls.add( new Wall( (int)(hWidth*0.64), (int)(hWidth*0.64), (int)(hHeight*0.53), (int)(hHeight*0.65) ) );
			walls.add( new Wall( (int)(hWidth*0.73), (int)(hWidth*0.73), (int)(hHeight*0.53), (int)(hHeight*0.65) ) );
			
			//entrance/exit wall
			walls.add( new Wall( (int)(hWidth*0.86), (int)(hWidth*0.86), (int)(hHeight*0.57), hHeight ) );

			if(roomNo == 0){
				xTable = (int)(hWidth*0.64);
				yTable = (int)(hHeight*0.59);
				xBed = (int)(hWidth*0.16);
				yBed = (int)(hHeight*0.15);
				xKitchen = (int)(hWidth*0.59);
				yKitchen = (int)(hHeight*0.7);
			}else if(roomNo == 1){
				xTable = (int)(hWidth*0.74);
				yTable = (int)(hHeight*0.59);
				xBed = (int)(hWidth*0.48);
				yBed = (int)(hHeight*0.15);
				xKitchen = (int)(hWidth*0.65);
				yKitchen = (int)(hHeight*0.7);
			}else if(roomNo == 2){
				xTable = (int)(hWidth*0.69);
				yTable = (int)(hHeight*0.52);
				xBed = (int)(hWidth*0.79);
				yBed = (int)(hHeight*0.15);
				xKitchen = (int)(hWidth*0.71);
				yKitchen = (int)(hHeight*0.7);
			}else if(roomNo == 3){
				xTable = (int)(hWidth*0.69);
				yTable = (int)(hHeight*0.66);
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
			if (w.hitsHorizontally(xPos,  yPos)) {
				if(currDir == Direction.UP || currDir == Direction.DOWN) {
//					System.out.println("hit a horizontal wall");
					// go x first
					if (xPos == xDestination) {
						if (xDestNext == -1) {
//							System.out.println("changed xDest from "+xDestination+" to "+w.xEnd);
							xDestNext = xDestination;
							xDestination = w.xEnd;
						} else {
//							System.out.println("changed xDest from "+xDestination+" to "+xDestNext);
							xDestination = xDestNext;
						}
					}
					dir = Dir.x;
					break;
				}	
			}
			if (w.hitsVertically(xPos, yPos)) {
//				System.out.println("hit a vertical wall");
				// go y first
//				System.out.println("ypos: "+yPos+" ydest: "+yDestination);
				if (yPos == yDestination) {
					if (yDestNext == -1) {
						yDestNext = yDestination;
						yDestination = w.yEnd;
					} else {
						yDestination = yDestNext;
					}
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
		} else if (dir == Dir.all) {
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
		}
		
		dir = Dir.all;
		
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
		
		if (xPos == xDestination && yPos == yDestination) {
			if (xDestNext != -1) xDestination = xDestNext;
			if (yDestNext != -1) yDestination = yDestNext;
		}
		
		if (xPos == xDestFinal && yPos == yDestFinal) {
			
			if (command != Command.noCommand) moving.release();
			
			if (command == Command.LeaveHouse) {
				agent.msgAnimationLeavingFinished();
			}
			else if (command == Command.DoneMaintenance) agent.msgMaintenanceAnimationFinished();
			else if (command != Command.noCommand && command != Command.HelperMove) agent.msgAnimationFinished();
			
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
	
//	public void DoLeaveBedroom() {
//		if (type == "house") {
//			xDestination = (int)(hWidth*0.57);
//			yDestination = (int)(hHeight*0.65);
//		} else if (type == "apt") {
//			if (roomNo == 0) {
//				xDestination = (int)(hWidth*0.22);
//				yDestination = (int)(hHeight*0.52);
//			} else if (roomNo == 1) {
//				xDestination = (int)(hWidth*0.54);
//				yDestination = (int)(hHeight*0.52);
//			} else if (roomNo == 2) {
//				xDestination = (int)(hWidth*0.85);
//				yDestination = (int)(hHeight*0.52);
//			} else if (roomNo == 3) {
//				yDestination = (int)(hHeight*0.65);
//			}
//		}
//		command = Command.HelperMove;
//		try {
//			moving.acquire();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
		
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
	
//	private void GoTowardBed(int i) {
//		if (i == 0) {
//			xDestination = (int)(hWidth*0.57);
//			yDestination = (int)(hHeight*0.15);
//			command = Command.HelperMove;
//			inBedroom = true;
//		} else {
//			xDestination = (int)(hWidth*0.7);
//			yDestination = (int)(hHeight*0.15);
//			command = Command.GoToBed;
//		}
//		try {
//			moving.acquire();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
//	private void GoToAptRoom() {
//		if(roomNo == 0){
//			xDestination = (int)(hWidth*0.22);
//			yDestination = (int)(hHeight*0.38);
//		}else if(roomNo == 1){
//			xDestination = (int)(hWidth*0.54);
//			yDestination = (int)(hHeight*0.38);
//		}else if(roomNo == 2){
//			xDestination = (int)(hWidth*0.85);
//			yDestination = (int)(hHeight*0.38);
//		}else if(roomNo == 3){
//			xDestination = (int)(hWidth*0.56);
//			yDestination = (int)(hHeight*0.65);
//		}
//		command = Command.HelperMove;
//		try {
//			moving.acquire();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		inBedroom = true;
//	}
	
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
		if (type == "house"){
			for (int i=0; i<6; i++) {
				MaintainArea(i);
			}
		} else if (type == "apt"){
			for (int i=0; i<4; i++) {
				MaintainApt(i);
			}
		}
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
