package com.example.lbar.fragments.mainMenuFragments.eventFragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lbar.MainActivity;
import com.example.lbar.R;
import com.example.lbar.adapter.CommentAdapter;
import com.example.lbar.adapter.MessageAdapter;
import com.example.lbar.helpClasses.Comment;
import com.example.lbar.helpClasses.Message;
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

public class CommentsFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser fUser;
    private String senderUserID, receiverUserID, text, urll;

    private String eventID;
    private String eventAuthorID;
    private String eventHeader;
    private String eventText;
    private String eventIMG;

    private TextView txtEventText;

    private ConstraintLayout constraintLayout;
    private TextInputEditText text_to_send;
    private ConstraintLayout comment_layout;
    private TextInputLayout textInputLayout_send;

    private CommentAdapter commentAdapter;
    private List<Comment> mComments;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        eventID = this.getArguments().getString("ev_id");
        eventAuthorID = this.getArguments().getString("us_id");
        eventHeader = this.getArguments().getString("header");
        eventText = this.getArguments().getString("text");
        eventIMG = this.getArguments().getString("img");

        mAuth = FirebaseAuth.getInstance();
        fUser = mAuth.getCurrentUser();
        senderUserID = fUser.getUid();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments, container, false);
        AppCompatActivity main_activity = (MainActivity) getActivity();

        initItems(view);

        txtEventText.setText(eventText);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        readComments();

        textInputLayout_send.setEndIconOnClickListener(view1 -> {
            text = text_to_send.getText().toString();
            if (!text.equals("")) {
                sendComment(text);
            } else {
                Toast.makeText(main_activity, R.string.pls_enter_mess, Toast.LENGTH_SHORT).show();
            }
            text_to_send.setText("");
        });

        constraintLayout.setOnClickListener(view12 -> closeFragment());

        return view;
    }

    private void closeFragment() {
        try {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.from_left, R.anim.to_left)
                    .replace(R.id.fragment_container,
                            new MainEventFragment()).commit();
        } catch (Exception D) {
            Toast.makeText(getContext(), R.string.sww, Toast.LENGTH_SHORT).show();
        }
    }

    private void readComments() {
        mComments = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Comments");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mComments.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Comment cmt = snapshot.getValue(Comment.class);

                    try {
                        assert cmt != null;
                        if (cmt.getComment_event_id().equals(eventID)) {
                            mComments.add(cmt);
                        }
                    } catch (NullPointerException e) {
                        Log.d("getId_in_comment", "Exception" + e);
                    }
                }

                commentAdapter = new CommentAdapter(getContext(), mComments);
                recyclerView.setAdapter(commentAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendComment(String commentText) {
        DatabaseReference reference = FirebaseDatabase.getInstance(getString(R.string.fdb_inst)).getReference();

        String newRef = reference.child("Comments").push().getKey();
        Comment cmt = new Comment(fUser.getUid(), eventID, commentText, newRef);
        reference.child("Comments").child(newRef).setValue(cmt);
    }

    private void initItems(View v) {
        txtEventText = v.findViewById(R.id.comment_ev_text);

        constraintLayout = v.findViewById(R.id.comments_top_layout);
        text_to_send = v.findViewById(R.id.et_send_comment);
        comment_layout = v.findViewById(R.id.constraint_for_comment_edit_text);
        textInputLayout_send = v.findViewById(R.id.textField_comment);

        recyclerView = v.findViewById(R.id.recycler_view_comments);
        recyclerView.setHasFixedSize(true);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                    closeFragment();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }
}
