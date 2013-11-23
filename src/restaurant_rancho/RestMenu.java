package restaurant_rancho; 

import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;


public class RestMenu {
	
	public Hashtable<String, Double> menuItems;
	public List<String> menuList;
	
	
	public RestMenu() {
		menuItems = new Hashtable<String, Double>();
		menuList = new ArrayList<String>();
		
	}
	
	public String getItemAt(int index) {
		return menuList.get(index);
		
	}
	
	public void removeItem(String choice) {
		for (int i = 0; i < menuList.size(); i++) {
			if (menuList.get(i) == choice) menuList.remove(i);
		}
	}
	
	public void addItem(String choice, double price ) {
		menuList.add(choice);
		menuItems.put(choice,  price);
	}
	
	public void replenish() {
		menuList.clear();
		menuList.add("Pizza");
		menuList.add("Steak");
		menuList.add("Chicken");
		menuList.add("Salad");
		menuList.add("Latte");
	}
	
	
	
	
}
