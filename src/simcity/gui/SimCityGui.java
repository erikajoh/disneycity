package simcity.gui;

import housing.Housing;
import housing.ResidentAgent.State;
import housing.gui.HousingAnimationPanel;
import housing.test.mock.LoggedEvent;
import bank.ManagerAgent;
import bank.gui.Bank;
import bank.gui.BankAnimationPanel;

import javax.swing.*;

import market.Market;
import market.gui.MarketAnimationPanel;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import restaurant_bayou.gui.BayouAnimationPanel;
import restaurant_bayou.gui.RestaurantBayou;
import restaurant_rancho.gui.RanchoAnimationPanel;
import restaurant_rancho.gui.RestaurantRancho;
import restaurant_rancho.gui.RestaurantRanchoGui;
import restaurant_rancho.gui.RestaurantRancho;
import restaurant_pizza.gui.RestaurantPizza;
import restaurant_pizza.gui.PizzaAnimationPanel;
import restaurant_haus.gui.HausAnimationPanel;
import restaurant_haus.gui.RestaurantHaus;
import simcity.PersonAgent;
import transportation.TransportationPanel;
import restaurant_cafe.gui.CafeAnimationPanel;
import restaurant_cafe.gui.RestaurantCafe;

public class SimCityGui extends JFrame  {

	public static final int WINDOWX = 800;
	public static final int WINDOWY = 600;
	
	String name;
	
	SimCityPanel simCityPanel;
	
	JPanel cards;
				
	public static RestaurantRancho restRancho;
	public RanchoAnimationPanel ranchoAniPanel = new RanchoAnimationPanel();
	
	public static RestaurantBayou restBayou;
	public BayouAnimationPanel bayouAniPanel = new BayouAnimationPanel();
	
	public static RestaurantPizza restPizza;
	public PizzaAnimationPanel pizzaAniPanel = new PizzaAnimationPanel();
	
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
	
	public static Market mickeysMarket;
	public MarketAnimationPanel markAniPanel = new MarketAnimationPanel();
		
	public static Bank pirateBank;
	public BankAnimationPanel bankAniPanel = new BankAnimationPanel();
	
	public static RestaurantCafe restCafe;
	public CafeAnimationPanel cafeAniPanel = new CafeAnimationPanel();
	
	public static RestaurantHaus restHaus; 
	public HausAnimationPanel hausAniPanel = new HausAnimationPanel();
	
	TransportationPanel cityAniPanel = new TransportationPanel(this);
	private JPanel cityBanner = new JPanel();
	private JPanel zoomBanner = new JPanel();
	private JPanel cityAnimation = new JPanel();
	private JPanel zoomAnimation = new JPanel();
	private JPanel cityPanel = new JPanel();
	private JPanel zoomPanel = new JPanel();
	private JLabel cityLabel = new JLabel("Disney City View                                          ");
	private JLabel zoomLabel = new JLabel("Click on a Building to see Animation Inside");
		
	public static ArrayList<JPanel> animationPanelsList = new ArrayList<JPanel>();
	
