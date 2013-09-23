package restaurant;

import agent.Agent;
import restaurant.WaiterAgent;
import restaurant.CustomerAgent.AgentEvent;
import restaurant.gui.*;

import java.util.*;
import java.util.concurrent.Semaphore;

public class CookAgent extends Agent{
    private String name = "TheBestCook";
	public List<Order> orders = new ArrayList<Order>();
	Timer timer = new Timer();
	public CookGui cookGui = null;
	
	public CookAgent() {
		super();
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
		timer.schedule(new TimerTask() {
			public void run() {
				print("Cooking food");
				msgDone(order);
			}
		}, 5000);
	}

	private void returnOrder(Order order) {
		Do("The order is ready");
		order.w.msgOrderIsReady(order);
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