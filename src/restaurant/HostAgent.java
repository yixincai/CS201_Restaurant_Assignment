package restaurant;

import agent.Agent;
import restaurant.WaiterAgent;
import restaurant.gui.HostGui;
import restaurant.interfaces.*;
import restaurant.test.mock.EventLog;

import java.util.*;

/**
 * Restaurant Host Agent
 */

public class HostAgent extends Agent implements Host{
	public EventLog log = new EventLog();
	static final int NTABLES = 3;//a global for the number of tables.
	//Notice that we implement waitingCustomers using ArrayList, but type it
	//with List semantics.
	public List<MyCustomer> waitingCustomers = Collections.synchronizedList(new ArrayList<MyCustomer>());
	public Collection<Table> tables;
	public List<MyWaiter> waiters = Collections.synchronizedList(new ArrayList<MyWaiter>()); 
	//note that tables is typed with Collection semantics.
	//Later we will see how it is implemented

	private String name;
	int waiterNumber = 0;

	public HostGui hostGui = null;

	public HostAgent(String name) {
		super();

		this.name = name;
		// make some tables
		tables = Collections.synchronizedList(new ArrayList<Table>(NTABLES));
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

	public void msgIWantFood(Customer cust) {
		synchronized(waitingCustomers){
			waitingCustomers.add(new MyCustomer(cust));
			Do("Got customer " + waitingCustomers.size());
			stateChanged();
		}
	}

	public void msgIAmLeaving(Customer cust) {
		synchronized(waitingCustomers){
			for (MyCustomer customer : waitingCustomers) {
				if (customer.customer == cust) {
					print(cust + " want to leave");
					waitingCustomers.remove(customer);
					stateChanged();
					return;
				}
			}
		}
	}
	
	public void msgIWantToStay(Customer cust) {
		synchronized(waitingCustomers){
			for (MyCustomer customer : waitingCustomers) {
				if (customer.customer == cust) {
					print(cust + " want to stay");
					customer.state = MyCustomer.CustomerState.staying;
					stateChanged();
					return;
				}
			}
		}
	}

	public void msgTableIsFree(Customer cust, int tablenumber) {
		synchronized(tables){
			for (Table table : tables) {
				if (table.tableNumber == tablenumber) {
					print(cust + " leaving " + table);
					table.setUnoccupied();
					stateChanged();
					return;
				}
			}
		}
	}
	
	public void msgWantToBreak(Waiter w){
		synchronized(waiters){
			for (MyWaiter waiter : waiters) {
				if (waiter.w == w) {
					print(w + " want to break");
					waiter.state = MyWaiter.WaiterState.askingForBreak;
					stateChanged();
				}
			}
		}
	}
	
	public void msgWantToComeBack(Waiter w){
		synchronized(waiters){
			waiters.add(new MyWaiter(w));
			stateChanged();
		}
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		try{
			synchronized(waiters){
				for (MyWaiter waiter : waiters) {
					if (waiter.state == MyWaiter.WaiterState.askingForBreak && waiters.size() > 1) {
						Do("Break granted");
						waiter.w.msgBreakGranted();
						waiters.remove(waiter);
						return true;
					}
				}
			}
			boolean hasEmptyTable = false;
			synchronized(tables){
				for (Table table : tables) {
					if (!table.isOccupied()) {
						hasEmptyTable = true;
						synchronized(waitingCustomers){
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
				}
			}
			if (!hasEmptyTable){
				synchronized(waitingCustomers){
					for (MyCustomer customer : waitingCustomers){
						if(customer.state == MyCustomer.CustomerState.waiting){
							customer.customer.msgNoSeat();
							customer.state = MyCustomer.CustomerState.deciding;
							return true;
						}
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

	private void seatCustomer(Customer customer, Table table) {
		if (waiterNumber < waiters.size() - 1)
			waiterNumber++;
		else
			waiterNumber = 0;
		Do("Telling waiter " + waiterNumber + " " + waiters.get(waiterNumber).w + " to seat customer");
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
		Customer occupiedBy;
		int tableNumber;

		Table(int tableNumber) {
			this.tableNumber = tableNumber;
		}

		void setOccupant(Customer cust) {
			occupiedBy = cust;
		}

		void setUnoccupied() {
			occupiedBy = null;
		}

		boolean isOccupied() {
			return occupiedBy != null;
		}

		public String toString() {
			return "table " + tableNumber;
		}
	}
	
	public static class MyWaiter{
		Waiter w;
		public enum WaiterState
		{none, askingForBreak};
		private WaiterState state = WaiterState.none;
		
		MyWaiter(Waiter w){
			this.w = w;
		}
	}
	
	public static class MyCustomer{
		Customer customer;
		public enum CustomerState
		{waiting, deciding, staying};
		private CustomerState state;
		
		MyCustomer(Customer cust){
			this.customer = cust;
			this.state = CustomerState.waiting;
		}
	}
}

