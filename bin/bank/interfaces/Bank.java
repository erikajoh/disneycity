package bank.interfaces;

import bank.interfaces.Person;
import bank.interfaces.Teller;

public interface Bank {
	public void msgEnteredBank(Person person);
	
	public void msgTellerFree(Teller teller);

}