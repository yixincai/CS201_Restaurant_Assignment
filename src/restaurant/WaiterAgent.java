package restaurant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.Semaphore;

import restaurant.CookAgent.*;
import restaurant.CustomerAgent;
import restaurant.HostAgent;
import restaurant.Menu;
import restaurant.gui.*;
import agent.Agent;

public class WaiterAgent extends Agent {

	private List<MyCustomer> customers = Collections.synchronizedList(new ArrayList<MyCustomer>());
	public CookAgent cook = null;
	public HostAgent host = null;
	public CashierAgent cashier = null;
	public WaiterGui waiterGui = null;
	private Semaphore atTable = new Semaphore(0,true);
	boolean breakRequest = false, backRequest = false;//Two booleans from gui to tell whether to go on break or to 
	boolean	OnBreak = false, breakEnabled = true; //Two booleans to tell gui what to show and whether to enable

	private String name;

	public WaiterAgent(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public boolean getBreakStatus(){
		return OnBreak;
	}
	
	public boolean getBreakEnable(){
		return breakEnabled;
	}

	public void setHost(HostAgent h){
		this.host = h;
	}

	public void setCook(CookAgent c){
		this.cook = c;
	}

	public void setCashier(CashierAgent c){
		this.cashier = c;
	}

	public void releaseSemaphore(){
		atTable.release();
		stateChanged();
	}

	// Messages

	public void msgSitAtTable(CustomerAgent cust, int tablenumber) {
		customers.add(new MyCustomer(cust, tablenumber, MyCustomer.CustomerState.waiting));
		stateChanged();
	}

	public void msgNoMoneyAndLeaving(CustomerAgent cust){
		for (MyCustomer c: customers) {
			if (c.c == cust) {
				c.state = MyCustomer.CustomerState.noMoney;
				stateChanged();
			}
		}		
	}

	public void msgReadyToOrder(CustomerAgent cust) {
		for (MyCustomer c: customers) {
			if (c.c == cust) {
				c.state = MyCustomer.CustomerState.readyToOrder;
				stateChanged();
			}
		}
	}

	public void msgHereIsTheChoice(CustomerAgent cust, String choice) {
		for (MyCustomer c: customers) {
			if (c.c == cust) {
				c.choice = choice;
				c.state = MyCustomer.CustomerState.orderGiven;
				atTable.release();
				stateChanged();
				return;
			}
		}
	}

	public void msgOrderIsReady(String choice, int tableNumber) {
		for (MyCustomer c: customers) {
			if (c.tableNumber == tableNumber) {
				c.state = MyCustomer.CustomerState.orderReady;
				stateChanged();
			}
		}
	}

	public void msgFoodRunsOut(String choice, int tableNumber) {
		for (MyCustomer c: customers) {
			if (c.tableNumber == tableNumber) {
				Do("Got msg " + choice + " is running out.");
				c.state = MyCustomer.CustomerState.noFood;
				stateChanged();
			}
		}
	}

	public void msgDoneEating(CustomerAgent cust) {
		for (MyCustomer c: customers) {
			if (c.c == cust) {
				c.state = MyCustomer.CustomerState.finishedEating;
				stateChanged();
			}
		}
	}

	public void msgHereIsTheCheck(double money, CustomerAgent cust){
		for (MyCustomer c: customers) {
			if (c.c == cust) {
				c.state = MyCustomer.CustomerState.checkComputed;
				c.check = money;
				stateChanged();
			}
		}
	}

	public void msgLeavingRestaurant(CustomerAgent cust){
		for (MyCustomer c: customers) {
			if (c.c == cust) {
				c.state = MyCustomer.CustomerState.leaving;
				stateChanged();
			}
		}
	}

	public void msgAskForBreak(){
		breakRequest = true;
		OnBreak = true;// for gui purpose
		breakEnabled = false;//for gui purpose
		stateChanged();
	}

	public void msgBreakGranted(){
		breakEnabled = true;//for gui purpose
		Do("Break request granted.");
		stateChanged();
	}

	public void msgAskToComeBack(){
		backRequest = true;
		OnBreak = false; // for gui purpose
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		try{
			if (breakRequest){
				Do("Tell host to break");
				host.msgWantToBreak(this);
				breakRequest = false;
				return true;
			}
			if (backRequest){
				Do("Tell host I'm coming back");
				host.msgWantToComeBack(this);
				backRequest = false;
				return true;
			}
			for (MyCustomer customer : customers) {
				if (customer.state == MyCustomer.CustomerState.finishedEating) {
					computeBill(customer);
					return true;
				}
			}
			for (MyCustomer customer : customers) {
				if (customer.state == MyCustomer.CustomerState.noMoney) {
					clearCustomer(customer);
					return true;
				}
			}
			for (MyCustomer customer : customers) {
				if (customer.state == MyCustomer.CustomerState.checkComputed) {
					giveCheck(customer);
					return true;
				}
			}
			for (MyCustomer customer : customers) {
				if (customer.state == MyCustomer.CustomerState.leaving) {
					clearCustomer(customer);
					return true;
				}
			}
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
				if (customer.state == MyCustomer.CustomerState.noFood){
					giveNewMenu(customer);
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

	private void seatCustomer(MyCustomer customer) {
		waiterGui.DoFetchCustomer();
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		customer.state = MyCustomer.CustomerState.none;
		customer.c.msgFollowMe(this, customer.tableNumber, new Menu());
		DoSeatCustomer(customer.c, customer.tableNumber);
	}

	private void askForChoice(MyCustomer customer){
		customer.state = MyCustomer.CustomerState.none;	
		DoGoToCustomer(customer.c, customer.tableNumber);
		customer.c.msgWhatWouldYouLike();
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void giveNewMenu(MyCustomer customer){
		Do("give new menu");
		DoGoToCustomer(customer.c, customer.tableNumber);
		customer.state = MyCustomer.CustomerState.none;
		Menu m = new Menu();
		m.menu.remove(customer.choice);
		customer.c.msgNoFood(m);
	}

	private void processOrder(MyCustomer customer){
		Do("Process order");
		customer.state = MyCustomer.CustomerState.none;
		DoGoToCook();
		cook.msgHereIsTheOrder(this, customer.choice, customer.tableNumber);
	}

	private void giveOrderToCustomer(MyCustomer customer){
		DoGoToCook();
		Do("Give order to customer");
		customer.state = MyCustomer.CustomerState.none;
		DoGiveFoodToCustomer(customer.c, customer.tableNumber, customer.choice);
		customer.c.msgHereIsYourFood(customer.choice);
	}

	private void computeBill(MyCustomer customer){
		Do("Ask Cashier to compute bill");
		cashier.msgComputeBill(this, customer.c, customer.choice);
		customer.state = MyCustomer.CustomerState.none;
	}

	private void giveCheck(MyCustomer customer){
		Do("Give Customer the bill");
		customer.c.msgHereIsTheCheck(customer.check, cashier);
		customer.state = MyCustomer.CustomerState.none;
	}

	private void clearCustomer(MyCustomer customer){
		Do("Clear customer");
		host.msgTableIsFree(customer.c, customer.tableNumber);
		customers.remove(customer);
	}

	private void DoSeatCustomer(CustomerAgent customer, int table){
		print("Seating " + customer + " at " + table);
		waiterGui.DoGoToTable(customer, table);
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		waiterGui.DoLeaveCustomer();
	}

	private void DoGoToCustomer(CustomerAgent customer, int table){
		print("Going to " + customer + " at " + table);
		waiterGui.DoGoToTable(customer, table);
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		waiterGui.DoLeaveCustomer();
	}

	private void DoGoToCook(){
		print("Going to cook");
		waiterGui.DoGoToCook();
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void DoGiveFoodToCustomer(CustomerAgent customer, int table, String food){
		print("Giving food to " + customer + " at " + table);
		waiterGui.DoBringFood(customer, table, food);
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		waiterGui.DoLeaveCustomer();
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
		double check = 0;
		public enum CustomerState
		{none, waiting, noMoney, readyToOrder, 
			orderGiven, orderReady, noFood, finishedEating, checkComputed, leaving};
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

