import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.LinkedList;

public class TerminalComms {
    static Scanner UserInput = new Scanner(System.in);

    // ReturnPlayers
    // Preconditions:
    //  - User(s) are asked a question and the input needs to be interpreted
    // Postconditions:
    //  - The user input is parsed and the correct function(s) are called
    // Notes:
    public static String ParseInput(String output) {
        System.out.print(output);
        UserInput.nextLine();   //eat newline character
        String returnStr = UserInput.next().trim(); //get input
        return returnStr;
    }

    public static LinkedList<String> GetPlayerNames() {
        System.out.print("How many players are there? ");
        int numPlayers = 0;
        boolean rightNumPlayers = false;

        while (!rightNumPlayers){
            try {
                numPlayers = UserInput.nextInt();
            } catch (InputMismatchException e) {
                System.out.print("Error: Enter a number\n");
                UserInput.nextLine();
            }

            if(numPlayers < 2 || numPlayers > 8){
                System.out.format("Error: Cannot play with %d players\nHow many players are there? ", numPlayers);
                numPlayers = 0;

            } else {
                rightNumPlayers = true;
            }

        }

        LinkedList<String> playerList = new LinkedList<String>();
        for(int i = 1; i <= numPlayers; i++ ){
            System.out.format("Player %d's name: ", i);
            playerList.add(UserInput.next());
        }

        return playerList;

    }


    // OutputMessage
    // Preconditions:
    //  - One or more parts of the program need to communicate with the user(s)
    // Postconditions:
    //  - The message has successfully been sent
    public static void OutputMessage(String output) {
        System.out.print(output);

    }

    public static String PlayerTurn(String PlayerID){
        System.out.format("%s, would you like to Upgrade, Work, or Move for your turn or would you like your player Info? ", PlayerID);
        String response = UserInput.next().trim();




        while(!response.equalsIgnoreCase("upgrade") && !response.equalsIgnoreCase("work") && !response.equalsIgnoreCase("move") && !response.equalsIgnoreCase("info")) {
            System.out.print("Error: Not a possible action, please try again\n");
            System.out.print("Would you like to Upgrade, Work, or Act for your turn or view player Info? ");
            response = UserInput.next().trim();
        }

        return response;
    }

}


