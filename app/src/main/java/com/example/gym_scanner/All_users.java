package com.example.gym_scanner;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class All_users extends AppCompatActivity {
    private RecyclerView recyclerView;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference collectionReference;
    Firebase_All firebase_all;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
        recyclerView = findViewById(R.id.recycle_all);
        Setup_Recycle();
    }

    void Setup_Recycle(){
        collectionReference=firebaseFirestore.collection("users");
        Query query =collectionReference.orderBy("stamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<All_item> options = new FirestoreRecyclerOptions.Builder<All_item>().setQuery(query, All_item.class).build();
        firebase_all= new Firebase_All(options);
        RecyclerView recyclerView = findViewById(R.id.recycle_all);
       // recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(firebase_all);
        firebase_all.setOnitemClick(new Firebase_Adapter_users.OnitemClick() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                All_item all_item = documentSnapshot.toObject(All_item.class);
                String userIDD = documentSnapshot.getId();
                Intent intent = new Intent(All_users.this,User_Info.class);
                intent.putExtra("user",userIDD);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        Setup_Recycle();
        firebase_all.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebase_all.stopListening();
    }
}