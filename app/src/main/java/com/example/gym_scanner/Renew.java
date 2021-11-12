package com.example.gym_scanner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.gym_scanner.databinding.ActivityRenewBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Renew extends AppCompatActivity {
    FirebaseFirestore firebaseFirestore;
    ActivityRenewBinding binding;
    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityRenewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
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
                binding.username.setText(userID);



            }
        }

    }

    public void renew_button(View view) {
        System.out.println(userID);

        renew_setup();


    }

    void renew_setup() {
        String today_D = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
        Map<String, Object> edit = new HashMap<>();
        Double days = Double.parseDouble(binding.daysNumber.getText().toString().trim());
        System.out.println(days);
        edit.put("daysnumber",days);
        edit.put("date",today_D);



        documentReference.update(edit).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(Renew.this,"Succecedd",Toast.LENGTH_LONG).show();

            }
        });

    }

    public void scan_ac(View view) {
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setCaptureActivity(Capture.class);
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        intentIntegrator.setPrompt("Scanning..");
        intentIntegrator.initiateScan();
        System.out.println("USer is " + userID);
        binding.username.setText(userID);

    }
}