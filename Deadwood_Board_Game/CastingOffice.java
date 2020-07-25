import org.w3c.dom.*;


public class CastingOffice {
    private int[][] UpgradeTable = new int[2][6];

//creates a new office object. The office object only holds the upgrade table, which is created from the XML Node,
//which is passed through when the function is called
    public CastingOffice(Node OfficeInfo) {
        NodeList SubInfo;
        SubInfo = OfficeInfo.getChildNodes();
        NodeList children = null;
        for (int i = 0; i < SubInfo.getLength(); i++) {
            if (SubInfo.item(i).getNodeName().equalsIgnoreCase("upgrades")) {
                children = SubInfo.item(i).getChildNodes();
                break;
            }
        }
        NamedNodeMap[] info = new NamedNodeMap[10];
        int InfoIndex = 0;

        for (int i = 0; i<children.getLength(); i++){
            if (children.item(i).getNodeName().equalsIgnoreCase("upgrade")) {
                info[InfoIndex] = children.item(i).getAttributes();
                InfoIndex++;
            }
        }

        int Level;
        for (int i = 0; i < 10; i++) {
            Level = Integer.parseInt(info[i].getNamedItem("level").getNodeValue()) - 1;
            if (info[i].getNamedItem("currency").getNodeValue().equalsIgnoreCase("dollar")){
                UpgradeTable[0][Level] = Integer.parseInt(info[i].getNamedItem("amt").getNodeValue());
            } else if (info[i].getNamedItem("currency").getNodeValue().equalsIgnoreCase("credit")){
                UpgradeTable[1][Level] = Integer.parseInt(info[i].getNamedItem("amt").getNodeValue());
            }

        }
    }

    //returns the full upgrade table when called
    public int[][] getUpgradeTable(int rank, int money, int fame) {
        return UpgradeTable;
   }

   //checks to see if the player can upgrade with money given their current rank and money
    private int validMoneyUpgrade(int money, int rank){
        if(rank < 6 && rank >= 1){
            if(money >= UpgradeTable[0][rank]){
                return UpgradeTable[0][rank];
            }
        }
        return -1;
    }

    //checks to see if the player can upgrade with fame given their current rank and fame
    private int validFameUpgrade(int fame, int rank){
        if(rank < 6 && rank >= 1){
            if(fame >= UpgradeTable[1][rank]){
                return UpgradeTable[1][rank];
            }
        }
        return -1;
    }

    //checks to see if the player can upgrade given their rank, fame, and money by calling the smaller functions
    public boolean canUpgrade(int rank, int money, int fame) {
        int validFame = validFameUpgrade(fame, rank);
        int validMoney = validMoneyUpgrade(money, rank);
        if (validFame == -1 || validMoney == -1) {
            return false;
        }

        return true;
    }



}