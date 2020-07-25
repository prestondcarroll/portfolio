package cs412_project.csci412.wwu.edu.cs412_project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_AddSensor extends AppCompatActivity {
    private DatabaseManager dbm = DatabaseManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addsensor);

        /* back to main activity */
        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        /* Add sensor */
        Button addSensor = findViewById(R.id.confirm_button);
        addSensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDevice(view);
            }
        });
    }

    public void createDevice(View v) {
        EditText name_field = (EditText) findViewById(R.id.name_field);
        CheckBox enable_check = (CheckBox) findViewById(R.id.enable_check);
        boolean armed = enable_check.isChecked();
        if (name_field.getText().toString().equals("")) {
            Toast.makeText(this, "Sensor name can not be empty.", Toast.LENGTH_LONG).show();
            return;
        }

        if(name_field.getText().length() >= 20){
            Toast.makeText(this, "Sensor name is too long", Toast.LENGTH_SHORT).show();
            return;
        }

        Device d = new Device(name_field.getText().toString().trim());
        d.setArmed(armed);
        dbm.getUser().addDevices(d);
        dbm.addDevice(d, dbm.getUser().getId());
        Toast.makeText(this, "Sensor Added", Toast.LENGTH_LONG).show();
        name_field.setText("");
        enable_check.setChecked(false);
    }

    public void goBack(View v) {
        this.finish();
    }
}
