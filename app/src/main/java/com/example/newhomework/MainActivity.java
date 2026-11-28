package com.example.newhomework;

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

            etName.setText("ישראל ישראלי");
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
}
