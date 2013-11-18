package housing;

import agent.Agent;

import java.util.*;



/**
 * Housing renter agent.
 */
public class RenterAgent extends Agent {
	private String name;
		
	// agent correspondents
	private Building building = null;
	private OwnerAgent owner = null;
	private double amt;
	String food;

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

	public void msgTimeToPay(OwnerAgent o, double amt){
		owner = o;
		state = State.paymentDue;
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
	protected boolean pickAndExecuteAnAction() {
		//	CustomerAgent is a finite state machine
		if(state == State.enteringHouse){
			Do("Entering house");
			EnterHouse();
			return true;
		}
		else if(state == State.paymentDue){
			owner.msgHereIsPayment(this, amt);
			return true;
		}
		else if(state == State.readyToCook){
			owner.msgReadyToCook(this, food);
			return true;
		}
		else if(state == State.wantsMaintenance){
			owner.msgWantMaintenance(this);
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
	}
	
	private void LeaveHouse(){
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
	
	public void setOwner(OwnerAgent o) {
		owner = o;
	}
	
//	public void setGui(RenterGui r) {
//		renterGui = r;
//	}

//	public RenterGui getGui() {
//		return renterGui;
//	}
}

