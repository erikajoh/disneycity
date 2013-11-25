package restaurant_cafe.test.mock;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import restaurant_cafe.CashierAgent.Bill;
import restaurant_cafe.gui.Check;
import restaurant_cafe.gui.Menu;
import restaurant_cafe.gui.MenuItem;
import restaurant_cafe.interfaces.Cashier;
import restaurant_cafe.interfaces.Customer;
import restaurant_cafe.interfaces.Market;
import restaurant_cafe.interfaces.Waiter;

public class MockCashier extends Mock {

	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public Cashier cashier;
	private Menu menu;
	
	public class MyCustomer {
		public MockCustomer customer;
		public String choice;
		public Check check;
		double amountPaid;
		MockWaiter waiter;
		public CustomerState state;
		public MyCustomer(MockCustomer cust, MockWaiter w, String c){
			customer = cust;
			waiter = w;
			choice = c;
			state = CustomerState.producingCheck;
		}
	}
    public enum CustomerState{idle, producingCheck, checkReady, giveCheck, paying, makeChange, giveChange, noMoney};
	
	public List<MyCustomer> customers = new ArrayList<MyCustomer>();
	Bill clearBill = null;

	public class Bill {
		Market market;
		double total;
		boolean paid;
		
		public Bill(Market m, double t){
			market = m;
			total = t;
			paid = false;
		}
	}
	public List<Bill> bills = new ArrayList<Bill>();

	public MockCashier(String name, Menu m) {
		super(name);
		menu = m;
	}
	
	
	public void msgBillFromMarket(MockMarket market, double total){
		bills.add(new Bill(market, total));
	}
	
	public void msgProduceCheck(Customer c, Waiter w, String choice){
		
	}
	
	public void msgProduceCheck(MockCustomer c, MockWaiter w, String choice){
		MyCustomer mc = new MyCustomer(c, w, choice);
		customers.add(mc);
	}
	
	public void msgClearBill(Market market){
		synchronized(bills){
		for(Bill bill : bills){
			if(bill.market == market){
				clearBill = bill; break;
			}
		 }
		}
	}
	
	public void msgCheckReady(MyCustomer customer){
		
	}
	
	public void msgWaiterHere(Customer customer){
		
	}
	
	public void msgWaiterHere(MockCustomer customer){
		MyCustomer mc = null;
		for(MyCustomer cust : customers){
			if(cust.customer == customer){
				mc = cust;
				break;
			}
		}
		mc.state = CustomerState.giveCheck;
	}
	
	public void msgHereIsPayment(MockCustomer cust, double cash){
		MyCustomer customer = null;
		for(MyCustomer mc : customers){
			if(mc.customer == cust){
				customer = mc;
				break;
			}
		}
		customer.state = CustomerState.makeChange;
		customer.amountPaid = cash;
	}
	
	public void msgNoMoney(Customer c){
		
	}
	
	public boolean pickAndExecuteAnAction(){
		for(MyCustomer mc : customers){
			if(mc.state == CustomerState.producingCheck){
				produceCheck(mc);
				return true;
			}
		}
		for(MyCustomer mc : customers){
			if(mc.state == CustomerState.giveCheck){
			    giveCheckToWaiter(mc);
				return true;
			}
		}
		for(MyCustomer mc : customers){
			if(mc.state == CustomerState.makeChange){
			    makeChange(mc);
				return true;
			}
		}
		boolean billPaid = false;
		  for(Bill bill : bills){
			  if(bill.paid == false) {
			    bill.market.msgPaidBill(bill.total, true);
			    bill.paid = true;
			    billPaid = true;
		      }
		  }
		  if(billPaid == true){
			  return true;
		  }
		  if(clearBill != null){
				clearBill();
				return true;
			}
		  
		return false;
	}
	
	private void produceCheck(final MyCustomer customer){
		double price = 0;
		for(MenuItem item : menu.getItems()){
			if(item.getName().equals(customer.choice)){
				price = Math.round(item.getPrice() * 100.0) / 100.0;
				break;
			}
	    }
		
		customer.state = CustomerState.idle;
		customer.check = new Check(customer.choice, price);
		
		//normally a timer here		
		msgCheckReady(customer);
	}
	
	private void giveCheckToWaiter(MyCustomer customer){
		customer.waiter.msgHereIsCheck(customer.customer, customer.check);
		customer.state = CustomerState.idle;
	}
	
	private void makeChange(final MyCustomer customer){
		customer.state = CustomerState.idle;
		final double change = Math.round((customer.amountPaid - customer.check.getTotal())*100.0)/100.0;
		customer.customer.msgHereIsChange(change); //normally after a timer
	}
	
	private void clearBill(){
		bills.remove(clearBill);
		clearBill = null;
	}
}
