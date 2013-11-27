package restaurant_rancho.test.mock;
import restaurant_rancho.Order;
import restaurant_rancho.interfaces.Cook;

public class MockCook implements Cook{ 
	
	String name;
	public EventLog log = new EventLog();
	public MockCook(String name) {
		super();
		this.name = name;
	}
	
	@Override
	public void msgAddOrder(Order o) {
		log.add(new LoggedEvent("Received msg Add Order"));
	}
	@Override
	public void msgHereIsOrder(String choice, int amount, int id) {
		log.add(new LoggedEvent("Received msg Here Is Order for " +amount + " " + choice));
		
	}
	
	
	
	
	
	

}
