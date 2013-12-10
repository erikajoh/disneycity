package restaurant_rancho.interfaces;
import simcity.RestMenu;

public interface Cashier {
	
	public void subtract(double amount);
	
	public void msgShiftDone(double wage);
	
	public abstract void msgComputeCheck(Waiter w, Customer c, String choice, RestMenu menu);
	
	public abstract void msgHereIsMoney(Customer c, double amount);

}
