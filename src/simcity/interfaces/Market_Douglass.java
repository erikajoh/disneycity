package simcity.interfaces;

import market.CustomerAgent;
import simcity.PersonAgent;
import simcity.Restaurant;

public interface Market_Douglass {

	String getName();
	public void msgLeaving(CustomerAgent c) ;
	public void msgHereIsPayment(Restaurant rest, double amt);
    public void personAs(Restaurant r, String choice, int quantity, int id);
    public void personAs(PersonAgent p, String name, double money, String choice, int quantity, String location);
    public void personAs(PersonAgent p, String name, double money, String choice, int quantity);
}
