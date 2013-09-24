package restaurant;

import java.util.*;

public class Menu {
	public static final List<Food> menu = new ArrayList<Food>();
	
	Menu(){
		menu.add(new Food("Steak", 15.99));
		menu.add(new Food("Chicken", 10.99));
		menu.add(new Food("Salad", 5.99));
		menu.add(new Food("Pizza", 8.99));
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
