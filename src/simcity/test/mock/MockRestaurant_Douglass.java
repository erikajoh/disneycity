package simcity.test.mock;

import java.util.*;

import market.Market;
import bank.gui.Bank;
import agent.Constants;
import simcity.PersonAgent;
import simcity.interfaces.*;
import simcity.RestMenu;

public class MockRestaurant_Douglass extends Mock_Douglass implements Restaurant_Douglass {

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

	@Override
	public RestMenu getMenu() { return menu; }

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
		timer.schedule(new TimerTask() {
			public void run() {
				finalPerson.setIsNourished(true);
				finalPerson.msgDoneEating(true, moneyOnHand);
			}
	    }, Constants.SECOND / 2);
	}
	
	/*
	@Override
	public void msgHereIsBill(double amount, boolean lastBillFulfilled) {
		if(lastBillFulfilled) {
			log.add(new LoggedEvent("msgHereIsBill from cashier: valid payment"));
		}
		else {
			log.add(new LoggedEvent("msgHereIsBill from cashier: invalid payment"));
		}
	}
	*/
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
	public boolean isOpen() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setBank(Bank b) {
		// TODO Auto-generated method stub
		
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
}
