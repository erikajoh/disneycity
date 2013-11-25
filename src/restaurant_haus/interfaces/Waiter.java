package restaurant_haus.interfaces;

/**
 * A sample Customer interface built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public interface Waiter {
	public abstract void msgCheckPlease(Customer c);
	
	public abstract void msgHereIsCheck(Customer c, double price);
}