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
		
		animModule = new AnimationModule("Edgar", "WalkDown", 10);
	}
	
	public void updatePosition() {
		if(Math.abs(xDestination - xPos) <= speed)
			xPos = xDestination;
		else if(xPos < xDestination) {
			xPos += speed;
			animModule.changeAnimation("WalkRight");
		}
		else if(xPos > xDestination) {
			xPos -= speed;
			animModule.changeAnimation("WalkLeft");
		}
		
		if(Math.abs(yDestination - yPos) <= speed)
			yPos = yDestination;
		if(yPos < yDestination) {
			yPos += speed;
			animModule.changeAnimation("WalkDown");
		}
		else if(yPos > yDestination) {
			yPos -= speed;
			animModule.changeAnimation("WalkUp");
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
		
		if(animModule.getAnimation().equals("Taunt") && animModule.getLastFrame())
			agent.msgDestination();
	}

	public void draw(Graphics2D g) {
		animModule.updateAnimation();
		g.drawImage(animModule.getImage(), (int)xPos, (int)yPos, null);
	}

	public void setDestination (float xDestination, float yDestination) {
		this.xDestination = xDestination * 25;
		this.yDestination = yDestination * 25+2;
		reachedHalfway = false;
		reachedDestination = false;
	}
	
	public void doTauntAndLeave() {
		animModule.changeAnimation("Taunt", 20);
	}
	
	public void setIgnore() {
		isPresent = false;
	}
	
	public boolean isPresent() {
		return isPresent;
	}
}
