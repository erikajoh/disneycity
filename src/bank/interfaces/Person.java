package bank.interfaces;

public interface Person {
	
	public void msgArrive(int num, double requestAmt);
	public void msgLeave(int num, double balance, double loanAmt, int loanTime);
	
	public String getName();

}
