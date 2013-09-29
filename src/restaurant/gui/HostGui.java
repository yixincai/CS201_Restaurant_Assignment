package restaurant.gui;

import restaurant.HostAgent;

import java.awt.*;
import javax.swing.*;

public class HostGui extends JPanel implements Gui {

    private HostAgent agent = null;

    private int xPos = 50, yPos = 20;//default waiter position
    public static int xGap = 40;
    public static int yGap = 40;
    
    private ImageIcon i = new ImageIcon("image/host.png");
    private Image image = i.getImage();
    public HostGui(HostAgent agent) {
        this.agent = agent;
    }

    public void updatePosition() {
    }

    public void draw(Graphics2D g) {
    	g.drawImage(image, xPos, yPos, xGap, yGap, this);
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
