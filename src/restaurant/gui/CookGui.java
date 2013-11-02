package restaurant.gui;

import javax.swing.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.ImageIcon;

import restaurant.CookAgent;
import restaurant.HostAgent;

public class CookGui extends JPanel implements Gui{
	
	private CookAgent agent;
    private ImageIcon i = new ImageIcon("image/cook.jpg");
    private Image image = i.getImage();
    private ImageIcon ifridge = new ImageIcon("image/fridge.png");
    private Image fridgeimage = ifridge.getImage();
    private ImageIcon i2 = new ImageIcon("image/host.png");
    private Image plateimage = i2.getImage();
    private ImageIcon igrill = new ImageIcon("image/grill.jpg");
    private Image grillimage = igrill.getImage();    
    private String food = "";
    private boolean show_food = false;
    
	CookGui(CookAgent c){
		this.agent = c;
	}

    private int xPos = 200, yPos = 290, xPlate = 100, xCooking = 200, xFridge = 300;//default cook position
    private int xDestination = 100, yDestination = 290;
    public static int xGap = 30;
    public static int yGap = 30;
	private enum Command {noCommand, GoToSeat};
	private Command command=Command.noCommand;

	public void DoGoCookFood(){
    	xDestination = xCooking;
        command = Command.GoToSeat;
	}
	
	public void DoCookFood(String choice){
		this.food = choice;
		show_food = true;
	}

	public void DoFinishFood(){
		this.food = "";
		show_food = false;
	}
	
	public void DoGoToFridge(){
    	xDestination = xFridge;
        command = Command.GoToSeat;
	}
	
	public void DoPutPlate(){
    	xDestination = xPlate;
        command = Command.GoToSeat;
	}
	
	public void DoGoHome(){
    	xDestination = xCooking;
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
        	agent.releaseSemaphore();
        }
    }

    public void draw(Graphics2D g) {
    	g.setColor(Color.BLUE);
    	g.drawImage(image, xPos, yPos, xGap, yGap, this);
    	g.drawImage(plateimage, xPlate - xGap, yPos-40, xGap, yGap, this);
    	g.drawImage(grillimage, xCooking, yPos-40, xGap, yGap, this);
    	g.drawImage(fridgeimage, xFridge + 30, yPos-40, xGap, yGap, this);
    	if (show_food)
    		g.drawString(food, xCooking, yPos - 50);
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
