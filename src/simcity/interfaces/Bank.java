package simcity.interfaces;

import simcity.PersonAgent;

public interface Bank {
	public String getName();
	
	public void msgRequestWithdrawal(double amount, int accountNumber, PersonAgent person);
}
