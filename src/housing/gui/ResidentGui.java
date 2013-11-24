package housing.gui;

import housing.ResidentAgent;

import java.awt.*;
import java.util.concurrent.Semaphore;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class ResidentGui implements Gui{

	private ResidentAgent agent = null;
	private boolean isPresent = false;
	private String text = "";

	private int xPos, yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand, EnterHouse, GoToCouch, GoToBed, AtBed, GoToBath, GoToTable, GoToKitchen, LeaveBedroom, DoMaintenance, DoneMaintenance, LeaveHouse};
	private Command command=Command.noCommand;
	
	private Semaphore moving = new Semaphore(0, true);
	private boolean inBedroom = false;

	public static final int hWidth = 400;
	public static final int hHeight = 360;

	public ResidentGui(ResidentAgent r){
		agent = r;
		agent.setGui(this);
		xPos = (int)(hWidth*0.27);
		yPos = (int)(hHeight*0.92);
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
			
			if (command == Command.DoMaintenance || command == Command.DoneMaintenance || command == Command.GoToBed || command == Command.AtBed || command == Command.LeaveBedroom) moving.release();
			
			if (command == Command.DoneMaintenance) agent.msgMaintenanceAnimationFinished();
			else if (command != Command.noCommand) agent.msgAnimationFinished();
			command=Command.noCommand;
			
		}
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.CYAN);
		g.fillRect(xPos, yPos, hWidth/20, hHeight/15);
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
	
	public void DoEnterHouse() {
		System.out.println("Entering house");
		setPresent(true);
		xDestination = (int)(hWidth*0.27);
		yDestination = (int)(hHeight*0.65);
		command = Command.EnterHouse;
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
		for (int i=0; i<2; i++) {
			GoTowardBed(i);
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
			xDestination = (int)(hWidth*0.8);
			yDestination = (int)(hHeight*0.15);
			command = Command.AtBed;
		}
	}
	
	public void DoGoToBath() {
		xDestination = -hWidth/5;
		yDestination = -hHeight/6;
		command = Command.GoToBath;
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
		xDestination = (int)(hWidth*0.27);
		yDestination = (int)(hHeight*0.65);
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
		for (int i=0; i<6; i++) {
			MaintainArea(i);
			try {
				moving.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
			xDestination = (int)(hWidth*0.27);
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
		xDestination = (int)(hWidth*0.8);
		yDestination = (int)(hHeight*0.8);
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
		xDestination = (int)(hWidth);
		yDestination = (int)(hHeight*0.15);
		command = Command.LeaveHouse;
	}
	
	public void setText(String t) {
		text = t;
	}
}
