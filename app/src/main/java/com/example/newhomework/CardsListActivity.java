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

//מחלקה המציגה רשימה של כרטיסי הביקור שנשמר בענן

public class CardsListActivity extends AppCompatActivity {

    private ListView lvCards;
    private TextView tvCount;

    private final ArrayList<Card> cards = new ArrayList<>();
    private ArrayAdapter<Card> adapter;

    private DatabaseReference ref;
    private ValueEventListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards_list);
        setTitle("הכרטיסים שלי");

        lvCards = findViewById(R.id.lvCards);
        tvCount = findViewById(R.id.tvCount);

        Button btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> {
            finish();
        });

        Button btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, cards);
        lvCards.setAdapter(adapter);

        if (!FirebaseConfig.isConfigured()) {
            tvCount.setText("חסרים ערכים ב-FirebaseConfig.java");
            return;
        }

        ref = CardsDb.cards(this);

        //משימה אישית!
        // מאזין שמקבל את כל הכרטיסים בכל שינוי בענן
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cards.clear();

                for (DataSnapshot child : snapshot.getChildren()) {
                    Card card = child.getValue(Card.class);
                    if (card != null) {
                        card.setKey(child.getKey());
                        cards.add(card);
                    }
                }

                adapter.notifyDataSetChanged();
                tvCount.setText(cards.size() + " כרטיסים בענן");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CardsListActivity.this,
                        "הקריאה נכשלה: " + error.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        };

        //גםמשימה כזה אישית
        ref.addValueEventListener(listener);

        // לחיצה רגילה פותחת את הכרטיס שנבחר (השלמה עצמית)
        lvCards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Card card = cards.get(position);
                Intent intent = new Intent(CardsListActivity.this,
                        CardActivity.class);
                intent.putExtra(MainActivity.KEY_CARD, card.getKey());
                intent.putExtra(MainActivity.KEY_NAME, card.getName());
                intent.putExtra(MainActivity.KEY_JOB, card.getJob());
                intent.putExtra(MainActivity.KEY_PHONE, card.getPhone());
                intent.putExtra(MainActivity.KEY_SITE, card.getSite());
                startActivity(intent);
            }
        });

        // משימה - לחיצה ארוכה מבקשת אישור למחיקה
        lvCards.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                askDelete(cards.get(position));
                return true;
            }
        });
    }

    // דיאלוג אישור לפני מחיקת הכרטיס מהענן
    private void askDelete(final Card c) {
        new AlertDialog.Builder(this)
                .setTitle("מחיקת כרטיס")
                .setMessage("למחוק את הכרטיס של " + c.getName() + "?")
                .setPositiveButton("מחק", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (c.getKey() == null) {
                            Toast.makeText(CardsListActivity.this,
                                    "לא נמצא מפתח לכרטיס", Toast.LENGTH_LONG).show();
                            return;
                        }

                        ref.child(c.getKey()).removeValue()
                                .addOnSuccessListener(unused ->
                                        Toast.makeText(CardsListActivity.this,
                                                "נמחק", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e ->
                                        Toast.makeText(CardsListActivity.this,
                                                "המחיקה נכשלה: " + e.getMessage(),
                                                Toast.LENGTH_LONG).show());
                    }
                })
                .setNegativeButton("ביטול", null)
                .show();
    }

    //משימה
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ref != null && listener != null) {
            ref.removeEventListener(listener);
        }
    }
}
