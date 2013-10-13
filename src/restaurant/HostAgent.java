package restaurant;

import agent.Agent;
import restaurant.WaiterAgent;
import restaurant.CustomerAgent.AgentState;
import restaurant.gui.HostGui;

import java.util.*;

/**
 * Restaurant Host Agent
 */

public class HostAgent extends Agent {
	static final int NTABLES = 3;//a global for the number of tables.
	//Notice that we implement waitingCustomers using ArrayList, but type it
	//with List semantics.
	public List<MyCustomer> waitingCustomers = Collections.synchronizedList(new ArrayList<MyCustomer>());
	public Collection<Table> tables;
	public List<MyWaiter> waiters = new ArrayList<MyWaiter>(); 
	//note that tables is typed with Collection semantics.
	//Later we will see how it is implemented

	private String name;
	int waiterNumber = 0;

	public HostGui hostGui = null;

	public HostAgent(String name) {
		super();

		this.name = name;
		// make some tables
		tables = new ArrayList<Table>(NTABLES);
		for (int ix = 1; ix <= NTABLES; ix++) {
			tables.add(new Table(ix));//how you add to a collections
		}
	}
	
	public void addWaiter(WaiterAgent w){
		waiters.add(new MyWaiter(w));
		stateChanged();
	}

	public String getMaitreDName() {
		return name;
	}

	public String getName() {
		return name;
	}

	// Messages

	public void msgIWantFood(CustomerAgent cust) {
		waitingCustomers.add(new MyCustomer(cust));
		Do("Got customer " + waitingCustomers.size());
		stateChanged();
	}

	public void msgIAmLeaving(CustomerAgent cust) {
		for (MyCustomer customer : waitingCustomers) {
			if (customer.customer == cust) {
				print(cust + " want to leave");
				waitingCustomers.remove(customer);
				stateChanged();
			}
		}
	}
	
	public void msgIWantToStay(CustomerAgent cust) {
		for (MyCustomer customer : waitingCustomers) {
			if (customer.customer == cust) {
				print(cust + " want to stay");
				customer.state = MyCustomer.CustomerState.staying;
				stateChanged();
			}
		}
	}

	public void msgTableIsFree(CustomerAgent cust, int tablenumber) {
		for (Table table : tables) {
			if (table.tableNumber == tablenumber) {
				print(cust + " leaving " + table);
				table.setUnoccupied();
				stateChanged();
			}
		}
	}
	
	public void msgWantToBreak(WaiterAgent w){
		for (MyWaiter waiter : waiters) {
			if (waiter.w == w) {
				print(w.getName() + " want to break");
				waiter.state = MyWaiter.WaiterState.askingForBreak;
				stateChanged();
			}
		}
	}
	
	public void msgWantToComeBack(WaiterAgent w){
		waiters.add(new MyWaiter(w));
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		try{
			for (MyWaiter waiter : waiters) {
				if (waiter.state == MyWaiter.WaiterState.askingForBreak && waiters.size() > 1) {
					Do("Break granted");
					waiter.w.msgBreakGranted();
					waiters.remove(waiter);
					return true;
				}
			}
			boolean hasEmptyTable = false;
			for (Table table : tables) {
				if (!table.isOccupied()) {
					hasEmptyTable = true;
					for (MyCustomer customer : waitingCustomers) {
						if (waiters.size() != 0){
							if (customer.state == MyCustomer.CustomerState.waiting ||
									customer.state == MyCustomer.CustomerState.staying) {
								seatCustomer(customer.customer, table);
								waitingCustomers.remove(customer);
								return true;
							}
						}
					}
				}
			}
			if (!hasEmptyTable){
				for (MyCustomer customer : waitingCustomers){
					if(customer.state == MyCustomer.CustomerState.waiting){
						customer.customer.msgNoSeat();
						customer.state = MyCustomer.CustomerState.deciding;
						return true;
					}
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

	private void seatCustomer(CustomerAgent customer, Table table) {
		if (waiterNumber < waiters.size() - 1)
			waiterNumber++;
		else
			waiterNumber = 0;
		Do("Telling waiter " + waiterNumber + " " + waiters.get(waiterNumber).w.getName() + " to seat customer");
		waiters.get(waiterNumber).w.msgSitAtTable(customer, table.tableNumber);
		table.setOccupant(customer);
	}

	//utilities

	public void setGui(HostGui gui) {
		hostGui = gui;
	}

	public HostGui getGui() {
		return hostGui;
	}

	private class Table {
		CustomerAgent occupiedBy;
		int tableNumber;

		Table(int tableNumber) {
			this.tableNumber = tableNumber;
		}

		void setOccupant(CustomerAgent cust) {
			occupiedBy = cust;
		}

		void setUnoccupied() {
			occupiedBy = null;
		}

		CustomerAgent getOccupant() {
			return occupiedBy;
		}

		boolean isOccupied() {
			return occupiedBy != null;
		}

		public String toString() {
			return "table " + tableNumber;
		}
	}
	
	public static class MyWaiter{
		WaiterAgent w;
		public enum WaiterState
		{none, askingForBreak};
		private WaiterState state = WaiterState.none;
		
		MyWaiter(WaiterAgent w){
			this.w = w;
		}
	}
	
	public static class MyCustomer{
		CustomerAgent customer;
		public enum CustomerState
		{waiting, deciding, staying};
		private CustomerState state;
		
		MyCustomer(CustomerAgent cust){
			this.customer = cust;
			this.state = CustomerState.waiting;
		}
	}
}

