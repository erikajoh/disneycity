package restaurant_bayou.test.mock;


import java.util.List;

import restaurant_bayou.CashierAgent;
import restaurant_bayou.WaiterAgent;
import restaurant_bayou.CashierAgent.Check;
import restaurant_bayou.HostAgent.Menu;
import restaurant_bayou.interfaces.Cashier;
import restaurant_bayou.interfaces.Customer;
import simcity.RestMenu;

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
	public CashierAgent cashier;

	public MockCustomer(String name) {
		super(name);

	}

	@Override
	public void msgFollowMeToTable(WaiterAgent w, RestMenu m, List<String> uf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgAnimationFinishedGoToSeat() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgWhatWouldYouLike() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgOrderHasBeenReceived() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgHereIsYourFood() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgAnimationFinishedLeaveRestaurant() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgAnimationFinishedGoToCashier() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgPleaseReorder(List<String> uf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgHereIsYourCheck(Check c) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Received msgHereIsYourCheck from cashier. Total = "+c.getCost()));
		
	}

	@Override
	public void msgHereIsChange(double amt) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Received HereIsYourChange from cashier. Change = "+amt));
		
	}

	@Override
	public void msgDoDishesAsPunishment(int time) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Received msgDoDishesAsPunishment"));
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
