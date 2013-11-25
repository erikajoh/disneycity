package restaurant_cafe.gui;

public class MenuItem {
	private Food food;
	private String itemName;
	private boolean available;
	private double itemPrice;
	public MenuItem(Food f){
		food = f;
		itemName = food.getName();
		if(f.getAmount() > 0){
			available = true;
		}
		else {
			available = false;
		}
		itemPrice = f.getPrice();
	}
	public Food getFood(){
		return food;
	}
	public String getName(){
		return itemName;
	}
	public void setAvailability(boolean a){
		available = a;
	}
	public boolean getAvailability(){
		return available;
	}
	public double getPrice(){
		return itemPrice;
	}
}
