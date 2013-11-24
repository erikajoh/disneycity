package restaurant_rancho.interfaces;
import restaurant_rancho.RestMenu;

public interface Cashier {
	
	public abstract void msgComputeCheck(Waiter w, Customer c, String choice, RestMenu menu);
	
	public abstract void msgHereIsMoney(Customer c, double amount);

}
