package restaurant_rancho.interfaces;

import restaurant_rancho.CookAgent;


public interface Market {
	public void msgNeedFood(CookAgent c, String food, int am);
	
	public void msgHereIsPayment(double amount, int oNum) ;

}
