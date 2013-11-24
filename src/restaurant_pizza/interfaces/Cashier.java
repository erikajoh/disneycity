package restaurant_pizza.interfaces;

import restaurant_pizza.Check;
import restaurant_pizza.FoodBill;
import simcity.RestMenu;

public interface Cashier {

	public abstract void msgCustomerNeedsCheck(Waiter w, String order,
			Customer customer, RestMenu menu);

	public abstract void msgPayingMyCheck(Customer c, Check check,
			double payment);

	public abstract void msgHereIsBill(FoodBill fb);
}