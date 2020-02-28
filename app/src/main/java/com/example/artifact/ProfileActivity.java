package com.example.artifact;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private TextView userName, userProfName, userStatus, userCountry, userGender, userDob;
    private ImageView userProfileImage;


    private DatabaseReference profileUserRef;
    private FirebaseAuth mAuth;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userName = (TextView) findViewById(R.id.user_name);
        userProfName = (TextView) findViewById(R.id.profile_name);
        userStatus = (TextView) findViewById(R.id.user_status);
        userCountry = (TextView) findViewById(R.id.user_country);
        userGender = (TextView) findViewById(R.id.user_gender);
        userDob = (TextView) findViewById(R.id.user_dob);
        userProfileImage = (CircleImageView) findViewById(R.id.profile_image);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        profileUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);


        profileUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    String username = dataSnapshot.child("username").getValue().toString();
                    String userFullName = dataSnapshot.child("fullname").getValue().toString();
                    String userDob = dataSnapshot.child("dob").getValue().toString();
                    String userCountry = dataSnapshot.child("country").getValue().toString();
                    String userGender = dataSnapshot.child("gender").getValue().toString();
                    String status = dataSnapshot.child("status").getValue().toString();

                    Picasso.get().load(myProfileImage).placeholder(R.drawable.profile).into(userProfileImage);

                    userName.setText(username);
                    userProfName.setText(userFullName);
                    ProfileActivity.this.userDob.setText(userDob);
                    userStatus.setText(status);
                    ProfileActivity.this.userCountry.setText("Country " + userCountry);
                    ProfileActivity.this.userGender.setText("Gender " + userGender);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
