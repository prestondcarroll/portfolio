/*

   Deadwood GUI helper file
   Author: Moushumi Sharmin
   This file shows how to create a simple GUI using Java Swing and Awt Library
   Classes Used: JFrame, JLabel, JButton, JLayeredPane

*/

import java.awt.*;
import javax.swing.*;
import javax.swing.ImageIcon;
import javax.imageio.ImageIO;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

//Layers , 0 Board
// 1 Shot Markers , Flipped Cards
// 2 Right action menu
// 3
// 4 Not Flipped Cards
// 5 Menu Player Stats
// 6 Players


@SuppressWarnings( "deprecation" )
public class View extends JFrame {

    // JLabels
    JLabel boardlabel;
    static JLabel mLabel;

    //JButtons
    static JButton bAct = new JButton();
    static JButton bRehearse = new JButton();
    static JButton bMove;
    static JButton bWork;
    static JButton bUpgrade;
    static JButton bTwo;
    static JButton bThree;
    static JButton bFour;
    static JButton bFive;
    static JButton bSix;
    static JButton bSeven;
    static JButton bEight;
    static JButton bConfirm;
    static JButton bYes;
    static JButton bNo;

    static JButton bLoc1 = new JButton();
    static JButton bLoc2 = new JButton();
    static JButton bLoc3 = new JButton();
    static JButton bLoc4 = new JButton();
    static JButton bLoc5 = new JButton();
    static JButton[] locButtons = new JButton[5];

    static JButton[] workButtons = new JButton[2];

    static JButton bRole1 = new JButton();
    static JButton bRole2 = new JButton();
    static JButton bRole3 = new JButton();
    static JButton bRole4 = new JButton();
    static JButton bRole5 = new JButton();
    static JButton bRole6 = new JButton();
    static JButton bRole7 = new JButton();
    static JButton bRole8 = new JButton();
    static JButton[] bRoles = new JButton[8];

    static JButton bUp1 = new JButton();
    static JButton bUp2 = new JButton();
    static JButton bUp3 = new JButton();
    static JButton bUp4 = new JButton();
    static JButton bUp5 = new JButton();
    static JButton bUp6 = new JButton();
    static JButton bUp7 = new JButton();
    static JButton bUp8 = new JButton();
    static JButton bUp9 = new JButton();
    static JButton bUp10 = new JButton();
    static JButton[] bUpgrades= new JButton[10];

    //JTextFields
    static JTextField playerNameBox;

    //JTextArea
    static JTextArea playerNameTextArea;

    // JLayered Pane
    static JLayeredPane bPane;

    //Images
    static ImageIcon icon; //deadwood board.png

    //Players
    JLabel p1label = new JLabel();
    JLabel p2label = new JLabel();
    JLabel p3label = new JLabel();
    JLabel p4label = new JLabel();
    JLabel p5label = new JLabel();
    JLabel p6label = new JLabel();
    JLabel p7label = new JLabel();
    JLabel p8label = new JLabel();
    static JLabel[] playerLbls = new JLabel[8];


    //misc
    private static int counter = 1;
    private static int numPlayers = 0;
    private static HashMap <String, Integer> numPlayerLocMap = new HashMap<String, Integer>();

