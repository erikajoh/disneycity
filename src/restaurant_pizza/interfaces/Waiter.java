package restaurant_pizza.interfaces;

import restaurant_pizza.Check;
import restaurant_pizza.CookAgent;
import restaurant_pizza.HostAgent;
import restaurant_pizza.gui.WaiterGui;

public interface Waiter {

	public abstract void msgHereIsCheck(Check aCheck);
	
	public abstract void msgShiftDone();
}
