package housing;

import agent.Agent;
import housing.interfaces.Owner;
import housing.interfaces.Renter;
import housing.test.mock.EventLog;
import housing.test.mock.LoggedEvent;

import java.util.*;



/**
 * Housing renter agent.
 */
public class RenterAgent extends Agent implements Renter {
	private String name;
		
	// agent correspondents
	private Building building;
	public Owner owner;
	private double amt;
	String food;
	
	public EventLog log = new EventLog();

	public enum State
	{idle, wantsToRent, enteringHouse, paymentDue, readyToCook, wantsMaintenance, leavingHouse};
	
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

	public void msgTimeToPay(Owner o, double amt){
		owner = o;
		state = State.paymentDue;
		stateChanged();
	}
	
	public void msgPaymentAccepted(){
		log.add(new LoggedEvent("Payment accepted by owner"));
		stateChanged();
	}
	
	public void msgFinishedMaintenance(){
		stateChanged();
	}
	
	public void msgFoodDone(){
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		//	CustomerAgent is a finite state machine
		if(state == State.enteringHouse){
			Do("Entering house");
			EnterHouse();
			return true;
		}
		else if(state == State.paymentDue){
			owner.msgHereIsPayment(this, amt);
			state = State.idle;
			return true;
		}
		else if(state == State.readyToCook){
			owner.msgReadyToCook(this, food);
			state = State.idle;
			return true;
		}
		else if(state == State.wantsMaintenance){
			owner.msgWantMaintenance(this);
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
		state = State.idle;
	}
	
	private void LeaveHouse(){
		state = State.idle;
	}

	// Accessors, etc.

	public String getName() {
		return name;
	}
	
	public void setBuilding(Building b) {
		building = b;
	}

	public String toString() {
		return "renter " + getName();
	}
	
	public void setOwner(Owner o) {
		owner = o;
	}
	
//	public void setGui(RenterGui r) {
//		renterGui = r;
//	}

//	public RenterGui getGui() {
//		return renterGui;
//	}
}

