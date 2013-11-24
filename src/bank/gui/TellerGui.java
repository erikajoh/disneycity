package bank.gui;

import bank.TellerAgent;

import java.awt.*;

public class TellerGui implements Gui{

	private TellerAgent agent = null;
	private boolean isPresent = false;
	private Font cashFont = new Font("Sans-Serif", Font.BOLD, 14);
	private Font otherFont = new Font("Sans-Serif", Font.BOLD, 12);
	private int number;
	private int initialX;
	private int initialY;
	private int baseX;
	private int baseY;

	BankGui gui;

	public TellerGui(TellerAgent c, BankGui gui, int num){
		agent = c;
		setPresent(true);
		number = num;
		baseX = number*90+65;
		baseY = 60;
		this.gui = gui;
	}

	public void updatePosition() {
	}

	public void draw(Graphics2D g) {
		
		g.setColor(Color.GREEN);
		g.fillRect(number*90+65, 30, 20, 20);
		g.setColor(Color.BLACK);
		g.setFont(cashFont);
		g.drawString("$", number*90+70, 45);
		g.setFont(otherFont);
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