package restaurant;

import agent.Agent;
import restaurant.WaiterAgent;
import restaurant.gui.*;

import java.util.*;

public class CookAgent extends Agent{
    private String name = "TheBestCook";
    public List<MarketAgent> markets = new ArrayList<MarketAgent>();
    int market_index = 0;
	public List<Order> orders = Collections.synchronizedList(new ArrayList<Order>());
	public Map<String, Food> inventory = new HashMap<String, Food>();
	Timer timer = new Timer();
	public CookGui cookGui = null;
	boolean lowInFood = true;
	
	public CookAgent() {
		super();
		inventory.put("Steak", new Food("Steak", 5000, 1, 3, 5));
		inventory.put("Chicken", new Food("Chicken", 4000, 1, 3, 5));
		inventory.put("Salad", new Food("Salad", 1000, 1, 3, 5));
		inventory.put("Pizza", new Food("Pizza", 3000, 1, 3, 5));
	}

	public void setGui(CookGui gui){
		cookGui = gui;
	}
	
	public String getName() {
		return name;
	}

	public void addMarket(MarketAgent market) {
		markets.add(market);
	}
	
	// Messages
	public void msgHereIsTheOrder(WaiterAgent w, String choice, int table) {
		Do("Order received");
		orders.add(new Order(w,choice,table,Order.OrderState.NotCooked));
		stateChanged();
	}

	public void msgDone(Order o){
		o.state = Order.OrderState.Cooked;
		stateChanged();
	}
	
	public void msgOrderFulfillment(Map<String, Integer> order){
		print("Market response received");
		inventory.get("Steak").amount += order.get("Steak");
		inventory.get("Chicken").amount += order.get("Chicken");
		inventory.get("Pizza").amount += order.get("Pizza");
		inventory.get("Salad").amount += order.get("Salad");
		if (inventory.get("Steak").amount < inventory.get("Steak").threshold)
			lowInFood = true;
		if (inventory.get("Chicken").amount < inventory.get("Chicken").threshold)
			lowInFood = true;
		if (inventory.get("Pizza").amount < inventory.get("Pizza").threshold)
			lowInFood = true;
		if (inventory.get("Salad").amount < inventory.get("Salad").threshold)
			lowInFood = true;
		stateChanged();
	}
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		try{
			if(lowInFood){
				if (market_index < markets.size() - 1)
					market_index++;
				else
					market_index = 0;
				lowInFood = false;
				askForSupply();
			}
			for (Order order : orders){
				if (order.state == Order.OrderState.Cooked){
					returnOrder(order);
					return true;
				}
			}
			for (Order order : orders){
				if (order.state == Order.OrderState.NotCooked){
					cookOrder(order);
					return true;
				}
			}
		}
		catch(ConcurrentModificationException e){
			return false;
		}

		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions
	
	private void askForSupply(){
		Do("Buy food from market.");
		Map<String, Integer> order = new HashMap<String, Integer>();
		if (inventory.get("Steak").amount <= inventory.get("Steak").threshold)
			order.put("Steak", inventory.get("Steak").capacity - inventory.get("Steak").amount);
		else
			order.put("Steak", 0);
		if (inventory.get("Chicken").amount <= inventory.get("Chicken").threshold)
			order.put("Chicken", inventory.get("Chicken").capacity - inventory.get("Chicken").amount);
		else
			order.put("Chicken", 0);
		if (inventory.get("Salad").amount <= inventory.get("Salad").threshold)
			order.put("Salad", inventory.get("Salad").capacity - inventory.get("Salad").amount);
		else
			order.put("Salad", 0);
		if (inventory.get("Pizza").amount <= inventory.get("Pizza").threshold)
			order.put("Pizza", inventory.get("Pizza").capacity - inventory.get("Pizza").amount);
		else
			order.put("Pizza", 0);
		markets.get(market_index).msgHereIsTheOrder(order);
	}

	private void cookOrder(final Order order) {
		Food f = inventory.get(order.choice);
		if (f.amount < f.threshold){
			lowInFood = true;
		}		
		if (f.amount == 0){
			Do(f.choice + " is running out");
			order.w.msgFoodRunsOut(order.choice, order.tableNumber);
			orders.remove(order);
			return;
		}
		DoCooking(order.choice);
		order.state = Order.OrderState.Cooking;
		final long time = f.cookingTime;
		f.amount--;
		timer.schedule(new TimerTask() {
			public void run() {
				print("Cooking " + order.choice + " with time of " + time);
				msgDone(order);
			}
		}, time);
	}

	private void returnOrder(Order order) {
		Do("The order is ready");
		order.w.msgOrderIsReady(order.choice, order.tableNumber);
		orders.remove(order);
	}
	
	// The animation DoXYZ() routines
	private void DoCooking(String choice) {
		print("Cooking " + choice);
		cookGui.DoCookFood(); 
	}

	//utilities

	public static class Order {
		String choice;
		int tableNumber;
		WaiterAgent w;
		public enum OrderState
		{None, NotCooked, Cooking, Cooked, Delivered};
		private OrderState state = OrderState.None;
		
		Order(WaiterAgent w, String choice, int tableNumber, OrderState state) {
			this.choice = choice;
			this.tableNumber = tableNumber;
			this.w = w;
			this.state = state;
		}
	}
	
	public static class Food {
		String choice;
		long cookingTime;
		int amount;
		int threshold;
		int capacity;
		boolean ordered = false;
		
		Food(String choice, long time, int amount, int threshold, int capacity){
			this.choice = choice;
			this.cookingTime = time;
			this.amount = amount;
			this.threshold = threshold;
			this.capacity = capacity;
		}
	}
}