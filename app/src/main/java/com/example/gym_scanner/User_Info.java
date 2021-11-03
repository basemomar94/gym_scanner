package com.example.gym_scanner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class User_Info extends AppCompatActivity {

    String userid;
    FirebaseFirestore firebaseFirestore;
    CollectionReference collectionReference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    TextView username,id,phone,mail,birth;
    ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        userid = getIntent().getStringExtra("user");
        actionBar.setTitle("User information");

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        storageReference = firebaseStorage.getReference();
        username=findViewById(R.id.name_info);
        id=findViewById(R.id.id_info);
        phone=findViewById(R.id.phone_info);
        mail=findViewById(R.id.mail_info);
        imageView=findViewById(R.id.profile_info);



        try {
            DocumentReference documentReference =firebaseFirestore.collection("users").document(userid);
            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                    username.setText( "User name : "+ value.getString("fname")+" "+value.getString("lname"));
                    id.setText("User id : "+userid);
                    phone.setText("Phone : "+ value.getString("phone"));
                    mail.setText("mail : "+value.getString("mail"));

                    Toast.makeText(User_Info.this, value.getString("fname"), Toast.LENGTH_LONG).show();


                }
            });

        } catch (Exception e){}


    }

    @Override
    protected void onStart() {

        super.onStart();

        System.out.println(userid+"TEST");
        Downloaduserphoto();
    }

    //Download user photo

    void Downloaduserphoto() {

        Toast.makeText(User_Info.this, userid, Toast.LENGTH_LONG).show();

        // Create a reference with an initial file path and name
        StorageReference profile = storageReference.child("image/profile/" + userid);
        System.out.println(profile+"profile");
        long MaxBYTES = 1024 * 1024;
        profile.getBytes(MaxBYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bitmap);
                Toast.makeText(User_Info.this, "Found", Toast.LENGTH_LONG).show();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(User_Info.this, e.getMessage(), Toast.LENGTH_LONG).show();

            }
        });

    }
}