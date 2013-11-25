package transportation.GUIs;
import java.awt.Graphics2D;

import AnimationTools.AnimationModule;
import simcity.gui.*;
import transportation.Agents.WalkerAgent;

public class WalkerGui implements Gui{
	
	private float xPos, yPos, xDestination, yDestination, xLast, yLast, speed;
	private AnimationModule animModule;
	boolean reachedHalfway, reachedDestination;
	
	
	WalkerAgent agent;
	boolean isPresent = true;
	
	public WalkerGui(float xPos, float yPos, WalkerAgent agent) {
		this.xPos = xPos * 25;
		this.yPos = yPos * 25;
		this.xDestination = xPos * 25;
		this.yDestination = yPos * 25;
		this.xLast = xPos * 25;
		this.yLast = yPos * 25;
		speed = 1.00f;
		this.agent = agent;		
		reachedHalfway = false;
		reachedDestination = false;
		
		animModule = new AnimationModule("Edgar", "Down");
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
			reachedDestination = true;
			agent.msgDestination();
		}
		
		if(animModule.getAnimation().equals("Taunt") && animModule.getLastFrame())
			agent.msgDestination();
	}

	public void draw(Graphics2D g) {
		animModule.updateAnimation();
		g.drawImage(animModule.getImage(), (int)xPos, (int)yPos, null);
	}

	public void setDestination (float xDestination, float yDestination) {
		xLast = xDestination;
		yLast = yDestination;
		this.xDestination = xDestination * 25;
		this.yDestination = yDestination * 25;
	}
	
	public void doTauntAndLeave() {
		animModule.changeAnimation("Taunt");
	}
	
	public void setIgnore() {
		isPresent = false;
	}
	
	public boolean isPresent() {
		return isPresent;
	}
}
