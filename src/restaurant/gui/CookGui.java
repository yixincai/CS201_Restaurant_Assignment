package restaurant.gui;

import javax.swing.*;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;

import restaurant.CookAgent;
import restaurant.HostAgent;

public class CookGui extends JPanel implements Gui{
	
	private CookAgent cook;
    private ImageIcon i = new ImageIcon("image/cook.jpg");
    private Image image = i.getImage();
    
	CookGui(CookAgent c){
		this.cook = c;
	}
	
	public void DoCookFood(){
		
	}
	
    private HostAgent agent = null;

    private int xPos = 300, yPos = 250;//default cook position
    public static int xTable = 200;
    public static int yTable = 150;
    public static int xGap = 40;
    public static int yGap = 40;


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
