package simcity.test.mock;

import java.util.*;

import agent.Constants;
import simcity.PersonAgent;
import simcity.interfaces.Bank;

public class MockBank_Douglass extends Mock_Douglass implements Bank {

	public EventLog log;
	public Timer timer;
	
	public MockBank_Douglass(String name) {
		super(name);
		log = new EventLog();
		timer = new Timer();
	}

	/*
	@Override
	public void msgHereIsBill(double amount, boolean lastBillFulfilled) {
		if(lastBillFulfilled) {
			log.add(new LoggedEvent("msgHereIsBill from cashier: valid payment"));
		}
		else {
			log.add(new LoggedEvent("msgHereIsBill from cashier: invalid payment"));
		}
	}
	*/
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void msgRequestWithdrawal(int accountNumber, double amount, PersonAgent person) {
		final double finalAmount = amount;
		final PersonAgent finalPerson = person; 
		log.add(new LoggedEvent("Received msgRequestWithdrawal: "
				+ "amount = " + amount + "; "
				+ "accountNumber = " + accountNumber));
		timer.schedule(new TimerTask() {
			public void run() {
				finalPerson.msgWithdrawalSuccessful(finalAmount);
			}
	    }, Constants.SECOND);
	}

	@Override
	public void msgRequestAccount(double amount, PersonAgent person) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgRequestDeposit(int accountNumber, double amount,
			PersonAgent person, boolean forLoan) {
		final double finalAmount = amount;
		final PersonAgent finalPerson = person; 
		log.add(new LoggedEvent("Received msgRequestDeposit: "
				+ "amount = " + amount + "; "
				+ "accountNumber = " + accountNumber + "; "
				+ "forLoan = " + forLoan));
		timer.schedule(new TimerTask() {
			public void run() {
				finalPerson.msgDepositSuccessful(finalAmount);
			}
	    }, Constants.SECOND);
	}

	@Override
	public void msgRequestLoan(int accountNumber, double amount,
			PersonAgent person) {
		// TODO Auto-generated method stub
		
	}
}
