package simcity;

import simcity.RestMenu;
import bank.gui.Bank;
import market.Market;

public interface Restaurant {
	
	public RestMenu getMenu();
	
	public String getRestaurantName();
	
	public void setBank(Bank b);
	
	public void setMarket(Market m);
	
	public boolean isOpen();

	public void personAs(PersonAgent p, String type, String name, double money);
	
	public void addPerson(PersonAgent p, String type, String name, double money);
	
	public String getType();

	public void msgHereIsOrder(String food, int quantity, int ID);
	
	public void msgEndOfShift();
}
