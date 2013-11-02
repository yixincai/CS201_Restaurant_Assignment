package restaurant;

import restaurant.gui.CustomerGui;
import agent.Agent;
import restaurant.interfaces.*;
import restaurant.test.mock.EventLog;

import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Restaurant customer agent.
 */
public class CustomerAgent extends Agent implements Customer{
	public EventLog log = new EventLog();
	private String name;
	private int hungerLevel = 5;        // determines length of meal
	Timer timer = new Timer();
	private CustomerGui customerGui;
	private Semaphore atTable = new Semaphore(0,true);
	// agent correspondents
	private HostAgent host;
	private WaiterAgent waiter;
	private CashierAgent cashier = null;
	private Menu menu = null;
	private String choice;
	private double money, check, debt = 0;
	private int seatnumber;
	private static final long thinking_time = 3000, eating_time = 5000;
	Random r = new Random();
	public int count;
	
	public enum AgentState
	{DoingNothing, WaitingInRestaurant, StillWaitingInRestaurant, BeingSeated, Seated, ReadyToOrder, 
		GivenOrder, Eating, DoneEating, waitingForBill, waitingForChange, Leaving};
	private AgentState state = AgentState.DoingNothing;//The start state

	public enum AgentEvent 
	{none, gotHungry, noSeat, toLeave, toStay, followHost, seated, noMoney, doneThinking,
		orderFood, noFood, gotFood, doneEating, billArrived, changeArrived, badLuck, doneLeaving};
	AgentEvent event = AgentEvent.none;

	/**
	 * Constructor for CustomerAgent class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the customergui so the customer can send it messages
	 */
	public CustomerAgent(String name, int count){
		super();
		this.money = 0;
		this.name = name;
		this.count = count;
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
		if (name.equals("5"))
			money = 5;
		else if (name.equals("7"))
			money = 7;
		else if (name.equals("10"))
			money = 10;
		else if (name.equals("20"))
			money = 20;
		else
			money += r.nextInt(20);
		Do("I have " + this.money + " dollars.");
		event = AgentEvent.gotHungry;
		stateChanged();
	}
	
	public void msgNoSeat() {
		print("Told by host no seat available.");
		event = AgentEvent.noSeat;
		stateChanged();
	}

	public void msgFollowMe(Waiter w, int tablenumber, Menu menu) {
		if (w instanceof WaiterAgent){
			print("Received msgSitAtTable");
			this.waiter = (WaiterAgent)w;
			this.menu = menu;
			seatnumber = tablenumber;
			event = AgentEvent.followHost;
			stateChanged();
		}
	}
	
	public void msgNoFood(Menu menu){
		this.menu = menu;
		print("Told by waiter to rethink");
		event = AgentEvent.noFood;
		stateChanged();		
	}

	public void msgWhatWouldYouLike() {
		print("Received msgWhatWouldYouLike");
		event = AgentEvent.orderFood;
		stateChanged();
	}
	
	public void msgHereIsYourFood(String choice) {
		print("Received food " + choice);
		event = AgentEvent.gotFood;
		stateChanged();
	}
	
	public void msgHereIsTheCheck(double money, Cashier cashier){
		if (cashier instanceof CashierAgent){
			print("Received check of " + money);
			event = AgentEvent.billArrived;
			this.cashier = (CashierAgent) cashier;
			this.check = money;
			stateChanged();
		}
	}
	
	public void msgHereIsTheChange(double change){
		print("Received change of " + change);
		event = AgentEvent.changeArrived;
		this.money = change;
		stateChanged();
	}
	
	public void msgYouDoNotHaveEnoughMoney(double debt){
		this.money = 0;
		this.debt += debt;
		Do("My debt is " + this.debt);
		event = AgentEvent.badLuck;
		stateChanged();
	}
	
	public void msgAnimationFinishedGoToSeat() {
		//from animation
		event = AgentEvent.seated;
		stateChanged();
	}
	
