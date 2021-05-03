package com.example.inductionapp2021;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

public class ViewFriendActivity extends AppCompatActivity {

    DatabaseReference mUserRef, requestRef, friendRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String profileImageURL, username, course;

    CircleImageView profileImage;
    TextView Username, Course;
    Button btnPerform, btnDecline;
    String CurrentState = "nothing_happened";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friend);

        final String userID = getIntent().getStringExtra("userKey");

        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
        requestRef = FirebaseDatabase.getInstance().getReference().child("Requests");
        friendRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        btnPerform = findViewById(R.id.btnPerform);
        btnDecline = findViewById(R.id.btnDecline);

        profileImage = findViewById(R.id.profileImage);
        Username = findViewById(R.id.username);
        Course = findViewById(R.id.course);


        LoadUser();

        btnPerform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PerformAction(userID);
            }
        });

    }

    private void LoadUser() {
        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    profileImageURL = snapshot.child("profileImage").getValue().toString();
                    username = snapshot.child("username").getValue().toString();
                    course = snapshot.child("course").getValue().toString();

                    Picasso.get().load(profileImageURL).into(profileImage);
                    Username.setText(username);
                    Course.setText(course);
                } else {
                    Toast.makeText(ViewFriendActivity.this, "Data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewFriendActivity.this, "" + error.getMessage().toString(), Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void PerformAction(String userID) {
        if (CurrentState.equals("nothing_happened")) {
            HashMap hashmap = new HashMap();
            hashmap.put("status", "pending");

            requestRef.child(mUser.getUid()).child(userID).updateChildren(hashmap).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(ViewFriendActivity.this, "Friend request sent", Toast.LENGTH_SHORT).show();
                    btnDecline.setVisibility(View.GONE);
                    CurrentState = "I_sent_pending";
                    btnPerform.setText("Cancel Request");
                } else {
                    Toast.makeText(ViewFriendActivity.this, "" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            });

        }

        if (CurrentState.equals("I_sent_pending") || CurrentState.equals("I_sent_decline")) {
            requestRef.child(mUser.getUid()).child(userID).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(ViewFriendActivity.this, "Request Cancelled", Toast.LENGTH_SHORT).show();
                    CurrentState = "nothing_happened";
                    btnPerform.setText("Send Request");
                    btnDecline.setVisibility(View.GONE);
                } else {
                    Toast.makeText(ViewFriendActivity.this, "" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (CurrentState.equals("they_sent_pending")) {
            requestRef.child(mUser.getUid()).child(userID).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    HashMap hashMap = new HashMap();
                    hashMap.put("status", "friend");
                    hashMap.put("username", username);
                    hashMap.put("profileImageUrl", profileImageURL);

                    friendRef.child(mUser.getUid()).child(userID).updateChildren(hashMap).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            friendRef.child(userID).child(mUser.getUid()).updateChildren(hashMap).addOnCompleteListener(task11 -> {
                                Toast.makeText(ViewFriendActivity.this, "Friend Request Accepted", Toast.LENGTH_SHORT).show();
                                CurrentState = "friends";
                                btnPerform.setText("Send Message");
                                btnDecline.setText("Remove Friend");
                                btnDecline.setVisibility(View.VISIBLE);

                            });
                        }

                    });

                }

            });
        }
        if (CurrentState.equals("friends")) {
            //
        }
    }

}


