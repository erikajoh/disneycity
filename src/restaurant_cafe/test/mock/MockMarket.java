package restaurant_cafe.test.mock;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import restaurant_cafe.gui.Food;
import restaurant_cafe.gui.Menu;
import restaurant_cafe.gui.MenuItem;
import restaurant_cafe.interfaces.Cook;
import restaurant_cafe.interfaces.Market;

/**
 * A sample MockCustomer built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public class MockMarket extends Mock implements Market {

	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public MockCashier cashier;
	public Collection<MyFood> foods =  Collections.synchronizedList(new ArrayList<MyFood>());
	String name;
	int number;
	Menu menu;
	int amount;
	boolean cashierPaid = false;
	
	public class Order {
		Cook cook;
	    MyFood food;
	    OrderState state;
	    int amountRequested;
	    int amountToGive;
		
	    public Order(Cook cp, Food f, int amt){
	    	cook = cp;
			synchronized(foods){
			  for(MyFood fd : foods){
				  if(fd.food == f){
			    	  food = fd;
			    	  break;
				  }
			  }
			}
			state = OrderState.requested;
	    	amountRequested = amt;
	    }
	};
	enum OrderState{requested, complete, done};
	public Collection<Order> orders = new ArrayList<Order>();
	
	public class MyFood {
		Food food;
		 int amount;
		
	    public MyFood(Food f, int amt){
	    	food = f;
	    	amount = amt;
	    }
	};
	
	public MockMarket(String name){
		super(name);
	}

	public MockMarket(int num, Menu m, int amt) {
		this("Market"+num);
		String name = "Market"+num;
		number = num;
		this.name = name;

		menu = m;
    	for(MenuItem item : menu.getItems()){
    	  foods.add(new MyFood(item.getFood(), amt));
    	}
	}
	
	
	public void setCashier(MockCashier c){
		cashier = c;
	}

	public void msgPaidBill(double total, boolean fullyPaid){
		cashierPaid = true;
	}
	
	public void msgHereIsOrder(Cook c, Food f, int amt){
		orders.add(new Order(c, f, amt));
	}
	
	public void msgFulfilledOrder(Order o){
		
	}
	
	public boolean pickAndExecuteAnAction(){
		for(Order order : orders){
		  if(order.state == OrderState.requested){
			  giveFoodToCook(order); //can go to this action b/c other one only controls timer
			 return true;
		   }
		}
		if(cashierPaid == true){
			clearBill();
			return true;
		}
		return false;
	}
	
	private void clearBill(){
		cashierPaid = false;
		cashier.msgClearBill(this);
	}
	
	private void giveFoodToCook(Order o){
		double total = 0.5*(o.food.food.getPrice()*o.amountToGive);
		total = Math.round(total * 100.0) / 100.0;
		cashier.msgBillFromMarket(this, total);
		o.state = OrderState.done;
		orders.remove(o);
	}
	
	public int getFoodAmount(Food f){
		int amount = 0;
		  synchronized(foods){
			for(MyFood food : foods){
				if(f == food.food){
					amount = f.getAmount(); break;
				}
			}
		  }
	   return amount;
	}
	
	public String getName(){
		return name;
	}
	
	public void setStock(Food f, int amt){
		
	}
	
	public void setAllStockToAmt(int amt){
		
	}

	@Override
	public void msgFulfilledOrder(restaurant_cafe.MarketAgent.Order o) {
		// TODO Auto-generated method stub
		
	}
}
