package com.example.apppiantina.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Person;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.apppiantina.R;
import com.example.apppiantina.model.Piantina;

import java.util.Collections;

public class BubbleNotificationManager {

    private static BubbleNotificationManager instance;
    private static final String CHANNEL_ID = "PlantNotification";
    private NotificationManager manager;
    Context context;


    private BubbleNotificationManager(Context context) {
            this.context = context;
            createNotificationChannel();
        }

    public static synchronized BubbleNotificationManager getInstance(Context context) throws Exception {
        if (instance == null) {
            instance = new BubbleNotificationManager(context);
        }
        return instance;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Garden", android.app.NotificationManager.IMPORTANCE_HIGH);

            channel.setDescription("This is the channel reserved for \"gardens\" notifications that will appear in bubbles");
            channel.setAllowBubbles(true);
            this.manager = this.context.getSystemService(NotificationManager.class);
            this.manager.createNotificationChannel(channel);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public void showBubble(String name) {

        String shortcutId = "chat_bubble_shortcut" + name;

        Intent target = new Intent(this.context, PlantBubbleActivity.class);
        target.setAction(Intent.ACTION_VIEW);

        PendingIntent bubbleIntent = PendingIntent.getActivity(
                this.context,
                0,
                target,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE
        );

        Person chatPartner = new Person.Builder()
                .setName(name)
                .setImportant(true)
                .build();

        ShortcutInfo shortcut =
                new ShortcutInfo.Builder(this.context, shortcutId)
                        .setCategories(Collections.singleton(Notification.CATEGORY_SOCIAL))
                        .setIntent(target)
                        .setLongLived(true)
                        .setShortLabel(chatPartner.getName())
                        .build();

        ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);
        shortcutManager.pushDynamicShortcut(shortcut);

        Notification.BubbleMetadata bubbleData =
                new Notification.BubbleMetadata.Builder()
                        .setDesiredHeight(600)
                        .setIcon(Icon.createWithResource(this.context, R.drawable.ic_launcher_foreground))
                        .setIntent(bubbleIntent)
                        .setAutoExpandBubble(true)
                        .build();

        Notification.Builder builder =
                new Notification.Builder(this.context, CHANNEL_ID)
                        .setContentIntent(bubbleIntent)
                        .setBubbleMetadata(bubbleData)
                        .setShortcutId(shortcutId)
                        .addPerson(chatPartner)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setCategory(Notification.CATEGORY_MESSAGE)
                        .setStyle(new Notification.MessagingStyle(chatPartner)
                                .addMessage("La tua piantina ha bisogno di acqua", System.currentTimeMillis(), chatPartner));

        this.manager.notify(1001, builder.build());
    }






    public void plantNotification(Piantina plant){

    }

}
