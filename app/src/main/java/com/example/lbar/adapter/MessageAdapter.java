package com.example.lbar.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lbar.R;
import com.example.lbar.fragments.DialogueFragment;
import com.example.lbar.helpClasses.Message;
import com.example.lbar.helpClasses.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private List<Message> mChat;
    private Context mContext;
    private String imageURI;
    private boolean animationStart = true;
    private boolean isFirstEnter = true;
    private View rl;

    private FirebaseUser fUser;

    public MessageAdapter(Context mContext, List<Message> mMessages, String imageURI, boolean animationStart, boolean isFirstEnter, View rl) {
        this.mChat = mMessages;
        this.mContext = mContext;
        this.imageURI = imageURI;
        this.animationStart = animationStart;
        this.isFirstEnter = isFirstEnter;
        this.rl = rl;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {

        Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), android.R.anim.slide_in_left);

        Message chat = mChat.get(position);


        holder.message.setText(chat.getMessage());
        Glide.with(mContext).load(imageURI).into(holder.profile_image);

        if (holder.getItemViewType() == MSG_TYPE_RIGHT){
            holder.message.setOnLongClickListener(view -> {

                showCorrectionDialog(chat);
                return false;
            });
        }


        // Жуткий костыль. Другого решения пока найти не смог.

        if (!isFirstEnter) {
            if (mChat.size() - 1 == mChat.indexOf(chat)) {
                holder.itemView.startAnimation(animation);
            }
        }
        if (mChat.size()-20 == mChat.indexOf(chat)) {
            animationStart = true;
        }
        if (animationStart){
            holder.itemView.startAnimation(animation);
        }
    }

    private void showCorrectionDialog(Message chat) {
        EditText et = rl.findViewById(R.id.et_corrected_name);

        et.setText(chat.getMessage());
        et.setSelection(et.getText().length());

        MaterialAlertDialogBuilder mdBuilder = new MaterialAlertDialogBuilder(mContext);
        mdBuilder.setTitle(R.string.header_correcting);
        mdBuilder.setMessage(R.string.change_mess_discr);
        mdBuilder.setBackground(mContext.getResources().getDrawable(R.drawable.dialog_drawable, null));

        if (rl.getParent() != null) {
            ((ViewGroup) rl.getParent()).removeView(rl);
        }
        mdBuilder.setView(rl);

        mdBuilder.setPositiveButton(R.string.apply, (dialogInterface, i) -> {
            //animationStart = false; - не работет, так как адаптер пересоздаётся;
            FirebaseDatabase.getInstance("https://lbar-messenger-default-rtdb.firebaseio.com/")
                    .getReference().child("Chats").child(chat.getAddress()).child("message")
                    .setValue(et.getText().toString());
        });

        mdBuilder.setNegativeButton(R.string.del_mess, (dialogInterface, i) -> {
            FirebaseDatabase.getInstance("https://lbar-messenger-default-rtdb.firebaseio.com/")
                    .getReference().child("Chats").child(chat.getAddress()).removeValue();
        });

        mdBuilder.show();
        //TODO: Разобраться с обновлением спика после изменения текста сообщения
        // (нижнее сообщение стартует анимацию, а этого делать бы не надо)
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView message;
        public ImageView profile_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_img);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        if (mChat.get(position).getSenderUserId().equals(fUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
