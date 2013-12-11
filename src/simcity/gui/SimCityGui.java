package simcity.gui;

import housing.Housing;
import housing.gui.HousingAnimationPanel;
import bank.gui.Bank;
import bank.gui.BankAnimationPanel;

import javax.swing.*;

import market.Market;
import market.gui.MarketAnimationPanel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import restaurant_bayou.gui.BayouAnimationPanel;
import restaurant_bayou.gui.RestaurantBayou;
import restaurant_rancho.gui.RanchoAnimationPanel;
import restaurant_rancho.gui.RestaurantRancho;
import restaurant_pizza.gui.RestaurantPizza;
import restaurant_pizza.gui.PizzaAnimationPanel;
import restaurant_haus.gui.HausAnimationPanel;
import restaurant_haus.gui.RestaurantHaus;
import simcity.PersonAgent;
import simcity.gui.trace.AlertLevel;
import simcity.gui.trace.AlertLog;
import simcity.gui.trace.AlertTag;
import simcity.gui.trace.TracePanel;
import transportation.TransportationPanel;
import restaurant_cafe.gui.CafeAnimationPanel;
import restaurant_cafe.gui.RestaurantCafe;

public class SimCityGui extends JFrame implements ActionListener, WindowListener {

	public static final int WINDOWX = 1180;
	public static final int WINDOWY = 730;
	
	String name;
	
	static SimCityPanel simCityPanel;
    WorkplacePropertyPanel workplacePropertyPanel;
    PersonPropertyPanel personPropertyPanel;
	TracePanel tracePanel;
	JFrame frame;
	
	JPanel cards;
	JButton popOutB;
	
	public boolean poppedOut = false;
				
	public static RestaurantRancho restRancho;
	public RanchoAnimationPanel ranchoAniPanel = new RanchoAnimationPanel();
	
	public static RestaurantBayou restBayou;
	public BayouAnimationPanel bayouAniPanel = new BayouAnimationPanel();
	
	public static RestaurantPizza restPizza;
	public PizzaAnimationPanel pizzaAniPanel = new PizzaAnimationPanel();

	public static RestaurantCafe restCafe;
	public CafeAnimationPanel cafeAniPanel = new CafeAnimationPanel();
	
	public static RestaurantHaus restHaus; 
	public HausAnimationPanel hausAniPanel = new HausAnimationPanel();
	
	public static Housing mainStApts1;
	public HousingAnimationPanel housAniPanel1 = new HousingAnimationPanel();
	
	public static Housing mainStApts2;
	public HousingAnimationPanel housAniPanel2 = new HousingAnimationPanel();
	
	public static Housing mainStApts3;
	public HousingAnimationPanel housAniPanel3 = new HousingAnimationPanel();
	
	public static Housing mainStApts4;
	public HousingAnimationPanel housAniPanel4 = new HousingAnimationPanel();
	
	public static Housing mainStApts5;
	public HousingAnimationPanel housAniPanel5 = new HousingAnimationPanel();
	
	public static Housing mainStApts6;
	public HousingAnimationPanel housAniPanel6 = new HousingAnimationPanel();
	
	public static Housing hauntedMansion;
	public HousingAnimationPanel housAniPanel7 = new HousingAnimationPanel();
	
	public static Housing cinderellaCastle;
	public HousingAnimationPanel housAniPanel8 = new HousingAnimationPanel();	
	
	public static Housing rabbitHole;
	public HousingAnimationPanel housAniPanel9 = new HousingAnimationPanel();	
	
	public static Housing pirateSuite;
	public HousingAnimationPanel housAniPanel10 = new HousingAnimationPanel();	
	
	public static Housing spaceMountain;
	public HousingAnimationPanel housAniPanel11 = new HousingAnimationPanel();	
	
	public static Housing tikiHut;
	public HousingAnimationPanel housAniPanel12 = new HousingAnimationPanel();
	
	public static Housing mainStApts7;
	public HousingAnimationPanel housAniPanel13 = new HousingAnimationPanel();
	
	public static Housing mainStApts8;
	public HousingAnimationPanel housAniPanel14 = new HousingAnimationPanel();
	
	public static Housing mainStApts9;
	public HousingAnimationPanel housAniPanel15 = new HousingAnimationPanel();
	
	public static Housing mainStApts10;
	public HousingAnimationPanel housAniPanel16 = new HousingAnimationPanel();
	
