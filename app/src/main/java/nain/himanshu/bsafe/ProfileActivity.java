package nain.himanshu.bsafe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class ProfileActivity extends AppCompatActivity {

    private TextInputEditText mName, mEmail, mPass, mMessage;
    private Button mSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mName = findViewById(R.id.name);
        mEmail = findViewById(R.id.email);
        mPass = findViewById(R.id.password);
        mMessage = findViewById(R.id.message);

        mSubmit = findViewById(R.id.submit);
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        loadData();

    }

    private void saveData() {

        SharedPreferences.Editor editor = getSharedPreferences(Config.PREFS, MODE_PRIVATE).edit();
        editor.clear();
        editor.putString(Config.NAME_FIELD, mName.getText().toString());
        editor.putString(Config.EMAIL_FIELD, mEmail.getText().toString());
        editor.putString(Config.PASSWORD_FIELD, mPass.getText().toString());
        editor.putString(Config.MESSAGE_FIELD, mMessage.getText().toString());
        editor.apply();
        Toast.makeText(this, "Details Saved", Toast.LENGTH_SHORT).show();

    }

    private void loadData() {

        SharedPreferences preferences = getSharedPreferences(Config.PREFS, MODE_PRIVATE);
        mName.setText(preferences.getString(Config.NAME_FIELD, getString(R.string.default_name)));
        mMessage.setText(preferences.getString(Config.MESSAGE_FIELD, getString(R.string.default_message)));
        mEmail.setText(preferences.getString(Config.EMAIL_FIELD, ""));
        mPass.setText(preferences.getString(Config.PASSWORD_FIELD, ""));

    }


}
