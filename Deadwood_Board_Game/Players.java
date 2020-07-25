//import com.sun.org.apache.xpath.internal.operations.Mod;

import java.util.ArrayList;

public class Players {
    private String Pid;
    private static int playerCounter = 0;
    private int playerNo;
    private int Fame;
    private int Rank;
    private int Money;
    private int RehearseLvl;
    private String CurrentLocation;
    private int FinalScore = 0;
    public boolean isWorking = false;
    private boolean onBoard = false;

    // Players Constructor
    // Preconditions:
    //  - Need to create a player to represent a user
    // Postconditions:
    //  - The user has a virtual representation
    public Players(String PlayerName, int numPlayers) {
        Pid = PlayerName;
        playerCounter++;
        playerNo = playerCounter;
        Money = 0;
        RehearseLvl = 0;
        CurrentLocation = "trailer";
        GuiComms.update_location(playerNo, CurrentLocation);

        if (numPlayers == 5){
            Fame = 2;
            Rank = 1;
        } else if (numPlayers == 6){
            Fame = 4;
            Rank = 1;
        } else if (numPlayers == 7 || numPlayers == 8){
            Fame = 0;
            Rank = 2;
        } else {
            Fame = 0;
            Rank = 1;
        }

    }

    // TakeTurn()
    // Preconditions:
    //  - The beginning of a user's turn
    // Postconditions:
    //  - The player's turn has finished
    public String TakeTurn(String Action) {
        if (Action.equalsIgnoreCase("Work")){
            if (isWorking) {
                Moderator.work();
                System.out.println("test");
                String actOrRehearse = GuiComms.ParseInput("");
                System.out.println(actOrRehearse);
                if (actOrRehearse.equalsIgnoreCase("Act")) {
                        Act();
                        return "Success";
                } else if (actOrRehearse.equalsIgnoreCase("Rehearse")) {

                    if (RehearseLvl < 6) {
                        if (1 + this.RehearseLvl <= GameBoard.getRoleLevel(Pid, CurrentLocation)) {
                            Rehearse();
                            return "Success";
                        } else {
                            return "Rehearse level is at the max.\nPlease choose a different action: ";
                        }
                    } else {
                        return "Rehearse level is at the max.\nPlease choose a different action: ";
                    }
                }
            } else {
                return "Error: You currently do not have a role\nPick a different action: ";
            }
        } else if (Action.equalsIgnoreCase("Info")){
            String message = "Player ID: " + Pid + "\n";
            message += "Player Rank: " + Rank + "\n";
            message += "Player Fame: " + Fame + "\n";
            message += "Player Money: " + Money + "\n";
            message += "Current Location: " + CurrentLocation + "\n";
            if (isWorking) {
                message += "Rehearse Level: " + RehearseLvl + "\n";
                message += "Current Role: " + GameBoard.getRoleName(Pid, this.CurrentLocation) + "\n";
            }
            //Moderator.messageUser(message);
            return "info";
        } else if(Action.equalsIgnoreCase("Move")) {

            if (!isWorking) {
                if (Move()) {
                    isWorking = true;
                }
                return "Success";
            } else {
                return "Error: Currently acting, cannot move\nPick a different action: ";
            }
        } else if (Action.equalsIgnoreCase("Upgrade")) {
                System.out.println("here");
                if (this.CurrentLocation.equals("office") && Rank < 6) {
                    if (Location.canUpgrade(Rank, Money, Fame)) {
                        Upgrade();
                        return "Success";
                    }  else {
                    return "Error: Insufficient money and fame to upgrade\nPlease choose a di" +
                            "fferent action: ";
                    }
                } else if (this.CurrentLocation.equals("office") && Rank == 6) {
                    return "Error: Rank cannot be upgraded further\nPlease choose a different action: ";
                } else {
                    return "Error: Incorrect Location, unable to upgrade if not in the Casting Office\nPlease choose a different action: ";
                }

        } else {
            return "Error: Invalid option\nPlease try again: \nUpgrade, Work, Move, or get Info: ";
        }

        return "Something is Wrong";
    }

