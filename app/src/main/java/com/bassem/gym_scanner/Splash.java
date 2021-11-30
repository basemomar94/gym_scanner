package com.bassem.gym_scanner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class Splash extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;
    public static final String MY_PREFS_NAME = "gym";
    Boolean direct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        firebaseFirestore=FirebaseFirestore.getInstance();
        google_play();
    }
    void google_play(){
        DocumentReference documentReference =firebaseFirestore.collection("admins").document("1");
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                direct=value.getBoolean("direct");
                if (direct==true){
                    Intent intent = new Intent(Splash.this,Dashboard.class);
                    startActivity(intent);
                    finish();
                } else {
                    SharedPreferences sharedPreferences = getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE);
                    String log = sharedPreferences.getString("log",null);
                    if (log!=null){
                        Intent intent = new Intent(Splash.this,Dashboard.class);
                        startActivity(intent);
                        finish();


                    } else {
                        Intent intent = new Intent(Splash.this,Login.class);
                        startActivity(intent);
                        finish();

                    }
                }

            }
        });
    }
}