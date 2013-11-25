package restaurant_haus.interfaces;


/**
 * A sample Customer interface built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public interface Cashier {
	public abstract void msgNeedCheck (Waiter w, String choice, Customer c);
	
	public abstract void msgHereIsPayment(Customer customer, double payment);

}