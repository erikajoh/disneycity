package restaurant_bayou.interfaces;

import java.util.List;

import restaurant_bayou.CashierAgent;
import restaurant_bayou.CookAgent;
import restaurant_bayou.MarketAgent;
import restaurant_bayou.MarketAgent.Payment;
import restaurant_bayou.MarketAgent.Request;

/**
 * A sample Waiter interface built to unit test a CashierAgent.
 *
 * @author Erika Johnson
 *
 */
public interface Market {
	
	public void msgNeedFood(CookAgent c, CashierAgent ca, String f, int amt);
	
	public void msgHereIsPayment(CashierAgent c, double amt);
	
	public String getName();

}