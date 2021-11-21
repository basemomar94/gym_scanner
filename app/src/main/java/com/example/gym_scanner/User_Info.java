package com.example.gym_scanner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gym_scanner.databinding.ActivityUserInfoBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class User_Info extends AppCompatActivity {

    String userid;
    FirebaseFirestore firebaseFirestore;
    CollectionReference collectionReference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    TextView username, id, phone, mail, admin;
    ImageView imageView;
    String pagetitle;
    ActivityUserInfoBinding binding;
    String mail_user;
    RecyclerView recyclerView;

    private ArrayList<date_item> Date_list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        userid = getIntent().getStringExtra("user");
        actionBar.setTitle(userid);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        storageReference = firebaseStorage.getReference();
        username = findViewById(R.id.birth_info);
        id = findViewById(R.id.id_info);
        phone = findViewById(R.id.phone_info);
        mail = findViewById(R.id.mail_info);
        imageView = findViewById(R.id.profile_info);
        Date_list = new ArrayList<>();
        recyclerView = findViewById(R.id.recycle_dates);


    }

    @Override
    protected void onStart() {


        super.onStart();

        getuser_info();


        System.out.println(userid + "TEST");
        Downloaduserphoto();
    }

    //Download user photo

    void Downloaduserphoto() {

        Toast.makeText(User_Info.this, userid, Toast.LENGTH_LONG).show();

        // Create a reference with an initial file path and name
        StorageReference profile = storageReference.child("image/profile/" + userid);
        System.out.println(profile + "profile");
        long MaxBYTES = 1024 * 1024;
        profile.getBytes(MaxBYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bitmap);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(User_Info.this, e.getMessage(), Toast.LENGTH_LONG).show();

            }
        });

    }


    void copytoClip(TextView textView) {
        ClipboardManager _clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        _clipboard.setText(textView.getText());
        Toast.makeText(User_Info.this, textView.getText(), Toast.LENGTH_LONG).show();


    }

    public void copy(View view) {


        switch (view.getId()) {
            case R.id.birth_info:
                copytoClip(binding.nameInfo);
                break;
            case R.id.id_info:
                copytoClip(binding.idInfo);
                break;
            case R.id.mail_info:
                copytoClip(binding.mailInfo);
                break;
            case R.id.phone_info:
                copytoClip(binding.phoneInfo);
                break;

        }
    }

    public void manage(View view) {
        Intent intent = new Intent(User_Info.this, Activation.class);
        intent.putExtra("user", userid);
        startActivity(intent);

    }

    void getuser_info() {
        try {
            DocumentReference documentReference = firebaseFirestore.collection("users").document(userid);
            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    pagetitle = (value.getString("fname") + " " + value.getString("lname"));

                    username.setText(value.getString("fname") + " " + value.getString("lname"));
                    id.setText(userid);
                    phone.setText(value.getString("phone"));
                    mail.setText(value.getString("mail"));
                    mail_user = value.getString("mail");
                    if (mail_user != null) {
                        getdates();
                    }


                }
            });

        } catch (Exception e) {
        }

    }

    void datesRecycle() {


        Date_Adpater date_adpater = new Date_Adpater(Date_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(date_adpater);


    }

    void getdates() {
        System.out.println(mail_user + "mail");

        firebaseFirestore.collection(mail_user).orderBy("stamp", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                    DocumentReference documentReference = firebaseFirestore.collection(mail_user).document(queryDocumentSnapshot.getId());
                    documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            String admin_user = value.getString("Admin");
                            String time = value.getString("time");
                            Date_list.add(new date_item(queryDocumentSnapshot.getId(), admin_user, time));
                            datesRecycle();


                        }
                    });


                }

            }

        });

    }
}