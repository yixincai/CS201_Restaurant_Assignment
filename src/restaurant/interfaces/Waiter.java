package restaurant.interfaces;

import restaurant.interfaces.Customer;

public interface Waiter {

	public abstract void msgSitAtTable(Customer cust, int tablenumber);

	public abstract void msgNoMoneyAndLeaving(Customer cust);

	public abstract void msgReadyToOrder(Customer cust);

	public abstract void msgHereIsTheChoice(Customer cust, String choice);

	public abstract void msgOrderIsReady(String choice, int tableNumber);

	public abstract void msgFoodRunsOut(String choice, int tableNumber);

	public abstract void msgDoneEating(Customer cust);
	
	public abstract void msgHereIsTheCheck(double money, Customer cust);

	public abstract void msgLeavingRestaurant(Customer cust);
	
	public void msgAskForBreak();

	public void msgBreakGranted();

	public void msgAskToComeBack();

}
