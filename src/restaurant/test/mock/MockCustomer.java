package restaurant.test.mock;

import restaurant.Menu;
import restaurant.interfaces.Cashier;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Waiter;

public class MockCustomer extends Mock implements Customer {

	public EventLog log = new EventLog();

	public MockCustomer(String name) {
		super(name);
	}
	
	public void gotHungry(){};
	
	public void msgNoSeat(){};

	public void msgFollowMe(Waiter w, int tablenumber, Menu menu){};
	
	public void msgNoFood(Menu menu){};

	public void msgWhatWouldYouLike(){};
	
	public void msgHereIsYourFood(String choice){};
	
	public void msgHereIsTheCheck(double money, Cashier cashier){};
	
	public void msgHereIsTheChange(double change){
		log.add(new LoggedEvent("Received HereIsTheChange from cashier. Change = "+ change));
	}
	
	public void msgYouDoNotHaveEnoughMoney(double debt){
		log.add(new LoggedEvent("Received YouDoNotHaveEnoughMoney from cashier. Debt = "+ debt));
	}
	
	public void msgAnimationFinishedGoToSeat(){};
	
	public void msgAnimationFinishedGoToCashier(){};
	
	public void msgAnimationFinishedLeaveRestaurant(){};
/*
	@Override
	public void HereIsYourTotal(double total) {
		log.add(new LoggedEvent("Received HereIsYourTotal from cashier. Total = "+ total));

		if(this.name.toLowerCase().contains("thief")){
			//test the non-normative scenario where the customer has no money if their name contains the string "theif"
			cashier.IAmShort(this, 0);

		}else if (this.name.toLowerCase().contains("rich")){
			//test the non-normative scenario where the customer overpays if their name contains the string "rich"
			cashier.HereIsMyPayment(this, Math.ceil(total));

		}else{
			//test the normative scenario
			cashier.HereIsMyPayment(this, total);
		}
	}

	@Override
	public void HereIsYourChange(double total) {
		log.add(new LoggedEvent("Received HereIsYourChange from cashier. Change = "+ total));
	}

	@Override
	public void YouOweUs(double remaining_cost) {
		log.add(new LoggedEvent("Received YouOweUs from cashier. Debt = "+ remaining_cost));
	}*/

}
