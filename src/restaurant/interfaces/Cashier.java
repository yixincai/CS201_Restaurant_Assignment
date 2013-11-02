package restaurant.interfaces;

public interface Cashier {

	public void msgComputeBill(Waiter w, Customer c, String choice);
	
	public void msgHereIsThePayment(Customer c, double check, double cash);
	
	public void msgHereIsTheBill(Market m, double bill);
}
