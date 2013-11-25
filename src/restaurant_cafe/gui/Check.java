package restaurant_cafe.gui;

public class Check {
	private String foodName;
	private double total;
	
	public Check(String f, double t){
		foodName = f;
		total = t;
	}
	
	public String getFoodName(){
		return foodName;
	}
	public double getTotal(){
		return total;
	}
}
