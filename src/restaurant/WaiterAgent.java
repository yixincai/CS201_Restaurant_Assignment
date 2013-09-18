package restaurant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Semaphore;

import restaurant.gui.CookGui;
import restaurant.CookAgent.*;
import restaurant.CustomerAgent;
import restaurant.HostAgent;
import restaurant.gui.*;
import agent.Agent;

public class WaiterAgent extends Agent {

	private List<MyCustomer> customers = new ArrayList<MyCustomer>();
	public CookAgent cook = null;
	public HostAgent host = null;
	public WaiterGui waiterGui = null;
	
	private String name;
	private Semaphore atTable = new Semaphore(0,true);

	public WaiterAgent(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public void setHost(HostAgent h){
		this.host = h;
	}
	
	public void setCook(CookAgent c){
		this.cook = c;
	}

	// Messages

	public void msgSitAtTable(CustomerAgent cust, int tablenumber) {
		customers.add(new MyCustomer(cust, tablenumber, MyCustomer.CustomerState.waiting));
		stateChanged();
	}

	public void msgReadyToOrder(CustomerAgent cust) {
		for (MyCustomer c: customers) {
			if (c.c == cust && c.state == MyCustomer.CustomerState.seated) {
				c.state = MyCustomer.CustomerState.readyToOrder;
				stateChanged();
			}
		}
	}

	public void msgHereIsTheChoice(CustomerAgent cust, String choice) {
		for (MyCustomer c: customers) {
			if (c.c == cust && c.state == MyCustomer.CustomerState.aboutToGiveOrder) {
				c.choice = choice;
				c.state = MyCustomer.CustomerState.orderGiven;
				System.out.println(c.state);
				stateChanged();
				return;
			}
		}
	}
	
	public void msgOrderIsReady(Order o) {
		for (MyCustomer c: customers) {
			if (c.tableNumber == o.tableNumber && c.state == MyCustomer.CustomerState.orderProcessed) {
				c.state = MyCustomer.CustomerState.orderReady;
				stateChanged();
			}
		}
	}
	
	public void msgDoneEating(CustomerAgent cust) {
		for (MyCustomer c: customers) {
			if (c.c == cust && c.state == MyCustomer.CustomerState.eating) {
				c.state = MyCustomer.CustomerState.finished;
				stateChanged();
			}
		}
	}	

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		
		for (MyCustomer customer : customers) {
			if (customer.state == MyCustomer.CustomerState.waiting) {
				seatCustomer(customer);
				return true;
			}
		}
		for (MyCustomer customer : customers) {
			if (customer.state == MyCustomer.CustomerState.readyToOrder){
				askForChoice(customer);
				return true;
			}
		}
		for (MyCustomer customer : customers) {
			if (customer.state == MyCustomer.CustomerState.orderGiven) {
				processOrder(customer);
				return true;
			}
		}
		for (MyCustomer customer : customers) {
			if (customer.state == MyCustomer.CustomerState.orderReady) {
				giveOrderToCustomer(customer);
				return true;
			}
		}
		for (MyCustomer customer : customers) {
			if (customer.state == MyCustomer.CustomerState.finished) {
				clearCustomer(customer);
				return true;
			}
		}

		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions

	private void seatCustomer(MyCustomer customer) {
		customer.state = MyCustomer.CustomerState.seated;
		customer.c.msgFollowMe(this, customer.tableNumber);
		DoSeatCustomer(customer.c, customer.tableNumber);

	}

	private void askForChoice(MyCustomer customer){
		customer.state = MyCustomer.CustomerState.aboutToGiveOrder;	
		customer.c.msgWhatWouldYouLike();
		DoGoToCustomer(customer.c, customer.tableNumber);
	}

	private void processOrder(MyCustomer customer){
		Do("Process order");
		customer.state = MyCustomer.CustomerState.orderProcessed;
		cook.msgHereIsTheOrder(this, customer.choice, customer.tableNumber);
		DoGoToCook();
	}
	
	private void giveOrderToCustomer(MyCustomer customer){
		Do("Give order to customer");
		customer.state = MyCustomer.CustomerState.eating;
		customer.c.msgHereIsYourFood(customer.choice);
		DoGiveFoodToCustomer(customer.c, customer.tableNumber);
	}

	private void clearCustomer(MyCustomer customer){
		Do("Clear customer");
		host.msgTableIsFree(customer.c, customer.tableNumber);
		customers.remove(customer);
	}
	
	private void DoSeatCustomer(CustomerAgent customer, int table){
		print("Seating " + customer + " at " + table);
		waiterGui.DoGoToTable(customer, table);
	}
	
	private void DoGoToCustomer(CustomerAgent customer, int table){
		print("Seating " + customer + " at " + table);
		waiterGui.DoGoToTable(customer, table);
	}
	
	private void DoGoToCook(){
		print("Going to cook");
		waiterGui.DoGoToCook();
	}
	
	private void DoGiveFoodToCustomer(CustomerAgent customer, int table){
		print("Seating " + customer + " at " + table);
		waiterGui.DoGoToTable(customer, table);
	}
	
	//utilities

	public void setGui(WaiterGui gui) {
		waiterGui = gui;
	}

	public WaiterGui getGui() {
		return waiterGui;
	}
	
	private static class MyCustomer {
		CustomerAgent c;
		int tableNumber;
		String choice = "";
		public enum CustomerState
		{none, waiting, seated, readyToOrder, aboutToGiveOrder, 
			orderGiven, orderProcessed, orderReady, eating, finished};
		private CustomerState state = CustomerState.none;
		
		MyCustomer(CustomerAgent c, int tableNumber, CustomerState s) {
			this.c = c;
			this.tableNumber = tableNumber;
			this.state = s;
		}

		public String toString() {
			return "table " + tableNumber;
		}
	}
}

