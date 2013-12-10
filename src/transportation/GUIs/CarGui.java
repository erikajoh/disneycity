package transportation.GUIs;
import java.awt.Graphics2D;
import java.awt.Point;

import AnimationTools.AnimationModule;
import transportation.TransportationPanel;
import transportation.Agents.CarAgent;

public class CarGui implements Gui{

	private float xPos, yPos, xDestination, yDestination, xLast, yLast, speed;
	private AnimationModule animModule;
	boolean reachedHalfway, reachedDestination;
	boolean crashed = false;

	TransportationPanel panel;
	CarAgent agent;
	boolean isPresent = true;

	public CarGui(float xPos, float yPos, CarAgent agent) {
		this.xPos = xPos * 25;
		this.yPos = yPos * 25;
		this.xDestination = xPos * 25;
		this.yDestination = yPos * 25;
		this.xLast = xPos * 25;
		this.yLast = yPos * 25;
		speed = 1.25f;
		this.agent = agent;		
		reachedHalfway = true;
		reachedDestination = true;

		animModule = new AnimationModule("Car", "Down", 10);
	}

	public void updatePosition() {
		if(!crashed) {
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

			if((Math.abs(((xDestination + xLast)/2)-xPos) <= speed || Math.abs(((yDestination + yLast)/2)-yPos) <= speed) && !reachedHalfway) {
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
	}

	public void draw(Graphics2D g, Point offset) {
		animModule.updateAnimation();
		if(xPos - offset.getX() < -30 || xPos - offset.getX() > 500 || yPos - offset.getY() < -30 || yPos - offset.getY() > 500)
			return;
		g.drawImage(animModule.getImage(), (int)xPos - (int)offset.getX(), (int)yPos - (int)offset.getY(), null);
		g.drawString(agent.getPerson().getName(), (int)xPos - (int)offset.getX(), (int)yPos - (int)offset.getY());
	}

	public void setDestination (float xDestination, float yDestination) {
		xLast = xPos;
		yLast = yPos;
		this.xDestination = xDestination * 25;
		this.yDestination = yDestination * 25;
		reachedHalfway = false;
		reachedDestination = false;
	}

	public void setIgnore() {
		isPresent = false;
	}

	public boolean isPresent() {
		return isPresent;
	}

	public void crash() {
		//Release other semaphore if haven't already
		//Change animation
		//Change boolean to prevent position updating
		if(!reachedHalfway) {
			agent.msgHalfway();
			reachedHalfway = true;
		}
		animModule.changeAnimation("Crash", 10);
		animModule.setNoLoop();
		crashed = true;
		agent.stopThread();
	}
	
	@Override
	public void setPanel(TransportationPanel p) {
		panel = p;
	}
	
	@Override
	public String returnType() {
		return "Car";
	}
}
