package simcity.test.mock;

import java.util.Timer;
import java.util.TimerTask;

import market.Market;
import simcity.PersonAgent;
import simcity.Restaurant;
import simcity.interfaces.Transportation_Douglass;
import transportation.Transportation;

public class MockTransportation_Douglass extends Mock_Douglass implements Transportation { //implements Bank {

	public EventLog log;
	public Timer timer;
	
	public MockTransportation_Douglass(String name) {
		super(name);
		log = new EventLog();
		timer = new Timer();
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void msgWantToGo(String startLocation, String endLocation, PersonAgent person, String mover, String character) {
		log.add(new LoggedEvent("Received msgWantToGo: "
				+ "startLocation = " + startLocation + "; "
				+ "endLocation = " + endLocation + "; "
				+ "person = " + person.getName() + "; "
				+ "method = " + mover));
		final PersonAgent thePerson = person;
		final String theLocation = endLocation;
		timer.schedule(new TimerTask() {
			public void run() {
				thePerson.msgReachedDestination(theLocation);
			}
		}, 1000);
	}
	
	@Override
	public void msgPayFare(PersonAgent p, float money) {
		//TODO
	}

	@Override
	public void msgSendDelivery(Restaurant restaurant, Market market,
			String food, int quantity, int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgSendDelivery(PersonAgent person, Market market, String food,
			int quantity, String location) {
		// TODO Auto-generated method stub
		
	}
}
