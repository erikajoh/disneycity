package simcity.interfaces;

import java.util.Map;

import simcity.PersonAgent;

public interface Restaurant {
	public String getName();
	public String getType();
	public Map<String, Double> getMenu();
	public void msgPersonAs(PersonAgent personAgent, String personType,
			String name, double moneyOnHand, String foodPreference);
}
