package restaurant_pizza.interfaces;

import restaurant_pizza.CookAgent;
import restaurant_pizza.MarketOrder;

public interface Market {

	//TODO: CashierMarket interaction step 3
	public abstract void msgHereIsBill(double amount, boolean lastBillFulfilled);

	public abstract String getName();

}