import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.ArrayList;

public class Sets {
    private String LocName;
    private int Budget;
    private String Description;
    private String img;
    private int LocNum;
    private int setID;
    private ArrayList<Roles> RoleList = new ArrayList<>();
    private boolean flipped = false;

    //private int[] coordinates = new int[4];

    // Sets Constructor
    // Preconditions:
    //  - Have the information for a set
    // Postconditions:
    //  - a new set is created and added to the list of sets
    public Sets(Node SetInfo, int numSet) {

        setID = numSet;
        NamedNodeMap Attributes = SetInfo.getAttributes();
        LocName = Attributes.getNamedItem("name").getNodeValue();
        Budget = Integer.parseInt(Attributes.getNamedItem("budget").getNodeValue());
        img = Attributes.getNamedItem("img").getNodeValue();



        NodeList SceneAndParts = SetInfo.getChildNodes();

        for (int i = 0; i < SceneAndParts.getLength(); i++) {
            if (SceneAndParts.item(i).getNodeName().equalsIgnoreCase("Scene")) {
                LocNum = Integer.parseInt(SceneAndParts.item(i).getAttributes().getNamedItem("number").getNodeValue());
                Description = SceneAndParts.item(i).getTextContent();
            } else if (SceneAndParts.item(i).getNodeName().equalsIgnoreCase("part")) {
                String PName = SceneAndParts.item(i).getAttributes().getNamedItem("name").getNodeValue();
                int PLevel = Integer.parseInt(SceneAndParts.item(i).getAttributes().getNamedItem("level").getNodeValue());
                NodeList subInfo = SceneAndParts.item(i).getChildNodes();
                String PLine = null;
                int[] coordinates = new int[4];
                for (int j = 0; j < subInfo.getLength(); j++) {
                    if (subInfo.item(j).getNodeName().equalsIgnoreCase("Line")) {
                        PLine = subInfo.item(j).getTextContent();
                    }
                    else if (subInfo.item(j).getNodeName().equalsIgnoreCase("area")){
                        coordinates[0] = Integer.parseInt(subInfo.item(j).getAttributes().getNamedItem("x").getNodeValue());
                        coordinates[1] = Integer.parseInt(subInfo.item(j).getAttributes().getNamedItem("y").getNodeValue());
                        coordinates[2] = Integer.parseInt(subInfo.item(j).getAttributes().getNamedItem("h").getNodeValue());
                        coordinates[3] = Integer.parseInt(subInfo.item(j).getAttributes().getNamedItem("w").getNodeValue());
                    }
                }

                RoleList.add(new Roles(PName, PLevel, PLine, coordinates, "on"));

            }


        }


    }

    public String getName() {
        return this.LocName;
    }

    public int getBudget() {
        return this.Budget;
    }

    public String getDescription() {
        return this.Description;
    }

    public int getLocNum() {
        return this.LocNum;
    }

    public ArrayList<Roles> getRoles() {
        return this.RoleList;
    }

    public boolean isFlipped() {
        return this.flipped;
    }

    public int getSetID(){
        return this.setID;
    }

    public String getImgID(){
        return img;
    }

}