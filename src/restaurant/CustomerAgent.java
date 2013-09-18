package restaurant;

import restaurant.gui.CustomerGui;
import restaurant.gui.RestaurantGui;
import agent.Agent;
import restaurant.WaiterAgent;
import restaurant.HostAgent;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Restaurant customer agent.
 */
public class CustomerAgent extends Agent {
	private String name;
	private int hungerLevel = 5;        // determines length of meal
	Timer timer = new Timer();
	private CustomerGui customerGui;

	// agent correspondents
	private HostAgent host;
	private WaiterAgent waiter;
	
	private int seatnumber;
	//    private boolean isHungry = false; //hack for gui
	public enum AgentState
	{DoingNothing, WaitingInRestaurant, BeingSeated, Seated, ReadyToOrder, Eating, DoneEating, Leaving};
	private AgentState state = AgentState.DoingNothing;//The start state

	public enum AgentEvent 
	{none, gotHungry, followHost, orderFood, gotFood, seated, doneEating, doneLeaving};
	AgentEvent event = AgentEvent.none;

	/**
	 * Constructor for CustomerAgent class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the customergui so the customer can send it messages
	 */
	public CustomerAgent(String name){
		super();
		this.name = name;
	}

	/**
	 * hack to establish connection to Host agent.
	 */
	public void setHost(HostAgent host) {
		this.host = host;
	}
	
	public void setWaiter(WaiterAgent w) {
		this.waiter = w;
	}
	
	public String getCustomerName() {
		return name;
	}
	// Messages

	public void gotHungry() {//from animation
		print("I'm hungry");
		event = AgentEvent.gotHungry;
		stateChanged();
	}

	public void msgFollowMe(WaiterAgent w, int tablenumber) {
		print("Received msgSitAtTable");
		this.waiter = w;
		seatnumber = tablenumber;
		event = AgentEvent.followHost;
		stateChanged();
	}

	public void msgWhatWouldYouLike() {
		print("Received msgWhatWouldYouLike");
		event = AgentEvent.orderFood;
		stateChanged();
	}
	
	public void msgHereIsYourFood(String choice) {
		print("Received food" + choice);
		event = AgentEvent.gotFood;
		stateChanged();
	}
	
	public void msgAnimationFinishedGoToSeat() {
		//from animation
		event = AgentEvent.seated;
		stateChanged();
	}
	public void msgAnimationFinishedLeaveRestaurant() {
		//from animation
		event = AgentEvent.doneLeaving;
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		//	CustomerAgent is a finite state machine

		if (state == AgentState.DoingNothing && event == AgentEvent.gotHungry ){
			state = AgentState.WaitingInRestaurant;
			goToRestaurant();
			return true;
		}
		else if (state == AgentState.WaitingInRestaurant && event == AgentEvent.followHost ){
			state = AgentState.BeingSeated;
			SitDown();
			return true;
		}
		else if (state == AgentState.BeingSeated && event == AgentEvent.seated){
			state = AgentState.Seated;
			AskWaiterToPickUpOrder();
			return true;
		}
		else if (state == AgentState.Seated && event == AgentEvent.orderFood){
			state = AgentState.ReadyToOrder;
			GiveOrder();
			return true;
		}	
		else if (state == AgentState.ReadyToOrder && event == AgentEvent.gotFood){
			state = AgentState.Eating;
			EatFood();
			return true;
		}
		else if (state == AgentState.Eating && event == AgentEvent.doneEating){
			state = AgentState.Leaving;
			leaveTable();
			return true;
		}
		else if (state == AgentState.Leaving && event == AgentEvent.doneLeaving){
			state = AgentState.DoingNothing;
			//no action
			return true;
		}
		return false;
	}

	// Actions

	private void goToRestaurant() {
		Do("Going to restaurant");
		host.msgIWantFood(this);//send our instance, so he can respond to us
	}

	private void AskWaiterToPickUpOrder() {
		Do("I'm ready to order");
		waiter.msgReadyToOrder(this);//send our instance, so he can respond to us
	}
	
	private void GiveOrder() {
		Do("Here is my order");
		waiter.msgHereIsTheChoice(this, "Steak");//send our instance, so he can respond to us
	}
	
	private void SitDown() {
		Do("Being seated. Going to table");
		customerGui.DoGoToSeat(seatnumber);//hack; only one table
	}

	private void EatFood() {
		Do("Eating Food");
		//This next complicated line creates and starts a timer thread.
		//We schedule a deadline of getHungerLevel()*1000 milliseconds.
		//When that time elapses, it will call back to the run routine
		//located in the anonymous class created right there inline:
		//TimerTask is an interface that we implement right there inline.
		//Since Java does not all us to pass functions, only objects.
		//So, we use Java syntactic mechanism to create an
		//anonymous inner class that has the public method run() in it.
		timer.schedule(new TimerTask() {
			Object cookie = 1;
			public void run() {
				print("Done eating, cookie=" + cookie);
				event = AgentEvent.doneEating;
				//isHungry = false;
				stateChanged();
			}
		},
		5000);//getHungerLevel() * 1000);//how long to wait before running task
	}

	private void leaveTable() {
		Do("Leaving.");
		waiter.msgDoneEating(this);
		customerGui.DoExitRestaurant();
	}

	// Accessors, etc.

	public String getName() {
		return name;
	}
	
	public int getHungerLevel() {
		return hungerLevel;
	}

	public void setHungerLevel(int hungerLevel) {
		this.hungerLevel = hungerLevel;
		//could be a state change. Maybe you don't
		//need to eat until hunger lever is > 5?
	}

	public String toString() {
		return "customer " + getName();
	}

	public void setGui(CustomerGui g) {
		customerGui = g;
	}

	public CustomerGui getGui() {
		return customerGui;
	}
}

