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
import com.example.lbar.helpClasses.Event;
import com.example.lbar.helpClasses.Room;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import static com.example.lbar.MainActivity.reference;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {

    private List<Room> mRooms;
    private Context mContext;

    public RoomAdapter(Context mContext, List<Room> mRooms) {
        this.mContext = mContext;
        this.mRooms = mRooms;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.room_item, parent, false);
        return new RoomAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Room room = mRooms.get(position);

        DatabaseReference ref1 = reference.child(room.getRoom_admin_id()).child("us_name");
        DatabaseReference ref2 = reference.child(room.getRoom_admin_id()).child("image");

        ref1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.authorName.setText(snapshot.getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Glide.with(mContext).load(snapshot.getValue().toString()).into(holder.authorImage);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        // TODO: discipline
        boolean synchronisation = room.isRoom_collection_synchronization();
        if (synchronisation){
            holder.sync.setText("SYNC");
        } else {
            holder.sync.setText("ASYNC");
        }
        holder.members.setText(room.getRoom_members().size() + " / " + room.getRoom_max_number_of_members());

        holder.roomHeader.setText(room.getRoom_header());
        String text = room.getRoom_description();
        if (text.equals("without")){
            holder.divider.setVisibility(View.GONE);
            holder.roomText.setVisibility(View.GONE);
        } else {
            holder.roomText.setText(text);
            holder.divider.setVisibility(View.VISIBLE);
            holder.roomText.setVisibility(View.VISIBLE);
        }
        holder.joinButton.setOnClickListener(view -> Snackbar.make(view, "Join this room!", BaseTransientBottomBar.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return mRooms.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView roomHeader;

        public ImageView disc;
        public TextView sync;
        public TextView members;

        public ImageView authorImage;
        public TextView authorName;

        public TextView roomText;
        public MaterialButton joinButton;

        public View divider;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            roomHeader = itemView.findViewById(R.id.room_item_header_txt);

            disc = itemView.findViewById(R.id.room_item_discipline_img);
            sync = itemView.findViewById(R.id.room_item_sync_txt);
            members = itemView.findViewById(R.id.room_item_members_txt);

            authorImage = itemView.findViewById(R.id.room_item_author_img);
            authorName = itemView.findViewById(R.id.room_item_author_name);

            roomText = itemView.findViewById(R.id.room_item_text);
            joinButton = itemView.findViewById(R.id.room_item_join_btn);

            divider = itemView.findViewById(R.id.divider2);
        }
    }
}
