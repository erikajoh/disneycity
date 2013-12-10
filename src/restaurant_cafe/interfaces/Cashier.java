package restaurant_cafe.interfaces;

import restaurant_cafe.interfaces.Customer;
import simcity.interfaces.Market_Douglass;
import restaurant_cafe.interfaces.Waiter;

/**
 * A sample Cashier interface built to unit test a CashierAgent.
 *
 * @author Dylan Eirinberg
 *
 */
public interface Cashier {
	
	public void msgBillFromMarket(Market_Douglass market, double total);
	
	public void msgProduceCheck(Customer c, Waiter w, String choice);
	
	public void msgWaiterHere(Customer customer);
	
	public void msgHereIsPayment(Customer cust, double cash);
	
	public void msgClearBill(Market_Douglass market, boolean declined);
	
	public void msgNoMoney(Customer c);

	public void subtract(double i);

	public void msgShiftDone();
	

}