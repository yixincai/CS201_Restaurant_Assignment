package restaurant.gui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class AnimationPanel extends JPanel implements ActionListener {
    private final int TABLEX = 200;
    private final int TABLEY = 150;
    private final int GAPX = 50;
    private final int GAPY = 50;
    private final int WINDOWX = 450;
    private final int WINDOWY = 350;
    private Image bufferImage;
    private Dimension bufferSize;
    private boolean table3_added = false;
    private List<Gui> guis = new ArrayList<Gui>();

    private int width, height, x_pos, y_pos;
    public AnimationPanel() {
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
        
        bufferSize = this.getSize();
 
    	Timer timer = new Timer(20, this );
    	timer.start();
    }

	public void actionPerformed(ActionEvent e) {
		repaint();  //Will have paintComponent called
	}
	
	public void updateTable(int x, int y, int w, int h){
		width = w;
		height = h;
		x_pos = x;
		y_pos = y;
		table3_added = true;
        for(Gui gui : guis) {
            gui.updateDestination(x, y, w, h);
        }
	}

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;

        //Clear the screen by painting a rectangle the size of the frame
        g2.setColor(getBackground());
        g2.fillRect(0, 0, WINDOWX, WINDOWY );

        //Here is the table
        g2.setColor(Color.ORANGE);
        g2.fillRect(TABLEX, TABLEY, GAPX, GAPY);//200 and 250 need to be table params

        g2.fillRect(TABLEX+100, TABLEY, GAPX, GAPY);
        if (table3_added)
        	g2.fillRect(x_pos, y_pos, width, height);        
        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.updatePosition();
            }
        }

        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.draw(g2);
            }
        }
    }

    public void pause(){
    	for (Gui gui : guis){
    		System.out.println("pausing");
    		gui.pauseThread();
    	}
    }

    public void resume(){
    	for (Gui gui : guis){
    		System.out.println("resuming");
    		gui.resumeThread();
    	}
    }
    
    public void addGui(CustomerGui gui) {
        guis.add(gui);
    }

    public void addGui(HostGui gui) {
        guis.add(gui);
    }
}
