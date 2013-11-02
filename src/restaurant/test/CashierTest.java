package restaurant.test;

import restaurant.CashierAgent;
import restaurant.test.mock.*;

import java.util.*;

import junit.framework.*;

public class CashierTest extends TestCase
{
	//these are instantiated for each test separately via the setUp() method.
	CashierAgent cashier;
	MockWaiter waiter1, waiter2;
	MockCustomer customer1, customer2;
	MockMarket market1, market2;
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();		
		cashier = new CashierAgent();		
		customer1 = new MockCustomer("mockcustomer");
		customer2 = new MockCustomer("mockcustomer2");	
		waiter1 = new MockWaiter("Mike");
		waiter2 = new MockWaiter("Fiona");
		market1 = new MockMarket("Market1");
		market2 = new MockMarket("Market2");
	}	
	/**
	 * This tests the cashier under very simple terms: one customer is ready to pay the exact bill.
	 */
	
	public void testOneNormalMarketScenario()
	{			
		//check preconditions
		assertEquals("Cashier should have 0 bills in it. It doesn't.",cashier.bills.size(), 0);
		assertEquals("Cashier should have 0 marketBills in it. It doesn't.",cashier.marketBills.size(), 0);
		assertEquals("CashierAgent should have an empty event log before the Cashier's HereIsTheBill is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());
		assertEquals("MockMarket 1 should have an empty event log before the Cashier's scheduler is called. Instead, the Cashier's event log reads: "
				+ market1.log.toString(), 0, market1.log.size());
		assertEquals("CashierAgent should have 130 dollars initially. Instead, the Cashier's balance is: "
				+ cashier.money, 130.0, cashier.money);	
		
		//send first message to cashier
		cashier.msgHereIsTheBill(market1, 100);//send the message from a waiter
		assertEquals("Cashier should have 1 market bill in it. It doesn't.",cashier.marketBills.size(), 1);	
		assertEquals("Cashier should have 0 customer bill in it. It doesn't.",cashier.bills.size(), 0);
		assertEquals("Bill should have the same market. It doesn't.",cashier.marketBills.get(0).market, market1);
		assertEquals("Bill should have the same amount. It doesn't.",cashier.marketBills.get(0).balance, 100.0);
		assertEquals("CashierAgent should have one line after the Cashier's ComputeBill is called. Instead, the Cashier's event log reads: "
				+ cashier.log.toString(), 1, cashier.log.size());		
		assertEquals("MockMarket should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ market1.log.toString(), 0, market1.log.size());

		//run scheduler
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		assertEquals("Cashier should have 0 market bill in it after the sceduler has been run. It doesn't.",cashier.marketBills.size(), 0);
		assertEquals("CashierAgent should have one event in log after scheduler is called. Instead, the Cashier's event log reads: "
				+ cashier.log.toString(), 1, cashier.log.size());
		assertEquals("MockMarket should have an event in log after the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ market1.log.toString(), 1, market1.log.size());
		assertEquals("CashierAgent should have 30 remaining dollars. Instead, the Cashier's balance is: "
				+ cashier.money, 30.0, cashier.money);	
		
		//check postconditions for step 1 and preconditions for step 2
		assertFalse("Cashier's scheduler should have returned false (no actions to do), but didn't.", cashier.pickAndExecuteAnAction());
		assertEquals("CashierAgent should have one event in log after scheduler is called. Instead, the Cashier's event log reads: "
				+ cashier.log.toString(), 1, cashier.log.size());
		
	}//end one normal market scenario	
	

	public void testTwoNormalMarketScenario()
	{			
		//check preconditions
		assertEquals("Cashier should have 0 bills in it. It doesn't.",cashier.bills.size(), 0);
		assertEquals("Cashier should have 0 marketBills in it. It doesn't.",cashier.marketBills.size(), 0);
		assertEquals("CashierAgent should have an empty event log before the Cashier's HereIsPayment is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());
		assertEquals("CashierAgent should have 130 dollars initially. Instead, the Cashier's balance is: "
				+ cashier.money, 130.0, cashier.money);		
		assertEquals("Market1 should have an empty event log before the Cashier's scheduler is called. Instead, Market1's event log reads: "
				+ market1.log.toString(), 0, market1.log.size());
		assertEquals("Market2 should have an empty event log before the Cashier's scheduler is called. Instead, Market2's event log reads: "
				+ market2.log.toString(), 0, market2.log.size());
		
		//send first message to cashier
		cashier.msgHereIsTheBill(market1, 50);//send the message from a waiter
		assertEquals("Cashier should have 1 market bill in it. It doesn't.",cashier.marketBills.size(), 1);	
		assertEquals("Cashier should have 0 customer bill in it. It doesn't.",cashier.bills.size(), 0);
		assertEquals("Bill should have the same waiter. It doesn't.",cashier.marketBills.get(0).market, market1);
		assertEquals("Bill should have the NotComputed state. It doesn't.",cashier.marketBills.get(0).balance, 50.0);
		assertEquals("CashierAgent should have one line after the Cashier's ComputeBill is called. Instead, the Cashier's event log reads: "
				+ cashier.log.toString(), 1, cashier.log.size());		
		assertEquals("MockMarket should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ market1.log.toString(), 0, market1.log.size());

		//send second message to cashier
		cashier.msgHereIsTheBill(market2, 60);//send the message from a waiter
		assertEquals("Cashier should have 2 market bill in it. It doesn't.",cashier.marketBills.size(), 2);	
		assertEquals("Bill should have the same market. It doesn't.",cashier.marketBills.get(1).market, market2);
		assertEquals("Bill should have the same value. It doesn't.",cashier.marketBills.get(1).balance, 60.0);
		assertEquals("CashierAgent should have two line after the Cashier's HereIsTheBill is called. Instead, the Cashier's event log reads: "
				+ cashier.log.toString(), 2, cashier.log.size());
		assertEquals("MockMarket should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ market2.log.toString(), 0, market2.log.size());		
		
		//run scheduler
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		assertEquals("Cashier should have 1 market bill in it. It doesn't.",cashier.marketBills.size(), 1);
		assertEquals("MockMarket 1 should have an event in log after the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ market1.log.toString(), 1, market1.log.size());
		assertEquals("MockMarket 2 should have no event in log after the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
				+ market2.log.toString(), 0, market2.log.size());
		assertEquals("CashierAgent should have 80 remaining dollars. Instead, the Cashier's balance is: "
				+ cashier.money, 80.0, cashier.money);	
		
		//run scheduler again to process the second bill
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		assertEquals("Cashier should have 0 market bill in it. It doesn't.",cashier.marketBills.size(), 0);
		assertEquals("MockMarket 2 should have an event in log after the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ market2.log.toString(), 1, market2.log.size());		
		assertEquals("MockMarket 1 should have no new event in log after the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
				+ market1.log.toString(), 1, market1.log.size());
		assertEquals("CashierAgent should have 20 remaining dollars. Instead, the Cashier's balance is: "
				+ cashier.money, 20.0, cashier.money);	
		
		//postconditions
		assertFalse("Cashier's scheduler should have returned false (no actions to do), but didn't.", cashier.pickAndExecuteAnAction());
		assertEquals("CashierAgent should have two former event in log after scheduler is called. Instead, the Cashier's event log reads: "
				+ cashier.log.toString(), 2, cashier.log.size());
		
		
	}//end two normal market scenario	
	
	public void testOneNonNormalMarketScenario()
	{			
		//check preconditions
		assertEquals("Cashier should have 0 bills in it. It doesn't.",cashier.bills.size(), 0);
		assertEquals("Cashier should have 0 marketBills in it. It doesn't.",cashier.marketBills.size(), 0);
		assertEquals("CashierAgent should have an empty event log before the Cashier's HereIsPayment is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());
		assertEquals("MockMarket 1 should have an empty event log before the Cashier's scheduler is called. Instead, the Cashier's event log reads: "
				+ market1.log.toString(), 0, market1.log.size());
		assertEquals("Cashier should have 130.0. It doesn't.",cashier.money, 130.0);
		
		//send first message to cashier
		cashier.msgHereIsTheBill(market1, 210);//send the message from a waiter
		assertEquals("Cashier should have 1 market bill in it. It doesn't.",cashier.marketBills.size(), 1);	
		assertEquals("Cashier should have 0 customer bill in it. It doesn't.",cashier.bills.size(), 0);
		assertEquals("Bill should have the same market. It doesn't.",cashier.marketBills.get(0).market, market1);
		assertEquals("Bill should have the same amount. It doesn't.",cashier.marketBills.get(0).balance, 210.0);
		assertEquals("CashierAgent should have one line after the Cashier's HereIsTheBill is called. Instead, the Cashier's event log reads: "
				+ cashier.log.toString(), 1, cashier.log.size());
		assertEquals("MockMarket should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ market1.log.toString(), 0, market1.log.size());

		//run scheduler
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		assertEquals("Cashier should still have 1 market bill in it. It doesn't.",cashier.marketBills.size(), 1);
		assertEquals("CashierAgent should have one event in log after scheduler is called. Instead, the Cashier's event log reads: "
				+ cashier.log.toString(), 1, cashier.log.size());
		assertEquals("Bill should have the remaining amount of 10.0. It doesn't.",cashier.marketBills.get(0).balance, 80.0);
		assertEquals("MockMarket should have an event in log after the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ market1.log.toString(), 1, market1.log.size());
		assertEquals("Cashier should have 0.0. It doesn't.",cashier.money, 0.0);

		
		//check postconditions for step 1 and preconditions for step 2
		assertFalse("Cashier's scheduler should have returned false (no actions to do), but didn't.", cashier.pickAndExecuteAnAction());

		//add money to cashier
		cashier.money = 100.0;
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		assertEquals("Cashier should still have 0 market bill in it. It doesn't.",cashier.marketBills.size(), 0);
		assertEquals("CashierAgent should have one event in log after scheduler is called. Instead, the Cashier's event log reads: "
				+ cashier.log.toString(), 1, cashier.log.size());
		assertEquals("Cashier should have 200.0. It doesn't.",cashier.money, 20.0);
		assertEquals("MockMarket should have two events in log after the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ market1.log.toString(), 2, market1.log.size());
		
	}//end one normal market scenario	
	
	public void testOneNormalCustomerScenario()
	{
		//setUp() runs first before this test!
			
		//check preconditions
		assertEquals("Cashier should have 0 bills in it. It doesn't.",cashier.bills.size(), 0);
		assertEquals("Cashier should have 0 marketBills in it. It doesn't.",cashier.marketBills.size(), 0);
		assertEquals("CashierAgent should have an empty event log before the Cashier's HereIsBill is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());

		//send first message to cashier
		cashier.msgComputeBill(waiter1, customer1, "Steak");//send the message from a waiter
		assertEquals("Cashier should have 1 bills in it. It doesn't.",cashier.bills.size(), 1);
		assertEquals("Bill should have the same waiter. It doesn't.",cashier.bills.get(0).waiter, waiter1);
		assertEquals("Bill should have the same customer. It doesn't.",cashier.bills.get(0).customer, customer1);
		assertEquals("Bill should have the NotComputed state. It doesn't.",cashier.bills.get(0).state, CashierAgent.CustomerBill.BillState.NotComputed);
		assertEquals("Bill should have the correct choice as Steak. It doesn't.",cashier.bills.get(0).choice, "Steak");		
		assertEquals("CashierAgent should have one line after the Cashier's ComputeBill is called. Instead, the Cashier's event log reads: "
				+ cashier.log.toString(), 1, cashier.log.size());		
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ waiter1.log.toString(), 0, waiter1.log.size());

		//run scheduler
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		assertEquals("Cashier should have 1 bills in it. It doesn't.",cashier.bills.size(), 1);
		assertEquals("CashierAgent should have one event in log after scheduler is called. Instead, the Cashier's event log reads: "
				+ cashier.log.toString(), 1, cashier.log.size());		
		assertEquals("MockWaiter should have an event in log after the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ waiter1.log.toString(), 1, waiter1.log.size());
		assertEquals("Bill should have the None state. It doesn't.",cashier.bills.get(0).state, CashierAgent.CustomerBill.BillState.None);
		assertEquals("Bill should have the correct price of the food. It doesn't.",cashier.bills.get(0).price, 15.99);
		
		//check postconditions for step 1 and preconditions for step 2
		assertFalse("Cashier's scheduler should have returned false (no actions to do), but didn't.", cashier.pickAndExecuteAnAction());
		assertEquals("CashierAgent should have one event in log after scheduler is called. Instead, the Cashier's event log reads: "
				+ cashier.log.toString(), 1, cashier.log.size());
		
		//check preconditions for step 2
		assertEquals("MockCustomer should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
				+ customer1.log.toString(), 0, customer1.log.size());
		
		//step 2 of the test
		cashier.msgHereIsThePayment(customer1, 15.99, 20.0);
		assertEquals("Cashier should have 1 bills in it. It doesn't.",cashier.bills.size(), 1);
		assertEquals("Bill should have the same customer. It doesn't.",cashier.bills.get(0).customer, customer1);
		assertEquals("Bill should have the ReturnedFromCustomer state. It doesn't.",cashier.bills.get(0).state, CashierAgent.CustomerBill.BillState.ReturnedFromCustomer);
		assertEquals("Bill should have the correct payment. It doesn't.",cashier.bills.get(0).cash, 20.0);
		assertEquals("CashierAgent should have one line after the Cashier's ComputeBill is called. Instead, the Cashier's event log reads: "
				+ cashier.log.toString(), 2, cashier.log.size());
		assertEquals("MockCustomer should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
				+ customer1.log.toString(), 0, customer1.log.size());
		
		//run scheduler
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		assertEquals("There should be no bills in it. It doesn't.",cashier.bills.size(), 0);
		assertEquals("CashierAgent should have one event in log after scheduler is called. Instead, the Cashier's event log reads: "
				+ cashier.log.toString(), 2, cashier.log.size());		
		assertEquals("MockCustomer should have an event in log after the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ customer1.log.toString(), 1, customer1.log.size());
		assertTrue("MockCustomer should have logged an event for receiving \"HereIsYourChange\" with the correct change, but his last event logged reads instead: " 
				+ customer1.log.getLastLoggedEvent().toString(), customer1.log.containsString("Received HereIsTheChange from cashier. Change = " + 4.01));
		
		//check postconditions for step 1 and preconditions for step 2
		assertFalse("Cashier's scheduler should have returned false (no actions to do), but didn't.", cashier.pickAndExecuteAnAction());

	}//end one normal customer scenario
	
	public void testTwoNormalCustomerScenario()
	{
		//setUp() runs first before this test!
			
		//check preconditions
		assertEquals("Cashier should have 0 bills in it. It doesn't.",cashier.bills.size(), 0);
		assertEquals("Cashier should have 0 marketBills in it. It doesn't.",cashier.marketBills.size(), 0);
		assertEquals("CashierAgent should have an empty event log before the Cashier's HereIsBill is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());
		assertEquals("MockCustomer 1 should have an empty event log before the Cashier's HereIsBill is called. Instead, the Cashier's event log reads: "
				+ customer1.log.toString(), 0, customer1.log.size());
		assertEquals("MockCustomer 2 should have an empty event log before the Cashier's HereIsBill is called. Instead, the Cashier's event log reads: "
				+ customer2.log.toString(), 0, customer2.log.size());
		assertEquals("MockWaiter 1 should have an empty event log before the Cashier's HereIsBill is called. Instead, the Cashier's event log reads: "
				+ waiter1.log.toString(), 0, waiter1.log.size());
		assertEquals("MockWaiter 2 should have an empty event log before the Cashier's HereIsBill is called. Instead, the Cashier's event log reads: "
						+ waiter2.log.toString(), 0, waiter2.log.size());
		
		//send first message to cashier
		cashier.msgComputeBill(waiter1, customer1, "Salad");//send the message from a waiter
		assertEquals("Cashier should have 0 marketBills in it. It doesn't.",cashier.marketBills.size(), 0);		
		assertEquals("Cashier should have 1 bills in it. It doesn't.",cashier.bills.size(), 1);
		assertEquals("Bill should have the same waiter. It doesn't.",cashier.bills.get(0).waiter, waiter1);
		assertEquals("Bill should have the same customer. It doesn't.",cashier.bills.get(0).customer, customer1);
		assertEquals("Bill should have the NotComputed state. It doesn't.",cashier.bills.get(0).state, CashierAgent.CustomerBill.BillState.NotComputed);
		assertEquals("Bill should have the correct choice as Steak. It doesn't.",cashier.bills.get(0).choice, "Salad");		
		assertEquals("CashierAgent should have one line after the Cashier's ComputeBill is called. Instead, the Cashier's event log reads: "
				+ cashier.log.toString(), 1, cashier.log.size());
		assertTrue("Cashier should have logged an event for receiving \"HereIsYourChange\" with the correct change, but his last event logged reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received ComputeBill from waiter. Choice = Salad"));
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ waiter1.log.toString(), 0, waiter1.log.size());
		
		//send second message to cashier
		cashier.msgComputeBill(waiter2, customer2, "Pizza");//send the message from a waiter
		assertEquals("Cashier should have 2 bills in it. It doesn't.",cashier.bills.size(), 2);
		assertEquals("Bill should have the same waiter. It doesn't.",cashier.bills.get(1).waiter, waiter2);
		assertEquals("Bill should have the same customer. It doesn't.",cashier.bills.get(1).customer, customer2);
		assertEquals("Bill should have the NotComputed state. It doesn't.",cashier.bills.get(1).state, CashierAgent.CustomerBill.BillState.NotComputed);
		assertEquals("Bill should have the correct choice as Steak. It doesn't.",cashier.bills.get(1).choice, "Pizza");		
		assertEquals("CashierAgent should have two line after the Cashier's ComputeBill is called. Instead, the Cashier's event log reads: "
				+ cashier.log.toString(), 2, cashier.log.size());
		assertTrue("Cashier should have logged an event for receiving \"HereIsYourChange\" with the correct change, but his last event logged reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received ComputeBill from waiter. Choice = Pizza"));
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ waiter1.log.toString(), 0, waiter1.log.size());		

		//run scheduler
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		assertEquals("Cashier should have 2 bills in it. It doesn't.",cashier.bills.size(), 2);
		assertEquals("MockWaiter should have an event in log after the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ waiter1.log.toString(), 1, waiter1.log.size());
		assertTrue("Waiter 1 should have logged an event for receiving \"HereIsYourChange\" with the correct change, but his last event logged reads instead: " 
				+ waiter1.log.getLastLoggedEvent().toString(), waiter1.log.containsString("Received HereIsTheCheck from cashier. Check = "+ 5.99));
		assertEquals("Bill should have the None state. It doesn't.",cashier.bills.get(0).state, CashierAgent.CustomerBill.BillState.None);
		assertEquals("Bill should have the correct price of the food. It doesn't.",cashier.bills.get(0).price, 5.99);
		
		//run scheduler again
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		assertEquals("Cashier should have 2 bills in it. It doesn't.",cashier.bills.size(), 2);
		assertEquals("CashierAgent should have two events in log after scheduler is called. Instead, the Cashier's event log reads: "
				+ cashier.log.toString(), 2, cashier.log.size());
		assertEquals("MockWaiter should have an event in log after the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ waiter2.log.toString(), 1, waiter1.log.size());
		assertTrue("MockWaiter2 should have logged an event for receiving \"HereIsYourChange\" with the correct change, but his last event logged reads instead: " 
				+ waiter2.log.getLastLoggedEvent().toString(), waiter2.log.containsString("Received HereIsTheCheck from cashier. Check = "+ 8.99));
		assertEquals("Bill should have the None state. It doesn't.",cashier.bills.get(1).state, CashierAgent.CustomerBill.BillState.None);
		assertEquals("Bill should have the correct price of the food. It doesn't.",cashier.bills.get(1).price, 8.99);		
		
		//check postconditions for step 1 and preconditions for step 2
		assertFalse("Cashier's scheduler should have returned false (no actions to do), but didn't.", cashier.pickAndExecuteAnAction());
		assertEquals("CashierAgent should have one event in log after scheduler is called. Instead, the Cashier's event log reads: "
				+ cashier.log.toString(), 2, cashier.log.size());
		
		//check preconditions for step 2
		assertEquals("MockCustomer 1 should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
				+ customer1.log.toString(), 0, customer1.log.size());
		assertEquals("MockCustomer 2 should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
				+ customer2.log.toString(), 0, customer2.log.size());
		
		//step 2 of the test
		cashier.msgHereIsThePayment(customer1, 5.99, 10.0);
		assertEquals("Cashier should have 2 bills in it. It doesn't.",cashier.bills.size(), 2);
		assertEquals("Bill should have the same customer. It doesn't.",cashier.bills.get(0).customer, customer1);
		assertEquals("Bill should have the ReturnedFromCustomer state. It doesn't.",cashier.bills.get(0).state, CashierAgent.CustomerBill.BillState.ReturnedFromCustomer);
		assertEquals("Bill should have the correct payment. It doesn't.",cashier.bills.get(0).cash, 10.0);
		assertEquals("CashierAgent should have three line after the Cashier's ComputeBill is called. Instead, the Cashier's event log reads: "
				+ cashier.log.toString(), 3, cashier.log.size());
		assertTrue("Cashier should have logged an event for receiving \"HereIsTheCheck\" with the correct change, but his last event logged reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received HereIsTheCheck from customer. Check = "+ 5.99 + " Payment = "+ 10.0));
		assertEquals("MockCustomer 1 should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
				+ customer1.log.toString(), 0, customer1.log.size());

		cashier.msgHereIsThePayment(customer2, 8.99, 15);
		assertEquals("Cashier should have 2 bills in it. It doesn't.",cashier.bills.size(), 2);
		assertEquals("Bill should have the same customer. It doesn't.",cashier.bills.get(1).customer, customer2);
		assertEquals("Bill should have the ReturnedFromCustomer state. It doesn't.",cashier.bills.get(1).state, CashierAgent.CustomerBill.BillState.ReturnedFromCustomer);
		assertEquals("Bill should have the correct payment. It doesn't.",cashier.bills.get(1).cash, 15.0);
		assertEquals("CashierAgent should have four lines after the Cashier's ComputeBill is called. Instead, the Cashier's event log reads: "
				+ cashier.log.toString(), 4, cashier.log.size());
		assertTrue("Cashier should have logged an event for receiving \"HereIsTheCheck\" with the correct change, but his last event logged reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received HereIsTheCheck from customer. Check = "+ 8.99 + " Payment = "+ 15.0));
		assertEquals("MockCustomer 2 should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
				+ customer2.log.toString(), 0, customer2.log.size());
		
		//run scheduler
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		assertEquals("There should be one remaining bills in it. It doesn't.",cashier.bills.size(), 1);
		assertEquals("CashierAgent should have four events in log after scheduler is called. Instead, the Cashier's event log reads: "
				+ cashier.log.toString(), 4, cashier.log.size());
		assertEquals("MockCustomer 1 should have an event in log after the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ customer1.log.toString(), 1, customer1.log.size());
		assertTrue("MockCustomer should have logged an event for receiving \"HereIsYourChange\" with the correct change, but his last event logged reads instead: " 
				+ customer1.log.getLastLoggedEvent().toString(), customer1.log.containsString("Received HereIsTheChange from cashier. Change = " + 4.01));
		assertEquals("MockCustomer 2 should have no event in log after the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
				+ customer2.log.toString(), 0, customer2.log.size());
		
		//run scheduler again
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		assertEquals("There should be no bills in it. It doesn't.",cashier.bills.size(), 0);
		assertEquals("MockCustomer 2 should have an event in log after the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ customer2.log.toString(), 1, customer2.log.size());
		assertTrue("MockCustomer should have logged an event for receiving \"HereIsYourChange\" with the correct change, but his last event logged reads instead: " 
				+ customer2.log.getLastLoggedEvent().toString(), customer2.log.containsString("Received HereIsTheChange from cashier. Change = " + 6.01));
		assertEquals("MockCustomer 1 should have an event in log after the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
				+ customer1.log.toString(), 1, customer1.log.size());
		
		//check postconditions for step 1 and preconditions for step 2
		assertFalse("Cashier's scheduler should have returned false (no actions to do), but didn't.", cashier.pickAndExecuteAnAction());

	}//end two normal customers scenario
	
	public void testOneNonNormalCustomerScenario()
	{
		//setUp() runs first before this test!
			
		//check preconditions
		assertEquals("Cashier should have 0 bills in it. It doesn't.",cashier.bills.size(), 0);
		assertEquals("Cashier should have 0 marketBills in it. It doesn't.",cashier.marketBills.size(), 0);
		assertEquals("CashierAgent should have an empty event log before the Cashier's HereIsBill is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());

		//send first message to cashier
		cashier.msgComputeBill(waiter1, customer1, "Steak");//send the message from a waiter
		assertEquals("Cashier should have 1 bills in it. It doesn't.",cashier.bills.size(), 1);
		assertEquals("Bill should have the same waiter. It doesn't.",cashier.bills.get(0).waiter, waiter1);
		assertEquals("Bill should have the same customer. It doesn't.",cashier.bills.get(0).customer, customer1);
		assertEquals("Bill should have the NotComputed state. It doesn't.",cashier.bills.get(0).state, CashierAgent.CustomerBill.BillState.NotComputed);
		assertEquals("CashierAgent should have one line after the Cashier's ComputeBill is called. Instead, the Cashier's event log reads: "
				+ cashier.log.toString(), 1, cashier.log.size());		
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ waiter1.log.toString(), 0, waiter1.log.size());

		//run scheduler
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		assertEquals("Cashier should have 1 bills in it. It doesn't.",cashier.bills.size(), 1);
		assertEquals("CashierAgent should have one event in log after scheduler is called. Instead, the Cashier's event log reads: "
				+ cashier.log.toString(), 1, cashier.log.size());		
		assertEquals("MockWaiter should have an event in log after the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ waiter1.log.toString(), 1, waiter1.log.size());
		assertEquals("Bill should have the None state. It doesn't.",cashier.bills.get(0).state, CashierAgent.CustomerBill.BillState.None);

		
		//check postconditions for step 1 and preconditions for step 2
		assertFalse("Cashier's scheduler should have returned false (no actions to do), but didn't.", cashier.pickAndExecuteAnAction());
		assertEquals("CashierAgent should have one event in log after scheduler is called. Instead, the Cashier's event log reads: "
				+ cashier.log.toString(), 1, cashier.log.size());
		
		//check preconditions for step 2
		assertEquals("MockCustomer should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
				+ customer1.log.toString(), 0, customer1.log.size());
		
		//step 2 of the test
		cashier.msgHereIsThePayment(customer1, 15.99, 10);
		assertEquals("Cashier should have 1 bills in it. It doesn't.",cashier.bills.size(), 1);
		assertEquals("Bill should have the same customer. It doesn't.",cashier.bills.get(0).customer, customer1);
		assertEquals("Bill should have the NotComputed state. It doesn't.",cashier.bills.get(0).state, CashierAgent.CustomerBill.BillState.ReturnedFromCustomer);
		assertEquals("CashierAgent should have one line after the Cashier's ComputeBill is called. Instead, the Cashier's event log reads: "
				+ cashier.log.toString(), 2, cashier.log.size());		
		assertEquals("MockCustomer should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
				+ customer1.log.toString(), 0, customer1.log.size());
		
		//run scheduler
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		assertEquals("Cashier should no bills in it. It doesn't.",cashier.bills.size(), 0);
		assertEquals("CashierAgent should have one event in log after scheduler is called. Instead, the Cashier's event log reads: "
				+ cashier.log.toString(), 2, cashier.log.size());		
		assertEquals("MockCustomer should have an event in log after the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ customer1.log.toString(), 1, customer1.log.size());
		assertTrue("MockCustomer should have logged an event for receiving \"HereIsYourChange\" with the correct change, but his last event logged reads instead: " 
				+ customer1.log.getLastLoggedEvent().toString(), customer1.log.containsString("Received YouDoNotHaveEnoughMoney from cashier. Debt = " + 5.99));
		
		//check postconditions for step 1 and preconditions for step 2
		assertFalse("Cashier's scheduler should have returned false (no actions to do), but didn't.", cashier.pickAndExecuteAnAction());

	}//end one non normal customer scenario

}
