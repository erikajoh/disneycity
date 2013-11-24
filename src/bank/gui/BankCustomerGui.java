package bank.gui;

import bank.BankCustomerAgent;

import java.awt.*;

public class BankCustomerGui implements Gui{

	private BankCustomerAgent agent = null;
	private boolean isPresent = false;

	BankGui gui;

	private int windowX, windowY;
	private int xPos, yPos;
	private int xDestination, yDestination;
	private String drawString = "";
	private String initial;
	private enum Command {noCommand, GoToTeller, LeaveBank}; //shortened to noCommand and walking?
	private Command command=Command.noCommand;
	private boolean isInBank = false;


	public BankCustomerGui(BankCustomerAgent c, BankGui gui, int wx, int wy){ //HostAgent m) {
		agent = c;
		windowX = wx;
		windowY = wy;
		xPos = windowX/2-10;
		yPos = windowY-25;
		xDestination = xPos;
		yDestination = yPos;
		initial = c.toString().substring(9, 10);
		isPresent = true;
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

	public void draw(Graphics2D g) {
		g.setColor(Color.CYAN);
		g.fillRect(xPos, yPos, 20, 20);
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
	
	public void setInBank(boolean inBank) {
		isInBank = inBank;
	}
	
	public boolean isInBank() {
		return isInBank;
	}
	
	public void setPresent(boolean p) {
		isPresent = p;
	}

	public void DoGoToTeller(int xd, int yd) {//later you will map seatnumber to table coordinates.
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