	public static Housing mainStApts11;
	public HousingAnimationPanel housAniPanel17 = new HousingAnimationPanel();
	
	public static Market mickeysMarket;
	public MarketAnimationPanel markAniPanel = new MarketAnimationPanel();
	
	public static Market minniesMarket; // new
	public MarketAnimationPanel markAniPanel2 = new MarketAnimationPanel();
		
	public static Bank pirateBank;
	public BankAnimationPanel bankAniPanel = new BankAnimationPanel();
	
	public static Bank buccaneerBank;
	public BankAnimationPanel bank2AniPanel = new BankAnimationPanel();
	
	private final static int tellerAmt = 4;
	
	TransportationPanel cityAniPanel = new TransportationPanel(this);
	private JPanel cityBanner = new JPanel();
	private JPanel zoomBanner = new JPanel();
	private static JPanel cityAnimation = new JPanel();
	private JPanel zoomAnimation = new JPanel();
	private JPanel cityPanel = new JPanel();
	private JPanel zoomPanel = new JPanel();
	private JPanel personPanel = new JPanel();
	private JPanel workplacePanel = new JPanel();
	private JPanel logPanel = new JPanel();
	private JLabel cityLabel = new JLabel("Disney City View                                          ");
	private JLabel zoomLabel = new JLabel("Click on a Building to see Animation Inside");
	
	GridBagConstraints c1, c2, c3, c4, c5, c6, c7, c8, c9;
		
	public static ArrayList<JPanel> animationPanelsList = new ArrayList<JPanel>();
	
