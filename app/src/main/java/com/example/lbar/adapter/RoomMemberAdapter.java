package com.example.lbar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lbar.R;
import com.example.lbar.helpClasses.RoomMember;
import com.example.lbar.helpClasses.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import static com.example.lbar.MainActivity.reference;

public class RoomMemberAdapter extends RecyclerView.Adapter<RoomMemberAdapter.ViewHolder> {

    private List<RoomMember> mMembers;
    private Context mContext;

    public RoomMemberAdapter(Context mContext, List<RoomMember> mMembers) {
        this.mMembers = mMembers;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RoomMemberAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.room_member_item, parent, false);
        return new RoomMemberAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomMemberAdapter.ViewHolder holder, int position) {
        RoomMember member = mMembers.get(position);
        reference.child(member.getMember_id())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            holder.username.setText(user.getUs_name());
                            Glide.with(mContext).load(user.getImage()).into(holder.profile_image);
                            if (member.getMember_result() != null) {
                                holder.time.setText(convertFromMStoString(
                                        member.getMember_result()));
                            } else {
                                holder.time.setText("new"); // R.string.joined_room
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        Glide.with(mContext).load(member.getMember_scramble_img()).into(holder.scrambled);
        Glide.with(mContext).load(member.getMember_result_img()).into(holder.solved);
    }

    @Override
    public int getItemCount() {
        return mMembers.size();
    }

    private String convertFromMStoString(long ms) {
        if (ms == -1L) return "Not enough info";
        String result = "";
        if (ms / 3600000 != 0) {
            result += strCorrection(ms / 3600000, 2) + ":";
        }
        ms %= 3600000;
        if (ms / 60000 != 0) {
            result += strCorrection(ms / 60000, 2) + ":";
        }
        ms %= 60000;
        result += strCorrection(ms / 1000, 2) + ":" + strCorrection(ms %= 1000, 3);

        return result;
    }

    private String strCorrection(long numToCorrect, int digits) {
        StringBuilder num = new StringBuilder(Long.toString(numToCorrect));
        while (num.length() < digits) num.insert(0, "0");
        return num.toString();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public ImageView profile_image;
        public TextView time;
        public ImageView scrambled;
        public ImageView solved;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.room_member_item_author);
            profile_image = itemView.findViewById(R.id.room_member_item_iv);
            time = itemView.findViewById(R.id.room_member_item_time);
            scrambled = itemView.findViewById(R.id.room_member_item_iv_scr);
            solved = itemView.findViewById(R.id.room_member_item_iv_sld);
        }
    }
}
