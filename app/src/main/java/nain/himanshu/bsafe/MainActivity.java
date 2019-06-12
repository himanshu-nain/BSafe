package nain.himanshu.bsafe;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import im.delight.android.location.SimpleLocation;
import nain.himanshu.bsafe.Database.AppDatabase;

public class MainActivity extends AppCompatActivity {

    private Button mPanicButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPanicButton = findViewById(R.id.panicButton);
        mPanicButton.setOnClickListener(onPanicButtonClickListener);



        Utils.createStickyNotification(this, PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Config.STICKY_NOTIFICATION, true));
    }


    private View.OnClickListener onPanicButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(getApplicationContext(), EmergencyService.class);
            startService(intent);

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.ac_trusted_contacts:
                startActivity(new Intent(this, TrustedContactsActivity.class));
                break;

            case R.id.ac_profile:
                startActivity(new Intent(this, ProfileActivity.class));
                break;

            case R.id.ac_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;

            case R.id.ac_help:
                break;

            case R.id.ac_about:
                break;
        }
        return true;
    }
}
