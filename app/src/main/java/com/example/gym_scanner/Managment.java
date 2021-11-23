package com.example.gym_scanner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.gym_scanner.databinding.ActivityManagmentBinding;
import com.example.gym_scanner.databinding.ActivityManagmentBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Managment extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    String userID;
    FirebaseFirestore firebaseFirestore;

    ActivityManagmentBinding binding;
    boolean Editing;
    boolean status;
    StorageReference storageReference;
    FirebaseStorage firebaseStorage;
    Double numberofdays;
    String date;
    String searchinput;
    int actual_remaining;
    String today_Date;
    Boolean activation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Editing = false;

        super.onCreate(savedInstanceState);
        binding = ActivityManagmentBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //spinner menue setup
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinnerItems, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinner.setOnItemSelectedListener(this);
        directdata();


    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        searchinput = adapterView.getItemAtPosition(i).toString();


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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
               // Toast.makeText(this, "Scanned: " + userID, Toast.LENGTH_LONG).show();
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

        System.out.println("click");
        System.out.println(Editing);
        if (!Editing) {
            System.out.println(false);
            binding.editLayout.setVisibility(View.VISIBLE);
            binding.infoLayout.setVisibility(View.INVISIBLE);
            binding.edit.setText("Save");
            Editing = true;
            binding.renew.setVisibility(View.INVISIBLE);
            binding.activateButton.setVisibility(View.INVISIBLE);

        } else {
            updatinginfo();
            binding.editLayout.setVisibility(View.INVISIBLE);
            binding.infoLayout.setVisibility(View.VISIBLE);
            binding.edit.setText("Edit");
            binding.renew.setVisibility(View.VISIBLE);
            binding.activateButton.setVisibility(View.VISIBLE);
            Editing = false;
        }

    }

    void gettindata() {
        try {
            binding.usercard.setVisibility(View.VISIBLE);
            Downloaduserphoto();
            if (userID != null) {
            }
            DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    activation = value.getBoolean("activation");
                    System.out.println(activation);
                    try {
                        if (activation == true) {


                            binding.status.setText("This account is active");
                            binding.status.setTextColor(Color.GREEN);
                            binding.activateButton.setText("Deactivate");

                        } else {

                            binding.status.setTextColor(Color.RED);
                            binding.status.setText("This account is inactive");
                            binding.activateButton.setText("Activate");
                        }
                    } catch (Exception e) {

                    }
                    try {
                        binding.userid.setText("User ID : " + userID);
                        binding.fnameRight.setText("first name : " + value.getString("fname"));
                        binding.fnameEdit.setText(value.getString("fname"));

                        binding.lnameRight.setText("last name : " + value.getString("lname"));
                        binding.lnameEdit.setText(value.getString("lname"));

                        binding.phoneRight.setText("phone : " + value.getString("phone"));
                        binding.phoneEdit.setText(value.getString("phone"));

                        binding.subscribtionDateRight.setText("Subscribtion date : " + value.getString("date"));
                        binding.subscribtionEdit.setText(value.getString("date"));
                        date = value.getString("date");

                        binding.daysRight.setText("number of days : " + value.getDouble("daysnumber").toString());
                        binding.daysEdit.setText(value.getDouble("daysnumber").toString());
                        numberofdays = value.getDouble("daysnumber");
                        calculate_remaing();
                    } catch (Exception e) {
                        binding.usercard.setVisibility(View.INVISIBLE);

                    }


                }
            });
        } catch (Exception e) {

            binding.usercard.setVisibility(View.INVISIBLE);
        }


    }

    public void activateNow(View view) {

        DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
        Map<String, Object> add = new HashMap<>();
        if (!activation) {

            add.put("activation", true);
            add.put("date", today_Date);
            add.put("stamp", FieldValue.serverTimestamp());

        } else {
            add.put("activation", false);
        }

        documentReference.update(add).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


            }
        });


    }

    void updatinginfo() {
        try {
            System.out.println(userID + "Err");
            DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);

            Map<String, Object> update = new HashMap<>();


            update.put("fname", binding.fnameEdit.getText().toString().trim());
            update.put("lname", binding.lnameEdit.getText().toString().trim());
            update.put("phone", binding.phoneEdit.getText().toString().trim());
            update.put("date", binding.subscribtionEdit.getText().toString().trim());
            Double days = Double.parseDouble(binding.daysEdit.getText().toString());
            update.put("daysnumber", days);
            documentReference.update(update).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(Managment.this, "Updates were saved", Toast.LENGTH_LONG).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Managment.this, e.getMessage().toString(), Toast.LENGTH_LONG).show();


                }
            });
        } catch (Exception e) {
            Toast.makeText(Managment.this, e.getMessage().toString(), Toast.LENGTH_LONG).show();
            System.out.println(e);

        }

    }

    public void search(View view) {


        try {
            if (searchinput.equals("id")) {
                userID = binding.enterdUserid.getText().toString().trim();
                gettindata();
            } else {
                CollectionReference collectionReference = firebaseFirestore.collection("users");

                collectionReference.whereEqualTo(searchinput, binding.enterdUserid.getText().toString().trim()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                System.out.println(document.getId());
                                userID = document.getId();
                                gettindata();

                            }
                        }

                    }
                });
            }


        } catch (Exception e) {
            Toast.makeText(Managment.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }

    void Downloaduserphoto() {
        try {
            // Create a reference with an initial file path and name
            StorageReference profile = storageReference.child("image/profile/" + userID);
            System.out.println(profile.toString() + "profile");
            long MaxBYTES = 1024 * 1024;
            profile.getBytes(MaxBYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {

                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    binding.actPro.setImageBitmap(bitmap);


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(Managment.this, e.getMessage(), Toast.LENGTH_LONG).show();

                }
            });

        } catch (Exception e) {
            Toast.makeText(Managment.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    public void test(View view) {
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {

                int id = (int) parent.getItemAtPosition(position);
                System.out.println(id + "check");


                switch (id) {
                    case 0:
                        System.out.println(0);
                        break;
                    case 1:
                        System.out.println(1);
                        break;
                    default:
                        System.out.println("NO");

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    void renew_setup() {
        Double days;

        String today_D = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
        Map<String, Object> edit = new HashMap<>();
        if (actual_remaining>0){
             days = Double.parseDouble(binding.daysNumber.getText().toString().trim()) + actual_remaining;
        } else days=Double.parseDouble(binding.daysNumber.getText().toString().trim());

        System.out.println(days);
        edit.put("daysnumber", days);
        edit.put("date", today_D);


        documentReference.update(edit).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(Managment.this, "Succecedd", Toast.LENGTH_LONG).show();

            }
        });

    }

    public void renew_button(View view) {
        renew_setup();
    }

    void calculate_remaing() {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date date_Sub = simpleDateFormat.parse(date);
            today_Date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
            Date today_date = simpleDateFormat.parse(today_Date);
            long remaing = Math.abs(today_date.getTime() - date_Sub.getTime());
            int diffenrence = (int) TimeUnit.DAYS.convert(remaing, TimeUnit.MILLISECONDS);
            actual_remaining = (int) (numberofdays - diffenrence);


            binding.remaingTx.setText("remaing days : " + actual_remaining);


        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    void searchbymobile() {

    }

    void directdata() {
        String intent_id = getIntent().getStringExtra("user");

        if (intent_id != null) {
            userID = intent_id;
            gettindata();
            Downloaduserphoto();
        }
    }


    public void go_to_info(View view) {
        Intent intent = new Intent(Managment.this, User_Info.class);
        intent.putExtra("user", userID);
        startActivity(intent);
    }
}