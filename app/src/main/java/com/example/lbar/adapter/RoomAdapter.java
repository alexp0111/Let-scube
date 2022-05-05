package com.example.lbar.adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.lbar.fragments.mainMenuFragments.roomsFragments.RoomsStartFragment;
import com.example.lbar.fragments.mainMenuFragments.roomsFragments.roomsBattleFragments.RoomsKeyBattleFragment;
import com.example.lbar.helpClasses.Event;
import com.example.lbar.helpClasses.Message;
import com.example.lbar.helpClasses.Room;
import com.example.lbar.helpClasses.RoomMember;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.lbar.MainActivity.reference;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {

    private DatabaseReference ref = FirebaseDatabase
            .getInstance("https://lbar-messenger-default-rtdb.firebaseio.com/")
            .getReference("Rooms");


    private List<RoomMember> roomMembers = new ArrayList<>();

    private List<Room> mRooms;
    private Context mContext;
    private View rl;
    private String usID;

    public RoomAdapter(Context mContext, List<Room> mRooms, String usID) {
        this.mContext = mContext;
        this.mRooms = mRooms;
        this.usID = usID;
    }

    public RoomAdapter(Context mContext, List<Room> mRooms, String usID, View rl) {
        this.mContext = mContext;
        this.mRooms = mRooms;
        this.rl = rl;
        this.usID = usID;
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
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Glide.with(mContext).load(snapshot.getValue().toString()).into(holder.authorImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        setDisciplineImage(holder, room.getRoom_puzzle_discipline());

        boolean synchronisation = room.isRoom_collection_synchronization();
        if (synchronisation) {
            holder.sync.setText("SYNC");
        } else {
            holder.sync.setText("ASYNC");
        }
        holder.members.setText(room.getRoom_members().size() + " / " + room.getRoom_max_number_of_members());

        holder.roomHeader.setText(room.getRoom_header());
        String text = room.getRoom_description();
        if (text.equals("without")) {
            holder.divider.setVisibility(View.GONE);
            holder.roomText.setVisibility(View.GONE);
        } else {
            holder.roomText.setText(text);
            holder.divider.setVisibility(View.VISIBLE);
            holder.roomText.setVisibility(View.VISIBLE);
        }

        // Listener
        holder.joinButton.setOnClickListener(view -> {
            if (room.getRoom_members().size() == room.getRoom_max_number_of_members()) {
                Toast.makeText(mContext, "Room is crowded", Toast.LENGTH_SHORT).show();
            } else {
                if (room.getRoom_access().equals("private")) {
                    showPasswordDialog(room.getRoom_password(), room);
                } else {
                    addNewRoomMember(room);
                }
            }
        });
    }

    private void addNewRoomMember(Room room) {
        RoomMember roomMember = new RoomMember(usID, false);
        if (ref != null)
            ref.child(room.getRoom_id()).child("room_members").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    roomMembers.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        RoomMember member = snapshot.getValue(RoomMember.class);
                        roomMembers.add(member);
                    }

                    roomMembers.add(roomMember);
                    ref.child(room.getRoom_id())
                            .child("room_members")
                            .setValue(roomMembers)
                            .addOnCompleteListener(task -> goToNextFragment(room));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }

    private void goToNextFragment(Room room) {
        RoomsKeyBattleFragment fragment = new RoomsKeyBattleFragment();
        Bundle bundle = new Bundle();
        bundle.putString("room_id", room.getRoom_id());
        bundle.putString("newMember_id", usID);
        fragment.setArguments(bundle);

        try {
            ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.from_bottom, R.anim.alpha_to_low)
                    .replace(R.id.fragment_container,
                            fragment).commit();
        } catch (Exception D) {
            Toast.makeText(mContext, R.string.sww, Toast.LENGTH_SHORT).show();
        }
    }

    private void setDisciplineImage(ViewHolder holder, Integer room_puzzle_discipline) {
        int ResID = R.drawable.error;
        switch (room_puzzle_discipline) {
            case 0: {
                ResID = R.drawable.cube_typo0;
                break;
            }
            case 1: {
                ResID = R.drawable.cube_typo1;
                break;
            }
            case 2: {
                ResID = R.drawable.cube_typo2;
                break;
            }
            case 3: {
                ResID = R.drawable.cube_typo3;
                break;
            }
            case 4: {
                ResID = R.drawable.cube_typo4;
                break;
            }
            case 5: {
                ResID = R.drawable.cube_typo5;
                break;
            }
            case 6: {
                ResID = R.drawable.cube_pyraminx;
                break;
            }
            case 7: {
                ResID = R.drawable.cube_sqube;
                break;
            }
            case 8: {
                ResID = R.drawable.cube_megaminx;
                break;
            }
            case 9: {
                ResID = R.drawable.cube_clock;
                break;
            }
            case 10: {
                ResID = R.drawable.cube_square1;
                break;
            }
        }
        holder.disc.setImageResource(ResID);
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

    private void showPasswordDialog(String password, Room room) {
        TextInputEditText et = rl.findViewById(R.id.et_corrected_name);
        TextInputLayout il = rl.findViewById(R.id.textField_puzzle_name);
        il.setStartIconVisible(false);

        MaterialAlertDialogBuilder mdBuilder = new MaterialAlertDialogBuilder(mContext);
        mdBuilder.setTitle(R.string.enter_pass);
        mdBuilder.setBackground(mContext.getResources().getDrawable(R.drawable.dialog_drawable, null));

        if (rl.getParent() != null) {
            ((ViewGroup) rl.getParent()).removeView(rl);
        }
        mdBuilder.setView(rl);

        mdBuilder.setPositiveButton(R.string.apply, (dialogInterface, i) -> {
            if (et.getText().toString().equals(password)) {
                addNewRoomMember(room);
            }
        });

        mdBuilder.show();
    }
}
