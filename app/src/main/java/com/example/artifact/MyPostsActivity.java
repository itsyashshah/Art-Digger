package com.example.artifact;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyPostsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView myPostList;
    private FirebaseAuth mAuth;
    private DatabaseReference PostsRef;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        mToolbar = (Toolbar) findViewById(R.id.my_posts_bar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Posts");

        myPostList = (RecyclerView) findViewById(R.id.my_all_posts_list);
        myPostList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myPostList.setLayoutManager(linearLayoutManager);

        DisplayMyAllPosts();

    }

    private  void DisplayMyAllPosts()
    {
        Query myPostQuery = PostsRef.orderByChild("uid")
                .startAt(currentUserId).endAt(currentUserId + "\uf8ff");
        FirebaseRecyclerOptions<Posts> options=new FirebaseRecyclerOptions.Builder<Posts>()
                .setQuery(myPostQuery, Posts.class)
                .build();
        FirebaseRecyclerAdapter<Posts, MyPostsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Posts, MyPostsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyPostsViewHolder myPostsViewHolder, int i, @NonNull Posts model) {
                myPostsViewHolder.setFullname(model.getFullname());
                myPostsViewHolder.setTime(model.getTime());
                myPostsViewHolder.setDate(model.getDate());
                myPostsViewHolder.setDescription(model.getDescription());
                myPostsViewHolder.setProfileImage(getApplicationContext(), model.getProfileimage());
                myPostsViewHolder.setPostimage(getApplicationContext(), model.getPostimage());

            }

            @NonNull
            @Override
            public MyPostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.all_post_layout,parent,false);
                return new MyPostsActivity.MyPostsViewHolder(view);
            }
        };
        myPostList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class MyPostsViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public MyPostsViewHolder (View itemView)
        {
            super(itemView);
            mView = itemView;
        }
//        TextView username, date, time, description;
//        CircleImageView user_post_image;
//        ImageView postImage;
//        ImageButton LikepostButton;
//        TextView DisplayNoofLikes;
//        private Object TextView;

//        public MyPostsViewHolder(View itemView) {
//                super(itemView);
//                mView = itemView;
//
////            username = itemView.findViewById(R.id.post_user_name);
////            date = itemView.findViewById(R.id.post_date);
////            time = itemView.findViewById(R.id.post_time);
////            description = itemView.findViewById(R.id.post_description);
////            postImage = itemView.findViewById(R.id.post_image);
////            user_post_image = itemView.findViewById(R.id.post_profile_image);
//
////            LikepostButton = (ImageButton) mView.findViewById(R.id.like_image_button);
////            DisplayNoofLikes = (TextView) mView.findViewById(R.id.display_likes);
//        }


        public void setFullname(String fullname) {
            TextView username = (TextView)mView.findViewById(R.id.post_user_name);
            username.setText(fullname);
        }

        public void setProfileImage(Context applicationContext, String profileimage) {
            CircleImageView image = (CircleImageView) mView.findViewById(R.id.all_users_profile_image);
            Picasso.get().load(profileimage).into(image);
        }

        public void setTime(String time) {
            TextView PostTime = (TextView) mView.findViewById(R.id.post_time);
            PostTime.setText("    " + time);
        }

        public void setDate(String date) {
            TextView friendsDate = (TextView) mView.findViewById(R.id.post_date);
            friendsDate.setText("   " + date);
        }

        public void setDescription(String description) {
            TextView PostDescription = (TextView) mView.findViewById(R.id.post_description);
            PostDescription.setText(description);
        }

        public void setPostimage(Context applicationContext, String postimage) {
            ImageView PostImage = (ImageView) mView.findViewById(R.id.post_image);
            Picasso.get().load(postimage).into(PostImage);

        }
    }

}
