package com.example.newhomework;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;

public class MainActivity extends AppCompatActivity {

    // שמות ה-Extras נשארים קבועים כדי שכל המסכים ישתמשו באותם שמות
    public static final String KEY_NAME = "name";
    public static final String KEY_JOB = "job";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_SITE = "site";
    public static final String KEY_CARD = "key";

    private EditText etName;
    private EditText etJob;
    private EditText etPhone;
    private EditText etSite;

    // null = כרטיס חדש; ערך קיים = מצב עריכה
    private String editKey = null;

    private Button btnCreateCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.etName);
        etJob = findViewById(R.id.etJob);
        etPhone = findViewById(R.id.etPhone);
        etSite = findViewById(R.id.etSite);

        btnCreateCard = findViewById(R.id.btnCreateCard);
        Button btnAbout = findViewById(R.id.btnAbout);

        btnCreateCard.setOnClickListener(v -> saveCard());

        btnAbout.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        });

        // אם הגיע key, הטופס נפתח במצב עריכה
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

        if (name.isEmpty()) {
            Toast.makeText(this, "חובה למלא שם", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!FirebaseConfig.isConfigured()) {
            Toast.makeText(this, "חסרים ערכים ב-FirebaseConfig.java", Toast.LENGTH_LONG).show();
            return;
        }

        final Card card = new Card(name, job, phone, site);
        DatabaseReference ref = CardsDb.cards(this);

        if (editKey == null) {
            // הוספה: push יוצר מפתח חדש וייחודי
            String key = ref.push().getKey();
            if (key == null) {
                Toast.makeText(this, "לא ניתן ליצור מפתח לכרטיס", Toast.LENGTH_LONG).show();
                return;
            }

            card.setKey(key);
            ref.child(key).setValue(card)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(MainActivity.this,
                                    "נשמר בענן", Toast.LENGTH_SHORT).show();
                            clearForm();
                            openCard(card);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this,
                                    "השמירה נכשלה: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

        }
        else
        {
            // עדכון: משתמשים באותו key, ולכן לא נוצר כרטיס נוסף
            card.setKey(editKey);
            ref.child(editKey).setValue(card)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(MainActivity.this,
                                    "הכרטיס עודכן", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this,
                                    "העדכון נכשל: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void openCard(Card c) {
        Intent intent = new Intent(this, CardActivity.class);
        intent.putExtra(KEY_CARD, c.getKey());
        intent.putExtra(KEY_NAME, c.getName());
        intent.putExtra(KEY_JOB, c.getJob());
        intent.putExtra(KEY_PHONE, c.getPhone());
        intent.putExtra(KEY_SITE, c.getSite());
        startActivity(intent);
    }

    private void clearForm() {
        etName.setText("");
        etJob.setText("");
        etPhone.setText("");
        etSite.setText("");
        etName.requestFocus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

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

