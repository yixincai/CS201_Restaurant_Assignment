package restaurant.gui;

import java.awt.*;

public interface Gui {

    public void updatePosition();
    public void draw(Graphics2D g);
    public boolean isPresent();
    public void pauseThread();
    public void resumeThread();
    public void updateDestination(int x, int y, int w, int h);
}
