package restaurant.interfaces;

import restaurant.CookAgent;


public interface Market {
	public void msgNeedFood(CookAgent c, String food, int am);
	
	public void msgHereIsPayment(double amount, int oNum) ;

}
