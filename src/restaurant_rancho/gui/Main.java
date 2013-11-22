package restaurant_rancho.gui;

import restaurant_rancho.gui.RestaurantRanchoGui;
import restaurant_rancho.gui.RestaurantRancho;

import javax.swing.JFrame;

public class Main{
	
public static void main(String[] args) {
	   // public void run() {
	        RestaurantRanchoGui gui = new RestaurantRanchoGui("name");
	        RestaurantRancho rest = gui.getRestaurant();
	        rest.addPerson("Cook", "cook", false);
	        rest.addPerson("Waiters", "w", false);
	        rest.addPerson("Cashier", "cash", false);
	        rest.addPerson("Market", "Trader Joes", false);
	        rest.addPerson("Host", "Host", false);
	        rest.addPerson("Customers", "c", true);
	        gui.run();
	        
	    }
}