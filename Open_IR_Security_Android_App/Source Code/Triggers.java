package cs412_project.csci412.wwu.edu.cs412_project;

import java.util.ArrayList;

public class Triggers {
    private ArrayList<String> triggers;
    private String deviceName;

    public Triggers(String deviceName) {
        this.deviceName = deviceName;
        triggers = new ArrayList<>();
    }

    public void setTriggers(ArrayList<String> triggers) {
        this.triggers = triggers;
    }

    public ArrayList<String> getTriggers() {
        return triggers;
    }

    public String getName() {
        return deviceName;
    }

}
