package transportation.Agents;

import java.util.List;

import simcity.PersonAgent;
import transportation.Objects.BusStop;

public class Bus extends MobileAgent{

	class BusRider {
		PersonAgent person;
		BusStop destination;
		
		public BusRider(PersonAgent person) {
			
		}
	}
	
	List<BusRider> busRiders;
	
	@Override
	protected boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getType() {
		return "bus";
	}
}
