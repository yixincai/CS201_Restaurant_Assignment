package restaurant.test.mock;

import restaurant.interfaces.*;

public class MockWaiter extends Mock implements Waiter{
	public EventLog log = new EventLog();
	
	public MockWaiter(String name) {
		super(name);
	}
	
	public void msgSitAtTable(Customer cust, int tablenumber, int count){}

	public void msgNoMoneyAndLeaving(Customer cust){}

	public void msgReadyToOrder(Customer cust){}

	public void msgHereIsTheChoice(Customer cust, String choice){}

	public void msgOrderIsReady(String choice, int tableNumber){}

	public void msgFoodRunsOut(String choice, int tableNumber){}

	public void msgDoneEating(Customer cust){}
	
	public void msgHereIsTheCheck(double money, Customer cust){
		log.add(new LoggedEvent("Received HereIsTheCheck from cashier. Check = "+ money));
	}

	public void msgLeavingRestaurant(Customer cust){}
	
	public void msgAskForBreak(){}

	public void msgBreakGranted(){}

	public void msgAskToComeBack(){}
	
}