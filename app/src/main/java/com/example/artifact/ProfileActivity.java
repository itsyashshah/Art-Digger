package com.example.artifact;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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


    private DatabaseReference profileUserRef, FriendsRef, PostRef;
    private FirebaseAuth mAuth;
    private Button MyPosts, Myfriends;

    private String userId;
    private int countfriends=0;

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
        Myfriends = (Button) findViewById(R.id.my_friends_button);
        MyPosts = (Button) findViewById(R.id.my_post_button);


        Myfriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SendUserToConnectionActivity();

            }
        });

        MyPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToMyPostActivity();

            }
        });

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        profileUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");

        FriendsRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    countfriends = (int) dataSnapshot.getChildrenCount();
                    Myfriends.setText(Integer.toString(countfriends) + "  Friends");

                }
                else
                {
                    Myfriends.setText("0 Friends");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            SendUserToMainActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void SendUserToMainActivity() {

        Intent mainIntent = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(mainIntent);
    }

    private void SendUserToConnectionActivity() {

        Intent profileActivityIntent = new Intent(ProfileActivity.this, FriendsActivity.class);
        startActivity(profileActivityIntent);
    }

    private void SendUserToMyPostActivity() {

        Intent profileActivityIntent = new Intent(ProfileActivity.this, MyPostsActivity.class);
        startActivity(profileActivityIntent);
    }


}
