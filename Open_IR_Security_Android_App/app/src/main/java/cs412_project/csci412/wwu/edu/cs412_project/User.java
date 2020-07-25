package cs412_project.csci412.wwu.edu.cs412_project;

import java.util.ArrayList;

/**
 * Created by propers on 10/23/18.
 */

public class User {
    private String id; //reference id provided by firebase (UUID) of user.
    private ArrayList<Device> devices;
    private String email; //username


    public User(String id, String email) {
        this.id = id;
        this.email = email;

        devices = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<Device> getDevices() {
        return devices;
    }

    public void setDevices(ArrayList<Device> devices) {
        this.devices = devices;
    }

    public void addDevices(Device device) {
        this.devices.add(device);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
