package restaurant.interfaces;

import java.util.Map;

public interface Cook {
	public void msgHereIsTheOrder(Waiter w, String choice, int table);
	
	public void msgOrderFulfillment(Market m, Map<String, Integer> order);
}
