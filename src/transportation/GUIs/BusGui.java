package transportation.GUIs;
import java.awt.Graphics2D;
import java.awt.Point;

import AnimationTools.AnimationModule;
import transportation.TransportationPanel;
import transportation.Agents.BusAgent;

public class BusGui implements Gui{
	
	private float xPos, yPos, xDestination, yDestination, xLast, yLast, speed;
	private AnimationModule animModule;
	boolean reachedHalfway, reachedDestination;
	
	TransportationPanel panel;
	BusAgent agent;
	boolean isPresent = true;
	
	public BusGui(float xPos, float yPos, BusAgent agent) {
		this.xPos = xPos * 25;
		this.yPos = yPos * 25 + 2;
		this.xDestination = xPos * 25;
		this.yDestination = yPos * 25 + 2;
		this.xLast = xPos * 25;
		this.yLast = yPos * 25 + 2;
		speed = 4.0f;
		this.agent = agent;		
		reachedHalfway = true;
		reachedDestination = true;
		
		animModule = new AnimationModule("Chocobus", "Down", 7);
	}
	
	public void updatePosition() {
		if(Math.abs(xDestination - xPos) <= speed)
			xPos = xDestination;
		else if(xPos < xDestination) {
			xPos += speed;
			animModule.changeAnimation("Right");
		}
		else if(xPos > xDestination) {
			xPos -= speed;
			animModule.changeAnimation("Left");
		}
		
		if(Math.abs(yDestination - yPos) <= speed)
			yPos = yDestination;
		if(yPos < yDestination) {
			yPos += speed;
			animModule.changeAnimation("Down");
		}
		else if(yPos > yDestination) {
			yPos -= speed;
			animModule.changeAnimation("Up");
		}
		
		if(Math.abs(((xDestination + xLast)/2)-xPos) <= speed || Math.abs(((yDestination + yLast)/2)-yPos) <= speed && !reachedHalfway) {
			agent.msgHalfway();
			reachedHalfway = true;
		}
		
		if(xPos == xDestination && yPos == yDestination && !reachedDestination) {
			xLast = xDestination;
			yLast = yDestination;
			reachedDestination = true;
			agent.msgDestination();
		}
	}

	public void draw(Graphics2D g, Point offset) {
		animModule.updateAnimation();
		if(xPos - offset.getX() < -30 || xPos - offset.getX() > panel.getWidth()+50 || yPos - offset.getY() < -30 || yPos - offset.getY() > panel.getHeight()+50) return;
		g.drawImage(animModule.getImage(), (int)xPos - (int)offset.getX(), (int)yPos - (int)offset.getY(), null);
	}

	public void setDestination (float xDestination, float yDestination) {
		xLast = xPos;
		yLast = yPos;
		this.xDestination = xDestination * 25;
		this.yDestination = yDestination * 25 + 2;
		reachedHalfway = false;
		reachedDestination = false;
	}
	
	public void setStill() {
		animModule.setStill();
		animModule.changeFrame(1);
	}
	
	public void setIgnore() {
		isPresent = false;
	}
	
	public boolean isPresent() {
		return isPresent;
	}
	
	public void setPanel(TransportationPanel p) {
		panel = p;
	}
	
	@Override
	public String returnType() {
		return "Bus";
	}
}
