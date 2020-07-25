public class Roles {
    private String PartName = null;
    private int PartLevel = 0;
    private String PartLine = null;
    private String CurrentPlayer = null;
    private int[] coordinates;
    private String onOrOff = null;

    public Roles (String PName, int PLevel, String PLine, int[] coord, String placement){
        PartName = PName;
        PartLevel = PLevel;
        PartLine = PLine;
        coordinates = coord;
        onOrOff = placement;
    }

    public String getPartName() {
        return PartName;
    }

    public int getPartLevel() {
        return PartLevel;
    }

    public String getPartLine() {
        return PartLine;
    }

    public String getCurrentPlayer() {
        return CurrentPlayer;
    }

    //checks to see if there is currently a player
    public boolean isPlayerHere() {
        return !(CurrentPlayer == null);
    }

    //sets the current player
    public void setCurrentPlayer(String Pid) {
        if (CurrentPlayer != null) {
            //Moderator.messageUser("Error: There is already a player in this role");
        } else {
            CurrentPlayer = Pid;
        }

    }

    public int[] getCoordinates() {

        return coordinates;
    }
    //removes current player
    public void removePlayer(){
        CurrentPlayer = null;
    }

    public String get_onOrOff(){
        return onOrOff;
    }


}
