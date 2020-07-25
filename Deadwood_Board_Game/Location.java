import org.w3c.dom.*;
import java.util.*;


public class Location {
    static private CastingOffice Office;
    static private ArrayList<Sets> SetsInfo = new ArrayList<Sets>(40);

    //this function is given a Document that holds all the scene cards and their roles
    //along with a Node containing the info for the CastingOffice and set up the casting office and all
    //the sets and roles in the given doc
    public static void SetUpLocations(Document SetInfoDoc, Node CastingOfficeInfo) {
        SetsInfo = CreateSets(SetInfoDoc);
        Office = new CastingOffice(CastingOfficeInfo);
        //RandomizeSets();
    }


    /*
    private static void RandomizeSets() {

        Integer[] setOrder = new Integer[40];
        for(int i = 0; i < setOrder.length; i++){
            setOrder[i] = i;
        }
        Collections.shuffle(Arrays.asList(setOrder));
        //System.out.println(Arrays.toString(setOrder));

        return;
    }
    */

    //Creates all the sets from the given document
    private static ArrayList<Sets> CreateSets(Document SetInfoDoc){

        NodeList SetListInfo = SetInfoDoc.getDocumentElement().getChildNodes();
        int length = SetListInfo.getLength();
        ArrayList<Sets> SetsInfo = new ArrayList<>(40);


        int currentIndex = 0;

        for (int i = 0; i < length; i++) {
            if (SetListInfo.item(i).getNodeName().equalsIgnoreCase("Card")){
                SetsInfo.add(currentIndex, new Sets(SetListInfo.item(i), i));
                currentIndex++;
            }
        }

        return SetsInfo;
    }

    //acts as a go between fro the Moderator and the Castingoffice function of the same name
    public static boolean canUpgrade (int rank, int money, int fame){
        return Office.canUpgrade(rank, money, fame);
    }

    //gets the upgrade table from the CastingOffice class
    public static int[][] getUpgradeTable (int rank, int money, int fame){
        return Office.getUpgradeTable(rank, money, fame);
    }


    //removes and returns the next scene card from the list
    public static Sets getNextSet() {
        return SetsInfo.remove(0);
    }
}
