package restaurant_pizza;

import agent_pizza.Agent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.net.URL;
import java.util.*;

import restaurant_pizza.interfaces.Cashier;
import restaurant_pizza.interfaces.Customer;
import restaurant_pizza.interfaces.Market;
import restaurant_pizza.interfaces.Waiter;
import restaurant_pizza.test.mock.EventLog;
import restaurant_pizza.test.mock.LoggedEvent;
import simcity.RestMenu;
import simcity.PersonAgent;

public class CashierAgent extends Agent implements Cashier {
	
	// ***** DATA *****
	
	public EventLog log = new EventLog();
	
	private Map<String, Double> menu =
			Collections.synchronizedMap(new HashMap<String, Double>());
	public List<MyCustomer> myCustomers =
			Collections.synchronizedList(new ArrayList<MyCustomer>()); //customers pending their checks to be approved
	public List<Check> checks =
			Collections.synchronizedList(new ArrayList<Check>());
	
	public enum CustomerState {NewCustomer, ValidPayment, InvalidPayment};
	public enum CheckState {NewCheck, InTransit, SentToWaiter, PaidByCustomer};
	private String name;
	PersonAgent person;
	boolean shiftDone = false;

	
	// TODO: CashierMarket interaction MarketAgent added stuff
	// TODO: CashierMarket interaction what is CheckTwo? A new class?
	private List<FoodBill> billsToPay = Collections.synchronizedList(new ArrayList<FoodBill>());
	private double totalMoney = 200.0; // keeps track of the money received
	private double totalDebt = 0.0; // keeps track of the money received
	
	public CashierAgent(String name) {
		super();
		this.name = name;
		//initializeMenu();
	}
	
	@Override
	public String getName() {
		return name;
	}

	public void setPerson(PersonAgent p) {
		person = p;
	}
	
	public double getTotalMoney() {
		return totalMoney;
	}
	
	public void setTotalMoney(double aMoney) {
		totalMoney = aMoney;
	}
	
	public void subtract(double amount) {
		totalMoney -= 10;
	}
	