	public SimCityGui(String name) {
		
		popOutB = new JButton("Pop Out");
		popOutB.addActionListener(this);
			
		tracePanel = new TracePanel();
		tracePanel.showAlertsWithLevel(AlertLevel.ERROR);		//THESE PRINT RED, WARNINGS PRINT YELLOW on a black background... :/
		tracePanel.showAlertsWithLevel(AlertLevel.INFO);		//THESE PRINT BLUE
		tracePanel.showAlertsWithLevel(AlertLevel.MESSAGE);		//THESE SHOULD BE THE MOST COMMON AND PRINT BLACK
		tracePanel.hideAlertsWithLevel(AlertLevel.DEBUG);
		tracePanel.showAlertsWithTag(AlertTag.CITY);
		tracePanel.showAlertsWithTag(AlertTag.PERSON);
		tracePanel.showAlertsWithTag(AlertTag.BANK);
		tracePanel.showAlertsWithTag(AlertTag.MARKET);
		tracePanel.showAlertsWithTag(AlertTag.HOUSING);
		tracePanel.showAlertsWithTag(AlertTag.TRANSPORTATION);
		tracePanel.showAlertsWithTag(AlertTag.RESTAURANT);
//		tracePanel.hideAlertsWithTag(AlertTag.CITY);
		AlertLog.getInstance().addAlertListener(tracePanel);
		AlertLog.getInstance().logInfo(AlertTag.CITY, "CITY", "SimCity Disneyland created");
						
		cards = new JPanel(new CardLayout());
		cards.add(housAniPanel7, "Haunted Mansion");
		cards.add(housAniPanel8, "Cinderella Castle");
		cards.add(housAniPanel9, "Rabbit Hole");
		cards.add(housAniPanel10, "Pirate's Suite");
		cards.add(housAniPanel11, "Space Mountain");
		cards.add(housAniPanel12, "Tiki Hut");
		cards.add(housAniPanel1, "Main St Apartments #1");
		cards.add(housAniPanel2, "Main St Apartments #2");
		cards.add(housAniPanel3, "Main St Apartments #3");
		cards.add(housAniPanel4, "Main St Apartments #4");
		cards.add(housAniPanel5, "Main St Apartments #5");
		cards.add(housAniPanel6, "Main St Apartments #6");
		cards.add(housAniPanel13, "Main St Apartments #7");
		cards.add(housAniPanel14, "Main St Apartments #8");
		cards.add(housAniPanel15, "Main St Apartments #9");
		cards.add(housAniPanel16, "Main St Apartments #10");
		cards.add(housAniPanel17, "Main St Apartments #11");
		cards.add(markAniPanel, "Mickey's Market");
		cards.add(markAniPanel2, "Minnie's Market");
		cards.add(bankAniPanel, "Pirate Bank");
		cards.add(bankAniPanel, "Buccaneer Bank");
		cards.add(ranchoAniPanel, "Rancho Del Zocalo");
		cards.add(cafeAniPanel, "Carnation Cafe");
		cards.add(bayouAniPanel, "Blue Bayou");
		cards.add(pizzaAniPanel, "Pizza Port");
		cards.add(hausAniPanel, "Village Haus");
				
		animationPanelsList.add(housAniPanel1);
		animationPanelsList.add(housAniPanel2);
		animationPanelsList.add(housAniPanel3);
		animationPanelsList.add(housAniPanel4);
		animationPanelsList.add(housAniPanel5);
		animationPanelsList.add(housAniPanel6);
		animationPanelsList.add(housAniPanel7);
		animationPanelsList.add(housAniPanel8);
		animationPanelsList.add(housAniPanel9);
		animationPanelsList.add(housAniPanel10);
		animationPanelsList.add(housAniPanel11);
		animationPanelsList.add(housAniPanel12);
		animationPanelsList.add(housAniPanel13);
		animationPanelsList.add(housAniPanel14);
		animationPanelsList.add(housAniPanel15);
		animationPanelsList.add(housAniPanel16);
		animationPanelsList.add(housAniPanel17);
		animationPanelsList.add(markAniPanel);
		animationPanelsList.add(markAniPanel2);
		animationPanelsList.add(bankAniPanel);
		animationPanelsList.add(bank2AniPanel);
		animationPanelsList.add(ranchoAniPanel);
		animationPanelsList.add(cafeAniPanel);
		animationPanelsList.add(bayouAniPanel);
		animationPanelsList.add(pizzaAniPanel);
		animationPanelsList.add(hausAniPanel);
		
		showPanel("Mickey's Market");
		
		// Restaurants etc. must be created before simCityPanel is constructed, as demonstrated below
		restRancho = new RestaurantRancho(this, "Rancho Del Zocalo");
		restBayou = new RestaurantBayou(this, "Blue Bayou");
		restPizza = new RestaurantPizza(this, "Pizza Port");
		restHaus = new RestaurantHaus(this, "Village Haus");
		restCafe = new RestaurantCafe(this, "Carnation Cafe");
		hauntedMansion = new Housing(housAniPanel7, "Haunted Mansion");
		cinderellaCastle = new Housing(housAniPanel8, "Cinderella Castle");
		rabbitHole = new Housing(housAniPanel9, "Rabbit Hole");
		pirateSuite = new Housing(housAniPanel10, "Pirate's Suite");
		spaceMountain = new Housing(housAniPanel11, "Space Mountain");
		tikiHut = new Housing(housAniPanel12, "Tiki Hut");
		mainStApts1 = new Housing(housAniPanel1, "Main St Apartments #1");
		mainStApts2 = new Housing(housAniPanel2, "Main St Apartments #2");
		mainStApts3 = new Housing(housAniPanel3, "Main St Apartments #3");
		mainStApts4 = new Housing(housAniPanel4, "Main St Apartments #4");
		mainStApts5 = new Housing(housAniPanel5, "Main St Apartments #5");
		mainStApts6 = new Housing(housAniPanel6, "Main St Apartments #6");
		mainStApts7 = new Housing(housAniPanel13, "Main St Apartments #7");
		mainStApts8 = new Housing(housAniPanel14, "Main St Apartments #8");
		mainStApts9 = new Housing(housAniPanel15, "Main St Apartments #9");
		mainStApts10 = new Housing(housAniPanel16, "Main St Apartments #10");
		mainStApts11 = new Housing(housAniPanel17, "Main St Apartments #11");
		mickeysMarket = new Market(this, "Mickey's Market", cityAniPanel.getTransportation());
		minniesMarket = new Market(this, "Minnie's Market", cityAniPanel.getTransportation());
		pirateBank = new Bank(this, "Pirate Bank", tellerAmt);
		buccaneerBank = new Bank(this, "Buccaneer Bank", tellerAmt);
		
		simCityPanel = new SimCityPanel(this);
		workplacePropertyPanel = new WorkplacePropertyPanel(this);
		// TODO: still need to implement this
		personPropertyPanel = new PersonPropertyPanel(this);
		
		setLayout(new GridBagLayout());
		setBounds(WINDOWX/20, WINDOWX/20, WINDOWX, WINDOWY);
		//GridBagConstraints c = new GridBagConstraints();
		//c.fill = GridBagConstraints.BOTH;
		c1 = new GridBagConstraints();
		c1.fill = GridBagConstraints.BOTH;
		c1.weightx = .3;
		c1.gridx = 0;
		c1.gridy = 0; 
		c1.gridwidth = 3;
		cityBanner.setBorder(BorderFactory.createTitledBorder("City Banner"));
		cityBanner.add(popOutB);
		add(cityBanner, c1);
		
		c2 = new GridBagConstraints();
		c2.fill = GridBagConstraints.BOTH;
		c2.weightx = .3; 
		c2.gridx = 3;
		c2.gridy = 0;
		c2.gridwidth = 3;
		zoomBanner.setBorder(BorderFactory.createTitledBorder("Zoom Banner"));
		zoomBanner.add(zoomLabel);
		add(zoomBanner, c2);
		
		c3 = new GridBagConstraints();
		c3.fill = GridBagConstraints.BOTH;
		c3.weightx = .1; 
		c3.weighty = .6;
		c3.gridx = 6;
		c3.gridy = 0;
		c3.gridwidth = 3;
		c3.gridheight = 3;
		personPanel.setBorder(BorderFactory.createTitledBorder("Person Properties"));
		//personPanel.setLayout(new BoxLayout(workplacePanel, BoxLayout.Y_AXIS));
		personPanel.add(personPropertyPanel);
		add(personPanel, c3);
		
		c4 = new GridBagConstraints();
		c4.fill = GridBagConstraints.BOTH;
		c4.weightx = .1; 
		c4.weighty = .4;
		c4.gridx = 6;
		c4.gridy = 4;
		c4.gridwidth = 3;
		c4.gridheight = 4;
		workplacePanel.setBorder(BorderFactory.createTitledBorder("Workplace Properties"));
		workplacePanel.setLayout(new BoxLayout(workplacePanel, BoxLayout.Y_AXIS));
		workplacePanel.add(workplacePropertyPanel);
		add(workplacePanel, c4);
		
		c5 = new GridBagConstraints();
		c5.fill = GridBagConstraints.BOTH;
		c5.weightx = .3;
		c5.weighty = .8;
		c5.gridx = 0;
		c5.gridy = 1;
		c5.gridwidth = 3;
		c5.gridheight = 4;
		cityAnimation.setLayout(new BoxLayout(cityAnimation, BoxLayout.Y_AXIS));
		cityAnimation.add(cityAniPanel);
		//cityAnimation.setBorder(BorderFactory.createTitledBorder("City Animation"));
		add(cityAnimation, c5);
		
		c6 = new GridBagConstraints();
		c6.fill = GridBagConstraints.BOTH;
		c6.weightx = .3;
		c6.weighty= .8;
		c6.gridx = 3;
		c6.gridy=1;
		c6.gridwidth = 3;
		c6.gridheight = 4;
		zoomAnimation.setLayout(new BoxLayout(zoomAnimation, BoxLayout.Y_AXIS));
		zoomAnimation.add(cards);
//		ranchoAniPanel.setVisible(false);
//		zoomAnimation.add(housAniPanel);
		//zoomAnimation.setBorder(BorderFactory.createTitledBorder("Zoom Animation"));
		add(zoomAnimation, c6);
		
		c7 = new GridBagConstraints();
		c7.fill = GridBagConstraints.BOTH;
		c7.weightx= .3;
		c7.weighty = .05;
		c7.gridx=0;
		c7.gridy = 6;
		c7.gridwidth = 3;
		c7.gridheight = 1;
		cityPanel.setBorder(BorderFactory.createTitledBorder("City Panel"));
		cityPanel.setLayout(new BoxLayout(cityPanel, BoxLayout.Y_AXIS));
		cityPanel.add(simCityPanel);
		add(cityPanel, c7);
		
		c8 = new GridBagConstraints();
		c8.fill = GridBagConstraints.BOTH;
		c8.weightx= .3;
		c8.weighty = .05;
		c8.gridx= 3;
		c8.gridy= 6;
		c8.gridwidth = 3;
		c8.gridheight = 1;
		zoomPanel.setBorder(BorderFactory.createTitledBorder("Zoom Panel"));
		add(zoomPanel, c8);	
		
		c9 = new GridBagConstraints();
		c9.fill = GridBagConstraints.BOTH;
		c9.weightx = .8; 
		c9.weighty = .01;
		c9.gridx = 0;
		c9.gridy = 7;
		c9.gridwidth = 8;
		c9.gridheight = 1;
		logPanel.setBorder(BorderFactory.createTitledBorder("Logger"));
		logPanel.setLayout(new BoxLayout(logPanel, BoxLayout.Y_AXIS));
		logPanel.add(tracePanel);
		add(logPanel, c9);
		
		simCityPanel.setTransPanel(cityAniPanel);

	}
	
