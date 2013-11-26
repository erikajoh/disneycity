package restaurant_cafe.gui;

import restaurant_cafe.CustomerAgent;
import simcity.gui.SimCityGui;
import AnimationTools.AnimationModule;


import java.awt.*;

public class CustomerGui implements Gui{

	private CustomerAgent agent = null;
	private boolean isPresent = false;
	private boolean isHungry = false;

	SimCityGui gui;
	AnimationModule animModule = new AnimationModule();
	private int xPos, yPos;
	private int xDestination, yDestination;
	private String drawString = "";
	private String initial;
	private enum Command {noCommand, GoToRestaurant, GoToSeat, GoToCashier, GoToCook, LeaveRestaurant}; //shortened to noCommand and walking?
	private Command command=Command.noCommand;

	public static final int xTable1 = 160;
	public static final int yTable1 = 250;
	public static final int xTable2 = 360;
	public static final int yTable2 = 100;
	public static final int xTable3 = 360;
	public static final int yTable3 = 300;

	public CustomerGui(CustomerAgent c, SimCityGui gui){ //HostAgent m) {
		agent = c;
		xPos = -40;
		yPos = -40;
		xDestination = -40;
		yDestination = -40;
		initial = c.toString().substring(9, 10);
		//maitreD = m;
		this.gui = gui;
	}

	public void updatePosition() {
		if (xPos < xDestination)
			xPos++;
		else if (xPos > xDestination)
			xPos--;

		if (yPos < yDestination)
			yPos++;
		else if (yPos > yDestination)
			yPos--;

		if (xPos == xDestination && yPos == yDestination) {
			if (command==Command.GoToSeat) {
				agent.msgAnimationFinishedGoToSeat();
			}
			else if (command==Command.GoToCashier) {
				agent.msgAnimationFinishedGoToCashier();
			}
			else if (command==Command.GoToCook) {
				agent.msgAnimationFinishedGoToCook();
			}
			else if (command==Command.LeaveRestaurant) {
				agent.msgAnimationFinishedLeaveRestaurant();
				isHungry = false;
				//gui.setCustomerEnabled(agent);
			}
			animModule.setStill();
			command=Command.noCommand;
		}
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.CYAN);
		//g.fillRect(xPos, yPos, 20, 20);
		animModule.updateAnimation();//updates the frame and animation 
		g.drawImage(animModule.getImage(), (int)xPos, (int)yPos, null);
		g.setColor(Color.BLACK);
		g.drawString(initial, xPos+5, yPos+15);
		
		if(!drawString.equals("")){
			g.setColor(Color.WHITE);
			g.fillRect(xPos+20, yPos, 20, 20);
			g.setColor(Color.BLACK);
			g.drawString(drawString, xPos+20, yPos+15);
		}
	}
	
	public void setDrawString(String choice, boolean eating){
		if(choice.equals("Steak")){
			drawString = "St";
		}
		else if(choice.equals("Chicken")){
			drawString = "C";
		}
		else if(choice.equals("Salad")){
			drawString = "Sa";
		}
		else if(choice.equals("Pizza")){
			drawString = "P";
		}
		else{
			drawString = "";
		}
		if(!eating){
			drawString += "?";
		}
	}

	public boolean isPresent() {
		return isPresent;
	}
	public void setHungry() {
		isHungry = true;
		agent.msgGotHungry();
		setPresent(true);
	}
	public boolean isHungry() {
		return isHungry;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}

	public void DoGoToSeat(int seatnumber) {//later you will map seatnumber to table coordinates.
		TableList tableList = new TableList();
    	Point tablePoint = tableList.getTablePoint(seatnumber);
		xDestination = tablePoint.x;
		yDestination = tablePoint.y;
		command = Command.GoToSeat;
	}
	
	public void DoGoToRestaurant(int num){
		xDestination = 40-20*(num%3);
		yDestination = 20*(num/3);
		command = Command.GoToRestaurant;
	}
	
	public void DoGoToCashier() {
		xDestination = 200;
		yDestination = 30;
		command = Command.GoToCashier;
	}
	
	public void DoGoToCook() {
		xDestination = 570;
		yDestination = 470;
		command = Command.GoToCook;
	}

	public void DoExitRestaurant() {
		xDestination = -40;
		yDestination = -40;
		command = Command.LeaveRestaurant;
	}
}
