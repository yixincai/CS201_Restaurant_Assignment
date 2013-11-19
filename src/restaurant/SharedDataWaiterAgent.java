package restaurant;

import restaurant.CookAgent.Order;

public class SharedDataWaiterAgent extends WaiterAgent{
	public SharedDataWaiterAgent(String name) {
		super(name);
	}

	@Override
	protected void processOrder(WaiterAgent.MyCustomer customer) {
		Do("Process order");
		customer.state = MyCustomer.CustomerState.none;
		DoGoToRevolvingStand();
		Do("Making a new order");
        Order data = new Order(this, customer.choice, customer.tableNumber, Order.OrderState.NotCooked);
        Do("Trying to put order on revolving stand");
        r.revolving_stand.insert(data);
	}
}

