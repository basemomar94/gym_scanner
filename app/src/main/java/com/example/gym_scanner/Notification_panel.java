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

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
        uploadphoto();


    }

    void send_notifications() {

        DocumentReference documentReference = firebaseFirestore.collection("notifications").document(notification_id);
        Map<String, Object> notification = new HashMap<>();
        notification.put("title", binding.titleNoti.getText().toString().trim());
        notification.put("body", binding.bodyNoti.getText().toString().trim());
        notification.put("stamp", FieldValue.serverTimestamp());
        documentReference.set(notification).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

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
        getRandomString(80);
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);

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
}