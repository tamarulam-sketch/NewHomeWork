package com.example.newhomework;

import android.content.Context;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

//המחלקה מכילה את פרטי ההתחברות של האפליקציה לFireBase
//בנוסף היא בודקת האם הוא מוגדר ומאתחלת אותו איפה שצריך

public class FirebaseConfig {

    // ↓↓↓ מעתיקים את שלושת הערכים מ-FirebaseConfig.java של פרויקט פרק 11 ↓↓↓
    public static final String API_KEY = "AIzaSyB1x2y3z4A5B6C7D8E9F0gHiJkLmNoPqRs";
    public static final String APPLICATION_ID = "1:123456789012:android:a1b2c3d4e5f6a7b8";
    public static final String DATABASE_URL = "https://phonebook-yb-default-rtdb.europe-west1.firebasedatabase.app";
    // ↑↑↑ בלי רווחים, ובתוך המרכאות ↑↑↑

    //בדיקה האם Firebase מוגדר
    public static boolean isConfigured() {
        return !API_KEY.isEmpty() && !APPLICATION_ID.isEmpty() && !DATABASE_URL.isEmpty();
    }

    // מחבר את האפליקציה ל-Firebase. מספיק פעם אחת: בפעם השנייה יוצאים מיד
    public static void ensureInitialized(Context context) {

        //בודק האם קיים חיבור של הפיירבייס באפליקציה
        if (!FirebaseApp.getApps(context).isEmpty()) {
            return;                                     // כבר מחובר
        }

        //יצירת אובייקט שמכיל את הגדרות החיבור לפיירבייס
        FirebaseOptions options = new FirebaseOptions.Builder().setApiKey(API_KEY)
                .setApplicationId(APPLICATION_ID)
                .setDatabaseUrl(DATABASE_URL).build();

        //מבצעים בפועל אתחול של פיירבייס
        FirebaseApp.initializeApp(context.getApplicationContext(), options);
    }
}
