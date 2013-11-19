package restaurant_rancho;

import agent_rancho.Agent;
import restaurant_rancho.gui.RestaurantRanchoGui;

import java.util.*;

public class OwnerAgent extends Agent {
	//data
	private double cash; 
	public String name;
	
	public OwnerAgent(String name) {
		super();
		this.name = name;
		cash = 0;
	}
	
	//messages
	//msgWantJob(String type, )
	
	protected boolean pickAndExecuteAnAction() {
		return true;
	}
	

}
