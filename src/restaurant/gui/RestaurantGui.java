package restaurant.gui;

import restaurant.CustomerAgent;
import restaurant.WaiterAgent;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
/**
 * Main GUI class.
 * Contains the main frame and subsequent panels
 */
public class RestaurantGui extends JFrame implements ActionListener{
    /* The GUI has two frames, the control frame (in variable gui) 
     * and the animation frame, (in variable animationFrame within gui)
     */
	AnimationPanel animationPanel = new AnimationPanel();
	
    /* restPanel holds 2 panels
     * 1) the staff listing, menu, and lists of current customers all constructed
     *    in RestaurantPanel()
     * 2) the infoPanel about the clicked Customer (created just below)
     */    
    private RestaurantPanel restPanel = new RestaurantPanel(this);
    /* infoPanel holds information about the clicked customer, if there is one*/
    private JPanel infoPanel;
    private JLabel infoLabel; //part of infoPanel
    private JCheckBox stateCB = new JCheckBox();;//part of infoLabel
    private Object currentPerson;/* Holds the agent that the info is about.
    								Seems like a hack */
    //private JPanel waiterInfoPanel;
    //private JLabel waiterInfoLabel; //part of infoPanel
    private JButton waiterStateB;//part of infoLabel
    
    private JCheckBox pauseCB;//part of infoLabel
    
