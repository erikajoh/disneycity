package simcity.interfaces;

import bank.interfaces.BankCustomer;
import simcity.PersonAgent;
import simcity.interfaces.Person;

public interface Bank_Douglass {
	public String getBankName();
	
	public void msgRequestAccount(Person person, double reqAmt, boolean present);
	public void msgRequestDeposit(Person person, int accountNum, double reqAmt, boolean present);
	public void msgRequestWithdrawal(Person person, int accountNum, double reqAmt, boolean present);
	public void msgLeave(BankCustomer bc, int accountNum, double change, double loanAmt, int loanTime);
  
}
