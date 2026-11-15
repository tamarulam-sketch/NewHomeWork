
package com.example.newhomework;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_NAME = "KEY_NAME";
    public static final String KEY_JOB = "KEY_JOB";
    public static final String KEY_PHONE = "KEY_PHONE";
    public static final String KEY_SITE = "KEY_SITE";

    // שומרים את שדות הטקסט כדי שנוכל להשתמש בהם גם בתפריט
    private EditText etName;
    private EditText etJob;
    private EditText etPhone;
    private EditText etSite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // הצגת מסך הבית מתוך קובץ XML
        setContentView(R.layout.activity_main);

        // קישור הרכיבים
        etName = findViewById(R.id.etName);
        etJob = findViewById(R.id.etJob);
        etPhone = findViewById(R.id.etPhone);
        etSite = findViewById(R.id.etSite);

        Button btnCreateCard = findViewById(R.id.btnCreateCard);
        Button btnAbout = findViewById(R.id.btnAbout);

        // כפתור יצירת כרטיס ביקור
        btnCreateCard.setOnClickListener(v -> {

            String name = etName.getText().toString();
            String job = etJob.getText().toString();
            String phone = etPhone.getText().toString();
            String site = etSite.getText().toString();

            Intent intent = new Intent(MainActivity.this,
                    CardActivity.class);

            intent.putExtra(KEY_NAME, name);
            intent.putExtra(KEY_JOB, job);
            intent.putExtra(KEY_PHONE, phone);
            intent.putExtra(KEY_SITE, site);

            startActivity(intent);
        });

        // כפתור אודות
        btnAbout.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,
                    AboutActivity.class);

            startActivity(intent);
        });
    }

    // הצגת התפריט העליון
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // טיפול בלחיצה על אפשרויות התפריט
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        // ניקוי כל השדות
        if (id == R.id.action_clear) {
            etName.setText("");
            etJob.setText("");
            etPhone.setText("");
            etSite.setText("");
            return true;
        }

        // מילוי פרטים לדוגמה
        if (id == R.id.action_fill) {
            etName.setText("ישראל ישראלי");
            etJob.setText("מעצב גרפי");
            etPhone.setText("0501234567");
            etSite.setText("www.example.com");
            return true;
        }

        // מעבר למסך אודות
        if (id == R.id.action_about) {
            Intent intent = new Intent(MainActivity.this,
                    AboutActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
