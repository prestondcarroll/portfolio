
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.w3c.dom.*;
import java.util.*;

public class GameBoard {
    //The Arraylist holds all the connections between different locations and
    //SetLocations holds all the different locations on the board
    private static ArrayList<ArrayList<String>> LocationConnections;
    private static GameBoardPieces[] SetLocations = new GameBoardPieces[10];

    //Only called at the beginning of the game, this begins the set up of the board for GameBoardPieces, Sets, and Roles
    public static void SetUpBoard(Document BoardInfo) {
        LocationConnections = new ArrayList<>(10);
        NodeList BoardParts = BoardInfo.getDocumentElement().getChildNodes();
        int CurrentBoardPiece = 0;
        for (int i = 0; i < BoardParts.getLength(); i++){
            if (BoardParts.item(i).getNodeName().equalsIgnoreCase("set") || BoardParts.item(i).getNodeName().equalsIgnoreCase("office") || BoardParts.item(i).getNodeName().equalsIgnoreCase("trailer")) {
                NodeList currentSetInfo = BoardParts.item(i).getChildNodes();
                for (int j = 0; j < currentSetInfo.getLength(); j++) {
                    if (currentSetInfo.item(j).getNodeName().equalsIgnoreCase("neighbors")){
                        ArrayList<String> adjacentSets = new ArrayList<>(4);

                        if (BoardParts.item(i).getNodeName().equalsIgnoreCase("set")) {
                            adjacentSets.add(BoardParts.item(i).getAttributes().getNamedItem("name").getNodeValue());
                        } else if (BoardParts.item(i).getNodeName().equalsIgnoreCase("office")) {
                            adjacentSets.add("office");
                        } else {
                            adjacentSets.add("trailer");
                        }

                        NodeList neighbors = currentSetInfo.item(j).getChildNodes();
                        for (int k = 0; k < neighbors.getLength(); k++) {
                            if (neighbors.item(k).getNodeName().equalsIgnoreCase("neighbor")){
                                adjacentSets.add(neighbors.item(k).getAttributes().getNamedItem("name").getNodeValue());

                            }
                        }

                        LocationConnections.add(adjacentSets);
                    }
                }

                if (BoardParts.item(i).getNodeName().equalsIgnoreCase("set")) {
                    SetLocations[CurrentBoardPiece] = new GameBoardPieces(BoardParts.item(i));
                    CurrentBoardPiece++;
                }

            }
        }
    }

    //Act is called when a player acts. This function takes care of a players acting from the board side. The die is rolled and all the
    //results are calculated. If the Scene is complemented the method checks to see if It's A Wrap triggers and also returns the relevant
    //information for the calling method to give out the reward to the acting player
    public static String Act(int RehearseLevel, String Location, String Pid) {
        GameBoardPieces placeToAct = null;
        for (GameBoardPieces area: SetLocations) {
            if (area.isPiece(Location)) {
                placeToAct = area;
                break;
            }
        }

        if (Moderator.rollDie() + RehearseLevel >= placeToAct.getPartLevel(Pid)) {
            //Moderator.messageUser("Acting Was a Success!\n");
            //Moderator.messageUser("Take " + (placeToAct.getCurrentTakeNumber() + 1) + " finished! " + (placeToAct.getNumTakes() - placeToAct.getCurrentTakeNumber() - 1) + " take(s) left. ");
            if (placeToAct.actSuccess()) {
                //Moderator.messageUser("It's a Wrap!!! ");
                Moderator.itsAWrap(placeToAct.getPlayerList(),placeToAct.getCurrentSet().getBudget(), placeToAct.getName());
            }
            if (placeToAct.onOrOff(Pid)) {
                return "Success On";
            } else {
                return "Success Off";
            }

        } else {
            //Moderator.messageUser("Acting was not successful...");
            if (placeToAct.onOrOff(Pid)) {
                return "Failure On";
            } else {
                return "Failure Off";
            }
        }



    }

    //Checks the location connections to get all the locations that are adjacent to
    //the given location. Those connected locations are then returrned
    public static ArrayList<String> getNearby(String location){
        for(int i = 0; i < LocationConnections.size(); i++) {
            ArrayList<String> adjacentSets = LocationConnections.get(i);
            if (adjacentSets.get(0).equalsIgnoreCase(location)){
                return adjacentSets;
            }
        }

        System.out.printf("Error: no nearby sets. Can not find location %s\n", location);
        return null;


    }

    //The scenes on each Location are reset(new scenes are placed on each location.
    //This should only be called at the end of the day
    public static void ResetLocations() {
        for (GameBoardPieces area: SetLocations) {
            if (!area.dayInfo())
                View.remove_card_from_GUI_for_day(area.getName());
            area.newLocation();
            int[][] takeCoords = area.get_take_coords();
            for (int i = 0; i < area.getNumTakes(); i++) {
                View.undoTake(takeCoords[i]);
            }
        }
    }

    //This function returns the role level for a role that a given player
    //has taken at a given location. This can be used to check if an act is a
    //success
    public static int getRoleLevel(String Pid, String name) {
        for (GameBoardPieces area : SetLocations) {
            if (area.getName().equals(name)) {
                return area.getPartLevel(Pid);

            }

        }
        return 0;
    }

    //This function, if called, returns whether or not a player has taken a role at their given location.
    //It finds the given location and gives the takeRole function at the location the player's ID and rank
    public static boolean takeRole(String Pid, String name, int rank) {
        for (GameBoardPieces area: SetLocations) {
            if (area.getName().equalsIgnoreCase(name)) {
                boolean answer = area.takeRole(Pid, rank);
                if (answer) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    //Returns the role name of the given player at the given location
    public static String getRoleName(String Pid, String name) {
        for(GameBoardPieces area: SetLocations) {
            if (area.getName().equalsIgnoreCase(name)) {
                return area.getPartName(Pid);
            }
        }

        return "no part!(??)"; //for error
    }

    //returns the location with the given name
    public static GameBoardPieces getPiece(String name) {
        for (GameBoardPieces Loc : SetLocations) {
            if (Loc.getName().equalsIgnoreCase(name)) {
                return Loc;
            } else if ((Loc.getName().contains("Main") || Loc.getName().contains("main")) && (name.contains("Main") || name.contains("main"))) {
                return Loc;
            } else if ((Loc.getName().contains("Secret") || Loc.getName().contains("secret")) && (name.contains("Secret") || name.contains("secret"))) {
                return Loc;
            } else if ((Loc.getName().contains("General") || Loc.getName().contains("general")) && (name.contains("General") || name.contains("general"))) {
                return Loc;
            } else if ((Loc.getName().contains("Train") || Loc.getName().contains("train")) && (name.contains("Train") || name.contains("train"))) {
                return Loc;
            }
        }
        return null;
    }

    //returns the location coordinates of the given location
    public static int[] get_loc_coords(String loc){
        if (loc.equalsIgnoreCase("trailer")) {
            int[] returnStuff = {991, 248, 194, 201};
            return returnStuff;
        } else if (loc.equalsIgnoreCase("office")) {
            int[] returnStuff = {9, 459, 208, 209};
            return returnStuff;
        } else {
            for (int i = 0; i <= 10; i++) {
                if (SetLocations[i].getName().equals(loc)) {
                    return SetLocations[i].get_loc_coords();
                }
            }
        }
        return null;
    }

}








