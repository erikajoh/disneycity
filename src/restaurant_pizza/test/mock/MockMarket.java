package restaurant_pizza.test.mock;

import restaurant_pizza.CookAgent;
import restaurant_pizza.MarketOrder;
import restaurant_pizza.interfaces.Cashier;
import restaurant_pizza.interfaces.Market;

public class MockMarket extends Mock implements Market {

	public Cashier cashier;
	public EventLog log;
	
	public MockMarket(String name) {
		super(name);
		log = new EventLog();
	}

	@Override
	public void msgHereIsBill(double amount, boolean lastBillFulfilled) {
		if(lastBillFulfilled) {
			log.add(new LoggedEvent("msgHereIsBill from cashier: valid payment"));
		}
		else {
			log.add(new LoggedEvent("msgHereIsBill from cashier: invalid payment"));
		}
	}
	
	@Override
	public String getName() {
		return name;
	}
}
