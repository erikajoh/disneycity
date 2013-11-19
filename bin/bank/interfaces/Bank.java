package bank.interfaces;

import bank.interfaces.BankCustomer;
import bank.interfaces.Teller;
import bank.gui.BankGui;

public interface Bank {
	public void msgTellerFree(Teller teller);
	
	public void msgRequestAccount(double amount, Person person);
	public void msgRequestDeposit(int accountNumber, double amount, Person person, boolean forLoan);
	public void msgRequestWithdrawal(int accountNumber, double amount, Person person);
	public void msgRequestLoan(int accountNumber, double amount, Person person);
	
}