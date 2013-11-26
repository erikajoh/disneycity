package transportation;

import simcity.PersonAgent;
import simcity.Restaurant;

public interface Transportation {
	public abstract void msgWantToGo(String startLocation, String endLocation, PersonAgent person, String mover, String character);

	public abstract void msgPayFare(PersonAgent personAgent, float fareToPay);
	
	public abstract void msgSendDelivery(Restaurant restaurant, String food, int quantity);
}
