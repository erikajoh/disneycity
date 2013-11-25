package restaurant_cafe.test.mock;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import restaurant_cafe.CookAgent.Order;
import restaurant_cafe.gui.Food;
import restaurant_cafe.gui.Menu;
import restaurant_cafe.gui.MenuItem;
import restaurant_cafe.interfaces.Cashier;
import restaurant_cafe.interfaces.Cook;
import restaurant_cafe.interfaces.Waiter;

/**
 * A sample MockCustomer built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public class MockCook extends Mock implements Cook {

	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public MockCashier cashier;
	public Collection<MyFood> foods =  Collections.synchronizedList(new ArrayList<MyFood>());
	String name;
	int number;
	Menu menu;
	int amount;
	
	public class MyFood {
		Food food;
		 int amount;
		
	    public MyFood(Food f, int amt){
	    	food = f;
	    	amount = amt;
	    }
	};
	
	public MockCook(String name){
		super(name);
	}

	public void msgHereIsOrder(Waiter w, String choice, Integer table){
		
	}
	
	public void msgFulfilledOrder(Food food, int amount){
		
	}
	
	public void msgOutOfFood(Food f, int ex){
		
	}
	
	public void msgFoodDone(Order o){
		
	}
	
	public String getName(){
		return name;
	}
}
