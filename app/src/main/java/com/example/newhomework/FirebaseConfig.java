package com.example.newhomework;

import android.content.Context;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

public class FirebaseConfig {

    public static final String API_KEY =
            "AIzaSyA9gbeF3nwlJ3eqt1asBJ7FPx4wVHFAioE";

    public static final String APPLICATION_ID =
            "1:342399346963:android:d4f675e3156e1b517ad2e6";

    public static final String DATABASE_URL =
            "https://phonebook-yb-ce9b3-default-rtdb.firebaseio.com";

    public static boolean isConfigured(Context context) {
        return FirebaseApp.getApps(context).size() > 0;
    }

    public static void ensureInitialized(Context context) {

        if (!FirebaseApp.getApps(context).isEmpty()) {
            return;
        }

        FirebaseOptions options =
                new FirebaseOptions.Builder()
                        .setApiKey(API_KEY)
                        .setApplicationId(APPLICATION_ID)
                        .setDatabaseUrl(DATABASE_URL)
                        .build();

        FirebaseApp.initializeApp(
                context.getApplicationContext(), options);
    }
}
