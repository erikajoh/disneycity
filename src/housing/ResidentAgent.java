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
public class ResidentAgent extends Agent {
	private String name;
		
	// agent correspondents
	private Housing housing;
	private double amt;
	private int roomNo;
	private String type, foodPreference;
	private ResidentGui renterGui;
	private Semaphore moving = new Semaphore(0, true);
	private Building building;
	
	public EventLog log = new EventLog();

	public enum State
	{idle, enteringHouse, readyToCook, noFood, foodDone, wantsMaintenance, maintenanceDone, goingToBed, leavingHouse, left};
	
	State state = State.idle;
	
//	State prevState; //hack
	
	/**
	 * Constructor for RenterAgent class
	 *
	 * @param name name of the customer
	 */
	public ResidentAgent(String name, String type, int roomNo){
		super();
		this.name = name;
		this.type = type;
		this.roomNo = roomNo;
		building = new Building(type);
//		state = State.enteringHouse; //hack
//		foodPreference = "American"; //hack
	}

	public String getCustomerName() {
		return name;
	}
	
	// Messages
	
	public void msgAnimationFinished(){ //from animation
		moving.release();
//		if (prevState == State.enteringHouse) state = State.readyToCook; //hack
//		else if (prevState == State.foodDone) state = State.goingToBed; //hack
//		else if (prevState == State.goingToBed) state = State.leavingHouse; //hack
		stateChanged();
	}
	
	public void msgAnimationLeavingFinished(){ //from animation
		moving.release();
		state = State.left;
		stateChanged();
	}
	
	public void msgMaintenanceAnimationFinished(){ //from animation
		moving.release();
		log.add(new LoggedEvent("Finished maintenance"));
		state = State.maintenanceDone;
		stateChanged();
	}
	
	public void msgDoMaintenance(){ //from Housing class
		state = State.wantsMaintenance;
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
	
	public void msgDoneCooking(boolean success) { //from Building class
		if (success) state = State.foodDone;
		else state = State.noFood;
		stateChanged();
	}

	public void msgHome(Map<String, Integer> items) { //from Housing class
		building.addItems(items);
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
//		prevState = state; //hack
		if(state == State.enteringHouse){
			log.add(new LoggedEvent("Entering house"));
			state = State.idle;
			Do("Entering house");
			EnterHouse();
			return true;
		}
		else if(state == State.readyToCook){
			log.add(new LoggedEvent("Ready to cook"));
			state = State.idle;
			Do("Ready to cook");
			CookFood();
			return true;
		}
		else if(state == State.noFood){
			log.add(new LoggedEvent("No food"));
			state = State.idle;
			Do("No food");
			housing.msgFoodDone(this, false);
			return true;
		}
		else if(state == State.foodDone){
			log.add(new LoggedEvent("Food done"));
			state = State.idle;
			Do("Food done");
			GoToTable();
			housing.msgFoodDone(this, true);
			return true;
		}
		else if(state == State.wantsMaintenance){
			log.add(new LoggedEvent("Wants maintenance"));
			state = State.idle;
			Do("Want maintenance");
			DoMaintenance();
			return true;
		}
		else if(state == State.maintenanceDone){
			log.add(new LoggedEvent("Maintenance done"));
			state = State.idle;
			Do("Maintenance done");
			housing.msgFinishedMaintenance(this);
//			state = State.goingToBed; //hack
			return true;
		}
		else if(state == State.goingToBed){
			log.add(new LoggedEvent("Going to bed"));
			state = State.idle;
			Do("Going to bed");
			GoToBed();
			return true;
		}
		else if(state == State.leavingHouse){
			log.add(new LoggedEvent("Leaving house"));
			state = State.idle;
			Do("Leaving house");
			LeaveHouse();
			return true;
		}
		else if(state == State.left){
			log.add(new LoggedEvent("Left"));
			state = State.idle;
			Do("Left house");
			housing.msgLeft(this);
			return true;
		}
		return false;
	}

	// Actions
	
	private void EnterHouse(){
		if (renterGui != null) renterGui.DoEnterHouse();
		try {
			moving.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		housing.msgEntered(this);
		stateChanged();
	}
	
	private void CookFood(){
		if (renterGui != null) renterGui.DoGoToKitchen();
		try {
			moving.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		building.cookFood(this, foodPreference);
		stateChanged();
	}
	
	private void GoToTable(){
		if (renterGui != null) renterGui.DoGoToTable();
		try {
			moving.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		stateChanged();
	}
	
	private void DoMaintenance(){
		if (renterGui != null) renterGui.DoMaintenance();
		try {
			moving.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		stateChanged();
	}
	
	private void GoToBed(){
		if (renterGui != null) renterGui.DoGoToBed();
		try {
			moving.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		stateChanged();
	}
	
	private void LeaveHouse(){
		if (renterGui != null) renterGui.DoLeaveHouse();
		try {
			moving.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		stateChanged();
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

