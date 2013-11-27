package simcity.interfaces;
import bank.gui.Bank;

public interface Person {
	
	public String getName();
	public void msgWakeUp() ;
	
	public void msgGoToSleep() ;
	
	public void msgSetHungry() ;

	public void msgGoToWork(int i);
	
	public void msgStopWork(double amount) ;
	
	// from Housing
	public void msgDoneEntering() ;
	
	public void msgRentIsDue(double amount) ;
	
	public void msgHereIsRent(double amount) ;
	
	public void msgNeedMaintenance() ;
	public void msgFinishedMaintenance() ;
	
	public void msgFoodDone(boolean doneEating) ;
	
	public void msgDoneLeaving() ;
	
	// from Transportation
	public void msgReachedDestination(String destination) ;
	
	public void msgPayFare(double fare) ;

	// from Bank
	public void msgLeftBank(Bank_Douglass theBank, int accountNumber, double change, double loanAmount, int loanTime) ;
	// from Restaurant
	public void msgDoneEating(boolean success, double newMoneyOnHand) ;
	
	// from Market
	public void msgHereIsOrder(String order, int quantity);
	

}