    // Constructor
    public View() {

        // Set the title of the JFrame
        super("Deadwood");
        // Set the exit option for the JFrame
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

        // Create the JLayeredPane to hold the display, cards, dice and buttons
        bPane = getLayeredPane();

        // Create the deadwood board
        boardlabel = new JLabel();
        icon =  new ImageIcon("board.jpg");
        boardlabel.setIcon(icon);
        boardlabel.setBounds(0,0,icon.getIconWidth(),icon.getIconHeight());

        // Add the board to the lowest layer
        bPane.add(boardlabel, new Integer(0));

        // Set the size of the GUI
        setSize(icon.getIconWidth()+200,icon.getIconHeight());

        playerLbls[0] = p1label;
        playerLbls[1] = p2label;
        playerLbls[2] = p3label;
        playerLbls[3] = p4label;
        playerLbls[4] = p5label;
        playerLbls[5] = p6label;
        playerLbls[6] = p7label;
        playerLbls[7] = p8label;

        locButtons[0] = bLoc1;
        locButtons[1] = bLoc2;
        locButtons[2] = bLoc3;
        locButtons[3] = bLoc4;
        locButtons[4] = bLoc5;

        workButtons[0] = bAct;
        workButtons[1] = bRehearse;

        bRoles[0] = bRole1;
        bRoles[1] = bRole2;
        bRoles[2] = bRole3;
        bRoles[3] = bRole4;
        bRoles[4] = bRole5;
        bRoles[5] = bRole6;
        bRoles[6] = bRole7;
        bRoles[7] = bRole8;

        bUpgrades[0] = bUp1;
        bUpgrades[1] = bUp2;
        bUpgrades[2] = bUp3;
        bUpgrades[3] = bUp4;
        bUpgrades[4] = bUp5;
        bUpgrades[5] = bUp6;
        bUpgrades[6] = bUp7;
        bUpgrades[7] = bUp8;
        bUpgrades[8] = bUp9;
        bUpgrades[9] = bUp10;

        workButtons[0] = bAct;
        workButtons[1] = bRehearse;

        numPlayerLocMap.put("trailer", 0);
        numPlayerLocMap.put("office", 0);
        numPlayerLocMap.put("Train Station", 0);
        numPlayerLocMap.put("Secret Hideout", 0);
        numPlayerLocMap.put("Church", 0);
        numPlayerLocMap.put("Hotel", 0);
        numPlayerLocMap.put("Main Street", 0);
        numPlayerLocMap.put("General Store", 0);
        numPlayerLocMap.put("Ranch", 0);
        numPlayerLocMap.put("Bank", 0);
        numPlayerLocMap.put("Saloon", 0);
        numPlayerLocMap.put("Jail", 0);


        pick_Players_menu();


        //getnames()

        //default_menu();
    }

    //Creates the menu for when the game starts and the number of players needs to be chosen
    private void pick_Players_menu(){
        // Create the Menu for action buttons
        mLabel = new JLabel("How many Players?");
        mLabel.setBounds(icon.getIconWidth()+20,0,200,20);
        bPane.add(mLabel,new Integer(2));

        // Create Action buttons
        bTwo = new JButton("2");
        bTwo.setBackground(Color.white);
        bTwo.setBounds(icon.getIconWidth()+10, 30,100, 20);
        bTwo.addMouseListener(new boardMouseListener());

        bThree = new JButton("3");
        bThree.setBackground(Color.white);
        bThree.setBounds(icon.getIconWidth()+10, 60,100, 20);
        bThree.addMouseListener(new boardMouseListener());

        bFour = new JButton("4");
        bFour.setBackground(Color.white);
        bFour.setBounds(icon.getIconWidth()+10, 90,100, 20);
        bFour.addMouseListener(new boardMouseListener());

        bFive = new JButton("5");
        bFive.setBackground(Color.white);
        bFive.setBounds(icon.getIconWidth()+10, 120,100, 20);
        bFive.addMouseListener(new boardMouseListener());

        bSix = new JButton("6");
        bSix.setBackground(Color.white);
        bSix.setBounds(icon.getIconWidth()+10, 150,100, 20);
        bSix.addMouseListener(new boardMouseListener());

        bSeven = new JButton("7");
        bSeven.setBackground(Color.white);
        bSeven.setBounds(icon.getIconWidth()+10, 180,100, 20);
        bSeven.addMouseListener(new boardMouseListener());

        bEight = new JButton("8");
        bEight.setBackground(Color.white);
        bEight.setBounds(icon.getIconWidth()+10, 210,100, 20);
        bEight.addMouseListener(new boardMouseListener());


        // Place the action buttons in the top layer
        bPane.add(bTwo, new Integer(2));
        bPane.add(bThree, new Integer(2));
        bPane.add(bFour, new Integer(2));
        bPane.add(bFive, new Integer(2));
        bPane.add(bSix, new Integer(2));
        bPane.add(bSeven, new Integer(2));
        bPane.add(bEight, new Integer(2));

    }

