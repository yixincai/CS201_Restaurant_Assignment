package restaurant.interfaces;

public interface Host {

	public void msgIWantFood(Customer cust);

	public void msgIAmLeaving(Customer cust);
	
	public void msgIWantToStay(Customer cust);

	public void msgTableIsFree(Customer cust, int tablenumber);
	
	public void msgWantToBreak(Waiter w);
	
	public void msgWantToComeBack(Waiter w);

}
