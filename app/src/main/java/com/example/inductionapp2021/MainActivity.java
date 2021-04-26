package com.example.inductionapp2021;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inductionapp2021.Utils.Comment;
import com.example.inductionapp2021.Utils.Posts;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_CODE = 101;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mUserRef, postRef,likeRef, commentRef;
    String profileImageUrlV, usernameV;
    CircleImageView profileImageView;
    TextView usernameHeader;
    ImageView addImagePost, sendPostImage;
    EditText inputStatusDescription;
    Uri imageUri;
    ProgressDialog mLoadingBar;
    StorageReference postImageRef;

    FirebaseRecyclerAdapter<Posts,MyViewHolder>adapter;
    FirebaseRecyclerOptions<Posts> options;
    RecyclerView recyclerView;
    FirebaseRecyclerOptions<Comment>commentOption;
    FirebaseRecyclerAdapter<Comment, CommentViewHolder> commentAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Make Me University Ready");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        addImagePost = findViewById(R.id.addImagePost);
        sendPostImage = findViewById(R.id.sendPostImage);
        inputStatusDescription = findViewById(R.id.inputAddPost);
        mLoadingBar = new ProgressDialog(this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navView);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        likeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        commentRef = FirebaseDatabase.getInstance().getReference().child("Comments");

        postImageRef = FirebaseStorage.getInstance().getReference().child("PostImages");

        View view = navigationView.inflateHeaderView(R.layout.drawer_header);

        profileImageView = view.findViewById(R.id.profile_image_header);
        usernameHeader = view.findViewById(R.id.username_header);
        navigationView.setNavigationItemSelectedListener(this);

        //POST STATUS BUTTON
        sendPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddPost();
            }
        });



        //LOAD IMAGE FROM LIBRARY WHEN UPLOADING
        addImagePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE);

            }
        });

        LoadPosts();

    }

    private void LoadPosts() {
        options = new FirebaseRecyclerOptions.Builder<Posts>().setQuery(postRef, Posts.class).build();
        adapter = new FirebaseRecyclerAdapter<Posts, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Posts model) {
                String postKey = getRef(position).getKey();
                holder.postDesc.setText(model.getStatusDescription());
                String timeAgo = calculateTimeAgo(model.getDate());
                holder.timeAgo.setText(timeAgo);
                holder.username.setText(model.getUsername());
                Picasso.get().load(model.getPostImageURL()).into(holder.postImage);
                Picasso.get().load(model.getUserProfileImage()).into(holder.profileImagePost);
                holder.countLikes(postKey, mUser.getUid(), likeRef);

                holder.imgLike.setOnClickListener(view -> likeRef.child(postKey).child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            likeRef.child(postKey).child(mUser.getUid()).removeValue();
                            holder.imgLike.setColorFilter(Color.parseColor("#636869"), PorterDuff.Mode.MULTIPLY); //Changes colour of like image
                            notifyDataSetChanged();
                        }
                        else
                        {
                            likeRef.child(postKey).child(mUser.getUid()).setValue("Like");
                            holder.imgLike.setColorFilter(Color.parseColor("#e3a919"), PorterDuff.Mode.MULTIPLY); //Changes colour of like image
                            notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Toast.makeText(MainActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }));

                holder.sendComment.setOnClickListener(view -> {
                    String comment = holder.inputComments.getText().toString();
                    if (comment.isEmpty()){
                        Toast.makeText(MainActivity.this, "You can't comment nothing!", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        AddComment(holder, postKey, commentRef, mUser.getUid(), comment);

                    }
                });
                
                LoadComments(postKey);

            }


            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_post, parent, false);
                return new MyViewHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    private void LoadComments(String postKey) {
        MyViewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        commentOption = new FirebaseRecyclerOptions.Builder<Comment>().setQuery(commentRef.child(postKey), Comment.class).build();
        commentAdapter = new FirebaseRecyclerAdapter<Comment, CommentViewHolder>(commentOption) {
            @Override
            protected void onBindViewHolder(@NonNull CommentViewHolder holder, int position, @NonNull Comment model) {
                Picasso.get().load(model.getProfileImageUrl()).into(holder.profileImage);
                holder.username.setText(model.getUsername());
                holder.comment.setText(model.getComment());
            }

            @NonNull
            @Override
            public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_comment, parent, false);
                return new CommentViewHolder(view);
            }
        };
        commentAdapter.startListening();
        MyViewHolder.recyclerView.setAdapter(commentAdapter);

    }

    private void AddComment(MyViewHolder holder, String postKey, DatabaseReference commentRef, String uid, String comment) {
        HashMap hashMap = new HashMap();
        hashMap.put("username",usernameV);
        hashMap.put("profileImageUrl", profileImageUrlV);
        hashMap.put("comment", comment);

        commentRef.child(postKey).child(uid).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful())
                {
                    Toast.makeText(MainActivity.this, "Comment Added", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                    holder.inputComments.setText(null);
                }
                else
                {
                    Toast.makeText(MainActivity.this, ""+task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    //Calculates how long ago a post was made
    private String calculateTimeAgo(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            long time = sdf.parse(date).getTime();
            long now = System.currentTimeMillis();
            CharSequence ago =
                    DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);
            return ago + "";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            addImagePost.setImageURI(imageUri); //passes the image URI to the image view
        }
    }

    //Add post conditions. Checks to see if anything is empty and give appropriate response.
    private void AddPost() {
        String postDesc = inputStatusDescription.getText().toString();
        if (postDesc.isEmpty() && imageUri == null) {

            inputStatusDescription.setError("You can't post nothing!");
            Toast.makeText(this, "You can't post nothing!", Toast.LENGTH_SHORT).show();
        } else if (imageUri == null) {
            inputStatusDescription.setError("Please select an image!");
            Toast.makeText(this, "Please select an image.", Toast.LENGTH_SHORT).show();
        } else if (postDesc.isEmpty()) {
            inputStatusDescription.setError("Please add a caption to your status!");
        } else {
            mLoadingBar.setTitle("Adding Post");
            mLoadingBar.setCanceledOnTouchOutside(false);
            mLoadingBar.show();



            //Date Formatter
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
            final String strDate = formatter.format(date);

            //Uploading Post To Database
            postImageRef.child(mUser.getUid()+ strDate).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        postImageRef.child(mUser.getUid()+ strDate).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {


                                HashMap hashMap = new HashMap();
                                hashMap.put("date", strDate);
                                hashMap.put("postImageURL", uri.toString());
                                hashMap.put("statusDescription", postDesc);
                                hashMap.put("userProfileImage", profileImageUrlV);
                                hashMap.put("username", usernameV);


                                postRef.child(mUser.getUid()+ strDate).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()) {
                                            mLoadingBar.dismiss();
                                            Toast.makeText(MainActivity.this, "Post Added", Toast.LENGTH_SHORT).show();
                                            addImagePost.setImageResource(R.drawable.ic_add_post_image);
                                            inputStatusDescription.setText("");
                                        } else {
                                            mLoadingBar.dismiss();
                                            Toast.makeText(MainActivity.this, "" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        });
                    } else {
                        mLoadingBar.dismiss();
                        Toast.makeText(MainActivity.this, "" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mUser == null) {
            SendUserToLoginScreen(); //If not logged in send to Login screen
        } else {
            mUserRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        profileImageUrlV = snapshot.child("profileImage").getValue().toString();
                        usernameV = snapshot.child("username").getValue().toString();

                        Picasso.get().load(profileImageUrlV).into(profileImageView);
                        usernameHeader.setText(usernameV);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, "Data not found", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void SendUserToLoginScreen() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
                break;

            case R.id.profile:
                Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
                break;

            case R.id.friends:
                Toast.makeText(this, "Friends", Toast.LENGTH_SHORT).show();
                break;

            case R.id.findFriend:
                Toast.makeText(this, "Find Friends", Toast.LENGTH_SHORT).show();
                break;

            case R.id.chat:
                Toast.makeText(this, "Chat", Toast.LENGTH_SHORT).show();
                break;

            case R.id.logout:
                Toast.makeText(this, "Log Out", Toast.LENGTH_SHORT).show();
                break;
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return true;
    }
}

