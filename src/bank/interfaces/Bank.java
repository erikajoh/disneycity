package restaurant.interfaces;

import restaurant.interfaces.Person;
import restaurant.interfaces.Teller;

public interface Bank {
	public void msgEnteredBank(Person person);
	
	public void msgTellerFree(Teller teller);

}