package restaurant_bayou.test.mock;


import java.util.List;

import restaurant_bayou.CustomerAgent;
import restaurant_bayou.CashierAgent.Check;
import restaurant_bayou.HostAgent.Menu;
import restaurant_bayou.interfaces.Cashier;
import restaurant_bayou.interfaces.Customer;
import restaurant_bayou.interfaces.Waiter;

/**
 * A sample MockWaiter built to unit test a CashierAgent.
 *
 * @author Erika Johnson
 *
 */
public class MockWaiter extends Mock implements Waiter {

	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public Cashier cashier;

	public MockWaiter(String name) {
		super(name);

	}

	@Override
	public void msgOrderIsDone(int t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgLeavingTable(CustomerAgent cust) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgAtTable() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgSeatCustomer(CustomerAgent cust, Menu m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgImReadyToOrder(CustomerAgent cust) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgWaiterReady() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgHereIsMyChoice(CustomerAgent cust, String c) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgDoneEating(CustomerAgent c) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgDoneLeaving(CustomerAgent c) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgOutOfFood(List<String> uf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgOKToGoOnBreak(boolean ok) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgHereIsCheck(Check c) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Received msgHereIsCheck from cashier. Total = "+ c.getCost()));
		
	}
	
//	@Override
//	public void HereIsYourTotal(double total) {
//		log.add(new LoggedEvent("Received HereIsYourTotal from cashier. Total = "+ total));
//
////		if(this.getName().toLowerCase().contains("thief")){
////			//test the non-normative scenario where the customer has no money if their name contains the string "theif"
////			cashier.IAmShort(this, 0);
////
////		}else
//		if (this.getName().toLowerCase().contains("bad")){
//			//test the non-normative scenario where the customer overpays if their name contains the string "rich"
//			cashier.msgHereIsMoney(this, Math.ceil(total));
//
//		}else{
//			//test the normative scenario
//			cashier.msgHereIsMoney(this, total);
//		}
//	}
//
//	@Override
//	public void HereIsYourChange(double total) {
//		log.add(new LoggedEvent("Received HereIsYourChange from cashier. Change = "+ total));
//	}
//
//	@Override
//	public void YouOweUs(double remaining_cost) {
//		log.add(new LoggedEvent("Received YouOweUs from cashier. Debt = "+ remaining_cost));
//	}

}
