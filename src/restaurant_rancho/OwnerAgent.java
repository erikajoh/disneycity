package restaurant_rancho;

import agent_rancho.Agent;
import restaurant_rancho.gui.RestaurantRanchoGui;
import restaurant_rancho.interfaces.Person;
import java.util.*;

public class OwnerAgent extends Agent {
	//data
	Person person;
	private double cash; 
	public String name;
	private enum OwnerState{nothing, payingBank};
	private List<Applicant> applicants;
	private enum appState{pending, denied, accepted, nothing};
	private boolean hiring;
	private boolean needCook, needCashier, needHost;
	private int numWaiters;
	private OwnerState os;
	public OwnerAgent(String name) {
		super();
		this.name = name;
		cash = 0;
		applicants = new ArrayList<Applicant>();
		needCook = true;
		needCashier = true;
		needHost = true;
		hiring = true;
		os = OwnerState.nothing;
		
	}
	
	public void setRestaurant() {
	
	}
	
	public void addWorker(String type) {
		if (type == "Waiters") {
			numWaiters++;
		}
		if (type == "Host") {
			needHost = false;
		}
		if (type == "Cashier") {
			needCashier = false;
		}
		if (type == "Cook") {
			needCook = false;
		}
		if (numWaiters>2 && needCook == false && needCashier == false && needHost == false) {
			hiring = false;
		}
	}
	
	//messages
	public void msgWantJob(Person p) {
		Applicant app = new Applicant(p);
		applicants.add(app);
		stateChanged();
	}
	

	
	//public void 
	
	protected boolean pickAndExecuteAnAction() {
		if(!applicants.isEmpty() || os != OwnerState.nothing) {
			for (Applicant a : applicants) {
				if (a.as == appState.pending){
					if (hiring) {
						hire(a);
						a.as = appState.accepted;
						return true;
					}
					else {
						tellNotHiring(a);
						a.as = appState.denied;
						return true;
					}
				}
			}
			return true;
		}
		return false;
	}
	
	//actions 
	
	private void hire(Applicant a) {
		String type;
		if (needCook) {
			type = "cook";
			needCook = false;
		}
		else if (needHost) {
			type = "host";
			needHost = false;
		}
		else if (numWaiters==0) {
			type = "waiters";
			numWaiters++;
		}
		else if (needCashier) {
			type = "cashier";
			needCashier = false;
		}
		else if (numWaiters <3) {
			type = "waiters";
			numWaiters++;
		}
		else type = "";
		
		if (numWaiters>2 && needCook == false && needCashier == false && needHost == false) {
			hiring = false;
		}
		
		a.person.msgYouAreHired("schedule", type);
		applicants.remove(a);
		stateChanged();
	}
	
	private void tellNotHiring(Applicant a) {
		a.person.msgNotHiring();
		applicants.remove(a);
		stateChanged();
		
	}
	
	private class Applicant {
		Person person; 
		appState as;
		Applicant(Person p){
			person = p;
			as = appState.nothing;
		}
	}
}
	


