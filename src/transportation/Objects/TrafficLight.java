package transportation.Objects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import astar.astar.Position;

public class TrafficLight implements ActionListener{
	//ALWAYS PLACE TRAFFIC LIGHT IN THE TOP LEFT GRID IN THE CENTER OF THE INTERSECTION OR THE TRANSPORTATION WILL BREAK HORRIBLY
	Position location;
	MovementTile[][] grid;
	Timer timer;

	private enum LightState {
		UPDOWN,
		UPDOWNCAUTION,
		LEFTRIGHT,
		LEFTRIGHTCAUTION
	}
	LightState light;

	public TrafficLight(Position location, MovementTile[][] grid) {
		timer = new Timer(5000, this);
		this.location = location;
		this.grid = grid;

		light = LightState.UPDOWN;

		//SET THE MOVEMENT TILES TO BE WHAT THE INTERSECTION NEEDS
		for(int i = -2; i < 4; i++) {
			grid[location.getX()][location.getY()+i].setMovement(false, true, false, false, MovementTile.MovementType.TRAFFICCROSSNONE);
			grid[location.getX()+1][location.getY()+i].setMovement(true, false, false, false, MovementTile.MovementType.TRAFFICCROSSNONE);
			grid[location.getX()+i][location.getY()].setMovement(false, false, true, false, MovementTile.MovementType.TRAFFICCROSSNONE);
			grid[location.getX()+i][location.getY()+1].setMovement(false, false, false, true, MovementTile.MovementType.TRAFFICCROSSNONE);
		}
		changeCrossWalk(0, -2, MovementTile.MovementType.TRAFFICCROSSROAD);
		changeCrossWalk(0, 2, MovementTile.MovementType.TRAFFICCROSSROAD);
		changeCrossWalk(-2, 0, MovementTile.MovementType.TRAFFICCROSSWALK);
		changeCrossWalk(2, 0, MovementTile.MovementType.TRAFFICCROSSWALK);
		
		grid[location.getX()][location.getY()].setMovement(false, true, true, false, MovementTile.MovementType.TRAFFICCROSSROAD);
		grid[location.getX()+1][location.getY()].setMovement(true, false, true, false, MovementTile.MovementType.TRAFFICCROSSROAD);
		grid[location.getX()][location.getY()+1].setMovement(false, true, false, true, MovementTile.MovementType.TRAFFICCROSSROAD);
		grid[location.getX()+1][location.getY()+1].setMovement(true, false, false, true, MovementTile.MovementType.TRAFFICCROSSROAD);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//based on state it changes the way people may walk and drive
		switch (light) {
		case UPDOWN://Change it to caution, lock out everyone from intersection
			timer.setDelay(2500);
			light = LightState.UPDOWNCAUTION;
			changeCrossWalk(0, -2, MovementTile.MovementType.TRAFFICCROSSNONE);
			changeCrossWalk(0, 2, MovementTile.MovementType.TRAFFICCROSSNONE);
			changeCrossWalk(-2, 0, MovementTile.MovementType.TRAFFICCROSSNONE);
			changeCrossWalk(2, 0, MovementTile.MovementType.TRAFFICCROSSNONE);
			break;
		case UPDOWNCAUTION://change direction
			if(checkIntersectionClear()) {
				timer.setDelay(5000);
				light = LightState.LEFTRIGHT;
				changeCrossWalk(0, -2, MovementTile.MovementType.TRAFFICCROSSWALK);
				changeCrossWalk(0, 2, MovementTile.MovementType.TRAFFICCROSSWALK);
				changeCrossWalk(-2, 0, MovementTile.MovementType.TRAFFICCROSSROAD);
				changeCrossWalk(2, 0, MovementTile.MovementType.TRAFFICCROSSROAD);
			}
			break;
		case LEFTRIGHT://Change it to caution, lock out everyone from intersection
			timer.setDelay(2500);
			light = LightState.LEFTRIGHTCAUTION;
			changeCrossWalk(0, -2, MovementTile.MovementType.TRAFFICCROSSNONE);
			changeCrossWalk(0, 2, MovementTile.MovementType.TRAFFICCROSSNONE);
			changeCrossWalk(-2, 0, MovementTile.MovementType.TRAFFICCROSSNONE);
			changeCrossWalk(2, 0, MovementTile.MovementType.TRAFFICCROSSNONE);
			break;
		case LEFTRIGHTCAUTION://change direction
			if(checkIntersectionClear()) {
				timer.setDelay(5000);
				light = LightState.UPDOWN;
				changeCrossWalk(0, -2, MovementTile.MovementType.TRAFFICCROSSROAD);
				changeCrossWalk(0, 2, MovementTile.MovementType.TRAFFICCROSSROAD);
				changeCrossWalk(-2, 0, MovementTile.MovementType.TRAFFICCROSSWALK);
				changeCrossWalk(2, 0, MovementTile.MovementType.TRAFFICCROSSWALK);
			}
			break;
		}
	}

	private void changeCrossWalk(int x, int y, MovementTile.MovementType state) {
		for(int i = 0; i < 2; i++) {
			for(int j = 0; j < 2; j++) {
				grid[i+x+location.getX()][j+y+location.getY()].setMovementType(state);
			}
		}
	}
	
	private boolean checkIntersectionClear() {
		for(int i = -2; i < 4; i++) {
			for(int j = -2; j < 4; j++) {
				if(grid[i+location.getX()][j+location.getY()].availablePermits() != 1) {
					return false;
				}
			}
		}
		return true;
	}
}
