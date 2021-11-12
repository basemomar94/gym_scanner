package com.example.gym_scanner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

public class User_Info extends AppCompatActivity {

    String userid;
    FirebaseFirestore firebaseFirestore;
    CollectionReference collectionReference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    TextView username,id,phone,mail,admin;
    ImageView imageView;
    String pagetitle;
    Firebase_Adapter_dates firebase_adapter_dates;
    ActivityUserInfoBinding binding;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityUserInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        userid = getIntent().getStringExtra("user");
        actionBar.setTitle(userid);

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        storageReference = firebaseStorage.getReference();
        username=findViewById(R.id.name_info);
        id=findViewById(R.id.id_info);
        phone=findViewById(R.id.phone_info);
        mail=findViewById(R.id.mail_info);
        imageView=findViewById(R.id.profile_info);






    }

    @Override
    protected void onStart() {


        super.onStart();
        setupRecycle();

        try {
            DocumentReference documentReference =firebaseFirestore.collection("users").document(userid);
            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    pagetitle=( value.getString("fname")+" "+value.getString("lname"));

                    username.setText( value.getString("fname")+" "+value.getString("lname"));
                    id.setText(userid);
                    phone.setText(value.getString("phone"));
                    mail.setText(value.getString("mail"));




                }
            });

        } catch (Exception e){}

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
                


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(User_Info.this, e.getMessage(), Toast.LENGTH_LONG).show();

            }
        });

    }
    void setupRecycle (){
        collectionReference = FirebaseFirestore.getInstance().collection(userid);
        Query query = collectionReference;
        FirestoreRecyclerOptions<date_item> options = new FirestoreRecyclerOptions.Builder<date_item>().setQuery(query,date_item.class).build();
        firebase_adapter_dates = new Firebase_Adapter_dates(options);
        RecyclerView recyclerView = findViewById(R.id.date_recycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(firebase_adapter_dates);

    }


    void copytoClip(TextView textView){
        ClipboardManager _clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        _clipboard.setText(textView.getText());
        Toast.makeText(User_Info.this,textView.getText(),Toast.LENGTH_LONG).show();


    }

    public void copy(View view) {


        switch (view.getId()){
            case R.id.name_info:copytoClip(binding.nameInfo);
            break;
            case R.id.id_info:copytoClip(binding.idInfo);
            break;
            case R.id.mail_info:copytoClip(binding.mailInfo);
            break;
            case R.id.phone_info:copytoClip(binding.phoneInfo);
            break;

        }
    }
}