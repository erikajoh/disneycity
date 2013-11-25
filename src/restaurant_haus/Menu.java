package restaurant_haus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Menu {
	/*Need implementation of List<String> choices*/
	ArrayList<String> choices = new ArrayList<String>();
	Map<String, Double> prices = new HashMap<String, Double>();
	
	public Menu() {
		choices.add("Pastrami Cheeseburger");
		choices.add("Chicken Sausage Pretzel Roll");
		choices.add("BLT Flatbread");
		choices.add("Apple & Cheddar Salad");
		
		prices.put("Pastrami Cheeseburger", 11.49);
	    prices.put("Chicken Sausage Pretzel Roll", 8.99);
	    prices.put("BLT Flatbread", 8.79);
	    prices.put("Apple & Cheddar Salad", 7.99);
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
