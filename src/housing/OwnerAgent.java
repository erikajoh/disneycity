package housing;

import agent.Agent;
import housing.interfaces.Owner;

import java.util.*;

/**
 * Restaurant Host Agent
 */

public class OwnerAgent extends Agent implements Owner {

	enum State {idle, paymentDue, paymentPending, paymentRcvd, readyToCook, cooking, foodDone, wantsMaintenance, doingMaintenance, maintenanceDone};

	class MyRenter {
	  RenterAgent agent;
	  double amt;
	  String food;
	  Building building;
	  State state;
	  
	  public MyRenter(RenterAgent ra){
		  agent = ra;
		  state = State.idle;
	  }
	}
	
	MyRenter renter;
	List<MyRenter> renters = Collections.synchronizedList(new ArrayList<MyRenter>());

	private String name;
	//OwnerGui ownerGui;
	
	public OwnerAgent(String name) {
		super();

		this.name = name;
	}

	public String getName() {
		return name;
	}

	
	// Messages
	
	public void msgWantToRent(RenterAgent ra){
		print("New renter is here");
		renters.add(new MyRenter(ra));
		stateChanged();
	}
	
	public void msgHereIsPayment(RenterAgent ra, double amount){
		print("Payment received from renter");
		for(MyRenter r : renters){
			if(r.agent == ra){
				r.state = State.paymentRcvd;
				r.amt = amount;
				stateChanged();
			}
		}
	}
	
	public void msgReadyToCook(RenterAgent ra, String food){
		for(MyRenter r : renters){
			if(r.agent == ra){
				r.state = State.readyToCook;
				r.food = food;
				stateChanged();
			}
		}
	}
	
	public void msgWantMaintenance(RenterAgent ra){
		for(MyRenter r : renters){
			if(r.agent == ra){
				r.state = State.wantsMaintenance;
				stateChanged();
			}
		}
	}

	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		/* Think of this next rule as:
            Does there exist a table and customer,
            so that table is unoccupied and customer is waiting.
            If so seat him at the table.
		 */
		
		for (MyRenter r: renters) {
			if (r.state == State.paymentDue) {
				r.agent.msgTimeToPay(this, r.amt);
				r.state = State.paymentPending;
				return true;
			}
		}
		for (MyRenter r: renters) {
			if (r.state == State.paymentRcvd) {
				r.agent.msgPaymentAccepted();
				r.state = State.idle;
				return true;
			}
		}
		for (MyRenter r: renters) {
			if (r.state == State.wantsMaintenance) {
				r.building.doMaintenance();
				r.state = State.doingMaintenance;
				return true;
			}
		}
		for (MyRenter r: renters) {
			if (r.state == State.maintenanceDone) {
				r.agent.msgFinishedMaintenance();
				r.state = State.idle;
				return true;
			}
		}
		for (MyRenter r: renters) {
			if (r.state == State.readyToCook) {
				r.state = State.cooking;
				r.building.cookFood(r.food);
				return true;
			}
		}
		for (MyRenter r: renters) {
			if (r.state == State.foodDone) {
				r.agent.msgFoodDone();
				r.state = State.idle;
				return true;
			}
		}
		
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions	
	
	//utilities
//	public void setGui(OwnerGui gui) {
//		ownerGui = gui;	
//	}
	
//	public void setBuilding(Building b) {
//		building = b;
//	}

//	public OwnerGui getGui() {
//		return ownerGui;
//	}
}


