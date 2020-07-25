//import com.sun.org.apache.xpath.internal.operations.Mod;
import javafx.scene.Scene;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class GameBoardPieces {
    private Sets currentSet;
    private String Name;
    private int currentTakeNumber = 0;
    private int numTakes = 0;
    private ArrayList<Roles> locationRoles = new ArrayList<>();
    private int numRoles = 0;
    private boolean sceneDoneForDay = false;
    private int[] coordinates = new int[4];
    private int[][] takeCoords = new int[3][4];
    private boolean visited = false;


    //Creates a new Location object based off of the XML node passed to it
    public GameBoardPieces(Node info) {
        Name = info.getAttributes().getNamedItem("name").getNodeValue();
        NodeList PieceInfo = info.getChildNodes();

        for (int i = 0; i < PieceInfo.getLength(); i++) {
            if (PieceInfo.item(i).getNodeName().equalsIgnoreCase("parts")){
                NodeList partInfo = PieceInfo.item(i).getChildNodes();
                for (int j = 0; j < partInfo.getLength(); j++){
                    if (partInfo.item(j).getNodeName().equalsIgnoreCase("part")){
                        String PName = partInfo.item(j).getAttributes().getNamedItem("name").getNodeValue();
                        String PLine = partInfo.item(j).getTextContent();
                        int PLevel = Integer.parseInt(partInfo.item(j).getAttributes().getNamedItem("level").getNodeValue());
                        NodeList children = partInfo.item(j).getChildNodes();
                        int[] roleCoord = new int[4];
                        for (int k = 0; k < children.getLength(); k++) {
                            if (children.item(k).getNodeName().equalsIgnoreCase("area")){
                                roleCoord[0] = Integer.parseInt(children.item(k).getAttributes().getNamedItem("x").getNodeValue());
                                roleCoord[1] = Integer.parseInt(children.item(k).getAttributes().getNamedItem("y").getNodeValue());
                                roleCoord[2] = Integer.parseInt(children.item(k).getAttributes().getNamedItem("h").getNodeValue());
                                roleCoord[3] = Integer.parseInt(children.item(k).getAttributes().getNamedItem("w").getNodeValue());
                            }
                        }
                        locationRoles.add(new Roles(PName, PLevel, PLine, roleCoord, "off"));
                        numRoles++;
                    }
                }
            } else if (PieceInfo.item(i).getNodeName().equalsIgnoreCase("takes")){
                NodeList takeInfo = PieceInfo.item(i).getChildNodes();

                int idx = 0;
                for (int j = 0; j < takeInfo.getLength(); j++){
                    if (takeInfo.item(j).getNodeName().equalsIgnoreCase("take")){
                        NodeList takeList = takeInfo.item(j).getChildNodes();
                        if(takeList.item(0).getNodeName().equalsIgnoreCase("area")) {
                            takeCoords[idx][0] = Integer.parseInt(takeList.item(0).getAttributes().getNamedItem("x").getNodeValue());
                            takeCoords[idx][1] = Integer.parseInt(takeList.item(0).getAttributes().getNamedItem("y").getNodeValue());
                            takeCoords[idx][2] = Integer.parseInt(takeList.item(0).getAttributes().getNamedItem("h").getNodeValue());
                            takeCoords[idx][3] = Integer.parseInt(takeList.item(0).getAttributes().getNamedItem("w").getNodeValue());
                            idx++;
                            numTakes++;
                        }
                    }

                }
            }
            else if (PieceInfo.item(i).getNodeName().equalsIgnoreCase("area")){
                coordinates[0] = Integer.parseInt(PieceInfo.item(i).getAttributes().getNamedItem("x").getNodeValue());
                coordinates[1] = Integer.parseInt(PieceInfo.item(i).getAttributes().getNamedItem("y").getNodeValue());
                coordinates[2] = Integer.parseInt(PieceInfo.item(i).getAttributes().getNamedItem("h").getNodeValue());
                coordinates[3] = Integer.parseInt(PieceInfo.item(i).getAttributes().getNamedItem("w").getNodeValue());
            }

        }


        currentSet = Location.getNextSet();
        GuiComms.add_card_to_board(currentSet.getImgID(), Name, 1);
        GuiComms.add_card_to_board("50.png", Name, 6);    //add backside image over the normal one
        GuiComms.add_shot_markers(takeCoords);

        locationRoles.addAll(currentSet.getRoles());
    }

    //Returns true if the given names matches the nae of this piece
    public boolean isPiece(String NameToCheck){
        if (this.Name.equalsIgnoreCase(NameToCheck)){
            return true;
        }

        return false;
    }

    //removes all the roles and current scene info. It then adds a new scene and its role information.
    //It then updates the GUI
    public void newLocation() {
        locationRoles.removeAll(currentSet.getRoles());
        for (Roles role: locationRoles) {
            role.removePlayer();
        }
        currentSet = Location.getNextSet();
        GuiComms.add_card_to_board(currentSet.getImgID(), Name, 1);
        GuiComms.add_card_to_board("50.png", Name, 4);    //add backside image over the normal one
        locationRoles.addAll(currentSet.getRoles());
        visited = false;
        sceneDoneForDay = false;
        currentTakeNumber = 0;
    }

    //Returns the part level for the given player, if that player is here
    public int getPartLevel(String Pid) {
        for (int i = 0; i < locationRoles.size(); i++) {
            if (locationRoles.get(i).getCurrentPlayer() != null && locationRoles.get(i).getCurrentPlayer().equals(Pid)){
                return locationRoles.get(i).getPartLevel();
            }
        }

        return 0;
    }

    //returns the part name for the given player, if that player is here
    public String getPartName(String Pid) {
        for (int i = 0; i < locationRoles.size(); i++) {
            if (locationRoles.get(i).getCurrentPlayer() != null && locationRoles.get(i).getCurrentPlayer().equals(Pid)){
                return locationRoles.get(i).getPartName();
            }
        }
        return null;
    }

    //returns the name of this Location
    public String getName() {
        return this.Name;
    }

    //returns the current set at this location
    public Sets getCurrentSet() {
        return this.currentSet;
    }

    //This function is called if the player's act was a success. It also updates the GUI with any new info
    public boolean actSuccess() {
        if (currentTakeNumber < numTakes ) {
            GuiComms.completeTake(takeCoords[currentTakeNumber]);
        }
        currentTakeNumber++;
        if (currentTakeNumber == numTakes) {
            sceneDoneForDay = true;
            View.remove_card_from_GUI_for_day(Name);
            //Moderator.messageUser("Scene successfully completed!\n");
            Moderator.SceneEnd(Name);


            for (int i = numRoles; i < locationRoles.size(); i++) {
                if (locationRoles.get(i).isPlayerHere()) {
                    return true;
                }
            }
        }
        return false;
    }

    //Returns a list the size of the number of roles, filled in with the names of the
    //players at the index of their respective roles
    public String[] getPlayerList() {
        String[] playerList = new String[locationRoles.size() - numRoles];
        int index = 0;
        for (int i = numRoles; i < locationRoles.size(); i++) {
            playerList[index] = locationRoles.get(i).getCurrentPlayer();
        }
        return playerList;
    }

    //Prompts the player with all the roles they can take at a given location in the GUI. If they accept a role, the role is updated to reflect that
    //and true is returned. Otherwise false is returned
    public boolean takeRole(String Pid, int rank) {
        String[] availRoles = new String[locationRoles.size() + 1];
        availRoles[locationRoles.size()] = "No Role";
        for (int i = 0; i < locationRoles.size() ; i++) {
           if (locationRoles.get(i).getPartLevel() <= rank && locationRoles.get(i).isPlayerHere() == false) {
                availRoles[i] = locationRoles.get(i).getPartName();
           } else {
               availRoles[i] = null;
           }
        }

        View.role_menu(availRoles);
        String answer = GuiComms.ParseInput("");


        if (!answer.equals(Integer.toString(locationRoles.size()))){
            locationRoles.get(Integer.parseInt(answer)).setCurrentPlayer(Pid);
            GuiComms.move_player(Moderator.get_current_playerNo(),
                    locationRoles.get(Integer.parseInt(answer)).getCoordinates(),
                    locationRoles.get(Integer.parseInt(answer)).get_onOrOff(),
                    Name, 0);
            return true;
        } else {
            return false;
        }
    }

    //returns true if the player's role is on the card and false if it isn't
    public boolean onOrOff(String Pid) {
        for (int i = 0; i < locationRoles.size(); i++) {
            if (locationRoles.get(i).getCurrentPlayer() != null && locationRoles.get(i).getCurrentPlayer().equals(Pid) && i < numRoles) {
                return true;
            } else if (locationRoles.get(i).getCurrentPlayer() != null && locationRoles.get(i).getCurrentPlayer().equals(Pid) && i >= numRoles) {
                return false;
            }
        }
        return false;
    }

    //returns the info saying whether or not the scene has been completed for the day
    public boolean dayInfo() {
        return sceneDoneForDay;
    }

    //returns the number of overall takes
    public int getNumTakes() {
        return numTakes;
    }

    //returns the number of takes that have been completed
    public int getCurrentTakeNumber() {
        return currentTakeNumber;
    }

    //returns the location coordinates for this location
    public int[] get_loc_coords(){
        return this.coordinates;
    }

    public int[][] get_take_coords(){
        return this.takeCoords;
    }

    //returns the value keeping track of whether or not this place has been visited during this day
    public boolean isVisited() {
        return visited;
    }

    //Called if the location gets visited in a day
    public void visit() {
        visited = true;
    }

    public void removePlayer(String Pid) {
        for (int i = 0; i < numRoles; i++) {
            if (locationRoles.get(i).getCurrentPlayer().equals(Pid)) {
                locationRoles.get(i).removePlayer();
            }
        }
    }
}
