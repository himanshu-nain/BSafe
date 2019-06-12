package nain.himanshu.bsafe;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import im.delight.android.location.SimpleLocation;
import nain.himanshu.bsafe.Database.AppDatabase;

public class EmergencyService extends IntentService {

    private SimpleLocation location;
    private double[] coordinates;

    private SimpleLocation.Listener locationListener = new SimpleLocation.Listener() {
        @Override
        public void onPositionChanged() {

            coordinates[0] = location.getLatitude();
            coordinates[1] = location.getLongitude();

        }
    };

    public EmergencyService() {
        super("EMERGENCY");
    }

    private String getAddress(){

        Geocoder geocoder = new Geocoder(this);
        String add = "";

        try{
            List<Address> addresses =  geocoder.getFromLocation(coordinates[0],coordinates[1], 1);
            Address address = addresses.get(0);

            add = String.format(
                    Locale.ENGLISH,
                    "Address Line 1 - %s, Address Line 2 - %s, Landmark - %s, SubLocality - %s, Locality - %s, Administrative Area - %s, Country - %s, Postal - %s",
                    address.getAddressLine(0),
                    address.getAddressLine(1),
                    address.getFeatureName(),
                    address.getSubLocality(),
                    address.getLocality(),
                    address.getAdminArea(),
                    address.getCountryName(),
                    address.getPostalCode()
            );

            return add;

        }catch (Exception e){
            e.printStackTrace();
        }

        return add;

    }

    private String prepareMessage(){

        coordinates[0] = location.getLatitude();
        coordinates[1] = location.getLongitude();

        SharedPreferences preferences = getSharedPreferences(Config.PREFS, MODE_PRIVATE);
        String name = preferences.getString(Config.NAME_FIELD, getString(R.string.default_name));
        String text = preferences.getString(Config.MESSAGE_FIELD, getString(R.string.default_message));

        String message = String.format(
                Locale.ENGLISH,
                "%s, %s. Last Known Location: %s. Latitude - %f, Longitude - %f",
                name,
                text,
                getAddress(),
                coordinates[0],
                coordinates[1]
        );
        return  message;

    }

    private void sendMessages(List<String> numbers){

        String message = prepareMessage();

        for (String number: numbers){
            SmsManager manager = SmsManager.getDefault();
            manager.sendTextMessage(number, null, message, null, null);
        }
        Handler mHandler = new Handler(getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Messages sent successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        coordinates = new double[2];
        location = new SimpleLocation(getApplicationContext());
        location.setListener(locationListener);
        if (!location.hasLocationEnabled()) {
            SimpleLocation.openSettings(this);
        }else {
            coordinates[0] = location.getLatitude();
            coordinates[1] = location.getLongitude();
        }

        try {
            final List<String> numbers = new ArrayList<>();
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    numbers.addAll(AppDatabase.getInstance(getApplicationContext()).contactsDao().getAllNumbers());

                }
            });
            thread.setName("EMERGENCY_HANDLE_THREAD");
            thread.setPriority(Thread.MAX_PRIORITY);
            thread.start();
            thread.join();

            sendMessages(numbers);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
