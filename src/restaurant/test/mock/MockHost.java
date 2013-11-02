package restaurant.test.mock;

import restaurant.interfaces.*;

public class MockHost extends Mock implements Host{
	public EventLog log = new EventLog();

	public MockHost(String name) {
		super(name);
	}
	
	public void msgIWantFood(Customer cust, int count){}

	public void msgIAmLeaving(Customer cust){}
	
	public void msgIWantToStay(Customer cust){}

	public void msgTableIsFree(Customer cust, int tablenumber){}
	
	public void msgWantToBreak(Waiter w){}
	
	public void msgWantToComeBack(Waiter w){}
}
