package restaurant.test;

import java.util.ArrayList;
import java.util.Collection;

import restaurant.TellerAgent;
import restaurant.MarketAgent;
import restaurant.MarketAgent.Order;
//import restaurant.CashierAgent.cashierBillState;
//import restaurant.WaiterAgent.Bill;
import restaurant.gui.Food;
import restaurant.gui.Menu;
import restaurant.interfaces.Cook;
import restaurant.interfaces.Person;
import restaurant.interfaces.Waiter;
import restaurant.test.mock.MockTeller;
import restaurant.test.mock.MockCook;
import restaurant.test.mock.MockPerson;
import restaurant.test.mock.MockMarket;
import restaurant.test.mock.MockWaiter;
import junit.framework.*;

/**
 * 
 * This class is a JUnit test class to unit test the CashierAgent's basic interaction
 * with waiters, customers, and the host.
 * It is provided as an example to students in CS201 for their unit testing lab.
 *
 * @author Monroe Ekilah
 */
public class TellerTest extends TestCase
{
	//these are instantiated for each test separately via the setUp() method.
	//CashierAgent cashier;
	MockTeller cashier;
	MockWaiter waiter0;
	MockWaiter waiter1;
	MockPerson customer0;
	MockPerson customer1;
	MockPerson customer2;
	MockCook cook;
	public Collection<Food> foods = new ArrayList<Food>();
	public Collection<MockMarket> markets = new ArrayList<MockMarket>();
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();	
		
		 Food food = new Food("Steak", 3000, 5, 4, 8, 15.99);
	        foods.add(food);
	        food = new Food("Chicken", 2500, 5, 4, 8, 10.99);
	        foods.add(food);
	        food = new Food("Salad", 2000, 5, 4, 8, 5.99);
	        foods.add(food);
	        food = new Food("Pizza", 3500, 5, 4, 8, 8.99);
	        foods.add(food);
	        