    //Once the number of players has been chosen, this is called so that the players
    //may enter their usernames in a new menu
    private static void get_names_menu(){

        clear_menu(2);
        //add textfield
        playerNameBox = new JTextField(20);
        playerNameBox.setBackground(Color.white);
        playerNameBox.setBounds(icon.getIconWidth()+10, 30,100, 20);
        playerNameBox.addKeyListener(new textAreaListener());

        playerNameTextArea = new JTextArea(5, 20);
        playerNameTextArea.setBackground(Color.white);
        playerNameTextArea.setBounds(icon.getIconWidth()+10, 30,100, 20);
        playerNameTextArea.setEditable(true);
        bPane.add(playerNameBox, new Integer(2));
        bPane.add(playerNameTextArea, new Integer(2));

        // Create the Menu for action buttons
        mLabel = new JLabel("Player 1's name?");
        mLabel.setBounds(icon.getIconWidth()+20,0,200,20);
        bPane.add(mLabel,new Integer(2));

        // Confirm button
        bConfirm = new JButton("Confirm Name");
        bConfirm.setBackground(Color.white);
        bConfirm.setBounds(icon.getIconWidth()+10, 60,150, 20);
        bConfirm.addMouseListener(new boardMouseListener());
        bPane.add(bConfirm, new Integer(2));

        init_players();
    }

    //Whenever the menu needs to be updates, this function is used
    private static void update_menu_text(String newText){
        mLabel.setText(newText);
        playerNameBox.setText("");

    }

    //the default menu is the player turn menu. This is called at the beginning of each player's turn
    public static void default_menu(){

        clear_menu(2);
        clear_menu(5);

        //Display Dice and name
        String playerName = GuiComms.get_current_player_name();
        int playerNo = GuiComms.get_current_player_no();
        System.out.println(playerNo);
        JLabel playerlbl = playerLbls[playerNo-1];
        JLabel menuPlayerLbl = new JLabel();
        menuPlayerLbl.setIcon(playerlbl.getIcon());
        menuPlayerLbl.setBounds(1350,200, 40, 40);
        clear_menu(5);
        bPane.add(menuPlayerLbl, new Integer(5));
        bPane.repaint();



        mLabel = new JLabel(playerName + "'s Dice:" );
        mLabel.setBounds(1205,200,200,20);
        bPane.add(mLabel,new Integer(5));
        int[] playerStats = GuiComms.get_player_stats();
        JLabel moneyL = new JLabel("Money: " + playerStats[0]);
        moneyL.setBounds(1205,220,200,20);
        bPane.add(moneyL,new Integer(5));

        JLabel fameL = new JLabel("Fame: " + playerStats[1]);
        fameL.setBounds(1205,240,200,20);
        bPane.add(fameL,new Integer(5));

        JLabel rankL = new JLabel("Rank: " + playerStats[2]);
        rankL.setBounds(1205,260,200,20);
        bPane.add(rankL,new Integer(5));

        moneyL = new JLabel("Money: " + playerStats[3]);
        moneyL.setBounds(1205,280,200,20);
        bPane.add(moneyL,new Integer(5));
        bPane.repaint();



        //Display Menu
        mLabel = new JLabel(playerName + ",  Select an action");
        mLabel.setBounds(icon.getIconWidth()+20,0,200,20);
        bPane.add(mLabel,new Integer(2));

        // Create Action buttons
        bWork = new JButton("WORK");
        bWork.setBackground(Color.white);
        bWork.setBounds(icon.getIconWidth()+10, 30,100, 20);
        bWork.addMouseListener(new boardMouseListener());

        bUpgrade = new JButton("UPGRADE");
        bUpgrade.setBackground(Color.white);
        bUpgrade.setBounds(icon.getIconWidth()+10,60,100, 20);
        bUpgrade.addMouseListener(new boardMouseListener());


        bMove = new JButton("MOVE");
        bMove.setBackground(Color.white);
        bMove.setBounds(icon.getIconWidth()+10,90,100, 20);
        bMove.addMouseListener(new boardMouseListener());

        // Place the action buttons in the top layer
        bPane.add(bWork, new Integer(2));
        bPane.add(bUpgrade, new Integer(2));
        bPane.add(bMove, new Integer(2));

        bPane.repaint();

    }

