
package com.example.newhomework;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CardActivity extends Activity {

    private String name;
    private String job;
    private String phone;
    private String site;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        Intent receivedIntent = getIntent();

        //קבלת הנתונים מהמסך הקודם
        name = receivedIntent.getStringExtra(MainActivity.KEY_NAME);
        job = receivedIntent.getStringExtra(MainActivity.KEY_JOB);
        phone = receivedIntent.getStringExtra(MainActivity.KEY_PHONE);
        site = receivedIntent.getStringExtra(MainActivity.KEY_SITE);

        //בדיקה שהנתונים קיימים
        if (name == null) name = "";
        if (job == null) job = "";
        if (phone == null) phone = "";
        if (site == null) site = "";

        //חיבור מהאקסמל לפה
        TextView tvName = findViewById(R.id.tvName);
        TextView tvJob = findViewById(R.id.tvJob);
        TextView tvPhone = findViewById(R.id.tvPhone);
        TextView tvSite = findViewById(R.id.tvSite);

        tvName.setText(name);
        tvJob.setText(job);
        tvPhone.setText("📞 " + phone);
        tvSite.setText("🌐 " + site);

        Button btnDial = findViewById(R.id.btnDial);
        Button btnOpenSite = findViewById(R.id.btnOpenSite);
        Button btnShare = findViewById(R.id.btnShare);
        Button btnBack = findViewById(R.id.btnBack);

        // פתיחת החייגן
        btnDial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //בודק אם הכניסו מספר  טלפון
                if (phone.trim().isEmpty()) {
                    Toast.makeText(CardActivity.this, "לא הוזן מספר טלפון", Toast.LENGTH_SHORT).show();
                    return;
                }

                //בקשה לפתיחת האפליקצייה של הטלפון
                Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone)
                );

                //נועד רק אם האפליקציה לא תצליח לפתוח את החייגן
                try {
                    startActivity(dialIntent);
                }
                catch (ActivityNotFoundException e) {Toast.makeText(
                            CardActivity.this, "אין אפליקציה מתאימה", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // פתיחת האתר בדפדפן
        btnOpenSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (site.trim().isEmpty()) {
                    Toast.makeText(CardActivity.this, "לא הוזן אתר", Toast.LENGTH_SHORT).show();
                    return;
                }

                String address = site.trim();

                //אם הכתובת לא מתחילה בשום אופציה שכתובה בIF זה מוסיף לה את ההתחלה של האתר כדי שזה יעבור
                if (!address.startsWith("http://") && !address.startsWith("https://")) {
                    address = "https://" + address;
                }

                //Uri.parse = הופך את הטקסט לכתובת שאפשר להעביר לפעולה
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(address));

                try {
                    startActivity(webIntent);
                }
                catch (ActivityNotFoundException e) {
                    Toast.makeText(CardActivity.this, "אין אפליקציה מתאימה", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // שיתוף הכרטיס
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //קארד-טקסט מכיל את כל פרטי הכרטיס
                String cardText =
                        "כרטיס ביקור\n"
                                + "שם: " + name + "\n"
                                + "תפקיד: " + job + "\n"
                                + "טלפון: " + phone + "\n"
                                + "אתר: " + site;


                //יוצרים בקשה לשיתוף מידע (הכפתור שיתוף)
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, cardText);

                //יוצרת חלון שיתוף
                Intent chooser = Intent.createChooser(shareIntent, "שתף כרטיס עם...");

                //טיפול במקרה של שגיאה
                try {
                    startActivity(chooser);
                }
                catch (ActivityNotFoundException e) {
                    Toast.makeText(CardActivity.this, "אין אפליקציה מתאימה", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // חזרה למסך הקודם
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
