package restaurant_rancho.interfaces;

import restaurant_rancho.RestMenu;
import restaurant_rancho.WaiterAgent;
import restaurant_rancho.CustomerAgent.AgentEvent;
import restaurant_rancho.CustomerAgent.AgentState;
import restaurant_rancho.gui.CustomerGui;

/**
 * A sample Customer interface built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public interface Customer {

	public abstract String getName();
	
	public abstract CustomerGui getGui();
	
	public abstract void gotHungry();
	
	public abstract void msgSitAtTable(Waiter w, RestMenu m) ;

	public abstract void msgAnimationFinishedGoToSeat() ;
	
	public abstract void msgRestaurantFull();
	
	public abstract void msgWhatIsYourOrder();
	
	public abstract void msgHereIsCheck(double amount);
	
	public abstract void msgHereIsChange(double amount);
	
	public abstract void msgCheckIncomplete(double owed);
	
	public abstract void msgWhatIsReorder(RestMenu menu);
	
	public abstract void msgHereIsYourFood();

	public abstract void msgAnimationFinishedLeaveRestaurant();

}