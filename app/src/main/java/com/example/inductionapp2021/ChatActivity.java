package com.example.inductionapp2021;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inductionapp2021.Utils.Chat;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    EditText inputChat;
    ImageView btnSendChat, btnSendImageChat;
    CircleImageView userProfileAppBar;
    TextView usernameAppBar, statusAppBar;
    String otherUserID;
    DatabaseReference mUserRef, messageRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String otherUsername, otherUserProfileLink, otherUserStatus;
    FirebaseRecyclerOptions<Chat> options;
    FirebaseRecyclerAdapter<Chat, ChatMyViewHolder> adapter;
    String myProfileImageLink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        otherUserID = getIntent().getStringExtra("OtherUserID");

        recyclerView = findViewById(R.id.recyclerView);
        inputChat = findViewById(R.id.inputChat);
        btnSendChat = findViewById(R.id.btnSendChat);
        btnSendImageChat = findViewById(R.id.btnSendImageChat);

        userProfileAppBar = findViewById(R.id.userProfileAppBar);
        usernameAppBar = findViewById(R.id.usernameAppBar);
        statusAppBar = findViewById(R.id.statusAppBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        messageRef = FirebaseDatabase.getInstance().getReference().child("Messages");

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        LoadOtherUsers();
        LoadMyProfile();

        btnSendChat.setOnClickListener(view -> SendChatMessage());

        LoadMessage();


        mUserRef.child(otherUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    otherUsername = snapshot.child("username").getValue().toString();
                    otherUserProfileLink = snapshot.child("profileImage").getValue().toString();
                    otherUserStatus = snapshot.child("status").getValue().toString();
                    Picasso.get().load(otherUserProfileLink).into(userProfileAppBar);
                    usernameAppBar.setText(otherUsername);
                    statusAppBar.setText(otherUserStatus);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void LoadMyProfile() {
        mUserRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    myProfileImageLink = snapshot.child("profileImage").getValue().toString();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void LoadMessage() {
        options = new FirebaseRecyclerOptions.Builder<Chat>().setQuery(messageRef.child(mUser.getUid()).child(otherUserID), Chat.class).build();
        adapter = new FirebaseRecyclerAdapter<Chat, ChatMyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ChatMyViewHolder holder, int position, @NonNull Chat model) {
                if (model.getUserID().equals(mUser.getUid()))
                {
                    holder.firstUserText.setVisibility(View.GONE);
                    holder.firstUserProfile.setVisibility(View.GONE);
                    holder.secondUserText.setVisibility(View.VISIBLE);
                    holder.secondUserProfile.setVisibility(View.VISIBLE);

                    holder.secondUserText.setText(model.getMessage());
                    Picasso.get().load(myProfileImageLink).into(holder.secondUserProfile);



                }

                else
                {
                    holder.firstUserText.setVisibility(View.VISIBLE);
                    holder.firstUserProfile.setVisibility(View.VISIBLE);
                    holder.secondUserText.setVisibility(View.GONE);
                    holder.secondUserProfile.setVisibility(View.GONE);

                    holder.firstUserText.setText(model.getMessage());
                    Picasso.get().load(otherUserProfileLink).into(holder.firstUserProfile);
                }
            }

            @NonNull
            @Override
            public ChatMyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_message, parent, false);
                return new ChatMyViewHolder(view);
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }

    private void SendChatMessage() {
        String message = inputChat.getText().toString();
        if (message.isEmpty()) {
            Toast.makeText(this, "Please write something", Toast.LENGTH_SHORT).show();
        } else {
            HashMap hashmap = new HashMap();
            hashmap.put("message", message);
            hashmap.put("status", "unseen");
            hashmap.put("userID", mUser.getUid());

            messageRef.child(otherUserID).child(mUser.getUid()).push().updateChildren(hashmap).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    messageRef.child(mUser.getUid()).child(otherUserID).push().updateChildren(hashmap).addOnCompleteListener(task1 -> {
                        if (task.isSuccessful()) {
                            inputChat.setText(null);
                            Toast.makeText(this, "Message Sent", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

        }
    }

    private void LoadOtherUsers() {
    }
}