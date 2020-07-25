package cs412_project.csci412.wwu.edu.cs412_project;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

/*
    Singleton class that interfaces the app logic with the project Firebase database.
 */
public class DatabaseManager {
    private FirebaseDatabase db;
    private User user;
    private static DatabaseManager instance;
    private ArrayList<Device> devices;
    private ArrayList<Triggers> triggers;

    public static DatabaseManager getInstance() {
        if (instance == null)
            instance = new DatabaseManager();
        return instance;
    }

    private DatabaseManager() {
        db = FirebaseDatabase.getInstance();
        devices = new ArrayList<>();
        triggers = new ArrayList<>();
    }

    public void setCurrentUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }

    //Used during new user creation in AddUserActivity. Adds a new authenticated user
    //into the Firebase database.
    public void createUser(User u) {
        DatabaseReference ref = db.getReference(u.getId());
        ref.child(u.getId()).setValue(null);
        ref = db.getReference(u.getId());
        ref.child("email").setValue(u.getEmail());
        ref.child("devices").setValue(null);
    }


    public void addDevice(Device device, String userID) {
        DatabaseReference ref = db.getReference( userID + "/devices");
        ref.child(device.getName()).setValue(null);
        ref = db.getReference(userID + "/devices/" + device.getName());
        ref.child("deviceName").setValue(device.getName());
        ref.child("isArmed").setValue(device.isArmed());
        ref.child("isOnline").setValue(false);
        ref.child("wifiSSD").setValue("");
        ref.child("wifiPass").setValue("");
        ref.child("triggerEvents").setValue(null);
        /*ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String deviceName = (String) dataSnapshot.getValue();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        */
    }

    //
    public void addArmedListener(final Device d){
        DatabaseReference ref = db.getReference(user.getId() + "/devices/" + d.getName() + "/isArmed");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean b = (boolean) dataSnapshot.getValue();
                d.setArmed(b);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void setArmed(boolean Armed, Device d) {
        DatabaseReference ref = db.getReference(user.getId() + "/devices/" + d.getName() + "/isArmed");
        ref.setValue(Armed);
        d.setArmed(Armed);
    }

    // Retrieve all devices for a given user from the database and store inside arrayList devices.
    public ArrayList<Device> getDevices() {
        DatabaseReference ref = db.getReference(user.getId() + "/devices");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot newData = dataSnapshot;
                Iterable<DataSnapshot> deviceList = newData.getChildren();
                devices.clear();
                for (DataSnapshot data : deviceList) {
                    String devKey = data.getKey().toString();
                    Log.d("DEBUG", devKey);
                    Device d = new Device(devKey);
                    getInstance().addArmedListener(d); //todo
                    devices.add(d);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return devices;
    }

    public ArrayList<String> getTriggers(Device device) {
        DatabaseReference ref = db.getReference( user.getId() + "/devices/" + device.getName() + "/triggerEvents");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot newData = dataSnapshot;
                Iterable<DataSnapshot> trigList = newData.getChildren();

                String currentDevice = dataSnapshot.getRef().getParent().getKey();
              //  String currentDevice = ref.getParent().getKey();
                int index = 0;

                /* clear triggers that are corresponding device name */
                while (index < triggers.size()) {
                    if (triggers.get(index).getName().equals(currentDevice)) {
                        triggers.get(index).getTriggers().clear();
                        break;
                    }
                    else {
                        index++;
                    }
                }
                ArrayList<String> tempTriggers = new ArrayList<>();
              //  tempTriggers.clear();
                for (DataSnapshot data : trigList) {
                    String devKey = data.getValue().toString();
                    Log.d("DEBUGTrig", devKey);
                    tempTriggers.add(devKey);
                }

                /* there is a list of triggers for the specific device */
                if (triggers.size() > index && triggers.get(index).getTriggers() != null) {
                    triggers.get(index).setTriggers(tempTriggers);
                } else {
                    Triggers t = new Triggers(currentDevice);
                    t.setTriggers(tempTriggers);
                    triggers.add(t);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        for (int i = 0; i < triggers.size(); i++) {
            if (triggers.get(i).getName().equals(device.getName())) {
                return triggers.get(i).getTriggers();
            }
        }
        return null;
    }

    public ArrayList<Triggers> getAllTriggers() {
        for(Device d: devices){
            getTriggers(d);
        }
        return triggers;
    }

    //Internal testing and prototyping methods
    public void delDevice(Device device, String userID) {
        DatabaseReference ref = db.getReference(userID + "/devices/" + device.getName());
        ref.child(device.getName()).removeValue();
    }

    private void addTrigger(String trig, Device device) {
        DatabaseReference ref = db.getReference(user.getId() + "/devices/" + device.getName() + "/triggerEvents");
        ref.child(trig).setValue(trig);
    }

    private void addTimestamp(Device device) {
        DatabaseReference ref = db.getReference(user.getId() + "/devices/" + device.getName() + "/triggerEvents");
        ref.child("Timestamp").setValue(ServerValue.TIMESTAMP);
    }

    private void delTrigger(Device d) {
        //DatabaseReference ref = db.getReference(user.getId() + "/devices/" + device.getName() + "/triggerEvents");
        //ref.child(trig).removeValue();
    }

    private void delUser(User user) {
        DatabaseReference ref = db.getReference("users");
        ref.child(user.getId()).removeValue();
    }

    public void addDeviceToArray(ArrayList<Device> devices, Device d) {
        devices.add(d);
    }

}
