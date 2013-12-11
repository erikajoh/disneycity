package transportation;

import market.Market;
import simcity.PersonAgent;
import simcity.Restaurant;
import simcity.interfaces.Person;

public interface Transportation{
	public abstract void msgWantToGo(String startLocation, String endLocation, Person person, String mover, String character);

	public abstract void msgPayFare(Person personAgent, float fareToPay);
	
	public abstract void msgSendDelivery(Restaurant restaurant, Market market, String food, int quantity, int id);

	public abstract void msgSendDelivery(Person person, Market market, String food, int quantity, String location);

	public abstract void setCrashing();

}
