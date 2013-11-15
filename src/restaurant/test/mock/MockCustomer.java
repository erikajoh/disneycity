package restaurant.test.mock;


import restaurant.RestMenu;
import restaurant.WaiterAgent;
import restaurant.interfaces.Cashier;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Waiter;
import restaurant.gui.CustomerGui;

/**
 * A sample MockCustomer built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public class MockCustomer extends Mock implements Customer {

	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public Cashier cashier;
	public EventLog log = new EventLog();
	String name;
	
	public MockCustomer(String name) {
		super(name);
		this.name = name;

	}
	
	public void gotHungry(){
		
	}
	
	public CustomerGui getGui(){
		return null;
	}
	
	public void msgSitAtTable(Waiter w, RestMenu m) {
		
	}

	public void msgAnimationFinishedGoToSeat() {
		
	}
	
	public void msgRestaurantFull(){
		
	}
	
	public void msgWhatIsYourOrder(){
		
	}
	@Override
	public void msgHereIsCheck(double amount){
		log.add(new LoggedEvent("Received HereIsYourTotal from cashier. Total = "+ amount));
		if(this.name.toLowerCase().contains("thief")){
			//test the non-normative scenario where the customer has no money if their name contains the string "theif"
			cashier.msgHereIsMoney(this, 0);

		}
		else{
			//test the normative scenario
			cashier.msgHereIsMoney(this, amount);
		}
	}
	@Override
	public void msgHereIsChange(double amount){
		log.add(new LoggedEvent("Received HereIsYourChange. Change = "+ amount));
		
	}
	@Override
	public void msgCheckIncomplete(double owed){
		log.add(new LoggedEvent("Received YouOweUs from cashier. Debt = "+ owed));
	}
	
	public void msgWhatIsReorder(RestMenu menu){
		
	}
	
	public void msgHereIsYourFood(){
		
	}

	public void msgAnimationFinishedLeaveRestaurant(){
		
	}

}
