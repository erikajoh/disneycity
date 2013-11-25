package transportation.GUIs;
import java.awt.Graphics2D;

import AnimationTools.AnimationModule;
import simcity.gui.*;

public class CarGUI implements Gui{
	
	private float xPos, yPos, xDestination, yDestination, speed;
	private AnimationModule animModule;
	
	boolean isPresent = true;
	
	public CarGUI(float xPos, float yPos, float xDestination, float yDestination) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.xDestination = xDestination;
		this.yDestination = yDestination;
		speed = 1.25f;
		
		animModule = new AnimationModule("Car", "Down");
	}
	
	public void updatePosition() {
		if(xPos < xDestination) {
			xPos += speed;
			animModule.changeAnimation("Right");
		}
		else if(xPos > xDestination) {
			xPos -= speed;
			animModule.changeAnimation("Left");
		}
		
		if(yPos < yDestination) {
			yPos += speed;
			animModule.changeAnimation("Down");
		}
		else if(yPos > yDestination) {
			yPos -= speed;
			animModule.changeAnimation("Up");
		}
	}

	public void draw(Graphics2D g) {
		animModule.updateAnimation();
		g.drawImage(animModule.getImage(), (int)xPos, (int)yPos, null);
	}

	public void setIgnore() {
		isPresent = false;
	}
	
	public boolean isPresent() {
		return isPresent;
	}
	
}