	public void msgAnimationFinishedGoToCashier() {
		//from animation
		atTable.release();
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
	public boolean pickAndExecuteAnAction() {
		//	CustomerAgent is a finite state machine
		try{
			if (state == AgentState.DoingNothing && event == AgentEvent.gotHungry ){
				state = AgentState.WaitingInRestaurant;
				goToRestaurant();
				return true;
			}
			else if (state == AgentState.WaitingInRestaurant && event == AgentEvent.noSeat){
				ThinkAboutLeaving();
				return true;
			}
			else if (state == AgentState.StillWaitingInRestaurant && event == AgentEvent.followHost){
				state = AgentState.BeingSeated;
				SitDown();
				return true;
			}			
			else if (state == AgentState.WaitingInRestaurant && event == AgentEvent.followHost){
				state = AgentState.BeingSeated;
				SitDown();
				return true;
			}
			else if (state == AgentState.BeingSeated && event == AgentEvent.seated){
				state = AgentState.Seated;
				ThinkAboutMenu();
				return true;
			}
			else if (state == AgentState.Seated && event == AgentEvent.doneThinking){
				state = AgentState.ReadyToOrder;
				AskWaiterToPickUpOrder();
				return true;
			}
			else if (state == AgentState.Seated && event == AgentEvent.noMoney){
				state = AgentState.Leaving;
				leaveRestaurant();
				return true;
			}
			else if (state == AgentState.ReadyToOrder && event == AgentEvent.orderFood){
				state = AgentState.GivenOrder;
				GiveOrder();
				return true;
			}
			else if (state == AgentState.GivenOrder && event == AgentEvent.noFood){
				state = AgentState.Seated;
				RethinkAboutMenu();
				return true;
			}
			else if (state == AgentState.GivenOrder && event == AgentEvent.gotFood){
				state = AgentState.Eating;
				EatFood();
				return true;
			}
			else if (state == AgentState.Eating && event == AgentEvent.doneEating){
				state = AgentState.waitingForBill;
				askForBill();
				return true;
			}
			else if (state == AgentState.waitingForBill && event == AgentEvent.billArrived){
				state = AgentState.waitingForChange;
				askForChange();
				return true;
			}
			else if (state == AgentState.waitingForChange && event == AgentEvent.changeArrived){
				state = AgentState.Leaving;
				leaveRestaurant();
				return true;
			}
			else if (state == AgentState.waitingForChange && event == AgentEvent.badLuck){
				state = AgentState.Leaving;
				leaveRestaurant();
				return true;
			}
			else if (state == AgentState.Leaving && event == AgentEvent.doneLeaving){
				state = AgentState.DoingNothing;
				//no action
				return true;
			}
		}
		catch(ConcurrentModificationException e){
			return false;
		}
		return false;
	}

	// Actions

	private void goToRestaurant() {
		Do("Going to restaurant");
		customerGui.DoGoWaiting();
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		host.msgIWantFood(this,count);//send our instance, so he can respond to us
	}
	
	private void ThinkAboutLeaving(){
		Do("Think about whether to stay waiting or to leave.");
		int choice = r.nextInt(2);
		Do("The random number is " + choice);
		if (choice == 0){
			Do("I want to leave the restaurant");
			host.msgIAmLeaving(this);
			Do("I want to leave the restaurant");
			state = AgentState.Leaving;
			leaveRestaurant();
		}
		else{
			host.msgIWantToStay(this);
			Do("I want to stay in the restaurant");
			state = AgentState.StillWaitingInRestaurant;
		}
	}

	private void AskWaiterToPickUpOrder() {
		Do("I'm ready to order");
		waiter.msgReadyToOrder(this);//send our instance, so he can respond to us
	}
	
	private void ThinkAboutMenu() {
		Do("Thinking about Food");
		boolean hasAffordableFood = false;
		for(int i=0; i<menu.menu.size(); i++){
			if (menu.menu.get(i).price <= money)
				hasAffordableFood = true;
		}
		int choice = r.nextInt(2);
		Do("The choice to stay/leave is " + choice);
		if(!hasAffordableFood && choice == 1){
			waiter.msgNoMoneyAndLeaving(this);
			event = AgentEvent.noMoney;
			return;
		}
		int food_choice = r.nextInt(menu.menu.size());
		if (hasAffordableFood){
			while (menu.menu.get(food_choice).price > money){
				if (food_choice < r.nextInt(menu.menu.size()-1))
					food_choice++;
				else
					food_choice = 0;
			}
		}
		this.choice = menu.menu.get(food_choice).name;
		timer.schedule(new TimerTask() {
			public void run() {
				event = AgentEvent.doneThinking;
				stateChanged();
			}
		},
		thinking_time);
	}
	
	private void RethinkAboutMenu() {
		Do("Thinking about Food");
		boolean hasAffordableFood = false;
		for(int i=0; i<menu.menu.size(); i++){
			Do("The size of new menu is " + menu.menu.size());
			if (menu.menu.get(i).price <= money)
				hasAffordableFood = true;
		}
		if(!hasAffordableFood){
			waiter.msgNoMoneyAndLeaving(this);
			event = AgentEvent.noMoney;
			return;
		}
		int food_choice = r.nextInt(menu.menu.size());
		while (menu.menu.get(food_choice).price > money){
			if (food_choice < r.nextInt(menu.menu.size()-1))
				food_choice++;
			else
				food_choice = 0;
		}
		this.choice = menu.menu.get(food_choice).name;
		timer.schedule(new TimerTask() {
			public void run() {
				event = AgentEvent.doneThinking;
				stateChanged();
			}
		},
		thinking_time);
	}
	
	private void GiveOrder() {
		Do("Here is my order");
		customerGui.showOrderFood(this.choice);
		waiter.msgHereIsTheChoice(this, this.choice);//send our instance, so he can respond to us
	}
	
	private void SitDown() {
		Do("Being seated. Going to table");
		customerGui.DoGoToSeat(seatnumber);//hack; only one table
	}

	private void EatFood() {
		customerGui.eatFood(this.choice);
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
		eating_time);//getHungerLevel() * 1000);//how long to wait before running task
	}

	private void askForBill() {
		Do("Asking for bill");
		waiter.msgDoneEating(this);
	}
	
	private void askForChange() {
		Do("Leaving and Asking for change with money " + money);
		waiter.msgLeavingRestaurant(this);
		customerGui.DoGoToCashier();
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		cashier.msgHereIsThePayment(this, check+debt, money);
	}
	
	private void leaveRestaurant() {
		Do("Leaving restaurant");
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

