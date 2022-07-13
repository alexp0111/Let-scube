package com.example.lbar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lbar.R;
import com.example.lbar.helpClasses.Comment;
import com.example.lbar.helpClasses.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private List<Comment> mComments;
    private Context mContext;

    private FirebaseUser fUser;

    public CommentAdapter(Context mContext, List<Comment> mComments) {
        this.mComments = mComments;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // TODO: replace with custom items for comments
        View view;
        if (viewType == MSG_TYPE_RIGHT) {
            view = LayoutInflater.from(mContext).inflate(R.layout.comment_item_right, parent, false);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.comment_item_left, parent, false);
        }
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, int position) {
        Comment cmt = mComments.get(position);

        holder.message.setText(cmt.getComment_text());

        DatabaseReference refAuthor = FirebaseDatabase.getInstance(mContext.getString(R.string.fdb_inst))
                .getReference().child("Users").child(cmt.getComment_author_id());

        refAuthor.child("image").get().addOnCompleteListener(task ->
                Glide.with(mContext).load(task.getResult().getValue().toString()).into(holder.profile_image)
        ).addOnCompleteListener(task1 -> refAuthor.child("us_name").get().addOnCompleteListener(
                task3 -> holder.author.setText(task3.getResult().getValue().toString())
        ));
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView message;
        public ImageView profile_image;
        public TextView author;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_img);
            author = itemView.findViewById(R.id.comment_author_txt);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        if (mComments.get(position).getComment_author_id().equals(fUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
