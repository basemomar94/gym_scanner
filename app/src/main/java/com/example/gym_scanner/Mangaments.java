package com.example.gym_scanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Mangaments extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mangaments);
    }

    public void activation(View view) {
        Intent intent = new Intent(Mangaments.this,Activation.class);
        startActivity(intent);
    }

    public void Renew(View view) {
        Intent intent = new Intent(Mangaments.this,Renew.class);
        startActivity(intent);
    }
}