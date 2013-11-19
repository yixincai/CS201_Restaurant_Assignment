package restaurant;

import agent.Agent;
import restaurant.gui.*;
import restaurant.interfaces.*;
import restaurant.test.mock.EventLog;

import java.util.*;
import java.util.concurrent.Semaphore;

public class CookAgent extends Agent implements Cook{
	public Restaurant r;
	public EventLog log = new EventLog();
    private String name = "TheBestCook";
    public List<MyMarket> markets = new ArrayList<MyMarket>();
	public List<Order> orders = Collections.synchronizedList(new ArrayList<Order>());
	public Map<String, Food> inventory = new HashMap<String, Food>();
	Timer timer = new Timer(), timer2 = new Timer();
	public CookGui cookGui = null;
	boolean lowInFood = true;
	enum CookState{notOrdered,MarketOrderDelivered};
	CookState state = CookState.notOrdered;
	enum CheckState{notChecked,Checked};
	CheckState check_state = CheckState.notChecked;	
	private Semaphore atTable = new Semaphore(0,true);
	
	public CookAgent() {
		super();
		inventory.put("Steak", new Food("Steak", 5000, 1, 3, 5));
		inventory.put("Chicken", new Food("Chicken", 4000, 1, 3, 5));
		inventory.put("Salad", new Food("Salad", 1000, 0, 3, 5));
		inventory.put("Pizza", new Food("Pizza", 3000, 1, 3, 5));
	}
	
	public void notifyCook(){
		stateChanged();
		check_state = CheckState.notChecked;
	}

	public void setGui(CookGui gui){
		cookGui = gui;
	}
	
	public String getName() {
		return name;
	}

	public void addMarket(MarketAgent market) {
		markets.add(new MyMarket(market));
	}
	
	// Messages
	public void releaseSemaphore(){
		atTable.release();
	}
	
	public void msgHereIsTheOrder(Waiter w, String choice, int table) {
		Do("Order received");
		orders.add(new Order(w,choice,table,Order.OrderState.NotCooked));
		stateChanged();
	}

	public void msgDone(Order o){
		o.state = Order.OrderState.Cooked;
		stateChanged();
	}
	
	public void msgOrderFulfillment(Market m, Map<String, Integer> order){
		print("Market response received");
		state = CookState.notOrdered;
		for (MyMarket market : markets){
			if (market.market == m){
				if (order.get("Steak") == 0 && order.get("Chicken") == 0 
						&& order.get("Pizza") ==0 && order.get("Salad") == 0){
					market.state = MyMarket.MarketState.Empty;
				}
				break;
			}
		}
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
	public boolean pickAndExecuteAnAction() {
		try{
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
			Order order = r.revolving_stand.remove();
			if (order!=null){
				DoGoToRevolvingStand();
				orders.add(order);
				cookOrder(order);
				return true;
			}
			if(lowInFood && state == CookState.notOrdered){
				for (MyMarket market : markets){
					if (market.state == MyMarket.MarketState.NotEmpty){
						askForSupply(market);
						state =  CookState.MarketOrderDelivered;
						lowInFood = false;
						return true;
					}
				}
			}
		}
		catch(ConcurrentModificationException e){
			return false;
		}
		GoHome();
		if (check_state == CheckState.notChecked){
			timer2.schedule(new TimerTask() {
				public void run() {
					print("Notify the cook to check revolving stand");
					notifyCook();
				}
			}, 10000);
			check_state = CheckState.Checked;
		}
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions
	
	private void askForSupply(MyMarket market){
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
		market.market.msgHereIsTheOrder(order);
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
		DoGoToFridge();
		order.state = Order.OrderState.Cooking;
		final long time = f.cookingTime;
		f.amount--;
		DoGoToCookingPlace();
		DoCookFood(order.choice);
		timer.schedule(new TimerTask() {
			public void run() {
				print("Cooking " + order.choice + " with time of " + time);
				msgDone(order);
				cookGui.DoFinishFood();
			}
		}, time);
	}

	private void returnOrder(Order order) {
		Do("The order is ready");
		DoGoToPlate();
		order.w.msgOrderIsReady(order.choice, order.tableNumber);
		orders.remove(order);
	}
	
	private void GoHome(){
		cookGui.DoGoHome();
	}
	
	// The animation DoXYZ() routines
	private void DoGoToCookingPlace() {
		print("Go Cooking.");
		cookGui.DoGoCookFood();
		try{
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void DoCookFood(String choice){
		print("Cooking " + choice);
		cookGui.DoCookFood(choice);
	}

	private void DoGoToFridge() {
		print("Going to fridge");
		cookGui.DoGoToFridge();
		try{
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void DoGoToPlate() {
		print("Going to plating area");
		cookGui.DoPutPlate();
		try{
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void DoGoToRevolvingStand() {
		print("Going to revolving stand");
		cookGui.DoGoToRevolvingStand();
		try{
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	//utilities

	public static class Order {
		String choice;
		int tableNumber;
		Waiter w;
		public enum OrderState
		{None, NotCooked, Cooking, Cooked, Delivered};
		private OrderState state = OrderState.None;
		
		Order(Waiter w, String choice, int tableNumber, OrderState state) {
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
	
	public static class MyMarket{
		Market market;
		public enum MarketState
		{NotEmpty, Empty};
		MarketState state;
		
		MyMarket(Market m){
			this.market = m;
			this.state = MarketState.NotEmpty;
		}
	}
}