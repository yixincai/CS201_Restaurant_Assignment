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
	private enum Command {noCommand, GoToSeat, LeaveRestaurant};
	private Command command=Command.noCommand;

	public static final int xTable = 200;
	public static final int yTable = 150;
    private final int GAPX = 30;
    private final int GAPY = 30;
    private final int OriginX = -40;
    private final int OriginY = -40;
    
    private ImageIcon i = new ImageIcon("image/customer.jpg");
    private Image image = i.getImage();
    
    private String choice;
    private boolean show_choice = false;
    
	public CustomerGui(CustomerAgent c, RestaurantGui gui){ //HostAgent m) {
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
			if (command==Command.GoToSeat) agent.msgAnimationFinishedGoToSeat();
			else if (command==Command.LeaveRestaurant) {
				agent.msgAnimationFinishedLeaveRestaurant();
				System.out.println("about to call gui.setCustomerEnabled(agent);");
				isHungry = false;
				gui.setCustomerEnabled(agent);
			}
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
			xDestination = 200;
			yDestination = yTable;
		}
		else if(seatnumber == 2){
			xDestination = 300;
			yDestination = yTable;
		}
		else if (seatnumber == 3){
			xDestination = 100;
			yDestination = yTable;
		}
		command = Command.GoToSeat;
	}

	public void updateDestination(int x, int y, int w, int h){
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
		show_choice = false;
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
