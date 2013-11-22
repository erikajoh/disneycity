package bank.gui;

public class Account {
	private int number;
	private double balance;
	private double loanBalance;
	private double loanTime;
	
	public Account(int num){
		number = num;
	}
	public void setNumber(int num){
		number = num;
	}
	public int getNumber(){
		return number;
	}
	public void setBalance(double bal){
		balance = bal;
	}
	public double getBalance(){
		return balance;
	}
	public void setLoanBalance(double loanBal){
		loanBalance = loanBal;
	}
	public double getLoanBalance(){
		return loanBalance;
	}
	public void setLoanTime(double lt){
		loanTime = lt;
	}
	public double getLoanTime(){
		return loanTime;
	}
}