	        Menu menu = new Menu(foods);
	        cashier = new MockTeller("cashier", menu);		
	        customer0 = new MockPerson("mockcustomer0");	
	        customer1 = new MockPerson("mockcustomer1");		
	        customer2 = new MockPerson("mockcustomer2");		
	        waiter0 = new MockWaiter("mockwaiter0", menu);
	        waiter1 = new MockWaiter("mockwaiter1", menu);
	        cook = new MockCook("mockCook");
	   	 for(int i = 0; i<3; i++){
	            MockMarket market = new MockMarket(i, menu, 5);
	            market.setCashier(cashier);
	            markets.add(market);
	     }
	}	
	/**
	 * This tests the cashier under very simple terms: paying one market
	 */
	public void testOneMarketScenario(){
		for(MockMarket market : markets){
           market.setAllStockToAmt(4);
		}
		MockMarket market = null;
		Food food = null;
		for(MockMarket m : markets){
			market = m; break; //get first MockMarket
		}
		for(Food f : foods){
			food = f; break; //get first Food
		}
		
		market.msgHereIsOrder(cook, food, 4);
		
		//check preconditions
		assertEquals("Make sure cashier doesn't have any bills", cashier.bills.size(), 0);		
		assertTrue("Market's scheduler should have returned true (as it is biling the cashier)", market.pickAndExecuteAnAction());
		assertFalse("Market's scheduler should have returned false (as it has already billed the cashier and is idle)", market.pickAndExecuteAnAction());

		//test
		assertEquals("Make sure cashier has a bill", cashier.bills.size(), 1);		
		assertTrue("Cashier's scheduler should have returned true (as it is paying the markets' bill(s))", cashier.pickAndExecuteAnAction());
		assertFalse("Cashier's scheduler should have returned true (as it has already paid the markets' bill(s) and is idle)", cashier.pickAndExecuteAnAction());
		//market hasn't cleared the cashier yet
		assertEquals("Make sure cashier removes bill since it is paid", cashier.bills.size(), 1);		
		
		assertTrue("Market is now making sure the cashier paid enough", market.pickAndExecuteAnAction());
		assertFalse("Market doesn't have anything to do", market.pickAndExecuteAnAction());
		assertTrue("Cashier is removing the bill now", cashier.pickAndExecuteAnAction());
		assertFalse("Cashier doesn't have any bills left to remove", cashier.pickAndExecuteAnAction());
		
		//bill is officially cleared
		assertEquals("Make sure cashier removes bill since it is paid", cashier.bills.size(), 0);		
	}
	/**
	 * This tests the cashier under very simple terms: paying one market
	 */
	public void testMultipleMarketsScenario(){
		for(MockMarket market : markets){
           market.setAllStockToAmt(2);
		}
		MockMarket market0 = null, market1 = null;
		Food food = null;
		int count = 0;
		for(MockMarket m : markets){
			if(count == 0){
			 market0 = m; //get first market
			}
			else if(count == 1){
			  market1 = m; //get second market
			}
			else {
				break;
			}
			count++;
		}
		
		for(Food f : foods){
			food = f; break; //get first Food
		}
		
		market0.msgHereIsOrder(cook, food, 4);
		market1.msgHereIsOrder(cook, food, 3);
		
		//check preconditions
		assertEquals("Make sure cashier doesn't have any bills", cashier.bills.size(), 0);		
		assertTrue("Market0's scheduler should have returned true (as it is biling the cashier)", market0.pickAndExecuteAnAction());
		assertFalse("Market0's scheduler should have returned false (as it has already billed the cashier and is idle)", market0.pickAndExecuteAnAction());

		assertTrue("Market1's scheduler should have returned true (as it is biling the cashier)", market1.pickAndExecuteAnAction());
		assertFalse("Market1's scheduler should have returned false (as it has already billed the cashier and is idle)", market1.pickAndExecuteAnAction());
		
		//test
		assertEquals("Make sure cashier has a bill", cashier.bills.size(), 2);		
		assertTrue("Cashier's scheduler should have returned true (as it is paying the markets' bill(s))", cashier.pickAndExecuteAnAction());
		assertFalse("Cashier's scheduler should have returned true (as it has already paid the markets' bill(s) and is idle)", cashier.pickAndExecuteAnAction());
		
		//market hasn't cleared the cashier yet
		assertEquals("Make sure cashier removes bill since it is paid", cashier.bills.size(), 2);
		
		assertTrue("Market is now making sure the cashier paid enough", market1.pickAndExecuteAnAction());
		assertTrue("Cashier is removing the bill now", cashier.pickAndExecuteAnAction());
		assertFalse("Cashier doesn't have any bills left to remove", cashier.pickAndExecuteAnAction());
		
		//one bill is officially cleared
		assertEquals("Make sure cashier removes bill since it is paid", cashier.bills.size(), 1);		
		
		//clearing second bill
		assertTrue("Market is now making sure the cashier paid enough", market0.pickAndExecuteAnAction());
		assertFalse("Market doesn't have anything to do", market0.pickAndExecuteAnAction());
		assertTrue("Cashier is removing the bill now", cashier.pickAndExecuteAnAction());
		assertFalse("Cashier doesn't have any bills left to remove", cashier.pickAndExecuteAnAction());
		
		//second bill is officially cleared
		assertEquals("Make sure cashier removes bill since it is paid", cashier.bills.size(), 0);		
	}
	/**
	 * This tests the cashier under very simple terms: one customer is ready and able to pay the bill.
	 */
	public void testOneNormalCustomerScenario()
	{
		//setUp() runs first before this test!
		
		customer0.setCashier(cashier);//You can do almost anything in a unit test.	
		customer0.setBalance(25.00);
		
		//check waiter preconditions
		assertEquals("Make sure waiter doesn't have any customers", waiter0.customers.size(), 0);		
		waiter0.msgPleaseSeatCustomer(customer0, 0);
		assertEquals("Make sure waiter now has a customer", waiter0.customers.size(), 1);		
		
		//check preconditions
		assertEquals("Cashier should have 0 bills in it. It doesn't.", cashier.customers.size(), 0);		
		assertEquals("CashierAgent should have an empty event log before the Cashier's HereIsBill is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());
	
		//step 1 of the test (cashier producing the check)
		
		cashier.msgProduceCheck(customer0, waiter0, "Steak");
		
		//check postconditions for step 1 and preconditions for step 2
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ waiter0.log.toString(), 0, waiter0.log.size());
		
		assertEquals("Cashier should have 1 bill in it. It doesn't.", cashier.customers.size(), 1);
				
		assertTrue("Cashier's scheduler should have returned true (as the check is being produced)", cashier.pickAndExecuteAnAction());
		
		assertEquals(
				"MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
						+ waiter0.log.toString(), 0, waiter0.log.size());
		
		assertEquals(
				"MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
						+ waiter0.log.toString(), 0, waiter0.log.size());
		
		assertFalse("Cashier's scheduler should have returned false (since the check has been produced and nothing else has to happen)", cashier.pickAndExecuteAnAction());

		
		//step 2 of the test (waiter arrives at cashier)
		cashier.msgWaiterHere(customer0);
		assertTrue("Cashier's scheduler should have returned true (as the waiter is here)", cashier.pickAndExecuteAnAction());
		assertFalse("Cashier's scheduler should have returned false (as the waiter left and cashier doesn't currently need to do anything else)", cashier.pickAndExecuteAnAction());
		
		assertTrue("Customer shouldn't have check yet", customer0.check == null);
		assertTrue("Waiter's scheduler should have returned true (since it is giving the customer the check)", waiter0.pickAndExecuteAnAction());
		assertFalse("Waiter's scheduler should have returned false (since it is giving the customer the check)", waiter0.pickAndExecuteAnAction());
		
		assertFalse("Customer should have check", customer0.check == null);
		
		//step 3 of test (customer pays and receives change)
		//check preconditions for step 3
		
		assertTrue("Checks should have equal value", customer0.check.getTotal() == cashier.customers.get(0).check.getTotal());
		assertTrue("Check should be 15.99 as customer only ordered steak", customer0.check.getTotal() == 15.99);
		assertTrue("CashierBill should contain a bill with the right customer in it. It doesn't.", 
				cashier.customers.get(0).customer == customer0);
		
		//step 3
		customer0.msgAnimationFinishedGoToCashier();
		assertTrue("Customer's scheduler should have returned true (since customer is paying cashier)", customer0.pickAndExecuteAnAction());
		assertFalse("Waiter's scheduler should have returned false (since customer has paid cashier and is waiting for change)", customer0.pickAndExecuteAnAction());
		assertTrue("Customer should have 5.81, cost of balance minus what he paid, but hasn't received change yet", customer0.getBalance() == 5.81);
		
		assertTrue("Cashier's scheduler should have returned true (since cashier is getting change)", cashier.pickAndExecuteAnAction());
		assertFalse("Cashier's scheduler should have returned false (since cashier has already given change)", cashier.pickAndExecuteAnAction());

		assertTrue("Customer should have received change, so his balance should be more than 5.81", customer0.getBalance() > 5.81);
		
	}//end one normal customer scenario
	
	/**
	 * This tests the cashier under very simple terms: multiple customers (3) are ready to pay the exact bills.
	 */
	public void testOneMultipleCustomerScenario()
	{
		//setUp() runs first before this test!
		
		customer0.setCashier(cashier);//You can do almost anything in a unit test.	
		customer1.setCashier(cashier);//You can do almost anything in a unit test.	
		customer2.setCashier(cashier);//You can do almost anything in a unit test.	

		customer0.setBalance(25.00);
		customer1.setBalance(19.00);
		customer2.setBalance(18.00);

		
		//check waiter preconditions
		assertEquals("Make sure waiter0 doesn't have any customers", waiter0.customers.size(), 0);	
		assertEquals("Make sure waiter1 doesn't have any customers", waiter1.customers.size(), 0);		
		waiter0.msgPleaseSeatCustomer(customer0, 0);
		waiter0.msgPleaseSeatCustomer(customer1, 1);
		waiter1.msgPleaseSeatCustomer(customer2, 2);

		assertEquals("Make sure waiter0 now has 2 customers", waiter0.customers.size(), 2);		
		assertEquals("Make sure waiter1 now has 1 customer", waiter1.customers.size(), 1);		
		
		//check preconditions
		assertEquals("Cashier should have 0 bills in it. It doesn't.", cashier.customers.size(), 0);		
		assertEquals("CashierAgent should have an empty event log before the Cashier's HereIsBill is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());
	
		//step 1 of the test (cashier producing the check)
		
		cashier.msgProduceCheck(customer0, waiter0, "Steak");
		cashier.msgProduceCheck(customer1, waiter0, "Chicken");
		cashier.msgProduceCheck(customer2, waiter1, "Pizza");

		
		//check postconditions for step 1 and preconditions for step 2
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ waiter0.log.toString(), 0, waiter0.log.size());
		
		assertEquals("Cashier should have 3 bills in it. It doesn't.", cashier.customers.size(), 3);
				
		assertTrue("Cashier's scheduler should have returned true (as the check is being produced)", cashier.pickAndExecuteAnAction());
		
		assertEquals(
				"MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
						+ waiter0.log.toString(), 0, waiter0.log.size());
		
		assertEquals(
				"MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
						+ waiter0.log.toString(), 0, waiter0.log.size());
		
		assertTrue("Cashier's scheduler should have returned true (as the second check is being produced)", cashier.pickAndExecuteAnAction());
		assertTrue("Cashier's scheduler should have returned true (as the third and final check is being produced)", cashier.pickAndExecuteAnAction());
		assertFalse("Cashier's scheduler should have returned false (since the check has been produced and nothing else has to happen)", cashier.pickAndExecuteAnAction());

		
		//step 2 of the test (waiter arrives at cashier)
		cashier.msgWaiterHere(customer0);
		assertTrue("Cashier's scheduler should have returned true (as the waiter is here for the first cust)", cashier.pickAndExecuteAnAction());
		cashier.msgWaiterHere(customer1);
		assertTrue("Cashier's scheduler should have returned true (as the waiter is here for the second cust)", cashier.pickAndExecuteAnAction());
		assertFalse("Cashier's scheduler should have returned false (as the waiter left and cashier doesn't currently need to do anything else)", cashier.pickAndExecuteAnAction());
		
		assertTrue("Customers shouldn't have checks yet", customer0.check == null && customer1.check == null);
		assertTrue("Waiter's scheduler should have returned true (since it is giving the first customer the check)", waiter0.pickAndExecuteAnAction());
		assertTrue("Waiter's scheduler should have returned true (since it is giving the second customer the check)", waiter0.pickAndExecuteAnAction());
		assertFalse("Waiter's scheduler should have returned false (since it is giving the customer the check)", waiter0.pickAndExecuteAnAction());
		
		assertFalse("First customers should have checks", customer0.check == null && customer1.check == null);
		
		//step 3 of test (customer pays and receives change)
		//check preconditions for step 3
		
		assertTrue("Checks should have equal value for customer0", customer0.check.getTotal() == cashier.customers.get(0).check.getTotal());
		assertTrue("Check should be 15.99 as customer0 only ordered steak", customer0.check.getTotal() == 15.99);
		assertTrue("CashierBill should contain a bill with the right customer (0) in it. It doesn't.", 
				cashier.customers.get(0).customer == customer0);
		
		assertTrue("Checks should have equal value for customer1", customer1.check.getTotal() == cashier.customers.get(1).check.getTotal());
		assertTrue("Check should be 10.99 as customer1 only ordered steak", customer1.check.getTotal() == 10.99);
		assertTrue("CashierBill should contain a bill with the right customer (1) in it. It doesn't.", 
				cashier.customers.get(1).customer == customer1);
		
		//step 3 (customer1 will arrive before customer0)
		customer1.msgAnimationFinishedGoToCashier();
		assertTrue("Customer's scheduler should have returned true (since customer1 is paying cashier)", customer1.pickAndExecuteAnAction());
		assertFalse("Waiter's scheduler should have returned false (since customer1 has paid cashier and is waiting for change)", customer1.pickAndExecuteAnAction());
		assertTrue("Customer should have 5.81, cost of balance minus what he paid, but hasn't received change yet", customer1.getBalance() == 5.81);
		
		assertTrue("Cashier's scheduler should have returned true (since cashier is getting change)", cashier.pickAndExecuteAnAction());
		assertTrue("Customer should have received change, so his balance should be 8.01", customer1.getBalance() == 8.01);
		
		//now customer2 will arrive (out of order)
		cashier.msgWaiterHere(customer2);
		assertTrue("Cashier's scheduler should have returned true (as the waiter is here for the second cust)", cashier.pickAndExecuteAnAction());
		assertFalse("Cashier's scheduler should have returned false (as the waiter left and cashier doesn't currently need to do anything else)", cashier.pickAndExecuteAnAction());
		
		assertTrue("Customers shouldn't have checks yet", customer2.check == null);
		assertTrue("Waiter's scheduler should have returned true (since it is giving the third customer the check)", waiter1.pickAndExecuteAnAction());
		assertFalse("Waiter's scheduler should have returned false (since it is giving the customer the check)", waiter1.pickAndExecuteAnAction());
		
		assertFalse("First customers should have checks", customer2.check == null);
		
		//now customer 2 will get check and pay (out of order)
		assertTrue("Checks should have equal value for customer2", customer2.check.getTotal() == cashier.customers.get(2).check.getTotal());
		assertTrue("Check should be 8.99 as customer0 only ordered steak", customer2.check.getTotal() == 8.99);
		assertTrue("CashierBill should contain a bill with the right customer (2) in it. It doesn't.", 
				cashier.customers.get(2).customer == customer2);
		
		customer2.msgAnimationFinishedGoToCashier();
		assertTrue("Customer2's scheduler should have returned true (since customer2 is paying cashier)", customer2.pickAndExecuteAnAction());
		assertFalse("Waiter1's scheduler should have returned false (since customer2 has paid cashier and is waiting for change)", customer2.pickAndExecuteAnAction());
		assertTrue("Customer2 should have 7.21, cost of balance minus what he paid, but hasn't received change yet", customer2.getBalance() == 7.21);
		
		assertTrue("Cashier's scheduler should have returned true (since cashier is getting change)", cashier.pickAndExecuteAnAction());
		assertTrue("Customer2 should have received change, so his balance should be 9.01", customer2.getBalance() == 9.01);
		
		//finally customer 0 will pay
		customer0.msgAnimationFinishedGoToCashier();
		assertTrue("Customer0's scheduler should have returned true (since customer is paying cashier)", customer0.pickAndExecuteAnAction());
		assertFalse("Waiter0's scheduler should have returned false (since customer has paid cashier and is waiting for change)", customer0.pickAndExecuteAnAction());
		assertTrue("Customer0 should have 5.81, cost of balance minus what he paid, but hasn't received change yet", customer0.getBalance() == 5.81);
		
		assertTrue("Cashier's scheduler should have returned true (since cashier is getting change)", cashier.pickAndExecuteAnAction());
		assertFalse("Cashier's scheduler should have returned false (since cashier has already given change)", cashier.pickAndExecuteAnAction());

		assertTrue("Customer0 should have received change, so his balance should be more than 5.81", customer0.getBalance() > 5.81);
		
	}//end multiple normal customer scenario
	
	/**
	 * This tests the cashier with having to pay two separate bills to the same market while also doing normal interactions with the customers
	 */
	public void testOneMarketMultipleCustomerScenario()
	{
		//setUp() runs first before this test!
		
		customer0.setCashier(cashier);//You can do almost anything in a unit test.	
		customer1.setCashier(cashier);//You can do almost anything in a unit test.	
		customer2.setCashier(cashier);//You can do almost anything in a unit test.	

		customer0.setBalance(25.00);
		customer1.setBalance(19.00);
		customer2.setBalance(18.00);

		
		//check waiter preconditions
		assertEquals("Make sure waiter0 doesn't have any customers", waiter0.customers.size(), 0);	
		assertEquals("Make sure waiter1 doesn't have any customers", waiter1.customers.size(), 0);		
		waiter0.msgPleaseSeatCustomer(customer0, 0);
		waiter0.msgPleaseSeatCustomer(customer1, 1);
		waiter1.msgPleaseSeatCustomer(customer2, 2);

		assertEquals("Make sure waiter0 now has 2 customers", waiter0.customers.size(), 2);		
		assertEquals("Make sure waiter1 now has 1 customer", waiter1.customers.size(), 1);		
		
		//check preconditions
		assertEquals("Cashier should have 0 bills in it. It doesn't.", cashier.customers.size(), 0);		
		assertEquals("CashierAgent should have an empty event log before the Cashier's HereIsBill is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());
	
		//step 1 of the test (cashier producing the check)
		
		cashier.msgProduceCheck(customer0, waiter0, "Steak");
		cashier.msgProduceCheck(customer1, waiter0, "Chicken");
		cashier.msgProduceCheck(customer2, waiter1, "Pizza");

		
		//check postconditions for step 1 and preconditions for step 2
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ waiter0.log.toString(), 0, waiter0.log.size());
		
		assertEquals("Cashier should have 3 bills in it. It doesn't.", cashier.customers.size(), 3);
				
		assertTrue("Cashier's scheduler should have returned true (as the check is being produced)", cashier.pickAndExecuteAnAction());
		
		assertEquals(
				"MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
						+ waiter0.log.toString(), 0, waiter0.log.size());
		
		assertEquals(
				"MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
						+ waiter0.log.toString(), 0, waiter0.log.size());
		
		assertTrue("Cashier's scheduler should have returned true (as the second check is being produced)", cashier.pickAndExecuteAnAction());
		assertTrue("Cashier's scheduler should have returned true (as the third and final check is being produced)", cashier.pickAndExecuteAnAction());
		assertFalse("Cashier's scheduler should have returned false (since the check has been produced and nothing else has to happen)", cashier.pickAndExecuteAnAction());

		
		//step 2 of the test (waiter arrives at cashier)
		cashier.msgWaiterHere(customer0);
		assertTrue("Cashier's scheduler should have returned true (as the waiter is here for the first cust)", cashier.pickAndExecuteAnAction());
		cashier.msgWaiterHere(customer1);
		assertTrue("Cashier's scheduler should have returned true (as the waiter is here for the second cust)", cashier.pickAndExecuteAnAction());
		assertFalse("Cashier's scheduler should have returned false (as the waiter left and cashier doesn't currently need to do anything else)", cashier.pickAndExecuteAnAction());
		
		assertTrue("Customers shouldn't have checks yet", customer0.check == null && customer1.check == null);
		assertTrue("Waiter's scheduler should have returned true (since it is giving the first customer the check)", waiter0.pickAndExecuteAnAction());
		assertTrue("Waiter's scheduler should have returned true (since it is giving the second customer the check)", waiter0.pickAndExecuteAnAction());
		assertFalse("Waiter's scheduler should have returned false (since it is giving the customer the check)", waiter0.pickAndExecuteAnAction());
		
		assertFalse("First customers should have checks", customer0.check == null && customer1.check == null);
		
		//market code...at approx the point the waiter gets the check, the market is billing the cashier
		for(MockMarket market : markets){
	         market.setAllStockToAmt(4);
		}
		MockMarket market = null;
		Food food = null;
		for(MockMarket m : markets){
			market = m; break; //get first MockMarket
		}
		String choice = cashier.customers.get(0).choice;
		for(Food f : foods){
			if(choice.equals(f.getName())){
				food = f; break; //get first Food
			}
		}
			
		market.msgHereIsOrder(cook, food, 4);
			
		//check preconditions
		assertEquals("Make sure cashier doesn't have any bills", cashier.bills.size(), 0);		
		assertTrue("Market's scheduler should have returned true (as it is biling the cashier)", market.pickAndExecuteAnAction());
		assertFalse("Market's scheduler should have returned false (as it has already billed the cashier and is idle)", market.pickAndExecuteAnAction());

		//test
		assertEquals("Make sure cashier has a bill", cashier.bills.size(), 1);		
		assertTrue("Cashier's scheduler should have returned true (as it is paying the markets' bill(s))", cashier.pickAndExecuteAnAction());
		assertFalse("Cashier's scheduler should have returned true (as it has already paid the markets' bill(s) and is idle)", cashier.pickAndExecuteAnAction());
		//market hasn't cleared the cashier yet
		assertEquals("Make sure cashier removes bill since it is paid", cashier.bills.size(), 1);		
			
		assertTrue("Market is now making sure the cashier paid enough", market.pickAndExecuteAnAction());
		assertFalse("Market doesn't have anything to do", market.pickAndExecuteAnAction());
		assertTrue("Cashier is removing the bill now", cashier.pickAndExecuteAnAction());
		assertFalse("Cashier doesn't have any bills left to remove", cashier.pickAndExecuteAnAction());
			
		//bill is officially cleared
		assertEquals("Make sure cashier removes bill since it is paid", cashier.bills.size(), 0);		
		
		//step 3 of test (customer pays and receives change)
		//check preconditions for step 3
		
		assertTrue("Checks should have equal value for customer0", customer0.check.getTotal() == cashier.customers.get(0).check.getTotal());
		assertTrue("Check should be 15.99 as customer0 only ordered steak", customer0.check.getTotal() == 15.99);
		assertTrue("CashierBill should contain a bill with the right customer (0) in it. It doesn't.", 
				cashier.customers.get(0).customer == customer0);
		
		assertTrue("Checks should have equal value for customer1", customer1.check.getTotal() == cashier.customers.get(1).check.getTotal());
		assertTrue("Check should be 10.99 as customer1 only ordered steak", customer1.check.getTotal() == 10.99);
		assertTrue("CashierBill should contain a bill with the right customer (1) in it. It doesn't.", 
				cashier.customers.get(1).customer == customer1);
		
		//step 3 (customer1 will arrive before customer0)
		customer1.msgAnimationFinishedGoToCashier();
		assertTrue("Customer's scheduler should have returned true (since customer1 is paying cashier)", customer1.pickAndExecuteAnAction());
		assertFalse("Waiter's scheduler should have returned false (since customer1 has paid cashier and is waiting for change)", customer1.pickAndExecuteAnAction());
		assertTrue("Customer should have 5.81, cost of balance minus what he paid, but hasn't received change yet", customer1.getBalance() == 5.81);
		
		assertTrue("Cashier's scheduler should have returned true (since cashier is getting change)", cashier.pickAndExecuteAnAction());
		assertTrue("Customer should have received change, so his balance should be 8.01", customer1.getBalance() == 8.01);
		
		//now customer2 will arrive (out of order)
		cashier.msgWaiterHere(customer2);
		assertTrue("Cashier's scheduler should have returned true (as the waiter is here for the second cust)", cashier.pickAndExecuteAnAction());
		assertFalse("Cashier's scheduler should have returned false (as the waiter left and cashier doesn't currently need to do anything else)", cashier.pickAndExecuteAnAction());
		
		assertTrue("Customers shouldn't have checks yet", customer2.check == null);
		assertTrue("Waiter's scheduler should have returned true (since it is giving the third customer the check)", waiter1.pickAndExecuteAnAction());
		assertFalse("Waiter's scheduler should have returned false (since it is giving the customer the check)", waiter1.pickAndExecuteAnAction());
		
		assertFalse("First customers should have checks", customer2.check == null);
		
		//market code...another waiter is here, so the same market bills the cashier
		
	   choice = cashier.customers.get(2).choice;
		for(Food f : foods){
			if(choice.equals(f.getName())){
				food = f; break; //get first Food
			}
		}
			
		market.msgHereIsOrder(cook, food, 4);
			
		//check preconditions
		assertEquals("Make sure cashier doesn't have any bills", cashier.bills.size(), 0);		
		assertTrue("Market's scheduler should have returned true (as it is biling the cashier)", market.pickAndExecuteAnAction());
		assertFalse("Market's scheduler should have returned false (as it has already billed the cashier and is idle)", market.pickAndExecuteAnAction());

		//test
		assertEquals("Make sure cashier has a bill", cashier.bills.size(), 1);		
		assertTrue("Cashier's scheduler should have returned true (as it is paying the markets' bill(s))", cashier.pickAndExecuteAnAction());
		assertFalse("Cashier's scheduler should have returned true (as it has already paid the markets' bill(s) and is idle)", cashier.pickAndExecuteAnAction());
		//market hasn't cleared the cashier yet
		assertEquals("Make sure cashier removes bill since it is paid", cashier.bills.size(), 1);		
			
		assertTrue("Market is now making sure the cashier paid enough", market.pickAndExecuteAnAction());
		assertFalse("Market doesn't have anything to do", market.pickAndExecuteAnAction());
		assertTrue("Cashier is removing the bill now", cashier.pickAndExecuteAnAction());
		assertFalse("Cashier doesn't have any bills left to remove", cashier.pickAndExecuteAnAction());
			
		//bill is officially cleared
		assertEquals("Make sure cashier removes bill since it is paid", cashier.bills.size(), 0);	
		
		//now customer 2 will get check and pay (out of order)
		assertTrue("Checks should have equal value for customer2", customer2.check.getTotal() == cashier.customers.get(2).check.getTotal());
		assertTrue("Check should be 8.99 as customer0 only ordered steak", customer2.check.getTotal() == 8.99);
		assertTrue("CashierBill should contain a bill with the right customer (2) in it. It doesn't.", 
				cashier.customers.get(2).customer == customer2);
		
		customer2.msgAnimationFinishedGoToCashier();
		assertTrue("Customer2's scheduler should have returned true (since customer2 is paying cashier)", customer2.pickAndExecuteAnAction());
		assertFalse("Waiter1's scheduler should have returned false (since customer2 has paid cashier and is waiting for change)", customer2.pickAndExecuteAnAction());
		assertTrue("Customer2 should have 7.21, cost of balance minus what he paid, but hasn't received change yet", customer2.getBalance() == 7.21);
		
		assertTrue("Cashier's scheduler should have returned true (since cashier is getting change)", cashier.pickAndExecuteAnAction());
		assertTrue("Customer2 should have received change, so his balance should be 9.01", customer2.getBalance() == 9.01);
		
		//finally customer 0 will pay
		customer0.msgAnimationFinishedGoToCashier();
		assertTrue("Customer0's scheduler should have returned true (since customer is paying cashier)", customer0.pickAndExecuteAnAction());
		assertFalse("Waiter0's scheduler should have returned false (since customer has paid cashier and is waiting for change)", customer0.pickAndExecuteAnAction());
		assertTrue("Customer0 should have 5.81, cost of balance minus what he paid, but hasn't received change yet", customer0.getBalance() == 5.81);
		
		assertTrue("Cashier's scheduler should have returned true (since cashier is getting change)", cashier.pickAndExecuteAnAction());
		assertFalse("Cashier's scheduler should have returned false (since cashier has already given change)", cashier.pickAndExecuteAnAction());

		assertTrue("Customer0 should have received change, so his balance should be more than 5.81", customer0.getBalance() > 5.81);
		
	}//end normal one market, multiple customers scenario
	
	/**
	 * This tests the cashier with having to pay two separate bills to the different market while also doing normal interactions with the customers
	 */
	public void testMultipleMarketsMultipleCustomerScenario()
	{
		//setUp() runs first before this test!
		
		customer0.setCashier(cashier);//You can do almost anything in a unit test.	
		customer1.setCashier(cashier);//You can do almost anything in a unit test.	
		customer2.setCashier(cashier);//You can do almost anything in a unit test.	

		customer0.setBalance(25.00);
		customer1.setBalance(19.00);
		customer2.setBalance(18.00);

		
		//check waiter preconditions
		assertEquals("Make sure waiter0 doesn't have any customers", waiter0.customers.size(), 0);	
		assertEquals("Make sure waiter1 doesn't have any customers", waiter1.customers.size(), 0);		
		waiter0.msgPleaseSeatCustomer(customer0, 0);
		waiter0.msgPleaseSeatCustomer(customer1, 1);
		waiter1.msgPleaseSeatCustomer(customer2, 2);

		assertEquals("Make sure waiter0 now has 2 customers", waiter0.customers.size(), 2);		
		assertEquals("Make sure waiter1 now has 1 customer", waiter1.customers.size(), 1);		
		
		//check preconditions
		assertEquals("Cashier should have 0 bills in it. It doesn't.", cashier.customers.size(), 0);		
		assertEquals("CashierAgent should have an empty event log before the Cashier's HereIsBill is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());
	
		//step 1 of the test (cashier producing the check)
		
		cashier.msgProduceCheck(customer0, waiter0, "Steak");
		cashier.msgProduceCheck(customer1, waiter0, "Chicken");
		cashier.msgProduceCheck(customer2, waiter1, "Pizza");

		
		//check postconditions for step 1 and preconditions for step 2
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ waiter0.log.toString(), 0, waiter0.log.size());
		
		assertEquals("Cashier should have 3 bills in it. It doesn't.", cashier.customers.size(), 3);
				
		assertTrue("Cashier's scheduler should have returned true (as the check is being produced)", cashier.pickAndExecuteAnAction());
		
		assertEquals(
				"MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
						+ waiter0.log.toString(), 0, waiter0.log.size());
		
		assertEquals(
				"MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
						+ waiter0.log.toString(), 0, waiter0.log.size());
		
		assertTrue("Cashier's scheduler should have returned true (as the second check is being produced)", cashier.pickAndExecuteAnAction());
		assertTrue("Cashier's scheduler should have returned true (as the third and final check is being produced)", cashier.pickAndExecuteAnAction());
		assertFalse("Cashier's scheduler should have returned false (since the check has been produced and nothing else has to happen)", cashier.pickAndExecuteAnAction());

		
		//step 2 of the test (waiter arrives at cashier)
		cashier.msgWaiterHere(customer0);
		assertTrue("Cashier's scheduler should have returned true (as the waiter is here for the first cust)", cashier.pickAndExecuteAnAction());
		cashier.msgWaiterHere(customer1);
		assertTrue("Cashier's scheduler should have returned true (as the waiter is here for the second cust)", cashier.pickAndExecuteAnAction());
		assertFalse("Cashier's scheduler should have returned false (as the waiter left and cashier doesn't currently need to do anything else)", cashier.pickAndExecuteAnAction());
		
		assertTrue("Customers shouldn't have checks yet", customer0.check == null && customer1.check == null);
		assertTrue("Waiter's scheduler should have returned true (since it is giving the first customer the check)", waiter0.pickAndExecuteAnAction());
		assertTrue("Waiter's scheduler should have returned true (since it is giving the second customer the check)", waiter0.pickAndExecuteAnAction());
		assertFalse("Waiter's scheduler should have returned false (since it is giving the customer the check)", waiter0.pickAndExecuteAnAction());
		
		assertFalse("First customers should have checks", customer0.check == null && customer1.check == null);
		
		//market code...at approx the point the waiter gets the check, the market is billing the cashier
		for(MockMarket market : markets){
	           market.setAllStockToAmt(2);
			}
		MockMarket market0 = null, market1 = null;
		Food food = null;
		int count = 0;
		for(MockMarket m : markets){
			if(count == 0){
			  market0 = m; //get first market
			}
			else if(count == 1){
			  market1 = m; //get second market
			}
			else {
				break;
			}
			count++;
		}
		String choice = cashier.customers.get(0).choice;	
		for(Food f : foods){
			if(choice.equals(f.getName())){
				food = f; break; //get first Food
			}
		}
		
			
		market0.msgHereIsOrder(cook, food, 4);
		market1.msgHereIsOrder(cook, food, 3);
			
		//check preconditions
		assertEquals("Make sure cashier doesn't have any bills", cashier.bills.size(), 0);		
		assertTrue("Market0's scheduler should have returned true (as it is biling the cashier)", market0.pickAndExecuteAnAction());
		assertFalse("Market0's scheduler should have returned false (as it has already billed the cashier and is idle)", market0.pickAndExecuteAnAction());

		assertTrue("Market1's scheduler should have returned true (as it is biling the cashier)", market1.pickAndExecuteAnAction());
		assertFalse("Market1's scheduler should have returned false (as it has already billed the cashier and is idle)", market1.pickAndExecuteAnAction());
			
		//test
		assertEquals("Make sure cashier has a bill", cashier.bills.size(), 2);		
		assertTrue("Cashier's scheduler should have returned true (as it is paying the markets' bill(s))", cashier.pickAndExecuteAnAction());
		assertFalse("Cashier's scheduler should have returned true (as it has already paid the markets' bill(s) and is idle)", cashier.pickAndExecuteAnAction());
			
		//market hasn't cleared the cashier yet
		assertEquals("Make sure cashier removes bill since it is paid", cashier.bills.size(), 2);
			
		assertTrue("Market is now making sure the cashier paid enough", market1.pickAndExecuteAnAction());
		assertTrue("Cashier is removing the bill now", cashier.pickAndExecuteAnAction());
		assertFalse("Cashier doesn't have any bills left to remove", cashier.pickAndExecuteAnAction());
			
		//one bill is officially cleared
		assertEquals("Make sure cashier removes bill since it is paid", cashier.bills.size(), 1);		
			
		//clearing second bill
		assertTrue("Market is now making sure the cashier paid enough", market0.pickAndExecuteAnAction());
		assertFalse("Market doesn't have anything to do", market0.pickAndExecuteAnAction());
		assertTrue("Cashier is removing the bill now", cashier.pickAndExecuteAnAction());
		assertFalse("Cashier doesn't have any bills left to remove", cashier.pickAndExecuteAnAction());
			
		//second bill is officially cleared
		assertEquals("Make sure cashier removes bill since it is paid", cashier.bills.size(), 0);	
		
		//step 3 of test (customer pays and receives change)
		//check preconditions for step 3
		
		assertTrue("Checks should have equal value for customer0", customer0.check.getTotal() == cashier.customers.get(0).check.getTotal());
		assertTrue("Check should be 15.99 as customer0 only ordered steak", customer0.check.getTotal() == 15.99);
		assertTrue("CashierBill should contain a bill with the right customer (0) in it. It doesn't.", 
				cashier.customers.get(0).customer == customer0);
		
		assertTrue("Checks should have equal value for customer1", customer1.check.getTotal() == cashier.customers.get(1).check.getTotal());
		assertTrue("Check should be 10.99 as customer1 only ordered steak", customer1.check.getTotal() == 10.99);
		assertTrue("CashierBill should contain a bill with the right customer (1) in it. It doesn't.", 
				cashier.customers.get(1).customer == customer1);
		
		//step 3 (customer1 will arrive before customer0)
		customer1.msgAnimationFinishedGoToCashier();
		assertTrue("Customer's scheduler should have returned true (since customer1 is paying cashier)", customer1.pickAndExecuteAnAction());
		assertFalse("Waiter's scheduler should have returned false (since customer1 has paid cashier and is waiting for change)", customer1.pickAndExecuteAnAction());
		assertTrue("Customer should have 5.81, cost of balance minus what he paid, but hasn't received change yet", customer1.getBalance() == 5.81);
		
		assertTrue("Cashier's scheduler should have returned true (since cashier is getting change)", cashier.pickAndExecuteAnAction());
		assertTrue("Customer should have received change, so his balance should be 8.01", customer1.getBalance() == 8.01);
		
		//now customer2 will arrive (out of order)
		cashier.msgWaiterHere(customer2);
		assertTrue("Cashier's scheduler should have returned true (as the waiter is here for the second cust)", cashier.pickAndExecuteAnAction());
		assertFalse("Cashier's scheduler should have returned false (as the waiter left and cashier doesn't currently need to do anything else)", cashier.pickAndExecuteAnAction());
		
		assertTrue("Customers shouldn't have checks yet", customer2.check == null);
		assertTrue("Waiter's scheduler should have returned true (since it is giving the third customer the check)", waiter1.pickAndExecuteAnAction());
		assertFalse("Waiter's scheduler should have returned false (since it is giving the customer the check)", waiter1.pickAndExecuteAnAction());
		
		assertFalse("First customers should have checks", customer2.check == null);
		
		//now customer 2 will get check and pay (out of order)
		assertTrue("Checks should have equal value for customer2", customer2.check.getTotal() == cashier.customers.get(2).check.getTotal());
		assertTrue("Check should be 8.99 as customer0 only ordered steak", customer2.check.getTotal() == 8.99);
		assertTrue("CashierBill should contain a bill with the right customer (2) in it. It doesn't.", 
				cashier.customers.get(2).customer == customer2);
		
		customer2.msgAnimationFinishedGoToCashier();
		assertTrue("Customer2's scheduler should have returned true (since customer2 is paying cashier)", customer2.pickAndExecuteAnAction());
		assertFalse("Waiter1's scheduler should have returned false (since customer2 has paid cashier and is waiting for change)", customer2.pickAndExecuteAnAction());
		assertTrue("Customer2 should have 7.21, cost of balance minus what he paid, but hasn't received change yet", customer2.getBalance() == 7.21);
		
		assertTrue("Cashier's scheduler should have returned true (since cashier is getting change)", cashier.pickAndExecuteAnAction());
		assertTrue("Customer2 should have received change, so his balance should be 9.01", customer2.getBalance() == 9.01);
		
		//finally customer 0 will pay
		customer0.msgAnimationFinishedGoToCashier();
		assertTrue("Customer0's scheduler should have returned true (since customer is paying cashier)", customer0.pickAndExecuteAnAction());
		assertFalse("Waiter0's scheduler should have returned false (since customer has paid cashier and is waiting for change)", customer0.pickAndExecuteAnAction());
		assertTrue("Customer0 should have 5.81, cost of balance minus what he paid, but hasn't received change yet", customer0.getBalance() == 5.81);
		
		assertTrue("Cashier's scheduler should have returned true (since cashier is getting change)", cashier.pickAndExecuteAnAction());
		assertFalse("Cashier's scheduler should have returned false (since cashier has already given change)", cashier.pickAndExecuteAnAction());

		assertTrue("Customer0 should have received change, so his balance should be more than 5.81", customer0.getBalance() > 5.81);
		
	}//end multiple market, multiple customer scenario
	
	
}
