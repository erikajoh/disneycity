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
	public void	msgRequestDeposit(double requestAmt, int accountNum);
	public void	msgRequestWithdraw(double requestAmt, int accountNum);
	public void	msgThief(double requestAmt);
	
	public void msgGoToTeller(Teller teller);
	public void msgAccountOpened(int accountNum, double change);
	public void msgMoneyDeposited(double change, double loanAmt, int loanTime);
	public void msgMoneyWithdrawn(double change, double loanAmt, int loanTime);
	public void msgRobbedBank(double cash, boolean success);
	
	public int getAccountNum();
	public double getBalance();
	public double getChange();

	public double getLoanAmount();
	public int getLoanTime();

}