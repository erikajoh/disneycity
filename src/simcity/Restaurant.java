package simcity;

import restaurant_rancho.RestMenu;
import restaurant_rancho.interfaces.Bank;


public interface Restaurant {
	
	public RestMenu getMenu();
	
	public void setBank(Bank b);

	public void personAs(PersonAgent p, String type, String name, double money, String choice);
	
	public void addPerson(PersonAgent p, String type, String name, double money, String choice);
}