	public void initializeMenu() {
		try {
			URL fileURL = getClass().getResource("/res/MenuTextFile.txt");
			URI fileURI = fileURL.toURI();
			BufferedReader br = new BufferedReader(new FileReader(new File(fileURI)));
			int numItems = Integer.parseInt(br.readLine());
			StringTokenizer st;
			for(int i = 0; i < numItems; i++) {
				st = new StringTokenizer(br.readLine());
				String itemName = st.nextToken();
				double price = Double.parseDouble(st.nextToken());
				st.nextToken();
				menu.put(itemName, price);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// ***** MESSAGES *****
	
	public void msgShiftDone() {
		boolean shiftDone = true;
		if(billsToPay.size() == 0)
			shiftDone = true;
	}
	
	@Override
	public void msgCustomerNeedsCheck(Waiter w, String order, Customer customer, RestMenu menu) {
		print("calling msgCustomerNeedsCheck()");
		double amountDue = menu.menuItems.get(order);
		Check newCheck = new Check(order, w, customer, amountDue);
		if(isNewCustomer(customer))
			myCustomers.add(new MyCustomer(customer));
		checks.add(newCheck);
		stateChanged();
	}
	
	public void msgShiftDone() {
		shiftDone = true;
		if (!pickAndExecuteAnAction()) {person.msgStopWork(10);}
	}

	@Override
	public void msgPayingMyCheck(Customer c, Check check, double payment) {
		log.add(new LoggedEvent("Calling msgPayingMyCheck"));
		print("calling msgPayingMyCheck(), payment = " + payment);
		MyCustomer mc = null;
		synchronized(myCustomers) {
			for(MyCustomer tempMC : myCustomers)
				if(tempMC.cust.equals(c)) {
					mc = tempMC;
					break;
				}
		}
		check.state = CheckState.PaidByCustomer;
		// set state depending on whether customer has enough or not
		if(payment < check.amountDue) {
			print("invalid");
			mc.customerState = CustomerState.InvalidPayment;
		}
		else {
			print("valid");
			mc.customerState = CustomerState.ValidPayment;
		}
		stateChanged();
	}

	@Override
	public void msgHereIsBill(FoodBill fb) {
		log.add(new LoggedEvent("msgHereIsBill from " + fb.market.getName() + " for order " + fb.order
				+ " and amount " + fb.amountDue));
		billsToPay.add(fb);
		stateChanged();
	}
	
	// ***** SCHEDULER *****
	// changed from protected to public for JUnit testing
	@Override
	public boolean pickAndExecuteAnAction() {

		// TODO: CashierMarket interaction scheduler step
		// Priority over the others
		// If there exists a bill in here, pay it, subtract from totalMoney
		// If negative number, convert to debt and set totalMoney = 0;
		// else, with whatever totalMoney you have left, make up for the debt as well.
		// send message to market containing the amount of money paid: msgHereIsBill
		// remove bill from List
		// return true;
		if(billsToPay.size() > 0) {
			FoodBill theBill = billsToPay.remove(0);
			double amountToPay = theBill.amountDue;
			boolean billIsPaid = true;
			if(totalMoney >= amountToPay) {
				totalMoney -= theBill.amountDue;
				if(totalDebt > 0) { 
					// cashier will make up as much previous debt as possible
					amountToPay += Math.min(totalMoney, totalDebt);
					totalMoney -= amountToPay;
					totalDebt -= amountToPay;
					print("Repaying debts; total debt is now " + totalDebt);
				}
			}
			else {
				amountToPay = totalMoney;
				totalDebt += (theBill.amountDue - amountToPay);
				totalMoney = 0;
				billIsPaid = false;
			}
			print("Paying " + amountToPay + " in total");
			if(billIsPaid)
				print("This bill is fully paid off!");
			else {
				print("This bill is not fully paid off - will pay " + (theBill.amountDue - amountToPay) + " next time."); 
				print("Total debt is now " + totalDebt);
			}
			Market theMarket = theBill.market;
			theMarket.msgHereIsBill(amountToPay, billIsPaid);
			return true;
		}
		
		print("pickAndExecute called");
		if(checks.isEmpty()) {
			print("no checks");
			return false;
		}
		
		// loop through all waiting customers and tables
		synchronized (checks) {
		
			for(int indCheck = 0; indCheck < checks.size(); indCheck++) {
				Check aCheck = checks.get(indCheck);
				if(aCheck.state == CheckState.NewCheck) {
					deliverCheck(aCheck.waiter, aCheck);
					aCheck.state = CheckState.InTransit;
					return true;
				}
				if(aCheck.state == CheckState.PaidByCustomer) {
					Customer customerOnCheck = aCheck.customer;
					synchronized (myCustomers) {
						for(int i = 0; i < myCustomers.size(); i++) {
							MyCustomer mc = myCustomers.get(i);
							if(mc.cust.equals(customerOnCheck)) {
								print("ready to inspect check");
								if(mc.customerState == CustomerState.ValidPayment)
									handleValidPayment(aCheck);
								else
									handleInvalidPayment(aCheck);
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	public void deliverCheck(Waiter freeWaiter, Check aCheck) {
		aCheck.waiter.msgHereIsCheck(aCheck);
	}
	
	public void handleValidPayment(Check aCheck) {
		print("handle valid");
		log.add(new LoggedEvent("handleValidPayment called"));
		totalMoney += aCheck.amountDue;
		Customer theCustomer = aCheck.customer;
		print("Received " + aCheck.amountDue + " from Customer");
		print("Total money in the register: " + totalMoney);
		checks.remove(aCheck);
		theCustomer.msgLeaveRestaurant();
	}
	
	public void handleInvalidPayment(Check aCheck) {
		print("handle invalid");
		log.add(new LoggedEvent("handleInvalidPayment called"));
		Customer customerOnCheck = aCheck.customer;
		synchronized (myCustomers) {
			for(int i = 0; i < myCustomers.size(); i++) {
				MyCustomer mc = myCustomers.get(i);
				if(mc.cust.equals(customerOnCheck)) {
					mc.debt += aCheck.amountDue;
					print("Next time you come, pay up this debt: " + mc.debt);
					log.add(new LoggedEvent("Customer has this much debt to repay: " + mc.debt));
				}
			}
		}	
		checks.remove(aCheck);
		Customer theCustomer = aCheck.customer;
		theCustomer.msgLeaveRestaurant();
	}
	
	//utilities

	private boolean isNewCustomer(Customer c) {
		synchronized (myCustomers) {
			for(MyCustomer tempMC : myCustomers) {
				if(tempMC.cust.equals(c)) {
					return false;
				}
			}
		}
		return true;
	}
	
	private class MyCustomer {
		Customer cust;
		CustomerState customerState;
		double debt = 0.0;
		
		public MyCustomer(Customer aCustomerAgent) {
			cust = aCustomerAgent;
			customerState = CustomerState.NewCustomer;
		}
	}
}