	public static void main(String[] args) {
		SimCityGui gui = new SimCityGui("Sim City Disneyland");
		gui.setTitle("SimCity Disneyland");
		gui.setVisible(true);
		gui.setResizable(false);
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		PersonAgent p = new PersonAgent("PersonCashier", hauntedMansion, 100, "Italian", true, "", null, 'c');
		restRancho.setMarket(mickeysMarket);
		restRancho.setBank(pirateBank);
		restCafe.setMarket(minniesMarket);
		//restRancho.addPerson(null, "Cook", "cook", 50);
        //restRancho.addPerson(null, "WaiterPC", "w", 50);
        //restRancho.addPerson(p, "Cashier", "cash", 50);
        //restRancho.addPerson(null, "Market", "Trader Joes", 50);
        //restRancho.addPerson(null, "Host", "Host", 50);
        //restRancho.addPerson(null, "Customer", "Sally", 50);
		
		restPizza.addPerson(null, "Cook", "pcook", 50);
        restPizza.addPerson(null, "Waiter", "pw", 50);
        restPizza.addPerson(null, "Cashier", "pcash", 50);
        restPizza.addPerson(null, "Host", "pHost", 50);
		//restPizza.addPerson(null, "Customer", "pSally", 50);
		
        restBayou.addPerson(null, "Cook", "cook", 50);
        restBayou.addPerson(null, "WaiterPC", "w", 50);
        restBayou.addPerson(null, "Cashier", "cash", 50);
        restBayou.addPerson(null, "Market", "Trader Joes", 50);
        restBayou.addPerson(null, "Host", "Host", 50);
        restBayou.addPerson(null, "Customer", "Sally", 50);
        
		restCafe.setMarket(mickeysMarket);
		restCafe.setBank(pirateBank);
		restCafe.addPerson(null, "Cook", "cook", 50);
        restCafe.addPerson(null, "WaiterPC", "w", 50);
        restCafe.addPerson(p, "Cashier", "cash", 50);
        restCafe.addPerson(null, "Market", "Trader Joes", 50);
        restCafe.addPerson(null, "Host", "Host", 50);
        //restCafe.addPerson(null, "Customer", "Sally", 50);

		restHaus.addPerson(null, "Cook", "cook", 50);
        restHaus.addPerson(null, "Waiter", "w", 50);
        restHaus.addPerson(null, "Cashier", "cash", 50);
        restHaus.addPerson(null, "Host", "Host", 50);
        restHaus.setMarket(mickeysMarket);
        restPizza.setMarket(mickeysMarket);
		//restHaus.addPerson(null, "Customer", "Sally", 50);
		
//		mickeysMarket.addPerson(null, "Manager", "MRAWP");
//		mickeysMarket.addPerson(null, "Cashier", "Kapow");
//		mickeysMarket.addPerson(null, "Worker", "Bleep");
//		mickeysMarket.addPerson(null, "Worker", "Meep");
//		mickeysMarket.addPerson(p, "ehe", 500, "Italian", 1);
		
	}
	
	public void showPanel(String panel) {
		System.out.println("showing " + panel);
		((CardLayout)(cards.getLayout())).show(cards, panel);
	}
	
	public void updateGui(){
		workplacePropertyPanel.updateGui();
	}
	
	public void popOut() {
        poppedOut = true;
		remove(cityAnimation);
		revalidate();
		repaint();
		frame = new JFrame("City View");
		frame.setSize(850,750);
        frame.add(cityAnimation);
        frame.addWindowListener(this);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
	}
	
	public void popIn() {
        poppedOut = false;
		add(cityAnimation,c5);
		cityAnimation.updateUI();
	}
	
	public void closePopOut() {
		if (poppedOut) {
			frame.dispose();
			popIn();
		}
	}
	
	public void updateDayInfo(String day, String phase) {
		zoomLabel.setText("It is currently " + phase + " on this " + day + ".");
	}
	
	public SimCityPanel getSimCityPanel() {
		return simCityPanel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == popOutB) popOut();
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		popIn();
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
}
