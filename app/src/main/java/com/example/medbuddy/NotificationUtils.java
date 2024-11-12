package com.example.medbuddy;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

public class NotificationUtils {

//    private static final String CHANNEL_ID = "medicine_reminder_channel";
//    private static final int NOTIFICATION_ID = 1;
//
//    // Create notification channel for Android 8.0 and above
//    public static void createNotificationChannel(Context context) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = "Medicine Reminders";
//            String description = "Channel for medicine reminders";
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
//            channel.setDescription(description);
//            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
//    }
//
//    // Method to schedule a notification at a specific time
//    public static void scheduleNotification(Context context, String medicineName, String time, int requestCode) {
//        Intent intent = new Intent(context, NotificationReceiver.class);
//        intent.putExtra("medicineName", medicineName);
//
//        // Use FLAG_IMMUTABLE flag for API 31+ compliance
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_IMMUTABLE);
//
//        // Set up the alarm time
//        Calendar calendar = Calendar.getInstance();
//        String[] timeParts = time.split(":");
//        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeParts[0]));
//        calendar.set(Calendar.MINUTE, Integer.parseInt(timeParts[1]));
//        calendar.set(Calendar.SECOND, 0);
//
//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        if (alarmManager != null) {
//            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
//        }
//    }
//
//    // Method to create a notification
//    public static void sendNotification(Context context, String medicineName) {
//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
//                .setSmallIcon(R.drawable.baseline_access_alarm_24)
//                .setContentTitle("Time to Take Your Medicine!")
//                .setContentText("It's time to take your " + medicineName)
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                .setAutoCancel(true);
//
//        notificationManager.notify(NOTIFICATION_ID, builder.build());
//    }
}
