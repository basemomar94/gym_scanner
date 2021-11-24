package com.example.gym_scanner;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gym_scanner.databinding.ActivityAllUsersBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class All_users extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private RecyclerView recyclerView;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference collectionReference;
    Firebase_All firebase_all;
    ActivityAllUsersBinding binding;
    boolean active;
    Query query;
    String searchinput;



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

        //Switch key settings
        binding.activeKey.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == true) {
                    active=true;
                    System.out.println(active);
                    Setup_Recycle();
                    active=false;


                    firebase_all.startListening();
                    binding.activeKey.setText("Active");
                } else {
                    Setup_Recycle();
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
        System.out.println(active +"check");
    }

    public void search(View view) {

        search_fun();
    }

    void search_fun(){
        String text = binding.search.getText().toString().trim();
        collectionReference = firebaseFirestore.collection("users");
        Query query = collectionReference.orderBy("fname", Query.Direction.DESCENDING).startAt(text);
        FirestoreRecyclerOptions<All_item> options = new FirestoreRecyclerOptions.Builder<All_item>().setQuery(query, All_item.class).build();


        firebase_all = new Firebase_All(options);
        RecyclerView recyclerView = findViewById(R.id.recycle_all);
        // recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(firebase_all);
        firebase_all.startListening();


    }
    //spinner settings

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        searchinput=adapterView.getItemAtPosition(i).toString();
        System.out.println(searchinput);

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}