	public SimCityGui(String name) {
				
		cards = new JPanel(new CardLayout());
		cards.add(housAniPanel7, "Haunted Mansion");
		cards.add(housAniPanel1, "Main St Apartments #1");
		cards.add(housAniPanel2, "Main St Apartments #2");
		cards.add(housAniPanel3, "Main St Apartments #3");
		cards.add(housAniPanel4, "Main St Apartments #4");
		cards.add(housAniPanel5, "Main St Apartments #5");
		cards.add(housAniPanel6, "Main St Apartments #6");
		cards.add(markAniPanel, "Mickey's Market");
		cards.add(bankAniPanel, "Pirate Bank");
		cards.add(ranchoAniPanel, "Rancho Del Zocalo");
		cards.add(cafeAniPanel, "Carnation Cafe");
		cards.add(bayouAniPanel, "Blue Bayou");
		cards.add(pizzaAniPanel, "Pizza Port");
		cards.add(hausAniPanel, "Village Haus");
				
		animationPanelsList.add(housAniPanel7);
		animationPanelsList.add(housAniPanel1);
		animationPanelsList.add(housAniPanel2);
		animationPanelsList.add(housAniPanel3);
		animationPanelsList.add(housAniPanel4);
		animationPanelsList.add(housAniPanel5);
		animationPanelsList.add(housAniPanel6);
		animationPanelsList.add(markAniPanel);
		animationPanelsList.add(bankAniPanel);
		animationPanelsList.add(ranchoAniPanel);
		animationPanelsList.add(cafeAniPanel);
		animationPanelsList.add(bayouAniPanel);
		animationPanelsList.add(pizzaAniPanel);
		animationPanelsList.add(hausAniPanel);
		
		// Restaurants etc. must be created before simCityPanel is constructed, as demonstrated below
		restRancho = new RestaurantRancho(this, "Rancho Del Zocalo");
		restBayou = new RestaurantBayou(this, "Blue Bayou");
		restPizza = new RestaurantPizza(this, "Pizza Port");
		restHaus = new RestaurantHaus(this, "Village Haus");
		restCafe = new RestaurantCafe(this, "Carnation Cafe");
		hauntedMansion = new Housing(housAniPanel7, "Haunted Mansion");
		mainStApts1 = new Housing(housAniPanel1, "Main St Apartments #1");
		mainStApts2 = new Housing(housAniPanel2, "Main St Apartments #2");
		mainStApts3 = new Housing(housAniPanel3, "Main St Apartments #3");
		mainStApts4 = new Housing(housAniPanel4, "Main St Apartments #4");
		mainStApts5 = new Housing(housAniPanel5, "Main St Apartments #5");
		mainStApts6 = new Housing(housAniPanel6, "Main St Apartments #6");
		mickeysMarket = new Market(this, "Mickey's Market", cityAniPanel.getTransportation());
		pirateBank = new Bank(this, "Pirate Bank");
		//ManagerAgent manager = new ManagerAgent("Manager", pirateBank, this);
		//pirateBank.setManager(manager);		
		simCityPanel = new SimCityPanel(this);
		//manager.startThread();
		
		setLayout(new GridBagLayout());
		setBounds(WINDOWX/20, WINDOWX/20, WINDOWX, WINDOWY);
		GridBagConstraints c = new GridBagConstraints();
		//c.fill = GridBagConstraints.BOTH;
		GridBagConstraints c1 = new GridBagConstraints();
		c1.fill = GridBagConstraints.BOTH;
		c1.weightx = .5;
		c1.gridx = 0;
		c1.gridy = 0; 
		c1.gridwidth = 3;
		cityBanner.setBorder(BorderFactory.createTitledBorder("City Banner"));
		cityBanner.add(cityLabel);
		add(cityBanner, c1);
		GridBagConstraints c3 = new GridBagConstraints();
		c3.fill = GridBagConstraints.BOTH;
		c3.weightx = .5; 
		c3.gridx = 5;
		c3.gridy = 0;
		c3.gridwidth = 5;
		zoomBanner.setBorder(BorderFactory.createTitledBorder("Zoom Banner"));
		zoomBanner.add(zoomLabel);
		add(zoomBanner, c3);
		GridBagConstraints c2 = new GridBagConstraints();
		c2.fill = GridBagConstraints.BOTH;
		c2.weightx = .5;
		c2.weighty = .32;
		c2.gridx = 0;
		c2.gridy = 1;
		c2.gridwidth = 3;
		c2.gridheight = 3;
		cityAnimation.setLayout(new BoxLayout(cityAnimation, BoxLayout.Y_AXIS));
		cityAnimation.add(cityAniPanel);
		//cityAnimation.setBorder(BorderFactory.createTitledBorder("City Animation"));
		add(cityAnimation, c2);
		GridBagConstraints c4 = new GridBagConstraints();
		c4.fill = GridBagConstraints.BOTH;
		c4.weightx = .5;
		c4.weighty=.32;
		c4.gridx = 3;
		c4.gridy=1;
		c4.gridwidth = GridBagConstraints.REMAINDER;
		c4.gridheight = 3;
		zoomAnimation.setLayout(new BoxLayout(zoomAnimation, BoxLayout.Y_AXIS));
		zoomAnimation.add(cards);
//		ranchoAniPanel.setVisible(false);
//		zoomAnimation.add(housAniPanel);
		//zoomAnimation.setBorder(BorderFactory.createTitledBorder("Zoom Animation"));
		add(zoomAnimation, c4);
		GridBagConstraints c5 = new GridBagConstraints();
		c5.fill = GridBagConstraints.BOTH;
		c5.weightx= .5;
		c5.weighty = .18;
		c5.gridx=0;
		c5.gridy=5;
		c5.gridwidth = 3;
		c5.gridheight = 2;
		cityPanel.setBorder(BorderFactory.createTitledBorder("City Panel"));
		add(cityPanel, c5);
		GridBagConstraints c6 = new GridBagConstraints();
		c6.fill = GridBagConstraints.BOTH;
		c6.weightx= 0;
		c6.weighty = .18;
		c6.gridx=3;
		c6.gridy=5;
		c6.gridwidth = GridBagConstraints.REMAINDER;
		c6.gridheight =2;
		zoomPanel.setBorder(BorderFactory.createTitledBorder("Zoom Panel"));
		add(zoomPanel, c6);			
	}
	
