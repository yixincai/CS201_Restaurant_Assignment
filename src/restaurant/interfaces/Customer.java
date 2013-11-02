package restaurant.interfaces;

import restaurant.interfaces.Waiter;
import restaurant.interfaces.Cashier;
import restaurant.Menu;

/**
 * A sample Customer interface built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public interface Customer {

	public void gotHungry();
	
	public void msgNoSeat();

	public void msgFollowMe(Waiter w, int tablenumber, Menu menu);
	
	public void msgNoFood(Menu menu);

	public void msgWhatWouldYouLike();
	
	public void msgHereIsYourFood(String choice);
	
	public void msgHereIsTheCheck(double money, Cashier cashier);
	
	public void msgHereIsTheChange(double change);
	
	public void msgYouDoNotHaveEnoughMoney(double debt);
	
	public void msgAnimationFinishedGoToSeat();
	
	public void msgAnimationFinishedGoToCashier();
	
	public void msgAnimationFinishedLeaveRestaurant();

}