    //Gets rid of whatever menu is currently being used in the given layer
    private static void clear_menu(int layer){
        Component[] elements = bPane.getComponentsInLayer(layer);
        for (Component i: elements ){
            bPane.remove(i);
            bPane.repaint();
        }
        bPane.repaint();
    }


    //Used to add cards to the gui for when scenes are added to the game board
    public static void add_card_to_GUI(String image, String setLocation, int layer){
        JLabel cardlabel = new JLabel();
        ImageIcon cIcon =  new ImageIcon(View.class.getResource("cards/"+ image));
        cardlabel.setIcon(cIcon);
        cardlabel.setOpaque(true);

        if(setLocation.equals("Train Station")){
            cardlabel.setBounds(21,69,cIcon.getIconWidth()+2,cIcon.getIconHeight());
        }
        else if(setLocation.equals("Secret Hideout")){
            cardlabel.setBounds(27,732,cIcon.getIconWidth()+2,cIcon.getIconHeight());
        }
        else if(setLocation.equals("Church")){
            cardlabel.setBounds(623,734,cIcon.getIconWidth()+2,cIcon.getIconHeight());
        }
        else if(setLocation.equals("Hotel")){
            cardlabel.setBounds(969,740,cIcon.getIconWidth()+2,cIcon.getIconHeight());
        }
        else if(setLocation.equals("Main Street")){
            cardlabel.setBounds(969,28,cIcon.getIconWidth()+2,cIcon.getIconHeight());
        }
        else if(setLocation.equals("General Store")){
            cardlabel.setBounds(370,282,cIcon.getIconWidth()+2,cIcon.getIconHeight());
        }
        else if(setLocation.equals("Ranch")){
            cardlabel.setBounds(252,478,cIcon.getIconWidth()+2,cIcon.getIconHeight());
        }
        else if(setLocation.equals("Bank")){
            cardlabel.setBounds(623,475,cIcon.getIconWidth()+2,cIcon.getIconHeight());
        }
        else if(setLocation.equals("Saloon")){
            cardlabel.setBounds(632,280,cIcon.getIconWidth()+2,cIcon.getIconHeight());
        }
        else if(setLocation.equals("Jail")){
            cardlabel.setBounds(281,27,cIcon.getIconWidth()+2,cIcon.getIconHeight());
        }
        bPane.add(cardlabel, new Integer(layer));

    }

    //Used to get the coordinates of all the locations on the board
    public static int[] get_set_coords(String setLocation){
        int[] coords = new int[2];
        int x = 0;
        int y = 0;
        if(setLocation.equalsIgnoreCase("Train Station")){
            x = 21;
            y = 69;
        }
        else if(setLocation.equalsIgnoreCase("Secret Hideout")){
            x = 27;
            y =732;
        }
        else if(setLocation.equalsIgnoreCase("Church")){
            x = 623;
            y = 734;
        }
        else if(setLocation.equalsIgnoreCase("Hotel")){
            x = 969;
            y = 740;
        }
        else if(setLocation.equalsIgnoreCase("Main Street")){
            x = 969;
            y = 28;
        }
        else if(setLocation.equalsIgnoreCase("General Store")){
            x = 370;
            y = 282;
        }
        else if(setLocation.equalsIgnoreCase("Ranch")){
            x = 252;
            y = 478;
        }
        else if(setLocation.equalsIgnoreCase("Bank")){
            x = 623;
            y = 475;
        }
        else if(setLocation.equalsIgnoreCase("Saloon")){
            x = 632;
            y = 280;
        }
        else if(setLocation.equalsIgnoreCase("Jail")){
            x = 281;
            y = 27;
        }

        coords[0] = x;
        coords[1] = y;
        return coords;
    }

