package restaurant.interfaces;

import java.util.Map;

public interface Market {
	public void msgHereIsTheOrder(Map<String, Integer> order);
	
	public void msgHereIsThePayment(double s);
}
