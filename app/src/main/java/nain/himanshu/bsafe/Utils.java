package nain.himanshu.bsafe;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import static android.content.Context.NOTIFICATION_SERVICE;

public class Utils {

    private static final int EMERGENCY_REQUEST = 13;

    public static void createStickyNotification(Context context, boolean show) {

        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        if(show){
            Intent intent = new Intent(context, EmergencyService.class);
            PendingIntent pIntent = PendingIntent.getService(
                    context,
                    EMERGENCY_REQUEST,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );

            Notification.Builder builder = new Notification.Builder(context);
            builder.setOngoing(true);
            builder.setAutoCancel(false);
            builder.setSmallIcon(R.mipmap.ic_launcher_round);
            builder.setContentTitle("!! ALERT TRUSTED CONTACTS !!");
            builder.setContentIntent(pIntent);

            manager.notify(Config.EMERGENCY_NOTIFICATION_ID, builder.build());
        }else{
            manager.cancel(Config.EMERGENCY_NOTIFICATION_ID);
        }

    }

}
