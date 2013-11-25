package restaurant_cafe.interfaces;

import restaurant_cafe.interfaces.Customer;
import restaurant_cafe.interfaces.Market;
import restaurant_cafe.interfaces.Waiter;

/**
 * A sample Cashier interface built to unit test a CashierAgent.
 *
 * @author Dylan Eirinberg
 *
 */
public interface Cashier {
	
	public void msgBillFromMarket(Market market, double total);
	
	public void msgProduceCheck(Customer c, Waiter w, String choice);
	
	public void msgWaiterHere(Customer customer);
	
	public void msgHereIsPayment(Customer cust, double cash);
	
	public void msgClearBill(Market market, boolean declined);
	
	public void msgNoMoney(Customer c);
	

}