	public static void main(String[] args) {
		SimCityGui gui = new SimCityGui("Sim City Disneyland");
		PersonAgent p = new PersonAgent("PersonCashier", hauntedMansion, 100, "Italian", true, "", null, 'c');
		gui.setTitle("SimCity Disneyland");
		gui.setVisible(true);
		gui.setResizable(false);
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		restRancho.setMarket(mickeysMarket);
		restRancho.setBank(pirateBank);
		restRancho.addPerson(null, "Cook", "cook", 50);
        restRancho.addPerson(null, "WaiterPC", "w", 50);
        restRancho.addPerson(p, "Cashier", "cash", 50);
        restRancho.addPerson(null, "Market", "Trader Joes", 50);
        restRancho.addPerson(null, "Host", "Host", 50);
        restRancho.addPerson(null, "Customer", "Sally", 50);
		//restPizza.addPerson(null, "Cook", "cook", 50);
        //restPizza.addPerson(null, "Waiter", "w", 50);
        //restPizza.addPerson(null, "Cashier", "cash", 50);
        //restPizza.addPerson(null, "Host", "Host", 50);
		//restPizza.addPerson(null, "Customer", "Sally", 50);
		
        //restBayou.addPerson(null, "Cook", "cook", 50);
        //restBayou.addPerson(null, "Waiter", "w", 50);
        //restBayou.addPerson(null, "Cashier", "cash", 50);
        //restBayou.addPerson(null, "Market", "Trader Joes", 50);
        //restBayou.addPerson(null, "Host", "Host", 50);
        //restBayou.addPerson(null, "Customer", "Sally", 50);
        
		restCafe.setMarket(mickeysMarket);
		restCafe.setBank(pirateBank);
		restCafe.addPerson(null, "Cook", "cook", 50);
        restCafe.addPerson(null, "Waiter", "w", 50);
        restCafe.addPerson(p, "Cashier", "cash", 50);
        restCafe.addPerson(null, "Market", "Trader Joes", 50);
        restCafe.addPerson(null, "Host", "Host", 50);
        restCafe.addPerson(null, "Customer", "Sally", 50);
	
        
		restHaus.addPerson(null, "Cook", "cook", 50);
        restHaus.addPerson(null, "Waiter", "w", 50);
        restHaus.addPerson(null, "Cashier", "cash", 50);
        restHaus.addPerson(null, "Host", "Host", 50);
		//restHaus.addPerson(null, "Customer", "Sally", 50);
		
		mickeysMarket.addPerson(null, "Manager", "MRAWP");
		mickeysMarket.addPerson(null, "Cashier", "Kapow");
		mickeysMarket.addPerson(null, "Worker", "Bleep");
		mickeysMarket.addPerson(null, "Worker", "Meep");

	}
	
	public void showPanel(String panel) {
		System.out.println("showing " + panel);
		((CardLayout)(cards.getLayout())).show(cards, panel);
	}
}
