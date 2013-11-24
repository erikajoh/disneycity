package restaurant_bayou.interfaces;

import java.util.List;

import restaurant_bayou.CustomerAgent;
import restaurant_bayou.CashierAgent.Check;
import restaurant_bayou.HostAgent.Menu;
import restaurant_bayou.HostAgent.Table;
import simcity.RestMenu;

/**
 * A sample Waiter interface built to unit test a CashierAgent.
 *
 * @author Erika Johnson
 *
 */
public interface Waiter {
	public void msgOrderIsDone(int t);

	public void msgLeavingTable(CustomerAgent cust);

	public void msgAtTable();
	
	public void msgSeatCustomer(CustomerAgent cust, RestMenu m);
	
	public void msgImReadyToOrder(CustomerAgent cust);
	
	public void msgWaiterReady();
	
	public void msgHereIsMyChoice(CustomerAgent cust, String c);
	
	public void msgDoneEating(CustomerAgent c);
	
	public void msgDoneLeaving(CustomerAgent c);
	
	public void msgOutOfFood(List<String> uf);
	
	public void msgOKToGoOnBreak(boolean ok);
	
	public void msgHereIsCheck(Check c);

}