package restaurant.gui;

import restaurant.CustomerAgent;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
/**
 * Main GUI class.
 * Contains the main frame and subsequent panels
 */
public class RestaurantGui extends JFrame implements ActionListener , KeyListener{
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
    private JCheckBox stateCB;//part of infoLabel
    private JTextField addPersonA = new JTextField();

    private Object currentPerson;/* Holds the agent that the info is about.
    								Seems like a hack */
    private JCheckBox stateCB1, pauseCB;//part of infoLabel
    private JButton addPersonB = new JButton("AddTable");
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

        Dimension restDim = new Dimension(WINDOWX, (int) (WINDOWY * .3));
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

        stateCB = new JCheckBox();
        stateCB.setVisible(false);
        stateCB.addActionListener(this);

        infoPanel.setLayout(new GridLayout(1, 2, 30, 0));
        
        infoLabel = new JLabel(); 
        infoLabel.setText("<html><pre><i>Click Add to make customers</i></pre></html>");
        infoPanel.add(infoLabel);
        infoPanel.add(stateCB);
        add(infoPanel);
        
        stateCB1 = new JCheckBox();        
        stateCB1.setVisible(true);
        stateCB1.addActionListener(this);
        stateCB1.setText("Hungry?");
        stateCB1.setEnabled(false);
        
        addPersonA.addActionListener(this);
        addPersonA.addKeyListener(this);
        addPersonA.setMaximumSize(new Dimension(300,100));;
        add(addPersonA);
        setFocusable(true);
        requestFocusInWindow();

        add(stateCB1);
        
        addPersonB.addActionListener(this);
        add(addPersonB);
        
        pauseCB = new JCheckBox("pause");
        pauseCB.setVisible(true);
        pauseCB.addActionListener(this);
        add(pauseCB);
        
    	add(animationPanel);
    }
    /**
     * updateInfoPanel() takes the given customer (or, for v3, Host) object and
     * changes the information panel to hold that person's info.
     *
     * @param person customer (or waiter) object
     */
    public void updateInfoPanel(Object person) {
        stateCB.setVisible(true);
        currentPerson = person;

        if (person instanceof CustomerAgent) {
            CustomerAgent customer = (CustomerAgent) person;
            stateCB.setText("Hungry?");
          //Should checkmark be there? 
            stateCB.setSelected(customer.getGui().isHungry());
          //Is customer hungry? Hack. Should ask customerGui
            stateCB.setEnabled(!customer.getGui().isHungry());
          // Hack. Should ask customerGui
            infoLabel.setText(
               "<html><pre>     Name: " + customer.getName() + " </pre></html>");
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
                stateCB1.setEnabled(false);
            }
        }
        else if (e.getSource() == stateCB1) {
            restPanel.addPerson1(addPersonA.getText());
            stateCB1.setEnabled(false);
            if (currentPerson instanceof CustomerAgent) {
                CustomerAgent c = (CustomerAgent) currentPerson;
                c.getGui().setHungry();
                stateCB.setEnabled(false);
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
        else if (e.getSource() == addPersonB){
        	Integer height = Integer.parseInt(JOptionPane.showInputDialog("Please enter table height:"));
        	Integer width = Integer.parseInt(JOptionPane.showInputDialog("Please enter table width:"));
        	Integer x_pos = Integer.parseInt(JOptionPane.showInputDialog("Please enter table x position:"));
        	Integer y_pos = Integer.parseInt(JOptionPane.showInputDialog("Please enter table y position:"));
        	animationPanel.updateTable(x_pos, y_pos, width, height);
        } 
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
                stateCB1.setEnabled(true);
                stateCB.setSelected(false);
                stateCB1.setSelected(false);
            }
        }
    }
    public void keyTyped(KeyEvent e) {
    	if (e.getSource() == addPersonA)
    		stateCB1.setEnabled(addPersonA.getText().compareTo("") != 0);
    }

    @Override
    public void keyReleased(KeyEvent e) {
    	if (e.getSource() == addPersonA)
    		stateCB1.setEnabled(addPersonA.getText().compareTo("") != 0);
    }

    @Override
    public void keyPressed(KeyEvent e) {
    	if (e.getSource() == addPersonA)
    		stateCB1.setEnabled(addPersonA.getText().compareTo("") != 0);
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
