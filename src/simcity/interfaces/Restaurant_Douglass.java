package simcity.interfaces;

import java.util.Map;

import market.Market;
import bank.gui.Bank;
import simcity.PersonAgent;
import simcity.RestMenu;

public interface Restaurant_Douglass {
	public String getName();
	public String getType();
	public RestMenu getMenu();
	public void personAs(PersonAgent personAgent, String personType,
			String name, double moneyOnHand);
	public boolean isOpen();
	public void setBank(Bank b);
	public void msgHereIsBill(Market m, double amt);
	public void msgHereIsOrder(String food, int quantity, int ID);
	public void msgEndOfShift();
}
