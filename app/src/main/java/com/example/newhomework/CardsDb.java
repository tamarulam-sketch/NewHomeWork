
package com.example.newhomework;

import android.content.Context;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//המחלקה מספקת למסמכים גישה במקום שבו נשמרים כרטיסי הביקור בFireBase,
//ובכך כל המסמכים משתמשים באותה הפנייה למסד הנתונים

public class CardsDb {

    // מתודה סטטית שמחזירה הפניה לצומת cards במסד הנתונים
    // מקבלת Context כדי לאפשר אתחול של Firebase
    public static DatabaseReference cards(Context context) {

        // מוודאת ש-Firebase אותחל לפני שניגשים למסד הנתונים
        FirebaseConfig.ensureInitialized(context);

        // מקבלת את מסד הנתונים לפי כתובת ה-URL שהוגדרה ב-FirebaseConfig
        // לאחר מכן מחזירה הפניה לצומת בשם cards
        // שבו נשמרים כל כרטיסי הביקור
        return FirebaseDatabase.getInstance(FirebaseConfig.DATABASE_URL)
                .getReference("cards");
    }
}