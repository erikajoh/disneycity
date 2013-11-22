package restaurant_rancho.test.mock;

import java.util.List;

import restaurant_rancho.Order;
import restaurant_rancho.interfaces.Customer;
import restaurant_rancho.interfaces.Waiter;

public class MockWaiter extends Mock implements Waiter{
	
	String name;
	public EventLog log = new EventLog();
	
	public MockWaiter(String name) {
		super(name);
		this.name = name;

	}
	
	public void msgCreateCustomer(Customer c, int t, int l) {
		
	}


	public void msgReadyToOrder(Customer c) {
		
	}

	public void msgReadyForCheck(Customer c){
		log.add(new LoggedEvent("Received message Ready for Check from customer"));
	}
	
	public void msgCheckReady(Customer cust, double amount){
		log.add(new LoggedEvent("Received Check Ready from cashier"));
	}
	
	public void msgHereIsMyOrder(Customer c, String choice){
		
	}
	
	public void msgUpdateMenu(List<String> foodsUnavailable){
		
	}
	
	public void msgOutOfFood(Order o){
		
	}
	
	public void msgFoodIsReady(Order o){
		
	}
	
	public void msgLeavingTable(Customer cust){
		
	}

	public void msgAtTable(){
		
	}
}
