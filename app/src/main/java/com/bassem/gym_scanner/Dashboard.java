package com.bassem.gym_scanner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bassem.gym_scanner.databinding.ActivityDashboardBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
    String yesterday;
    String time;
    FirebaseAuth firebaseAuth;
    private ActivityDashboardBinding binding;
    String adminname;
    TextView admin;
    String MY_PREFS_NAME = "gym";
    String searchinput="id";
    Double numberofdays;
    String date;
    int actual_remaining;
    Boolean active;
    String today_Date;


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
        int day_before = calendar.get(Calendar.DAY_OF_MONTH) - 1;
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        today_firebase = dayofmonth + "-" + month + "-" + year;
        yesterday = day_before + "-" + month + "-" + year;
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        admin = findViewById(R.id.admin);
        time = hour + ":" + min;

        SharedPreferences sharedPreferences = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        adminname = sharedPreferences.getString("log", null);
        if (adminname != null) {
            admin.setText(adminname);
        } else admin.setText("test");


        firebaseFirestore = FirebaseFirestore.getInstance();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinnerItems, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinner.setOnItemSelectedListener(this);


        gettodaycount();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        logout();
        return super.onOptionsItemSelected(item);
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
                    getusername();


                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    public void goToUsers(View view) {
        Intent intent = new Intent(Dashboard.this, com.bassem.gym_scanner.users_today.class);
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


                    today.setText("" + task.getResult().size());
                    Yesterday_count();

                }
            });

        } catch (Exception e) {
            today.setText("Today " + e.getMessage());
        }

    }

    void Yesterday_count() {
        try {

            firebaseFirestore.collection(yesterday).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    binding.yesterday.setText("" + task.getResult().size());
                    System.out.println(task.getResult().size());

                }
            });
        } catch (Exception e) {
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        gettodaycount();
    }

    public void logout() {


        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();

        Intent intent = new Intent(Dashboard.this, Login.class);
        startActivity(intent);
        finish();

    }


    public void goto_mangaAccounts(View view) {
        Intent intent = new Intent(Dashboard.this, Managment.class);
        startActivity(intent);
    }

    public void Scan(View view) {
        try {
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

                                userID = document.getId();
                                getusername();
                                binding.visitorCard.setVisibility(View.VISIBLE);


                            }
                        }

                    }
                });
            }
        } catch (Exception e) {
            Toast.makeText(Dashboard.this, e.getMessage(), Toast.LENGTH_LONG).show();
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

    void getusername() {
     //   binding.visitorCard.setVisibility(View.VISIBLE);
        calculat_remaing();
        System.out.println(userID);
        if (userID != null) {
            firebaseFirestore.collection(today_firebase).document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.getResult().exists()) {


                        Downloaduserphoto();
                        binding.remain.setText("This user has already been  scanned today");

                    }
                    else if (actual_remaining<0){
                        binding.remain.setTextColor(Color.RED);
                        binding.remain.setText("This user have to renew his subscription");
                        Downloaduserphoto();
                    }
                    else {
                        //Getting the username from the scanned ID

                        try {
                            DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
                            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                    lastuser_name.setText(value.getString("fname"));
                                    username = value.getString("fname") + " " + value.getString("lname");
                                    date = value.getString("date");
                                    active = value.getBoolean("activation");
                                    numberofdays = value.getDouble("daysnumber");
                                    if (active == false) {
                                        System.out.println(active);
                                        lastuser_name.setText("please activate the user first");
                                    } else {


                                        if (value.getString("fname") != null && active == true) {


                                            //Adding users to new DATABASE

                                            DocumentReference documentReference2 = firebaseFirestore.collection(value.getString("mail")).document(today_firebase);
                                            Map<String, Object> userstoday = new HashMap<>();
                                            userstoday.put("Admin", adminname);
                                            userstoday.put("time", time);
                                            userstoday.put("stamp", FieldValue.serverTimestamp());
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
                                            currentusers.put("stamp", FieldValue.serverTimestamp());
                                            documentReference1.set(currentusers).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    lastuser_id.setText(userID);


                                                    Downloaduserphoto();
                                                    gettodaycount();
                                                    calculat_remaing();
                                                    binding.enterdUserid.setText("");



                                                }
                                            });

                                        } else {
                                            Toast.makeText(Dashboard.this, "Wrong QR", Toast.LENGTH_LONG).show();
                                            lastuser_id.setText("please check the entered information");


                                        }
                                    }


                                }
                            });
                        } catch (Exception e) {

                        }

                    }
                }
            });
        } else {
            Toast.makeText(Dashboard.this, "Please enter ", Toast.LENGTH_LONG).show();
        }


    }


    void calculat_remaing() {

        DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                String firebase_date = value.getString("date");
                Double firebase_days = value.getDouble("daysnumber");
                String first_name = value.getString("fname");
                String last_name=value.getString("lname");
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                try {
                    Date date_Sub = simpleDateFormat.parse(firebase_date);
                    today_Date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                    Date today_date = simpleDateFormat.parse(today_Date);
                    long remaing = Math.abs(today_date.getTime() - date_Sub.getTime());
                    int diffenrence = (int) TimeUnit.DAYS.convert(remaing, TimeUnit.MILLISECONDS);
                    actual_remaining = (int) (firebase_days - diffenrence);
                    binding.remain.setText(Integer.toString(actual_remaining) + " days left");
                    binding.lastuserName.setText(first_name + ""+last_name);


                    System.out.println(actual_remaining);


                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }
        });


    }

    public void test(View view) {


    }

    public void user_info(View view) {
        Intent intent = new Intent(Dashboard.this, User_Info.class);
        intent.putExtra("user", userID);
        startActivity(intent);
    }

    public void GotoUsers(View view) {
        Intent intent = new Intent(Dashboard.this,All_users.class);
        startActivity(intent);
    }
}




