package bank.interfaces;

import java.util.List;

import bank.interfaces.Person;
import bank.interfaces.Teller;
import bank.gui.Account;
import bank.gui.TellerGui;


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