package com.example.lbar.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.example.lbar.MainActivity;
import com.example.lbar.R;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.lbar.MainActivity.dp_width;

public class DialogueFragment extends Fragment {

    private Toolbar toolbar;
    private CircleImageView profileImg;
    private Intent intent;
    private TextView username;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialogue, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        AppCompatActivity main_activity = (MainActivity) getActivity();


        toolbar = (Toolbar) view.findViewById(R.id.toolbar_in_dialogue);
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24);
            toolbar.setNavigationOnClickListener(view1 -> {
                try {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new FriendsFragment()).commit();
                } catch (Exception D) {
                    Toast.makeText(getContext(), R.string.sww, Toast.LENGTH_SHORT).show();
                }
            });
        }
        profileImg = view.findViewById(R.id.dialog_us_img);
        username = view.findViewById(R.id.dialog_txt_us_name);
        username.setWidth(dp_width / 2);
        String urll = this.getArguments().getString("user_img");
        String username_txt = this.getArguments().getString("user_name");
        username.setText(username_txt);
        Glide.with(DialogueFragment.this).load(urll).into(profileImg);

        return view;
    }
}
