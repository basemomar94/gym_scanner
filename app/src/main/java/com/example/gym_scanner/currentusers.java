package com.example.gym_scanner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class currentusers extends AppCompatActivity {

    private RecyclerView recyclerView;

    private MyAdapter myAdapter;
    private ArrayList<User> userArrayList;
    String name;
    String userid;
    String today_firebase;
    EditText date;
    TextView date_textview;
    int numberofusers;


    TextView user;
    FirebaseFirestore firebaseFirestore =FirebaseFirestore.getInstance();
    CollectionReference collectionReference;
    NoteAdapter noteAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currentusers);

        userid = getIntent().getStringExtra("userid");
        date=findViewById(R.id.date);
        date_textview=findViewById(R.id.date_textview);

        Calendar calendar = Calendar.getInstance();
        int dayofmonth = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH)+1;
        int year = calendar.get(Calendar.YEAR);
        today_firebase = dayofmonth + "-" + month + "-" + year;
        System.out.println(today_firebase);

        recyclerView=findViewById(R.id.users_recycle);

        setupRecycle();
        getCount();



        //getting user data
        /*firebaseFirestore=FirebaseFirestore.getInstance();
        DocumentReference documentReference = firebaseFirestore.collection("users").document(userid);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

              name=value.getString("fname");


             // userid=value.getString("userid");
                System.out.println(name+"checktest");

            }
        });*/

        //Recycle view




    }

    private void setupRecycle() {
        collectionReference=firebaseFirestore.collection(today_firebase);
        Query query = collectionReference.orderBy("name", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>().setQuery(query,User.class).build();
        noteAdapter= new NoteAdapter(options);
        RecyclerView recyclerView=findViewById(R.id.users_recycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(noteAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        noteAdapter.startListening();
    }

    public void search(View view) {
        try {
            String targetdate = date.getText().toString().trim();
            today_firebase=targetdate;
            recyclerView.setVisibility(View.VISIBLE);
            System.out.println(today_firebase);
            setupRecycle();
            getCount();
            date_textview.setText("List of users on "+today_firebase+"("+numberofusers+")");

            noteAdapter.startListening();
            date.setText("");
        } catch (Exception e){

            recyclerView.setVisibility(View.INVISIBLE);

            noteAdapter.startListening();
            date_textview.setText("Please enter a date");
            Toast.makeText(currentusers.this,e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }

    void getCount (){
        firebaseFirestore.collection(today_firebase).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                System.out.println(task.getResult().size());
                numberofusers=task.getResult().size();
                date_textview.setText("List of users on "+today_firebase+"("+numberofusers+")");

            }
        });
    }
}

