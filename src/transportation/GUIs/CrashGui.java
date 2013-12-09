package transportation.GUIs;

import java.awt.Graphics2D;
import java.awt.Point;

import AnimationTools.AnimationModule;
import astar.astar.Position;

public class CrashGui implements Gui{
	Position crashPosition;
	AnimationModule animModule;
	CrashState state;
	final float fallSpeed = 10.0f;
	
	float xPos = 0.00f; 
	float yPos = 0.00f;
	
	enum CrashState {
		EXPLOSION,
		FALL,
		PRAY,
		CAST,
		DONE
	}
	
	public void CrashGui(Position position, boolean skipExplosion) {
		crashPosition = new Position(position.getX(), position.getY());
		if(skipExplosion) {//Celes just falls from the sky
			xPos = position.getX() * 25 - 7;
			yPos = position.getY() * 25 -100;
			
			animModule = new AnimationModule("Celes", "Fall", 40);
			state = CrashState.EXPLOSION;
		}
		else {
			xPos = position.getX() * 25 - 4;
			yPos = position.getY() * 25 - 14;
			
			animModule = new AnimationModule("Explosion", "Explosion", 2);
			state = CrashState.EXPLOSION;
		}
	}
	
	@Override
	public void updatePosition() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw(Graphics2D g, Point offset) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isPresent() {
		// TODO Auto-generated method stub
		return false;
	}

}
