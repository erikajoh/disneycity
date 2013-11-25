package restaurant_cafe.interfaces;

import restaurant_cafe.gui.Check;
import restaurant_cafe.gui.Menu;
import restaurant_cafe.interfaces.Waiter;

/**
 * A sample Customer interface built to unit test a CashierAgent.
 *
 * @author Dylan Eirinberg
 *
 */
public interface Customer {
	public void msgGotHungry();
	
	public void msgRestaurantFull();

	public void msgFollowMe(Waiter w, int tn, Menu m);
	
	public void msgAskOrder();
	
	public void msgReorder();
	
	public void msgHereIsFood();
	
	public void msgHereIsCheck(Check c);
	
	public void msgHereIsChange(double change);
	
	public void msgCleanDishes();

	public int getNumber();

}