    //Used when a set card should no longer be seen on the GUI
    public static void remove_card_from_GUI(String setLocation,boolean visibility){
        int[] coords = get_set_coords(setLocation);
        bPane.getComponentAt(coords[0], coords[1]).setVisible(visibility);

    }

    //Used when a set card needs to be removed from the GUI
    public static void remove_card_from_GUI_for_day(String setLocation){
        int[] coords = new int[2];
        coords = get_set_coords(setLocation);
        bPane.remove(bPane.getComponentAt(coords[0], coords[1]));
        bPane.remove(bPane.getComponentAt(coords[0], coords[1]));

    }

    //Used to move the player to wherever they need to go on the board, from roles to location to stating the day over
    public static void move_player(int playerNo, int[] coords, String onOrOff, String newLoc, int hasRole){
        //move player to appropriate spot
        JLabel playerlbl = playerLbls[playerNo-1];
        playerlbl.setIcon(playerlbl.getIcon());

        //change values of number of players at a location
        String currentLoc = GuiComms.get_current_player_loc();
        if(!(newLoc.equals(currentLoc))) {
            int oldNum1 = numPlayerLocMap.get(currentLoc);
            int oldNum2 = numPlayerLocMap.get(newLoc);
            if(oldNum1 > 0) {
                numPlayerLocMap.put(currentLoc, oldNum1 - 1);
            }
            numPlayerLocMap.put(newLoc, oldNum2 + 1);
        }

        //determine coordinates for on card and off card roles
        if(onOrOff.equals("off")) {
            if(hasRole == 0){
                playerlbl.setBounds(coords[0], coords[1], coords[2], coords[3]);
            }
            else{
                int[] offsetCoords = adjust_player(newLoc);
                playerlbl.setBounds(offsetCoords[0] + coords[0], offsetCoords[1] + coords[1], coords[2], coords[3]);
            }

        }
        else{
            int [] setCoords = get_set_coords(newLoc);
            playerlbl.setBounds(setCoords[0] + coords[0], setCoords[1] + coords[1], coords[2], coords[3]);
        }
        bPane.add(playerlbl, new Integer(6));
        bPane.repaint();

    }

    //This menu displays the areas to which a player could possibly move
    public static void move_menu(ArrayList<String> nearby){
        clear_menu(2);
        //Prompt
        mLabel = new JLabel("MENU");
        mLabel.setBounds(icon.getIconWidth()+20,0,200,20);
        bPane.add(mLabel,new Integer(2));


        for (int i = 1; i < nearby.size(); i++) {
            // Create Action buttons
            JButton bLoc = locButtons[i];
            bLoc.setText(nearby.get(i));
            bLoc.setBackground(Color.white);
            bLoc.setBounds(icon.getIconWidth() + 10, 30+(i-1)*20, 125, 20);
            bLoc.addMouseListener(new boardMouseListener());
            bPane.add(bLoc, new Integer(2));
        }
    }

    //When a player chooses to work for their turn, this menu is used. It gives them the ability to act or rehearse
    public static void work_menu(){
        clear_menu(2);
        //Prompt
        mLabel = new JLabel("   Work Menu");
        mLabel.setBounds(icon.getIconWidth()+20,0,200,20);
        bPane.add(mLabel,new Integer(2));
        String[] actions = {"Act", "Rehearse"};

        for (int i = 0; i < 2; i++) {
            // Create Action buttons
            JButton bAction = workButtons[i];
            bAction.setText(actions[i]);
            bAction.setBackground(Color.white);
            bAction.setBounds(icon.getIconWidth() + 10, 30+i*20, 125, 20);
            bAction.addMouseListener(new boardMouseListener());
            bPane.add(bAction, new Integer(2));
        }
    }

