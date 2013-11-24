package restaurant_pizza;

import restaurant_pizza.interfaces.Market;

public class FoodBill {
	public String order;
	public Market market;
	public double amountDue;
	public FoodBill(String aOrder, Market aMarket, double anAmountDue) {
		order = aOrder;
		market = aMarket;
		amountDue = anAmountDue;
	}
}
