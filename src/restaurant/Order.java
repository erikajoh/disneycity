package restaurant;

import java.util.Timer;

import restaurant.CookAgent.orderState;

public class Order { 
	Timer timer;
	WaiterAgent w; 
	String choice;
	int table;
	orderState os;
	int cookingNum;

	Order(WaiterAgent wa, String c, int t){
		w = wa;
		choice = c;
		table = t;
		timer = new Timer();
		os = orderState.pending;
	}
		
}