package housing;

import agent.Agent;
import housing.gui.RenterGui;
import housing.interfaces.Owner;
import housing.interfaces.Renter;
import housing.test.mock.EventLog;
import housing.test.mock.LoggedEvent;

import java.util.*;
import java.util.concurrent.Semaphore;



/**
 * Housing renter agent.
 */
public class RenterAgent extends Agent implements Renter {
	private String name;
		
	// agent correspondents
	private Housing housing = new Housing("hack");
	public Owner owner;
	private double amt;
	String food;
	private RenterGui renterGui;
	private Semaphore moving = new Semaphore(1, true);
	
	public EventLog log = new EventLog();

	public enum State
	{idle, wantsToRent, enteringHouse, goingToBed, paymentDue, readyToCook, foodDone, wantsMaintenance, maintenanceDone, leavingHouse};
	
	State state = State.idle;
	
	/**
	 * Constructor for RenterAgent class
	 *
	 * @param name name of the customer
	 */
	public RenterAgent(String name){
		super();
		this.name = name;
		state = State.enteringHouse;
	}

	public String getCustomerName() {
		return name;
	}
	
	// Messages
	
	public void msgTimeToPay(double amt){
		state = State.paymentDue;
		stateChanged();
	}
	
	public void msgPaymentAccepted(){
		print("payment accepted");
		log.add(new LoggedEvent("Payment accepted by owner"));
		// state = State.readyToCook; //hack
		stateChanged();
	}
	
	public void msgFinishedMaintenance(){
		print("finished maintenance");
		log.add(new LoggedEvent("Finished maintenance"));
		state = State.maintenanceDone;
		// state = State.leavingHouse; //hack
		stateChanged();
	}
	
	public void msgFoodDone(){
		print("food done");
		log.add(new LoggedEvent("Food is done"));
		state = State.foodDone;
		// state = State.wantsMaintenance; //hack
		stateChanged();
	}
	
	public void msgAnimationFinished(){
		moving.release();
		stateChanged();
	}
	
	public void msgLeave() { //from Housing class
		state = State.leavingHouse;
		stateChanged();
	}

	public void msgCookFood(String choice) { //from Housing class
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
			owner.msgWantToRent(this);
			// state = State.paymentDue; //hack
			return true;
		}
		else if(state == State.paymentDue){
			owner.msgHereIsPayment(this, amt);
			state = State.idle;
			return true;
		}
		else if(state == State.readyToCook){
			owner.msgReadyToCook(this, food);
			GoToKitchen();
			return true;
		}
		else if(state == State.foodDone){
			housing.msgFoodDone(this);
			state = State.idle;
			return true;
		}
		else if(state == State.wantsMaintenance){
			owner.msgWantMaintenance(this);
			state = State.idle;
			return true;
		}
		else if(state == State.maintenanceDone){
			housing.msgFinishedMaintenance(this);
			state = State.idle;
			return true;
		}
		else if(state == State.leavingHouse){
			Do("Leaving house");
			LeaveHouse();
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
		state = State.idle;
	}
	
	private void GoToKitchen(){
		try {
			moving.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		renterGui.DoGoToKitchen();
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
		state = State.idle;
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
	
	public void setOwner(Owner o) {
		owner = o;
	}
	
	public void setGui(RenterGui r) {
		renterGui = r;
	}

	public RenterGui getGui() {
		return renterGui;
	}
	
}

