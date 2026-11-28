package com.example.newhomework;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

// מחלקה המציגה את כל כרטיסי הביקור שנשמרו בענן
public class CardsListActivity extends AppCompatActivity {

    private ListView lvCards;
    private TextView tvCount;

    // רשימה ששומרת את הכרטיסים שהתקבלו מ-Firebase
    private final ArrayList<Card> cards = new ArrayList<>();

    private ArrayAdapter<Card> adapter;

    private DatabaseReference ref;
    private ValueEventListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards_list);

        setTitle("הכרטיסים שלי");

        // חיבור רכיבי המסך
        lvCards = findViewById(R.id.lvCards);
        tvCount = findViewById(R.id.tvCount);

        Button btnBack = findViewById(R.id.btnBack);
        Button btnAdd = findViewById(R.id.btnAdd);

        // חזרה למסך הקודם
        btnBack.setOnClickListener(v -> finish());

        // כרטיס חדש: חוזרים לטופס הקיים
        btnAdd.setOnClickListener(v -> finish());

        // יצירת מתאם שמציג את הכרטיסים ב-ListView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cards);

        lvCards.setAdapter(adapter);

        if (!FirebaseConfig.isConfigured(this)) {
            Toast.makeText(this,
                    "Firebase לא אותחל",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // קבלת הפניה למקום שבו נשמרים הכרטיסים בענן
        ref = CardsDb.cards(this);

        // מאזין שמקשיב לכל שינוי ברשימת הכרטיסים בענן
        listener = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // מנקים את הרשימה הישנה
                cards.clear();

                // עוברים על כל הכרטיסים שהתקבלו מהענן
                for (DataSnapshot child : snapshot.getChildren()) {

                    Card card = child.getValue(Card.class);

                    if (card != null) {

                        // שמירת המפתח של הכרטיס
                        card.setKey(child.getKey());

                        // הוספת הכרטיס לרשימה
                        cards.add(card);
                    }
                }

                // עדכון התצוגה במסך
                adapter.notifyDataSetChanged();

                // הצגת מספר הכרטיסים
                tvCount.setText(
                        cards.size() + " כרטיסים בענן"
                );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(
                        CardsListActivity.this,
                        "הקריאה נכשלה: " + error.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        };

        // חיבור המאזין ל-Firebase
        ref.addValueEventListener(listener);

        // לחיצה על כרטיס ברשימה פותחת את מסך הכרטיס
        lvCards.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        Card card = cards.get(position);

                        Intent intent = new Intent(
                                CardsListActivity.this,
                                CardActivity.class
                        );

                        // העברת פרטי הכרטיס למסך הבא
                        intent.putExtra(
                                MainActivity.KEY_CARD,
                                card.getKey()
                        );

                        intent.putExtra(
                                MainActivity.KEY_NAME,
                                card.getName()
                        );

                        intent.putExtra(
                                MainActivity.KEY_JOB,
                                card.getJob()
                        );

                        intent.putExtra(
                                MainActivity.KEY_PHONE,
                                card.getPhone()
                        );

                        intent.putExtra(
                                MainActivity.KEY_SITE,
                                card.getSite()
                        );

                        startActivity(intent);
                    }
                }
        );

        // לחיצה ארוכה על כרטיס מאפשרת למחוק אותו
        lvCards.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {

                    @Override
                    public boolean onItemLongClick(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        askDelete(cards.get(position));

                        return true;
                    }
                }
        );
    }

    // הוספת כרטיס לדוגמה כדי לבדוק עדכון אוטומטי
    private void addFriendCard() {

        if (ref == null) {
            Toast.makeText(
                    this,
                    "אין חיבור למסד הנתונים",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        // יצירת כרטיס של חבר לדוגמה
        Card friendCard = new Card(
                "חבר לדוגמה",
                "מפתח אפליקציות",
                "0501234567",
                "friend.example.com"
        );

        // יצירת מפתח חדש לכרטיס
        String key = ref.push().getKey();

        if (key == null) {
            Toast.makeText(
                    this,
                    "לא ניתן ליצור כרטיס",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        friendCard.setKey(key);

        // שמירת הכרטיס בענן
        ref.child(key).setValue(friendCard)
                .addOnSuccessListener(unused ->
                        Toast.makeText(
                                this,
                                "החבר הוסיף כרטיס בהצלחה",
                                Toast.LENGTH_SHORT
                        ).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(
                                this,
                                "הוספת הכרטיס נכשלה: " + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
    }

    // הצגת דיאלוג אישור לפני מחיקת כרטיס
    private void askDelete(final Card c) {

        new AlertDialog.Builder(this)
                .setTitle("מחיקת כרטיס")
                .setMessage(
                        "למחוק את הכרטיס של " + c.getName() + "?"
                )

                .setPositiveButton(
                        "מחק",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(
                                    DialogInterface dialog,
                                    int which) {

                                if (c.getKey() == null) {

                                    Toast.makeText(
                                            CardsListActivity.this,
                                            "לא נמצא מפתח לכרטיס",
                                            Toast.LENGTH_LONG
                                    ).show();

                                    return;
                                }

                                // מחיקת הכרטיס מ-Firebase
                                ref.child(c.getKey())
                                        .removeValue()
                                        .addOnSuccessListener(unused ->
                                                Toast.makeText(
                                                        CardsListActivity.this,
                                                        "נמחק",
                                                        Toast.LENGTH_SHORT
                                                ).show()
                                        )
                                        .addOnFailureListener(e ->
                                                Toast.makeText(
                                                        CardsListActivity.this,
                                                        "המחיקה נכשלה: "
                                                                + e.getMessage(),
                                                        Toast.LENGTH_LONG
                                                ).show()
                                        );
                            }
                        }
                )

                .setNegativeButton("ביטול", null)
                .show();
    }

    // הסרת המאזין כאשר יוצאים מהמסך
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (ref != null && listener != null) {
            ref.removeEventListener(listener);
        }
    }
}
