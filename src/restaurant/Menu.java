package restaurant;

import java.util.*;

public class Menu {
	public List<Food> menu = new ArrayList<Food>();
	
	Menu(){
		menu.add(new Food("Steak", 15.99));
		menu.add(new Food("Chicken", 10.99));
		menu.add(new Food("Salad", 5.99));
		menu.add(new Food("Pizza", 8.99));
	}
	
	public void remove(String s){
		for (int i=0; i<menu.size(); i++){
			if (menu.get(i).name.equals(s)){
				menu.remove(i);
				return;
			}
		}
	}
	
	public double getPrice(String s){
		for (int i=0; i<menu.size(); i++){
			if (menu.get(i).name.compareTo(s) == 0){
				return menu.get(i).price;
			}
		}
		return 0;
	}
	
	public class Food{
		public String name;
		public double price; 
		Food(String name, double price){
			this.name = name;
			this.price = price;
		}
		
	}
}
