package restaurant;

import agent.Agent;
import restaurant.CookAgent;
import restaurant.interfaces.*;
import restaurant.test.mock.EventLog;

import java.util.*;

public class MarketAgent extends Agent implements Market{
	public EventLog log = new EventLog();
	private String name = "Market";
	public CookAgent cook = null;
	public CashierAgent cashier = null;
	private Map<String, Integer> order = new HashMap<String, Integer>();
	private Map<String, Integer> inventory = new HashMap<String, Integer>();
	boolean orderReceived = false, orderFinished = false;
	Random r = new Random();
	Timer timer = new Timer();

	public MarketAgent(String name) {
		super();
		this.name = name;
		inventory.put("Steak", r.nextInt(10));
		inventory.put("Chicken", r.nextInt(10));
		inventory.put("Salad", r.nextInt(10));
		inventory.put("Pizza", r.nextInt(10));
	}

	public String getName() {
		return name;
	}

	public void setCashier(CashierAgent cashier) {
		this.cashier = cashier;
	}
	
	public void setCook(CookAgent cook) {
		this.cook = cook;
	}
	// Messages
	public void msgHereIsTheOrder(Map<String, Integer> order) {
		Do("Cook order received");
		this.order = order;
		orderReceived = true;
		stateChanged();
	}
	
	public void msgHereIsThePayment(double s) {
		Do("Cashier payment received with amount of " + s);
	}

	public void msgDone(){
		orderFinished = true;
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		try{
			if (orderReceived){
				timer.schedule(new TimerTask() {
					public void run() {
						msgDone();
					}
				}, 5000);
				orderReceived = false;
				return true;
			}
			if (orderFinished){
				returnOrder(order);
				orderFinished = false;
				return true;
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

	private void returnOrder(Map<String, Integer> order) {
		Do("Give back the order to cook.");
		double bill = 0.0;
		Map<String, Integer> new_order = new HashMap<String, Integer>();
		int storage = inventory.get("Steak");
		int requirement = order.get("Steak");
		if (storage >= requirement){
			new_order.put("Steak", requirement);
			inventory.put("Steak", storage - requirement);
			bill += requirement*15.99;
		}
		else{
			new_order.put("Steak", storage);
			inventory.put("Steak", 0);
			bill += storage*15.99;
		}
		storage = inventory.get("Chicken");
		requirement = order.get("Chicken");
		if (storage >= requirement){
			new_order.put("Chicken", requirement);
			inventory.put("Chicken", storage - requirement);
			bill += requirement*10.99;
		}
		else{
			new_order.put("Chicken", storage);
			inventory.put("Chicken", 0);
			bill += storage*10.99;
		}
		storage = inventory.get("Salad");
		requirement = order.get("Salad");
		if (storage >= requirement){
			new_order.put("Salad", requirement);
			inventory.put("Salad", storage - requirement);
			bill += requirement*5.99;
		}
		else{
			new_order.put("Salad", storage);
			inventory.put("Salad", 0);
			bill += storage*5.99;
		}
		storage = inventory.get("Pizza");
		requirement = order.get("Pizza");
		if (storage >= requirement){
			new_order.put("Pizza", requirement);
			inventory.put("Pizza", storage - requirement);
			bill += requirement*8.99;
		}
		else{
			new_order.put("Pizza", storage);
			inventory.put("Pizza", 0);
			bill += storage*8.99;
		}
		cook.msgOrderFulfillment(this,new_order);
		Do("Give the bill to cashier");
		if (!(new_order.get("Steak") == 0 && new_order.get("Chicken") == 0 
				&& new_order.get("Pizza") ==0 && new_order.get("Salad") == 0)){
			cashier.msgHereIsTheBill(this, bill);
		}
	}
}