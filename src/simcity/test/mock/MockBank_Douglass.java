package simcity.test.mock;

import java.util.*;

import bank.interfaces.BankCustomer;
import agent.Constants;
import simcity.PersonAgent;
import simcity.interfaces.Bank_Douglass;
import simcity.interfaces.Person;

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
	public void msgRequestAccount(Person person, double reqAmt, boolean present) {
		person.msgLeftBank(this, 1, reqAmt, 0.0, 0);
	}
	
	@Override
	public void msgRequestWithdrawal(Person person, int accountNumber, double amount, boolean present) {
		final int finalNum = accountNumber;
		final double finalAmount = amount;
		final Person finalPerson = person; 
		log.add(new LoggedEvent("Received msgRequestWithdrawal(): "
				+ "amount = " + amount + "; "));
		person.msgLeftBank(null, finalNum, finalAmount, 0.0, 3);
	}

	@Override
	public void msgRequestDeposit(Person person, int accountNumber, double amount, boolean present) {
		final double finalAmount = amount;
		final int finalNum = accountNumber;
		final double finalLoan = 0;
		final Person finalPerson = person; 
		log.add(new LoggedEvent("Received msgRequestDeposit: "
				+ "amount = " + amount + "; "
				+ "accountNumber = " + accountNumber));
		timer.schedule(new TimerTask() {
			public void run() {
				finalPerson.msgLeftBank(null, -finalNum, finalAmount, 0.0, 3);
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
		return;
	}
}
