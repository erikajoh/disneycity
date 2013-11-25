package transportation.GUIs;
import java.awt.Graphics2D;

import AnimationTools.AnimationModule;
import simcity.gui.*;

public class CarGUI implements Gui{
	
	private float xPos, yPos, xDestination, yDestination, xLast, yLast, speed;
	private AnimationModule animModule;
	boolean reachedHalfway, reachedDestination;
	
	boolean isPresent = true;
	
	public CarGUI(float xPos, float yPos, float xDestination, float yDestination) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.xDestination = xDestination;
		this.yDestination = yDestination;
		speed = 1.25f;
		
		reachedHalfway = false;
		reachedDestination = false;
		
		animModule = new AnimationModule("Car", "Down");
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
		
	}

	public void draw(Graphics2D g) {
		animModule.updateAnimation();
		g.drawImage(animModule.getImage(), (int)xPos, (int)yPos, null);
	}

	public void setDestination (float xDestination, float yDestination) {
		xLast = xDestination;
		yLast = yDestination;
		this.xDestination = xDestination;
		this.yDestination = yDestination;
	}
	
	public void setIgnore() {
		isPresent = false;
	}
	
	public boolean isPresent() {
		return isPresent;
	}
	
}