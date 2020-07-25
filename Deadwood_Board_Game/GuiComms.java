import java.util.ArrayList;
import java.util.LinkedList;

public class GuiComms {

    private static int playerNum = 0;
    private static String newName = "";
    private static String action = "";


    //Used at the beginning of the game to make sure the player gets the correct number
    public static void setPlayerNum(int i){
        playerNum = i;
    }


    //Used at the beginning of the game to make sure the player that enters a name gets that name
    public static void setPlayerName(String name){
        newName = name;
    }

    //used whenever a player makes a decision for what to do on their turn
    public static void setAction(String newAction){
        action = newAction;
    }

    //gets the list of players at the beginning of the game along with the total number of players and assigns the players num
    public static LinkedList<String> GetPlayerNames(){

        //wait for number of players
        while (playerNum == 0){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //wait for each player name
        LinkedList<String> playerList = new LinkedList<String>();
        for (int i = 0; i < playerNum; i++){
            while(newName.equals("")){

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            playerList.add(newName);
            newName = "";
        }
        System.out.println("Test");
        return playerList;
    }

    //Used as a middle man for the function of the same name in the view class
    public static void add_card_to_board(String setID, String setLocation, int layer){
        //System.out.println("Set ID is: " + setID + " SetLocation is: " + setLocation);
        View.add_card_to_GUI(setID, setLocation, layer);

    }

    //moves the player to the new loc on the GUI
    public static void update_location(int playerNo, String newLoc){
//        View.move_player(playerNo, newLoc){
//
//        }
    }

    //Used as a replacement for the same function in the TerminalOutput class. This is an adapter for the View and is used
    //when TakeTurn is called. returns the action the player would like to use
    public static String PlayerTurn(String PlayerID) {
        //wait for number of players
        while (action.equals("")){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        String newAction = action;
        action = "";
        return newAction;
    }

    //Used as a replacement for the same function in the TerminalOutput class. This is an adapter for the View and is used whenever a function in
    //the model needs input from the user
    public static String ParseInput(String output) {
        //wait for number of players
        while (action.equals("")){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String newAction = action;
        action = "";
        return newAction;
    }

    //Sends the nearby locations to the current player to the view so that they can be
    //displayed in the GUI
    public static void send_locations(ArrayList<String> nearby){
        View.move_menu(nearby);
    }

    //Gets the name of the current player so that it can be displayed
    public static String get_current_player_name(){
        return Moderator.get_current_player_name();
    }

    //Gets the naumber of the current player so that it can be displayed
    public static int get_current_player_no(){
        return Moderator.get_current_playerNo();
        //add one to zero index
    }


    //When called displays the default menu in the GUI
    public static void start_default_menu(){
        View.default_menu();
    }


    //Used to move the player on the GUI to the new coords and location
    public static void move_player(int playerNo, int[] coords, String onOrOff, String loc, int hasRole){
        View.move_player(playerNo, coords, onOrOff, loc, hasRole);
    }


    //adds shot markers to the given coordinates
    public static void add_shot_markers(int[][] takeCoords){
        View.add_shot_markers(takeCoords);
    }

    //When a location is visited for the first time, removes the top card there by making it invisible
    public static void visitLoc(String Loc) {
        View.remove_card_from_GUI(Loc, false);

    }

    //When a take is completed this function is called
    public static void completeTake(int[] coords) {
        View.completeTake(coords);
    }

    ////When a Scene is finished for the day, remove both cards from it
    public static void finishScene(String locName) {
        View.remove_card_from_GUI_for_day(locName);
    }

    //called by the GUI so that it can display the stats of the current player
    public static int[] get_player_stats(){
        return Moderator.get_current_playerStats();
    }

    //When a player is in the CastingOffice and chooses to upgrade, this function is called and forwards the
    //possible upgrades to the view where they will be put in the GUI
    public static void start_upgrade_menu(String[] choices){
        View.upgrade_menu(choices);
    }

    public static String get_current_player_loc(){
        return Moderator.get_player_loc();
    }

    public static void display_results(String message){
        View.end_menu(message);
    }

    public static void change_num_players(){
        View.change_num_players();
    }

}












