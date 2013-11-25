package restaurant_cafe.gui;

public class Food {
	private String choice;
	private int cookingTime;
	private int amount;
	private int low;
	private int capacity;
	private double price;
	private int orderAttempts;
	
	public Food(String c, int ct, int amt, int min, int cap, double p){
		choice = c;
		cookingTime = ct;
		amount = amt;
		low = min;
		capacity = cap;
		price = p;
		orderAttempts = 0;
	}
	public String getName(){
		return choice;
	}
	public int getCookingTime(){
		return cookingTime;
	}
	public void setAmount(int amt){
		amount = amt;
	}
	public void decreaseAmount(){
		if(amount > 0){
			amount--;
		}
	}
	public int getAmount(){
		return amount;
	}
	public int getLowAmt(){
		return low;
	}
	public int getCapacity(){
		return capacity;
	}
	public double getPrice(){
		return price;
	}
	public void setOrderAttempts(int oa){
		orderAttempts = oa;
	}
	public int getOrderAttempts(){
		return orderAttempts;
	}
	
}
