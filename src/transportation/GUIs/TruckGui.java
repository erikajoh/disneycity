package transportation.GUIs;
import java.awt.Graphics2D;
import java.awt.Point;

import AnimationTools.AnimationModule;
import transportation.TransportationPanel;
import transportation.Agents.TruckAgent;

public class TruckGui implements Gui{
	
	private float xPos, yPos, xDestination, yDestination, xLast, yLast, speed;
	private AnimationModule animModule;
	boolean reachedHalfway, reachedDestination;
	
	TransportationPanel panel;
	TruckAgent agent;
	boolean isPresent = true;
	
	public TruckGui(float xPos, float yPos, TruckAgent agent) {
		this.xPos = xPos * 25 -3;
		this.yPos = yPos * 25;
		this.xDestination = xPos * 25 -3;
		this.yDestination = yPos * 25;
		this.xLast = xPos * 25 - 3;
		this.yLast = yPos * 25;
		speed = 1.66f;
		this.agent = agent;		
		reachedHalfway = true;
		reachedDestination = true;
		
		animModule = new AnimationModule("Pelipper", "Idle", 5);
	}
	
	public void updatePosition() {
		if(Math.abs(xDestination - xPos) <= speed)
			xPos = xDestination;
		else if(xPos < xDestination) {
			xPos += speed;
			animModule.changeAnimation("Right", 5);
		}
		else if(xPos > xDestination) {
			xPos -= speed;
			animModule.changeAnimation("Left", 5);
		}
		
		if(Math.abs(yDestination - yPos) <= speed)
			yPos = yDestination;
		if(yPos < yDestination) {
			yPos += speed;
			animModule.changeAnimation("Down", 5);
		}
		else if(yPos > yDestination) {
			yPos -= speed;
			animModule.changeAnimation("Up", 5);
		}
		
		if(Math.abs(xLast - xPos) >= 15 || Math.abs(yLast - yPos) >= 15 && !reachedHalfway) {
			agent.msgHalfway();
			reachedHalfway = true;
		}
		
		if(xPos == xDestination && yPos == yDestination && !reachedDestination) {
			reachedDestination = true;
//			System.out.println("Reached Destination");
			agent.msgDestination();
		}
		
		if(animModule.getAnimation().equals("Deliver") && animModule.getLastFrame())
			agent.msgDestination();
	}
	
	public void doIdle() {
		animModule.changeAnimation("Idle", 30);
	}
	
	public void doDeliveryDance() {
		animModule.changeAnimation("Deliver", 5);
	}
	
	public void draw(Graphics2D g, Point offset) {
		animModule.updateAnimation();
		if(xPos - offset.getX() < -30 || xPos - offset.getX() > panel.getWidth()+50 || yPos - offset.getY() < -30 || yPos - offset.getY() > panel.getHeight()+50) return;
		g.drawImage(animModule.getImage(), (int)xPos - (int)offset.getX(), (int)yPos - (int)offset.getY(), null);
	}

	public void setDestination (float xDestination, float yDestination) {
		xLast = xPos;
		yLast = yPos;
		this.xDestination = xDestination * 25 - 3;
		this.yDestination = yDestination * 25;
		reachedHalfway = false;
//		System.out.println("resetting Destination");
		reachedDestination = false;
	}
	
	public void setIgnore() {
		isPresent = false;
	}
	
	public boolean isPresent() {
		return isPresent;
	}

	@Override
	public void setPanel(TransportationPanel p) {
		panel = p;
	}
	@Override
	public String returnType() {
		return "Truck";
	}
}
