package cs412_project.csci412.wwu.edu.cs412_project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EmailActivity extends AppCompatActivity {

    private EditText toEditText;
    private TextView subjectContents;
    private TextView messageContents;

    public Bundle bundle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);
        String message;
        String id;
        String email;

        //get parameters sent through intent
        bundle = this.getIntent().getExtras();
        email = bundle.getString("email");
        id = bundle.getString("id");
        toEditText = findViewById(R.id.edit_text_to);
        subjectContents = findViewById(R.id.text_view_subject_contents);
        messageContents = findViewById(R.id.text_view_message_contents);
        toEditText.setText(email);
        message = "Hello, you device key for Open IR Security is: \n\n" + id + "\n\nPlease follow the " +
                "instructions to enter the key on your device.\nThank you for using Open" +
                " IR Security!";
        messageContents.setText(message);
        Button buttonSend = findViewById(R.id.button_send);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail(v);
            }
        });
    }

    public void sendEmail(View v) {

        String recipientList = toEditText.getText().toString();
        String[] recipients = recipientList.split(",");

        String subject = subjectContents.getText().toString();
        String message = messageContents.getText().toString();

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, recipients);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);

        emailIntent.setType("message/rfc822");
        startActivityForResult(Intent.createChooser(emailIntent, "Choose an email client"), 0);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 0) {
            Intent returnIntent = new Intent();
            setResult(EmailActivity.RESULT_OK, returnIntent);
            finish();
        }
    }
}
