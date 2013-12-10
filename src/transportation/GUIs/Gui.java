package transportation.GUIs;

import java.awt.*;
import java.util.Timer;

import transportation.TransportationPanel;

public interface Gui {
	
    public void updatePosition();
    public void draw(Graphics2D g, Point offset);
    public boolean isPresent();
    //public void changeAnimation(String animation);
	public void setPanel(TransportationPanel transportationPanel);
	public String returnType();
}