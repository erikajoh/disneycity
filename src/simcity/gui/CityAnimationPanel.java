package simcity.gui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CityAnimationPanel extends JPanel implements ActionListener{
	public static final int WINDOWX = 400;
	public static final int WINDOWY = 340;
	
	private Image bufferImage;
	private Dimension bufferSize; 
	
	//private List<Gui> guis 
	
	public CityAnimationPanel() {
		setSize(WINDOWX, WINDOWY);
		setVisible(true);
		
		bufferSize = this.getSize();
		
		Timer timer = new Timer(10, (ActionListener)this );
		timer.start();
		
	}
	
	public void actionPerformed(ActionEvent e) {
		repaint();
	}
	
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		
		g2.setColor(getBackground());
		g2.fillRect(0,  0,  WINDOWX, WINDOWY);
		
		g2.setColor(Color.ORANGE); 
		g2.fillRect(0, 0, WINDOWX, WINDOWY);
		
	}
	
	
	

}
