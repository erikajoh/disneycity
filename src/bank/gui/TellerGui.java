package bank.gui;

import bank.TellerAgent;
import simcity.gui.SimCityGui;

import java.awt.*;

public class TellerGui implements Gui{

	private TellerAgent agent = null;
	private boolean isPresent = false;
	private Image pirateTeller; 
	private int number;
	private int baseX;
	private int baseY;

	SimCityGui gui;

	public TellerGui(TellerAgent c, SimCityGui gui, int num){
		agent = c;
		setPresent(true);
		number = num;
		baseX = number*90+49;
		baseY = 80;
		this.gui = gui;
	}

	public void updatePosition() {
	}

	public void draw(Graphics2D g) {
		
		pirateTeller = Toolkit.getDefaultToolkit().getImage("res/pirateTeller.png");
	    g.drawImage(pirateTeller, number*90+49, 33, 35, 35, null);
	}
	
	public void setBaseX(int bx) {
		baseX = bx;
	}
	
	public void setBaseY(int by) {
		baseY = by;
	}
	
	public int getBaseX() {
		return baseX;
	}
	
	public int getBaseY() {
		return baseY;
	}

	
	public boolean isPresent() {
		return isPresent;
	}
	
	public void setPresent(boolean p) {
		isPresent = p;
	}
}