    //Adds the dice of each player to the board and keeps track of them
    private static void init_players(){

        File file = new File("path.txt");
        String path = file.getAbsolutePath();
        path = path.replace("path.txt", "");
        String image = "players/p0/0_1.png";
        int x = 991;
        int y = 188;

        for (int i = 0; i < numPlayers; i++) {
            // Add a dice to represent a player.
            image = image.replace( i + "_", i + 1 + "_");
            int newNum = i + 1;
            String number = "" + newNum;
            image = image.replace("p" + i, "p" + number);
            JLabel playerlbl = playerLbls[i];
            ImageIcon pIcon = new ImageIcon(View.class.getResource(image));
            playerlbl.setIcon(pIcon);


            if (i % 2 == 0){
                playerlbl.setBounds(x, y, 194, 201);
                x += 40;
            }
            else{
                playerlbl.setBounds(x, y, 194, 201);
                y += 40;
                x -= 40;
            }
            bPane.add(playerlbl, new Integer(6));
            numPlayerLocMap.put("trailer", 2);
        }
    }

    //used to ask a player if they want to take a role when they move to a new location on the board
    static void role_menu(){
        clear_menu(2);
        bPane.repaint();
        //Prompt
        mLabel = new JLabel("Take A Role?");
        mLabel.setBounds(icon.getIconWidth()+20,0,200,20);
        bPane.add(mLabel,new Integer(2));
        bYes = new JButton("Yes");
        bYes.setBackground(Color.white);
        bYes.setBounds(icon.getIconWidth() + 10, 30, 125, 20);
        bYes.addMouseListener(new boardMouseListener());

        bNo = new JButton("No");
        bNo.setBackground(Color.white);
        bNo.setBounds(icon.getIconWidth() + 10, 50, 125, 20);
        bNo.addMouseListener(new boardMouseListener());

        bPane.add(bNo, new Integer(2));
        bPane.add(bYes, new Integer(2));
    }

    //When a player moves to a new location and they decide they want to look at the roles, this menu
    //pops up and shows the possible options they have for roles. If they don't have any possible options
    //the only clickable option is the "no role" button
    static void role_menu(String[] roles){
        clear_menu(2);
        bPane.repaint();
        //Prompt
        mLabel = new JLabel("Which role do you want?");
        mLabel.setBounds(icon.getIconWidth()+20,0,200,20);
        clear_menu(2);
        bPane.repaint();
        bPane.add(mLabel,new Integer(2));

        for (int i = 0; i < roles.length; i++) {
            // Create Action buttons
            JButton bRole = bRoles[i];
            if (roles[i] != null) {
                bRole.setText(roles[i]);
                bRole.setBackground(Color.white);
                bRole.setBounds(icon.getIconWidth() + 10, 30 + i * 20, 175, 20);
                bRole.addMouseListener(new boardMouseListener());
                bPane.add(bRole, new Integer(2));
            }
        }

    }

    //Adds all the shot markers to the board. They are set to not enabled so they look like they're not there
    static void add_shot_markers(int[][] takeCoords){
        for(int i = 0; takeCoords[i][0] != 0; i++) {
            JLabel shotMarker = new JLabel();
            shotMarker.setIcon(new ImageIcon("shot.png"));
            shotMarker.setBounds(takeCoords[i][0], takeCoords[i][1], takeCoords[i][2], takeCoords[i][3]);
            shotMarker.setEnabled(false);
            bPane.add(shotMarker, new Integer(8));
            if(i+1 == 3){
                break;
            }
        }
    }

    //When a take is finished, it is enabled so that it's clearly there
    public static void completeTake(int[] coords) {
        bPane.getComponentAt(coords[0], coords[1]).setEnabled(true);
    }

