package simcity; 

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
	
	public void remove(String choice) {
		for (int i = 0; i < menuList.size(); i++) {
			if (menuList.get(i) == choice) menuList.remove(i);
		}
	}
	
	public void addItem(String choice, double price ) {
		menuList.add(choice);
		menuItems.put(choice,  price);
	}
	
	public void add(String choice) {
		menuList.add(choice);
	}
	

	
	
	
	
}
