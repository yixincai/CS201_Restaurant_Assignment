package restaurant.gui;

import restaurant.CustomerAgent;

import java.awt.*;

import javax.swing.*;

public class CustomerGui extends JPanel implements Gui{

	private CustomerAgent agent = null;
	private boolean isPresent = false;
	private boolean isHungry = false;

	//private HostAgent host;
	RestaurantGui gui;

	private int xPos, yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand, GoToSeat, LeaveRestaurant, GoToCashier};
	private Command command=Command.noCommand;

	public static final int xTable1 = 200, xTable2 = 300, xTable3 = 100;
	public static final int yTable = 150;
    private final int GAPX = 30;
    private final int GAPY = 30;
    private final int OriginX = -60;
    private final int OriginY = -60;
    
    private ImageIcon i = new ImageIcon("image/customer.jpg");
    private Image image = i.getImage();
    
    private String choice;
    private boolean show_choice = false;
    
	public CustomerGui(CustomerAgent c, RestaurantGui gui){ 
		agent = c;
		xPos = OriginX;
		yPos = OriginY;
		xDestination = OriginX;
		yDestination = OriginY;
		//maitreD = m;
		this.gui = gui;
	}

	public void updatePosition() {
		if (xPos < xDestination)
			xPos++;
		else if (xPos > xDestination)
			xPos--;

		if (yPos < yDestination)
			yPos++;
		else if (yPos > yDestination)
			yPos--;

		if (xPos == xDestination && yPos == yDestination) {
			if (command==Command.GoToSeat)
				agent.msgAnimationFinishedGoToSeat();
			else if (command==Command.LeaveRestaurant) {
				agent.msgAnimationFinishedLeaveRestaurant();
				System.out.println("about to call gui.setCustomerEnabled(agent);");
				isHungry = false;
				gui.setCustomerEnabled(agent);
			}
			else if (command==Command.GoToCashier) 
				agent.msgAnimationFinishedGoToCashier();
			command=Command.noCommand;
		}
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.BLUE);
    	g.drawImage(image, xPos, yPos, GAPX, GAPY, this);
    	if (show_choice)
    		g.drawString(this.choice, xDestination, yDestination + 40);
	}

	public boolean isPresent() {
		return isPresent;
	}
	public void setHungry() {
		isHungry = true;
		agent.gotHungry();
		setPresent(true);
	}
	public boolean isHungry() {
		return isHungry;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}

	public void DoGoToSeat(int seatnumber) {//later you will map seatnumber to table coordinates.
		if (seatnumber == 1){
			xDestination = xTable1;
			yDestination = yTable;
		}
		else if(seatnumber == 2){
			xDestination = xTable2;
			yDestination = yTable;
		}
		else if (seatnumber == 3){
			xDestination = xTable3;
			yDestination = yTable;
		}
		command = Command.GoToSeat;
		show_choice = false;
	}
	
	public void DoGoToCashier() {
		show_choice = false;
		choice = "";
		xDestination = 300;
		yDestination = 70;
		command = Command.GoToCashier;
	}
	
	public void DoGoWaiting() {
		show_choice = false;
		choice = "";
		xDestination = 50;
		yDestination = 50;
		command = Command.GoToCashier;
	}
	
	public void DoGoToJail() {
		xDestination = 50;
		yDestination = 170;
	}

	public void showOrderFood(String choice){
		this.choice = choice + "?";
		show_choice = true;
	}

	public void eatFood(String choice){
		this.choice = choice;
		show_choice = true;
	}
	
	public void DoExitRestaurant() {
		xDestination = OriginX;
		yDestination = OriginY;
		command = Command.LeaveRestaurant;
	}
	
    public void pauseThread(){
    	if (agent != null)
    		agent.pauseThread();
    }

    public void resumeThread(){
    	if (agent != null)
    		agent.resumeThread();
    }
}
