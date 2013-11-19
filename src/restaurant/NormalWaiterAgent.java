package restaurant;

public class NormalWaiterAgent extends WaiterAgent{

	public NormalWaiterAgent(String name) {
		super(name);
	}

	@Override
	protected void processOrder(MyCustomer customer) {
		Do("Process order");
		customer.state = MyCustomer.CustomerState.none;
		DoGoToCook();
		cook.msgHereIsTheOrder(this, customer.choice, customer.tableNumber);
	}
}

