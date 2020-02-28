package com.example.artifact;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.w3c.dom.Text;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private EditText userName, userprofname, userstatus, usercountry, usergender, userdob;
    private Button UpdateAccountSettingsButton;
    private ProgressDialog loadingbar;

    private CircleImageView userProfImage;

    private DatabaseReference SettingsuserRef;
    private FirebaseAuth mAuth;
    private StorageReference UserProfileImageRef;
    private String currentUserID;
    final static int Gallery_Pick = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();

        mToolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currentUserID = mAuth.getCurrentUser().getUid();
        SettingsuserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

        userName = (EditText) findViewById(R.id.settings_username);
        userprofname = (EditText) findViewById(R.id.settings_fullname);
        userstatus = (EditText) findViewById(R.id.settings_status);
        usercountry = (EditText) findViewById(R.id.settings_country);
        usergender = (EditText) findViewById(R.id.settings_gender);
        userdob = (EditText) findViewById(R.id.settings_dob);
        loadingbar = new ProgressDialog(this);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        userProfImage = (CircleImageView) findViewById(R.id.settings_profile_image);

        UpdateAccountSettingsButton = (Button) findViewById(R.id.settings_update_button);


        SettingsuserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    String username = dataSnapshot.child("username").getValue().toString();
                    String userfullname = dataSnapshot.child("fullname").getValue().toString();
                    String userDob = dataSnapshot.child("dob").getValue().toString();
                    String userCountry = dataSnapshot.child("country").getValue().toString();
                    String userGender = dataSnapshot.child("gender").getValue().toString();
                    String status = dataSnapshot.child("status").getValue().toString();

                    Picasso.get().load(myProfileImage).placeholder(R.drawable.profile).into(userProfImage);
                    userName.setText(username);
                    userprofname.setText(userfullname);
                    userdob.setText(userDob);
                    userstatus.setText(status);
                    usercountry.setText(userCountry);
                    usergender.setText(userGender);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        UpdateAccountSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ValidateAccountInfo();

            }
        });

        userProfImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null) {
            Uri ImageUri = data.getData();

            CropImage.activity(ImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                loadingbar.setTitle("Profile Image");
                loadingbar.setMessage("Please wait, while we updating your profile image");
                loadingbar.setCanceledOnTouchOutside(true);
                loadingbar.show();

                Uri resultUri = result.getUri();

                final StorageReference filePath = UserProfileImageRef.child(currentUserID + ".jpg");

                filePath.putFile(resultUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downUri = task.getResult();
                            final String downloadUrl = downUri.toString();
                            SettingsuserRef.child("profileimage").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Intent selfIntent = new Intent(SettingsActivity.this, SettingsActivity.class);
                                                startActivity(selfIntent);
                                                loadingbar.dismiss();
                                            } else {
                                                String message = task.getException().getMessage();
                                                Toast.makeText(SettingsActivity.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
                                                loadingbar.dismiss();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(SettingsActivity.this, "Error Occured: Image can not be cropped. Try Again.", Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();
                        }
                    }
                });
            }
        }
    }




    private void ValidateAccountInfo() {

        String username = userName.getText().toString();
        String profilename = userprofname.getText().toString();
        String dob = userdob.getText().toString();
        String country = usercountry.getText().toString();
        String gender = usergender.getText().toString();
        String status = userstatus.getText().toString();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Please write your Username.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(profilename)) {
            Toast.makeText(this, "Please write your Fullname.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(dob)) {
            Toast.makeText(this, "Please write your Date of birth.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(country)) {
            Toast.makeText(this, "Please write your Country.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(gender)) {
            Toast.makeText(this, "Please write your Gender.", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(status))
        {
            Toast.makeText(this,"Please write your Status.",Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingbar.setTitle("Profile Image");
            loadingbar.setMessage("Please wait, while we updating your profile image");
            loadingbar.setCanceledOnTouchOutside(true);
            loadingbar.show();

            UpdateAccountInfo(username, profilename, dob, country, gender, status);
        }

    }

    private void UpdateAccountInfo(String username, String profilename, String dob, String country, String gender, String status) {

        HashMap userMap = new HashMap();
        userMap.put("username", username);
        userMap.put("fullname", profilename);
        userMap.put("status", status);
        userMap.put("country", country);
        userMap.put("dob", dob);
        userMap.put("gender", gender);
        SettingsuserRef.updateChildren(userMap)
        .addOnCompleteListener(new OnCompleteListener() {
    @Override
    public void onComplete(@NonNull Task task) {

        if (task.isSuccessful())
        {
            loadingbar.dismiss();
            SendUserToMainActivity();
            Toast.makeText(SettingsActivity.this,"Account Settings are been Updated Successfully..",Toast.LENGTH_SHORT).show();

        }
        else
        {
            loadingbar.dismiss();
            Toast.makeText(SettingsActivity.this,"Error Occured",Toast.LENGTH_SHORT).show();

        }
    }
});

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            SendUserToMainActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void SendUserToMainActivity() {

        Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
        mainIntent.addFlags((Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        startActivity(mainIntent);
        finish();
    }

}



