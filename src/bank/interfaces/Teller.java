package bank.interfaces;

import bank.interfaces.BankCustomer;
import bank.gui.Account;
import bank.gui.TellerGui;

/**
 * A sample Cashier interface built to unit test a CashierAgent.
 *
 * @author Dylan Eirinberg
 *
 */
public interface Teller {
	
	public void msgNewCustomer(BankCustomer person);
	
	public void msgOpenAccount(BankCustomer person, double cash);

	public void msgDepositCash(int accountNum, double cash);
	
	public void msgWithdrawCash(int accountNum, double cash);
	
	public void msgRobBank(double cash);
		
	public void msgLeavingBank();
	
	public void msgClose();
	
	public TellerGui getGui();

}