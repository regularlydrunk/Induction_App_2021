package com.example.inductionapp2021;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyViewHolder extends RecyclerView.ViewHolder {
    CircleImageView profileImagePost;
    ImageView postImage, imgLike, imgComment, sendComment;
    TextView username, timeAgo, postDesc, likeCount, commentCount;
    EditText inputComments;


    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        profileImagePost = itemView.findViewById(R.id.profileImagePost);
        postImage = itemView.findViewById(R.id.postImage);
        username = itemView.findViewById(R.id.profileUsernamePost);
        timeAgo = itemView.findViewById(R.id.timeAgo);
        postDesc = itemView.findViewById(R.id.postDesc);
        imgComment = itemView.findViewById(R.id.imgComment);
        imgLike = itemView.findViewById(R.id.imgLike);
        likeCount = itemView.findViewById(R.id.likeCount);
        commentCount = itemView.findViewById(R.id.commentCount);
        sendComment = itemView.findViewById(R.id.sendComment);
        inputComments = itemView.findViewById(R.id.inputComments);
    }

}
