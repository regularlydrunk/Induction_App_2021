package com.example.inductionapp2021;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendViewHolder extends RecyclerView.ViewHolder {

    CircleImageView profileImage;
    TextView username, course;


    public FindFriendViewHolder(@NonNull View itemView) {
        super(itemView);

        profileImage  = itemView.findViewById(R.id.profileImage);
        username  = itemView.findViewById(R.id.username);
        course  = itemView.findViewById(R.id.course);

    }
}
