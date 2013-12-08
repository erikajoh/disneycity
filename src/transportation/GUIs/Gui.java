package transportation.GUIs;

import java.awt.*;
import java.util.Timer;

public interface Gui {
	
    public void updatePosition();
    public void draw(Graphics2D g, Point offset);
    public boolean isPresent();
    //public void changeAnimation(String animation);
}