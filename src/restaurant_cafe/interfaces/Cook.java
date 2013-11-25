package restaurant_cafe.interfaces;

import restaurant_cafe.CookAgent.Order;
import restaurant_cafe.gui.Food;
import restaurant_cafe.interfaces.Cook;
import restaurant_cafe.interfaces.Waiter;

public interface Cook {
	public void msgHereIsOrder(Waiter w, String choice, Integer table);
	
	public void msgFulfilledOrder(Food food, int amount);
	
	public void msgOutOfFood(Food f, int ex);
	
	public void msgFoodDone(Order o);
	
	public String getName();

}