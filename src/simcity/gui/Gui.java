package simcity.gui;

import java.awt.*;
import java.util.Timer;

public interface Gui {
	
    public void updatePosition();
    public void draw(Graphics2D g);
    public boolean isPresent();
    //public void changeAnimation(String animation);
}