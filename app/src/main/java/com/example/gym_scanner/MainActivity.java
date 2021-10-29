package com.example.gym_scanner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button scan, currentusers;
    String userID;
    String username;
    FirebaseFirestore firebaseFirestore;
    TextView now, today, lastuser_name, lastuser_id;
    String dayofMonth = "1";
    final static String today_vistors = "today";
    int users_today;
    EditText editText;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    ImageView profile_pic;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scan = findViewById(R.id.scan);
        currentusers = findViewById(R.id.currentusers);
        now = findViewById(R.id.now);
        today = findViewById(R.id.today);
        lastuser_name = findViewById(R.id.lastuser_name);
        lastuser_id = findViewById(R.id.lastuser_id);
        scan.setOnClickListener(this);
        editText=findViewById(R.id.announcemt);
        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();
        profile_pic=findViewById(R.id.profile_pic);

        firebaseFirestore = FirebaseFirestore.getInstance();
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendmessage();

            }
        });

        update_today();


    }

    @Override
    public void onClick(View view) {
        scan();
        System.out.println("user" + userID);
    }

    void scan() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setCaptureActivity(Capture.class);
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        intentIntegrator.setPrompt("Scanning..");
        intentIntegrator.initiateScan();
        System.out.println("USer is " + userID);

    }


    // Get the results:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {

                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                userID = result.getContents();
                users_today++;
                savetoday();
                today.setText("Today: " + users_today);
                System.out.println(users_today + "check");

                lastuser_id.setText(userID);

                //Getting the username from the scanned ID
                DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
                documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                        lastuser_name.setText(value.getString("fname"));
                        username = value.getString("fname");

                        //Adding current users
                        DocumentReference documentReference1 = firebaseFirestore.collection("currentusers").document("Day1");
                        Map<String, Object> currentusers = new HashMap<>();
                        currentusers.put("name", username);
                        currentusers.put("userid", userID);

                        documentReference1.set(currentusers).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                Downloaduserphoto();


                            }
                        });
                    }
                });
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



    public void goToUsers(View view) {
        Intent intent = new Intent(MainActivity.this, com.example.gym_scanner.currentusers.class);
        intent.putExtra("userid", userID);
        startActivity(intent);
    }

    void savetoday() {

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("today", users_today);
        editor.commit();

    }

    void update_today() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        users_today = sharedPreferences.getInt("today", Integer.parseInt("0"));
        today.setText("Today " + users_today);


    }

    public void logo(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        users_today = 0;
        editor.clear();
        today.setText("Today " + users_today);
        savetoday();
        System.out.println(users_today);

    }

    public void send(View view) {
        sendmessage();
    }

    void sendmessage (){
        DocumentReference documentReference =firebaseFirestore.collection("message").document("message");
        Map<String,Object> message = new HashMap<>();
        message.put("message",editText.getText().toString().trim());
        documentReference.set(message).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        });
        editText.setText("");

    }
    void Downloaduserphoto(){
        // Create a reference with an initial file path and name
        StorageReference profile = storageReference.child("image/profile/"+userID);
        long MaxBYTES = 1024*1024;
        profile.getBytes(MaxBYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                profile_pic.setImageBitmap(bitmap);
                Toast.makeText(MainActivity.this,"Found",Toast.LENGTH_LONG).show();




            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();

            }
        });

    }

}




