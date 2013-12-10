package restaurant_haus;


public class Order {
	WaiterAgent w;
	String choice;
	int table;
	State s;

	Order(WaiterAgent w, String choice, int table) {
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
}