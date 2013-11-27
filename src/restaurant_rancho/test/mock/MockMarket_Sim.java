package restaurant_rancho.test.mock;

import market.CustomerAgent;
import simcity.PersonAgent;
import simcity.Restaurant;
import simcity.interfaces.Market_Douglass;

public class MockMarket_Sim implements Market_Douglass {
	
	String name;
	public EventLog log = new EventLog();
	public MockMarket_Sim(String name) {
		super();
		this.name = name;
        
	}
	
	public String getName(){
		return name;
		
	}
	public void msgLeaving(CustomerAgent c) {
		log.add(new LoggedEvent("Received msg Leaving from customer " + c.getName()));
		
	}
	public void msgHereIsPayment(Restaurant rest, double amt){
		log.add(new LoggedEvent("Received msg Here Is Payment from rest " + rest.getRestaurantName()));
		
	}
    public void personAs(Restaurant r, String choice, int quantity, int id){
    	log.add(new LoggedEvent("Received msg personAs from rest " + r.getRestaurantName()));
    	
    }
    public void personAs(PersonAgent p, String name, double money, String choice, int quantity, String location){
    	
    }
    public void personAs(PersonAgent p, String name, double money, String choice, int quantity){
    	
    }

}
