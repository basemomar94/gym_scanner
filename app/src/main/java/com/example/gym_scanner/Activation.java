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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();


            } else {
                userID = result.getContents();
                Toast.makeText(this, "Scanned: " + userID, Toast.LENGTH_LONG).show();
                DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
                Map<String, Object> add = new HashMap<>();
                add.put("activation", "true");
                documentReference.update(add).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });


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
}