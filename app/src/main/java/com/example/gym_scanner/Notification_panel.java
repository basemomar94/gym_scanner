package com.example.gym_scanner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.PrecomputedTextCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gym_scanner.databinding.ActivityNotificationPanelBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Notification_panel extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;


    private static final String ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm";
    FirebaseFirestore firebaseFirestore;
    EditText announcement;
    TextView current_ann;
    String message;
    static String notification_id;
    ActivityNotificationPanelBinding binding;
    Uri imageView;
    StorageReference storageReference;
    String token;
    boolean picked_image = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityNotificationPanelBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("image");
        announcement = findViewById(R.id.announcemt);
        current_ann = findViewById(R.id.current_message);
        getcurrent_message();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Notification Panel");
        actionBar.setDisplayHomeAsUpEnabled(true);
        custom_noti();

    }

    public void send(View view) {
        sendmessage();


    }

    void sendmessage() {


        DocumentReference documentReference = firebaseFirestore.collection("message").document("message");
        Map<String, Object> message = new HashMap<>();
        message.put("message", announcement.getText().toString().trim());

        documentReference.set(message).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        });
        announcement.setText("");

    }

    void getcurrent_message() {
        DocumentReference documentReference = firebaseFirestore.collection("message").document("message");
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                current_ann.setText(value.getString("message"));

            }
        });
    }

    public void Delete(View view) {
        DocumentReference documentReference = firebaseFirestore.collection("message").document("message");


        documentReference.update("message", FieldValue.delete());


    }

    public void send_noti(View view) {

        send_notifications();
        if (picked_image==true){
            uploadphoto();
            picked_image=false;
        }



    }

    //sending notification

    void send_notifications() {
        if (picked_image==false){
            getRandomString(25);
            System.out.println(notification_id);
        }

        DocumentReference documentReference = firebaseFirestore.collection("notifications").document(notification_id);
        Map<String, Object> notification = new HashMap<>();
        notification.put("title", binding.titleNoti.getText().toString().trim());
        notification.put("body", binding.bodyNoti.getText().toString().trim());
        notification.put("stamp", FieldValue.serverTimestamp());
        documentReference.set(notification).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
               Push_noti();

                Toast.makeText(Notification_panel.this, "notification is sent", Toast.LENGTH_LONG).show();
                binding.bodyNoti.setText("");
                binding.titleNoti.setText("");



            }
        });


    }

    private static String getRandomString(final int sizeOfRandomString) {
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(sizeOfRandomString);
        for (int i = 0; i < sizeOfRandomString; ++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        notification_id = sb.toString();

        return notification_id;
    }

    public void add_photo(View view) {
        getRandomString(25);
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
        picked_image=true;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageView = data.getData();
            binding.photo.setImageURI(imageView);
            binding.photo.setVisibility(View.VISIBLE);
            binding.addPhoto.setVisibility(View.GONE);


        }
    }

    void uploadphoto() {
        if (imageView != null) {

            StorageReference notification = storageReference.child("notification/" + notification_id);
            notification.putFile(imageView).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(Notification_panel.this, "image is uploaded", Toast.LENGTH_LONG);
                    binding.photo.setVisibility(View.GONE);
                    binding.addPhoto.setVisibility(View.VISIBLE);


                }
            });


        }

    }

    public void test(View view) {
        System.out.println(picked_image);
        if (picked_image==false){
            getRandomString(25);
            System.out.println(notification_id +"check");
        }


    }
    void Push_noti(){
        custom_noti();
        String serverToken = "key=AAAA56SLr6U:APA91bF--Ffapsdf36Osx9Km__w1lh59HfAUWI6ENJ_ESJtXkc-0rl1UtNCfUplISrYM3e4vVClr7NhLM8l3IJVzNkBxULAgkPvtcZRPzFbYrPOoi5CARjw9UxwvRTQTVaC0SPJtOJ5t";
        String userToken = token;
        String title = binding.titleNoti.getText().toString();
        String msgBody = binding.bodyNoti.getText().toString();
        JSONObject object = new JSONObject();
        try {
            object.put("to", userToken);
            JSONObject notification = new JSONObject();
            notification.put("title", title);
            notification.put("body", msgBody);
            object.put("notification", notification);
            Log.v("NotificationCallBack", "my object to string"+object.toString(1));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, object.toString());
        Request request = new Request.Builder()
                .url("https://fcm.googleapis.com/fcm/send")
                .method("POST", body)
                .addHeader("Authorization", serverToken)
                .addHeader("Content-Type", "application/json")
                .build();

        Runnable runnable = () -> {
            try {
                Response response = client.newCall(request).execute();
                System.out.println(response.body());
            } catch (Exception e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> {

            });
        };
        Thread thread = new Thread(runnable);
        thread.start();

    }
    void custom_noti(){

        token=getIntent().getStringExtra("token");
        System.out.println(token+"token");
        String user=getIntent().getStringExtra("user");
        if (token!=null){

            binding.customLayout.setVisibility(View.VISIBLE);
            binding.customNoti.setText(""+user);
        } else {
            token="/topics/Gym";
        }

    }
}