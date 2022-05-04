package com.example.lbar.fragments.mainMenuFragments.roomsFragments.roomsBattleFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.lbar.MainActivity;
import com.example.lbar.R;
import com.example.lbar.adapter.RoomAdapter;
import com.example.lbar.helpClasses.Room;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RoomsPrivateBattleFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser fUser;

    private DatabaseReference eventReference;
    private DatabaseReference mFriendsRef;

    private RecyclerView recyclerViewInRooms;
    private RoomAdapter roomAdapter;

    private List<Room> mRooms;

    private DrawerLayout drawer;
    private View rl;

    private com.google.android.material.progressindicator.LinearProgressIndicator progressBar;
    private SwipeRefreshLayout srl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_private_rooms, container, false);
        rl = inflater.inflate(R.layout.dialog_renaming, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        AppCompatActivity main_activity = (MainActivity) getActivity();

        mAuth = FirebaseAuth.getInstance();
        fUser = mAuth.getCurrentUser();

        eventReference = FirebaseDatabase.getInstance(getString(R.string.fdb_inst)).getReference("Rooms");

        initItems(view);

        mRooms = new ArrayList<>();

        recyclerViewInRooms.setHasFixedSize(true);
        recyclerViewInRooms.setLayoutManager(new LinearLayoutManager(getContext()));

        if (fUser != null && eventReference != null){
            progressBar.setVisibility(View.VISIBLE);
            readRooms();
        }

        srl.setOnRefreshListener(() -> {
            progressBar.setVisibility(View.VISIBLE);
            readRooms();
            progressBar.setVisibility(View.GONE);
            srl.setRefreshing(false);
        });

        return view;
    }

    private void initItems(View v) {
        progressBar = v.findViewById(R.id.prog_bar_private_rooms);
        srl = v.findViewById(R.id.pull_to_refresh_private_room);

        recyclerViewInRooms = v.findViewById(R.id.recycler_private_rooms);
    }

    private void readRooms() {
        eventReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mRooms.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Room room = snapshot.getValue(Room.class);
                    assert room != null;
                    if (room.getRoom_access().equals("private"))
                        mRooms.add(room);
                }

                Collections.reverse(mRooms);
                roomAdapter = new RoomAdapter(getContext(), mRooms, fUser.getUid(), rl);
                recyclerViewInRooms.setAdapter(roomAdapter);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Log.d("List_ev", error.getMessage());
            }
        });
    }
}
