package restaurant_rancho.test.mock;
import bank.interfaces.BankCustomer;
import simcity.interfaces.Bank_Douglass;
import simcity.interfaces.Person;


public class MockBank extends Mock implements Bank_Douglass {
	

		public EventLog log = new EventLog();
		String name;
		
		public MockBank(String name) {
			super(name);
			this.name = name;
		}
		
	
		public String getBankName() {
			return name;
			
		}
		
		public void msgRequestAccount(Person person, double reqAmt, boolean present) {
			log.add(new LoggedEvent("Received msg Request Account"));
			
			
		}
		public void msgRequestDeposit(Person person, int accountNum, double reqAmt, boolean present) {
			log.add(new LoggedEvent("Received msg Request Deposit"));
			
		}
		public void msgRequestWithdrawal(Person person, int accountNum, double reqAmt, boolean present) {
			log.add(new LoggedEvent("Received msg Request Withdrawal"));
			
		}
		public void msgLeave(BankCustomer bc, int accountNum, double change, double loanAmt, int loanTime){
			log.add(new LoggedEvent("Received msg Leave"));
			
		}


		@Override
		public boolean isOpen() {
			// TODO Auto-generated method stub
			return false;
		}


		@Override
		public void addPerson(Person person) {
			// TODO Auto-generated method stub
			
		}
}
