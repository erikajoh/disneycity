package restaurant_cafe.gui;


import restaurant_cafe.CustomerAgent;
import restaurant_cafe.WaiterAgent;
import simcity.gui.SimCityGui;

import java.awt.*;

public class WaiterGui implements Gui {

    private WaiterAgent agent = null;
    private int xPos = -20, yPos = -20;//default waiter position
    private int xDestination = -20, yDestination = -20;//default start position
    private String initial = "";
    private String orderString = "";
    private boolean onBreak = false;
    int tableDestination;
	private enum Command {noCommand, walkToHost, walkToTable, walkToCook, walkToCashier};
	private Command command = Command.noCommand;
	SimCityGui gui;

    public WaiterGui(WaiterAgent agent, SimCityGui gui) {
        this.agent = agent;
        if(agent.getName() != null){
		initial = agent.getName().substring(0, 1);
        }
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
        
        if (xPos == xDestination && yPos == yDestination){
        	if(command == Command.walkToTable) {
        		agent.msgAtTable(tableDestination);
        	}
        	else if(command == Command.walkToCook){
        		agent.msgAtCook();
       		 	command = Command.noCommand;
        	}
        	else if(command == Command.walkToHost){
        		System.out.println("AT HOST");
       		 	agent.msgAtHost();
       		 	command = Command.noCommand;
        	}
        	else if(command == Command.walkToCashier){
        		System.out.println("AT CASHIER");
       		 	agent.msgAtCashier();
       		 	command = Command.noCommand;
        	}
        }
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.MAGENTA);
        g.fillRect(xPos, yPos, 20, 20);
		g.setColor(Color.BLACK);
		g.drawString(initial, xPos+5, yPos+15);
		
		if(!orderString.equals("")){
			g.setColor(Color.WHITE);
			g.fillRect(xPos, yPos+20, 20, 20);
			g.setColor(Color.BLACK);
			g.drawString(orderString, xPos+5, yPos+35);
		}
    }

    public boolean isPresent() {
        return true;
    }
    
    public void DoGoToTable(int tableNumber) {
		TableList tableList = new TableList();
    	Point tablePoint = tableList.getTablePoint(tableNumber);
    	tableDestination = tableNumber;
		xDestination = tablePoint.x + 20;
		yDestination = tablePoint.y - 20;
		command = Command.walkToTable;
    }
    
    public void DoGoToCook(){
    	xDestination = 400-80;
		yDestination = 330-80;
		command = Command.walkToCook;
    }

    public void DoLeaveCustomer(int num) {
    	System.out.println("Leave customer");
        xDestination = 20*(num/4);
        yDestination = 330-20*(num%4);
        command = Command.walkToHost;
    }
    public void DoGoToHost(int num) {
    	System.out.println("Go to host");
        xDestination = 60-20*(num%3);
        yDestination = 20*(num/3)+20;
        command = Command.walkToHost;
    }
    
    public void DoGoToCashier() {
    	System.out.println("Go to cashier");
        xDestination = 200;
        yDestination = 30;
        command = Command.walkToCashier;
    }
    
	public void setOrderString(String choice){
		if(choice.equals("Steak")){
			orderString = "St";
		}
		else if(choice.equals("Chicken")){
			orderString = "C";
		}
		else if(choice.equals("Salad")){
			orderString = "Sa";
		}
		else if(choice.equals("Pizza")){
			orderString = "P";
		}
		else{
			orderString = "";
		}
	}
	
	public void goOnBreak() {
		onBreak = true;
		agent.msgGoOnBreak();
	}
	public void endBreak() {
		onBreak = false;
		//gui.setOffBreak(agent);
		agent.msgEndBreak();
	}
	
	public boolean isOnBreak(){
		return onBreak;
	}

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
    public int getTableXPos() {
        return xDestination;
    }
    public int getTableYPos() {
        return yDestination;
    }

}