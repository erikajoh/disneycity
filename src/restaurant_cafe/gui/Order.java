package restaurant_cafe.gui;

import java.util.Collection;

import restaurant_cafe.CookAgent.OrderState;
import restaurant_cafe.interfaces.Waiter;

public class Order {
	public Waiter waiter;
	public Food food;
	public int exclude = 0; //which markets already ordered from for order
	public int table;
	public OrderState s;
    public Order(Waiter wp, String c, int t){
    	waiter = wp;
    	Collection<Food> foods = wp.getCook().getFoods();
		synchronized(foods){
    	  for(Food f : foods){
    		  if(f.getName().equals(c)){
    			  food = f;
    			  break;
    		  }
    	  }
		}
    	table = t;
    	s = OrderState.pending;
    }
};
