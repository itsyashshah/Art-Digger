package com.example.artifact;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private Toolbar Chattoolbar;
    private ImageButton SendMessageButton, SendImagefileButton;
    private TextInputEditText userMessageInput;
    private TextInputLayout userMessageInputLayout;
    private String messageReceiverID, messageReceiverName, messageSenderID, saveCurrentDate, saveCurrentTime;
    private DatabaseReference RootRef;
    private RecyclerView userMessagesList;

    private TextView receivername;
    private CircleImageView receiverProfileImage;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        messageSenderID = mAuth.getCurrentUser().getUid();


        RootRef = FirebaseDatabase.getInstance().getReference();

        messageReceiverID = getIntent().getExtras().get("visit_user_id").toString();
        messageReceiverName = getIntent().getExtras().get("username").toString();


        IntializeFields();

        DisplayReceiverInfo();

        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendMessage();
            }
        });
    }

    private void SendMessage() {
        String MessageText = userMessageInput.getText().toString();

        if(TextUtils.isEmpty(MessageText))
        {
            userMessageInput.setError("Required");

        }
        else
        {
            String message_sender_ref = "Messages/" + messageSenderID +"/" + messageReceiverID;
            String message_receiver_ref = "Messages/" + messageReceiverID +"/" + messageSenderID;

            DatabaseReference user_message_key = RootRef.child("Messages").child(messageSenderID)
                    .child(messageReceiverID).push();

            String message_push_id = user_message_key.getKey();

            Calendar calFordDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
            saveCurrentDate = currentDate.format(calFordDate.getTime());

            Calendar calFordTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm aa");
            saveCurrentTime = currentTime.format(calFordTime.getTime());

            Map messageTextBody = new HashMap();
                messageTextBody.put("message", MessageText);
                messageTextBody.put("time", saveCurrentTime);
                messageTextBody.put("date", saveCurrentDate);
                messageTextBody.put("type", "text");
                messageTextBody.put("from", messageSenderID);

            Map messageBodyDetails = new HashMap();
                messageBodyDetails.put(message_sender_ref + "/" + message_push_id , messageTextBody);
                messageBodyDetails.put(message_receiver_ref + "/" + message_push_id, messageTextBody);

                RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful())
                        {

                        }
                        else
                        {
                            String message = task.getException().getMessage();
                            Log.d("Error:", message);
                        }
                    }
                });



        }
    }

    private void DisplayReceiverInfo() {

        receivername.setText(messageReceiverName);

        RootRef.child("Users").child(messageReceiverID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    final String profileImage = dataSnapshot.child("profileimage").getValue().toString();
                    Picasso.get().load(profileImage).placeholder(R.drawable.profile).into(receiverProfileImage);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void IntializeFields() {

        Chattoolbar = (Toolbar) findViewById(R.id.chat_bar_layout);
        setSupportActionBar(Chattoolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View action_bar_view = layoutInflater.inflate(R.layout.chat_custom_bar, null);
        actionBar.setCustomView(action_bar_view);

        receivername = (TextView) findViewById(R.id.custom_profile_name);
        receiverProfileImage = (CircleImageView) findViewById(R.id.custom_profile_image);


        SendMessageButton = (ImageButton) findViewById(R.id.send_message_button);
        SendImagefileButton = (ImageButton) findViewById(R.id.send_image_button);
        userMessageInput = (TextInputEditText) findViewById(R.id.input_message_edittext);
        userMessageInputLayout = (TextInputLayout) findViewById(R.id.input_message);

    }
}
