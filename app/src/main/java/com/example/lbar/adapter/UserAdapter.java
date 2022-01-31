package com.example.lbar.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lbar.R;
import com.example.lbar.helpClasses.User;
import com.example.lbar.fragments.DialogueFragment;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<User> mUsers;
    private Context mContext;
    private boolean flag;

    public UserAdapter(Context mContext, List<User> mUsers, boolean flag){
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.flag = flag;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), android.R.anim.slide_in_left );

        User user = mUsers.get(position);

        holder.username.setText(user.getUs_name());
        Glide.with(mContext).load(user.getImage()).into(holder.profile_image);

        if (user.getUs_status().equals("online")){
            holder.status_line.setText("online");
            holder.status_line.setTextColor(Color.parseColor("#FFC107"));
        } else {
            holder.status_line.setText("offline");
            holder.status_line.setTextColor(Color.parseColor("#BDBDBD"));
        }

        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            holder.itemView.setOnClickListener(view -> {

                DialogueFragment fragment = new DialogueFragment();
                Bundle bundle = new Bundle();
                bundle.putString("us_id", user.getUs_id());
                bundle.putString("user_img", user.getImage());
                bundle.putString("user_name", user.getUs_name());
                // Отправлять инфу сразу - друг или нет.
                fragment.setArguments(bundle);

                try {
                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.to_right, R.anim.from_right)
                            .replace(R.id.fragment_container,
                            fragment).commit();
                } catch (Exception D) {
                    Toast.makeText(mContext, R.string.sww, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(mContext, "To chat, firstly enter your account", Toast.LENGTH_SHORT).show();
        }
        // Надо отлаживать и думать над анимацией. Она обновляет список каждый раз, когда кто-то в приложении
        // выходит или меняет тему - так быть не должно. Или не должно приносить дискомфорт пользователю

        // При смене темы вылетает офлайн на мгновение - странно?

        //holder.itemView.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public ImageView profile_image;
        public TextView status_line;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.user_item_name);
            profile_image = itemView.findViewById(R.id.user_item_profile_img);
            status_line = itemView.findViewById(R.id.status_list);
        }
    }
}
