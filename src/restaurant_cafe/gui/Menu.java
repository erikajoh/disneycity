package restaurant_cafe.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Collection;

public class Menu {
private Collection<Food> foods =  Collections.synchronizedList(new ArrayList<Food>());
private List<MenuItem> items =  Collections.synchronizedList(new ArrayList<MenuItem>());
 
 public Menu(Collection<Food> fds) {
	 foods = fds;
	 synchronized(foods){
	   for(Food food : foods){
		   items.add(new MenuItem(food));
	   }
	 }
 }
 public void removeItem(MenuItem item){
	 items.remove(item);
	 System.out.println("REMOVE = "+items.size());
 }
 public void addItem(Food f){
	 items.add(new MenuItem(f));
	 System.out.println("ADD = "+items.size());
 }
 public List<MenuItem> getItems(){
	return items;
 }
 public Collection<Food> getFoods(){
	 return foods;
 }
}