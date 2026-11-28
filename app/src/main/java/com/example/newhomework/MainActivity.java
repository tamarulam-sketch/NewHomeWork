package com.example.newhomework;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_CARD = "key";
    public static final String KEY_NAME = "name";
    public static final String KEY_JOB = "job";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_SITE = "site";

    // קבועים עבור מערכת ההתראות
    public static final String CHANNEL_ID = "cards_channel";
    private static final int NOTIF_ID = 201;
    private static final int PERM_REQUEST = 1001;

    private EditText etName;
    private EditText etJob;
    private EditText etPhone;
    private EditText etSite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // חיבור שדות הטופס
        etName = findViewById(R.id.etName);
        etJob = findViewById(R.id.etJob);
        etPhone = findViewById(R.id.etPhone);
        etSite = findViewById(R.id.etSite);

        // קבלת פרטים קודמים במקרה של עריכת כרטיס
        Intent receivedIntent = getIntent();

        if (receivedIntent.hasExtra(KEY_NAME)) {
            etName.setText(receivedIntent.getStringExtra(KEY_NAME));
            etJob.setText(receivedIntent.getStringExtra(KEY_JOB));
            etPhone.setText(receivedIntent.getStringExtra(KEY_PHONE));
            etSite.setText(receivedIntent.getStringExtra(KEY_SITE));
        }

        // כפתור יצירת כרטיס
        Button btnCreateCard = findViewById(R.id.btnCreateCard);

        btnCreateCard.setOnClickListener(v -> {

            // קבלת הפרטים מהשדות
            String name = etName.getText().toString().trim();
            String job = etJob.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String site = etSite.getText().toString().trim();

            // בדיקה שכל השדות מלאים
            if (name.isEmpty() || job.isEmpty() || phone.isEmpty() || site.isEmpty()) {

                Toast.makeText(this, "יש למלא את כל הפרטים", Toast.LENGTH_SHORT).show();

                return;
            }

            // יצירת אובייקט כרטיס
            Card card = new Card(name, job, phone, site);

            // קבלת הפניה למסד הנתונים
            DatabaseReference ref = CardsDb.cards(this);

            // בדיקה האם עורכים כרטיס קיים
            String key = receivedIntent.getStringExtra(KEY_CARD);

            if (key == null)
            {
                // יצירת מפתח חדש לכרטיס
                key = ref.push().getKey();
            }

            if (key == null)
            {
                Toast.makeText(this, "לא ניתן ליצור כרטיס", Toast.LENGTH_SHORT).show();
                return;
            }
            final String savedKey = key;

            // שמירת המפתח בכרטיס
            card.setKey(savedKey);

            // שמירת הכרטיס ב-Firebase
            ref.child(savedKey).setValue(card)
                    .addOnSuccessListener(unused -> {

                        Toast.makeText(this, "הכרטיס נשמר בהצלחה!", Toast.LENGTH_SHORT).show();

                        notifyCardSaved(card);

                        // מעבר למסך הכרטיס
                        Intent intent = new Intent(MainActivity.this, CardActivity.class);

                        intent.putExtra(KEY_CARD, savedKey);
                        intent.putExtra(KEY_NAME, name);
                        intent.putExtra(KEY_JOB, job);
                        intent.putExtra(KEY_PHONE, phone);
                        intent.putExtra(KEY_SITE, site);

                        startActivity(intent);
                        finish();
                    })

                    .addOnFailureListener(e ->
                    {

                        Toast.makeText(this, "שמירת הכרטיס נכשלה: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });

        // יצירת ערוץ ובקשת הרשאה
        createNotificationChannel();
        askNotificationPermission();
    }

    // יצירת התפריט העליון
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // טיפול בבחירת אפשרות בתפריט
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        // ניקוי הטופס
        if (id == R.id.action_clear) {

            etName.setText("");
            etJob.setText("");
            etPhone.setText("");
            etSite.setText("");

            Toast.makeText(this, "הטופס נוקה", Toast.LENGTH_SHORT).show();

            return true;
        }

        // מילוי פרטים לדוגמה
        if (id == R.id.action_fill) {

            etName.setText("תמר משולם");
            etJob.setText("מפתח תוכנה");
            etPhone.setText("0501234567");
            etSite.setText("www.example.com");

            return true;
        }

        // מעבר לרשימת הכרטיסים
        if (id == R.id.action_list) {

            Intent intent = new Intent(MainActivity.this, CardsListActivity.class);

            startActivity(intent);
            return true;
        }

        // מעבר למסך אודות
        if (id == R.id.action_about) {

            Intent intent = new Intent(MainActivity.this, AboutActivity.class);

            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // יצירת ערוץ ההתראות
    private void createNotificationChannel() {

        // ערוצים קיימים מאנדרואיד 8 ומעלה
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "כרטיסים חדשים", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);

            manager.createNotificationChannel(channel);
        }
    }

    // בקשת הרשאה להצגת התראות
    private void askNotificationPermission() {

        // ההרשאה נדרשת מאנדרואיד 13 ומעלה
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED)
            {

                ActivityCompat.requestPermissions(
                        this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERM_REQUEST);
            }
        }
    }

    // שליחת התראה לאחר שמירת כרטיס מוצלחת
    private void notifyCardSaved(Card c) {

        // יצירת Intent שיפתח את מסך הכרטיס
        Intent intent = new Intent(this, CardActivity.class);

        // העברת פרטי הכרטיס למסך הבא
        intent.putExtra(KEY_CARD, c.getKey());
        intent.putExtra(KEY_NAME, c.getName());
        intent.putExtra(KEY_JOB, c.getJob());
        intent.putExtra(KEY_PHONE, c.getPhone());
        intent.putExtra(KEY_SITE, c.getSite());

        // הגדרת אופן פתיחת המסך
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // יצירת PendingIntent
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // בניית ההתראה
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)

                        .setSmallIcon(android.R.drawable.ic_dialog_email)

                        .setContentTitle("הכרטיס נשמר בענן")

                        .setContentText("הכרטיס של " + c.getName())

                        // חיבור הלחיצה למסך הכרטיס
                        .setContentIntent(pi)

                        .setAutoCancel(true);

        // בדיקת הרשאה
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(
                    this, "אין הרשאה להצגת התראות", Toast.LENGTH_LONG).show();

            return;
        }

        // שליחת ההתראה
        NotificationManagerCompat.from(this).notify(NOTIF_ID, builder.build());
    }
}
