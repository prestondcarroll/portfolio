//Import these for the DomXML Parser

//import com.sun.org.apache.xpath.internal.operations.Mod;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;


public class Moderator {
    private static Players[] PlayerList;
    private static int NumDays;
    private static int CurrentPlayerIndex = 0;
    private static int oldPlayerIndex = 0;
    private static int NumScenes = 10;


    // ReturnPlayers
    // Preconditions:
    //  - The program has just been started
    // Postconditions:
    //  - The set up is finished
    public static void Begin() {
        View view = new View();
        Document boardInfo = XMLParse("board.xml");
        Document locationInfo = XMLParse("cards.xml");

        SetUpGame(boardInfo, locationInfo);
        Day();



    }

    // CreatePlayerList
    // Preconditions:
    //  - The players have entered their nicknames
    // Postconditions:
    //  - The player names are randomized and put in a list for turn order
    static private void CreatePlayerList() {
        LinkedList<String> PlayerSet = GuiComms.GetPlayerNames();
        int size = PlayerSet.size();
        PlayerList = new Players[size];

        for (int i = size - 1; i >= 0; i--) {
            PlayerList[i] = new Players(PlayerSet.remove((int)(Math.random() * i)), size);

        }

        if (size == 2 || size == 3){
            NumDays = 3;
        } else {
            NumDays = 4;
        }

    }


    // SetUpGame
    // Preconditions:
    //  - The beginning of the game
    // Postconditions:
    //  - The other parts of the program have been set up and readied
    static private void SetUpGame(Document boardInfo, Document locationInfo) {
        CreatePlayerList();
        Node office = null;
        NodeList BoardNodes = boardInfo.getDocumentElement().getChildNodes();

        for (int i = 0; i < BoardNodes.getLength(); i++) {
            if (BoardNodes.item(i).getNodeName().equalsIgnoreCase("office")) {
                office = BoardNodes.item(i);
                break;
            }
        }

        Location.SetUpLocations(locationInfo, office);
        GameBoard.SetUpBoard(boardInfo);

    }

    // EndGame
    // Preconditions:
    //  - The conditions for the day to end are met and it's the last day
    // Postconditions:
    //  - The game is over, a winner has been declared, and the users are asked if they would like to play again
    private static void EndGame() {
        LinkedList<String> winners = new LinkedList<String>();
        int maxScore = 0;
        String output;

        //messageUser("Player scores are: \n");
        for (Players Player: PlayerList) {
            Player.CalculateFinalScore();
            if (maxScore < Player.getFinalScore()) {
                maxScore = Player.getFinalScore();
            }
            //messageUser(Player.getPid() + " " + Player.getFinalScore() + "\n");
        }


        for (Players Player: PlayerList) {

            if (maxScore == Player.getFinalScore()) {
                winners.add(Player.getPid());
            }
        }

        if (winners.size() == 1){
            output = winners.getFirst() + " is the winner!\n";
        } else if (winners.size() == 2){
            output = winners.getFirst() + " and " + winners.getLast() + " are the winners!\n";
        } else {
            output = winners.getFirst() + ", ";
            for (int i = 1; i <= winners.size(); i++){
                output += (winners.getFirst() + ", ");
            }

            output += ("and " + winners.getLast() + " ") + "are the winners!\n";

        }

        GuiComms.display_results(output);

    }

    // DaysEnd
    // Preconditions:
    //  - The conditions for a day to end are met
    // Postconditions:
    //  - The players and board are set up for the following day
    private static void DaysEnd() {
        GameBoard.ResetLocations();
        returnPlayers();
    }

    // Day
    // Preconditions:
    //  - Set up of the program is finished
    // Postconditions:
    //  - The end of the last day has elapsed
    // Notes: This function runs continuously until the end of the last day, in which case is calls the EndGame function
    private static void Day() {
        int DayNum = 1;

        while(DayNum <= NumDays){
            while(NumScenes != 1){
                GuiComms.start_default_menu();
                String returnMessage = PlayerList[CurrentPlayerIndex].TakeTurn(GuiComms.PlayerTurn(PlayerList[CurrentPlayerIndex].getPid()));
                while (!returnMessage.equals("Success")) {
                    if (!returnMessage.equals("info")) {
                        returnMessage = PlayerList[CurrentPlayerIndex].TakeTurn(GuiComms.ParseInput(returnMessage));
                    } else {
                        returnMessage = PlayerList[CurrentPlayerIndex].TakeTurn(GuiComms.ParseInput("Would you like to Upgrade, Work, or Move for your turn or would you like your player Info? "));
                    }
                }

                CurrentPlayerIndex++;
                if (!(CurrentPlayerIndex < PlayerList.length)) {
                    CurrentPlayerIndex = 0;
                }
            }

            if (NumScenes == 1){
                DaysEnd();
                DayNum++;
                NumScenes = 10;
            }
        }

        EndGame();

    }

