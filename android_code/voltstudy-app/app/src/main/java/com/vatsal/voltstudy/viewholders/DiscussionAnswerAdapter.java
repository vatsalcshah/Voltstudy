package com.vatsal.voltstudy.viewholders;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vatsal.voltstudy.R;
import com.vatsal.voltstudy.models.Comment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class DiscussionAnswerAdapter extends RecyclerView.Adapter<DiscussionAnswerAdapter.CommentViewHolder> {

    private Context mContext;
    private List<Comment> mData;
    private String firebaseUser;


    public DiscussionAnswerAdapter(Context mContext, List<Comment> mData,String firebaseUser) {
        this.mContext = mContext;
        this.mData = mData;
        this.firebaseUser = firebaseUser;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.row_comment,parent,false);
        return new CommentViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {

        Glide.with(mContext)
                .load(mData.get(position).getUimg())
                .placeholder(R.drawable.ic_user_place_holder)
                .circleCrop()
                .into(holder.img_user);

        holder.tv_name.setText(mData.get(position).getUname());
        holder.tv_content.setText(mData.get(position).getContent());
        holder.tv_date.setText(timestampToString((Long)mData.get(position).getTimestamp()));

        isLiked(mData.get(position).getCommentId(), holder.likeButton, mData.get(position).getUid());
        nrLikes(holder.tv_likes_number, mData.get(position).getCommentId(), mData.get(position).getUid());

        holder.likeButton.setOnClickListener(view -> {
            if(holder.likeButton.getTag().equals("like")){
                FirebaseDatabase.getInstance().getReference()
                        .child("commentLikes")
                        .child(mData.get(position).getUid())
                        .child(mData.get(position).getCommentId())
                        .child(firebaseUser)
                        .setValue(true);
            }else {
                FirebaseDatabase.getInstance().getReference()
                        .child("commentLikes")
                        .child(mData.get(position).getUid())
                        .child(mData.get(position).getCommentId())
                        .child(firebaseUser).removeValue();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder{

        ImageView img_user;
        TextView tv_name,tv_content,tv_date,tv_likes_number;
        ImageView likeButton;

        public CommentViewHolder(View itemView) {
            super(itemView);
            img_user = itemView.findViewById(R.id.comment_user_img);
            tv_name = itemView.findViewById(R.id.comment_username);
            tv_content = itemView.findViewById(R.id.comment_content);
            tv_date = itemView.findViewById(R.id.comment_date);
            tv_likes_number = itemView.findViewById(R.id.row_answer_likes);
            likeButton = itemView.findViewById(R.id.like_answer_button);
        }
    }



    private String timestampToString(long time) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("hh:mm a  MMM dd, yyyy",calendar).toString();
        return date;

    }


    private void isLiked(String commentId, final ImageView imageView, String commentCreatorID){

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("commentLikes")
                .child(commentCreatorID)
                .child(commentId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.ic_thumb_up_red);
                    imageView.setTag("liked");
                }else{
                    imageView.setImageResource(R.drawable.ic_thumb_up_grey);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void nrLikes(TextView likes, String commentId, String commentCreatorId){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("commentLikes")
                .child(commentCreatorId)
                .child(commentId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                likes.setText(dataSnapshot.getChildrenCount() + " likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
