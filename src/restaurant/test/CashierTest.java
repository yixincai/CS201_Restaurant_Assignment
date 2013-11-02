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
		waiter1 = new MockWaiter("Mike");
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
		assertEquals("CashierAgent should have an empty event log before the Cashier's HereIsPayment is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());
		assertEquals("MockMarket 1 should have an empty event log before the Cashier's scheduler is called. Instead, the Cashier's event log reads: "
				+ market1.log.toString(), 0, market1.log.size());
		
		//send first message to cashier
		cashier.msgHereIsTheBill(market1, 140);//send the message from a waiter
		assertEquals("Cashier should have 1 market bill in it. It doesn't.",cashier.marketBills.size(), 1);	
		assertEquals("Cashier should have 0 customer bill in it. It doesn't.",cashier.bills.size(), 0);
		assertEquals("Bill should have the same waiter. It doesn't.",cashier.marketBills.get(0).market, market1);
		assertEquals("Bill should have the NotComputed state. It doesn't.",cashier.marketBills.get(0).balance, 140.0);
		assertEquals("CashierAgent should have one line after the Cashier's ComputeBill is called. Instead, the Cashier's event log reads: "
				+ cashier.log.toString(), 1, cashier.log.size());		
		assertEquals("MockMarket should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ market1.log.toString(), 0, market1.log.size());

		//run scheduler
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		assertEquals("Cashier should have 0 market bill in it. It doesn't.",cashier.marketBills.size(), 0);
		assertEquals("CashierAgent should have one event in log after scheduler is called. Instead, the Cashier's event log reads: "
				+ cashier.log.toString(), 1, cashier.log.size());
		assertEquals("MockMarket should have an event in log after the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ market1.log.toString(), 1, market1.log.size());
		
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
		assertEquals("CashierAgent should have 200 dollars initially. Instead, the Cashier's balance is: "
				+ cashier.money, 200.0, cashier.money);		
		assertEquals("Market1 should have an empty event log before the Cashier's scheduler is called. Instead, Market1's event log reads: "
				+ market1.log.toString(), 0, market1.log.size());
		assertEquals("Market2 should have an empty event log before the Cashier's scheduler is called. Instead, Market2's event log reads: "
				+ market2.log.toString(), 0, market2.log.size());
		
		//send first message to cashier
		cashier.msgHereIsTheBill(market1, 100);//send the message from a waiter
		assertEquals("Cashier should have 1 market bill in it. It doesn't.",cashier.marketBills.size(), 1);	
		assertEquals("Cashier should have 0 customer bill in it. It doesn't.",cashier.bills.size(), 0);
		assertEquals("Bill should have the same waiter. It doesn't.",cashier.marketBills.get(0).market, market1);
		assertEquals("Bill should have the NotComputed state. It doesn't.",cashier.marketBills.get(0).balance, 100.0);
		assertEquals("CashierAgent should have one line after the Cashier's ComputeBill is called. Instead, the Cashier's event log reads: "
				+ cashier.log.toString(), 1, cashier.log.size());		
		assertEquals("MockMarket should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ market1.log.toString(), 0, market1.log.size());

		//send second message to cashier
		cashier.msgHereIsTheBill(market2, 80);//send the message from a waiter
		assertEquals("Cashier should have 2 market bill in it. It doesn't.",cashier.marketBills.size(), 2);	
		assertEquals("Bill should have the same market. It doesn't.",cashier.marketBills.get(1).market, market2);
		assertEquals("Bill should have the same value. It doesn't.",cashier.marketBills.get(1).balance, 80.0);
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
		
		//run scheduler again to process the second bill
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		assertEquals("Cashier should have 0 market bill in it. It doesn't.",cashier.marketBills.size(), 0);
		assertEquals("MockMarket 2 should have an event in log after the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ market2.log.toString(), 1, market2.log.size());		
		assertEquals("MockMarket 1 should have no new event in log after the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
				+ market1.log.toString(), 1, market1.log.size());
		
		//postconditions
		assertFalse("Cashier's scheduler should have returned false (no actions to do), but didn't.", cashier.pickAndExecuteAnAction());
		assertEquals("CashierAgent should have two former event in log after scheduler is called. Instead, the Cashier's event log reads: "
				+ cashier.log.toString(), 2, cashier.log.size());
		
		
	}//end two normal market scenario	
	
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
		cashier.msgHereIsThePayment(customer1, 10, 20);
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
		
		//check postconditions for step 1 and preconditions for step 2
		assertFalse("Cashier's scheduler should have returned false (no actions to do), but didn't.", cashier.pickAndExecuteAnAction());

	}//end one normal customer scenario
	
	/*
	//check postconditions for step 2 / preconditions for step 3

	
	assertTrue("Cashier should have logged \"Received ReadyToPay\" but didn't. His log reads instead: " 
			+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received ReadyToPay"));

	assertTrue("CashierBill should contain a bill of price = $7.98. It contains something else instead: $" 
			+ cashier.bills.get(0).bill.netCost, cashier.bills.get(0).bill.netCost == 7.98);
	
	assertTrue("CashierBill should contain a bill with the right customer in it. It doesn't.", 
				cashier.bills.get(0).bill.customer == customer);
	/*
	
	//step 3
	//NOTE: I called the scheduler in the assertTrue statement below (to succintly check the return value at the same time)
	assertTrue("Cashier's scheduler should have returned true (needs to react to customer's ReadyToPay), but didn't.", 
				cashier.pickAndExecuteAnAction());
	
	//check postconditions for step 3 / preconditions for step 4
	assertTrue("MockCustomer should have logged an event for receiving \"HereIsYourTotal\" with the correct balance, but his last event logged reads instead: " 
			+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received HereIsYourTotal from cashier. Total = 7.98"));

		
	assertTrue("Cashier should have logged \"Received HereIsMyPayment\" but didn't. His log reads instead: " 
			+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received HereIsMyPayment"));
	
	
	assertTrue("CashierBill should contain changeDue == 0.0. It contains something else instead: $" 
			+ cashier.bills.get(0).changeDue, cashier.bills.get(0).changeDue == 0);
	
	
	
	//step 4
	assertTrue("Cashier's scheduler should have returned true (needs to react to customer's ReadyToPay), but didn't.", 
				cashier.pickAndExecuteAnAction());
	
	//check postconditions for step 4
	assertTrue("MockCustomer should have logged an event for receiving \"HereIsYourChange\" with the correct change, but his last event logged reads instead: " 
			+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received HereIsYourChange from cashier. Change = 0.0"));

	
	assertTrue("CashierBill should contain a bill with state == done. It doesn't.",
			cashier.bills.get(0).state == cashierBillState.done);
	
	assertFalse("Cashier's scheduler should have returned false (no actions left to do), but didn't.", 
			cashier.pickAndExecuteAnAction());
	
*/	
	
}
