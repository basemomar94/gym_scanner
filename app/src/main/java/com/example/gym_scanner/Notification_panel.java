package com.example.gym_scanner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class Notification_panel extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;
    EditText announcement;
    TextView current_ann;
    String message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_panel);
        firebaseFirestore = FirebaseFirestore.getInstance();
        announcement = findViewById(R.id.announcemt);
        current_ann = findViewById(R.id.current_message);
        getcurrent_message();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Notification Panel");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void send(View view) {
        sendmessage();


    }

    void sendmessage() {


        DocumentReference documentReference = firebaseFirestore.collection("message").document("message");
        Map<String, Object> message = new HashMap<>();
        message.put("message", announcement.getText().toString().trim());

        documentReference.set(message).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        });
        announcement.setText("");

    }

    void getcurrent_message() {
        DocumentReference documentReference = firebaseFirestore.collection("message").document("message");
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                current_ann.setText(value.getString("message"));

            }
        });
    }

    public void Delete(View view) {
        DocumentReference documentReference = firebaseFirestore.collection("message").document("message");


        documentReference.update("message", FieldValue.delete());


    }
}