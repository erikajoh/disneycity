package simcity.gui;
import agent_rancho.Agent;
import restaurant_rancho.gui.RestaurantRancho;
import restaurant_rancho.gui.RestaurantRanchoGui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class SimCityPanel extends JPanel{
	
		 RestaurantRanchoGui ranchoGui;
	     RestaurantRancho ranchoRest;
	     private JPanel group = new JPanel();
	     
	     public SimCityPanel() {
	    	 ranchoGui = new RestaurantRanchoGui("Rancho de Zocales");
	    	 ranchoRest = ranchoGui.getRestaurant();
	    	 ranchoRest.addPerson("Cook", "cook", false);
		     ranchoRest.addPerson("Waiters", "w", false);
		     ranchoRest.addPerson("Cashier", "cash", false);
		     ranchoRest.addPerson("Market", "Trader Joes", false);
		     ranchoRest.addPerson("Host", "Host", false);
		     ranchoRest.addPerson("Customers", "c", true);
		     ranchoGui.run();
		     
		     setLayout(new GridLayout());
	     }


}
