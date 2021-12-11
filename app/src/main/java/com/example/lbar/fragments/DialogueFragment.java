package com.example.lbar.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lbar.MainActivity;
import com.example.lbar.R;
import com.example.lbar.adapter.MessageAdapter;
import com.example.lbar.helpClasses.Message;
import com.example.lbar.fragments.mainMenuFragments.PeopleFragment;
import com.example.lbar.helpClasses.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.lbar.MainActivity.dp_width;
import static com.example.lbar.MainActivity.reference;

public class DialogueFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser fUser;
    private DatabaseReference friendsRef;
    private ArrayList<String> friends = new ArrayList<>();
    private String senderUserID, receiverUserID, text, urll;

    private Toolbar toolbar;
    private CircleImageView profileImg;
    private ImageView addFriend;
    private TextView username;
    private String username_txt;

    private TextInputEditText text_to_send;
    private ConstraintLayout message_layout;
    private TextInputLayout textInputLayout_send;

    private MessageAdapter messageAdapter;
    private List<Message> mChat;
    private RecyclerView recyclerView;

    private boolean isFriend;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialogue, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        AppCompatActivity main_activity = (MainActivity) getActivity();

        mAuth = FirebaseAuth.getInstance();
        fUser = mAuth.getCurrentUser();
        senderUserID = fUser.getUid();
        friendsRef = reference.child(fUser.getUid()).child("us_friends");

        isFriend = false;

        toolbar = (Toolbar) view.findViewById(R.id.toolbar_in_dialogue);
        setToolbarSettings(toolbar);

        initItems(view);

        isFriendCheckListener();

        ///->
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        ///->

        readMessages(senderUserID, receiverUserID, urll);

        textInputLayout_send.setEndIconOnClickListener(view1 -> {
            text = text_to_send.getText().toString();
            if (!text.equals("")) {
                sendMessage(senderUserID, receiverUserID, text);
            } else {
                Toast.makeText(main_activity, "Please, enter a message", Toast.LENGTH_SHORT).show();
            }
            text_to_send.setText("");
        });

        setItemAnimations();

        return view;
    }

    private void isFriendCheckListener() {
        friendsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String friend_id = snapshot.getValue().toString();

                    Log.d("fr_chkr", friend_id);

                    if (friend_id.equals(receiverUserID)) {
                        Log.d("fr_chkr", "bingo");
                        isFriend = true;
                    }
                }
                setToolbarProfileInfo();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initItems(View view) {
        profileImg = view.findViewById(R.id.dialog_us_img);
        addFriend = view.findViewById(R.id.add_a_friend);
        message_layout = view.findViewById(R.id.constraint_for_message_edit_text);

        recyclerView = view.findViewById(R.id.recycler_view_messages);
        recyclerView.setHasFixedSize(true);

        username = view.findViewById(R.id.dialog_txt_us_name);
        username.setWidth(dp_width / 2);

        text_to_send = view.findViewById(R.id.et_send);
        textInputLayout_send = view.findViewById(R.id.textField_message);

        urll = this.getArguments().getString("user_img");
        receiverUserID = this.getArguments().getString("us_id");
        username_txt = this.getArguments().getString("user_name");
    }

    private void setToolbarSettings(Toolbar tbar) {
        if (tbar != null) {
            tbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24);
            tbar.setNavigationOnClickListener(view1 -> {
                try {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.from_left, R.anim.to_left)
                            .replace(R.id.fragment_container,
                                    new PeopleFragment()).commit();
                } catch (Exception D) {
                    Toast.makeText(getContext(), R.string.sww, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setItemAnimations() {
        message_layout.setTranslationX(dp_width);
        message_layout.setAlpha(1);
        message_layout.animate().translationX(0).alpha(1)
                .setDuration(200).setStartDelay(100).start();
    }

    private void setToolbarProfileInfo() {
        username.setText(username_txt);
        Glide.with(DialogueFragment.this).load(urll).into(profileImg);

        if (isFriend){
            Log.d("fr_chkr", "it is friend");
            addFriend.setImageResource(R.drawable.ic_already_friend);
        } else {
            Log.d("fr_chkr", "it is not a friend");
            addFriend.setImageResource(R.drawable.ic_add_friend);
        }
    }

    private void sendMessage(String sender, String receiver, String message_text) {

        DatabaseReference reference = FirebaseDatabase.getInstance(getString(R.string.fdb_inst)).getReference();

        Message msg = new Message(sender, receiver, message_text);
        reference.child("Chats").push().setValue(msg);
    }

    private void readMessages(final String myID, final String userID, String imageURI) {
        mChat = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chats");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message chat = snapshot.getValue(Message.class);

                    try {
                        if (chat.getReceiverUserId().equals(myID) && chat.getSenderUserId().equals(userID) ||
                                chat.getReceiverUserId().equals(userID) && chat.getSenderUserId().equals(myID)) {
                            mChat.add(chat);
                        }

                        messageAdapter = new MessageAdapter(getContext(), mChat, imageURI);
                        recyclerView.setAdapter(messageAdapter);
                    } catch (NullPointerException e) {
                        Log.d("getId_in_dialogue", "Exception" + e);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
