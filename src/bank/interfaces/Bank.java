package bank.interfaces;

import bank.interfaces.BankCustomer;
import bank.interfaces.Teller;
import bank.gui.BankGui;

public interface Bank {
	public void msgTellerFree(Teller teller, BankCustomer bc);
	
	public void msgCustomerHere(Person person);
	public void msgRequestAccount(double amount, Person person);
	public void msgRequestDeposit(int accountNumber, double amount, Person person, boolean forLoan);
	public void msgRequestWithdrawal(int accountNumber, double amount, Person person);
	public void msgRequestLoan(int accountNumber, double amount, Person person);
	
	public BankGui getGui();
	public Person getPerson(BankCustomer bc);
}