package simcity.interfaces;

import market.CustomerAgent;
import simcity.Restaurant;

public interface Market_Douglass {

	String getName();
	public void msgLeaving(CustomerAgent c) ;
	public void msgHereIsPayment(Restaurant rest, double amt);

}
