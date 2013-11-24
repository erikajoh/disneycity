package restaurant_pizza.interfaces;

import java.util.HashMap;

import restaurant_pizza.Check;
import restaurant_pizza.HostAgent;
import restaurant_pizza.gui.CustomerGui;

public interface Customer {

	public abstract void msgPaymentApproved();

	public abstract void msgPaymentInvalid();
	
	public abstract void msgLeaveRestaurant();
}