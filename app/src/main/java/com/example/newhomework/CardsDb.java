
package com.example.newhomework;

import android.content.Context;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CardsDb {

    // מחזירה הפניה למקום שבו נשמרים כרטיסי הביקור
    public static DatabaseReference cards(Context context) {

        FirebaseConfig.ensureInitialized(context);

        return FirebaseDatabase.getInstance()
                .getReference("cards");
    }
}
