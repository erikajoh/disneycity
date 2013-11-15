package restaurant.interfaces;

public interface Cashier {
	
	public abstract void msgComputeCheck(Waiter w, Customer c, String choice);
	
	public abstract void msgHereIsMoney(Customer c, double amount);

}
