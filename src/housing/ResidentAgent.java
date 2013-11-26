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
	private int roomNo;
	private String type, foodPreference;
	private ResidentGui renterGui;
	private Semaphore moving = new Semaphore(0, true);
	private Building building;
	
	public EventLog log = new EventLog();
	
	private final static int START_INVENTORY = 1;

	public enum State
	{idle, enteringHouse, readyToCook, noFood, foodDone, wantsMaintenance, maintenanceDone, goingToBed, leavingHouse, left};
	
	State state = State.idle;
	
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
		state = State.idle;
		state = State.enteringHouse; //hack
		foodPreference = "American"; //hack
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
			// TODO: This be hacked
			inventory.put("Mexican", START_INVENTORY);
			inventory.put("Southern", START_INVENTORY);
			inventory.put("Italian", START_INVENTORY);
			inventory.put("German", START_INVENTORY);
			inventory.put("American", START_INVENTORY);
		}
		private boolean getFood(String f) {
			if (inventory.get(f) != 0) {
				inventory.put(f, inventory.get(f)-1);
				state = State.idle;
				return true;
			} else {
				return false;
			}
		}
		public void cookFood() {
			if (getFood(foodPreference)) {
				timer.schedule(new TimerTask() {
					public void run() {
						print("food done");
						log.add(new LoggedEvent("Food is done"));
						state = State.foodDone;
						stateChanged();
					}
				},
				5000);
			} else {
				print("no food");
				log.add(new LoggedEvent("No food"));
				state = State.noFood;
				stateChanged();
			}
		}

		public void addItems(Map<String, Integer> items) {
			Set<String> keySet = items.keySet();
			String[] keyArray = keySet.toArray(new String[keySet.size()]);
			for(int i = 0; i < keyArray.length; i++) {
				if(inventory.get(keyArray[i]) != null) {
					Integer aQuantity = items.get(keyArray[i]);
					inventory.put(keyArray[i], aQuantity + inventory.get(keyArray[i]));
				}
			}
		}
	}
	
	// Messages
	
	public void msgAnimationFinished(){ //from animation
		moving.release();
		stateChanged();
	}
	
	public void msgDoMaintenance(){
		state = State.wantsMaintenance;
		stateChanged();
	}
	
	public void msgAnimationLeavingFinished(){
		moving.release();
		state = State.left;
		stateChanged();
	}
	
	public void msgMaintenanceAnimationFinished(){ //from animation
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
		if(state == State.enteringHouse){
			Do("Entering house");
			EnterHouse();
//			state = State.readyToCook; //hack
			return true;
		}
		else if(state == State.readyToCook){
			Do("Ready to cook");
			CookFood();
			return true;
		}
		else if(state == State.noFood){
			Do("No food");
			housing.msgFoodDone(this, false);
//			state = State.wantsMaintenance; //hack
			return true;
		}
		else if(state == State.foodDone){
			Do("Food done");
			GoToTable();
			housing.msgFoodDone(this, true);
//			state = State.wantsMaintenance; //hack
			return true;
		}
		else if(state == State.wantsMaintenance){
			Do("Want maintenance");
			DoMaintenance();
			return true;
		}
		else if(state == State.maintenanceDone){
			Do("Maintenance done");
			housing.msgFinishedMaintenance(this);
//			state = State.goingToBed; //hack
			return true;
		}
		else if(state == State.goingToBed){
			Do("Going to bed");
			GoToBed();
//			state = State.leavingHouse; //hack
			return true;
		}
		else if(state == State.leavingHouse){
			Do("Leaving house");
			LeaveHouse();
			return true;
		}
		else if(state == State.left){
			Do("Left house");
			housing.msgLeft(this);
			state = State.idle;
			return true;
		}
		return false;
	}

	// Actions
	
	private void EnterHouse(){
		state = State.idle;
		renterGui.DoEnterHouse();
		try {
			moving.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		housing.msgEntered(this);
	}
	
	private void CookFood(){
		state = State.idle;
		renterGui.DoGoToKitchen();
		try {
			moving.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		building.cookFood();
	}
	
	private void GoToTable(){
		state = State.idle;
		renterGui.DoGoToTable();
		try {
			moving.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void DoMaintenance(){
		state = State.idle;
		renterGui.DoMaintenance();
		try {
			moving.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void GoToBed(){
		state = State.idle;
		renterGui.DoGoToBed();
		try {
			moving.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void LeaveHouse(){
		state = State.idle;
		renterGui.DoLeaveHouse();
		try {
			moving.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

