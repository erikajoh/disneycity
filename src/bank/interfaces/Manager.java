package bank.interfaces;

import java.util.List;

import bank.interfaces.BankCustomer;
import bank.interfaces.Teller;
import simcity.gui.SimCityGui;
import bank.gui.Account;

public interface Manager {
	public void msgTellerFree(Teller teller, BankCustomer bc);
	
	public void msgCustomerHere(BankCustomer bc);
	public void msgRequestAccount(BankCustomer bc, double amount);
	public void msgRequestDeposit(BankCustomer bc, int accountNumber, double amount);
	public void msgRequestWithdrawal(BankCustomer bc, int accountNumber, double amount);
	public void msgThief(BankCustomer bc, double amount);
	
	public void addAccount(Account account);
	public List<Account> getAccounts();
	public SimCityGui getGui();
}