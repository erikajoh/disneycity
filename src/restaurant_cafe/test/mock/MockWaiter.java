package restaurant_cafe.test.mock;


import java.util.ArrayList;
import java.util.List;

import restaurant_cafe.gui.Check;
import restaurant_cafe.gui.Food;
import restaurant_cafe.gui.Menu;
import restaurant_cafe.interfaces.Cashier;
import restaurant_cafe.interfaces.Customer;
import restaurant_cafe.interfaces.Waiter;

/**
 * A sample MockWaiter built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public class MockWaiter extends Mock implements Customer {

	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public Cashier cashier;
	public Menu menu;
	
	public class MyCustomer {
		Customer customer;
		int number;
		String choice;
		public Check check;
		CustomerState state;
		public MyCustomer(Customer c, int tableNum, CustomerState s){
			customer = c;
			state = s;
		}
	}
	enum CustomerState{idle, waiting, readyToSit, readyToOrder, reorder, ordering, ordered, foodCooking, foodReady, foodBeingDelivered, produceCheck, gettingCheck, givingCheck, paying, eating, doneEating};
	public List<MyCustomer> customers = new ArrayList<MyCustomer>();

	public MockWaiter(String name, Menu m) {
		super(name);

	}

	public void msgGoOnBreak() {
		
	}
	
	public void msgEndBreak() {
		
	}

	public void msgPleaseSeatCustomer(Customer cust, int tableNum) {
		
	}
	
	public void msgPleaseSeatCustomer(MockCustomer cust, int tableNum) {
		customers.add(new MyCustomer(cust, tableNum, CustomerState.waiting));
	}
	
	public void msgLeaveCustomer() {
		
	}
	
	public void msgOutOfFood(Food f){
		
	}
	
	public void msgReadyToOrder(Customer cust){
		
	}
	
	public void msgHereIsMyOrder(Customer cust, String choice){
		
	}
	
	public void msgOrderDone(String choice, int table){
		
	}
	
	public void msgDoneEating(Customer cust){
		
	}
	
	public void msgHereIsCheck(Customer cust, Check check){
		
	}
	
	public void msgHereIsCheck(MockCustomer cust, Check check){
		MyCustomer customer = null;
		for(MyCustomer mc : customers){
			if(mc.customer == cust){
			   customer = mc;
			   break;
		    }
		}
		customer.check = check;
		customer.state = CustomerState.paying; //added, normally triggered by animation
	}
	
	public void msgCustomerLeaving(Customer cust){
		
	}
	
	public boolean pickAndExecuteAnAction(){
		for(MyCustomer customer : customers){
			  if(customer.state == CustomerState.paying){
				  giveCheck(customer);
				  return true;
			  }
		}
		 return false;
	}
	
	private void giveCheck(MyCustomer customer){
		customer.state = CustomerState.idle;
		customer.customer.msgHereIsCheck(customer.check);
	}
	
	public int getNumber(){
		return 0;
	}
	
	@Override
	public void msgGotHungry() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgRestaurantFull() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgFollowMe(Waiter w, int tn, Menu m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgAskOrder() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgReorder() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgHereIsFood() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgHereIsCheck(Check c) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgHereIsChange(double change) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgCleanDishes() {
		// TODO Auto-generated method stub
		
	}

}
