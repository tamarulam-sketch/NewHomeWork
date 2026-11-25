
package com.example.newhomework;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// מחלקה המציגה כרטיס ביקור ומאפשרת לבצע פעולות עליו
public class CardActivity extends AppCompatActivity {

    // פרטי הכרטיס
    private String key;
    private String name;
    private String job;
    private String phone;
    private String site;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        // הגדרת הכותרת וחץ החזרה בסרגל העליון
        setTitle("הכרטיסים");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // קבלת הנתונים מהמסך הקודם
        Intent receivedIntent = getIntent();

        key = receivedIntent.getStringExtra(MainActivity.KEY_CARD);
        name = receivedIntent.getStringExtra(MainActivity.KEY_NAME);
        job = receivedIntent.getStringExtra(MainActivity.KEY_JOB);
        phone = receivedIntent.getStringExtra(MainActivity.KEY_PHONE);
        site = receivedIntent.getStringExtra(MainActivity.KEY_SITE);

        // מניעת ערכי null
        if (name == null) name = "";
        if (job == null) job = "";
        if (phone == null) phone = "";
        if (site == null) site = "";

        // חיבור רכיבי המסך
        TextView tvName = findViewById(R.id.tvName);
        TextView tvJob = findViewById(R.id.tvJob);
        TextView tvPhone = findViewById(R.id.tvPhone);
        TextView tvSite = findViewById(R.id.tvSite);

        // הצגת פרטי הכרטיס
        tvName.setText(name);
        tvJob.setText(job);
        tvPhone.setText("📞 " + phone);
        tvSite.setText("🌐 " + site);

        // כפתור חזרה שנמצא במסך
        Button btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());
    }

    // יצירת התפריט העליון
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.card_menu, menu);
        return true;
    }

    // טיפול בלחיצות על התפריט
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        // חזרה למסך הקודם
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        // עריכת הכרטיס
        if (id == R.id.action_edit) {

            Intent editIntent =
                    new Intent(this, MainActivity.class);

            editIntent.putExtra(MainActivity.KEY_CARD, key);
            editIntent.putExtra(MainActivity.KEY_NAME, name);
            editIntent.putExtra(MainActivity.KEY_JOB, job);
            editIntent.putExtra(MainActivity.KEY_PHONE, phone);
            editIntent.putExtra(MainActivity.KEY_SITE, site);

            startActivity(editIntent);
            finish();

            return true;
        }

        // חיוג למספר הטלפון
        if (id == R.id.action_dial) {

            if (phone.trim().isEmpty()) {
                Toast.makeText(this,
                        "לא הוזן מספר טלפון",
                        Toast.LENGTH_SHORT).show();
                return true;
            }

            Intent dialIntent = new Intent(
                    Intent.ACTION_DIAL,
                    Uri.parse("tel:" + phone)
            );

            try {
                startActivity(dialIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this,
                        "אין אפליקציה מתאימה",
                        Toast.LENGTH_SHORT).show();
            }

            return true;
        }

        // שיתוף כרטיס הביקור
        if (id == R.id.action_share) {

            String cardText = "כרטיס ביקור\n"
                    + "שם: " + name + "\n"
                    + "תפקיד: " + job + "\n"
                    + "טלפון: " + phone + "\n"
                    + "אתר: " + site;

            Intent shareIntent =
                    new Intent(Intent.ACTION_SEND);

            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, cardText);

            Intent chooser = Intent.createChooser(
                    shareIntent,
                    "שתף כרטיס עם..."
            );

            try {
                startActivity(chooser);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this,
                        "אין אפליקציה מתאימה",
                        Toast.LENGTH_SHORT).show();
            }

            return true;
        }

        // פתיחת האתר בדפדפן
        if (id == R.id.action_web) {

            if (site.trim().isEmpty()) {
                Toast.makeText(this,
                        "לא הוזן אתר",
                        Toast.LENGTH_SHORT).show();
                return true;
            }

            String address = site.trim();

            if (!address.startsWith("http://")
                    && !address.startsWith("https://")) {
                address = "https://" + address;
            }

            Intent webIntent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(address)
            );

            try {
                startActivity(webIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this,
                        "אין אפליקציה מתאימה",
                        Toast.LENGTH_SHORT).show();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
