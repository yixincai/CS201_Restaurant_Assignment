package restaurant.test.mock;

import java.util.Map;

import restaurant.interfaces.*;

public class MockCook extends Mock implements Cook{
	public EventLog log = new EventLog();

	public MockCook(String name) {
		super(name);
	}
	
	public void msgHereIsTheOrder(Waiter w, String choice, int table){};
	
	public void msgOrderFulfillment(Market m, Map<String, Integer> order){};
}