    // Rehearse
    // Preconditions:
    //  - The player has chosen rehearse as their action
    // Postconditions:
    //  - The player has rehearsed
    private void Rehearse() {
        changeRehearseLvl(1);
       // Moderator.messageUser("Your rehearse level is now " + this.RehearseLvl + "\n" + "");
    }

    // Act
    // Preconditions:
    //  - The player has chosen Act as their action
    // Postconditions:
    //  - The player has rehearsed
    private void Act() {
        String todo = GameBoard.Act(RehearseLvl, CurrentLocation, Pid);
        String[] tasks = todo.split(" ");
        if (tasks[0].equals("Success")) {
            if (tasks[0].equals("On")) {
                //Moderator.messageUser("\nYou gained 2 fame\n");
                changeFame(2);
            } else {
                //Moderator.messageUser("\nYou gained $1 and 1 fame\n");
                changeFame(1);
                changeMoney(1);
            }
        } else {
            if (tasks[0].equals("On")) {
                //Moderator.messageUser("\nYou didn't gain anything\n");
            } else {
                //Moderator.messageUser("\nYou still gained $1\n");
                changeMoney(1);
            }
        }
    }

    // Move
    // Preconditions:
    //  - The player has chosen to move as their action
    // Postconditions:
    //  - The player has moved locations
    private boolean Move() {
        String message = "";
        String Loc;

        //System.out.println("Where do you want to move?");
        ArrayList<String> nearby = GameBoard.getNearby(CurrentLocation);

        //if (nearby != null) {
        //    nearby.remove(CurrentLocation);
        //}
        GuiComms.send_locations(nearby);
        //for(int i = 1; i < nearby.size(); i++){
        //    System.out.printf("\t%s\n", nearby.get(i));
        //}

        Loc = Moderator.contactUser(message);

        if (!Loc.equalsIgnoreCase("trailer") && !Loc.equalsIgnoreCase("office")) {
            if (!GameBoard.getPiece(Loc).isVisited()) {
                GuiComms.visitLoc(Loc);
                GameBoard.getPiece(Loc).visit();
            }
        }
        GuiComms.move_player(playerNo, GameBoard.get_loc_coords(Loc), "off", Loc, 1);
        changeLocation(Loc);



        if (!Loc.equals("trailer") && !Loc.equals("office") && !GameBoard.getPiece(Loc).dayInfo()) {
            String response = GuiComms.ParseInput("Would you like to take a look at the available roles at this location?(y/n) ");

            if (response.equalsIgnoreCase("y")) {
                if (GameBoard.takeRole(Pid, CurrentLocation, Rank)) {
                    return true;
                }
            }
        }
        return false;
    }

    //Checks to see if the given Loc is in the neaby locations
    private boolean containsLoc(ArrayList<String> nearby, String Loc){
        for (int i =0; i < nearby.size(); i++) {
            if (Loc.length() > 2) {
                if (nearby.contains(Loc) || nearby.contains(Loc.substring(0, 1).toUpperCase() + Loc.substring(1))) {
                    return true;
                } else if (nearby.get(i).contains("Main") && (Loc.contains("Main") || Loc.contains("main"))) {
                    return true;
                } else if (nearby.get(i).contains("Secret") && (Loc.contains("Secret") || Loc.contains("secret"))) {
                    return true;
                } else if (nearby.get(i).contains("General") && (Loc.contains("General") || Loc.contains("general"))) {
                    return true;
                } else if (nearby.get(i).contains("Train") && (Loc.contains("Train") || Loc.contains("train"))) {
                    return true;
                }
            }
        }
        return false;
    }

