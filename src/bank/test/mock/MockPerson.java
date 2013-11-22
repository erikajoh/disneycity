package bank.test.mock;

import bank.BankCustomerAgent.AgentEvent;
import bank.BankCustomerAgent.AgentState;
import bank.gui.Check;
import bank.gui.Menu;
import bank.interfaces.Teller;
import bank.interfaces.BankCustomer;
import bank.interfaces.Waiter;

/**
 * A sample MockCustomer built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public class MockPerson extends Mock implements BankCustomer {

	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public MockTeller cashier;
	Menu menu;
	String choice;
	double balance;
	public Check check;
	public enum AgentState
	{DoingNothing, WaitingInbank, BeingSeated, MakingChoice, MadeChoice, WaitingForFood, Eating, DoneEating, GoingToCashier, GettingChange, Cleaning, DoneCleaning, Leaving};
	private AgentState state = AgentState.DoingNothing;

	public enum AgentEvent 
	{none, gotHungry, longWait, followHost, seated, askedToOrder, ordered, broughtFood, gotCheck, paying, goToClean, clean, paid, doneLeaving};
	AgentEvent event = AgentEvent.none;
	

	public MockPerson(String name) {
		super(name);
	}
	
	public void setCashier(MockTeller c){
		cashier = c;
	}

	public void msgGotHungry() {
		
	}
	
	public void msgBankFull() {
		
	}

	public void msgFollowMe(Waiter w, int tn, Menu m) {
		
	}
	
	public void msgAskOrder(){
		
	}
	
	public void msgReorder(){
		
	}
	
	public void msgHereIsFood(){
		
	}
	
	public void msgHereIsCheck(Check c){
		check = c;
		event = AgentEvent.gotCheck;
	}
	
	public void msgHereIsChange(double change){
		balance += change;
		event = AgentEvent.paid;
	}
	
	public void msgAnimationFinishedGoToCashier() {
		event = AgentEvent.paying;
	}
	
	public void msgCleanDishes(){
		
	}
	
	public boolean pickAndExecuteAnAction(){
		if (event == AgentEvent.paying){
			payCashier();
			return true;
		}
		return false;
	}
	
	private void payCashier(){
		event = AgentEvent.none;
		double pay = check.getTotal();
		if(balance > (pay*1.2)){
			pay *= 1.2;
		}
		else if(balance < pay){
			cashier.msgNoMoney(this);
			return;
		}
		pay = Math.round(pay*100.0)/100.0;
		balance = Math.round((balance-pay)*100.0)/100.0;
		cashier.msgHereIsPayment(MockPerson.this, pay);
	}
	
	public double getBalance(){
		return balance;
	}
	
	public void setBalance(double b){
		balance = b;
	}
	
	public int getNumber(){
		return 0;
	}
	
}
