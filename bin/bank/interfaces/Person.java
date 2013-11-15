package restaurant.interfaces;

import java.util.List;

import restaurant.interfaces.Person;
import restaurant.interfaces.Teller;
import restaurant.gui.Account;
import restaurant.gui.TellerGui;


/**
 * A sample Customer interface built to unit test a CashierAgent.
 *
 * @author Dylan Eirinberg
 *
 */
public interface Person {
	public void msgGoToTeller(Teller teller);
	public void msgAccountOpened(Account account);
	public void msgMoneyDeposited();
	public void msgMoneyWithdrawn(double amtWithdrawn);
	public void msgLoanDecision(boolean status);
	
	public List<Account> getAccounts();

}