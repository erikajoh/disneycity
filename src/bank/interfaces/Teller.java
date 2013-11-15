package restaurant.interfaces;

import restaurant.interfaces.Person;
import restaurant.gui.Account;
import restaurant.gui.TellerGui;

/**
 * A sample Cashier interface built to unit test a CashierAgent.
 *
 * @author Dylan Eirinberg
 *
 */
public interface Teller {
	
	public void msgNewCustomer(Person person);
	
	public void msgOpenAccount(Person person, double cash);

	public void msgDepositCash(Account account, double cash);
	
	public void msgWithdrawCash(Account account, double cash);
	
	public void msgAskForLoan(Account account, double cash);
	
	public void msgLeavingBank();
	
	public TellerGui getGui();

}