    // Upgrade
    // Preconditions:
    //  - The player has chosen to upgrade as their action
    // Postconditions:
    //  - The player has chosen whether or not to upgrade
    private void Upgrade() {
        int[][] info = Location.getUpgradeTable(Rank, Money, Fame);

        if (info == null){
            Moderator.contactUser("Insufficient funds for upgrading");
            return;
        }
        String message ="You can upgrade to Rank ";
        int NextRank = Rank + 1;
        int AvailableUpgrades = 0;
        String[] upgrades = new String[10]; //strings of different upgrade action possiblities for gui
        int idx = 0;


        for (int i = NextRank; i < 7; i ++) {
            if (info[0][i-1] <= Money || (info[1][i-1] <= Fame)) {
                AvailableUpgrades++;
            }
        }
        int maxRank = Rank + AvailableUpgrades;

        if(AvailableUpgrades == 1) {
            if (info[0][NextRank-1] <= Money && info[1][NextRank-1] <= Fame) {
                message += NextRank + " for " + info[0][NextRank-1] + " Dollars or " + info[1][NextRank-1] + " Fame";
                upgrades[idx] = NextRank + " money";
                idx++;
                upgrades[idx] = NextRank + " fame";

            } else if (info[0][NextRank-1] <= Money) {
                message += NextRank + " for " + info[0][NextRank-1] + " Dollars;";
                upgrades[idx] = NextRank + " money";
            } else {
                message += NextRank + " for " + info[1][NextRank-1] + " Fame";
                upgrades[idx] = NextRank + " fame";
            }
        } else if (AvailableUpgrades == 2) {
            if (info[0][NextRank-1] <= Money && info[1][NextRank-1] <= Fame) {
                message += NextRank + " for " + info[0][NextRank-1] + " Dollars or " + info[1][NextRank-1] + " Fame";
                upgrades[idx] = NextRank + " money";
                idx++;
                upgrades[idx] = NextRank + " fame";
                idx++;
            } else if (info[0][NextRank-1] <= Money) {
                message += NextRank + " for " + info[0][NextRank-1] + " Dollars;";
                upgrades[idx] = NextRank + " money";
                idx++;
            } else {
                message += NextRank + " for " + info[1][NextRank-1] + " Fame";
                upgrades[idx] = NextRank + " fame";
                idx++;
            }

            NextRank++;
            message += ", or to Rank ";

            if (info[0][NextRank-1] <= Money && info[1][NextRank-1] <= Fame) {
                message += NextRank + " for " + info[0][NextRank] + " Dollars or " + info[1][NextRank] + " Fame";
                upgrades[idx] = NextRank + " money";
                idx++;
                upgrades[idx] = NextRank + " fame";
            } else if (info[0][NextRank-1] <= Money) {
                message += NextRank + " for " + info[0][NextRank-1] + " Dollars;";
                upgrades[idx] = NextRank + " money";
            } else {
                message += NextRank + " for " + info[1][NextRank-1] + " Fame";
                upgrades[idx] = NextRank + " fame";
            }
        } else if (AvailableUpgrades >= 3) {
            while(AvailableUpgrades > 1) {
                if (info[0][NextRank-1] <= Money && info[1][NextRank-1] <= Fame) {
                    message += NextRank + " for " + info[0][NextRank-1] + " Dollars or " + info[1][NextRank-1] + " Fame";
                    upgrades[idx] = NextRank + " money";
                    idx++;
                    upgrades[idx] = NextRank + " fame";
                    idx++;
                } else if (info[0][NextRank-1] <= Money) {
                    message += NextRank + " for " + info[0][NextRank-1] + " Dollars;";
                    message += NextRank + " for " + info[0][NextRank-1] + " Dollars;";
                    upgrades[idx] = NextRank + " money";
                    idx++;
                } else {
                    message += NextRank + " for " + info[1][NextRank-1] + " Fame";
                    upgrades[idx] = NextRank + " fame";
                    idx++;
                }

                NextRank++;
                AvailableUpgrades--;
                message += ", to Rank ";
            }

            //NextRank++;
            message += ", or to Rank ";

            if (info[0][NextRank-1] <= Money && info[1][NextRank-1] <= Fame) {
                message += NextRank + " for " + info[0][NextRank-1] + " Dollars or " + info[1][NextRank-1] + " Fame.";
                upgrades[idx] = NextRank + " money";
                idx++;
                upgrades[idx] = NextRank + " fame";
            } else if (info[0][NextRank] <= Money) {
                message += NextRank + " for " + info[0][NextRank-1] + " Dollars.;";
                upgrades[idx] = NextRank + " money";
            } else {
                message += NextRank + " for " + info[1][NextRank-1] + " Fame.";
                upgrades[idx] = NextRank + " fame";
            }

        }

        message += "\nEnter your decision as the rank you want, followed by which currency you will use (e.g. 4 money): ";
        GuiComms.start_upgrade_menu(upgrades);

        int valid = 0;
        while(valid != 1) {
            try {
                String UpgradeChoice = Moderator.contactUser(message);
                String[] splitUpgradeChoice = UpgradeChoice.split(" ");
                int newRank = Integer.parseInt(splitUpgradeChoice[0]);
                if(newRank <= maxRank) {
                    changeRank(newRank);
                    if (splitUpgradeChoice[1].equalsIgnoreCase("money")) {
                        changeMoney(info[0][newRank - 1] * -1);
                        valid = 1;
                    } else if (splitUpgradeChoice[1].equalsIgnoreCase("fame")) {
                        changeFame((info[1][newRank - 1]) * -1);
                        valid = 1;
                    } else {
                        System.out.println("Invalid currency. Try again");
                        valid = 0;
                    }
                }
                else{
                    valid = 0;
                }
            }
            catch (NumberFormatException e){
                System.out.println("Not a number. Try again.");
            }
            message = "Please Try Again";
        }
        System.out.println("You've been upgraded!");
        for (String str: upgrades){
            System.out.println(str);
        }
    }

