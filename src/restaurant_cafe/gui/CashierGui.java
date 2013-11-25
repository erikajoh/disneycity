package restaurant_cafe.gui;

import restaurant_cafe.CashierAgent;
import simcity.gui.SimCityGui;


import java.awt.*;

public class CashierGui implements Gui{

	private CashierAgent agent = null;
	private boolean isPresent = false;
	private Font cashFont = new Font("Sans-Serif", Font.BOLD, 14);
	private Font otherFont = new Font("Sans-Serif", Font.BOLD, 12);

	SimCityGui gui;

	public CashierGui(CashierAgent c, SimCityGui gui){
		agent = c;
		setPresent(true);
		this.gui = gui;
	}

	public void updatePosition() {
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.GREEN);
		g.fillRect(200, 0, 30, 30);
		g.setColor(Color.BLACK);
		g.setFont(cashFont);
		g.drawString("$", 210, 20);
		g.setFont(otherFont);
	}
	
	public boolean isPresent() {
		return isPresent;
	}
	
	public void setPresent(boolean p) {
		isPresent = p;
	}
}