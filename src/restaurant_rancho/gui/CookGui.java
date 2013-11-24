package restaurant_rancho.gui;
import restaurant_rancho.CookAgent;

import java.util.List;
import java.util.*;
import java.awt.*;

public class CookGui implements Gui{

	private boolean isPresent = true;

	//private HostAgent host;
	CookAgent cook;

	private int xPos, yPos;
	private int xDestination, yDestination;

	
	public static final int homeX = (RanchoAnimationPanel.WINDOWX*15)/20;
	public static final int homeY = (RanchoAnimationPanel.WINDOWY*5)/20;
	public static final int loc0X = (RanchoAnimationPanel.WINDOWX*15)/20;
	public static final int loc0Y = (RanchoAnimationPanel.WINDOWY*3)/20;
	public static final int loc2X = (RanchoAnimationPanel.WINDOWX*15)/20;
	public static final int loc2Y = (RanchoAnimationPanel.WINDOWY*7)/20;
	
	List<kitchenLoc> platingLocations;
	List<kitchenLoc> cookingLocations;
	boolean actionInProgress = false;
	String curText = "";
	
	String custText = "";
	
	
	public CookGui(CookAgent c){ //HostAgent m) {
		cook = c;
		xPos = (RanchoAnimationPanel.WINDOWX*15)/20;
		yPos = (RanchoAnimationPanel.WINDOWY*6)/20;
		//xDestination = (AnimationPanel.WINDOWX*15)/20;
		//yDestination = (AnimationPanel.WINDOWY*6)/20;
		platingLocations = Collections.synchronizedList(new ArrayList<kitchenLoc>());
		cookingLocations = Collections.synchronizedList(new ArrayList<kitchenLoc>());
		cookingLocations.add(new kitchenLoc(320, 90));
		cookingLocations.add(new kitchenLoc(320, 140));
		cookingLocations.add(new kitchenLoc(320, 190));
		platingLocations.add(new kitchenLoc(260, 90));
		platingLocations.add(new kitchenLoc(260, 140));
		platingLocations.add(new kitchenLoc(260, 190));
	}


	public void updatePosition() {
		if (actionInProgress==true) {
		if (xPos < xDestination)
			xPos++;
		else if (xPos > xDestination)
			xPos--;

		if (yPos < yDestination)
			yPos++;
		else if (yPos > yDestination)
			yPos--;

		if (xPos == xDestination && yPos == yDestination) {
			cook.msgAtLoc();
			actionInProgress = false;
		}
		}
	}

	public void draw(Graphics2D g) {
		
		g.setColor(Color.white);
    	g.setFont(new Font("helvetica", Font.BOLD, 16));
    	//Image panImage = Toolkit.getDefaultToolkit().getImage("res/pan.gif");
    	//g.drawImage(panImage, (AnimationPanel.WINDOWX*17)/20, (AnimationPanel.WINDOWY*5)/20, 45, 45, null);
    	//g.drawImage(panImage, (AnimationPanel.WINDOWX*17)/20, (AnimationPanel.WINDOWY*6)/20, 45, 45, null);
    	//g.drawImage(panImage, (AnimationPanel.WINDOWX*17)/20, (AnimationPanel.WINDOWY*7)/20, 45, 45, null);
		Image custImage = Toolkit.getDefaultToolkit().getImage("res/cook.gif");
		g.drawImage(custImage, xPos, yPos, 20, 25, null);
		g.setColor(Color.magenta.darker());
		g.drawString(curText, xPos +20, yPos+40);
		g.setColor(Color.white);
		synchronized(cookingLocations) {
			for (int i=0; i<3; i++ ){
				g.drawString(cookingLocations.get(i).text, cookingLocations.get(i).locX, cookingLocations.get(i).locY);
			}
		}
		synchronized(platingLocations) {
			for (int i=0; i<3; i++ ){
				g.drawString(platingLocations.get(i).text, platingLocations.get(i).locX, platingLocations.get(i).locY);
			}
		}
		g.finalize();
	}
	
	public void setText(String cText, int which, int num) {
		if (which == 0) {
			kitchenLoc loc = cookingLocations.get(num%3);
			loc.text = cText;
		}
		else if (which == 1) {
			kitchenLoc loc = platingLocations.get(num%3);
			loc.text = cText;
		}
	}
	public void setCurText(String cText) {
		curText = cText;
	}
	

	public boolean isPresent() {
		return isPresent;
	}
	
	public void setPresent(boolean p) {
		isPresent = p;
	}
	

	public void DoWalkToHome() {//later you will map seatnumber to table coordinates.
		xDestination = homeX;
		yDestination = homeY;
		actionInProgress = true;
	}
	
	public void DoWalkToFridge() {
		xDestination = loc0X;
		yDestination = loc0Y - 70;
		actionInProgress = true;
	}
	public void DoWalkToFood(int loc) {
		if (loc ==0) {
			xDestination = loc0X;
			yDestination = loc0Y;
		}
		else if (loc == 1) {
			DoWalkToHome();
		}
		else if (loc ==2) {
			xDestination = loc2X;
			yDestination = loc2Y;
		}
		actionInProgress = true;
		
	}
	
	private class kitchenLoc {
		int locX;
		int locY;
		boolean empty;
		String text;
		
		kitchenLoc(int x, int y) {
			locX = x;
			locY = y;
			empty = true;
			text = "";
		}
	}
	



}

	



