package restaurant_bayou.test.mock;


import java.util.List;

import restaurant_bayou.CashierAgent;
import restaurant_bayou.CookAgent;
import restaurant_bayou.interfaces.Cashier;
import restaurant_bayou.interfaces.Market;

/**
 * A sample MockWaiter built to unit test a CashierAgent.
 *
 * @author Erika Johnson
 *
 */
public class MockMarket extends Mock implements Market {

	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public Cashier cashier;
	private String name;

	public MockMarket(String name) {
		super(name);
		this.name = name;
	}

	@Override
	public void msgNeedFood(CookAgent c, CashierAgent ca, String f, int amt){
		
	}
	
	public void msgHereIsPayment(CashierAgent c, double amt){
		log.add(new LoggedEvent("Received msgHereIsPayment."));
	}
	
	public String getName(){
		return this.name;
	}

}
