package restaurant.gui;


import restaurant.CustomerAgent;
import restaurant.HostAgent;

import java.awt.*;
import javax.swing.*;

public class HostGui extends JPanel implements Gui {

    private HostAgent agent = null;

    private int xPos = 300, yPos = 50;//default waiter position
    public static int xGap = 20;
    public static int yGap = 20;
    
    private ImageIcon i = new ImageIcon("image/host.jpg");
    private Image image = i.getImage();
    public HostGui(HostAgent agent) {
        this.agent = agent;
    }

    public void updatePosition() {
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.MAGENTA);
        g.fillRect(xPos, yPos, xGap, yGap);
    }
    
	public void updateDestination(int x, int y, int w, int h){
	}

    public boolean isPresent() {
        return true;
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
