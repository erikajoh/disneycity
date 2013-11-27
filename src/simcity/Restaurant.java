package simcity;

import simcity.RestMenu;
import bank.gui.Bank;
import market.Market;
import simcity.interfaces.Market_Douglass;
import simcity.interfaces.Person;
import simcity.interfaces.Bank_Douglass;

public interface Restaurant {
	
	public RestMenu getMenu();
	
	public String getRestaurantName();
		
	public void setBank(Bank_Douglass b);
	
	public void setMarket(Market_Douglass m);
	
	public boolean isOpen();

	public void personAs(Person p, String type, String name, double money);
	
	public void addPerson(Person p, String type, String name, double money);
	
	public String getType();
	
	public void msgHereIsBill(Market_Douglass m, double amt);

	public void msgHereIsOrder(String food, int quantity, int ID);
		
	public void msgEndOfShift();

}
