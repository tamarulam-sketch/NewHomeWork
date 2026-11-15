package com.example.newhomework;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CardsDb {

    // מחזיר את הצומת cards, וכל המסכים משתמשים באותה מתודה
    public static DatabaseReference cards(Context context) {
        FirebaseConfig.ensureInitialized(context);
        return FirebaseDatabase.getInstance(FirebaseConfig.DATABASE_URL)
                .getReference("cards");
    }
}
