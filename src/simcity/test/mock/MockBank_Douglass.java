package simcity.test.mock;

import java.util.*;

import bank.interfaces.BankCustomer;
import agent.Constants;
import simcity.PersonAgent;
import simcity.interfaces.Bank_Douglass;

public class MockBank_Douglass extends Mock_Douglass implements Bank_Douglass {

	public EventLog log;
	public Timer timer;
	
	public MockBank_Douglass(String name) {
		super(name);
		log = new EventLog();
		timer = new Timer();
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void msgRequestAccount(PersonAgent person, double reqAmt,
			boolean present) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgRequestWithdrawal(PersonAgent person, int accountNumber, double amount, boolean present) {
		final int finalNum = accountNumber;
		final double finalAmount = amount;
		final PersonAgent finalPerson = person; 
		log.add(new LoggedEvent("Received msgRequestWithdrawal: "
				+ "amount = " + amount + "; "
				+ "accountNumber = " + accountNumber));
		timer.schedule(new TimerTask() {
			public void run() {
				finalPerson.msgLeftBank(null, finalNum, -finalAmount, 0.0, 3);
			}
	    }, Constants.SECOND);
	}

	@Override
	public void msgRequestDeposit(PersonAgent person, int accountNumber, double amount, boolean present) {
		final double finalAmount = amount;
		final int finalNum = accountNumber;
		final double finalLoan = 0;
		final PersonAgent finalPerson = person; 
		log.add(new LoggedEvent("Received msgRequestDeposit: "
				+ "amount = " + amount + "; "
				+ "accountNumber = " + accountNumber));
		timer.schedule(new TimerTask() {
			public void run() {
				finalPerson.msgLeftBank(null, finalNum, finalAmount, 0.0, 3);
			}
	    }, Constants.SECOND);
	}

	@Override
	public String getBankName() {
		return name;
	}

	@Override
	public void msgLeave(BankCustomer bc, int accountNum, double change,
			double loanAmt, int loanTime) {
		
	}
}