    //When a take is reset, it is disabled so that it's clearly not there
    public static void undoTake(int[] coords) {
        bPane.getComponentAt(coords[0], coords[1]).setEnabled(false);
    }

    //When a player wants to upgrade at the Casating Office, this menu is displayer
    //and it gives them their options of possible upgrade, if they can upgrade
    public static void upgrade_menu(String[] upgrades){
        clear_menu(2);

        for (int i = 0; i < upgrades.length; i++) {
            // Create Action buttons
            JButton bUpg = bUpgrades[i];
            if (upgrades[i] != null && !upgrades[i].equals("")) {
                bUpg.setText(upgrades[i]);
                bUpg.setBackground(Color.white);
                bUpg.setBounds(icon.getIconWidth() + 10, 30 + i * 20, 175, 20);
                bUpg.addMouseListener(new boardMouseListener());
                bPane.add(bUpg, new Integer(2));
            }
        }
    }

    //provides adjustments if there are multiple players in one location so that they do not overlap so each
    //player can clearly see where they are
    private static int[] adjust_player(String loc){
        int[] coords = new int[2];
        int x = 0;
        int y = 0;
        int test3 = numPlayerLocMap.get(loc);

        System.out.println("test3 is: " + test3);


        if(numPlayerLocMap.get(loc) == 2){
            x = 40;
        }
        else if(numPlayerLocMap.get(loc) == 3){
            x = 80;
        }
        else if(numPlayerLocMap.get(loc) == 4){
            x = 120;
        }
        else if(numPlayerLocMap.get(loc) == 5){
            y = 40;
        }
        else if(numPlayerLocMap.get(loc) == 6){
            x = 40;
            y = 40;
        }
        else if(numPlayerLocMap.get(loc) == 7){
            x = 80;
            y = 40;
        }
        else if(numPlayerLocMap.get(loc) == 8){
            x = 120;
            y = 40;
        }

        coords[0] = x;
        coords[1] = y;
        return coords;
    }

    //change the number of players in the hashmap so when a player takes a role they are not considered part of
    //the number of people on that location. Numplayers only considers the people not on a role, so they don't overlap
    public static void change_num_players(){
        String loc = GuiComms.get_current_player_loc();
        int oldNum = numPlayerLocMap.get(loc);
        if(oldNum > 0) {
            numPlayerLocMap.put(loc, oldNum - 1);
            int newNum = numPlayerLocMap.get(loc);
        }
    }

    public static void end_menu(String message){
        //Display Dice and name
        clear_menu(2);
        clear_menu(5);



        mLabel = new JLabel("END RESULTS:\n" + message);
        mLabel.setBounds(1205,350,400,20);
        bPane.add(mLabel,new Integer(5));
        bPane.repaint();

    }

    // This class implements Mouse Events
    static class boardMouseListener implements MouseListener{

