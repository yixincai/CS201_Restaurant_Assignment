package restaurant.gui;


import restaurant.CustomerAgent;
import restaurant.WaiterAgent;

import java.awt.*;
import javax.swing.*;

public class WaiterGui extends JPanel implements Gui {

    private WaiterAgent agent = null;

    private int xPos = -20, yPos = -20;//default waiter position
    private int xDestination = -20, yDestination = -20;//default start position
    private int x_vary, y_vary;
    public static int xTable = 200;
    public static int yTable = 150;
    public static int xGap = 20;
    public static int yGap = 20;
    
    private ImageIcon i = new ImageIcon("image/host.jpg");
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
        //g.setColor(Color.MAGENTA);
        //g.fillRect(xPos, yPos, xGap, yGap);
    	g.drawImage(image, xPos, yPos, xGap, yGap, this);
    }

    public boolean isPresent() {
        return true;
    }

    public void DoBringToTable(CustomerAgent customer, int table_number) {
    	
		if (table_number == 1){
			xTable = 200;
		}
		else if(table_number == 2){
			xTable = 300;
		}
		else if (table_number == 3){
			xTable = x_vary;
			yTable = y_vary;
		}
    	xDestination = xTable + xGap;
        yDestination = yTable - yGap;
        while (xPos == xDestination && yPos == yDestination) {
        }
    }

	public void updateDestination(int x, int y, int w, int h){
		x_vary = x;
		y_vary = y;
	}    
    
    public void DoLeaveCustomer() {
        xDestination = -xGap;
        yDestination = -yGap;
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
