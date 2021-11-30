package com.bassem.gym_scanner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class Login extends AppCompatActivity {

    EditText mail,password;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;
    FirebaseFirestore firebaseFirestore;

    public static final String MY_PREFS_NAME = "gym";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_login);

        mail=findViewById(R.id.mail);
        password=findViewById(R.id.password);
        progressBar=findViewById(R.id.loading_log);
        firebaseFirestore=FirebaseFirestore.getInstance();






    }

    @Override
    protected void onStart() {
        super.onStart();







    }

    public void login(View view) {
        progressBar.setVisibility(View.VISIBLE);

        try {
            DocumentReference documentReference =firebaseFirestore.collection("admins").document(mail.getText().toString().trim());
            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    System.out.println(mail.getText().toString().trim());
                    System.out.println(password.getText().toString().trim());
                    String firebase_password=value.getString("password");
                    String login_password=password.getText().toString().trim();

                    if (login_password.equals(firebase_password)){



                        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE).edit();
                        editor.putString("log",mail.getText().toString().trim());
                        editor.apply();
                        Intent intent = new Intent(Login.this,Dashboard.class);
                        startActivity(intent);
                        finish();


                    }
                    else {
                        Toast.makeText(Login.this,"Please check your info",Toast.LENGTH_LONG).show();
                    }
                    progressBar.setVisibility(View.INVISIBLE);

                }
            });
        } catch (Exception e){
            Toast.makeText(Login.this,"Please check your info",Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);

        }
        }


    }
