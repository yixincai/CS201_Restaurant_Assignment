package restaurant.gui;

import restaurant.*;


import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class RestaurantPanel extends JPanel{

    //Host, cook, waiters and customers
    private HostAgent host = new HostAgent("Sarah");
    private HostGui hostGui = new HostGui(host);
    
    private CashierAgent cashier = new CashierAgent();
    private CashierGui cashierGui = new CashierGui(cashier);

    private WaiterAgent waiter = new WaiterAgent("Mike");
    private WaiterGui waiterGui = new WaiterGui(waiter);
    
    private CookAgent cook = new CookAgent();
    private CookGui cookGui = new CookGui(cook);
    
    private MarketAgent market1 = new MarketAgent("Market1"), market2 = new MarketAgent("Market2"), 
    		market3 = new MarketAgent("Market3");
    
    private Vector<CustomerAgent> customers = new Vector<CustomerAgent>();

    private JPanel restLabel = new JPanel();
    private ListPanel customerPanel = new ListPanel(this, "Customers");
    private JPanel group = new JPanel();
    private RestaurantGui gui; //reference to main gui

    public RestaurantPanel(RestaurantGui gui) {
        this.gui = gui;
        host.setGui(hostGui);
        waiter.setGui(waiterGui);
        cook.setGui(cookGui);
        cashier.setGui(cashierGui);
        
        host.addWaiter(waiter);
        waiter.setCook(cook);
        waiter.setHost(host);
        waiter.setCashier(cashier);
        cook.addMarket(market1);
        market1.setCook(cook);
        cook.addMarket(market2);
        market2.setCook(cook);
        cook.addMarket(market3);
        market3.setCook(cook);
        
        gui.animationPanel.addGui(hostGui);
        host.startThread();
        
        gui.animationPanel.addGui(waiterGui);
        waiter.startThread();
        
        gui.animationPanel.addGui(cookGui);
        cook.startThread();
        
        gui.animationPanel.addGui(cashierGui);
        cashier.startThread();
        
        market1.startThread();
        market2.startThread();
        market3.startThread();
        
        setLayout(new GridLayout(1, 2, 20, 20));
        group.setLayout(new GridLayout(1, 2, 10, 10));

        group.add(customerPanel);

        initRestLabel();
        add(restLabel);
        add(group);
    }
    

    /**
     * Sets up the restaurant label that includes the menu,
     * and host and cook information
     */
    private void initRestLabel() {
        JLabel label = new JLabel();
        //restLabel.setLayout(new BoxLayout((Container)restLabel, BoxLayout.Y_AXIS));
        restLabel.setLayout(new BorderLayout());
        label.setText(
                "<html><h3><u>Tonight's Staff</u></h3><table><tr><td>host:</td><td>" + host.getName() + "</td></tr></table><h3><u> Menu</u></h3><table><tr><td>Steak</td><td>$15.99</td></tr><tr><td>Chicken</td><td>$10.99</td></tr><tr><td>Salad</td><td>$5.99</td></tr><tr><td>Pizza</td><td>$8.99</td></tr></table><br></html>");

        restLabel.setBorder(BorderFactory.createRaisedBevelBorder());
        restLabel.add(label, BorderLayout.CENTER);
        restLabel.add(new JLabel("               "), BorderLayout.EAST);
        restLabel.add(new JLabel("               "), BorderLayout.WEST);
    }
    
    public void AskForBreak(){
    	waiter.msgAskForBreak();
    	System.out.println("Got break msg");
    }

    public void AskToComeBack(){
    	waiter.msgAskToComeBack();
    	System.out.println("Got back msg");
    }    
    
    /**
     * When a customer or waiter is clicked, this function calls
     * updatedInfoPanel() from the main gui so that person's information
     * will be shown
     *
     * @param type indicates whether the person is a customer or waiter
     * @param name name of person
     */
    public void showInfo(String type, String name) {

        if (type.equals("Customers")) {

            for (int i = 0; i < customers.size(); i++) {
                CustomerAgent temp = customers.get(i);
                if (temp.getName() == name)
                    gui.updateInfoPanel(temp);
            }
        }
    }

    public void addPerson1(String type, String name) {
    	if (type.equals("Customers")) {
    		CustomerAgent c = new CustomerAgent(name);	
    		CustomerGui g = new CustomerGui(c, gui);

    		gui.animationPanel.addGui(g);// dw
    		c.setHost(host);
    		c.setGui(g);
    		customers.add(c);
    		c.startThread();
    		c.getGui().setHungry();
    	}
    }
    /**
     * Adds a customer or waiter to the appropriate list
     *
     * @param type indicates whether the person is a customer or waiter (later)
     * @param name name of person
     */
    public void addPerson(String type, String name) {

    	if (type.equals("Customers")) {
    		CustomerAgent c = new CustomerAgent(name);	
    		CustomerGui g = new CustomerGui(c, gui);

    		gui.animationPanel.addGui(g);// dw
    		c.setHost(host);
    		c.setGui(g);
    		customers.add(c);
    		c.startThread();
    	}
    	else if (type.equals("Waiters")) {
    		System.out.println("waiter added");
    		WaiterAgent w = new WaiterAgent(name);	
    		WaiterGui g = new WaiterGui(w);

    		w.setGui(g);
    		w.setHost(host);
    		w.setCook(cook);
    		host.addWaiter(w);
    		gui.animationPanel.addGui(g);
    		w.startThread();
    	}
    }

}
