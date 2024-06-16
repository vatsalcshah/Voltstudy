package com.vatsal.voltstudy.viewholders;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vatsal.voltstudy.R;
import com.vatsal.voltstudy.auth_controller.LoginActivity;
import com.vatsal.voltstudy.discussion_section.DiscussionPostDetailActivity;
import com.vatsal.voltstudy.models.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class DiscussionPostAdapter extends RecyclerView.Adapter<DiscussionPostAdapter.MyViewHolder> {

    private Context mContext;
    private List<Post> mData ;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser ;


    public DiscussionPostAdapter(Context mContext, List<Post> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View row = LayoutInflater.from(mContext).inflate(R.layout.row_post_item,parent,false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvTitle.setText(mData.get(position).getTitle());
        holder.userName.setText(mData.get(position).getUserName());
        holder.discussionDetails.setText(mData.get(position).getDescription());

        Glide.with(mContext)
                .load(mData.get(position).getUserPhoto())
                .placeholder(R.drawable.ic_user_place_holder)
                .circleCrop()
                .into(holder.imgPostProfile);

        String postImageUrl = mData.get(position).getPicture();
        if(postImageUrl != null)
        {
            Glide.with(mContext)
                    .load(postImageUrl)
                    .into(holder.imgPost);
            holder.imgPost.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, userName, discussionDetails;
        ImageView imgPost;
        ImageView imgPostProfile;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.row_post_title);
            userName = itemView.findViewById(R.id.user_name);
            discussionDetails = itemView.findViewById(R.id.row_post_details);
            imgPostProfile = itemView.findViewById(R.id.row_post_profile_img);
            imgPost = itemView.findViewById(R.id.row_post_img);


            mAuth = FirebaseAuth.getInstance();
            currentUser = mAuth.getCurrentUser();

            itemView.setOnClickListener(view -> {
                if(currentUser != null) {
                    Intent postDetailActivity = new Intent(mContext, DiscussionPostDetailActivity.class);
                    Intent commentAdapter = new Intent(mContext, DiscussionAnswerAdapter.class);

                    int position = getAdapterPosition();

                    postDetailActivity.putExtra("title", mData.get(position).getTitle());
                    postDetailActivity.putExtra("postImage", mData.get(position).getPicture());
                    postDetailActivity.putExtra("description", mData.get(position).getDescription());
                    postDetailActivity.putExtra("postKey", mData.get(position).getPostKey());
                    postDetailActivity.putExtra("userPhoto", mData.get(position).getUserPhoto());
                    postDetailActivity.putExtra("userName", mData.get(position).getUserName());
                    postDetailActivity.putExtra("postUserId", mData.get(position).getUserId());

                    // will fix this later i forgot to add user name to post object
                    //postDetailActivity.putExtra("userName",mData.get(position).getUsername);
                    long timestamp = (long) mData.get(position).getTimeStamp();
                    postDetailActivity.putExtra("postDate", timestamp);
                    mContext.startActivity(postDetailActivity);

                    commentAdapter.putExtra("postKey", mData.get(position).getPostKey());

                }
                else {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getRootView().getContext());
                    builder1.setMessage("Please Login To Access Answers");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Login",
                            (dialog, id) -> {
                                Intent loginActivity = new Intent(mContext, LoginActivity.class);
                                mContext.startActivity(loginActivity);
                            });

                    builder1.setNegativeButton(
                            "Cancel",
                            (dialog, id) -> dialog.cancel());

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }

            });

        }


    }
}
