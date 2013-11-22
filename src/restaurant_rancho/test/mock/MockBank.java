package restaurant_rancho.test.mock;
import restaurant_rancho.interfaces.Bank;
import restaurant_rancho.HostAgent;

public class MockBank extends Mock implements Bank {
	

		public EventLog log = new EventLog();
		String name;
		
		public MockBank(String name) {
			super(name);
			this.name = name;
		}
		
		public void msgEnteredBank(HostAgent host) {
			
		}

}
