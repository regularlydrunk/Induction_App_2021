package com.example.inductionapp2021;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewFriendActivity extends AppCompatActivity {

    DatabaseReference mUserRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String profileImageURL, username, course;

    CircleImageView profileImage;
    TextView Username, Course;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friend);

        String userID = getIntent().getStringExtra("userKey");

        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        profileImage = findViewById(R.id.profileImage);
        Username = findViewById(R.id.username);
        Course = findViewById(R.id.course);


        LoadUser();

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
                }
                else
                {
                    Toast.makeText(ViewFriendActivity.this, "Data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewFriendActivity.this, ""+error.getMessage().toString(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}