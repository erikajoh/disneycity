package transportation.Test.mocks;

import bank.gui.Bank;
import simcity.interfaces.Bank_Douglass;
import simcity.interfaces.Person;

public class mockPerson_Daron implements Person {
	
	String name;
	
	public mockPerson_Daron(String name) {
		this.name = name;
	}
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void msgWakeUp() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgGoToSleep() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgSetHungry() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgGoToWork(int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgStopWork(double amount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgDoneEntering() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgRentIsDue(double amount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgHereIsRent(double amount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgNeedMaintenance() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgFinishedMaintenance() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgFoodDone(boolean doneEating) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgDoneLeaving() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgReachedDestination(String destination) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgPayFare(double fare) {
		// TODO Auto-generated method stub
		
	}

	public void msgLeftBank(Bank theBank, int accountNumber, double change,
			double loanAmount, int loanTime) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgDoneEating(boolean success, double newMoneyOnHand) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgHereIsOrder(String order, int quantity) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void msgLeftBank(Bank_Douglass theBank, int accountNumber,
			double change, double loanAmount, int loanTime) {
		// TODO Auto-generated method stub
		
	}

}
