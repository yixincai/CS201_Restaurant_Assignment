package restaurant;

import agent.Agent;
import restaurant.gui.*;
import restaurant.interfaces.*;
import restaurant.test.mock.EventLog;
import restaurant.test.mock.LoggedEvent;

import java.util.*;

public class CashierAgent extends Agent implements Cashier{
	public EventLog log = new EventLog();
    private String name = "Cashier";
	public List<CustomerBill> bills = Collections.synchronizedList(new ArrayList<CustomerBill>());
	public List<MarketBill> marketBills = Collections.synchronizedList(new ArrayList<MarketBill>());
	public static Menu menu = new Menu();
	public CashierGui cashierGui = null;
	public double money;
	
	public CashierAgent() {
		super();
		money = 200.0;
	}
	
	public void setGui(CashierGui g) {
		cashierGui = g;
	}
	
	public String getName() {
		return name;
	}
	
	// Messages
	public void msgComputeBill(Waiter w, Customer c, String choice) {
		log.add(new LoggedEvent("Received ComputeBill from waiter. Choice = "+ choice));
		Do("Bill Request received");
		bills.add(new CustomerBill(w,c,choice));
		stateChanged();
	}
	
	public void msgHereIsThePayment(Customer c, double check, double cash) {
		log.add(new LoggedEvent("Received HereIsTheCheck from customer. Check = "+ check + " Payment = "+ cash));
		Do("Payment received");
		for (CustomerBill bill : bills)
			if (bill.customer == c){
				bill.cash = cash;
				bill.price  = check;
				bill.state = CustomerBill.BillState.ReturnedFromCustomer;
				stateChanged();
			}
	}
	
	public void msgHereIsTheBill(Market m, double bill){
		log.add(new LoggedEvent("Received HereIsTheBill from market. Bill = "+ bill));
		Do("Market bill received with amount of " + bill);
		marketBills.add(new MarketBill(m,bill));
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		try{
			for (CustomerBill bill : bills){
				if (bill.state == CustomerBill.BillState.NotComputed){
					computeBill(bill);
					return true;
				}
			}
			for (CustomerBill bill : bills){
				if (bill.state == CustomerBill.BillState.ReturnedFromCustomer){
					makeChange(bill);
					bills.remove(bill);
					return true;
				}
			}
			if (marketBills.size()!=0 && money > 0){
				payMarketBill(marketBills.get(0));
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

	private void computeBill(CustomerBill bill) {
		Do("The Bills is computed.");
		bill.state = CustomerBill.BillState.None;
		bill.waiter.msgHereIsTheCheck(bill.price, bill.customer);
	}

	private void makeChange(CustomerBill bill) {
		if(bill.cash - bill.price < 0){
			Do("Customer DO NOT HAVE ENOUGH MONEY.");
			bill.customer.msgYouDoNotHaveEnoughMoney(bill.price - bill.cash);
			money += bill.cash;
			Do("Remaining money is " + money);
			return;
		}
		Do("Giving change to customer");
		money += bill.price;
		Do("Remaining money is " + money);
		bill.customer.msgHereIsTheChange(bill.cash - bill.price);
	}
	
	private void payMarketBill(MarketBill bill){
		Do("Paying Market Bill");
		if (money >= bill.balance){
			money -= bill.balance;
			Do("Remaining money is " + money);
			bill.market.msgHereIsThePayment(bill.balance);
			marketBills.remove(0);
		}
		else {
			marketBills.get(0).balance -= money;
			money = 0;
			Do("Do not have enough money with " + bill.balance +" debt");
			bill.market.msgHereIsThePayment(bill.balance);
		}
	}

	//utilities

	public static class CustomerBill {
		public Waiter waiter;
		public Customer customer;
		public String choice;
		public double price;
		public double cash;
		public double change;
		public enum BillState
		{None, NotComputed, ReturnedFromCustomer};
		public BillState state = BillState.None;
		
		CustomerBill(Waiter waiter, Customer customer, String choice){
			this.choice = choice;
			this.waiter = waiter;
			this.customer = customer;
			this.price = menu.getPrice(this.choice);
			state = BillState.NotComputed;
		}
	}
	
	public static class MarketBill {
		public double balance;
		public Market market;
		
		MarketBill(Market market, double money){
			this.balance = money;
			this.market = market;
		}
	}
}