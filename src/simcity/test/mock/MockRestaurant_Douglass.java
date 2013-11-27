package simcity.test.mock;

import java.util.*;

import market.Market;
import bank.gui.Bank;
import agent.Constants;
import simcity.PersonAgent;
import simcity.RestMenu;
import simcity.Restaurant;
import simcity.interfaces.*;
import simcity.RestMenu;

public class MockRestaurant_Douglass extends Mock_Douglass implements Restaurant {

	public EventLog log;
	public Timer timer;
	
	public String type;
	public Map<String, Double> menu;
	public List<MyCustomer> waitingCustomers;
	
	public MockRestaurant_Douglass(String name, String type, Map<String, Double> menu) {
		super(name);
		this.type = type;
		log = new EventLog();
		this.menu = menu;
		timer = new Timer();
		waitingCustomers = new ArrayList<MyCustomer>();
	}

	@Override
	public String getName() { return name; }
	
	@Override
	public String getType() { return type; }

	public RestMenu getMenu() {
		RestMenu theMenu = new RestMenu();
		Set<String> keySet = menu.keySet();
		for(String key : keySet)
			theMenu.addItem(key, menu.get(key));
		return theMenu;
	}

	@Override
	public void personAs(PersonAgent personAgent, String personType,
			String name, double moneyOnHand) {
		log.add(new LoggedEvent("Received msgPersonAs: "
				+ "name = " + name + "; "
				+ "money = " + moneyOnHand));
		if(personType.equals("Customer")) {
			MyCustomer newCustomer = new MyCustomer(name, moneyOnHand, "");
			waitingCustomers.add(newCustomer);
		}
		final PersonAgent finalPerson = personAgent; 
		final double finalMoney = moneyOnHand; 
		timer.schedule(new TimerTask() {
			public void run() {
				finalPerson.setIsNourished(true);
				finalPerson.msgDoneEating(true, finalMoney);
			}
	    }, Constants.SECOND / 2);
	}
	
	class MyCustomer {
		String name;
		double moneyOnHand;
		String foodPreference;
		public MyCustomer(String aName, double money, String food) {
			name = aName;
			moneyOnHand = money;
			foodPreference = food;
		}
	}

	@Override
	public String getRestaurantName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isOpen() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addPerson(PersonAgent p, String type, String name, double money) {
		
	}

	@Override
	public void msgHereIsBill(Market m, double amt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgHereIsOrder(String food, int quantity, int ID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgEndOfShift() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBank(Bank b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMarket(Market m) {
		// TODO Auto-generated method stub
		
	}
}
