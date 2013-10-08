package restaurant;

import agent.Agent;
import restaurant.WaiterAgent;
import restaurant.CustomerAgent;
import restaurant.gui.*;

import java.util.*;

public class CashierAgent extends Agent{
    private String name = "Cashier";
	public List<Bill> bills = Collections.synchronizedList(new ArrayList<Bill>());
	public static Menu menu = new Menu();
	public CashierGui cashierGui = null;
	
	public CashierAgent() {
		super();
	}
	
	public void setGui(CashierGui g) {
		cashierGui = g;
	}
	
	public String getName() {
		return name;
	}

	public void msgComputeBill(WaiterAgent w, CustomerAgent c, String choice) {
		Do("Bill Request received");
		bills.add(new Bill(w,c,choice));
		stateChanged();
	}
	
	// Messages
	public void msgHereIsThePayment(CustomerAgent c, double check, double cash) {
		Do("Payment received");
		for (Bill bill : bills)
			if (bill.customer == c){
				bill.cash = cash;
				bill.price  = check;
				bill.state = Bill.BillState.ReturnedFromCustomer;
				stateChanged();
			}
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		try{
			for (Bill bill : bills){
				if (bill.state == Bill.BillState.NotComputed){
					computeBill(bill);
					return true;
				}
			}
			for (Bill bill : bills){
				if (bill.state == Bill.BillState.ReturnedFromCustomer){
					makeChange(bill);
					bills.remove(bill);
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

	private void computeBill(Bill bill) {
		Do("The Bills is computed.");
		bill.state = Bill.BillState.None;
		bill.waiter.msgHereIsTheCheck(bill.price, bill.customer);
	}

	private void makeChange(Bill bill) {
		if(bill.cash - bill.price < 0){
			Do("Customer DO NOT HAVE ENOUGH MONEY.");
			bill.customer.msgYouDoNotHaveEnoughMoney();
			return;
		}
		Do("Giving change to customer");
		bill.customer.msgHereIsTheChange(bill.cash - bill.price);
	}

	//utilities

	public static class Bill {
		WaiterAgent waiter;
		CustomerAgent customer;
		String choice;
		double price;
		double cash;
		double change;
		public enum BillState
		{None, NotComputed, ReturnedFromCustomer};
		private BillState state = BillState.None;
		
		Bill(WaiterAgent waiter, CustomerAgent customer, String choice){
			this.choice = choice;
			this.waiter = waiter;
			this.customer = customer;
			this.price = menu.getPrice(this.choice);
			state = BillState.NotComputed;
		}
	}
}