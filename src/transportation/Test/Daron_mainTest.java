//package simcity;
//
//import java.awt.Color;
//import java.awt.Dimension;
//import java.awt.Graphics;
//import java.awt.Graphics2D;
//
//import javax.swing.JFrame;
//import javax.swing.JPanel;
//
//import simcity.gui.GUI_Example;
//import simcity.gui.Gui;
//
//public class Daron_mainTest {
//	
//	public static void main(String[] args) {
//		JFrame frame = new JFrame();
//		Gui Edgar = new GUI_Example(null, "Edgar");
//        Edgar.changeAnimation("WalkLeft");
//		Test_PANEL gui = new Test_PANEL(Edgar);
//		frame.setSize(300, 300);
//        frame.setVisible(true);
//        frame.setResizable(false);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        
//        gui.setMinimumSize(new Dimension(300,300));
//        gui.setSize(300, 300);
//        gui.setVisible(true);
//        frame.add(gui);
//        
//        while(true) {
//        	update(gui);
//        }
//	}
//	
//	public static void update(Test_PANEL gui) {
//		gui.repaint();
//	}
//}
