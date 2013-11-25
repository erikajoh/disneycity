package restaurant_cafe.interfaces;

import restaurant_cafe.MarketAgent.Order;
import restaurant_cafe.gui.Food;
import restaurant_cafe.interfaces.Cook;

public interface Market {
	public void msgPaidBill(double total, boolean fullyPaid);
	
	public void msgHereIsOrder(Cook c, Food f, int amt);
	
	public void msgFulfilledOrder(Order o);
	
	public int getFoodAmount(Food f);
	
	public String getName();
	
	public void setStock(Food f, int amt);
	
	public void setAllStockToAmt(int amt);
}