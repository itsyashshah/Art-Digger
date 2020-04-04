package com.example.artifact;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsActivity extends AppCompatActivity {

    private RecyclerView myFriendList;
    private DatabaseReference FriendsRef, UsersRef;
    private FirebaseAuth mAuth;
    private String online_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        mAuth = FirebaseAuth.getInstance();
        online_user_id=mAuth.getCurrentUser().getUid();
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(online_user_id);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        myFriendList = (RecyclerView) findViewById(R.id.friend_list);
        myFriendList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myFriendList.setLayoutManager(linearLayoutManager);

        DisplayAllFriends();

    }


    private void DisplayAllFriends() {

        Query query = FriendsRef.orderByChild("date"); // haven't implemented a proper list sort yet.

        FirebaseRecyclerOptions<Friends> options = new FirebaseRecyclerOptions.Builder<Friends>().setQuery(query, Friends.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FriendsViewHolder friendsViewHolder, int position, @NonNull Friends friends) {

                friendsViewHolder.setDate(friends.getDate());
                final String usersIDs = getRef(position).getKey();

                UsersRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final String username = dataSnapshot.child("fullname").getValue().toString();
                            final String profileimage = dataSnapshot.child("profileimage").getValue().toString();

                            friendsViewHolder.setFullName(username);
                            friendsViewHolder.setProfileImage(getApplicationContext(), profileimage);

                            friendsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    CharSequence options[] = new CharSequence[]
                                            {
                                                    username + "'s Profile",
                                                    "Send Message"
                                            };
                                    AlertDialog.Builder builder= new  AlertDialog.Builder(FriendsActivity.this);
                                    builder.setTitle("Select Option");

                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (i == 0)
                                            {
                                                Intent profileintent = new Intent(FriendsActivity.this, PersonProfileActivity.class);
                                                profileintent.putExtra("visit_user_id", usersIDs);
                                                startActivity(profileintent);
                                            }
                                            if (i == 1)
                                            {
                                                Intent Chatintent = new Intent(FriendsActivity.this, ChatActivity.class);
                                                Chatintent.putExtra("visit_user_id", usersIDs);
                                                Chatintent.putExtra("username", username);
                                                startActivity(Chatintent);

                                            }
                                        }
                                    });
                                    builder.show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            public FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_users_display_layout, parent ,false);
                return new FriendsViewHolder(view);
            }
        };
        adapter.startListening();
        myFriendList.setAdapter(adapter);
    }
    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setProfileImage(Context applicationContext, String profileimage) {
            CircleImageView image = (CircleImageView) mView.findViewById(R.id.all_users_profile_image);
            Picasso.get().load(profileimage).into(image);
        }

        public void setFullName(String fullName){
            TextView myName = (TextView) mView.findViewById(R.id.all_users_profile_fullname);
            myName.setText(fullName);
        }

        public void setDate(String date){
            TextView friendsDate = (TextView) mView.findViewById(R.id.all_users_profile_user_status);
            friendsDate.setText("Friends since: " + date);
        }
    }

}
