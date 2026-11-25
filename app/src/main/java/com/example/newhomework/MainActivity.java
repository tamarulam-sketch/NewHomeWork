
package com.example.newhomework;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;

// מסך הטופס: יצירת כרטיס, עריכת כרטיס ושמירה בענן
public class MainActivity extends AppCompatActivity {

    // שמות קבועים להעברת נתונים בין המסכים
    public static final String KEY_NAME = "name";
    public static final String KEY_JOB = "job";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_SITE = "site";
    public static final String KEY_CARD = "key";

    private EditText etName;
    private EditText etJob;
    private EditText etPhone;
    private EditText etSite;

    // null = כרטיס חדש, אחרת מדובר בעריכת כרטיס קיים
    private String editKey = null;

    private Button btnCreateCard;

    // קבועים למערכת ההתראות
    private static final String CHANNEL_ID = "cards_channel";
    private static final int NOTIF_ID = 201;
    private static final int PERM_REQUEST = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // אתחול Firebase לפני שמירת כרטיסים
        FirebaseConfig.ensureInitialized(this);

        // יוצרים ערוץ ומבקשים הרשאה להתראות
        createNotificationChannel();
        askNotificationPermission();

        etName = findViewById(R.id.etName);
        etJob = findViewById(R.id.etJob);
        etPhone = findViewById(R.id.etPhone);
        etSite = findViewById(R.id.etSite);

        btnCreateCard = findViewById(R.id.btnCreateCard);

        // לחיצה על יצירת כרטיס
        btnCreateCard.setOnClickListener(v -> saveCard());


        // בדיקה האם הגענו לכאן לצורך עריכת כרטיס
        editKey = getIntent().getStringExtra(KEY_CARD);

        if (editKey != null) {
            etName.setText(getIntent().getStringExtra(KEY_NAME));
            etJob.setText(getIntent().getStringExtra(KEY_JOB));
            etPhone.setText(getIntent().getStringExtra(KEY_PHONE));
            etSite.setText(getIntent().getStringExtra(KEY_SITE));

            btnCreateCard.setText("שמור שינויים");
            setTitle("עריכת כרטיס");
        }
    }


    private void saveCard() {

        String name = etName.getText().toString().trim();
        String job = etJob.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String site = etSite.getText().toString().trim();

        // בדיקה שהוזן שם
        if (name.isEmpty()) {
            Toast.makeText(this, "חובה למלא שם",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // בדיקה ש-Firebase אותחל
        if (!FirebaseConfig.isConfigured(this)) {
            Toast.makeText(this, "Firebase לא אותחל",
                    Toast.LENGTH_LONG).show();
            return;
        }

        Card card = new Card(name, job, phone, site);
        DatabaseReference ref = CardsDb.cards(this);

        if (editKey == null) {

            // יצירת מפתח חדש
            String key = ref.push().getKey();

            if (key == null) {
                Toast.makeText(this, "לא ניתן ליצור מפתח לכרטיס",
                        Toast.LENGTH_LONG).show();
                return;
            }

            card.setKey(key);

            // שליחת הכרטיס לשמירה ב-Firebase
            ref.child(key).setValue(card)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(MainActivity.this,
                                "הכרטיס נשמר בהצלחה!",
                                Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(MainActivity.this,
                                "השמירה נכשלה: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    });

            // מעבר למסך הכרטיס בלי להמתין לתשובת Firebase
            openCard(card);

        } else {

            // עדכון כרטיס קיים
            card.setKey(editKey);

            ref.child(editKey).setValue(card)
                    .addOnSuccessListener(unused -> {

                        Toast.makeText(MainActivity.this,
                                "הכרטיס עודכן",
                                Toast.LENGTH_SHORT).show();

                        finish();
                    })
                    .addOnFailureListener(e -> {

                        Toast.makeText(MainActivity.this,
                                "העדכון נכשל: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    });
        }
    }


    // שלב 1: יצירת ערוץ ההתראות
    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "כרטיסים חדשים",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager =
                    getSystemService(NotificationManager.class);

            manager.createNotificationChannel(channel);
        }
    }

    // שלב 2: בקשת הרשאה להצגת התראות
    private void askNotificationPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        PERM_REQUEST
                );
            }
        }
    }

    // שלבים 3 ו-4: בניית התראה וחיבור שלה למסך הכרטיס
    private void notifyCardSaved(Card card) {

        // יצירת Intent למסך הכרטיס
        Intent intent = new Intent(this, CardActivity.class);

        // העברת פרטי הכרטיס למסך
        intent.putExtra(KEY_CARD, card.getKey());
        intent.putExtra(KEY_NAME, card.getName());
        intent.putExtra(KEY_JOB, card.getJob());
        intent.putExtra(KEY_PHONE, card.getPhone());
        intent.putExtra(KEY_SITE, card.getSite());

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // יצירת PendingIntent שמאפשר למערכת לפתוח את המסך
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT |
                        PendingIntent.FLAG_IMMUTABLE
        );

        // בניית ההתראה
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setContentTitle("הכרטיס נשמר בענן")
                        .setContentText("הכרטיס של " + card.getName())
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

        // בדיקה שהמשתמש אישר התראות
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this,
                    "אין הרשאה להצגת התראות",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // שליחת ההתראה למערכת
        NotificationManagerCompat.from(this).notify(NOTIF_ID, builder.build());
    }

    // פתיחת מסך הכרטיס לאחר שמירה
    private void openCard(Card c) {

        Intent intent = new Intent(this, CardActivity.class);

        intent.putExtra(KEY_CARD, c.getKey());
        intent.putExtra(KEY_NAME, c.getName());
        intent.putExtra(KEY_JOB, c.getJob());
        intent.putExtra(KEY_PHONE, c.getPhone());
        intent.putExtra(KEY_SITE, c.getSite());

        startActivity(intent);
    }

    // ניקוי הטופס לאחר שמירה
    private void clearForm() {

        etName.setText("");
        etJob.setText("");
        etPhone.setText("");
        etSite.setText("");

        etName.requestFocus();
    }

    // יצירת התפריט העליון של מסך הטופס
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // טיפול בלחיצות על אפשרויות התפריט
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_clear) {
            clearForm();
            return true;
        }

        if (id == R.id.action_fill) {
            etName.setText("ישראל ישראלי");
            etJob.setText("מעצב גרפי");
            etPhone.setText("0501234567");
            etSite.setText("www.example.com");
            return true;
        }

        if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }

        if (id == R.id.action_list) {
            startActivity(new Intent(this, CardsListActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
