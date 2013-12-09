package transportation.GUIs;
import java.awt.Graphics2D;
import java.awt.Point;

import AnimationTools.AnimationModule;
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
		speed = 1.50f;
		this.agent = agent;		
		reachedHalfway = true;
		reachedDestination = true;
		
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
		
		if(animModule.getAnimation().equals("Taunt") && animModule.getLastFrame())
			agent.msgDestination();
	}

	public void draw(Graphics2D g, Point offset) {
		animModule.updateAnimation();
		if(xPos - offset.getX() < -30 || xPos - offset.getX() > 500 || yPos - offset.getY() < -30 || yPos - offset.getY() > 500)
			return;
		g.drawImage(animModule.getImage(), (int)xPos - (int)offset.getX(), (int)yPos - (int)offset.getY(), null);
	}

	public void setDestination (float xDestination, float yDestination) {
		xLast = xPos;
		yLast = yPos;
		this.xDestination = xDestination * 25;
		this.yDestination = yDestination * 25+2;
		reachedHalfway = false;
		reachedDestination = false;
	}
	
	public void doTauntAndLeave() {
		animModule.changeAnimation("Taunt", 10);
	}
	
	public void setIgnore() {
		isPresent = false;
	}
	
	public void setStill() {
		animModule.setStill();
	}
	
	public void setMoving() {
		animModule.setMoving();
	}
	
	public boolean isPresent() {
		return isPresent;
	}
}
