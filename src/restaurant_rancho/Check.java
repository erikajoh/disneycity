package restaurant_rancho;

import restaurant_rancho.CashierAgent.checkState;
import restaurant_rancho.interfaces.Customer;
import restaurant_rancho.interfaces.Waiter;

public class Check {

		public Customer cust;
		public Waiter waiter;
		public String food;
		public double amount;
		public checkState cs;
		double change;
		
		public Check (Customer c, Waiter w, String f, double am) {
			cust = c;
			waiter = w;
			food = f;
			amount = am;
			change = 0.0;
			cs = checkState.nothing;
		}

}
