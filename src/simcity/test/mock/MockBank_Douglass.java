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
	public void msgRequestWithdrawal(double amount, int accountNumber, PersonAgent person) {
		final double finalAmount = amount;
		final PersonAgent finalPerson = person; 
		log.add(new LoggedEvent("Received msgRequestWithdrawal: "
				+ "amount = " + amount + "; "
				+ "accountNumber = " + accountNumber));
		timer.schedule(new TimerTask() {
			public void run() {
				finalPerson.msgWithdrawalSuccessful(finalAmount);
			}
	    }, Constants.SECOND / 2);
	}
}
