package transportation;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.*;

import simcity.gui.SimCityGui;
import transportation.Agents.TransportationController;
import transportation.GUIs.Gui;

public class TransportationPanel extends JPanel implements ActionListener, MouseListener, MouseMotionListener{
	private final int WINDOWX = 400;
	private final int WINDOWY = 330;

	private Image img;
	SimCityGui gui;
	private Transportation controller;

	MouseEvent previousPosition;
	Point offset;

	private List<Gui> guis = Collections.synchronizedList(new ArrayList<Gui>());
	Timer timer;

	class BuildingFinder {
		int xLeft, xRight, yTop, yBottom;
		String name;

		BuildingFinder(int xLeft, int xRight, int yTop, int yBottom, String name) {
			this.xLeft = xLeft;
			this.xRight = xRight;
			this.yTop = yTop;
			this.yBottom = yBottom;
			this.name = name;
		}
	}

	List<BuildingFinder> buildings;

	public TransportationPanel(SimCityGui gui) {
		offset = new Point(0,0);
		setSize(WINDOWX, WINDOWY);
		setVisible(true);

		this.gui = gui;

		img = Toolkit.getDefaultToolkit().getImage("res/Background.png");	
		timer = new Timer(20, this );
		timer.start();

		controller = new TransportationController(this);

		addMouseListener(this);
		addMouseMotionListener(this);

		buildings = new ArrayList<BuildingFinder>();

		buildings.add(new BuildingFinder(50,100,2,52,"Rancho Del Zocalo"));
		buildings.add(new BuildingFinder(150,225,2,52,"Pirate Bank"));
		buildings.add(new BuildingFinder(225,275,2,52,"Main St Apartments #1"));
		buildings.add(new BuildingFinder(300,350,2,52,"Main St Apartments #2"));
		buildings.add(new BuildingFinder(0,50,52,102,"Main St Apartments #3"));
		buildings.add(new BuildingFinder(0,50,152,202,"Main St Apartments #4"));
		buildings.add(new BuildingFinder(350,400,102,152,"Blue Bayou"));
		buildings.add(new BuildingFinder(0,50,227,277,"Village Haus"));
		buildings.add(new BuildingFinder(350,400,227,277,"Main St Apartments #6"));
		buildings.add(new BuildingFinder(50,100,277,327,"Main St Apartments #5"));
		buildings.add(new BuildingFinder(100,150,277,327,"Pizza Port"));
		buildings.add(new BuildingFinder(175,275,277,327,"Mickey's Market"));
		buildings.add(new BuildingFinder(300,350,277,327,"Carnation Cafe"));
		buildings.add(new BuildingFinder(305,400, 2, 102,"Haunted Mansion"));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		repaint();
	}

	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;

		
		//offset changing
		if(previousPosition != null) {
			//Move Camera Up
			if(previousPosition.getY() >= 0 && previousPosition.getY() <= 20)
				offset.y -= 1;

			//Move Camera Down
			if(previousPosition.getY() >= 310 && previousPosition.getY() <= 330)
				offset.y += 1;

			//Move Camera Left
			if(previousPosition.getX() >= 0 && previousPosition.getX() <= 20)
				offset.x -= 1;

			//Move Camera Right
			if(previousPosition.getX() >= 380 && previousPosition.getY() <= 400)
				offset.x += 1;
		}

		//offset clamping
		if(offset.x < -20) {
			offset.x = -20;
		}
		
		if(offset.x > 20) {
			offset.x = 20;
		}
		
		if(offset.y > 20) {
			offset.y = 20;
		}
		if(offset.y < -20) {
			offset.y = -20;
		}
		
		g2.drawImage(img, (int)-offset.getX(), (int)-offset.getY(), null);

		synchronized (guis) {
			for(Gui gui : guis) {
				if (gui.isPresent()) {
					gui.updatePosition();
				}
			}
		}
		synchronized (guis) {
			for(Gui gui : guis) {
				if (gui.isPresent()) {
					gui.draw(g2, offset);
				}
			}
		}
	}

	public void addGui (Gui gui) {
		guis.add(gui);
	}

	public void pauseAnim() {
		timer.stop();
	}

	public void unpauseAnim() {
		timer.start();
	}

	@Override
	public void mouseClicked(MouseEvent me) {
		String name = findBuilding(me.getX(), me.getY());
		if(name != null)
			gui.showPanel(name);
	}

	private String findBuilding(int x, int y) {
		//for(BuildingFinder b : buildings) {
		for(int i = 0; i < buildings.size(); i++) {
			BuildingFinder b = buildings.get(i);
			if(x >= b.xLeft - offset.getX() && x < b.xRight - offset.getX() && y >= b.yTop - offset.getY() && y < b.yBottom - offset.getY())
				return b.name;
		}
		return null;
	}

	@Override
	public void mouseEntered(MouseEvent me) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseExited(MouseEvent me) {
		previousPosition = null;

	}

	@Override
	public void mousePressed(MouseEvent me) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent me) {
		// TODO Auto-generated method stub

	}

	public Transportation getTransportation() {
		return controller;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		previousPosition = e;
	}
}
