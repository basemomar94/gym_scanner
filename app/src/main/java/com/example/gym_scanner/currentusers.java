package com.example.gym_scanner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

public class currentusers extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdpter userAdpter;
    private ArrayList<User> users;
    String name;
    String userid;

    FirebaseFirestore firebaseFirestore;
    TextView user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currentusers);

        userid=getIntent().getStringExtra("userid");



        //getting user data
        firebaseFirestore=FirebaseFirestore.getInstance();
        DocumentReference documentReference = firebaseFirestore.collection("users").document(userid);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

              name=value.getString("fname");


             // userid=value.getString("userid");
                System.out.println(name+"checktest");

            }
        });

        //Recycle view

        System.out.println("number"+users.size());
        users.add(new User(name,userid,R.drawable.user1));
        users.add(new User("rony","dqszqsqw1szs2s",R.drawable.user1));
        userAdpter = new UserAdpter(this,users);
        recyclerView.setAdapter(userAdpter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        System.out.println("number"+users.size());

       // user=findViewById(R.id.user);

    }
}