    private JPanel waiterPanel;
    //private JTextField waiterNameTF = new JTextField(15);
    //private JButton addWaiterB = new JButton("AddWaiter");
    /**
     * Constructor for RestaurantGui class.
     * Sets up all the gui components.
     */
    public RestaurantGui() {
        int WINDOWX = 450;
        int WINDOWY = 700;
        
    	setBounds(50, 50, WINDOWX, WINDOWY);

        setLayout(new BoxLayout((Container) getContentPane(), 
        		BoxLayout.Y_AXIS));

        Dimension restDim = new Dimension(WINDOWX, (int) (WINDOWY * .35));
        restPanel.setPreferredSize(restDim);
        restPanel.setMinimumSize(restDim);
        restPanel.setMaximumSize(restDim);
        add(restPanel);
        
        // Now, setup the info panel
        Dimension infoDim = new Dimension(WINDOWX, (int) (WINDOWY * .1));
        infoPanel = new JPanel();
        infoPanel.setPreferredSize(infoDim);
        infoPanel.setMinimumSize(infoDim);
        infoPanel.setMaximumSize(infoDim);
        infoPanel.setBorder(BorderFactory.createTitledBorder("Information"));
        infoPanel.setLayout(new GridLayout(1, 2, 30, 0));
        
        /*waiterInfoPanel = new JPanel();
        waiterInfoPanel.setPreferredSize(infoDim);
        waiterInfoPanel.setMinimumSize(infoDim);
        waiterInfoPanel.setMaximumSize(infoDim);
        waiterInfoPanel.setBorder(BorderFactory.createTitledBorder("Waiter Information"));
        waiterInfoPanel.setLayout(new GridLayout(1, 2, 30, 0));
        */
        stateCB.setVisible(false);
        stateCB.addActionListener(this);

        waiterStateB = new JButton();
        waiterStateB.setVisible(false);
        waiterStateB.addActionListener(this);
        
        infoLabel = new JLabel(); 
        infoLabel.setText("<html><pre><i>Click Add to make customers and waiters</i></pre></html>");
        infoPanel.add(infoLabel);
        infoPanel.add(stateCB);
        add(infoPanel);

        /*waiterInfoLabel = new JLabel(); 
        waiterInfoLabel.setText("<html><pre><i>Click a waiter to show waiters</i></pre></html>");
        waiterInfoPanel.add(waiterInfoLabel);*/
        infoPanel.add(waiterStateB);
        
        pauseCB = new JCheckBox("pause");
        pauseCB.setVisible(true);
        pauseCB.addActionListener(this);
        add(pauseCB);
        
        Dimension animationDim = new Dimension(WINDOWX, (int) (WINDOWY * .5));
        animationPanel.setPreferredSize(animationDim);
        animationPanel.setMinimumSize(animationDim);
        animationPanel.setMaximumSize(animationDim);
    	add(animationPanel);
    }
    /**
     * updateInfoPanel() takes the given customer (or, for v3, Host) object and
     * changes the information panel to hold that person's info.
     *
     * @param person customer (or waiter) object
     */
    public void updateInfoPanel(Object person) {
    	stateCB.setVisible(false);
    	waiterStateB.setVisible(false);
        currentPerson = person;

        if (person instanceof CustomerAgent) {
        	stateCB.setVisible(true);
            CustomerAgent customer = (CustomerAgent) person;
            stateCB.setText("Hungry?");
          //Should checkmark be there? 
            stateCB.setSelected(customer.getGui().isHungry());
          //Is customer hungry? Hack. Should ask customerGui
            stateCB.setEnabled(!customer.getGui().isHungry());
          // Hack. Should ask customerGui
            infoLabel.setText(
               "<html><pre>     Customer: " + customer.getName() + " </pre></html>");
        }
        else if (person instanceof WaiterAgent) {
        	waiterStateB.setVisible(true);
            WaiterAgent waiter = (WaiterAgent) person;
            //waiterStateB.setText("Break?");
          //Should checkmark be there?
            if (waiter.getBreakStatus()){
            	waiterStateB.setText("Back");
            }
            else
            	waiterStateB.setText("Break");
          //Is customer hungry? Hack. Should ask customerGui
          // Hack. Should ask customerGui
            infoLabel.setText(
               "<html><pre>     Waiter: " + waiter.getName() + " </pre></html>");
        }
        infoPanel.validate();
    }
    /**
     * Action listener method that reacts to the checkbox being clicked;
     * If it's the customer's checkbox, it will make him hungry
     * For v3, it will propose a break for the waiter.
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == stateCB) {
            if (currentPerson instanceof CustomerAgent) {
                CustomerAgent c = (CustomerAgent) currentPerson;
                c.getGui().setHungry();
                stateCB.setEnabled(false);
            }
        }
        else if (e.getSource() == waiterStateB && waiterStateB.getText().equals("Break")) {
            if (currentPerson instanceof WaiterAgent) {
                WaiterAgent w = (WaiterAgent) currentPerson;
                restPanel.AskForBreak(w);
                waiterStateB.setText("Back");
            }
        }
        else if (e.getSource() == waiterStateB && waiterStateB.getText().equals("Back")) {
            if (currentPerson instanceof WaiterAgent) {
                WaiterAgent w = (WaiterAgent) currentPerson;
                restPanel.AskToComeBack(w);
                waiterStateB.setText("Break");
            }
        }
        else if (e.getSource() == pauseCB){
        	if (pauseCB.isSelected() && pauseCB.getText().compareTo("pause") == 0){
        		System.out.println("heard pause");
        		animationPanel.pause();
        		pauseCB.setText("resume");
        		pauseCB.setSelected(false);
        	}
        	else if(pauseCB.isSelected() && pauseCB.getText().compareTo("resume") == 0){
        		System.out.println("heard resume");
        		animationPanel.resume();
        		pauseCB.setText("pause");
        		pauseCB.setSelected(false);
        	}
        }
        /*else if (e.getSource() == addWaiterB){
        	if (waiterNameTF.getText().compareTo("") != 0){
        		System.out.println("Waiter added");
        		restPanel.addPerson("Waiters", waiterNameTF.getText());
        	}
        }*/
    }
    
    /**
     * Message sent from a customer gui to enable that customer's
     * "I'm hungry" checkbox.
     *
     * @param c reference to the customer
     */
    public void setCustomerEnabled(CustomerAgent c) {
        if (currentPerson instanceof CustomerAgent) {
            CustomerAgent cust = (CustomerAgent) currentPerson;
            if (c.equals(cust)) {
                stateCB.setEnabled(true);
                stateCB.setSelected(false);
            }
        }
    }

    /**
     * Main routine to get gui started
     */
    public static void main(String[] args) {
        RestaurantGui gui = new RestaurantGui();
        gui.setTitle("csci201 Restaurant");
        gui.setVisible(true);
        gui.setResizable(false);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
