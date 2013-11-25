package restaurant_haus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Menu {
	/*Need implementation of List<String> choices*/
	ArrayList<String> choices = new ArrayList<String>();
	Map<String, Double> prices = new HashMap<String, Double>();
	
	public Menu() {
		choices.add("steak");
		choices.add("chicken");
		choices.add("pizza");
		choices.add("salad");
		
		prices.put("steak", 15.99);
		prices.put("chicken", 10.99);
		prices.put("pizza", 8.99);
		prices.put("salad", 5.99);
	}
	
	public String returnOrder(int n) {
		return choices.get(n);
	}
	
	public int getNumItems() {
		return choices.size();
	}
	
	public boolean isOnMenu(String food) {
		if(choices.contains(food)) {
			return true;
		}
		else return false;
	}
	
	public void removeItem(String food) {
		choices.remove(food);
	}
	
	public void addItem(String food) {
		choices.add(food);
	}
	
	public double checkPrice(String food) {
		return prices.get(food);
	}
}
