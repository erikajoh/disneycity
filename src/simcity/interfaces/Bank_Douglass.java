package simcity.interfaces;

import bank.interfaces.BankCustomer;
import simcity.PersonAgent;

public interface Bank_Douglass {
	public String getBankName();
	
	public void msgRequestAccount(PersonAgent person, double reqAmt, boolean present);
	public void msgRequestDeposit(PersonAgent person, int accountNum, double reqAmt, boolean present);
	public void msgRequestWithdrawal(PersonAgent person, int accountNum, double reqAmt, boolean present);
	public void msgLeave(BankCustomer bc, int accountNum, double change, double loanAmt, int loanTime);
  
}
