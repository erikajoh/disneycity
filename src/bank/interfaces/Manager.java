package bank.interfaces;

import bank.interfaces.BankCustomer;
import bank.interfaces.Teller;
import simcity.gui.SimCityGui;

public interface Manager {
	public void msgTellerFree(Teller teller, BankCustomer bc);
	
	public void msgCustomerHere(Person person);
	public void msgRequestAccount(double amount, Person person);
	public void msgRequestDeposit(int accountNumber, double amount, Person person, boolean forLoan);
	public void msgRequestWithdrawal(int accountNumber, double amount, Person person);
	public void msgRequestLoan(int accountNumber, double amount, Person person);
	
	public SimCityGui getGui();
	public Person getPerson(BankCustomer bc);
}