package com.example.lbar.fragments.mainMenuFragments.eventFragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lbar.R;

public class CommentsFragment extends Fragment {

    private TextView txtEventText;

    private String eventID;
    private String eventAuthorID;
    private String eventHeader;
    private String eventText;
    private String eventIMG;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        eventID = this.getArguments().getString("ev_id");
        eventAuthorID = this.getArguments().getString("us_id");
        eventHeader = this.getArguments().getString("header");
        eventText = this.getArguments().getString("text");
        eventIMG = this.getArguments().getString("img");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments, container, false);

        initItems(view);

        txtEventText.setText(eventText);

        return view;
    }

    private void initItems(View v) {
        txtEventText = v.findViewById(R.id.comment_ev_text);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                    try {
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.from_left, R.anim.to_left)
                                .replace(R.id.fragment_container,
                                        new MainEventFragment()).commit();
                    } catch (Exception D) {
                        Toast.makeText(getContext(), R.string.sww, Toast.LENGTH_SHORT).show();
                    }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }
}
