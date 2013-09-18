package restaurant.gui;

import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.ImageIcon;

import restaurant.CookAgent;
import restaurant.CustomerAgent;
import restaurant.HostAgent;

public class CookGui implements Gui{
	
	private CookAgent cook;
	
	CookGui(CookAgent c){
		this.cook = c;
	}
	
	public void DoCookFood(){
		
	}
	
    private HostAgent agent = null;

    private int xPos = 300, yPos = 50;//default waiter position

    private int x_vary, y_vary;
    public static int xTable = 200;
    public static int yTable = 150;
    public static int xGap = 20;
    public static int yGap = 20;


    public void updatePosition() {
    }

    public void draw(Graphics2D g) {
        //g.setColor(Color.MAGENTA);
        g.fillRect(xPos, yPos, xGap, yGap);
    	//g.drawImage(image, xPos, yPos, xGap, yGap, this);
    }

    public boolean isPresent() {
        return true;
    }

	public void updateDestination(int x, int y, int w, int h){
		x_vary = x;
		y_vary = y;
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
