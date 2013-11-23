package restaurant_rancho; 

import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;


public class RestMenu {
	
	Hashtable<String, Double> menuItems;
	public List<String> menuList;
	
	
	public RestMenu() {
		menuItems = new Hashtable<String, Double>();
		menuItems.put("Pizza", 10.50);
		menuItems.put("Steak", 14.50);
		menuItems.put("Salad", 7.50);
		menuItems.put("Chicken", 12.50);
		menuItems.put("Latte", 3.25);
		menuList = new ArrayList<String>();
		menuList.add("Pizza");
		menuList.add("Steak");
		menuList.add("Chicken");
		menuList.add("Salad");
		menuList.add("Latte");
		
	}
	
	public String getItemAt(int index) {
		return menuList.get(index);
		
	}
	
	public void removeItem(String choice) {
		for (int i = 0; i < menuList.size(); i++) {
			if (menuList.get(i) == choice) menuList.remove(i);
		}
	}
	
	public void addItem(String choice) {
		menuList.add(choice);
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
