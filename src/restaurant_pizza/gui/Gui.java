package restaurant_pizza.gui;

import java.awt.*;

public interface Gui {

    public static final int xTable = 50;
    public static final int yTable = 200;
    public static final int tableSpacing = 90;

	//public static final int xHomeDestination = -40;
	//public static final int yHomeDestination = -40;
	
    public void updatePosition();
    public void draw(Graphics2D g);
    public boolean isPresent();
}