    //used to calculate the final score of the player. Can be used any time and will just update the ranking
    public void CalculateFinalScore() {
        FinalScore = getMoney() + getFame() + getRank() * 5;
    }

    //returns the final score
    public int getFinalScore() {
        return this.FinalScore;
    }

    //returns player ID
    public String getPid() {
        return this.Pid;
    }

    //returns player rank
    public int getRank() {
        return this.Rank;
    }

    //returns player fame
    public int getFame() {
        return this.Fame;
    }

    //returns player money
    public int getMoney() {
        return this.Money;
    }

    //returns player rehearse level
    public int getRehearseLvl() {
        return this.RehearseLvl;
    }

    //returns player location
    public String getLocation() {
        return this.CurrentLocation;
    }

    //checks to see if the player is on the board
    public boolean isOnBoard(){
        return onBoard;
    }

    //if the player changes position this function is used
    public void changeOnBoard(boolean value){
        this.onBoard = value;
    }

    //used when a player is no longer working
    public void changetoNotWorking(){
        this.isWorking = false;
    }

    //when a player upgrade this is used to change them to their new rank
    private void changeRank(int newRank) {
        Rank = newRank;
    }

    //changes a player's fame
    private void changeFame(int FameChange) {
        Fame += FameChange;
    }

    //changes a player's money
    private void changeMoney(int MoneyChange) {
        Money += MoneyChange;
    }
    //changes a player's rehearse level
    private void changeRehearseLvl(int RehearseChange) {
        RehearseLvl += RehearseChange;
    }

    //changes a player's location
    private void changeLocation(String newLocation) {
        if (newLocation.equalsIgnoreCase("main")){
            CurrentLocation = "Main Street";
        }
        else if (newLocation.equalsIgnoreCase("secret")){
            CurrentLocation = "Secret Hideout";
        }
        else if (newLocation.equalsIgnoreCase("train")){
            CurrentLocation = "Train Station";
        }
        else if (newLocation.equalsIgnoreCase("general")){
            CurrentLocation = "General Store";
        }
        else {
            CurrentLocation = newLocation;
        }
        System.out.println("newLocation is: " + CurrentLocation);
    }

    //returns a player to the trailers
    public void returnToTrailers(String loc){
        changeLocation(loc);
    }

    //when a player succeeds, give them the proper money amount
    public void giveReward(int money) {
        this.changeMoney(money);
    }

    //returns player num
    public int get_playerNo(){
        return playerNo;
    }

    //returns player stats
    public int[] get_player_stats(){
        int[] player_stats = new int[4];
        player_stats[0] = Money;
        player_stats[1] = Fame;
        player_stats[2] = Rank;
        player_stats[3] = RehearseLvl;
        return player_stats;
    }

    public String get_loc(){
        return CurrentLocation;
    }

    public boolean get_isWorking(){
        return isWorking;
    }

}