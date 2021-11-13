package com.example.gym_scanner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gym_scanner.databinding.ActivityDashboardBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Dashboard extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {


    String userID;
    String username;
    FirebaseFirestore firebaseFirestore;
    TextView now, today, lastuser_name, lastuser_id;
    String dayofMonth = "1";
    final static String today_vistors = "today";
    boolean card = false;
    int users_today;
    EditText editText;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    ImageView profile_pic;
    String today_firebase;
    String time;
    FirebaseAuth firebaseAuth;
    private ActivityDashboardBinding binding;
    String adminname;
    TextView admin;
    String MY_PREFS_NAME = "gym";
    String searchinput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        today = findViewById(R.id.today);
        lastuser_name = findViewById(R.id.lastuser_name);
        lastuser_id = findViewById(R.id.lastuser_id);
        binding.scan.setOnClickListener(this);
        editText = findViewById(R.id.announcemt);
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = firebaseStorage.getReference();
        profile_pic = findViewById(R.id.profile_pic);
        Calendar calendar = Calendar.getInstance();
        int dayofmonth = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        today_firebase = dayofmonth + "-" + month + "-" + year;
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        admin = findViewById(R.id.admin);
        time = hour + ":" + min;

        SharedPreferences sharedPreferences = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        adminname = sharedPreferences.getString("log", null);
        if (adminname != null) {
            admin.setText(adminname);
        }


        firebaseFirestore = FirebaseFirestore.getInstance();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinnerItems, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinner.setOnItemSelectedListener(this);


        // update_today();
        gettodaycount();


    }

    @Override
    public void onClick(View view) {
        scan();

    }

    void scan() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setCaptureActivity(Capture.class);
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        intentIntegrator.setPrompt("Scanning..");
        intentIntegrator.initiateScan();
        System.out.println("USer is " + userID);

    }


    // Get the results:


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {

                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                if (result.getContents().contains("//")) {
                    Toast.makeText(this, "Wrong QR", Toast.LENGTH_LONG).show();
                } else {
                    binding.visitorCard.setVisibility(View.VISIBLE);
                    userID = result.getContents();
                    System.out.println(userID);



                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    public void goToUsers(View view) {
        Intent intent = new Intent(Dashboard.this, com.example.gym_scanner.users_today.class);
        intent.putExtra("userid", userID);
        startActivity(intent);
    }


    public void send(View view) {

        Intent intent = new Intent(Dashboard.this, Notification_panel.class);
        startActivity(intent);
    }


    void Downloaduserphoto() {
        // Create a reference with an initial file path and name
        StorageReference profile = storageReference.child("image/profile/" + userID);
        long MaxBYTES = 1024 * 1024;
        profile.getBytes(MaxBYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                profile_pic.setImageBitmap(bitmap);



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(Dashboard.this, e.getMessage(), Toast.LENGTH_LONG).show();

            }
        });

    }

    void gettodaycount() {
        try {
            firebaseFirestore.collection(today_firebase).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {


                  //  today.setText("Today " + task.getResult().size());

                }
            });
        } catch (Exception e){
            today.setText("Today " + e.getMessage());
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        gettodaycount();
    }

    public void logout(View view) {


        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();

        Intent intent = new Intent(Dashboard.this, Login.class);
        startActivity(intent);
        finish();

    }

    public void test(View view) {
        DocumentReference documentReference = firebaseFirestore.collection("users").document("FD96yY0UtITduFmnh6zDEy0KvSE3");
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                String subdate = value.getString("date");
                Double daysofsub = value.getDouble("daysnumber");
                System.out.println(subdate);
                System.out.println(daysofsub);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String today_D = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                System.out.println(today_D);
                Date today_date = null;
                try {
                    today_date = simpleDateFormat.parse(today_D);
                    System.out.println(today_D);

                } catch (ParseException e) {

                }


                try {


                    Date subdate_date = simpleDateFormat.parse(subdate);
                    long remainig = Math.abs(today_date.getTime() - subdate_date.getTime());
                    int difference = (int) TimeUnit.DAYS.convert(remainig, TimeUnit.MILLISECONDS);
                    int actual_remaing = (int) (daysofsub - difference);
                    System.out.println(actual_remaing);
                    binding.remaingDays.setText(actual_remaing + "days");
                    System.out.println(actual_remaing + "Salah");


                } catch (Exception e) {
                    System.out.println(e);
                }


            }
        });

    }

    public void goto_mangaAccounts(View view) {
        Intent intent = new Intent(Dashboard.this,Activation.class);
        startActivity(intent);
    }

    public void Scan(View view) {
        if (searchinput.equals("id")) {
            userID = binding.enterdUserid.getText().toString().trim();
            getusername();
            binding.visitorCard.setVisibility(View.VISIBLE);

        } else {
            CollectionReference collectionReference = firebaseFirestore.collection("users");

            collectionReference.whereEqualTo(searchinput, binding.enterdUserid.getText().toString()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            System.out.println(document.getId());
                            userID = document.getId();
                            getusername();
                            binding.visitorCard.setVisibility(View.VISIBLE);


                        }
                    }

                }
            });
        }


    }


    //spinner setup

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        searchinput = adapterView.getItemAtPosition(i).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    void getusername(){
        firebaseFirestore.collection(today_firebase).document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    Toast.makeText(Dashboard.this, "This user has already been  scanned today", Toast.LENGTH_LONG).show();
                    lastuser_name.setText("This user has already been  scanned today");

                } else {
                    //Getting the username from the scanned ID
                    getusername();

                }
            }
        });

        try {
            DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    lastuser_name.setText(value.getString("fname"));
                    username = value.getString("fname") + " " + value.getString("lname");
                    String sub = value.getString("date");
                    Double daysofsub = value.getDouble("daysnumber");
                    binding.remaingDays.setText(daysofsub.toString());
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    try {
                        Date sub_date = simpleDateFormat.parse(sub);
                        String today_Date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                        Date today_date = simpleDateFormat.parse(today_Date);
                        long remaing = Math.abs(today_date.getTime() - sub_date.getTime());
                        int diffenrence = (int) TimeUnit.DAYS.convert(remaing, TimeUnit.MILLISECONDS);
                        System.out.println(daysofsub);
                        System.out.println(diffenrence);
                        int actual_remaining = (int) (daysofsub - diffenrence);
                        Toast.makeText(Dashboard.this, actual_remaining, Toast.LENGTH_LONG).show();
                        binding.remaingDays.setText(actual_remaining);
                        System.out.println(actual_remaining + "REMAIN");


                        System.out.println(diffenrence);
                    } catch (Exception e) {
                        binding.remaingDays.setText("Wrong");

                    }


                    if (value.getString("fname") != null) {
                        //Adding users to new DATABASE
                        DocumentReference documentReference2 = firebaseFirestore.collection(username).document(today_firebase);
                        Map<String, Object> userstoday = new HashMap<>();
                        userstoday.put("Admin", adminname);
                        documentReference2.set(userstoday).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });


                        //Adding today users users
                        DocumentReference documentReference1 = firebaseFirestore.collection(today_firebase).document(userID);
                        Map<String, Object> currentusers = new HashMap<>();
                        currentusers.put("name", username);
                        currentusers.put("userid", userID);
                        currentusers.put("time", time);
                        currentusers.put("admin", adminname);
                        documentReference1.set(currentusers).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                lastuser_id.setText(userID);


                                Downloaduserphoto();


                            }
                        });

                    } else {
                        Toast.makeText(Dashboard.this, "Wrong QR", Toast.LENGTH_LONG).show();
                        lastuser_id.setText("Wrong");


                    }
                }
            });
        } catch (Exception e) {

        }
    }
}




