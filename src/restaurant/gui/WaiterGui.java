package restaurant.gui;


import restaurant.CustomerAgent;
import restaurant.WaiterAgent;

import java.awt.*;
import javax.swing.*;

public class WaiterGui extends JPanel implements Gui {

    private WaiterAgent agent = null;

    private int xPos = 200, yPos = 50;//default waiter position
    private int xDestination = 200, yDestination = 50;//default start position
    public static int xTable = 200;
    public static int yTable = 150;
    public static int xGap = 30;
    public static int yGap = 30;
    
    private ImageIcon i = new ImageIcon("image/waiter.png");
    private Image image = i.getImage();
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
    }

    public void draw(Graphics2D g) {
    	g.drawImage(image, xPos, yPos, xGap, yGap, this);
    }

    public boolean isPresent() {
        return true;
    }

    public void DoGoToTable(CustomerAgent customer, int table_number) {
    	
		if (table_number == 1){
			xTable = 200;
		}
		else if(table_number == 2){
			xTable = 300;
		}
		else if (table_number == 3){
			xTable = 100;
		}
    	xDestination = xTable + xGap;
        yDestination = yTable - yGap;
        while (!(xPos == xDestination && yPos == yDestination)) {
        }
    }
    
    public void DoGoToCook() {
    	xDestination = 300;
        yDestination = 250 - yGap;
        while (!(xPos == xDestination && yPos == yDestination)) {
        }
    }

    public void DoLeaveCustomer() {
    	xDestination = 200;
        yDestination = 50;
    }

	public void updateDestination(int x, int y, int w, int h){
	}    
    
    public void DoFetchCustomer() {
        xDestination = -xGap;
        yDestination = -yGap;
        while (!(xPos == xDestination && yPos == yDestination)) {
        }
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
