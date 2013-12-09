package restaurant_cafe.gui;


import restaurant_cafe.CookAgent;
import restaurant_cafe.CustomerAgent;
import simcity.gui.SimCityGui; 
import simcity.interfaces.Person;

import java.awt.*;

public class CookGui implements Gui {

    private CookAgent agent = null;
    private int xPos, yPos;//default Cook position
    private int xDestination = 530, yDestination = 430;//default start position
	private enum Command {cook, plate, noCommand};
	private Command command = Command.noCommand;
	private String grillName = "";
	private String plateName = "";
	boolean grillVisible = false, plateVisible = false;
	SimCityGui gui;
	int aniWindowX = 400;
	int aniWindowY = 400; 
	private boolean leaving = false;
	Person person;

    public CookGui(CookAgent agent, SimCityGui gui) {
        this.agent = agent;
    	this.gui = gui;
		xPos = 530;
    	yPos = 430;
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
        	if(command == Command.cook) {
        		grillVisible = true;
        	}
        	if (leaving == true) {
        		leaving = false;
        		if (person!=null) person.msgStopWork(10);
        	}
        	else if(command == Command.plate){
        		plateVisible = true;
        	}
        	command = Command.noCommand;
        }
  
    }

    public void draw(Graphics2D g) {
    	//System.out.println(xPos + " " + yPos);
        g.setColor(Color.WHITE);
        g.fillRect(xPos, yPos, 20, 20);
		g.setColor(Color.BLACK);
		g.drawString("C", xPos+5, yPos+15);
		
		if(grillVisible == true){
			g.setColor(Color.WHITE);
			g.fillRect(aniWindowX-80, aniWindowY-40, 20, 20);
			g.setColor(Color.BLACK);
			g.drawString(grillName, aniWindowX-75, aniWindowY-25);
		}
		if(plateVisible == true){
			g.setColor(Color.WHITE);
			g.fillRect(aniWindowX-40, aniWindowY-80, 20, 20);
			g.setColor(Color.BLACK);
			g.drawString(plateName, aniWindowX-35, aniWindowY-65);
		}
    }

    public boolean isPresent() {
        return true;
    }
    
    public void DoLeave(Person p) {
    	xDestination = -50;
    	yDestination = -50; 
    	person = p;
    	leaving = true;
    }
    public void DoGrilling(String name){
    	xDestination = aniWindowX-60;
    	yDestination = aniWindowY-20;
    	grillName = getOrderString(name);
		command = Command.cook;
    }
    public void DoPlating(String name){
    	xDestination = aniWindowX-20;
    	yDestination = aniWindowY-60;
    	plateName = getOrderString(name);
		command = Command.plate;
    }
    public void DoneGrilling(){
    	grillVisible = false;
		command = Command.noCommand;
    }
    public void RemovePlate(){
    	plateVisible = false;
		command = Command.noCommand;
    }
    
	public String getOrderString(String choice){
		String orderString = "";
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
		return orderString;
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