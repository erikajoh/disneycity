package bank.interfaces;


import bank.interfaces.BankCustomer;
import bank.interfaces.Teller;
import bank.gui.Account;


/**
 * A sample Customer interface built to unit test a CashierAgent.
 *
 * @author Dylan Eirinberg
 *
 */
public interface BankCustomer {
	public void	msgRequestNewAccount(double requestAmt);
	public void	msgRequestDeposit(double requestAmt);
	public void	msgRequestWithdraw(double requestAmt);
	public void	msgRequestLoan(double requestAmt);
	
	public void msgGoToTeller(Teller teller);
	public void msgAccountOpened(int accountNum);
	public void msgMoneyDeposited();
	public void msgMoneyWithdrawn(double amtWithdrawn);
	public void msgLoanDecision(boolean status);
	
	public int getAccountNum();
	public Person getPerson();
	public double getBalance();

}