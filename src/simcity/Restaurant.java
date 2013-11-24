package simcity;

import restaurant_rancho.RestMenu;
import restaurant_rancho.interfaces.Bank;


public interface Restaurant {
	
	public RestMenu getMenu();
	
	public String getRestaurantName();
	
	public void setBank(Bank b);
	
	public boolean isOpen();

	public void personAs(PersonAgent p, String type, String name, double money);
	
	public void addPerson(PersonAgent p, String type, String name, double money, String choice);
}
