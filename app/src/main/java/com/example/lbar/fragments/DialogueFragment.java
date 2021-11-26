package com.example.lbar.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.lbar.MainActivity;
import com.example.lbar.R;
import com.example.lbar.database.Message;
import com.example.lbar.fragments.mainMenuFragments.FriendsFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.lbar.MainActivity.dp_width;

public class DialogueFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser fUser;
    private String senderUserID, receiverUserID, text;

    private Toolbar toolbar;
    private CircleImageView profileImg;
    private Intent intent;
    private TextView username;

    private TextInputEditText text_to_send;
    private ConstraintLayout message_layout;
    private TextInputLayout textInputLayout_send;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialogue, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        AppCompatActivity main_activity = (MainActivity) getActivity();

        mAuth = FirebaseAuth.getInstance();
        fUser = mAuth.getCurrentUser();
        senderUserID = fUser.getUid();

        toolbar = (Toolbar) view.findViewById(R.id.toolbar_in_dialogue);
        setToolbarSettings(toolbar);

        initItems(view);

        setToolbarProfileInfo();

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

    private void initItems(View view) {
        profileImg = view.findViewById(R.id.dialog_us_img);
        message_layout = view.findViewById(R.id.constraint_for_message_edit_text);

        username = view.findViewById(R.id.dialog_txt_us_name);
        username.setWidth(dp_width / 2);

        text_to_send = view.findViewById(R.id.et_send);
        textInputLayout_send = view.findViewById(R.id.textField_message);
    }

    private void setToolbarSettings(Toolbar tbar) {
        if (tbar != null) {
            tbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24);
            tbar.setNavigationOnClickListener(view1 -> {
                try {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.from_left, R.anim.to_left)
                            .replace(R.id.fragment_container,
                                    new FriendsFragment()).commit();
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
        String urll = this.getArguments().getString("user_img");
        receiverUserID = this.getArguments().getString("us_id");
        String username_txt = this.getArguments().getString("user_name");

        username.setText(username_txt);
        Glide.with(DialogueFragment.this).load(urll).into(profileImg);
    }

    private void sendMessage(String sender, String receiver, String message_text) {

        DatabaseReference reference = FirebaseDatabase.getInstance(getString(R.string.fdb_inst)).getReference();

        Message msg = new Message(sender, receiver, message_text);
        reference.child("Chats").push().setValue(msg);
    }
}
