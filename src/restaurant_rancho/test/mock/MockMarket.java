package restaurant_rancho.test.mock;

import restaurant_rancho.CashierAgent;
import restaurant_rancho.CookAgent;
import restaurant_rancho.interfaces.Market;

import java.util.List;
import java.util.ArrayList;

public class MockMarket extends Mock implements Market {

	public String name;
	public CashierAgent cashier;
	public EventLog log = new EventLog();
	
	public MockMarket(String name) {
		super(name);
		this.name = name;
	}
	public void msgNeedFood(CookAgent c, String food, int am) {
		
	}
	
	public void msgHereIsPayment(double amount, int oNum) {
		log.add(new LoggedEvent("Received payment from cashier of " + amount));
	}

	
}
