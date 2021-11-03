package com.example.gym_scanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;

public class users_today extends AppCompatActivity {

    private RecyclerView recyclerView;


    private ArrayList<User> userArrayList;
    String name;
    String userid;
    String today_firebase;
    EditText date;
    TextView date_textview;
    int numberofusers;


    TextView user;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference collectionReference;
    Firebase_Adapter firebaseAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currentusers);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Users Today");

        userid = getIntent().getStringExtra("userid");
        date = findViewById(R.id.date);
        date_textview = findViewById(R.id.date_textview);

        Calendar calendar = Calendar.getInstance();
        int dayofmonth = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        today_firebase = dayofmonth + "-" + month + "-" + year;
        System.out.println(today_firebase);

        recyclerView = findViewById(R.id.users_recycle);

        setupRecycle();
        getCount();
        date_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(users_today.this,User_Info.class);
                startActivity(intent);
            }
        });


    }

    private void setupRecycle() {
        collectionReference = firebaseFirestore.collection(today_firebase);
        Query query = collectionReference.orderBy("name", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>().setQuery(query, User.class).build();
        firebaseAdapter = new Firebase_Adapter(options);
        RecyclerView recyclerView = findViewById(R.id.users_recycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(firebaseAdapter);
        firebaseAdapter.setOnitemClick(new Firebase_Adapter.OnitemClick() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                User user = documentSnapshot.toObject(User.class);
                String userIDD = documentSnapshot.getId();
                Intent intent = new Intent(users_today.this,User_Info.class);
                intent.putExtra("user",userIDD);
                startActivity(intent);

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAdapter.startListening();
    }

    public void search(View view) {
        try {
            String targetdate = date.getText().toString().trim();
            today_firebase = targetdate;
            recyclerView.setVisibility(View.VISIBLE);
            System.out.println(today_firebase);
            setupRecycle();
            getCount();
            date_textview.setText("List of users on " + today_firebase + "(" + numberofusers + ")");

            firebaseAdapter.startListening();
            date.setText("");
        } catch (Exception e) {

            recyclerView.setVisibility(View.INVISIBLE);

            firebaseAdapter.startListening();
            date_textview.setText("Please enter a date");
            Toast.makeText(users_today.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    void getCount() {
        firebaseFirestore.collection(today_firebase).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                System.out.println(task.getResult().size());
                numberofusers = task.getResult().size();
                date_textview.setText("List of users on " + today_firebase + "(" + numberofusers + ")");

            }
        });
    }
    void goto_info(){
        Intent intent = new Intent(users_today.this,User_Info.class);
        startActivity(intent);
    }


}

