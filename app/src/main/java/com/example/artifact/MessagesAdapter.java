package com.example.artifact;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {
    private List<Messages> userMessagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersDatabaseRef;

    public MessagesAdapter (List<Messages> userMessagesList )
    {
        this.userMessagesList = userMessagesList;
    }

    public class MessageViewHolder  extends RecyclerView.ViewHolder{
        public TextView SenderMessageText, RecieverMessageText;
        public CircleImageView recieverProfileImage;
        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            SenderMessageText = (TextView) itemView.findViewById(R.id.sender_message_text);
            RecieverMessageText = (TextView) itemView.findViewById(R.id.receiver_message_text);
            recieverProfileImage = (CircleImageView) itemView.findViewById(R.id.message_profile_image);


        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View V = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_layout_of_user, parent, false );

        mAuth = FirebaseAuth.getInstance();

        return new MessageViewHolder(V);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        String messageSenderId = mAuth.getCurrentUser().getUid();
        Messages messages = userMessagesList.get(position);

        String fromUserId = messages.getFrom();
        String fromMessageType = messages.getType();

        UsersDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserId);
        UsersDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists())
                {
                    String image = dataSnapshot.child("profileimage").getValue().toString();

                    Picasso.get().load(image).placeholder(R.drawable.profile).into(holder.recieverProfileImage);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (fromMessageType.equals("text"))
        {
            holder.RecieverMessageText.setVisibility(View.INVISIBLE);
            holder.recieverProfileImage.setVisibility(View.INVISIBLE);

            if (fromUserId.equals(messageSenderId))
            {
                holder.SenderMessageText.setBackgroundResource(R.drawable.sender_message_text_background);
                holder.SenderMessageText.setTextColor(Color.WHITE);
                holder.SenderMessageText.setGravity(Gravity.LEFT);
                holder.SenderMessageText.setText(messages.getMessage());
            }
            else
            {
                holder.SenderMessageText.setVisibility(View.INVISIBLE);

                holder.RecieverMessageText.setVisibility(View.VISIBLE);
                holder.recieverProfileImage.setVisibility(View.VISIBLE);

                holder.RecieverMessageText.setBackgroundResource(R.drawable.receiver_message_text_background);
                holder.RecieverMessageText.setTextColor(Color.WHITE);
                holder.RecieverMessageText.setGravity(Gravity.LEFT);
                holder.RecieverMessageText.setText(messages.getMessage());


            }
        }
    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }
}
