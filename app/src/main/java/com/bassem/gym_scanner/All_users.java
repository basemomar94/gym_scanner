package com.bassem.gym_scanner;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bassem.gym_scanner.databinding.ActivityAllUsersBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Locale;

public class All_users extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private RecyclerView recyclerView;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference collectionReference;
    Firebase_All firebase_all;
    ActivityAllUsersBinding binding;
    boolean active=true;
    Query query;
    Query query_Search;
    String searchinput;
    int number;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityAllUsersBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        recyclerView = findViewById(R.id.recycle_all);
        Setup_Recycle();
        binding.activeKey.setChecked(true);
        count();

        //Switch key settings
        binding.activeKey.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == true) {
                    active=true;
                    System.out.println(active);
                    Setup_Recycle();
                    count();
                    active=false;


                    firebase_all.startListening();
                    binding.activeKey.setText("Active");
                } else {
                    Setup_Recycle();
                    count();
                    binding.activeKey.setText("All");
                    active=true;
                    firebase_all.startListening();
                }
            }
        });

        //spinner setup
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.search_spinner,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.searchSpinner.setOnItemSelectedListener(this);






        //search input

       binding.search.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

           }

           @Override
           public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

           }

           @Override
           public void afterTextChanged(Editable editable) {
               search_fun();
               if (binding.search.getText().toString().isEmpty()){
                   Setup_Recycle();
               }

           }
       });
    }




    void Setup_Recycle() {

        collectionReference = firebaseFirestore.collection("users");
        if (active==false){

                query = collectionReference.orderBy("stamp", Query.Direction.DESCENDING);

            }

         else {
            query = collectionReference.orderBy("stamp", Query.Direction.DESCENDING).whereEqualTo("activation", true);

        }
        FirestoreRecyclerOptions<All_item> options = new FirestoreRecyclerOptions.Builder<All_item>().setQuery(query, All_item.class).build();


        firebase_all = new Firebase_All(options);
        RecyclerView recyclerView = findViewById(R.id.recycle_all);
        // recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(firebase_all);
        firebase_all.setOnitemClick(new Firebase_Adapter_users.OnitemClick() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                All_item all_item = documentSnapshot.toObject(All_item.class);
                String userIDD = documentSnapshot.getId();
                Intent intent = new Intent(All_users.this, User_Info.class);
                intent.putExtra("user", userIDD);
                startActivity(intent);
            }
        });
      //  binding.countTV.setText(number);


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

    @Override
    protected void onResume() {
        super.onResume();
        if (binding.activeKey.isChecked()){
            active=true;
            Setup_Recycle();
            active=false;
        } else {
            active=false;
            Setup_Recycle();
            active=true;
        }


        firebase_all.startListening();
    }

    public void search(View view) {


    }

    void search_fun(){
        binding.activeKey.setChecked(false);
        binding.countTV.setVisibility(View.INVISIBLE);

       String firebase_search="fname";
        switch (searchinput) {
            case "name":{
                firebase_search="fname";


            }
            break;
            case "mail":{
                firebase_search="mail";

            }
            break;
            case "phone":{
                firebase_search="phone";

            }
            break;
        }
        String text = binding.search.getText().toString().trim().toLowerCase(Locale.ROOT);
        collectionReference = firebaseFirestore.collection("users");

            query = collectionReference.orderBy(firebase_search, Query.Direction.DESCENDING).startAt(text);

        FirestoreRecyclerOptions<All_item> options = new FirestoreRecyclerOptions.Builder<All_item>().setQuery(query, All_item.class).build();




        firebase_all = new Firebase_All(options);
        RecyclerView recyclerView = findViewById(R.id.recycle_all);
        // recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(firebase_all);
        firebase_all.startListening();
        firebase_all.setOnitemClick(new Firebase_Adapter_users.OnitemClick() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
              //  All_item all_item = documentSnapshot.toObject(All_item.class);

                String userIDD = documentSnapshot.getId();
                Intent intent = new Intent(All_users.this, User_Info.class);
                intent.putExtra("user", userIDD);
                startActivity(intent);

            }
        });


    }
    //spinner settings

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        searchinput=adapterView.getItemAtPosition(i).toString();
        switch (searchinput) {
            case "name":{

                binding.search.setHint("Enter first name");

            }
            break;
            case "mail":{
                binding.search.setHint("Enter user's mail");
            }
            break;
            case "phone":{

                binding.search.setHint("Enter user's phone");
            }
            break;
        }

        System.out.println(searchinput);

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    void count ( ){

        if (active==true){
            firebaseFirestore.collection("users").orderBy("stamp", Query.Direction.DESCENDING).whereEqualTo("activation",true).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {


                    number =   task.getResult().size();
                    System.out.println(number+"========================test");
                    binding.countTV.setVisibility(View.VISIBLE);

                    binding.countTV.setText("number of active users "+String.valueOf(number));

                }
            });
        } else {

            firebaseFirestore.collection("users").orderBy("stamp", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {


                    number =   task.getResult().size();
                    System.out.println(number+"========================test");
                    binding.countTV.setVisibility(View.VISIBLE);
                    binding.countTV.setText("number of all users "+String.valueOf(number));

                }
            });
        }


    }
}