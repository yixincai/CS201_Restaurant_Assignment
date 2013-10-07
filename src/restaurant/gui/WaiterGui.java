package restaurant.gui;


import restaurant.CustomerAgent;
import restaurant.WaiterAgent;

import java.awt.*;

import javax.swing.*;

public class WaiterGui extends JPanel implements Gui {

    private WaiterAgent agent = null;
    private boolean show_choice = false;
    private String food;
	public static final int xTable1 = 200, xTable2 = 300, xTable3 = 100, xCook = 300, yCook = 250, xPlace = 200, yPlace = 50;
    private int xPos = 200, yPos = 50;//default waiter position
    private int xDestination = 200, yDestination = 50;//default start position
    public static int xTable = 200;
    public static int yTable = 150;
    public static int xGap = 30;
    public static int yGap = 30;
    
    private ImageIcon i = new ImageIcon("image/waiter.png");
    private Image image = i.getImage();
    
	private enum Command {noCommand, GoToSeat};
	private Command command=Command.noCommand;
    
    public WaiterGui(WaiterAgent agent) {
        this.agent = agent;
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
        if (yPos == yDestination && xPos == xDestination && command == Command.GoToSeat){
        	System.out.println("release semaphore");
        	command = Command.noCommand;
            show_choice = false;
        	agent.releaseSemaphore();
        }
    }

    public void draw(Graphics2D g) {
    	g.setColor(Color.RED);
    	g.drawImage(image, xPos, yPos, xGap, yGap, this);
    	if (show_choice)
    		g.drawString(food, xPos, yPos - 10);
    }

    public boolean isPresent() {
        return true;
    }

    public void DoGoToTable(CustomerAgent customer, int table_number) {
    	
		if (table_number == 1){
			xTable = xTable1;
		}
		else if(table_number == 2){
			xTable = xTable2;
		}
		else if (table_number == 3){
			xTable = xTable3;
		}
    	xDestination = xTable + xGap;
        yDestination = yTable - yGap;
        command = Command.GoToSeat;
    }
    
    public void DoBringFood(CustomerAgent customer, int table_number, String food) {
    	
		if (table_number == 1){
			xTable = xTable1;
		}
		else if(table_number == 2){
			xTable = xTable2;
		}
		else if (table_number == 3){
			xTable = xTable3;
		}
		this.food = food;
		show_choice = true;
    	xDestination = xTable + xGap;
        yDestination = yTable - yGap;
        command = Command.GoToSeat;
    }
    
    public void DoGoToCook() {
    	xDestination = xCook;
        yDestination = yCook - yGap;
        command = Command.GoToSeat;
    }

    public void DoLeaveCustomer() {
    	xDestination = xPlace;
        yDestination = yPlace;
    }
    
    public void DoFetchCustomer() {
        xDestination = -xGap;
        yDestination = -yGap;
        command = Command.GoToSeat;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
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
