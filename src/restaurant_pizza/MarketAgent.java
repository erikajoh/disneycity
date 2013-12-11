package restaurant_pizza;

import agent_pizza.Agent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Semaphore;

import restaurant_pizza.interfaces.Cashier;
import restaurant_pizza.interfaces.Market;

/**
 * Market agent
 */
public class MarketAgent extends Agent implements Market {

	// ***** DATA *****
	private Map<String, Integer> inventory = Collections.synchronizedMap(new HashMap<String, Integer>());
	private List<MarketOrder> marketOrders = Collections.synchronizedList(new LinkedList<MarketOrder>());
	
	private static int defaultStock = 20; // amount of each food type market starts off with
	
	private String name;
	
	CookAgent cook = null;
	Cashier cashier = null;
	Semaphore pauseAction = new Semaphore(0, true);
	
	private double totalCashierPayments = 0.0; // keeps track of the money received
	private double foodBillCost = 5.0;

	public MarketAgent(String name) {
		super();
		this.name = name;
	}
	
	public void initializeMaps() throws Exception {
		URL fileURL = getClass().getResource("res/MenuTextFile.txt");
		URI fileURI = fileURL.toURI();
		BufferedReader br = new BufferedReader(new FileReader(new File(fileURI)));
		int numItems = Integer.parseInt(br.readLine());
		StringTokenizer st;
		for(int i = 0; i < numItems; i++) {
			st = new StringTokenizer(br.readLine());
			String itemName = st.nextToken();
			st.nextToken();
			int itemCookTime = Integer.parseInt(st.nextToken());
			inventory.put(itemName, defaultStock);
		}
		br.close();
	}

	// for unit testing
	public void setDefaultStock(int aDefaultStock) {
		defaultStock = aDefaultStock;
	}
	
	public void setCook(CookAgent theCook) {
		cook = theCook;
	}
	
	public void setCashier(Cashier theCashier) {
		cashier = theCashier;
	}
	
	/* (non-Javadoc)
	 * @see restaurant.Market#getName()
	 */
	@Override
	public String getName() {
		return name;
	}
	
	// ***** MESSAGES *****
	public void msgNeedFood(MarketOrder mo) {
		marketOrders.add(mo);
		stateChanged();
	}
	
	//TODO: CashierMarket interaction step 3
	@Override
	public void msgHereIsBill(double amount, boolean lastBillFulfilled) {
		totalCashierPayments += amount;
		print("Received " + amount + " from cashier");
		print("Total amount from cashier so far = " + totalCashierPayments);
		if(!lastBillFulfilled)
			print("Cashier still owes us!");
		stateChanged();
	}
	
	// ***** SCHEDULER *****
	
	protected boolean pickAndExecuteAnAction() {
		if(!marketOrders.isEmpty()) {
			MarketOrder currOrder = marketOrders.remove(0);
			int amountToGive = Math.min(inventory.get(currOrder.foodType), currOrder.amount);
			MarketOrder returnOrder = new MarketOrder(currOrder.foodType, amountToGive);
			int oldInvAmt = inventory.get(currOrder.foodType);
			inventory.put(currOrder.foodType, oldInvAmt - amountToGive);
			print("I now have " + (oldInvAmt - amountToGive) + " " + currOrder.foodType + "s left");
			GiveCookSupplies(returnOrder);
			return true;
		}
		return false;
	}

	// ***** ACTIONS *****
	
	private void GiveCookSupplies(MarketOrder mo) {
		print("GiveCookSupplies() called: foodType = " + mo.foodType + "; amt = " + mo.amount);
		//cook.msgHereIsResupply(mo);
		// TODO: CashierMarket interaction step 2 (the first of the new interactions)
		FoodBill fb = new FoodBill(mo.foodType, this, foodBillCost);
		cashier.msgHereIsBill(fb);
	}

	// utilities
}