        // Code for the different button clicks
        public void mouseClicked(MouseEvent e) {

            if (e.getSource()== bAct){
                GuiComms.setAction(("act"));
            }
            else if (e.getSource()== bRehearse){
                GuiComms.setAction(("rehearse"));
            }
            else if (e.getSource()== bWork) {
                GuiComms.setAction(("work"));
            }
            else if (e.getSource()== bUpgrade) {
                GuiComms.setAction("upgrade");
            }
            else if (e.getSource()== bMove){
                GuiComms.setAction(("move"));
            }
            else if (e.getSource()== bTwo){
                GuiComms.setPlayerNum(2);
                numPlayers = 2;
                get_names_menu();
            }
            else if (e.getSource()== bThree){
                GuiComms.setPlayerNum(3);
                numPlayers = 3;
                get_names_menu();
            }
            else if (e.getSource()== bFour){
                GuiComms.setPlayerNum(4);
                numPlayers = 4;
                get_names_menu();
            }
            else if (e.getSource()== bFive){
                GuiComms.setPlayerNum(5);
                numPlayers = 5;
                get_names_menu();
            }
            else if (e.getSource()== bSix){
                GuiComms.setPlayerNum(6);
                numPlayers = 6;
                get_names_menu();
            }
            else if (e.getSource()== bSeven){
                GuiComms.setPlayerNum(7);
                numPlayers = 7;
                get_names_menu();
            }
            else if (e.getSource()== bEight){
                GuiComms.setPlayerNum(8);
                numPlayers = 8;
                get_names_menu();
            }
            else if(e.getSource() == bConfirm){
                String name;
                name = playerNameBox.getText();
                if(name.equals("")) {

                    GuiComms.setPlayerName("Player " + counter);
                } else {
                    GuiComms.setPlayerName(name);
                }
                counter++;
                update_menu_text("Player " + counter + "'s name?");
                if(counter > numPlayers){
                    clear_menu(2);
                }
            }
            else if(e.getSource() == bLoc1){
                GuiComms.setAction(bLoc1.getText());
                role_menu();
            }
            else if(e.getSource() == bLoc2){
                GuiComms.setAction(bLoc2.getText());
                role_menu();
            }
            else if(e.getSource() == bLoc3){
                GuiComms.setAction(bLoc3.getText());
                role_menu();
            }
            else if(e.getSource() == bLoc4){
                GuiComms.setAction(bLoc4.getText());
                role_menu();
            }
            else if(e.getSource() == bLoc5){
                GuiComms.setAction(bLoc5.getText());
                role_menu();
            }
            else if(e.getSource() == bYes){
                GuiComms.setAction("y");
            }
            else if(e.getSource() == bNo){
                GuiComms.setAction("n");
                default_menu();
            }
            else if (e.getSource() == bRole1) {
                GuiComms.setAction("0");
                change_num_players();
            }
            else if (e.getSource() == bRole2) {
                GuiComms.setAction("1");
                change_num_players();
            }
            else if (e.getSource() == bRole3) {
                GuiComms.setAction("2");
                change_num_players();
            }
            else if (e.getSource() == bRole4) {
                GuiComms.setAction("3");
                change_num_players();
            }
            else if (e.getSource() == bRole5) {
                GuiComms.setAction("4");
                change_num_players();
            }
            else if (e.getSource() == bRole6) {
                GuiComms.setAction("5");
                change_num_players();
            }
            else if (e.getSource() == bRole7) {
                GuiComms.setAction("6");
                change_num_players();
            }
            else if (e.getSource() == bRole8) {
                GuiComms.setAction("7");
                change_num_players();
            }
            else if (e.getSource() == bUp1) {
                GuiComms.setAction(bUp1.getText());
            }
            else if (e.getSource() == bUp2) {
                GuiComms.setAction(bUp2.getText());
            }
            else if (e.getSource() == bUp3) {
                GuiComms.setAction(bUp3.getText());
            }
            else if (e.getSource() == bUp4) {
                GuiComms.setAction(bUp4.getText());
            }
            else if (e.getSource() == bUp5) {
                GuiComms.setAction(bUp5.getText());
            }
            else if (e.getSource() == bUp6) {
                GuiComms.setAction(bUp6.getText());
            }
            else if (e.getSource() == bUp7) {
                GuiComms.setAction(bUp7.getText());
            }
            else if (e.getSource() == bUp8) {
                GuiComms.setAction(bUp8.getText());
            }
            else if (e.getSource() == bUp9) {
                GuiComms.setAction(bUp9.getText());
            }
            else if (e.getSource() == bUp10) {
                GuiComms.setAction(bUp10.getText());
            }
        }
        public void mousePressed(MouseEvent e) {
        }
        public void mouseReleased(MouseEvent e) {
        }
        public void mouseEntered(MouseEvent e) {
        }
        public void mouseExited(MouseEvent e) {
        }

    }

    static class textAreaListener implements KeyListener{

        public void keyPressed(KeyEvent e) {

        }
        public void keyTyped(KeyEvent e){

        }
        public void keyReleased(KeyEvent e){

        }
    }

}