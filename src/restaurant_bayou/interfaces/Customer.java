package restaurant_bayou.interfaces;

import java.util.List;

import restaurant_bayou.WaiterAgent;
import restaurant_bayou.CashierAgent.Check;
import restaurant_bayou.CustomerAgent.AgentEvent;
import restaurant_bayou.CustomerAgent.AgentState;
import restaurant_bayou.HostAgent.Menu;
import simcity.RestMenu;

/**
 * A sample Customer interface built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public interface Customer {
	public void msgFollowMeToTable(WaiterAgent w, RestMenu m, List<String> uf);
	
	public void msgAnimationFinishedGoToSeat();
	
	public void msgWhatWouldYouLike();
	
	public void msgOrderHasBeenReceived();
	
	public void msgHereIsYourFood();

	public void msgAnimationFinishedLeaveRestaurant();
	
	public void msgAnimationFinishedGoToCashier();
	
	public void msgPleaseReorder(List<String> uf);
	
	public void msgHereIsYourCheck(Check c);
	
	public void msgHereIsChange(double amt);
	
	public void msgDoDishesAsPunishment(int time);

}