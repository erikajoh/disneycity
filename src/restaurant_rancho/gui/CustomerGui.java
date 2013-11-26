package restaurant_rancho.gui;

import restaurant_rancho.CustomerAgent;
import restaurant_rancho.HostAgent;
import simcity.gui.SimCityGui;
import AnimationTools.AnimationModule;

import java.awt.*;

public class CustomerGui implements Gui{

	private CustomerAgent agent = null;
	private boolean isPresent = false;
	private boolean isHungry = false;
	private boolean sitting = false;
	private enum direction { UP, DOWN, LEFT, RIGHT};
	private direction currDir = direction.UP;

	//private HostAgent host;
	SimCityGui gui;

	private int xPos, yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand, GoToWaitingSpot, GoToSeat, LeaveRestaurant};
	private Command command=Command.noCommand;
	AnimationModule animModule = new AnimationModule();
	
	public static final int xTable1 = (RanchoAnimationPanel.WINDOWX/6);
	public static final int yTable1 = (RanchoAnimationPanel.WINDOWY*25)/40;
	public static final int xTable2 = xTable1+90;
	public static final int yTable2 = yTable1;
	public static final int xTable3 = xTable2+90;
	public static final int yTable3 = yTable1;
	
	String custText = "";
	public int waitNum;
	
	
	public CustomerGui(CustomerAgent c, SimCityGui gui, int num){ //HostAgent m) {
		agent = c;
		xPos = -50;
		yPos = -50;
		command = Command.GoToWaitingSpot;
		xDestination = 25 + ((num%15)%5)*40;
		yDestination = 25 - ((num%15)/5)*40;
		//maitreD = m;
		waitNum = num;
		this.gui = gui;
	}


	public void updatePosition() {
		if (xPos < xDestination) {
			xPos++;
			currDir = direction.RIGHT;
		}
		else if (xPos > xDestination) {
			xPos--;
			currDir = direction.LEFT;
		}
		if (yPos < yDestination) {
			yPos++;
			currDir = direction.DOWN;
		}
		else if (yPos > yDestination) {
			yPos--;
			currDir = direction.UP;
		}
		
		sitting = xPos == xDestination && yPos == yDestination;
		if(sitting) {
			animModule.changeAnimation("Stand");
			animModule.setStill();
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
			if (command==Command.GoToSeat) {agent.msgAnimationFinishedGoToSeat(); }
			else if (command==Command.LeaveRestaurant) {
				agent.msgAnimationFinishedLeaveRestaurant();
				isHungry = false;
			}
			else if (command == Command.GoToWaitingSpot) {
				agent.msgAnimationFinishedGotToWaitingSpot();
			}
			command=Command.noCommand;
		}
	}

	public void draw(Graphics2D g) {
		
		g.setColor(Color.blue);
    	g.setFont(new Font("helvetica", Font.BOLD, 16));
		//Image custImage = Toolkit.getDefaultToolkit().getImage("res/customer.gif");
		//g.drawImage(custImage, xPos, yPos, 30, 30, null);
    	animModule.updateAnimation();//updates the frame and animation 
    	g.drawImage(animModule.getImage(), (int)xPos, (int)yPos, null);
		g.drawString(custText, xPos+30, yPos+20);
		g.finalize();
	}
	
	public void setText(String cText) {
		custText = cText;
	}

	public boolean isPresent() {
		return isPresent;
	}
	
	
	public void setHungry() {
		//if (agent.host.hostGui.getXPos() == -20 && agent.host.hostGui.getYPos() == -20) {
		isHungry = true;
		agent.gotHungry();
		setPresent(true);
		//}
		
	}
	public boolean isHungry() {
		return isHungry;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}
	
	public void DoGoToRestaurant(int loc) {
		xDestination = 25 + ((loc%15)%5)*40;
		yDestination = 25 - ((loc%15)/5)*40;
	}
	
	public void DoGoToSeat(int seatnumber) {//later you will map seatnumber to table coordinates.
		if (seatnumber==1){
		    xDestination = xTable1;
		    yDestination = yTable1;
		}
		else if (seatnumber==2){
			xDestination = xTable2;
			yDestination = yTable2;
		}
		else if (seatnumber==3) {
			xDestination = xTable3;
			yDestination = yTable3;
		}
		
		command = Command.GoToSeat;
	}

	public void DoExitRestaurant() {
		
		xDestination = -50;
		yDestination = -50;
		command = Command.LeaveRestaurant;
	}
}