    // XMLParse
    // Preconditions:
    //  - Start of the program
    // Postconditions:
    //  - The XML files are parsed and the contents are given to the relevant parts of the program
    //   to create their data structures
    // Notes:
    private static Document XMLParse(String DocName) {

        DocumentBuilder builder;

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            System.out.print("Error\n");
            builder = null;
        }

        try {
            return builder.parse(new File( DocName ));
        }
        catch (SAXException | IOException e) {
            System.out.print("Error: Not a valid xml file\n");
            return null;
        }
    }

    //Used when a meesage needs to be sent to the user from the Model and a response is needed
    public static String contactUser(String message) {
        return GuiComms.ParseInput(message);
    }

    //Function that acts like a rolled 6 sided die
    public static int rollDie(){
        return 1 + (int)(Math.random() * ((6 - 1) + 1));
    }

    //Function that acts like numDice rolled 6 sided dice. Also sorts the rolls in ascending order
    public static int[] rollDice(int numDice){
        int[] rolls = new int[numDice];
        for (int i = 0; i < numDice; i++){
            rolls[i] = 1 + (int)(Math.random() * ((6 - 1) + 1));
        }

        Arrays.sort(rolls);

        return rolls;

    }

    //When a scene is finished for the day and a user is on the Scene card, this function is called and it distributes
    //the rewards to the players
    public static void itsAWrap(String[] thePlayers, int budget, String location) {
        //messageUser("Now doing payout. \n\n");
        int[] DiceRolls = rollDice(budget);
        Arrays.sort(DiceRolls);
        int numRoles = thePlayers.length;


        int[] rewards = new int[numRoles];
        int roleReward = numRoles - 1;
        for (int i = budget - 1; i > -1; i--) {
            rewards[roleReward] += DiceRolls[i];
            roleReward--;
            if (roleReward == -1) {
                roleReward = numRoles - 1;
            }
        }

        //on the board payout
        for (int i = 0; i < thePlayers.length; i ++) {
            for (Players Player: PlayerList){
                if (thePlayers[i] != null && Player.getPid().equals(thePlayers[i])) {
                    //Moderator.messageUser(Player.getPid() + ", you have earned $" + rewards[i] + " from It's A Wrap!");
                    Player.giveReward(rewards[i]);

                }

            }
        }

        //off the board payout
        for (Players Player: PlayerList){
            if (Player.getLocation().equalsIgnoreCase(location)) {
                Player.changetoNotWorking();
                if (GameBoard.getRoleLevel(Player.getPid(), location) != 0 && Player.isOnBoard()) {
                    System.out.println(GameBoard.getRoleLevel(Player.getPid(), location));
                    Player.giveReward(GameBoard.getRoleLevel(Player.getPid(), location));   //does both players
                }
            }
        }


    }

    //when a day is finished all the players need to be returned to the trailer. Players also need to be changed to not working,
    //along with any info keeping track of where they were working
    public static void returnPlayers(){

        for (Players Player: PlayerList){
            if (Player.isWorking) {
                Player.changetoNotWorking();
                GameBoard.getPiece(Player.getLocation());
            }
        }

        oldPlayerIndex = CurrentPlayerIndex;
        for (int i = 0; i < PlayerList.length; i++) {
            CurrentPlayerIndex = i;
            if (CurrentPlayerIndex >= PlayerList.length) {
                CurrentPlayerIndex = 0;
            }
            GuiComms.move_player(PlayerList[i].get_playerNo(), GameBoard.get_loc_coords("trailer"), "off", "trailer", 1);
            PlayerList[i].returnToTrailers("trailer");

//            if (PlayerList[i].get_isWorking() == false){
//                GuiComms.change_num_players();
//            }
        }
        CurrentPlayerIndex = oldPlayerIndex;
    }

    //When a scene ends, decreases the num of scenes until day's end and makes sure players are no longer able to
    //work in this location
    public static void SceneEnd(String LocName){
        NumScenes--;
        for (Players Player: PlayerList){
            if (Player.getLocation().equalsIgnoreCase(LocName)) {
                Player.isWorking = false;
            }
        }
    }

    //returns the name of the player who is taking their turn
    public static String get_current_player_name(){
        return PlayerList[CurrentPlayerIndex].getPid();
    }

    //returns the number of the player who is taking their turn
    public static int get_current_playerNo(){
        return PlayerList[CurrentPlayerIndex].get_playerNo();
    }

    //returns the stats of the player who is taking their turn
    public static int[] get_current_playerStats(){
        return PlayerList[CurrentPlayerIndex].get_player_stats();
    }

    //If a player chooses to work, calls on the GUI to give the player their turn options
    public static void work() {
        View.work_menu();
    }

    public static String get_player_loc(){
        return PlayerList[CurrentPlayerIndex].get_loc();
    }




}
