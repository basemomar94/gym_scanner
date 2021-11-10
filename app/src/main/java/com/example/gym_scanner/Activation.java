package com.example.gym_scanner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.gym_scanner.databinding.ActivityActivationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Activation extends AppCompatActivity {
    String userID;
    FirebaseFirestore firebaseFirestore;
    String activation;
    ActivityActivationBinding binding;
    boolean Editing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Editing=false;

        super.onCreate(savedInstanceState);
        binding=ActivityActivationBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        firebaseFirestore = FirebaseFirestore.getInstance();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();


            } else {
                userID = result.getContents();
                Toast.makeText(this, "Scanned: " + userID, Toast.LENGTH_LONG).show();
                gettindata();


            }
        }

    }

    public void newact(View view) {




        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setCaptureActivity(Capture.class);
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        intentIntegrator.setPrompt("Scanning..");
        intentIntegrator.initiateScan();
        System.out.println("USer is " + userID);

        //onActivity
    }

    public void Edit(View view) {
        updatinginfo();
        System.out.println("click");
        System.out.println(Editing);
        if (!Editing){
            System.out.println(false);
            binding.editLayout.setVisibility(View.VISIBLE);
            binding.infoLayout.setVisibility(View.INVISIBLE);
            binding.edit.setText("Save");
            Editing=true;

        } else {
            binding.editLayout.setVisibility(View.INVISIBLE);
            binding.infoLayout.setVisibility(View.VISIBLE);
            binding.edit.setText("Edit");
            Editing=false;
        }

    }
    void gettindata (){
        binding.usercard.setVisibility(View.VISIBLE);
        DocumentReference documentReference =firebaseFirestore.collection("users").document(userID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                String activation=value.getString("activation");
                System.out.println(activation);
                if (activation.equals("true")){
                    binding.status.setText("This account is active");

                } else {
                    binding.status.setText("This account is inactive");
                }
                binding.userid.setText("User ID : "+userID);
                binding.fnameRight.setText("first name : "+value.getString("fname"));
                binding.fnameEdit.setText(value.getString("fname"));

                binding.lnameRight.setText("last name : "+value.getString("lname"));
                binding.lnameEdit.setText(value.getString("lname"));

                binding.phoneRight.setText("phone : "+value.getString("phone"));
                binding.phoneEdit.setText(value.getString("phone"));

                binding.subscribtionDateRight.setText("Subscribtion date : "+value.getString("date"));
                binding.subscribtionEdit.setText(value.getString("date"));

             //   binding.daysRight.setText("number of days : "+value.getString("daysnumber"));




            }
        });
    }

    public void activateNow(View view) {
        DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
        Map<String, Object> add = new HashMap<>();
        add.put("activation", "true");
        documentReference.update(add).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


            }
        });


    }
    void updatinginfo (){
        DocumentReference documentReference =firebaseFirestore.collection("users").document(userID);
        Map<String,Object> update = new HashMap<>();
        update.put("fname",binding.fnameEdit.getText().toString().trim());
        update.put("lname",binding.lnameEdit.getText().toString().trim());
        update.put("phone",binding.phoneEdit.getText().toString().trim());
        update.put("date",binding.subscribtionEdit.getText().toString().trim());
        update.put("daysnumber",binding.daysEdit.getText().toString().trim());
        documentReference.update(update).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(Activation.this,"Updates were saved",Toast.LENGTH_LONG).show();

            }
        });
    }
}