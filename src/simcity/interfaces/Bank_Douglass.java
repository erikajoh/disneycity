package simcity.interfaces;

import simcity.PersonAgent;

public interface Bank_Douglass {
	public String getName();
	
	public void msgRequestAccount(double amount, PersonAgent person);
	public void msgRequestDeposit(int accountNumber, double amount, PersonAgent person, boolean forLoan);
	public void msgRequestWithdrawal(int accountNumber, double amount, PersonAgent person);
	public void msgRequestLoan(int accountNumber, double amount, PersonAgent person);
}
