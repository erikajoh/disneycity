package restaurant_haus;


public class Order {
	WaiterAgent w;
	String choice;
	int table;
	State s;

	public Order(WaiterAgent w, String choice, int table) {
		this.w = w;
		this.choice = choice;
		this.table = table;
		s = State.Pending;
	}
	
	public enum State {
		Pending,
		Cooking,
		Done,
		Plated,
		PickedUp
	};
	
	public String getChoice() {
		return choice;
	}
}