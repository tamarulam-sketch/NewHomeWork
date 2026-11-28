package com.example.newhomework;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CardActivity extends AppCompatActivity {

    private String name, job, phone, site;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        // הצגת סרגל עליון עם חץ חזרה
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("הכרטיס");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // קבלת הפרטים מהמסך הראשון
        Intent receivedIntent = getIntent();

        name = receivedIntent.getStringExtra(MainActivity.KEY_NAME);
        job = receivedIntent.getStringExtra(MainActivity.KEY_JOB);
        phone = receivedIntent.getStringExtra(MainActivity.KEY_PHONE);
        site = receivedIntent.getStringExtra(MainActivity.KEY_SITE);

        if (name == null) name = "";
        if (job == null) job = "";
        if (phone == null) phone = "";
        if (site == null) site = "";

        // הצגת פרטי הכרטיס
        ((TextView) findViewById(R.id.tvName)).setText(name);
        ((TextView) findViewById(R.id.tvJob)).setText(job);
        ((TextView) findViewById(R.id.tvPhone)).setText("📞 " + phone);
        ((TextView) findViewById(R.id.tvSite)).setText("🌐 " + site);
    }

    // הצגת התפריט בסרגל העליון
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.card_menu, menu);
        return true;
    }

    // טיפול בלחיצות
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        // חזרה למסך הקודם
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        // חיוג
        if (id == R.id.action_dial) {

            if (phone.trim().isEmpty()) {
                Toast.makeText(this, "לא הוזן מספר טלפון",
                        Toast.LENGTH_SHORT).show();
                return true;
            }

            Intent dialIntent = new Intent(
                    Intent.ACTION_DIAL,
                    Uri.parse("tel:" + phone)
            );

            startActivity(dialIntent);
            return true;
        }

        // שיתוף
        if (id == R.id.action_share) {

            String cardText =
                    "כרטיס ביקור\n" +
                            "שם: " + name + "\n" +
                            "תפקיד: " + job + "\n" +
                            "טלפון: " + phone + "\n" +
                            "אתר: " + site;

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, cardText);

            startActivity(Intent.createChooser(
                    shareIntent, "שתף כרטיס עם..."
            ));

            return true;
        }

        // עריכת כרטיס
        if (id == R.id.action_edit) {

            Intent intent = new Intent(
                    CardActivity.this,
                    MainActivity.class
            );

            intent.putExtra(MainActivity.KEY_NAME, name);
            intent.putExtra(MainActivity.KEY_JOB, job);
            intent.putExtra(MainActivity.KEY_PHONE, phone);
            intent.putExtra(MainActivity.KEY_SITE, site);

            startActivity(intent);
            finish();
            return true;
        }

        // פתיחת אתר
        if (id == R.id.action_web) {

            if (site.trim().isEmpty()) {
                Toast.makeText(this, "לא הוזן אתר",
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
                Toast.makeText(this, "לא נמצא דפדפן",
                        Toast.LENGTH_SHORT).show();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
