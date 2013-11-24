package housing;

import agent.Agent;
import housing.gui.ResidentGui;
import housing.interfaces.Resident;
import housing.test.mock.EventLog;
import housing.test.mock.LoggedEvent;

import java.util.*;
import java.util.concurrent.Semaphore;


/**
 * Housing renter agent.
 */
public class ResidentAgent extends Agent implements Resident {
	private String name;
		
	// agent correspondents
	private Housing housing;
	private double amt;
	private String type, foodPreference;
	private ResidentGui renterGui;
	private Semaphore moving = new Semaphore(1, true);
	private Building building;
	
	public EventLog log = new EventLog();

	public enum State
	{idle, enteringHouse, readyToCook, noFood, foodDone, wantsMaintenance, maintenanceDone, goingToBed, leavingHouse};
	
	State state = State.idle;
	
	/**
	 * Constructor for RenterAgent class
	 *
	 * @param name name of the customer
	 */
	public ResidentAgent(String name, String type){
		super();
		this.name = name;
		this.type = type;
		building = new Building(type);
		state = State.idle;
	}

	public String getCustomerName() {
		return name;
	}
	
	class Building {
		String type;
		Timer timer = new Timer();
		private HashMap<String, Integer> inventory = new HashMap<String, Integer>();
		Building(String t) {
			type = t;
			inventory.put("Mexican", 1);
			inventory.put("Southern", 1);
			inventory.put("Italian", 1);
			inventory.put("German", 1);
			inventory.put("American", 1);
		}
		private boolean getFood(String f) {
			if (inventory.get(f) != 0) {
				inventory.put(f, inventory.get(f)-1);
				return true;
			} else {
				return false;
			}
		}
		public void cookFood() {
			timer.schedule(new TimerTask() {
				public void run() {
					if (getFood(foodPreference)) {
						print("food done");
						log.add(new LoggedEvent("Food is done"));
						state = State.foodDone;
						stateChanged();
					} else {
						print("no food");
						log.add(new LoggedEvent("Food is done"));
						state = State.noFood;
						stateChanged();
					}
				}
			},
			5000);
		}
	}
	
	// Messages
	
	public void msgAnimationFinished(){
		moving.release();
		stateChanged();
	}
	
	public void msgDoMaintenance(){
		state = State.wantsMaintenance;
		stateChanged();
	}
	
	public void msgMaintenanceAnimationFinished(){
		moving.release();
		print("finished maintenance");
		log.add(new LoggedEvent("Finished maintenance"));
		state = State.maintenanceDone;
		stateChanged();
	}
	
	public void msgLeave() { //from Housing class
		log.add(new LoggedEvent("Leaving"));
		state = State.leavingHouse;
		stateChanged();
	}

	public void msgCookFood(String choice) { //from Housing class
		foodPreference = choice;
		state = State.readyToCook;
		stateChanged();
	}

	public void msgHome() { //from Housing class
		state = State.enteringHouse;
		stateChanged();
	}

	public void msgToBed() { //from Housing class
		state = State.goingToBed;
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		if(state == State.enteringHouse){
			Do("Entering house");
			EnterHouse();
			state = State.idle;
//			state = State.readyToCook; //hack
			return true;
		}
		else if(state == State.readyToCook){
			CookFood();
			state = State.idle;
			return true;
		}
		else if(state == State.noFood){
			housing.msgFoodDone(this, false);
			state = State.idle;
//			state = State.wantsMaintenance; //hack
			return true;
		}
		else if(state == State.foodDone){
			housing.msgFoodDone(this, true);
			EatFood();
			state = State.idle;
//			state = State.wantsMaintenance; //hack
			return true;
		}
		else if(state == State.wantsMaintenance){
			DoMaintenance();
			state = State.idle;
			return true;
		}
		else if(state == State.maintenanceDone){
			housing.msgFinishedMaintenance(this);
			state = State.idle;
//			state = State.leavingHouse; //hack
			return true;
		}
		else if(state == State.goingToBed){
			GoToBed();
			state = State.idle;
			return true;
		}
		else if(state == State.leavingHouse){
			Do("Leaving house");
			LeaveHouse();
			state = State.idle;
			return true;
		}
		return false;
	}

	// Actions
	
	private void EnterHouse(){
		try {
			moving.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		renterGui.DoEnterHouse();
		housing.msgEntered(this);
	}
	
	private void CookFood(){
		try {
			moving.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		renterGui.DoGoToKitchen();
		building.cookFood();
	}
	
	private void EatFood(){
		try {
			moving.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		renterGui.DoGoToTable();
	}
	
	private void DoMaintenance(){
		try {
			moving.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		renterGui.DoMaintenance();
	}
	
	private void GoToBed(){
		try {
			moving.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		renterGui.DoGoToBed();
	}
	
	private void LeaveHouse(){
		try {
			moving.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		renterGui.DoLeaveHouse();
		housing.msgLeft(this);
	}

	// Accessors, etc.

	public String getName() {
		return name;
	}
	
	public void setHousing(Housing h) {
		housing = h;
	}

	public String toString() {
		return "renter " + getName();
	}
	
	public void setGui(ResidentGui r) {
		renterGui = r;
	}

	public ResidentGui getGui() {
		return renterGui;
	}
	
}

