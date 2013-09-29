package restaurant;

import agent.Agent;
import restaurant.WaiterAgent;
import restaurant.gui.*;

import java.util.*;

public class CookAgent extends Agent{
    private String name = "TheBestCook";
	public List<Order> orders = Collections.synchronizedList(new ArrayList<Order>());
	Timer timer = new Timer();
	private Map<String, Long> cook_time= new HashMap<String, Long>();
	public CookGui cookGui = null;
	
	public CookAgent() {
		super();
		cook_time.put("Steak", (long)5000);
		cook_time.put("Chicken", (long)4000);
		cook_time.put("Salad", (long)2000);
		cook_time.put("Pizza", (long)3000);
	}

	public void setGui(CookGui gui){
		cookGui = gui;
	}
	
	public String getName() {
		return name;
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
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		for (Order order : orders){
			if (order.state == Order.OrderState.NotCooked){
				cookOrder(order);
				return true;
			}
		}
		for (Order order : orders){
			if (order.state == Order.OrderState.Cooked){
				returnOrder(order);
				return true;
			}
		}

		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions

	private void cookOrder(final Order order) {
		DoCooking(order.choice);
		order.state = Order.OrderState.Cooking;
		final long time = cook_time.get(order.choice);
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
		//Notice how we print "customer" directly. It's toString method will do it.
		//Same with "table"
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
}