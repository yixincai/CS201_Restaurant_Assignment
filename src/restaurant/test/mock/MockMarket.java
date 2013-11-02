package restaurant.test.mock;

import java.util.Map;

import restaurant.interfaces.*;

public class MockMarket extends Mock implements Market{
	public EventLog log = new EventLog();

	public MockMarket(String name) {
		super(name);
	}
	
	public void msgHereIsTheOrder(Map<String, Integer> order){};
	
	public void msgHereIsThePayment(double s){
		log.add(new LoggedEvent("Received HereIsThePayment from cashier. Check = "+ s));
	}
}
