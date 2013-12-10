package restaurant_haus.test.mock;

import market.CustomerAgent;
import restaurant_haus.interfaces.*;
import simcity.PersonAgent;
import simcity.Restaurant;
import simcity.interfaces.Market_Douglass;

public class MockMarket extends Mock implements Market_Douglass{

	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public Cashier cashier;
	
	public double check;
	
	public MockMarket(String name) {
		super(name);

	}
	/*
	@Override
	public void msgOrderPayment(double money, boolean paidFull) {
		if(paidFull) {
			log.add(new LoggedEvent("Cashier Payed Full"));
		}
		
		else {
			log.add(new LoggedEvent("Cashier Payed Part"));
		}
	}

	@Override
	public void msgFinalPayment() {
		log.add(new LoggedEvent("Cashier Payed Off Debt"));
	}

	@Override
	public void msgIncrementalPayment(double money) {
		log.add(new LoggedEvent("Cashier Made Payment On Debt"));	
	}
	*/
	@Override
	public void msgLeaving(CustomerAgent c) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgHereIsPayment(Restaurant rest, double amt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void personAs(Restaurant r, String choice, int quantity, int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void personAs(PersonAgent p, String name, double money,
			String choice, int quantity, String location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void personAs(PersonAgent p, String name, double money,
			String choice, int quantity) {
		// TODO Auto-generated method stub
		
	}
}
