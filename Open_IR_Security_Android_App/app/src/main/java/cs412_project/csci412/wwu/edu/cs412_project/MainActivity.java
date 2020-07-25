package cs412_project.csci412.wwu.edu.cs412_project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private DatabaseManager dbm;
    private User user;
    private FirebaseAuth mAuth;
    private Timer autoUpdate;
    private boolean stopUpdatingView;

    //private User;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        stopUpdatingView = false;

        //Authentication stuff
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivityForResult(i, 0);
        } else {

            user = new User(currentUser.getUid(), currentUser.getEmail());
            dbm = DatabaseManager.getInstance();
            dbm.setCurrentUser(user);

            /* ===================to test database =======================
            dbm.createUser(user);
            Device d1 = new Device("Device 1");
            dbm.addDevice(d1, user.getId());
            Device d2 = new Device("Device 2");
            dbm.addDevice(d2, user.getId());
            // dbm.delDevice(d2, test.getId());
            //dbm.addTrigger("trigger1", d1, test);
            //dbm.delTrigger("trigger1", d1, test.getId());
            //User test2 = new User("21", "1234123", "cramerg2@wwu.edu");
            //dbm.createUser(test2);
            //dbm.delUser(test2);

            d1.addTrigger("Oct 30, 2018 at 16:18");
            d1.addTrigger("Oct 31, 2018 at 12:34");
            d2.addTrigger("Oct 28, 2018 at 1:14");
            dbm.addTrigger(d1.getTriggers().get(0), d1, user);
            dbm.addTrigger(d1.getTriggers().get(1), d1, user);
            dbm.addTrigger(d2.getTriggers().get(0), d2, user);
            user.addDevices(d1);
            user.addDevices(d2);
            dbm.setCurrentUser(user);
            */

        }

        /* button functionality */
        Button addsensor = findViewById(R.id.addsensorbutton);
        addsensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Activity_AddSensor.class));
            }
        });
        Button viewall = findViewById(R.id.viewallbutton);
        viewall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!dbm.getDevices().isEmpty()) {
                    startActivity(new Intent(MainActivity.this, Activity_log.class));
                }
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }


    public void updateView() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // Toast.makeText(this, "update VIEW", Toast.LENGTH_SHORT).show();
        if (currentUser == null) {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            // startActivityForResult(i,0);
        } else {
            TableLayout sensors = findViewById(R.id.tableLayout2);
            TableLayout alerts = findViewById(R.id.tableLayout);

            TableLayout.LayoutParams tlp = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
            TableRow.LayoutParams rlp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

            String test = "asdfasdfasdfasfd";
            TextView sensorTv, logTv;
            TableRow sensorRow, logRow;

            //Query List of Devices
            ArrayList<Device> devices = dbm.getDevices();

            //ArrayList<String> triggers;
            sensors.removeAllViews();
            int numDevices = devices.size();
            for (int x = 0; x < numDevices; x++) {
                sensorTv = new TextView(this);
                sensorTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                sensorTv.setLayoutParams(rlp);

                sensorTv.setText(devices.get(x).getName());

                //final String deviceName = devices.get(x).getName();

                sensorTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TextView tv = (TextView) view;
                        String deviceName = tv.getText().toString();
                        Intent intent = new Intent(MainActivity.this, Sensor_Activity.class);
                        intent.putExtra("devicename", deviceName);
                        startActivity(intent);
                    }
                });
                sensorRow = new TableRow(this);
                sensorRow.setLayoutParams(tlp);
                sensorRow.addView(sensorTv);
                sensors.addView(sensorRow, tlp);
            }

            /* update view of triggers */
            alerts.removeAllViews();
            ArrayList<Long> allDevicesDates = new ArrayList<>();
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat date = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

            ArrayList<Triggers> triggers = dbm.getAllTriggers();
            for (int y = 0; y < triggers.size(); y++) {
                allDevicesDates.clear();

                    /* get triggers of specific device if there is any*/
                    if (triggers.get(y).getTriggers().size() > 0) {
                        logTv = new TextView(this);
                        logTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                        logTv.setLayoutParams(rlp);
                        logTv.setText(triggers.get(y).getName() + ":");
                        logRow = new TableRow(this);
                        logRow.setLayoutParams(tlp);
                        logRow.addView(logTv);
                        alerts.addView(logRow, tlp);
                    }
                    Collections.reverse(triggers.get(y).getTriggers());
                    for (int z = 0; z < triggers.get(y).getTriggers().size() && z < 4; z++) {
                        allDevicesDates.add(Long.parseLong(triggers.get(y).getTriggers().get(z)));
                    }
                    /* sort all triggers */
                    if (allDevicesDates.size() != 0) {
                        Collections.sort(allDevicesDates);
                        Collections.reverse(allDevicesDates);

                    }

                    for (int x = 0; x < allDevicesDates.size(); x++) {
                        logTv = new TextView(this);
                        logTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                        logTv.setLayoutParams(rlp);
                        cal.setTimeInMillis(allDevicesDates.get(x));
                        logTv.setText("\t\t\t" + date.format(cal.getTime()));
                        logRow = new TableRow(this);
                        logRow.setLayoutParams(tlp);
                        logRow.addView(logTv);
                        alerts.addView(logRow, tlp);
                    }

            }

            TextView userText = findViewById(R.id.userEmailText);
            userText.setText(user.getEmail());
        }
    }

    @Override

    protected void onResume() {
        super.onResume();
        stopUpdatingView = false;
        updateView();
        autoUpdate = new Timer();
        autoUpdate.schedule(new TimerTask() {
            @Override
            public void run() {
                if (stopUpdatingView) { //kill current thread
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateView();
                        //Toast.makeText(MainActivity.this, "refreshed view", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }, 1500, 2000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0: //login activity, create user activity
                FirebaseUser currentUser = mAuth.getCurrentUser();
                user = new User(currentUser.getUid(), currentUser.getEmail());
                dbm = DatabaseManager.getInstance();
                dbm.setCurrentUser(user);
                updateView();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        autoUpdate.cancel(); //cancels all future threads, not current executing one
        stopUpdatingView = true; // flag that allows current thread to exit
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.user_logout:
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivityForResult(i,0);
        }
        return super.onOptionsItemSelected(item);
    }
}