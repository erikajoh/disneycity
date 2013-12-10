package transportation.GUIs;

import java.awt.Graphics2D;
import java.awt.Point;

import transportation.Agents.TransportationController;
import AnimationTools.AnimationModule;
import astar.astar.Position;

public class CrashGui implements Gui{
	Position crashPosition;
	AnimationModule animModule;
	CrashState state;
	final float fallSpeed = 10.0f;
	int prayLooping = 0;
	TransportationController controller;
	
	float xPos = 0.00f; 
	float yPos = 0.00f;
	
	enum CrashState {
		EXPLOSION,
		FALL,
		PRAY,
		CAST,
		DONE,
		LAND
	}
	
	public CrashGui(Position position, boolean skipExplosion, TransportationController controller) {
		crashPosition = new Position(position.getX(), position.getY());
		this.controller = controller;
		if(skipExplosion) {//Celes just falls from the sky
			xPos = position.getX() * 25 - 10;
			yPos = position.getY() * 25 - 112;
			
			animModule = new AnimationModule("Celes", "Fall", 8);
			animModule.setStill();
			state = CrashState.FALL;
		}
		else {//THE CRASH IS EXPLOSiONS
			xPos = position.getX() * 25 - 4;
			yPos = position.getY() * 25 - 14;
			
			animModule = new AnimationModule("Explosion", "Explosion", 2);
			state = CrashState.EXPLOSION;
		}
	}
	
	@Override
	public void updatePosition() {
		switch(state) {
		case EXPLOSION:
			if(animModule.getLastFrame()) {
				state = CrashState.FALL;
				xPos = crashPosition.getX() * 25 - 10;
				yPos = crashPosition.getY() * 25 - 112;
				animModule.setCharacter("Celes");
				animModule.changeAnimation("Fall", 8);
				animModule.setStill();
			}
			break;
		case FALL:
			if(yPos != crashPosition.getY() * 25 - 12)
				yPos += 5;
			if(yPos >= crashPosition.getY() * 25 - 12) {
				animModule.changeFrame(2);
				state = CrashState.LAND;
				animModule.setMoving();
			}
			break;
		case LAND:
			if(animModule.getLastFrame()) {
				state = CrashState.PRAY;
				animModule.changeAnimation("Praying", 5);
			}
			break;
		case PRAY:
			if(animModule.getLastFrame())
				prayLooping++;
			if(prayLooping == 100) {
				prayLooping = 0;
				state = CrashState.CAST;
				animModule.changeAnimation("Cast", 3);
			}
			break;
		case CAST:
			if(animModule.getLastFrame()) {
				state = CrashState.DONE;
				controller.msgCrashCompleted(this);
			}
			break;
		case DONE:
			break;
		}
		
	}

	@Override
	public void draw(Graphics2D g, Point offset) {
		animModule.updateAnimation();
		g.drawImage(animModule.getImage(), (int)xPos - (int)offset.getX(), (int)yPos - (int)offset.getY(), null);
	}

	@Override
	public boolean isPresent() {
		// TODO Auto-generated method stub
		return (state != CrashState.DONE);
	}

}
